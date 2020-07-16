package com.itstyle.modules.wechatpay.util;

import com.itstyle.common.constants.Constants;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/16 9:50
 * @Decription 退款认证
 */
public class ClientCustomSSL {

    public static String doRefund(String url, String data) throws Exception {
        /*
            注意PKCS12证书，是从微信商户平台 -->> 账户设置 -->> API安全 下载的
         */
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        File certFile = ResourceUtils.getFile("classpath:cert"
                + Constants.SF_FILE_SEPARATOR+ConfigUtil.CERT_PATH);
        FileInputStream inputStream = new FileInputStream(certFile);
        try{
            keyStore.load(inputStream,ConfigUtil.MCH_ID.toCharArray());
        }finally {
            inputStream.close();
        }
        SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(
                keyStore,ConfigUtil.MCH_ID.toCharArray()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext,new String[] {"TLSv1"},null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER
        );
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(data,"UTF-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                //String jsonStr = EntityUtils.toString(response.getEntity(),"UTF-8");
                String jsonStr = EntityUtils.toString(entity,"UTF-8");
                EntityUtils.consume(entity);
                return jsonStr;
            }finally {
                response.close();
            }
        }finally {
            httpClient.close();
        }
    }
}
