package com.itstyle.modules.unionpay.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.util.Map;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/24 16:34
 * @Decription
 */
public class HttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    /**
     * 目标地址
     */
    private URL url;
    /**
     * 通信连接超时时间
     */
    private int connectionTimeOut;
    /**
     * 通信读超时时间
     */
    private int readTimeOut;
    /**
     * 通信结果
     */
    private String result;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public int getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    /**
     * 获取通信结果
     * @return
     */
    public String getResult() {
        return result;
    }

    /**
     * 设置通信结果
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }

    public HttpClient(){
    }

    public HttpClient(String url,int connectionTimeOut,int readTimeOut){
        try {
            this.url = new URL(url);
            this.connectionTimeOut = connectionTimeOut;
            this.readTimeOut = readTimeOut;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送信息到服务器
     * @param data
     * @param encoding
     * @return
     */
    public int send(Map<String, String> data, String encoding) throws Exception {
        try {
            HttpURLConnection httpURLConnection = createConnection(encoding);
            if(null==httpURLConnection){
                throw new Exception("创建连接失败");
            }
            String sendData = this.getRequestParamString(data,encoding);
            logger.info("请求报文：[{}]",sendData);
            this.requestServer(httpURLConnection,sendData,encoding);
            this.result = this.response(httpURLConnection,encoding);
            logger.info("同步返回报文：[{}]",result);
            return httpURLConnection.getResponseCode();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 显示Response消息
     * @param connection
     * @param encoding
     * @return
     */
    private String response(final HttpURLConnection connection, String encoding) throws IOException {
        InputStream in = null;
        StringBuilder sb = new StringBuilder(1024);
        BufferedReader br = null;
        try {
            if(200 == connection.getResponseCode()){
                in = connection.getInputStream();
            }else {
                in = connection.getErrorStream();
            }
            sb.append(new String(read(in),encoding));
            logger.info("HTTP Return Status-Code:[{}]",connection.getResponseCode());
            return sb.toString();
        } catch (Exception e) {
            throw e;
        }finally {
            if(null != br){
                br.close();
            }
            if(null != in){
                in.close();
            }
            if(null!=connection){
                connection.disconnect();
            }
        }
    }

    private byte[] read(InputStream in) throws IOException {
        byte[] buf = new byte[1024];
        int length = 0;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        while ((length=in.read(buf,0,buf.length))>0){
            bout.write(buf,0,length);
        }
        bout.flush();
        return bout.toByteArray();
    }

    /**
     *  http post发送消息
     * @param connection
     * @param message
     * @param encoder
     */
    private void requestServer(HttpURLConnection connection, String message, String encoder) throws Exception {
        PrintStream out = null;
        try {
            connection.connect();
            out = new PrintStream(connection.getOutputStream(),false,encoder);
            out.print(message);
            out.flush();
        } catch (Exception e) {
            throw e;
        }finally {
            if(null != out){
                out.close();
            }
        }

    }


    /**
     * 将map存储的对象，转换为key=value&key=value的形式
     * @param requestParam
     * @param coder
     * @return
     */
    private String getRequestParamString(Map<String, String> requestParam, String coder) {
        if(null == coder || "".equals(coder)){
            coder="UTF-8";
        }
        StringBuffer stringBuffer = new StringBuffer();
        String reqstr = "";
        if(null!=requestParam && 0!=requestParam.size()){
            for(Map.Entry<String,String> en : requestParam.entrySet()){
                try {
                    stringBuffer.append(en.getKey()+"="+(
                            null== en.getValue() || "".equals(en.getValue()) ? "" : URLEncoder
                                .encode(en.getValue(),coder)+"&"
                            ));
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getMessage(),e);
                    return "";
                }
            }
            reqstr = stringBuffer.substring(0,stringBuffer.length()-1);
        }
        logger.info("请求报文(已做过URLEncode编码)：[{}]",reqstr);
        return reqstr;
    }

    /**
     * 创建连接
     * @param encoding
     * @return
     */
    private HttpURLConnection createConnection(String encoding) throws ProtocolException {
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            e.printStackTrace();
            return null;
        }
        httpURLConnection.setConnectTimeout(connectionTimeOut); //连接超时时间
        httpURLConnection.setReadTimeout(readTimeOut); //读取结果超时时间
        httpURLConnection.setDoInput(true); //可读
        httpURLConnection.setDoOutput(true); //可写
        httpURLConnection.setUseCaches(false);//取消缓存
        httpURLConnection.setRequestProperty("Content-type","application/x-www-form-urlencoded;charset="+encoding);
        httpURLConnection.setRequestMethod("POST");
        if("https".equalsIgnoreCase(url.getProtocol())){
            HttpsURLConnection husn = (HttpsURLConnection) httpURLConnection;
            husn.setSSLSocketFactory(new BaseHttpSSLSocketFactory());
            //解决由于服务器证书问题导致HTTPS无法访问的情况
            husn.setHostnameVerifier(new BaseHttpSSLSocketFactory.TrustAnyHostnameVeriiery());
        }
        return httpURLConnection;
    }
}
