package com.xianzhi.tool.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;

import com.xianzhi.office.R;

public class CustomDialog extends AlertDialog {
	private ImageView imageviews[] = new ImageView[7];
	private int imageviewIds[] = { R.id.dot_image1, R.id.dot_image2,
			R.id.dot_image3, R.id.dot_image4, R.id.dot_image5, R.id.dot_image6,
			R.id.dot_image7 };
	private int imageIds[] = { R.drawable.img_dot_1, R.drawable.img_dot_2,
			R.drawable.img_dot_3, R.drawable.img_dot_4 };

	private TextView infoview;
	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomDialog(Context context) {
		super(context);
	}
	CountDownTimer count;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_dialog);
		for (int i = 0; i < 7; i++) {
			imageviews[i] = (ImageView) findViewById(imageviewIds[i]);
		}
		infoview=(TextView)findViewById(R.id.textView3);
		count=new CountDownTimer(3000000, 200){
			int surrondcount = 0;
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				dismiss();
			}
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				if(!isShowing())
					cancel();
				for (int i = 0; i < 7; i++) {
					int num=surrondcount%11;
					if(i==num-2){
						imageviews[i].setImageResource(imageIds[3]);
					}else if(i==num-3||i==num-1){
						imageviews[i].setImageResource(imageIds[2]);
					}else if(i==num-4||i==num){
						imageviews[i].setImageResource(imageIds[1]);
					}else{
						imageviews[i].setImageResource(imageIds[0]);
					}
					imageviews[i].invalidate();
				}
				surrondcount++;
			}
			
		};
		count.start();
	}
	public void setInfo(String info){
		infoview.setText(info);
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		count.cancel();
	}
}