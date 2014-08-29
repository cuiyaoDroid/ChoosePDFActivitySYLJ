package com.xianzhi.office;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.droidtext.tool.DensityUtil;
import com.xianzhi.tool.adapter.mailListReceiveAdapter;
import com.xianzhi.tool.adapter.mailListSendAdapter;
import com.xianzhi.tool.db.AchmentDetailHelper;
import com.xianzhi.tool.db.Pager;
import com.xianzhi.tool.db.mailContentHelper;
import com.xianzhi.tool.db.mailContentHolder;
import com.xianzhi.tool.db.sendMailHelper;
import com.xianzhi.tool.db.sendMailHolder;
import com.xianzhi.tool.view.PullDownListView;
import com.xianzhi.tool.view.PullDownListView.OnRefreshListioner;
import com.xianzhi.webtool.HttpJsonTool;

public class mailBoxActivity extends Activity implements OnClickListener {
	public enum Listkind {
		receive,sent
	};
	private ImageButton receiveButton;
	private ImageButton sentButton;
	private ImageButton writeButton;
	private ViewPager mPager;
	private List<Map<String, Object>> receiverDatalist;
	private List<Map<String, Object>> sendDatalist;
	private mailListReceiveAdapter receiveadapter;
	private mailListSendAdapter sendadapter;
	private Handler mHandler;
	private PullDownListView receivepulldownlist;
	private PullDownListView sendpulldownlist;
	private Pager sendpage;
	private Pager receivepage;
	private int minReceiverid;
	private ImageView titleImage;
	private static final int PAGE_SIZE=15;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mail_box);
		mHandler = new Handler();
		initContentView();
		initPageView();
		initProgressDialog();
		initdeleteDialog();
		getReceiveBoxData();
		sendpage = new Pager(0, PAGE_SIZE);
		receivepage = new Pager(0, PAGE_SIZE);
	}

	private void initContentView() {
		receiveButton = (ImageButton) findViewById(R.id.receive_box_btn);
		receiveButton.setOnClickListener(this);
		sentButton = (ImageButton) findViewById(R.id.sent_box_btn);
		sentButton.setOnClickListener(this);
		writeButton = (ImageButton) findViewById(R.id.write_email_btn);
		writeButton.setOnClickListener(this);
		titleImage = (ImageView) findViewById(R.id.title_image);
		titleImage.setOnClickListener(this);
	}
	
	PopupWindow mPopupWindow;
	private void initDeletePopupwin(final int position,final View cell_view,final Listkind kind) {
		if(kind.equals(Listkind.receive)){
			stopGetReceiveData();
		}
		LayoutInflater mLayoutInflater = (LayoutInflater) this
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View popunwindwow = mLayoutInflater.inflate(
				R.layout.popupwin_delete_cell, null);
		TextView delete_btn = (TextView) popunwindwow.findViewById(R.id.cell_delete_tbtn);
		mPopupWindow = new PopupWindow(popunwindwow, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		delete_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPopupWindow.dismiss();
				showDeleteDialog(DELETE_DIALOG,position,kind);
			}
		});
		mPopupWindow.setFocusable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(R.style.AnimationFade);
		mPopupWindow.getContentView().measure(0,0);
		int height = mPopupWindow.getContentView().getMeasuredHeight();
		mPopupWindow.showAtLocation(cell_view, Gravity.RIGHT | Gravity.TOP,
				DensityUtil.dip2px(getApplicationContext(), 10),
				cell_view.getBottom() + mPager.getTop() - height / 2);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
			}
		});
	}
	final static int DELETE_DIALOG=0;
	public void showDeleteDialog(int dialogId,final int position,final Listkind kind) {
		Dialog dialog = null;
		switch (dialogId) {
		case DELETE_DIALOG:
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
					mailBoxActivity.this);
			alertBuilder
					.setTitle("删除")
					.setMessage("确定要删除该封邮件")
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									if(kind.equals(Listkind.receive)){
										deleteReceiveMail(position);
									}else if(kind.equals(Listkind.sent)){
										deleteSentMail(position);
									}
								}
							});
			dialog = alertBuilder.create();
			break;

		}
		dialog.show();
	}
	private ProgressDialog progressDialog;
	private void initdeleteDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("");
		progressDialog.setMessage("正在删除，请稍等...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(false);
	}
	private void deleteReceiveMail(final int position){
		progressDialog.show();
		final int id = (Integer) receiverDatalist.get(position - 1).get("id");
		AsyncTask<Void, Void, Void> deletetask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				HttpJsonTool.getInstance().deleteMail(id);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				receiverDatalist.remove(position - 1);
				mailContentHelper helper=new mailContentHelper(getApplicationContext());
				helper.delete_id(id);
				helper.close();
				AchmentDetailHelper achhelper=new AchmentDetailHelper(getApplicationContext());
				achhelper.delete_inboxid(id);
				achhelper.close();
				receiveadapter.notifyDataSetChanged();
				progressDialog.dismiss();
			}
		};
		deletetask.execute();
	}
	private void deleteSentMail(int position){
		int id = (Integer) sendDatalist.get(position - 1).get("id");
		sendDatalist.remove(position - 1);
		sendMailHelper helper=new sendMailHelper(getApplicationContext());
		helper.delete_id(id);
		helper.close();
		sendadapter.notifyDataSetChanged();
	}
	private void initPageView() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		ArrayList<View> viewlist = new ArrayList<View>();
		initReceiveView(viewlist);
		initSendView(viewlist);
		mPager.setAdapter(new MyPagerAdapter(viewlist));
		mPager.setCurrentItem(0);
		receiveButton.setImageResource(R.drawable.img_mail_receive_pressed);
		sentButton.setImageResource(R.drawable.img_mail_send);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void initReceiveView(ArrayList<View> viewlist) {
		receiverDatalist = new ArrayList<Map<String, Object>>();
		LayoutInflater inflater = getLayoutInflater();

		View receive_view = inflater.inflate(R.layout.view_receive_box, null);
		ListView receiveList = (ListView) receive_view
				.findViewById(R.id.receive_list);
		receiveadapter = new mailListReceiveAdapter(getApplicationContext(),
				receiverDatalist, R.layout.cell_mail_list, new String[] {
						"title", "content", "time" }, new int[] {
						R.id.mail_title, R.id.mail_content_sh, R.id.mail_time });
		receiveList.setAdapter(receiveadapter);
		receivepulldownlist = (PullDownListView) receive_view
				.findViewById(R.id.pulldownlist);
		receivepulldownlist.setRefreshListioner(new OnRefreshListioner() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				receivepage = new Pager(0, PAGE_SIZE);
				pulldownRefreshReceiveBoxData();
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				getmoreReceiveBoxData();
			}
		});
		receiveList = receivepulldownlist.mListView;
		receivepulldownlist.setMore(true);
		viewlist.add(receive_view);

		receiveList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mailBoxActivity.this,
						mailDetailActivity.class);
				int id = (Integer) receiverDatalist.get(position - 1).get("id");
				intent.putExtra("id", id);
				startActivityForResult(intent, RESULT_OK);
			}
		});
		receiveList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				initDeletePopupwin(position,arg1,Listkind.receive);
				return false;
			}
		});
	}

	private ProgressBar progressBar;

	private void initProgressDialog() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
	}
	private void stopGetReceiveData(){
		if(receivetask==null){
			return;
		}
		receivetask.cancel(true);
		progressBar.setVisibility(View.GONE);
	}
	AsyncTask<Void, Void, Void> receivetask=null;
	private void getReceiveBoxData() {
		progressBar.setVisibility(View.VISIBLE);
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		final int contactFlag = userInfo.getInt("contactFlag", 0);
		receivetask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				SharedPreferences userInfo = getSharedPreferences("user_info", 0);
				String name = userInfo.getString("name", "");
				String password = userInfo.getString("password", "");
				int savepassword = userInfo.getInt("savepassword", 0);
				int auto=getIntent().getIntExtra("auto", 0);
				if(savepassword==1&&auto==1){
					HttpJsonTool.getInstance().startLoginCheck(name, password);
				}
				HttpJsonTool.getInstance()
						.checkOutMail(getApplicationContext());
				if(contactFlag==0){
					HttpJsonTool.getInstance().checkOutContact(
							getApplicationContext());
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				SharedPreferences userInfo = getSharedPreferences("user_info", 0);
				userInfo.edit().putInt("contactFlag", 0).commit();
				progressBar.setVisibility(View.GONE);
				if (HttpJsonTool.state403) {
					gotoLoginView();
					return;
				}
				refreshReceiveList();
				receivepulldownlist.setMore(true);
				receiveadapter.notifyDataSetChanged();
			}
		};
		receivetask.execute();
	}

	private synchronized void refreshReceiveList() {
		mailContentHelper helper = new mailContentHelper(
				getApplicationContext());
		ArrayList<mailContentHolder> holderlist = helper.selectDataforList(
				HttpJsonTool.username, 0, receivepage.pagesize*(receivepage.curpage+1));
		receiverDatalist.clear();
		for (mailContentHolder holder : holderlist) {
			Map<String, Object> content = new HashMap<String, Object>();
			content.put("id", holder.getId());
			content.put("title", holder.getSubject());
			content.put("content", "发件人：" + holder.getFromUser() + " 内容："
					+ holder.getContent().substring(0, 15));
			content.put("time", holder.getSentDate());
			content.put("hasattachment", holder.getHasAttachment());
			content.put("readflag", holder.getReadFlag());
			receiverDatalist.add(content);
			minReceiverid = holder.getUid();
		}
		helper.close();
	}

	private void getmoreReceiveBoxData() {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				HttpJsonTool.getInstance().checkOutMoreMail(
						getApplicationContext(), minReceiverid);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (HttpJsonTool.state403) {
					gotoLoginView();
					return;
				}
				receivepulldownlist.onLoadMoreComplete();
				if (addReceiveList()) {
					receivepulldownlist.setMore(true);
				}
				receiveadapter.notifyDataSetChanged();
			}
		};
		task.execute();
	}

	private boolean addReceiveList() {
		receivepage.curpage++;
		mailContentHelper helper = new mailContentHelper(
				getApplicationContext());
		ArrayList<mailContentHolder> holderlist = helper.selectDataforList(
				HttpJsonTool.username, receivepage.curpage
						* receivepage.pagesize, receivepage.pagesize);
		Log.i("holder", ""+holderlist.size());
		for (mailContentHolder holder : holderlist) {
			Map<String, Object> content = new HashMap<String, Object>();
			content.put("id", holder.getId());
			content.put("title", holder.getSubject());
			content.put("content", "发件人：" + holder.getFromUser() + " 内容："
					+ holder.getContent().substring(0, 15));
			content.put("time", holder.getSentDate());
			content.put("hasattachment", holder.getHasAttachment());
			content.put("readflag", holder.getReadFlag());
			minReceiverid = holder.getUid();
			receiverDatalist.add(content);
		}
		helper.close();
		return holderlist.size() == sendpage.pagesize;
	}

	private void initSendView(ArrayList<View> viewlist) {
		sendDatalist = new ArrayList<Map<String, Object>>();
		LayoutInflater inflater = getLayoutInflater();

		View send_view = inflater.inflate(R.layout.view_sent_box, null);
		ListView sendList = (ListView) send_view.findViewById(R.id.send_list);
		sendadapter = new mailListSendAdapter(getApplicationContext(),
				sendDatalist, R.layout.cell_mail_list, new String[] { "title",
						"content", "time" }, new int[] { R.id.mail_title,
						R.id.mail_content_sh, R.id.mail_time });
		sendList.setAdapter(sendadapter);
		sendpulldownlist = (PullDownListView) send_view
				.findViewById(R.id.sendpulldownlist);
		sendpulldownlist.setRefreshListioner(new OnRefreshListioner() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				sendpage = new Pager(0, PAGE_SIZE);
				refreshSendList();
				mHandler.postDelayed(new Runnable() {
					public void run() {
						sendpulldownlist.onRefreshComplete();
						sendpulldownlist.setMore(true);
						sendadapter.notifyDataSetChanged();
					}
				}, 100);
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				mHandler.postDelayed(new Runnable() {
					public void run() {
						sendpulldownlist.onLoadMoreComplete();
						if (addSendList()) {
							sendpulldownlist.setMore(true);
						}
						sendadapter.notifyDataSetChanged();
					}
				}, 500);
			}
		});
		sendList = sendpulldownlist.mListView;
		sendpulldownlist.setMore(true);
		viewlist.add(send_view);

		sendList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mailBoxActivity.this,
						sendmailDetailActivity.class);
				int id = (Integer) sendDatalist.get(position - 1).get("id");
				intent.putExtra("id", id);
				startActivityForResult(intent, RESULT_OK);
			}
		});
		sendList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				initDeletePopupwin(position,arg1,Listkind.sent);
				return false;
			}
		});
	}

	private void refreshSendList() {
		sendMailHelper helper = new sendMailHelper(getApplicationContext());
		ArrayList<sendMailHolder> holderlist = helper.selectDataforList(
				HttpJsonTool.username, 0, sendpage.pagesize*(sendpage.curpage+1));
		sendDatalist.clear();
		for (sendMailHolder holder : holderlist) {
			Map<String, Object> content = new HashMap<String, Object>();
			content.put("id", holder.getId());
			content.put("title", holder.getSubject());
			content.put(
					"content",
					"收件人：" + holder.getEmailTo() + " 内容："
							+ Html.fromHtml(holder.getContent()));
			content.put("time", holder.getSentDate());
			content.put("hasattachment", holder.getAchment().length() > 0 ? 0
					: 0);
			sendDatalist.add(content);
		}
		helper.close();
	}

	private boolean addSendList() {
		sendpage.curpage++;
		sendMailHelper helper = new sendMailHelper(getApplicationContext());
		ArrayList<sendMailHolder> holderlist = helper.selectDataforList(
				HttpJsonTool.username, sendpage.curpage * sendpage.pagesize,sendpage.pagesize);
		for (sendMailHolder holder : holderlist) {
			Map<String, Object> content = new HashMap<String, Object>();
			content.put("id", holder.getId());
			content.put("title", holder.getSubject());
			content.put(
					"content",
					"收件人：" + holder.getEmailTo() + " 内容："
							+ Html.fromHtml(holder.getContent()));
			content.put("time", holder.getSentDate());
			content.put("hasattachment", holder.getAchment().length() > 0 ? 0
					: 0);
			sendDatalist.add(content);
		}
		helper.close();
		return holderlist.size() == sendpage.pagesize;
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshSendList();
		refreshReceiveList();
		receivepulldownlist.setMore(true);
		receiveadapter.notifyDataSetChanged();
		sendpulldownlist.setMore(true);
		sendadapter.notifyDataSetChanged();
	}

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				receiveButton
						.setImageResource(R.drawable.img_mail_receive_pressed);
				sentButton.setImageResource(R.drawable.img_mail_send);
				break;
			case 1:
				receiveButton.setImageResource(R.drawable.img_mail_receive);
				sentButton.setImageResource(R.drawable.img_mail_send_pressed);
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.receive_box_btn:
			mPager.setCurrentItem(0);
			break;
		case R.id.sent_box_btn:
			mPager.setCurrentItem(1);
			break;
		case R.id.write_email_btn:
			startActivityForResult(new Intent(getApplicationContext(),
					writeEmailActivity.class), RESULT_OK);
			break;
		case R.id.title_image:
			finish();
		default:
			break;
		}
	}

	private void pulldownRefreshReceiveBoxData() {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				HttpJsonTool.getInstance()
						.checkOutMail(getApplicationContext());
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (HttpJsonTool.state403) {
					gotoLoginView();
					return;
				}
				refreshReceiveList();
				receivepulldownlist.onRefreshComplete();
				receivepulldownlist.setMore(true);
				receiveadapter.notifyDataSetChanged();
			}
		};
		task.execute();
	}

	/*private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 4000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				return super.onKeyDown(keyCode, event);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(100, 1, 1, "退出登录");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 1) {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra("loginedit", 1);
			startActivity(intent);
			finish();
		}
		return super.onContextItemSelected(item);
	}

	private void gotoLoginView() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("loginedit", 1);
		Toast.makeText(getApplicationContext(), "您的账号已在其他设备上登录",
				Toast.LENGTH_LONG).show();
		startActivity(intent);
		HttpJsonTool.state403 = false;
		finish();
	}
}
