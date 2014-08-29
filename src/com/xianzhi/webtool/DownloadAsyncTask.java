package com.xianzhi.webtool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.CoreProtocolPNames;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.xianzhi.callbacklistener.downloadCallbackListener;
import com.xianzhi.office.appPDF;

/*****************************************************************************************************************************************
 * 异步线程下载类 参数说明： 第一个参数：String 传入的是需要下载的地址 第二个参数：Integer 是进度条的进度 第三个参数：String
 * 下载后的结果，如果您下载的是一张图片，可以将它改成BitMap
 *****************************************************************************************************************************************/
public class DownloadAsyncTask extends AsyncTask<String, Integer, Boolean> {

	private final ProgressDialog bar;
	private int count = 0;
	private File downloadFile = null;
	private int id = -1;
	private downloadCallbackListener listener;

	public DownloadAsyncTask(ProgressDialog bar) {
		super();
		this.bar = bar;
		bar.setProgress(count);
	}

	public String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	static TrustManager[] xtmArray = new X509TrustManageTool[] { new X509TrustManageTool() };

	/**
	 * 信任所有主机-对于任何证书都不做检查
	 
	private static void trustAllHosts() {
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, xtmArray, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	@Override
	protected Boolean doInBackground(String... params) {
//		HttpURLConnection connection = null;
		try {
//			URL url=new URL(HttpJsonTool.ServerUrl+ "/message/download.json");
//			if (url.getProtocol().toLowerCase().equals("https")) {  
//                connection = (HttpsURLConnection) url.openConnection();  
//                ((HttpsURLConnection) connection).setHostnameVerifier(DO_NOT_VERIFY);// 不进行主机名确认  
//                ((HttpsURLConnection) connection).setSSLSocketFactory(HttpsClient.getInstance().getSSLSocketFactory());
//  
//            } else {  
//            	connection = (HttpURLConnection) url.openConnection();  
//            } 
//			
//			connection.setConnectTimeout(10 * 1000);
//			// connection.connect();
//			connection.setDoOutput(true);
//			connection.setDoInput(true);
//			connection.setRequestMethod("POST");
//			connection.setUseCaches(false);
//
			String content = "path=" + URLEncoder.encode(params[0], "UTF-8");
			content += "&ext="
					+ URLEncoder.encode(getExtensionName(params[1]), "UTF-8");
			content += "&token=" + URLEncoder.encode(appPDF.token, "UTF-8");
//
//			connection.getOutputStream().write(content.getBytes());
//			connection.getOutputStream().flush();
//			connection.getOutputStream().close();
			HttpClient client = HttpsClient.getInstance().getHttpsClient();
			client.getParams().setParameter(
					CoreProtocolPNames.HTTP_CONTENT_CHARSET,Charset.forName("UTF-8"));
			HttpGet httpRequest = new HttpGet(HttpJsonTool.ServerUrl+ "/message/download.json?"+content);
			HttpResponse response;
            
	        response = client.execute(httpRequest);
	        HttpEntity entity = response.getEntity();
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				int size = (int) Long.parseLong(params[2]);
				bar.setMax(size);
				// Log.i("getContentLength", ""+connection.getContentLength());
				downloadFile = new File(params[3]);
				downloadFile.createNewFile();
//				InputStream inputStream = connection.getInputStream();
				InputStream inputStream = entity.getContent();
				FileOutputStream fileOutputStream = new FileOutputStream(
						downloadFile);
				byte[] buffer = new byte[10 * 1024];
				while (true) {
					int len = inputStream.read(buffer);
					publishProgress(len);
					if (len == -1) {
						break;
					}
					fileOutputStream.write(buffer, 0, len);
				}
				inputStream.close();
				fileOutputStream.close();
			}
			id = Integer.parseInt(params[4]);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			if (connection != null) {
//				connection.disconnect();
//			}
		}
		return true;
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		if (downloadFile != null && downloadFile.exists()) {
			downloadFile.delete();
		}
		bar.dismiss();
	}

	public void setdownloadCallbackListener(downloadCallbackListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		bar.dismiss();
		if (id != -1) {
			listener.onComplete(id);
		}
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		count += values[0];
		bar.setProgress(count);
		super.onProgressUpdate(values);
	}

}
