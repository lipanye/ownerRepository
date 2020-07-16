package com.itstyle.modules.unionpay.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/20 9:48
 * @Decription
 */
public class CertUtil {
    private static final Logger logger = LoggerFactory.getLogger(CertUtil.class);
    /**证书容器*/
    private static KeyStore keyStore = null;
    /**密码加密证书*/
    private static X509Certificate encryptCert = null;
    /**磁道加密证书*/
    private static X509Certificate encryptTrackCert = null;
    /**磁道公钥加密*/
    private static PublicKey encryptTrackKey = null;
    /**验证签名证书*/
    private static X509Certificate validateCert = null;
    /** 验签证书存储map */
    private static Map<String,X509Certificate> certMap = new HashMap<>();
    /** 根据传入证书文件路径和密码读取指定的证书容器(一种线程安全的实现方式) */
    private static final ThreadLocal<KeyStore> certKeyStoreLocal = new ThreadLocal<>();
    /** 基于Map存储d多商户RSA私钥 */
    private static final Map<String,KeyStore> certKeyStoreMap = new HashMap<>();
    
    static {
        init();
    }

    /**
     * 初始化所有证书
     */
    private static void init() {
        addProvider();
        if(SDKConstants.TRUE_STRING.equals(SDKConfig.getConfig().getSignleMode())){
            //单证书模式，初始化配置文件中的签名证书
            initSignCert();
        }
        initEncryptCert(); //初始化加密公钥证书
        initTrackKey(); //加载磁道公钥
        initValidateCertFromDir(); //初始化所有的验签证书
    }

    /**
     * 从指定目录下加载验证签名证书
     */
    private static void initValidateCertFromDir() {
        certMap.clear();
        String dir = SDKConfig.getConfig().getValidateCertDir();
        logger.info("加载验证签名证书目录==>"+dir);
        if(SDKUtil.isEmpty(dir)){
            logger.info("Error：acpsdk.validateCert.dir is empty");
            return;
        }
        CertificateFactory cf = null;
        FileInputStream fis = null;
        try {
            cf = CertificateFactory.getInstance("X.509","BC");
            File fileDir = new File(dir);
            File[] files = fileDir.listFiles(new CerFilter());
            for(int i=0;i<files.length;i++){
                File file = files[i];
                fis = new FileInputStream(file.getAbsoluteFile());
                validateCert = (X509Certificate) cf.generateCertificate(fis);
                certMap.put(validateCert.getSerialNumber().toString(),validateCert);
                //打印证书加载信息，供测试阶段调试
                logger.info("["+file.getAbsolutePath()+"][Certid="+validateCert.getSerialNumber().toString()+"]");
            }
            logger.info("LoadVerifyCert Successful");
        } catch (CertificateException e) {
            logger.error("LoadVerifyCert Error",e);
        } catch (NoSuchProviderException e) {
            logger.error("LoadVerifyCert Error No BC Provider",e);
        }catch (FileNotFoundException e) {
            logger.error("LoadVerifyCert Error File Not Found", e);
        }finally {
            if(null!=fis){
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("initValidateCertFromDir方法",e);
                }
            }
        }
    }

    /**
     *通过CertId获取证书Map中对应的证书的公钥
     * @param certId
     * @return
     */
    public static PublicKey getValidateKey(String certId) {
        X509Certificate cf;
        if(certMap.containsKey(certId)){
            //存在certId对应的证书对象
            cf = certMap.get(certId);
            return cf.getPublicKey();
        }else{
            //不存在则重新Load证书文件目录
            initValidateCertFromDir();
            if(certMap.containsKey(certId)){
                //存在certId对应的证书对象
                cf = certMap.get(certId);
                return cf.getPublicKey();
            }else{
                logger.info("缺少certId=["+certId+"] 对应的验签证书");
                return null;
            }
        }
    }

    /**
     * 证书文件过滤器
     */
    static class CerFilter implements FilenameFilter{
        public boolean isCer(String name){
            if(name.toLowerCase().endsWith(".cer")){
                return true;
            }else{
                return false;
            }
        }

        @Override
        public boolean accept(File dir, String name) {
            return isCer(name);
        }
    }


    /**
     * 加载磁道公钥
     */
    private static void initTrackKey() {
        if(!SDKUtil.isEmpty(SDKConfig.getConfig().getEncryptTrackKeyModules())
            && !SDKUtil.isEmpty(SDKConfig.getConfig().getEncryptTrackKeyExponent())){
            encryptTrackKey = SecureUtil.getPublicKey(SDKConfig.getConfig().getEncryptTrackKeyModules(),
                    SDKConfig.getConfig().getEncryptTrackKeyExponent());
            logger.info("LoadEncryptTrackKey Successful");
        }else{
            logger.info("WARN：acpsdk.encryptTrackKey.modules or acpsdk.encryptTrackKey.exponent is empty");
        }
    }

    /**
     * 加载密码加密证书 目前支持有两种加密
     */
    private static void initEncryptCert() {
        logger.info("加载敏感信息加密证书===>"+SDKConfig.getConfig().getEncryptCertPath());
        if(!SDKUtil.isEmpty(SDKConfig.getConfig().getEncryptCertPath())){
            encryptCert = initCert(SDKConfig.getConfig().getEncryptCertPath());
            logger.info("LoadEncryptCert Successful");
        }else{
            logger.info("WARN: acpsdk.encryptCert.path is empty");
        }
    }

    private static X509Certificate initCert(String encryptCertPath) {
        X509Certificate encryptCertTemp = null;
        CertificateFactory cf = null;
        FileInputStream fis = null;
        try {
            cf = CertificateFactory.getInstance("X.509","BC");
            fis = new FileInputStream(encryptCertPath);
            encryptCertTemp = (X509Certificate) cf.generateCertificate(fis);
            //打印证书加载信息，供测试阶段调试
            logger.info("["+encryptCertPath+"][CertId="+encryptCertTemp.getSerialNumber().toString()+"]");
        } catch (CertificateException e) {
            logger.error("InitCert Error",e);
        } catch (NoSuchProviderException e) {
            logger.error("LoadVerifyCert Error No BC Provider",e);
        }catch (FileNotFoundException e) {
            logger.error("InitCert Error File Not Found",e);
        }
        return encryptCertTemp;
    }

    /**
     * 加载签名证书
     */
    private static void initSignCert() {
        if(null!=keyStore){
            keyStore=null;
        }
        try {
            keyStore = getKeyInfo(SDKConfig.getConfig().getSignCertPath(),SDKConfig.getConfig().getSignCertPwd(),
                    SDKConfig.getConfig().getSignCertType());
            logger.info("InitSignCert Successful CertId:{}",getSignCertId());
        } catch (IOException e) {
            logger.error("InitSingCert Error",e);
        }
    }

    /**
     * 将证书文件读取为证书存储对象
     * @param pfxkeyfile 证书文件名
     * @param keypwd 证书密码
     * @param type 证书类型
     * @return 证书对象
     */
    private static KeyStore getKeyInfo(String pfxkeyfile, String keypwd, String type) throws IOException {
        logger.info("加载签名证书：{}",pfxkeyfile);
        FileInputStream fis = null;
        try {
            KeyStore ks = KeyStore.getInstance(type,"BC");
            logger.info("Load RSA CertPath:{},Pwd:{},type:{}",pfxkeyfile,keypwd,type);
            fis = new FileInputStream(pfxkeyfile);
            char[] nPassword = null == keypwd || "".equals(keypwd.trim())?null : keypwd.toCharArray();
            if(null != ks){
                ks.load(fis,nPassword);
            }
            return ks;
        } catch (Exception e) {
            if(Security.getProperty("BC")==null){
                logger.info("BC Provider not installed");
            }
            logger.error("getKeyInfo Error",e);
            if((e instanceof KeyStoreException) && "PKCS12".equals(type)){
                Security.removeProvider("BC");
            }
            return null;
        }finally {
            if(null!=fis){
                fis.close();
            }
        }
    }

    /**
     * 添加签名、验签、加密算法提供者
     */
    private static void addProvider() {
        if(Security.getProvider("BC")==null){
            logger.info("add BC provider");
            Security.addProvider(new BouncyCastleProvider());
        }else{
            Security.removeProvider("BC"); //解决eclipse调试时tomcat自动加载时，BC存在不明原因异常的问题
            Security.addProvider(new BouncyCastleProvider());
            logger.info("re-add BC provider");
        }
        printSysInfo();
    }
    //打印系统环境信息
    private static void printSysInfo() {
        logger.info("===================SYS INFO BEGIN==================");
        logger.info("os_name：{}",System.getProperty("os.name"));
        logger.info("os_arch：{}",System.getProperty("os.arch"));
        logger.info("os_version：{}",System.getProperty("os.version"));
        logger.info("java_vm_specification_version：{}",System.getProperty("java.vm.specification.version"));
        logger.info("java_vm_speification_vendor:{}",System.getProperty("java.vm.speification.vendor"));
        logger.info("java_vm_speification_name:{}",System.getProperty("java.vm.speification.name"));
        logger.info("java_vm_version:{}",System.getProperty("java.vm.version"));
        logger.info("java_vm_name:{}",System.getProperty("java.vm.name"));
        logger.info("java_version:{}",System.getProperty("java.version"));
        logger.info("java_vm_vendor:{}",System.getProperty("java.vm.vendor"));

        printProviders();
        logger.info("===================SYS INFO END==================");
    }


    public static void printProviders() {
        logger.info("Providers List:");
        Provider[] providers = Security.getProviders();
        for(int i=0;i<providers.length;i++){
            logger.info(i+1+"."+providers[i].getName());
        }
    }

    /**
     * 获取签名证书中的证书序列号(单证书)
     * 证书序列号（单证书）
     * @return 证书的物理编号
     */
    public static String getSignCertId() {
        try {
            Enumeration<String> aliasenum = keyStore.aliases();
            String keyAlias = null;
            if(aliasenum.hasMoreElements()){
                keyAlias = aliasenum.nextElement();
            }
            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(keyAlias);
            return certificate.getSerialNumber().toString();
        } catch (Exception e) {
            logger.error("getSignCertId Error", e);
            return null;
        }
    }
}
