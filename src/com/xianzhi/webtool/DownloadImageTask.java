package com.xianzhi.webtool;

import java.io.FilterInputStream;
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.xianzhi.callbacklistener.downloadBitmapListener;
import com.xianzhi.office.appPDF;

/*****************************************************************************************************************************************
 * 异步线程下载类 参数说明： 第一个参数：String 传入的是需要下载的地址 第二个参数：Integer 是进度条的进度 第三个参数：String
 * 下载后的结果，如果您下载的是一张图片，可以将它改成BitMap
 *****************************************************************************************************************************************/
public class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {

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
	} */

	static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
	Bitmap bitmap=null;
	@Override
	protected Bitmap doInBackground(String... params) {
//		HttpURLConnection connection = null;
		try {
//			URL url=new URL(HttpJsonTool.ServerUrl+ "/signatureInfo/download.json");
//			if (url.getProtocol().toLowerCase().equals("https")) {  
//                connection = (HttpsURLConnection) url.openConnection();  
//                ((HttpsURLConnection) connection).setHostnameVerifier(DO_NOT_VERIFY);// 不进行主机名确认  
//                ((HttpsURLConnection) connection).setSSLSocketFactory(HttpsClient.getInstance().getSSLSocketFactory());
//  
//            } else {  
//            	connection = (HttpURLConnection) url.openConnection();  
//            }  
//			connection.setConnectTimeout(10 * 1000);
////			 connection.connect();
//			connection.setDoOutput(true);
//			connection.setDoInput(true);
//			connection.setRequestMethod("POST");
//			connection.setUseCaches(false);
			String content = "signatureNumber=" + URLEncoder.encode(params[0], "UTF-8");
			content += "&token=" + URLEncoder.encode(appPDF.token, "UTF-8");
			
//			connection.getOutputStream().write(content.getBytes());
//			connection.getOutputStream().flush();
//			connection.getOutputStream().close();
			HttpClient client = HttpsClient.getInstance().getHttpsClient();
			client.getParams().setParameter(
					CoreProtocolPNames.HTTP_CONTENT_CHARSET,Charset.forName("UTF-8"));
			HttpGet httpRequest = new HttpGet(HttpJsonTool.ServerUrl+ "/signatureInfo/download.json?"+content);
			HttpResponse response;
            
	        response = client.execute(httpRequest);
	        HttpEntity entity = response.getEntity();
	        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream inputStream = entity.getContent();
				bitmap=BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			if (connection != null) {
//				connection.disconnect();
//			}
		}
		return bitmap;
	}
	static class FlushedInputStream extends FilterInputStream {
	    public FlushedInputStream(InputStream inputStream) {
	        super(inputStream);
	    }

	    @Override
	    public long skip(long n) throws IOException {
	        long totalBytesSkipped = 0L;
	        while (totalBytesSkipped < n) {
	            long bytesSkipped = in.skip(n - totalBytesSkipped);
	            if (bytesSkipped == 0L) {
	                  int byt = read();
	                  if (byt < 0) {
	                      break;  // we reached EOF
	                  } else {
	                      bytesSkipped = 1; // we read one byte
	                  }
	           }
	            totalBytesSkipped += bytesSkipped;
	        }
	        return totalBytesSkipped;
	    }
	}
	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}
	private downloadBitmapListener listener;
	public void setdownloadCallbackListener(downloadBitmapListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		Log.i("123","123");
		listener.onComplete(result);
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
