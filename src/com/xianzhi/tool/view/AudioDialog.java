package com.xianzhi.tool.view;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.xianzhi.office.R;
import com.xianzhi.office.appPDF;

public class AudioDialog extends AlertDialog {
	private MediaRecorder mediaRecorder;
	private boolean recording=false;
	public AudioDialog(Context context, int theme) {
		super(context, theme);
		mediaRecorder = new MediaRecorder();
	}

	public AudioDialog(Context context) {
		super(context);
	}
	CountDownTimer count;
	private Long startTime;
	private Long stopTime;
	private Long mDuring;
	private ImageView volume;
	public MediaRecorder getMediaRecorder() {
		return mediaRecorder;
	}

	public void setMediaRecorder(MediaRecorder mediaRecorder) {
		this.mediaRecorder = mediaRecorder;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getStopTime() {
		return stopTime;
	}

	public void setStopTime(Long stopTime) {
		this.stopTime = stopTime;
	}

	public Long getmDuring() {
		stopTime=System.currentTimeMillis();
		mDuring=stopTime-startTime;
		return mDuring;
	}

	public void setmDuring(Long mDuring) {
		this.mDuring = mDuring;
	}

	public void setRecording(boolean recording) {
		this.recording = recording;
	}

	public boolean getRecording(){
		return recording;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_rcd_hint_window);
		volume=(ImageView)findViewById(R.id.volume);
		count=new CountDownTimer(60*1000, 200){
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
				double amp = mediaRecorder.getMaxAmplitude()/2700.0;
				updateDisplay(amp);
			}
			
		};
		count.start();
	}
	private void updateDisplay(double signalEMA) {

		switch ((int) signalEMA) {
		case 0:
			volume.setImageResource(R.drawable.amp1);
			break;
		case 1:
		case 2:
			volume.setImageResource(R.drawable.amp2);
			break;
		case 3:
		case 4:
			volume.setImageResource(R.drawable.amp3);
			break;
		case 5:
			volume.setImageResource(R.drawable.amp4);
			break;
		case 6:
			volume.setImageResource(R.drawable.amp5);
			break;
		case 7:
			volume.setImageResource(R.drawable.amp6);
			break;
		default:
			volume.setImageResource(R.drawable.amp7);
			break;
		}
	}
	public void startRecord(String name) {
		recording=true;
		try {
			mediaRecorder = new MediaRecorder();  
			// 创建音频输出的文件路径  
			File soundFile = new File(appPDF.appPath+name+".amr");  
			// 设置录音的来源为麦克风  
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  
			// 设置录制的声音输出格式  
			mediaRecorder  
			.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);  
			// 设置声音的编码格式  
			mediaRecorder  
			.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  
			// 设置录音的输出文件路径  
			mediaRecorder.setOutputFile(soundFile.getAbsolutePath());  

			// 做预期准备  
			mediaRecorder.prepare();
			// 开始录音  
			mediaRecorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (RuntimeException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} 


	}
	/**
	 * 结束录音
	 */
	public void stopRecord() {
		recording=false;
		try {
			mediaRecorder.stop();
		} catch (IllegalStateException e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getAction()){
		case MotionEvent.ACTION_UP:
			dismiss();
			break;
		}
		return super.onTouchEvent(event);
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		startTime=System.currentTimeMillis();
		startRecord(startTime+"");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		count.cancel();
		stopRecord();
	}
}