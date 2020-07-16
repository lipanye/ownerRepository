package com.itstyle.modules.unionpay.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/24 17:25
 * @Decription
 */
public class BaseHttpSSLSocketFactory extends SSLSocketFactory {
    private static final Logger logger = LoggerFactory.getLogger(BaseHttpSSLSocketFactory.class);
    private SSLContext getSSLContext(){
        return createEasySSLContext();
    }

    private SSLContext createEasySSLContext() {
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null,new TrustManager[]{MyX509TrustManager.manager},null);
            return context;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    public static class MyX509TrustManager implements X509TrustManager{
        static MyX509TrustManager manager = new MyX509TrustManager();

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }


    @Override
    public String[] getDefaultCipherSuites() {
        return new String[0];
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return new String[0];
    }

    @Override
    public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
        return getSSLContext().getSocketFactory().createSocket(socket,s,i,b);
    }

    @Override
    public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(s,i);
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(s,i,inetAddress,i1);
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        return getSSLContext().getSocketFactory().createSocket(inetAddress,i);
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
        return getSSLContext().getSocketFactory().createSocket(inetAddress,i,inetAddress1,i1);
    }


    /**
     *  解决由于服务器证书问题导致HTTPS无法访问的情况 PS：HTTPS hostname wrong: should be <localhost></>
     */
    public static class TrustAnyHostnameVeriiery implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }
}
