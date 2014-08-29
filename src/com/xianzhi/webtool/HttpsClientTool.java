package com.xianzhi.webtool;

import java.security.KeyStore;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class HttpsClientTool {
	public static HttpClient getHttpsClient() {  
        try {  
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());  
            trustStore.load(null, null);  
            
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);  
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  
    
            HttpParams params = new BasicHttpParams();  
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);  
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);  
    
            SchemeRegistry registry = new SchemeRegistry();  
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));  
            registry.register(new Scheme("https", sf, 443));  
    
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);  
    
            return new DefaultHttpClient(ccm, params);  
        } catch (Exception e) {  
            return new DefaultHttpClient();  
        }  
    }  
	/*String requestHTTPSPage(String mUrl) {
		InputStream ins = null;
		String result = "";
		try {
			ins = context.getAssets().open("app_pay.cer"); //下载的证书放到项目中的assets目录中
			CertificateFactory cerFactory = CertificateFactory
					.getInstance("X.509");
			Certificate cer = cerFactory.generateCertificate(ins);
			KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
			keyStore.load(null, null);
			keyStore.setCertificateEntry("trust", cer);

			SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
			Scheme sch = new Scheme("https", socketFactory, 443);
			HttpClient mHttpClient = new DefaultHttpClient();
			mHttpClient.getConnectionManager().getSchemeRegistry()
					.register(sch);

			BufferedReader reader = null;
			try {
				Log.d(TAG, "executeGet is in,murl:" + mUrl);
				HttpGet request = new HttpGet();
				request.setURI(new URI(mUrl));
				HttpResponse response = mHttpClient.execute(request);
				if (response.getStatusLine().getStatusCode() != 200) {
					request.abort();
					return result;
				}

				reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				StringBuffer buffer = new StringBuffer();
				String line = null;
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}
				result = buffer.toString();
				Log.d(TAG, "mUrl=" + mUrl + "\nresult = " + result);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (ins != null)
					ins.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}*/
}
