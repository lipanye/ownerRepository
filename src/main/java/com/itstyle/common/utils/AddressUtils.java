package com.itstyle.common.utils;



import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/03/23 16:08
 * @Decription 根据IP地址获取详细的地域信息
 */

public class AddressUtils {
    private static final Logger logger = LoggerFactory.getLogger(AddressUtils.class);

    /**
     *
     * @param ip
     * @return
     */
    public static String getAddress(String ip){
        String urlStr = "http://ip.taobao.com/service/getIpInfo.php";
        String returnStr =getResult(urlStr,ip);
        if(returnStr !=null){
            //处理返回的省市区信息
            String[] temp = returnStr.split(",");
            if(temp.length<3){
                //无效ip，局域网测试
                return "0";
            }
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>返回的省市区信息>>>temp,{}",temp);
            String region =(temp[5].split(":"))[1].replaceAll("\"","");
            //省份
            region = decodeUnicode(region);
            String country = "";
            String area = "";
            String city = "";
            String county = "";
            String isp = "";
            for(int i=0;i<temp.length;i++){
                switch (i){
                    case 1:
                        country = (temp[i].split(":"))[2].replaceAll("\"","");
                        country =decodeUnicode(country);// 国家
                        break;
                    case 3:
                        area = (temp[i].split(":"))[1].replaceAll("\"", "");
                        area = decodeUnicode(area);// 地区
                        break;
                    case 5:
                        region = (temp[i].split(":"))[1].replaceAll("\"", "");
                        region = decodeUnicode(region);// 省份
                        break;
                    case 7:
                        city = (temp[i].split(":"))[1].replaceAll("\"", "");
                        city = decodeUnicode(city);// 市区
                        break;
                    case 9:
                        county = (temp[i].split(":"))[1].replaceAll("\"", "");
                        county = decodeUnicode(county);// 地区
                        break;
                    case 11:
                        isp = (temp[i].split(":"))[1].replaceAll("\"", "");
                        isp = decodeUnicode(isp); // ISP公司
                        break;
                }
            }
            String address =region+city;
            if(StringUtils.isBlank(address)){
                address="地球村";
            }
            return address;
        }
        return null;
    }

    /**
     * unicode 转中文
     * @param region
     * @return
     */
    private static String decodeUnicode(String region) {
        char aChar;
        int len = region.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = region.charAt(x++);
            if (aChar == '\\') {
                aChar = region.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = region.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed      encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

    /**
     *
     * @param urlStr 请求的地址
     * @param ip
     * @return
     */
    private static String getResult(String urlStr, String ip) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();//重新链接实例
            //设置链接超时时间，单位是毫秒，由2s改为5s
            connection.setConnectTimeout(5000);
            //设置读取数据超时时间，单位是毫秒
            connection.setReadTimeout(5000);
            //是否打开输出流 true|false
            connection.setDoOutput(true);
            //是否打开输入流 true|false
            connection.setDoInput(true);
            //提交方法 POST|GET
            connection.setRequestMethod("POST");
            //是否缓存 true|false
            connection.setUseCaches(false);
            //打开链接端口
            connection.connect();
            //打开输出流对端服务器写数据
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            //写数据
            out.writeBytes("ip="+ip);
            //刷新
            out.flush();
            //关闭输出流
            out.close();
            //往对端写完数据对端服务器返回数据
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(),"utf-8"));
            //以BufferedReader流来读取
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while((line = reader.readLine())!=null){
                buffer.append(line);
            }
            reader.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(connection!=null){
                //关闭链接
                connection.disconnect();
            }
        }
        return null;
    }

    /**
     * 获取ip地址
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if(!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)){
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if(!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)){
            int index = ip.indexOf(",");
            logger.info("Request Header为 X-Forwarded-For的时候，ip的值是{}",ip);
            if(index != -1){
                return ip.substring(0,index);
            }else {
                return ip;
            }
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
