package com.itstyle.modules.unionpay.util;

import com.sun.deploy.security.CertUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/20 9:20
 * @Decription
 */
public class SDKUtil {

    private static final Logger logger = LoggerFactory.getLogger(SDKUtil.class);
    /**
     * 过滤请求报文中的空字符串或者空格
     * @param requestData
     * @return
     */
    public static Map<String, String> filterBlank(Map<String, String> requestData) {
        logger.info("打印请求报文域 : ");
        Map<String,String> submitData = new HashMap<>();
        Set<String> keySet = requestData.keySet();
        for(String key : keySet){
            String value = requestData.get(key);
            if(StringUtils.isNotBlank(value)){
                // 对value值进行去除前后空处理
                submitData.put(key,value.trim());
                logger.info(key+" -- >"+value);
            }
        }
        return submitData;
    }

    /**
     * 生成签名值(SHA1摘要算法)
     * @param data 待签名数据map键值对形式
     * @param characterEncoding 编码
     * @return 签名是否成功
     */
    public static boolean sign(Map<String, String> data,String characterEncoding) {
        if(isEmpty(characterEncoding)){
            characterEncoding = "utf-8";
        }
        //设置签名证书序列号
        data.put(SDKConstants.param_certId, CertUtil.getSignCertId());
        return false;
    }

    /**
     * 判断字符串是否为null或空
     * @param characterEncoding 待判定的字符串数据
     * @return 判断结果 true-是|false-否
     */
    public static boolean isEmpty(String characterEncoding) {
        return null == characterEncoding || "".equals(characterEncoding.trim());
    }

    /**
     * 将Map中的数据转换成按照key的ascii码排序后的key1=value1&key2=value2的形式，不包含签名域signature
     * @param data 待拼接的map数据
     * @return 拼接好后的字符串
     */
    public static String coverMap2String(Map<String, String> data) {
        TreeMap<String,String> treeMap = new TreeMap<>();
        Iterator<Map.Entry<String,String>> it = data.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String,String> entry = it.next();
            if(SDKConstants.param_signature.equals(entry.getKey().trim())){
                continue;
            }
            treeMap.put(entry.getKey(),entry.getValue());
        }
        it = treeMap.entrySet().iterator();
        StringBuffer sb = new StringBuffer();
        while (it.hasNext()){
            Map.Entry<String,String> en = it.next();
            sb.append(en.getKey()+SDKConstants.EQUAL+en.getValue()+SDKConstants.AMPERSAND);
        }
        return sb.substring(0,sb.length()-1);
    }

    /**
     * 兼容老方法 将key=value&key=value形式的字符串转为Map对象
     * @param result
     * @return
     */
    public static Map<String,String> converResultString2Map(String result){
        return convertResultStringToMap(result);
    }
    /**
     *  将key=value&key=value形式的字符串转为Map对象
     * @param result
     * @return
     */
    public static Map<String, String> convertResultStringToMap(String result) {
        Map<String,String> map = null;
        if(StringUtils.isNotBlank(result)){
            if(result.startsWith("{") && result.endsWith("}")){
                System.out.println(result.length());
                result = result.substring(1,result.length()-1);
            }
            map = parseQString(result);
        }
        return map;
    }

    /**
     * 解析应答字符串，生成应答要素
     * @param result 需要解析的字符串
     * @return 解析的结果map
     */
    private static Map<String, String> parseQString(String result) {
        Map<String,String> map = new HashMap<>();
        int len = result.length();
        StringBuilder temp = new StringBuilder();
        char curChar;
        String key = null;
        boolean isKey = true;
        boolean isOpen = false; //值里有嵌套
        char openName = 0;
        if(len>0){
            for(int i=0;i<len;i++){ //遍历整个待解析的字符串
                curChar = result.charAt(i);//取当前字符
                if(isKey){ //如果当前生成的是key
                    if(curChar == '='){ //如果读到=分隔符
                        key = temp.toString();
                        temp.setLength(0);
                        isKey=false;
                    }else{
                        temp.append(curChar);
                    }
                }else { //如果当前生成的是value
                    if(isOpen){
                        if(curChar == openName){
                            isOpen = false;
                        }
                    }else{ //如果没开启嵌套
                        if(curChar == '{'){ //如果碰到，就开启嵌套
                            isOpen = true;
                            openName = '}';
                        }
                        if(curChar == '['){
                            isOpen = true;
                            openName=']';
                        }
                    }
                }
                if(curChar == '&' && !isOpen){ //如果读到&分隔符，同时这个分隔符不是值域，这是j将map里添加
                    putKeyValueToMap(temp,isKey,key,map);
                    temp.setLength(0);
                    isKey = true;
                }else{
                    temp.append(curChar);
                }
            }
            putKeyValueToMap(temp,isKey,key,map);
        }
        return map;
    }

    private static void putKeyValueToMap(StringBuilder temp, boolean isKey, String key, Map<String, String> map) {
    }

}
