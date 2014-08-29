package com.xianzhi.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.xianzhi.office.R;
import com.xianzhi.office.appPDF;
import com.xianzhi.office.mailBoxActivity;
import com.xianzhi.webtool.HttpJsonTool;

public class autoPushEmailService extends Service {

	private NotificationManager notificationMgr;
	private Timer mTimer;

	@Override
	public void onCreate() {
		super.onCreate();
		notificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mTimer = new Timer();
		mTimer.schedule(mTimerTask, 0, 15 * 60 * 1000);
	}

	@SuppressWarnings("deprecation")
	private void shownotification(int count) {
		Notification notification = new Notification();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.icon = R.drawable.icon_main_gwlz;
		String tickerText = "有" + count + "封新邮件";
		PendingIntent contentIndent = PendingIntent.getActivity(this, 0,
				new Intent(this, mailBoxActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification
				.setLatestEventInfo(this, "邮件提醒", tickerText, contentIndent);
		notificationMgr.notify(R.id.app_notification_id, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private final TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (appPDF.token.length() == 0) {
				return;
			}
			int num = HttpJsonTool.getInstance().checkMailforNotif(
					getApplicationContext());
			if (num > 0)
				shownotification(num);
		}
	};

	@Override
	public void onDestroy() {
		notificationMgr.cancel(R.id.app_notification_id);
		mTimer.cancel();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("autoPushEmailServer", "onStartCommand()");
		return Service.START_NOT_STICKY;
	}
}
