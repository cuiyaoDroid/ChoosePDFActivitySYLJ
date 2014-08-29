package com.xianzhi.office;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.xianzhi.tool.db.sendMailHelper;
import com.xianzhi.tool.db.sendMailHolder;
import com.xianzhi.tool.file.OpenFiles;

public class sendmailDetailActivity extends Activity implements OnClickListener {
	private ImageButton gobackButton;
	private ImageView hasattachment;
	private TextView titleText;
	private TextView receiverText;
	private TextView ccText;
	private TextView timeText;
	private WebView contentText;
	private sendMailHolder holder;
	private RelativeLayout ifattactmentLayout;
	private LinearLayout attactmentLayout;
	private ImageButton replyButton;
	private ImageButton receiver_spinner;
	private ImageButton cc_spinner;
	private int v_id = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mail_send_detail);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initContentView();
	}

	private sendMailHolder getMailDetail(int v_id) {
		sendMailHelper helper = new sendMailHelper(getApplicationContext());
		sendMailHolder holder = helper.selectData_id(v_id);
		helper.close();
		return holder;
	}

	private void initContentView() {
		gobackButton = (ImageButton) findViewById(R.id.goback_btn);
		gobackButton.setOnClickListener(this);
		replyButton = (ImageButton) findViewById(R.id.reply_email_btn);
		replyButton.setOnClickListener(this);
		v_id = getIntent().getIntExtra("id", -1);
		if (v_id == -1) {
			return;
		}

		holder = getMailDetail(v_id);
		hasattachment = (ImageView) findViewById(R.id.has_attachment);
		hasattachment
				.setVisibility(holder.getAchment().length() > 0 ? View.GONE
						: View.GONE);

		titleText = (TextView) findViewById(R.id.title_text);
		titleText.setText(holder.getSubject());
		receiverText = (TextView) findViewById(R.id.receiver_text);
		receiverText.setText(holder.getEmailTo());
		ccText = (TextView) findViewById(R.id.cc_text);
		ccText.setText(holder.getCc());
		cc_spinner = (ImageButton) findViewById(R.id.cc_spinner);
		cc_spinner.setOnClickListener(this);
		timeText = (TextView) findViewById(R.id.time_text);
		timeText.setText(holder.getSentDate());
		receiver_spinner = (ImageButton) findViewById(R.id.receiver_spinner);
		receiver_spinner.setOnClickListener(this);
		contentText = (WebView) findViewById(R.id.content_text);
		contentText.loadDataWithBaseURL(null, holder.getContent(), mimeType,
				encoding, null);
		ifattactmentLayout = (RelativeLayout) findViewById(R.id.ifattactment_layout);
		ifattactmentLayout
				.setVisibility(holder.getAchment().length() > 0 ? View.VISIBLE
						: View.GONE);
		attactmentLayout = (LinearLayout) findViewById(R.id.attactment_layout);
		attactmentLayout
				.setVisibility(holder.getAchment().length() > 0 ? View.VISIBLE
						: View.GONE);
		attactmentLayout.removeAllViews();
		String[] achments = holder.getAchment().split(" ");
		for (String achment : achments) {
			addAchmentView(achment);
		}
	}

	static final String mimeType = "text/html";
	static final String encoding = "utf-8";

	private void addAchmentView(final String path) {
		LayoutInflater inflater = getLayoutInflater();
		View achmentView = inflater
				.inflate(R.layout.cell_attactment_view, null);
		TextView nameText = (TextView) achmentView
				.findViewById(R.id.attactment_name_text);
		nameText.setText(convertUrlToFileName(path));
		TextView sizeText = (TextView) achmentView
				.findViewById(R.id.attactment_size_text);
		TextView downloadText = (TextView) achmentView
				.findViewById(R.id.attactment_download_text);
		sizeText.setVisibility(View.GONE);
		downloadText.setText(R.string.read);
		downloadText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(getExtensionName(path).equals("pdf")){
					Intent intent = new Intent(sendmailDetailActivity.this, MuPDFActivity.class);
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(path));
					intent.putExtra("id", -1);
					appPDF.path=path;
					startActivity(intent);
				}else {
					try {
						OpenFiles.OpenFile(getApplicationContext(), new File(path));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(getApplicationContext(), "打开文件失败", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float mDensity = dm.density;
		achmentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				(int) (50 * mDensity)));
		attactmentLayout.addView(achmentView);
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
	private String convertUrlToFileName(String url) {
		String[] strs = url.split("/");
		return strs[strs.length - 1];
	}
	boolean isreceiversingle = true;
	boolean isccsingle = true;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.goback_btn:
			finish();
			break;
		case R.id.reply_email_btn:
			Intent intent = new Intent(this, writeEmailActivity.class);
			intent.putExtra("re", 2);
			intent.putExtra("id", v_id);
			intent.putExtra("subject", holder.getSubject());
			startActivityForResult(intent, RESULT_OK);
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
}
