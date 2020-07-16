package com.itstyle.modules.wechatpay.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/07 10:11
 * @Decription MD5 加密
 */
public class MD5Util {

    private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);
    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public static String MD5Encode(String origin, String characterEncoding) {
        String resultString = "";
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if(characterEncoding == null || "".equals(characterEncoding)){
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            }else{
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(characterEncoding)));
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MD5加密失败，{}",e);
        }
        return resultString;
    }

    private static String byteArrayToHexString(byte[] digest) {
        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<digest.length;i++){
            buffer.append(byteToHexString(digest[i]));
        }
        return buffer.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if(n<0){
            n+=256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
}
