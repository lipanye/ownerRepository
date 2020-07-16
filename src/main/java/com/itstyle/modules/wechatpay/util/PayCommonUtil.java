package com.itstyle.modules.wechatpay.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/02 9:55
 * @Decription
 */
public class PayCommonUtil {
    private static final Logger logger = LoggerFactory.getLogger(PayCommonUtil.class);
    /**
     * 获取当前时间 -> yyyyMMddHHmmss
     * @return
     */
    public static String getCurrTime() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(date);
    }

    /**
     * 取出一个指定长度大小的随机正整数
     * @param length
     * @return
     */
    public static int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if(random<0.1){
            random = random+0.1;
        }
        for(int i=0;i<length;i++){
            num = num*10;
        }
        return (int) (random*num);
    }


    public static void main(String[] args) {
        String currTime = getCurrTime();
        String strTime = currTime.substring(8,currTime.length());
        System.out.println(strTime);
        String strRandom = buildRandom(4)+"";
        System.out.println(strRandom);
        String noceStr = strTime+strRandom;
        System.out.println(noceStr);
    }

    /**
     * sign签名
     * @param characterEncoding
     * @param packageParams
     * @param apiKey
     * @return
     */
    public static String createSign(String characterEncoding, SortedMap<Object, Object> packageParams, String apiKey) {
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if(null != value && !"".equals(value) && !"sign".equals(key) && !"key".equals(key)){
                sb.append(key+"="+value+"&");
            }
        }
        sb.append("key="+apiKey);
        logger.info("签名前的串，{}",sb.toString());
        String sign = MD5Util.MD5Encode(sb.toString(),characterEncoding).toUpperCase();
        logger.info("签名后并转成大写的串,{}",sign);
        return sign;
    }

    /**
     * 将请求参数转换为xml格式的string
     * @param packageParams
     * @return
     */
    public static String getRequestXml(SortedMap<Object, Object> packageParams) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<xml>");
        Set es = packageParams.entrySet();
        Iterator integer = es.iterator();
        while (integer.hasNext()){
            Map.Entry entry = (Map.Entry) integer.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if("attach".equalsIgnoreCase(key) || "body".equalsIgnoreCase(key) || "sign".equalsIgnoreCase(key)){
                buffer.append("<"+key+">"+"<![CDATA["+value+"]]></"+key+">");
            }else{
                buffer.append("<"+key+">"+value+"</"+key+">");
            }
        }
        buffer.append("</xml>");
        logger.info("将请求参数转换为xml格式的string,{}",buffer.toString());
        return buffer.toString();
    }

    public static boolean isTenpaySign(String characterEncoding, SortedMap<Object, Object> packageParams, String API_KEY) {
        StringBuffer stringBuffer = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator iterator = es.iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if(!"sign".equals(value) && null != value && !"".equals(value)){
                stringBuffer.append(key+"="+value+"&");
            }
        }
        stringBuffer.append("key="+API_KEY);
        //算出摘要
        String mySign = MD5Util.MD5Encode(stringBuffer.toString(),characterEncoding).toUpperCase();
        String tenpaySign = ((String) packageParams.get("sign")).toUpperCase();
        return mySign.equals(tenpaySign);
    }
}
