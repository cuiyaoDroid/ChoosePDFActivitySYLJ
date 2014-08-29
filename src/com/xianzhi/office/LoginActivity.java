package com.xianzhi.office;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.xianzhi.webtool.HttpJsonTool;

public class LoginActivity extends Activity implements OnClickListener {
	private ProgressDialog progressDialog;
	private ImageButton login_btn;
	private EditText name_input;
	private EditText password_input;
	private CheckBox savePassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initProgressDialog();
		initContentView();
		createPath(appPDF.appPath);
//		autoinsertContact();
		HttpJsonTool.getInstance().getCookieInfo();
	}

	private void initContentView() {
		appPDF.token = "";
		savePassword = (CheckBox) findViewById(R.id.save_password);
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		String name = userInfo.getString("name", "");
		String password = userInfo.getString("password", "");
		int savepassword = userInfo.getInt("savepassword", 0);
		// int loginedit = getIntent().getIntExtra("loginedit", 0);
		// if(savepassword==1&&loginedit==0){
		// Intent intent = new Intent(LoginActivity.this,
		// mailBoxActivity.class);
		// intent.putExtra("auto", 1);
		// startActivity(intent);
		// finish();
		// return;
		// }

		savePassword.setChecked(savepassword == 1 ? true : false);
		login_btn = (ImageButton) findViewById(R.id.login_btn);
		login_btn.setOnClickListener(this);
		name_input = (EditText) findViewById(R.id.name_input);
		password_input = (EditText) findViewById(R.id.password_input);
		name_input.setText(name);
		password_input.setText(password);
	}

	private void initProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("");
		progressDialog.setMessage("请稍等...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// progressDialog.setCancelable(false);
	}

	private void login(final String username, final String password) {
		progressDialog.show();
		AsyncTask<Void, Void, HttpJsonTool.LoginInfo> async = new AsyncTask<Void, Void, HttpJsonTool.LoginInfo>() {

			@Override
			protected HttpJsonTool.LoginInfo doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				HttpJsonTool json_tool = HttpJsonTool.getInstance();
				return json_tool.startLoginCheck(username, password);
			}

			@Override
			protected void onPostExecute(HttpJsonTool.LoginInfo result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				progressDialog.dismiss();
				if (result.equals(HttpJsonTool.LoginInfo.wronginput)) {
					Toast.makeText(getApplicationContext(), "用户名或密码错误",
							Toast.LENGTH_LONG).show();
					return;
				} else if (result.equals(HttpJsonTool.LoginInfo.webfaild)) {
					Toast.makeText(getApplicationContext(), "网络异常",
							Toast.LENGTH_LONG).show();
					return;
				} else if (result.equals(HttpJsonTool.LoginInfo.correctnoname)) {
					ShowEditNameDialog();
					return;
				}
//				EditName("测试账号改");
				gotoMailBox();
			}
		};
		async.execute();
	}

	private void EditName(final String name) {
		progressDialog.show();
		AsyncTask<Void, Void, Boolean> async = new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				HttpJsonTool json_tool = HttpJsonTool.getInstance();
				return json_tool.refreshName(name);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				progressDialog.dismiss();
				if (!result) {
					Toast.makeText(getApplicationContext(), "网络异常，请重试",
							Toast.LENGTH_LONG).show();
					return;
				}
				gotoMailBox();
			}
		};
		async.execute();
	}

	private void ShowEditNameDialog() {
		final EditText nameEdit = new EditText(LoginActivity.this);
		new AlertDialog.Builder(LoginActivity.this)
				.setTitle("请输入姓名")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(nameEdit)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						String name = nameEdit.getText().toString().trim();
						if (name.length() > 0) {
							EditName(name);
						} else {
							Toast.makeText(getApplicationContext(), "姓名不能为空",
									Toast.LENGTH_LONG).show();
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
	}

	private void createPath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	private void gotoMailBox() {
		Intent intent = new Intent(LoginActivity.this, mailBoxActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login_btn:
//			try {
//				HttpsClient.ssl(getApplicationContext());
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			String name = name_input.getText().toString().trim();
			String password = password_input.getText().toString().trim();
			if (name.equals("") || password.equals("")) {
				Toast.makeText(getApplicationContext(), "用户名和密码不能为空",
						Toast.LENGTH_LONG).show();
				break;
			}
			SharedPreferences userInfo = getSharedPreferences("user_info", 0);
			userInfo.edit().putString("name", name).commit();
			if (savePassword.isChecked()) {
				userInfo.edit().putString("password", password).commit();
				userInfo.edit().putInt("savepassword", 1).commit();
			} else {
				userInfo.edit().putString("password", "").commit();
				userInfo.edit().putInt("savepassword", 0).commit();
			}
			login(name, password);
			break;
		}
	}
/*
	@SuppressLint("UseSparseArrays")
	private void autoinsertContact() {
		Resources res = getResources();
		String[] employers = res.getStringArray(R.array.employer_item);
		ContactListStaticHelper helper = new ContactListStaticHelper(
				getApplicationContext());
		helper.clear();
		int count = 0;
		// Map<fatherid,childids>
		Map<Integer, String> em_idmap = new HashMap<Integer, String>();
		for (String employer : employers) {
			String[] employerDetail = employer.split(" ");
			int fatherid = Integer.parseInt(employerDetail[2]);
			int childid = 1000 + count;
			helper.insert(new ContactListStaticHolder(childid,
					employerDetail[0], employerDetail[1],
					ContactListStaticHelper.EmployeeFLAG, "", fatherid));
			String childids = em_idmap.get(fatherid);
			if (childids == null) {
				childids = childid + ",";
			} else {
				childids += childid + ",";
			}
			em_idmap.put(fatherid, childids);
			count++;
		}

		// Map<fatherid,childids>
		Map<Integer, String> depart_idmap = new HashMap<Integer, String>();
		String[] departments = res.getStringArray(R.array.department_item);
		for (String department : departments) {
			String[] employerDetail = department.split(" ");
			int fatherid = Integer.parseInt(employerDetail[2]);
			int childid = Integer.parseInt(employerDetail[0]);
			helper.insert(new ContactListStaticHolder(childid,
					employerDetail[1], "",
					ContactListStaticHelper.departmentFLAG, em_idmap
							.get(childid), fatherid));
			String childids = depart_idmap.get(fatherid);
			if (childids == null) {
				childids = childid + ",";
			} else {
				childids += childid + ",";
			}
			depart_idmap.put(fatherid, childids);
		}
		String[] provinces = res.getStringArray(R.array.province_item);
		for (String province : provinces) {
			String[] provinceDetail = province.split(" ");
			int id = Integer.parseInt(provinceDetail[0]);
			helper.insert(new ContactListStaticHolder(id,
					provinceDetail[1], "",
					ContactListStaticHelper.provinceFLAG, depart_idmap
							.get(id), -1));
		}
		helper.close();
	}*/
}
