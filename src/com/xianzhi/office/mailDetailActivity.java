package com.xianzhi.office;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.droidtext.tool.DensityUtil;
import com.artifex.mupdfdemo.MuPDFActivity;
import com.xianzhi.callbacklistener.downloadCallbackListener;
import com.xianzhi.tool.adapter.popEditTypeAdapter;
import com.xianzhi.tool.db.AchmentDetailHelper;
import com.xianzhi.tool.db.AchmentDetailHolder;
import com.xianzhi.tool.db.mailContentHelper;
import com.xianzhi.tool.db.mailContentHolder;
import com.xianzhi.tool.file.OpenFiles;
import com.xianzhi.webtool.DownloadAsyncTask;
import com.xianzhi.webtool.HttpJsonTool;

public class mailDetailActivity extends Activity implements OnClickListener,downloadCallbackListener{
	private ImageButton replyButton;
	private ImageButton gobackButton;
	private ImageView hasattachment;
	private RelativeLayout ifattactmentLayout;
	private RelativeLayout ifeditattactmentLayout;
	private LinearLayout attactmentLayout;
	private LinearLayout editattactmentLayout;
	private TextView titleText;
	private TextView receiverText;
	private TextView senderText;
	private TextView ccText;
	private TextView timeText;
	private WebView contentText;
	private mailContentHolder holder;
	private ImageButton receiver_spinner;
	private ImageButton cc_spinner;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mail_detail);
		initProgressDialog();
	}
	private void updataReadFlag(final int v_id){
		mailContentHelper helper=new mailContentHelper(getApplicationContext());
		helper.updataReadFlag(v_id, true);
		helper.close();
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				HttpJsonTool.getInstance().refreshReadFlag(v_id);
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(HttpJsonTool.state403){
					gotoLoginView();
					return;
				}
			}
		};
		task.execute();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initContentView();
		initEditTypePopupWindow();
	}
	private mailContentHolder getMailDetail(int v_id){
		mailContentHelper helper = new mailContentHelper(getApplicationContext());
		mailContentHolder holder = helper.selectData_id(v_id);
		helper.close();
		return holder;
	}
	private ProgressDialog progressDialog;
	private void initProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("");
		progressDialog.setMessage("正在下载...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(true);
	}
	private void initContentView() {
		replyButton = (ImageButton) findViewById(R.id.reply_email_btn);
		replyButton.setOnClickListener(this);
		gobackButton = (ImageButton) findViewById(R.id.goback_btn);
		gobackButton.setOnClickListener(this);
		
		int v_id = getIntent().getIntExtra("id", -1);
		if(v_id==-1){
			return;
		}
		
		holder = getMailDetail(v_id);
		if(holder.getReadFlag()==0){
			updataReadFlag(v_id);
		}
		hasattachment =(ImageView)findViewById(R.id.has_attachment);
		hasattachment.setVisibility(holder.getHasAttachment()==1?View.VISIBLE:View.GONE);
		ifattactmentLayout = (RelativeLayout)findViewById(R.id.ifattactment_layout);
		ifattactmentLayout.setVisibility(holder.getHasAttachment()==1?View.VISIBLE:View.GONE);
		attactmentLayout = (LinearLayout)findViewById(R.id.attactment_layout);
		attactmentLayout.setVisibility(holder.getHasAttachment()==1?View.VISIBLE:View.GONE);
		
		titleText = (TextView)findViewById(R.id.title_text);
		titleText.setText(holder.getSubject());
		receiverText = (TextView)findViewById(R.id.receiver_text);
		receiverText.setText(holder.getEmailTo());
		Log.i("holder", holder.getEmailTo());
		receiver_spinner = (ImageButton) findViewById(R.id.receiver_spinner);
		receiver_spinner.setOnClickListener(this);
		senderText = (TextView)findViewById(R.id.sender_text);
		senderText.setText(holder.getFromUser());
		ccText = (TextView)findViewById(R.id.cc_text);
		ccText.setText(holder.getCc());
		cc_spinner = (ImageButton) findViewById(R.id.cc_spinner);
		cc_spinner.setOnClickListener(this);
		timeText = (TextView)findViewById(R.id.time_text);
		timeText.setText(holder.getSentDate());
		contentText = (WebView)findViewById(R.id.content_text);
		contentText.loadDataWithBaseURL(null, holder.getContent(), mimeType, encoding, null);   
  
//		contentText.setText(Html.fromHtml(holder.getContent()));
		if(holder.getHasAttachment()==1){
			AchmentDetailHelper atthelper = new AchmentDetailHelper(getApplicationContext());
			ArrayList<AchmentDetailHolder> attHolderList = atthelper.selectData_inboxId(v_id);
			attactmentLayout.removeAllViews();
			for(AchmentDetailHolder attHolder:attHolderList){
				addAchmentView(attHolder);
			}
			atthelper.close();
		}
	}
	static final String mimeType = "text/html";   
	static final String encoding = "utf-8";   
	private void addAudioAchment(AchmentDetailHolder attHolder){
		LayoutInflater inflater = getLayoutInflater();
		View achmentView = inflater.inflate(R.layout.cell_audio_achment, null);
		TextView timeText=(TextView)achmentView.findViewById(R.id.audio1);
		final ImageView animation1=(ImageView)achmentView.findViewById(R.id.animation1);
		timeText.setText("");
		final String path=attHolder.getPath();
		final String name=attHolder.getName();
		final String size=attHolder.getSize()+"";
		final int id=attHolder.getId();
		final int downloadflag=attHolder.getDownloadflag();
		ClickAudiView=animation1;
		achmentView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(downloadflag==1&&(new File(appPDF.appPath+name).exists())){
					playAudio(appPDF.appPath+name);
				}else{
					downloadAchment(id,path,name,size);
				}
			}
		});
		attactmentLayout.addView(achmentView);
	}
	private ImageView ClickAudiView=null;
	private int[] s_amps=new int[]{R.drawable.s_amp0,R.drawable.s_amp1,R.drawable.s_amp2,R.drawable.s_amp3,R.drawable.s_amp4,R.drawable.s_amp5};
	private void playAudio(String path){
		
		final CountDownTimer count=new CountDownTimer(5*60000,100){
			int count=0;
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
			}
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				if(ClickAudiView==null){
					cancel();
					return;
				}
				ClickAudiView.setImageResource(s_amps[count%6]);
				count++;
			}
			
		};
		count.start();
		MediaPlayer mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(path);
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
	private void addAchmentView(AchmentDetailHolder attHolder){
		if(getExtensionName(attHolder.getName()).equals("amr")){
			addAudioAchment(attHolder);
			return;
		}
		LayoutInflater inflater = getLayoutInflater();
		View achmentView = inflater.inflate(R.layout.cell_attactment_view, null);
		TextView nameText = (TextView)achmentView.findViewById(R.id.attactment_name_text);
		nameText.setText("[原稿]"+attHolder.getName());
		int screenWidth  = getWindowManager().getDefaultDisplay().getWidth(); 
		nameText.setMaxWidth(screenWidth/2);
		TextView sizeText = (TextView)achmentView.findViewById(R.id.attactment_size_text);
		double achsize=attHolder.getSize();
		String str_achsize;
		DecimalFormat df = new DecimalFormat(".0#");//最多保留几位小数，就用几个#，最少位就用0来确定  
		if(achsize>0&&achsize<1024){
			str_achsize=df.format(achsize)+"B";
		}else if(achsize>1024&&achsize<1024*1024){
			achsize/=1024;
			str_achsize=df.format(achsize)+"K";
		}else {
			achsize/=1024*1024;
			str_achsize=df.format(achsize)+"M";
		}
		sizeText.setText("("+str_achsize+")");
		TextView downloadText = (TextView)achmentView.findViewById(R.id.attactment_download_text);
		final String path=attHolder.getPath();
		final String name=attHolder.getName();
		final String size=attHolder.getSize()+"";
		final int id=attHolder.getId();
		final int downloadflag=attHolder.getDownloadflag();
		downloadText.setText(downloadflag==1?R.string.read:R.string.download);
		downloadText.setClickable(true);
		downloadText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(downloadflag==1&&(new File(appPDF.appPath+name).exists())){
					if(getExtensionName(name).equals("pdf")){
						Intent intent = new Intent(mailDetailActivity.this, MuPDFActivity.class);
						intent.setAction(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(appPDF.appPath+name));
						intent.putExtra("id", id);
						appPDF.path=appPDF.appPath+name;
						startActivity(intent);
					}else {
						try {
							OpenFiles.OpenFile(getApplicationContext(), new File(appPDF.appPath+name));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Toast.makeText(getApplicationContext(), "打开文件失败", Toast.LENGTH_LONG).show();
						}
					}
				}else{
					downloadAchment(id,path,name,size);
				}
			}
		});
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float mDensity = dm.density;
		achmentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, (int) (50*mDensity)));
		attactmentLayout.addView(achmentView);
		int editachlen=attHolder.getEditname().length();
		ifeditattactmentLayout = (RelativeLayout)findViewById(R.id.ifeditattactment_layout);
		ifeditattactmentLayout.setVisibility(editachlen>0?View.VISIBLE:View.GONE);
		editattactmentLayout = (LinearLayout)findViewById(R.id.editattactment_layout);
		editattactmentLayout.setVisibility(editachlen>0?View.VISIBLE:View.GONE);
		editattactmentLayout.removeAllViews();
		if(editachlen>0){
			addEditAchmentView(attHolder);
		}
	}
	private void addEditAchmentView(final AchmentDetailHolder attHolder){
		LayoutInflater inflater = getLayoutInflater();
		View achmentView = inflater.inflate(R.layout.cell_attactment_view, null);
		TextView nameText = (TextView)achmentView.findViewById(R.id.attactment_name_text);
		nameText.setText("[审批]"+attHolder.getEditname());
		int screenWidth  = getWindowManager().getDefaultDisplay().getWidth(); 
		nameText.setMaxWidth(screenWidth/2);
		TextView sizeText = (TextView)achmentView.findViewById(R.id.attactment_size_text);
		double achsize=new File(appPDF.appPath+attHolder.getEditname()).length();
		String str_achsize;
		DecimalFormat df = new DecimalFormat(".0#");//最多保留几位小数，就用几个#，最少位就用0来确定  
		if(achsize>0&&achsize<1024){
			str_achsize=df.format(achsize)+"B";
		}else if(achsize>1024&&achsize<1024*1024){
			achsize/=1024;
			str_achsize=df.format(achsize)+"K";
		}else {
			achsize/=1024*1024;
			str_achsize=df.format(achsize)+"M";
		}
		sizeText.setText("("+str_achsize+")");
		TextView downloadText = (TextView)achmentView.findViewById(R.id.attactment_download_text);
		downloadText.setText(R.string.read);
		downloadText.setClickable(true);
		downloadText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mailDetailActivity.this, MuPDFActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(appPDF.appPath+attHolder.getEditname()));
				intent.putExtra("id", attHolder.getId());
				appPDF.path=appPDF.appPath+attHolder.getEditname();
				startActivity(intent);
			}
		});
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float mDensity = dm.density;
		achmentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, (int) (50*mDensity)));
		editattactmentLayout.addView(achmentView);
	}
	DownloadAsyncTask task;
	private void downloadAchment(int id,String path,String name,String size){
		progressDialog.show();
		String[] downLoadPath = { 
				path, name ,size, 
				appPDF.appPath+name ,id+""};  
		task=new DownloadAsyncTask(progressDialog);
		task.setdownloadCallbackListener(this);
		task.execute(downLoadPath);
	}
	boolean isreceiversingle = true;
	boolean isccsingle = true;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.reply_email_btn:
			showPopWindow(findViewById(R.id.reply_email_btn));
			break;
		case R.id.goback_btn:
			finish();
			break;
		case R.id.receiver_spinner:
			isreceiversingle = !isreceiversingle;
			receiverText.setSingleLine(isreceiversingle);
			receiver_spinner
					.setImageResource(isreceiversingle ? android.R.drawable.arrow_down_float
							: android.R.drawable.arrow_up_float);
			break;
		case R.id.cc_spinner:
			isccsingle = !isccsingle;
			ccText.setSingleLine(isccsingle);
			cc_spinner
					.setImageResource(isccsingle ? android.R.drawable.arrow_down_float
							: android.R.drawable.arrow_up_float);
			break;
		default:
			break;
		}
	}
	private PopupWindow EditTypePop;
	private popEditTypeAdapter popadapter;
	private List<Map<String, String>> popData;
	private void initEditTypePopupWindow() {
		popData = new ArrayList<Map<String, String>>();
		
		Map<String,String>data_1=new HashMap<String, String>();
		data_1.put("text", "回复");
		popData.add(data_1);
		Map<String,String>data_2=new HashMap<String, String>();
		data_2.put("text", "转发");
		popData.add(data_2);
		popadapter = new popEditTypeAdapter(getApplicationContext(), popData,
				R.layout.cell_poplist, new String[] { "text" },
				new int[] { R.id.autoedit_text });
		View popunwindwow = getLayoutInflater().inflate(
				R.layout.popupwin_reply_type, null);
		ListView list = (ListView)popunwindwow.findViewById(R.id.reply_type_listView);
		list.setAdapter(popadapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					Intent intent=new Intent(mailDetailActivity.this,writeEmailActivity.class);
					intent.putExtra("re", 1);
					intent.putExtra("id", holder.getId());
					startActivityForResult(intent, RESULT_OK);
					break;
				case 1:
					Intent reintent=new Intent(mailDetailActivity.this,writeEmailActivity.class);
					reintent.putExtra("re", 3);
					reintent.putExtra("id", holder.getId());
					startActivityForResult(reintent, RESULT_OK);
					break;
				default:
					break;
				}
				EditTypePop.dismiss();
			}
		});
		EditTypePop = new PopupWindow(popunwindwow, DensityUtil.dip2px(getApplicationContext(), 70),
				LayoutParams.WRAP_CONTENT);
		EditTypePop.setFocusable(true);
		EditTypePop.setBackgroundDrawable(new BitmapDrawable());
		EditTypePop.setOutsideTouchable(true);
	}
	private void showPopWindow(View view) {
		if (!EditTypePop.isShowing()) {
			EditTypePop.showAsDropDown(view);
		}
	}
	@Override
	public void onComplete(int id) {
		// TODO Auto-generated method stub
		AchmentDetailHelper helper=new AchmentDetailHelper(getApplicationContext());
		helper.updataDowloadFlag(id, true);
		AchmentDetailHolder holder = helper.selectData_Id(id);
		helper.close();
		if(holder!=null){
			if(holder.getDownloadflag()==1&&(new File(appPDF.appPath+holder.getName()).exists())){
				if(getExtensionName(holder.getName()).equals("pdf")){
					Intent intent = new Intent(mailDetailActivity.this, MuPDFActivity.class);
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(appPDF.appPath+holder.getName()));
					appPDF.path=appPDF.appPath+holder.getName();
					intent.putExtra("id", id);
					startActivity(intent);
				}else if(getExtensionName(holder.getName()).equals("amr")){
					playAudio(appPDF.appPath+holder.getName());
				}else {
					try {
						OpenFiles.OpenFile(getApplicationContext(), new File(appPDF.appPath+holder.getName()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(getApplicationContext(), "打开文件失败", Toast.LENGTH_LONG).show();
					}
				}
			}
		}
		initContentView();
	}
	public String getExtensionName(String filename) {   
        if ((filename != null) && (filename.length() > 0)) {   
            int dot = filename.lastIndexOf('.');   
            if ((dot >-1) && (dot < (filename.length() - 1))) {   
                return filename.substring(dot + 1);   
            }   
        }   
        return filename;   
    } 
	private void gotoLoginView(){
		Intent intent=new Intent(this,LoginActivity.class);
    	intent.putExtra("loginedit", 1);
    	Toast.makeText(getApplicationContext(), "您的账号已在其他设备上登录", Toast.LENGTH_LONG).show();
    	startActivity(intent);
    	HttpJsonTool.state403 = false;
    	finish();
	}
}
