package com.xianzhi.office;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.ChoosePDFActivity;
import com.xianzhi.callbacklistener.downloadAchmentListListener;
import com.xianzhi.tool.adapter.popListAdapter;
import com.xianzhi.tool.db.AchmentDetailHelper;
import com.xianzhi.tool.db.AchmentDetailHolder;
import com.xianzhi.tool.db.ContactListStaticHelper;
import com.xianzhi.tool.db.ContactListStaticHolder;
import com.xianzhi.tool.db.mailContentHelper;
import com.xianzhi.tool.db.mailContentHolder;
import com.xianzhi.tool.db.sendMailHelper;
import com.xianzhi.tool.db.sendMailHolder;
import com.xianzhi.tool.view.AudioDialog;
import com.xianzhi.tool.view.SelectDoublePopupWindow;
import com.xianzhi.webtool.DownloadAchmentListTask;
import com.xianzhi.webtool.HttpJsonTool;

public class writeEmailActivity extends Activity implements OnClickListener,downloadAchmentListListener {
	private ImageButton sendButton;
	private ImageButton gobackButton;
	private ImageButton addRecevierButton;
	private ImageButton addattachmentButton;
	private LinearLayout ccList;
	private LinearLayout attachmentList;
	private EditText receiverEidt;
	private EditText titleEdit;
	private EditText contentEdit;
	private WebView recontentEdit;
	public static final int Result_attachment = 1;
	private PopupWindow autoEditPop;
	private popListAdapter popadapter;
	private List<Map<String, String>> popData;
	public static EditText editingText = null;
	private ImageView mac1;
	private RelativeLayout audioInfo1;
	private TextView audio1;
	private ImageView animation1;
	private LinearLayout audioLayout1;
	private ScrollView scrollView1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_email);
		initContentView();
		// initAutoPopupWindow();
		getContactData();
	}

/*	private void initAutoPopupWindow() {
		popData = new ArrayList<Map<String, String>>();
		autoEditPop = new PopupWindow(this);
		ListView list = new ListView(this);
		popadapter = new popListAdapter(getApplicationContext(), popData,
				R.layout.cell_poplist, new String[] { "text" },
				new int[] { R.id.autoedit_text });
		list.setAdapter(popadapter);
		list.setBackgroundColor(Color.WHITE);
		list.setDividerHeight(0);
		autoEditPop = new PopupWindow(list, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		autoEditPop.setFocusable(false);
		autoEditPop.setBackgroundDrawable(new BitmapDrawable());
		autoEditPop.setOutsideTouchable(true);
		autoEditPop.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
			}
		});
		receiverEidt.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				showPopWindow(receiverEidt);
				popadapter.notifyDataSetChanged();
			}
		});
	}

	private void showPopWindow(EditText view) {
		String text = view.getText().toString().trim();
		if (text.length() == 0) {
			popData.clear();
			return;
		}
		editingText = view;
		if (!autoEditPop.isShowing()) {
			autoEditPop.showAsDropDown(view);
		}
		ContactListHelper helper = new ContactListHelper(
				getApplicationContext());
		ArrayList<ContactListHolder> selectData = helper.selectData(text);
		helper.close();
		popData.clear();
		for (ContactListHolder holder : selectData) {
			Map<String, String> data = new HashMap<String, String>();
			data.put("text", holder.getName() + holder.getAddress());
			popData.add(data);
		}
	}*/

	private AudioDialog audioDialog = null;
	private String audioname = "";

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		File audio = new File(audioname);
		if (audio.exists()) {
			audio.delete();
		}
	}

	private void initAudioView() {
		mac1 = (ImageView) findViewById(R.id.mac1);
		audio1 = (TextView) findViewById(R.id.audio1);
		audioLayout1 = (LinearLayout) findViewById(R.id.audio_layout1);
		audioInfo1 = (RelativeLayout) findViewById(R.id.audio_info1);
		animation1 = (ImageView) findViewById(R.id.animation1);
		mac1.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				File audio = new File(audioname);
				if (audio.exists()) {
					audio.delete();
				}
				audioDialog = new AudioDialog(writeEmailActivity.this,
						R.style.audioDialog);
				audioDialog.setCanceledOnTouchOutside(false);
				audioDialog.show();
				return false;
			}
		});
		scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
		scrollView1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_OUTSIDE:
					if (audioDialog != null) {
						if (!audioDialog.isShowing()) {
							break;
						}
						audioname = appPDF.appPath + audioDialog.getStartTime()
								+ ".amr";
						new File(audioname).delete();
						audioDialog.dismiss();
					}
					break;
				case MotionEvent.ACTION_UP:
					if (audioDialog != null) {
						if (!audioDialog.isShowing()) {
							break;
						}
						Long during = audioDialog.getmDuring();
						audioname = appPDF.appPath + audioDialog.getStartTime()
								+ ".amr";
						audioDialog.dismiss();
						refreshAudioView(during);
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
		mac1.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_OUTSIDE:
					if (audioDialog != null) {
						if (!audioDialog.isShowing()) {
							break;
						}
						audioname = appPDF.appPath + audioDialog.getStartTime()
								+ ".amr";
						new File(audioname).delete();
						audioDialog.dismiss();
					}
					break;
				case MotionEvent.ACTION_UP:
					if (audioDialog != null) {
						if (!audioDialog.isShowing()) {
							break;
						}
						Long during = audioDialog.getmDuring();
						audioname = appPDF.appPath + audioDialog.getStartTime()
								+ ".amr";
						audioDialog.dismiss();
						refreshAudioView(during);
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
	}

	private int[] s_amps = new int[] { R.drawable.s_amp0, R.drawable.s_amp1,
			R.drawable.s_amp2, R.drawable.s_amp3, R.drawable.s_amp4,
			R.drawable.s_amp5 };

	private void refreshAudioView(final Long during) {
		audioInfo1.setVisibility(View.VISIBLE);
		int time = (int) (during / 1000);
		audio1.setText(time + "″");
		audioInfo1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final CountDownTimer count = new CountDownTimer(during + 1000,
						100) {
					int count = 0;

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
					}

					@Override
					public void onTick(long millisUntilFinished) {
						// TODO Auto-generated method stub
						animation1.setImageResource(s_amps[count % 6]);
						count++;
					}

				};
				count.start();
				MediaPlayer mMediaPlayer = new MediaPlayer();
				try {
					mMediaPlayer.reset();
					mMediaPlayer.setDataSource(audioname);
					mMediaPlayer.prepare();
					mMediaPlayer.start();
					mMediaPlayer.setLooping(false);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mMediaPlayer
						.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mMediaPlayer) {
								mMediaPlayer.release();
								count.cancel();
							}
						});
			}
		});

	}

	private void initContentView() {
		sendButton = (ImageButton) findViewById(R.id.sent_email_btn);
		sendButton.setOnClickListener(this);
		gobackButton = (ImageButton) findViewById(R.id.goback_btn);
		gobackButton.setOnClickListener(this);
		addRecevierButton = (ImageButton) findViewById(R.id.add_recevier_btn);
		addRecevierButton.setOnClickListener(this);
		addattachmentButton = (ImageButton) findViewById(R.id.add_attachment_btn);
		addattachmentButton.setOnClickListener(this);
		titleEdit = (EditText) findViewById(R.id.title_ed_text);
		contentEdit = (EditText) findViewById(R.id.content_ed_text);
		recontentEdit = (WebView) findViewById(R.id.re_content_ed_text);
		recontentEdit.setVisibility(View.GONE);
		receiverEidt = (EditText) findViewById(R.id.receiver_ed_text);
		receiverEidt.setOnClickListener(this);
		initAudioView();

		initProgressDialog();
		initDowloadDialog();
		initReceiverList();
		initattachmentList();
		Intent re_intent = getIntent();
		int re = re_intent.getIntExtra("re", 0);
		if (re == 1) {
			int re_id = re_intent.getIntExtra("id", -1);
			if (re_id == -1) {
				return;
			}
			mailContentHelper helper = new mailContentHelper(this);
			mailContentHolder holder = helper.selectData_id(re_id);
			helper.close();
			if (holder == null) {
				return;
			}
			titleEdit.setText("回复：" + holder.getSubject());
			receiverEidt.setText(holder.getFromUser());
			recontentEdit.setVisibility(View.VISIBLE);
			re_content = "------------------ 原始邮件 ------------------<br /><br />"
					+ "发件人："
					+ holder.getFromUser()
					+ "<br /><br />"
					+ "收件人："
					+ holder.getEmailTo()
					+ "<br /><br />"
					+ ((holder.getCc().length() > 0) ? "抄送：" + holder.getCc()
							+ "<br /><br />" : "") + "主题：" + holder.getSubject()
					+ "<br /><br />" + holder.getContent();
			// recontentEdit.setText(re_content);
			recontentEdit.loadDataWithBaseURL(null,
					re_content, mimeType,
					encoding, null);
			AchmentDetailHelper achhelper = new AchmentDetailHelper(
					getApplicationContext());
			ArrayList<AchmentDetailHolder> achholderlist = achhelper
					.selectData_inboxId(re_id);
			achhelper.close();
			for (AchmentDetailHolder achholder : achholderlist) {
				String editname = achholder.getEditname();
				if (editname.length() > 0) {
					attachmentAdditem(appPDF.appPath + editname);
				}
			}

		} else if (re == 2) {
			int pv_id = re_intent.getIntExtra("id", -1);
			if (pv_id == -1) {
				return;
			}
			sendMailHelper helper = new sendMailHelper(this);
			sendMailHolder holder = helper.selectData_id(pv_id);
			helper.close();
			if (holder == null) {
				return;
			}
			titleEdit.setText(holder.getSubject());
			contentEdit.setText(Html.fromHtml(holder.getContent()));
			receiverEidt.setText(holder.getEmailTo());
			String[] attachments = holder.getAchment().split(" ");
			for (String attachment : attachments) {
				attachmentAdditem(attachment);
			}
			String[] ccs = holder.getCc().split(",");
			for (String cc : ccs) {
				ccAdditem(cc);
			}
		}else if (re == 3) {
			int re_id = re_intent.getIntExtra("id", -1);
			if (re_id == -1) {
				return;
			}
			mailContentHelper helper = new mailContentHelper(this);
			mailContentHolder holder = helper.selectData_id(re_id);
			helper.close();
			if (holder == null) {
				return;
			}
			titleEdit.setText("转发：" + holder.getSubject());
			recontentEdit.setVisibility(View.VISIBLE);
			re_content = "------------------ 原始邮件 ------------------<br /><br />"
					+ "发件人："
					+ holder.getFromUser()
					+ "<br /><br />"
					+ "收件人："
					+ holder.getEmailTo()
					+ "<br /><br />"
					+ ((holder.getCc().length() > 0) ? "抄送：" + holder.getCc()
							+ "<br /><br />" : "") + "主题：" + holder.getSubject()
					+ "<br /><br />" + holder.getContent();
			// recontentEdit.setText(re_content);
			recontentEdit.loadDataWithBaseURL(null,
					re_content, mimeType,
					encoding, null);
			AchmentDetailHelper achhelper = new AchmentDetailHelper(
					getApplicationContext());
			ArrayList<AchmentDetailHolder> achholderlist = achhelper
					.selectData_inboxId(re_id);
			achhelper.close();
			for (AchmentDetailHolder achholder : achholderlist) {
				String achmentname = achholder.getName();
				String path=achholder.getPath();
				String size=achholder.getSize()+"";
				int id=achholder.getId();
				int downloadflag=achholder.getDownloadflag();
				if(downloadflag!=1||(!new File(appPDF.appPath+achmentname).exists())){
					downloadAchment(id,path,achmentname,size);
				}
				else if (achmentname.length() > 0) {
					attachmentAdditem(appPDF.appPath + achmentname);
				}
			}
		}
	}
	private int downloadingflag=0;
	private void downloadAchment(int id,String path,String name,String size){
		if(downloadingflag==0&&!downloadDialog.isShowing()){
			downloadDialog.show();
		}
		downloadingflag++;
		String[] downLoadPath = { 
				path, name ,size, 
				appPDF.appPath+name ,id+""};  
		DownloadAchmentListTask task=new DownloadAchmentListTask();
		task.setdownloadAchmentListener(this);
		task.execute(downLoadPath);
	}
	private String re_content;
	static final String mimeType = "text/html";
	static final String encoding = "utf-8";
	private ProgressDialog progressDialog;

	private void initProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("");
		progressDialog.setMessage("请稍等...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// progressDialog.setCancelable(false);
	}
	private ProgressDialog downloadDialog;

	private void initDowloadDialog() {
		downloadDialog = new ProgressDialog(this);
		downloadDialog.setTitle("");
		downloadDialog.setMessage("正在读取附件,请稍等...");
		downloadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		downloadDialog.setCancelable(false);
	}
	private void initattachmentList() {
		attachmentList = (LinearLayout) findViewById(R.id.attachment_list);
	}

	private void attachmentAdditem(String path) {
		LayoutInflater inflater = getLayoutInflater();
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float mDensity = dm.density;
		final View attachmentChild = inflater.inflate(
				R.layout.cell_attachment_list, null);
		attachmentChild.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, (int) (30 * mDensity)));
		EditText attachmentEdit = (EditText) attachmentChild
				.findViewById(R.id.attchment_edit);
		ImageButton browseButton = (ImageButton) attachmentChild
				.findViewById(R.id.cell_browse_btn);
		browseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attachmentList.removeView(attachmentChild);
				Intent intent = new Intent(writeEmailActivity.this,
						ChoosePDFActivity.class);
				startActivityForResult(intent, Result_attachment);
			}
		});
		attachmentEdit.setText(path);
		ImageButton deletebtn = (ImageButton) attachmentChild
				.findViewById(R.id.cell_delete_btn);
		deletebtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attachmentList.removeView(attachmentChild);
			}
		});
		attachmentList.addView(attachmentChild);
	}

	private String getattachments() {
		String attachments = "";
		for (int i = 0; i < attachmentList.getChildCount(); i++) {
			View view = attachmentList.getChildAt(i);
			EditText attachmentEdit = (EditText) view
					.findViewById(R.id.attchment_edit);
			attachments += attachmentEdit.getText().toString().trim() + " ";
		}
		attachments += audioname;
		return attachments;
	}

	private void initReceiverList() {
		ccList = (LinearLayout) findViewById(R.id.cc_list);
	}

	private void ccAdditem(String ccName) {
		LayoutInflater inflater = getLayoutInflater();
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float mDensity = dm.density;
		final View ccChild = inflater
				.inflate(R.layout.cell_receiver_list, null);
		ccChild.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				(int) (30 * mDensity)));
		ImageButton deletebtn = (ImageButton) ccChild
				.findViewById(R.id.cell_delete_btn);
		deletebtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ccList.removeView(ccChild);
			}
		});
		final EditText nameedit = (EditText) ccChild
				.findViewById(R.id.receiver_edit);
		nameedit.setText(ccName);
		nameedit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				contactpopupWindow = new SelectDoublePopupWindow(
						writeEmailActivity.this, nameedit, valuelist);
				// 显示窗口
				contactpopupWindow.showAtLocation(
						findViewById(R.id.write_layout), Gravity.BOTTOM
								| Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
		/*
		 * nameedit.addTextChangedListener(new TextWatcher() {
		 * 
		 * @Override public void afterTextChanged(Editable s) { // TODO
		 * Auto-generated method stub }
		 * 
		 * @Override public void beforeTextChanged(CharSequence s, int start,
		 * int count, int after) { // TODO Auto-generated method stub }
		 * 
		 * @Override public void onTextChanged(CharSequence s, int start, int
		 * before, int count) { // TODO Auto-generated method stub
		 * showPopWindow(nameedit); popadapter.notifyDataSetChanged(); } });
		 */
		ccList.addView(ccChild);
	}

	private String getccs() {
		String cc = "";
		for (int i = 0; i < ccList.getChildCount(); i++) {
			View view = ccList.getChildAt(i);
			EditText receiverEdit = (EditText) view
					.findViewById(R.id.receiver_edit);
			String name = receiverEdit.getText().toString().trim();
			if (name.length() > 0) {
				cc += name + ",";
			}
		}
		return cc;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sent_email_btn:
			sendEmail();
			break;
		case R.id.goback_btn:
			finish();
			break;
		case R.id.add_recevier_btn:
			ccAdditem("");
			break;
		case R.id.add_attachment_btn:
			Intent intent = new Intent(writeEmailActivity.this,
					ChoosePDFActivity.class);
			startActivityForResult(intent, Result_attachment);
			break;
		case R.id.receiver_ed_text:
			if (valuelist.size() == 0) {
				getContactData();
			}
			contactpopupWindow = new SelectDoublePopupWindow(this,
					receiverEidt, valuelist);
			// 显示窗口
			contactpopupWindow.showAtLocation(
					this.findViewById(R.id.write_layout), Gravity.BOTTOM
							| Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		default:
			break;
		}
	}

	private HashMap<String, ArrayList<String>> valuelist;
	private SelectDoublePopupWindow contactpopupWindow;

	private void getContactData() {
		valuelist = new HashMap<String, ArrayList<String>>();
		ContactListStaticHelper helper = new ContactListStaticHelper(
				getApplicationContext());
		ArrayList<ContactListStaticHolder> de_holderlist = helper
				.selectflag(ContactListStaticHelper.departmentFLAG);
		for (ContactListStaticHolder holder : de_holderlist) {
			ContactListStaticHolder father = helper.selectId(holder
					.getFatherid());
			String name = "";
			if (father != null) {
				name = father.getName() + holder.getName();
			}
			ArrayList<String> childlist = new ArrayList<String>();
			String[] childs = holder.getChildids().split(",");
			for (String c_id : childs) {
				int id = Integer.parseInt(c_id);
				ContactListStaticHolder ho = helper.selectId(id);
				childlist.add(ho.getName() + ho.getAddress());
			}
			valuelist.put(name, childlist);
		}
		helper.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case Result_attachment:
			attachmentAdditem(data.getStringExtra("path"));
			break;
		default:
			break;
		}
	}

	private void writeEmailcache(sendMailHolder contentHolder) {
		sendMailHelper helper = new sendMailHelper(getApplicationContext());
		String[] achments=contentHolder.getAchment().split(" ");
		String arr_achment="";
		for(String achment:achments){
			String name=getFileName(achment);
			if(name==null){
				continue;
			}
			if(!achment.equals(appPDF.appPath+name))
				copyFile(achment,appPDF.appPath+name);
			arr_achment+=appPDF.appPath+name+" ";
		}
		contentHolder.setAchment(arr_achment);
		helper.insert(contentHolder);
		helper.close();
	}
	private String getFileName(String path){
		int index=path.lastIndexOf("/")+1;
		if(index==-1){
			return null;
		}
		return path.substring(index);
	}
	public boolean copyFile(String oldPath, String newPath) {

		boolean isok = true;
		try {
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[256];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				fs.flush();
				fs.close();
				inStream.close();
			} else {
				isok = false;
			}
		} catch (Exception e) {
			isok = false;
		}
		return isok;
	}
	/*
	 * private void writeContactcache(ContactListHolder contentHolder) {
	 * ContactListHelper helper = new
	 * ContactListHelper(getApplicationContext());
	 * helper.autoidinsert(contentHolder); helper.close(); }
	 */
	@SuppressLint("SimpleDateFormat")
	private void sendEmail() {
		progressDialog.show();
		String userName = HttpJsonTool.username;
		String subject = titleEdit.getText().toString().trim();
		String content = contentEdit.getText().toString()
				+ ((recontentEdit.getVisibility() == View.VISIBLE) ? "<br /><br />"
						+ re_content : "");
		String emailto = receiverEidt.getText().toString().trim();
		String cc = getccs();
		String bcc = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Time t = new Time();
		t.setToNow();
		String sentDate = sdf.format(t.toMillis(true));
		String achment = getattachments();
		final sendMailHolder contentHolder = new sendMailHolder(0, userName,
				subject, content, emailto, cc,
				bcc, sentDate, achment);
		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
				writeEmailcache(contentHolder);
				return HttpJsonTool.getInstance().sendMail(contentHolder);
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				progressDialog.dismiss();
				if (HttpJsonTool.state403) {
					gotoLoginView();
					return;
				}
				if (result.equals("邮件发送成功")) {
					// writeContactcache(new ContactListHolder(id, name,
					// address));
					Toast.makeText(getApplicationContext(), result,
							Toast.LENGTH_LONG).show();
					finish();
				} else {
					Toast.makeText(getApplicationContext(), result,
							Toast.LENGTH_LONG).show();
				}
			}
		};
		task.execute();
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


	@Override
	public void onComplete(String name) {
		// TODO Auto-generated method stub
		downloadingflag--;
		if(downloadingflag==0&&downloadDialog.isShowing()){
			downloadDialog.dismiss();
		}
		if (name.length() > 0) {
			attachmentAdditem(appPDF.appPath + name);
		}
	}
}
