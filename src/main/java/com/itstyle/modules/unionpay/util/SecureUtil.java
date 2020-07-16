package com.itstyle.modules.unionpay.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/21 9:40
 * @Decription
 */
public class SecureUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecureUtil.class);

    /**
     * 算法常量：MD5
     */
    private static final String ALGORITHM_MD5 = "MD5";
    /**
     * 算法常量：SHA1
     */
    private static final String ALGORITHM_SHA1 = "SHA1";
    /**
     * 算法常量：SHA1withRSA
     */
    private static final String BC_PROVI_ALGORITHM_SHA1RSA = "SHA1withRSA";

    public static PublicKey getPublicKey(String modules, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modules);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA","BC");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1,b2);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
           throw new RuntimeException("getPublicKey error",e);
        }
    }

    /**
     * sha1计算后进行16进制转换
     * @param data
     * @param encoding
     * @return
     */
    public static byte[] sha1X16(String data, String encoding) {
        byte[] bytes =  sha1(data,encoding);
        StringBuffer sha1StrBuffer = new StringBuffer();
        for(int i=0;i<sha1StrBuffer.length();i++){
            if(Integer.toHexString(0xFF & bytes[i]).length() == 1){
                sha1StrBuffer.append("0").append(Integer.toHexString(0xFF & bytes[i]));
            }else {
                sha1StrBuffer.append(Integer.toHexString(0xFF & bytes[i]));
            }
        }
        logger.info("SHA1计算之后进行16进制转换的字符串：{}",sha1StrBuffer.toString());

        try {
            return sha1StrBuffer.toString().getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * sha1计算
     * @param datas
     * @param encoding
     * @return
     */
    private static byte[] sha1(String datas, String encoding) {
        try {
            return sha1(datas.getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            logger.error("SHA1计算失败", e);
            return null;
        }
    }

    /**
     * sha1计算
     * @param data
     * @return
     */
    private static byte[] sha1(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM_SHA1);
            md.reset();
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA1计算失败",e);
            return null;
        }
    }

    /**
     * BASE64解码
     * @param bytes
     * @return
     */
    public static byte[] base64Deode(byte[] bytes) {
        return Base64.decodeBase64(bytes);
    }

    /**
     * 软验证签名
     * @param publicKey 公钥
     * @param signData 签名数据
     * @param srcData 摘要
     * @return
     */
    public static boolean validateSignBySoft(PublicKey publicKey, byte[] signData, byte[] srcData) throws Exception{
        Signature st = Signature.getInstance(BC_PROVI_ALGORITHM_SHA1RSA,"BC");
        st.initVerify(publicKey);
        st.update(srcData);
        return st.verify(signData);
    }
}
