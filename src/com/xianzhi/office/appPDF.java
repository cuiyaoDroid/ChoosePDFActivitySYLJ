package com.xianzhi.office;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import com.xianzhi.service.autoPushEmailService;
import com.xianzhi.webtool.HttpsClient;

public class appPDF extends Application{
	public static String path = null;
	public static ArrayList<HashMap<String,Object>> stampHolderlist;
	public static boolean ifstampchange;
	private static String midpath;
	public static final String appPath=Environment.getExternalStorageDirectory()+ "/.xianzhioffice/";
	public static String token = "";
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		HttpsClient.getInstance().init(getApplicationContext());
		ifstampchange=false;
		stampHolderlist =  new ArrayList<HashMap<String,Object>>();
//		startAutoPushService();
	}
	public static String getPath() {
		return path;
	}
	public void startAutoPushService(){
		Intent intent=new Intent(this,autoPushEmailService.class);
		startService(intent);
	}
	public static String refreshPath(){
		int index=path.indexOf("._");
		if(index!=-1){
			path = path.substring(0, index);
		}else {
			path = path.substring(0, path.lastIndexOf("."));
		}
		path+="._"+getTime()+".pdf";
		return path;
	}
	public static String getTime(){
		Time t=new Time();
		t.setToNow();
		String time = String.format("%04d%02d%02d%02d%02d%02d", t.year,
				t.month+1, t.monthDay,
				t.hour,t.minute, t.second);
		
		return time;
	}
	public static String refreshMidPath(){
		midpath=path+"xxx.pdf";
		return midpath;
	}
	public static String getMidPath(){
		return midpath;
	}
}
