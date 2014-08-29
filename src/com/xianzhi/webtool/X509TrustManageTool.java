package com.xianzhi.webtool;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class X509TrustManageTool implements X509TrustManager {  
    public X509Certificate[] getAcceptedIssuers() {  
        // return null;  
        return new X509Certificate[] {};  
    }  
    @Override  
    public void checkClientTrusted(X509Certificate[] chain, String authType)  
            throws CertificateException {  
        // TODO Auto-generated method stub  
  
    }  
    @Override  
    public void checkServerTrusted(X509Certificate[] chain, String authType)  
            throws CertificateException {  
        // TODO Auto-generated method stub  
    }  
};  