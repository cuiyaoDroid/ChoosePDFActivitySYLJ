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

import android.os.AsyncTask;

import com.xianzhi.callbacklistener.downloadAchmentListListener;
import com.xianzhi.office.appPDF;

/*****************************************************************************************************************************************
 * 异步线程下载类 参数说明： 第一个参数：String 传入的是需要下载的地址 第二个参数：Integer 是进度条的进度 第三个参数：String
 * 下载后的结果，如果您下载的是一张图片，可以将它改成BitMap
 *****************************************************************************************************************************************/
public class DownloadAchmentListTask extends AsyncTask<String, Integer, Boolean> {

	private File downloadFile = null;
	private String name = "";
	private downloadAchmentListListener listener;

	public DownloadAchmentListTask() {
		super();
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


	static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			String content = "path=" + URLEncoder.encode(params[0], "UTF-8");
			content += "&ext="
					+ URLEncoder.encode(getExtensionName(params[1]), "UTF-8");
			name=params[1];
			content += "&token=" + URLEncoder.encode(appPDF.token, "UTF-8");
			HttpClient client = HttpsClient.getInstance().getHttpsClient();
			client.getParams().setParameter(
					CoreProtocolPNames.HTTP_CONTENT_CHARSET,Charset.forName("UTF-8"));
			HttpGet httpRequest = new HttpGet(HttpJsonTool.ServerUrl+ "/message/download.json?"+content);
			HttpResponse response;
            
	        response = client.execute(httpRequest);
	        HttpEntity entity = response.getEntity();
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				downloadFile = new File(params[3]);
				downloadFile.createNewFile();
				InputStream inputStream = entity.getContent();
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[10 * 1024];
				while (true) {
					int len = inputStream.read(buffer);
					publishProgress(len);
					if (len == -1) {
						break;
					}
					arrayOutputStream.write(buffer, 0, len);
				}
				arrayOutputStream.close();
				inputStream.close();

				byte[] data = arrayOutputStream.toByteArray();
				FileOutputStream fileOutputStream = new FileOutputStream(
						downloadFile);
				fileOutputStream.write(data);
				fileOutputStream.close();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
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
	}

	public void setdownloadAchmentListener(downloadAchmentListListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (name.length() >0) {
			listener.onComplete(name);
		}
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

}
