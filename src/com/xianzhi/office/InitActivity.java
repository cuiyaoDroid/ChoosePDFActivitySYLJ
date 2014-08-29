package com.xianzhi.office;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

public class InitActivity extends Activity {
	public static String SDCardRoot = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + File.separator;
	public static String RAIL = "rail";

	public static void putVersion(String s) {
		try {
			FileOutputStream outStream = new FileOutputStream(SDCardRoot + RAIL
					+ "/5.txt", false);
			OutputStreamWriter writer = new OutputStreamWriter(outStream,
					"gb2312");
			writer.write(s);
			writer.flush();
			writer.close();// ¼ÇµÃ¹Ø±Õ

			outStream.close();
		} catch (Exception e) {
			System.out.println("write to sdcard for error");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		putVersion(getString(R.string.version));
		Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
		startActivityForResult(intent, RESULT_OK);
		finish();
		
	}
}
