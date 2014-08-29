package com.xianzhi.webtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.xianzhi.office.appPDF;
import com.xianzhi.tool.db.AchmentDetailHelper;
import com.xianzhi.tool.db.AchmentDetailHolder;
import com.xianzhi.tool.db.ContactListStaticHelper;
import com.xianzhi.tool.db.ContactListStaticHolder;
import com.xianzhi.tool.db.mailContentHelper;
import com.xianzhi.tool.db.mailContentHolder;
import com.xianzhi.tool.db.sendMailHolder;
public class HttpJsonTool {

	public enum LoginInfo {
		correct, webfaild, wronginput, correctnoname
	};
//	public static final String ServerUrl = "http://10.10.1.45:8080";
//	public static final String ServerUrl = "http://211.137.14.52:8080";
	public static final String ServerUrl = "https://eaccess.syrailway.cn:8443/mapping/sjznbg";
	private static HttpJsonTool httpjsontool = null;
	public static String username = "";
	public static boolean state403 = false;
	
	public static synchronized HttpJsonTool getInstance() {
		if (httpjsontool == null) {
			httpjsontool = new HttpJsonTool();
		}
		return httpjsontool;
	}

//	private String http = "https://eaccess.syrailway.cn:8443/mapping/sjznbg/1.jsp";
	private static CookieStore cookieInfo=null;
	public void getCookieInfo(){
		Thread th=new Thread(){
			public void run(){
				HttpClient httpClient = HttpsClient.getInstance().getHttpsClient();
				HttpGet httpRequest = new HttpGet(ServerUrl+"/1.jsp");
				try {
					httpClient.execute(httpRequest);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cookieInfo=((AbstractHttpClient) httpClient).getCookieStore();
			}
		};
		th.start();
}
	// 403·µ»ØµÇÂ½
	public synchronized LoginInfo startLoginCheck(String loginname,
			String password) {
		try {
			HttpClient client = HttpsClient.getInstance().getHttpsClient();
			if(cookieInfo!=null){
				((AbstractHttpClient) client).setCookieStore(cookieInfo);
			}
			StringBuilder builder = new StringBuilder();
			HttpPost httpRequest = new HttpPost(ServerUrl + "/userLogin.json");
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userName", loginname));
			params.add(new BasicNameValuePair("password", password));
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(httpRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
			
			Log.i("json", builder.toString());
			JSONObject jsonObject = new JSONObject(builder.toString());
			appPDF.token = jsonObject.getString("token");
			String name = jsonObject.getString("userName");
			username = name;
			if (name.equals("null")) {
				return LoginInfo.correctnoname;
			}
//			Log.i("refreshReadFlag", appPDF.token);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return LoginInfo.webfaild;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return LoginInfo.webfaild;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return LoginInfo.webfaild;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return LoginInfo.wronginput;
		}
		return LoginInfo.correct;
	}

	public synchronized int checkMailforNotif(Context context) {
		int num = -1;
		try {
			HttpClient client = HttpsClient.getInstance().getHttpsClient();
			if(cookieInfo!=null){
				((AbstractHttpClient) client).setCookieStore(cookieInfo);
			}
			StringBuilder builder = new StringBuilder();
			HttpPost httpRequest = new HttpPost(ServerUrl
					+ "/message/inbox.json?token="+appPDF.token);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("token", appPDF.token));
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(httpRequest);
			int statusCode=response.getStatusLine().getStatusCode();
			if(statusCode==403){
				state403=true;
				httpjsontool=null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
			new JSONObject(builder.toString());
			mailContentHelper helper = new mailContentHelper(context);
			helper.selectIdforNoti(username);
			helper.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return num;
	}
	private static final int PAGE_SIZE=15;
	public synchronized void checkOutMoreMail(Context context,int min) {
		try {
			HttpClient client = HttpsClient.getInstance().getHttpsClient();
			if(cookieInfo!=null){
				((AbstractHttpClient) client).setCookieStore(cookieInfo);
			}
			StringBuilder builder = new StringBuilder();
			HttpPost httpRequest = new HttpPost(ServerUrl
					+ "/message/inbox.json?token="+appPDF.token);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("token", appPDF.token));
			params.add(new BasicNameValuePair("minId", ""+min));
			params.add(new BasicNameValuePair("pageSize", ""+PAGE_SIZE));
//			Log.i("token", appPDF.token);
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(httpRequest);
			int statusCode=response.getStatusLine().getStatusCode();
			if(statusCode==403){
				state403=true;
				httpjsontool=null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
			JSONObject jsonObject = new JSONObject(builder.toString());
			inputMailCach(jsonObject, context);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void checkOutMail(Context context) {
		try {
			HttpClient client = HttpsClient.getInstance().getHttpsClient();
			if(cookieInfo!=null){
				((AbstractHttpClient) client).setCookieStore(cookieInfo);
			}
			StringBuilder builder = new StringBuilder();
			HttpPost httpRequest = new HttpPost(ServerUrl
					+ "/message/inbox.json?token="+appPDF.token);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pageSize", ""+PAGE_SIZE));
//			params.add(new BasicNameValuePair("token", appPDF.token));
//			Log.i("token", appPDF.token);
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(httpRequest);
			int statusCode=response.getStatusLine().getStatusCode();
			if(statusCode==403){
				Log.i("checkOutMail", "403");
				state403=true;
				httpjsontool=null;
				return;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
//			Log.i("builder", "builder="+builder.toString());
			JSONObject jsonObject = new JSONObject(builder.toString());
			inputMailCach(jsonObject, context);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void checkOutContact(Context context) {
		try {
			HttpClient client = HttpsClient.getInstance().getHttpsClient();
			if(cookieInfo!=null){
				((AbstractHttpClient) client).setCookieStore(cookieInfo);
			}
			StringBuilder builder = new StringBuilder();
			HttpPost httpRequest = new HttpPost(ServerUrl
					+ "/message/getContacts.json?token="+appPDF.token);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("token", appPDF.token));
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(httpRequest);
			int statusCode=response.getStatusLine().getStatusCode();
			if(statusCode==403){
				state403=true;
				httpjsontool=null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
//			Log.i("checkOutContact", builder.toString());
			JSONObject jsonObject = new JSONObject(builder.toString());
			autoinsertContact(jsonObject,context);
//			inputContactCach(jsonObject, context);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void refreshReadFlag(int id) {
		try {
			HttpClient client = HttpsClient.getInstance().getHttpsClient();
			if(cookieInfo!=null){
				((AbstractHttpClient) client).setCookieStore(cookieInfo);
			}
			StringBuilder builder = new StringBuilder();
			HttpPost httpRequest = new HttpPost(ServerUrl
					+ "/message/inbox/info.json?token="+appPDF.token+"&id="+String.valueOf(id));
			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("id", String.valueOf(id)));
//			params.add(new BasicNameValuePair("token", appPDF.token));
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(httpRequest);
			int statusCode=response.getStatusLine().getStatusCode();
			if(statusCode==403){
				state403=true;
				httpjsontool=null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
			Log.i("refreshReadFlag", builder.toString());
			new JSONObject(builder.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void deleteMail(int id) {
		try {
			HttpClient client = HttpsClient.getInstance().getHttpsClient();
			if(cookieInfo!=null){
				((AbstractHttpClient) client).setCookieStore(cookieInfo);
			}
			StringBuilder builder = new StringBuilder();
			HttpPost httpRequest = new HttpPost(ServerUrl
					+ "/message/inbox/delete.json?token="+appPDF.token);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", String.valueOf(id)));
//			params.add(new BasicNameValuePair("token", appPDF.token));
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(httpRequest);
			int statusCode=response.getStatusLine().getStatusCode();
			if(statusCode==403){
				state403=true;
				httpjsontool=null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
			new JSONObject(builder.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized Boolean refreshName(String name) {
		try {
			HttpClient client = HttpsClient.getInstance().getHttpsClient();
			if(cookieInfo!=null){
				((AbstractHttpClient) client).setCookieStore(cookieInfo);
			}
			StringBuilder builder = new StringBuilder();
			HttpPost httpRequest = new HttpPost(ServerUrl
					+ "/user/updateInfo.json?token="+appPDF.token/*+"&name="+URLEncoder.encode(name, "UTF-8")*/);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", name));
//			params.add(new BasicNameValuePair("token", appPDF.token));
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(httpRequest);
			int statusCode=response.getStatusLine().getStatusCode();
			if(statusCode==403){
				state403=true;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
//			Log.i("refreshName", builder.toString());
			new JSONObject(builder.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}


	public synchronized String sendMail(sendMailHolder holder) {
		String info = "";
		JSONObject sendMailJson = null;
		try {
			HttpClient client = HttpsClient.getInstance().getHttpsClient();
			if(cookieInfo!=null){
				((AbstractHttpClient) client).setCookieStore(cookieInfo);
			}
			client.getParams().setParameter(
					CoreProtocolPNames.HTTP_CONTENT_CHARSET,Charset.forName("UTF-8"));
			StringBuilder builder = new StringBuilder();
			HttpPost httpRequest = new HttpPost(ServerUrl
					+ "/message/sendEmail.json?token="+appPDF.token/*+"&to="+URLEncoder.encode(holder.getEmailTo(), "UTF-8")
					+"&cc="+URLEncoder.encode(holder.getCc(), "UTF-8")
					+"&bcc="+URLEncoder.encode(holder.getBcc(), "UTF-8")
					+"&subject="+URLEncoder.encode(holder.getSubject(), "UTF-8")
					+"&content="+URLEncoder.encode(holder.getContent(), "UTF-8")*/);
			String achment = holder.getAchment();
			String[] files=achment.split(" ");
			MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
		               null, Charset.forName("UTF-8"));
			for(String file:files){
				File f_file=new File(file);
				if(f_file.exists()){
					ContentBody cbFile = new FileBody(f_file,"","UTF-8");
					mpEntity.addPart("attachments", cbFile);
				}
			}
			ContentBody cbEmailTo = new StringBody(holder.getEmailTo(),Charset.forName("UTF-8"));
			ContentBody cbcc = new StringBody(holder.getCc(),Charset.forName("UTF-8"));
			ContentBody cbbcc = new StringBody(holder.getBcc(),Charset.forName("UTF-8"));
			ContentBody cbsubject = new StringBody(holder.getSubject(),Charset.forName("UTF-8"));
			ContentBody cbcontent = new StringBody(holder.getContent(),Charset.forName("UTF-8"));
			
			mpEntity.addPart("to", cbEmailTo);
			mpEntity.addPart("cc", cbcc);
			mpEntity.addPart("bcc", cbbcc);
			mpEntity.addPart("subject", cbsubject);
			mpEntity.addPart("content", cbcontent);
			
			httpRequest.setEntity(mpEntity);
			HttpResponse response = client.execute(httpRequest);
			int statusCode=response.getStatusLine().getStatusCode();
			if(statusCode==403){
				state403=true;
				httpjsontool=null;
			}
			Log.i("sendMail", statusCode+"");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
			Log.i("sendMail", builder.toString());
			sendMailJson = new JSONObject(builder.toString());
			info = sendMailJson.optString("info");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return info;
	}
	/*
	private void inputContactCach(JSONObject jsonObject, Context context)
			throws JSONException {
		JSONArray jsonlist = jsonObject.getJSONArray("contacts");
		for (int i = 0; i < jsonlist.length(); i++) {
			JSONObject json = (JSONObject) jsonlist.get(i);
			int id = json.optInt("id");
			String name = json.optString("name");
			String address = json.optString("address");
			ContactListHolder holder=new ContactListHolder(id, name, address);
			ContactListHelper helper=new ContactListHelper(context);
			helper.insert(holder);
			helper.close();
		}
	}*/
	@SuppressLint("SimpleDateFormat")
	private void inputMailCach(JSONObject jsonObject, Context context)
			throws JSONException {
		JSONArray jsonlist = jsonObject.getJSONArray("inboxes");
		Log.i("jsonlist.length()", ""+jsonlist.length());
		for (int i = 0; i < jsonlist.length(); i++) {
			JSONObject json = (JSONObject) jsonlist.get(i);
			int id = json.optInt("id");
			String userName = json.optString("userName");
			username=userName;
			int uid = json.optInt("uid");
			String subject = json.optString("subject");
			int index=subject.indexOf("=?");
			if(index!=-1){
				subject=subject.substring(0, index);
			}
			String content = json.optString("content");
			String emailTo = json.optString("emailTo");
			String cc = json.optString("cc");
			String bcc = json.optString("bcc");
			int readFlag = json.optInt("readFlag");
			String fromUser = json.optString("fromUser");
			Long l_sentDate = json.optLong("sentDate");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sentDate = sdf.format(l_sentDate);
			int hasAttachment = json.optInt("hasAttachment");
			int achmentcount;
			if (hasAttachment == 1) {
				JSONArray attachents = json.getJSONArray("attachents");
				achmentcount = attachents.length();
				AchmentDetailHelper achHelper = new AchmentDetailHelper(context);
				for (int j = 0; j < attachents.length(); j++) {
					JSONObject attachent = (JSONObject) attachents.get(j);
					int att_id = attachent.optInt("id");
					String att_name = attachent.optString("name");
					String att_path = attachent.optString("path");
					int att_inboxID = attachent.optInt("inboxId");
					Long att_size = attachent.optLong("size");
					AchmentDetailHolder att_holder = new AchmentDetailHolder(
							att_id, att_name, att_inboxID, att_path, att_size,
							-1,"");
					achHelper.insert(att_holder);
				}
				achHelper.close();
			} else {
				achmentcount = 0;
			}
			mailContentHolder mailcontent = new mailContentHolder(id, userName,
					uid, subject, content, emailTo, cc, bcc, readFlag,
					fromUser, sentDate, hasAttachment, achmentcount);
			mailContentHelper mailHelper = new mailContentHelper(context);
			mailHelper.insert(mailcontent);
			mailHelper.close();
		}
	}
	@SuppressLint("UseSparseArrays")
	private void autoinsertContact(JSONObject jsonObject,Context context) throws JSONException {
		ContactListStaticHelper helper = new ContactListStaticHelper(
				context);
		helper.clear();
		ArrayList<String>employers=new ArrayList<String>();
		JSONArray jsonlist = jsonObject.getJSONArray("contacts");
		for (int i = 0; i < jsonlist.length(); i++) {
			JSONObject json = (JSONObject) jsonlist.get(i);
			int departmentId = json.optInt("departmentId");
			String name = json.optString("name");
			String address = json.optString("address");
			employers.add(name+" "+address+" "+departmentId);
		}
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
		ArrayList<String>departments=new ArrayList<String>();
		ArrayList<String>provinces = new ArrayList<String>();
		JSONArray delist = jsonObject.getJSONArray("departments");
		for (int i = 0; i < delist.length(); i++) {
			JSONObject json = (JSONObject) delist.get(i);
			int id = json.optInt("id");
			String name = json.optString("name");
			int parentId = json.optInt("parentId");
			if(parentId==0){
				provinces.add(id+" "+name+" "+parentId);
				continue;
			}
			departments.add(id+" "+name+" "+parentId);
		}
		Map<Integer, String> depart_idmap = new HashMap<Integer, String>();
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
		
		for (String province : provinces) {
			String[] provinceDetail = province.split(" ");
			int id = Integer.parseInt(provinceDetail[0]);
			helper.insert(new ContactListStaticHolder(id,
					provinceDetail[1], "",
					ContactListStaticHelper.provinceFLAG, depart_idmap
							.get(id), -1));
		}
		helper.close();
	}
}
