package com.itstyle.modules.unionpay.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/20 10:48
 * @Decription
 */
public class SDKConfig {
    private static final Logger logger = LoggerFactory.getLogger(SDKConfig.class);
    /**
     *从应用的classpath下加载acp_sdk.properties属性文件并将该属性文件中的键值对赋值到SDKConfig类中
     */
    public static final String FILE_NAME = "acp_sdk.properties";
    /**
     * 前台请求URL
     */
    private String frontRequestUrl;
    /**
     * 后台请求URL
     */
    private String backRequestUrl;
    /**
     * 单笔查询
     */
    private String signleQueryUrl;
    /**
     * 批量查询
     */
    private String batchQueryUrl;
    /**
     * 批量交易
     */
    private String batchTransUrl;
    /**
     * 文件传输
     */
    private String fileTransUrl;
    /**
     * 签名证书路径
     */
    private String signCertPath;
    /**
     * 签名证书密码
     */
    private String signCertPwd;
    /**
     * 签名证书类型
     */
    private String signCertType;
    /**
     * 加密公钥证书路径
     */
    private String encryptCertPath;
    /**
     * 验证签名公钥证书目录
     */
    private String validateCertDir;
    /**
     * 按照商户代码读取指定签名证书目录
     */
    private String signCertDir;
    /**
     * 磁道加密证书路径
     */
    private String encryptTrackCertPath;
    /**
     * 磁道加密公钥模数
     */
    private String encryptTrackKeyModules;
    /**
     * 磁道加密公钥指数
     */
    private String encryptTrackKeyExponent;
    /**
     * 有卡交易
     */
    private String cardRequestUrl;
    /**
     * app交易
     */
    private String appRequestUrl;
    /**
     * 证书使用模式（单证书/多证书）
     */
    private String signleMode;

    /* 缴费相关地址 */
    private String jfRrontRequestUrl;
    private String jfBackRequestUrl;
    private String jfSingleQueryUrl;
    private String jfCardRequestrUrl;
    private String jfAppRequestUrl;

    /**
     * 配置文件中的前台URL常量
     */
    public static final String SDK_FRONT_URL = "acpsdk.frontTransUrl";
    /**
     * 配置文件中的后台URL常量
     */
    public static final String SDK_BACK_URL = "acpsdk.backTransUrl";
    /**
     * 配置文件中的单笔交易查询URL常量
     */
    public static final String SDK_SIGNQ_URL = "acpsdk.singleQueryUrl";
    /**
     * 配置文件中的批量交易查询URL常量
     */
    public static final String SDK_BATQ_URL = "acpsdk.batchQueryUrl";
    /**
     * 配置文件中的批量交易URL常量
     */
    public static final String SDK_BATTRANS_URL = "acpsdk.batchTransUrl";
    /**
     * 配置文件中的文件交易类型URL常量
     */
    public static final String SDK_FILETRANS_URL = "acpsdk.fileTransUrl";
    /**
     * 配置文件中的有卡交易URL常量
     */
    public static final String SDK_CARD_URL = "acpsdk.cardTransUrl";
    /**
     * 配置文件中的app交易URL常量
     */
    public static final String SDK_APP_URL = "acpsdk.appTransUrl";

    /*以下缴费产品使用，其他产品用不到，无视即可*/
    /**
     * 前台请求地址
     */
    public static final String JF_SDK_FRONT_TRANS_URL = "acpsdk.jfFrontTransUrl";
    /**
     *后台请求地址
     */
    public static final String JF_SDK_BACK_TRANS_URL = "acpsdk.jfBackTransUrl";
    /**
     * 单笔查询请求地址
     */
    public static final String JF_SDK_SINGLE_QUERY_URL = "acpsdk.jfSingleQueryUrl";
    /**
     * 有卡交易地址
     */
    public static final String JF_SDK_CARD_TRANS_URL = "acpsdk.jfCardTransUrl";
    /**
     * APP交易地址
     */
    public static final String JF_SDK_APP_TRANS_URL = "acpsdk.jfAppTransUrl";
    /**
     * 配置文件中签名证书路径常量
     */
    public static final String SDK_SIGNCERT_PATH = "acpsdk.signCert.path";
    /** 配置文件中签名证书密码常量. */
    public static final String SDK_SIGNCERT_PWD = "acpsdk.signCert.pwd";
    /** 配置文件中签名证书类型常量. */
    public static final String SDK_SIGNCERT_TYPE = "acpsdk.signCert.type";
    /** 配置文件中密码加密证书路径常量. */
    public static final String SDK_ENCRYPTCERT_PATH = "acpsdk.encryptCert.path";
    /** 配置文件中磁道加密证书路径常量. */
    public static final String SDK_ENCRYPTTRACKCERT_PATH = "acpsdk.encryptTrackCert.path";
    /** 配置文件中磁道加密公钥模数常量. */
    public static final String SDK_ENCRYPTTRACKKEY_MODULUS = "acpsdk.encryptTrackKey.modulus";
    /** 配置文件中磁道加密公钥指数常量. */
    public static final String SDK_ENCRYPTTRACKKEY_EXPONENT = "acpsdk.encryptTrackKey.exponent";
    /** 配置文件中验证签名证书目录常量. */
    public static final String SDK_VALIDATECERT_DIR = "acpsdk.validateCert.dir";

    /** 配置文件中是否加密cvn2常量. */
    public static final String SDK_CVN_ENC = "acpsdk.cvn2.enc";
    /** 配置文件中是否加密cvn2有效期常量. */
    public static final String SDK_DATE_ENC = "acpsdk.date.enc";
    /** 配置文件中是否加密卡号常量. */
    public static final String SDK_PAN_ENC = "acpsdk.pan.enc";
    /** 配置文件中证书使用模式 */
    public static final String SDK_SINGLEMODE = "acpsdk.singleMode";
    /** 操作对象. */
    private static SDKConfig config;
    /** 属性文件对象. */
    private Properties properties;

    public static Logger getLogger() {
        return logger;
    }

    public static String getFileName() {
        return FILE_NAME;
    }

    public String getFrontRequestUrl() {
        return frontRequestUrl;
    }

    public void setFrontRequestUrl(String frontRequestUrl) {
        this.frontRequestUrl = frontRequestUrl;
    }

    public String getBackRequestUrl() {
        return backRequestUrl;
    }

    public void setBackRequestUrl(String backRequestUrl) {
        this.backRequestUrl = backRequestUrl;
    }

    public String getSignleQueryUrl() {
        return signleQueryUrl;
    }

    public void setSignleQueryUrl(String signleQueryUrl) {
        this.signleQueryUrl = signleQueryUrl;
    }

    public String getBatchQueryUrl() {
        return batchQueryUrl;
    }

    public void setBatchQueryUrl(String batchQueryUrl) {
        this.batchQueryUrl = batchQueryUrl;
    }

    public String getBatchTransUrl() {
        return batchTransUrl;
    }

    public void setBatchTransUrl(String batchTransUrl) {
        this.batchTransUrl = batchTransUrl;
    }

    public String getFileTransUrl() {
        return fileTransUrl;
    }

    public void setFileTransUrl(String fileTransUrl) {
        this.fileTransUrl = fileTransUrl;
    }

    public String getSignCertPath() {
        return signCertPath;
    }

    public void setSignCertPath(String signCertPath) {
        this.signCertPath = signCertPath;
    }

    public String getSignCertPwd() {
        return signCertPwd;
    }

    public void setSignCertPwd(String signCertPwd) {
        this.signCertPwd = signCertPwd;
    }

    public String getSignCertType() {
        return signCertType;
    }

    public void setSignCertType(String signCertType) {
        this.signCertType = signCertType;
    }

    public String getEncryptCertPath() {
        return encryptCertPath;
    }

    public void setEncryptCertPath(String encryptCertPath) {
        this.encryptCertPath = encryptCertPath;
    }

    public String getValidateCertDir() {
        return validateCertDir;
    }

    public void setValidateCertDir(String validateCertDir) {
        this.validateCertDir = validateCertDir;
    }

    public String getSignCertDir() {
        return signCertDir;
    }

    public void setSignCertDir(String signCertDir) {
        this.signCertDir = signCertDir;
    }

    public String getEncryptTrackCertPath() {
        return encryptTrackCertPath;
    }

    public void setEncryptTrackCertPath(String encryptTrackCertPath) {
        this.encryptTrackCertPath = encryptTrackCertPath;
    }

    public String getEncryptTrackKeyModules() {
        return encryptTrackKeyModules;
    }

    public void setEncryptTrackKeyModules(String encryptTrackKeyModules) {
        this.encryptTrackKeyModules = encryptTrackKeyModules;
    }

    public String getEncryptTrackKeyExponent() {
        return encryptTrackKeyExponent;
    }

    public void setEncryptTrackKeyExponent(String encryptTrackKeyExponent) {
        this.encryptTrackKeyExponent = encryptTrackKeyExponent;
    }

    public String getCardRequestUrl() {
        return cardRequestUrl;
    }

    public void setCardRequestUrl(String cardRequestUrl) {
        this.cardRequestUrl = cardRequestUrl;
    }

    public String getAppRequestUrl() {
        return appRequestUrl;
    }

    public void setAppRequestUrl(String appRequestUrl) {
        this.appRequestUrl = appRequestUrl;
    }

    public String getSignleMode() {
        return signleMode;
    }

    public void setSignleMode(String signleMode) {
        this.signleMode = signleMode;
    }

    public String getJfRrontRequestUrl() {
        return jfRrontRequestUrl;
    }

    public void setJfRrontRequestUrl(String jfRrontRequestUrl) {
        this.jfRrontRequestUrl = jfRrontRequestUrl;
    }

    public String getJfBackRequestUrl() {
        return jfBackRequestUrl;
    }

    public void setJfBackRequestUrl(String jfBackRequestUrl) {
        this.jfBackRequestUrl = jfBackRequestUrl;
    }

    public String getJfSingleQueryUrl() {
        return jfSingleQueryUrl;
    }

    public void setJfSingleQueryUrl(String jfSingleQueryUrl) {
        this.jfSingleQueryUrl = jfSingleQueryUrl;
    }

    public String getJfCardRequestrUrl() {
        return jfCardRequestrUrl;
    }

    public void setJfCardRequestrUrl(String jfCardRequestrUrl) {
        this.jfCardRequestrUrl = jfCardRequestrUrl;
    }

    public String getJfAppRequestUrl() {
        return jfAppRequestUrl;
    }

    public void setJfAppRequestUrl(String jfAppRequestUrl) {
        this.jfAppRequestUrl = jfAppRequestUrl;
    }

    public static String getSdkFrontUrl() {
        return SDK_FRONT_URL;
    }

    public static String getSdkBackUrl() {
        return SDK_BACK_URL;
    }

    public static String getSdkSignqUrl() {
        return SDK_SIGNQ_URL;
    }

    public static String getSdkBatqUrl() {
        return SDK_BATQ_URL;
    }

    public static String getSdkBattransUrl() {
        return SDK_BATTRANS_URL;
    }

    public static String getSdkFiletransUrl() {
        return SDK_FILETRANS_URL;
    }

    public static String getSdkCardUrl() {
        return SDK_CARD_URL;
    }

    public static String getSdkAppUrl() {
        return SDK_APP_URL;
    }

    public static String getJfSdkFrontTransUrl() {
        return JF_SDK_FRONT_TRANS_URL;
    }

    public static String getJfSdkBackTransUrl() {
        return JF_SDK_BACK_TRANS_URL;
    }

    public static String getJfSdkSingleQueryUrl() {
        return JF_SDK_SINGLE_QUERY_URL;
    }

    public static String getJfSdkCardTransUrl() {
        return JF_SDK_CARD_TRANS_URL;
    }

    public static String getJfSdkAppTransUrl() {
        return JF_SDK_APP_TRANS_URL;
    }

    public static String getSdkSigncertPath() {
        return SDK_SIGNCERT_PATH;
    }

    public static String getSdkSigncertPwd() {
        return SDK_SIGNCERT_PWD;
    }

    public static String getSdkSigncertType() {
        return SDK_SIGNCERT_TYPE;
    }

    public static String getSdkEncryptcertPath() {
        return SDK_ENCRYPTCERT_PATH;
    }

    public static String getSdkEncrypttrackcertPath() {
        return SDK_ENCRYPTTRACKCERT_PATH;
    }

    public static String getSdkEncrypttrackkeyModulus() {
        return SDK_ENCRYPTTRACKKEY_MODULUS;
    }

    public static String getSdkEncrypttrackkeyExponent() {
        return SDK_ENCRYPTTRACKKEY_EXPONENT;
    }

    public static String getSdkValidatecertDir() {
        return SDK_VALIDATECERT_DIR;
    }

    public static String getSdkCvnEnc() {
        return SDK_CVN_ENC;
    }

    public static String getSdkDateEnc() {
        return SDK_DATE_ENC;
    }

    public static String getSdkPanEnc() {
        return SDK_PAN_ENC;
    }

    public static String getSdkSinglemode() {
        return SDK_SINGLEMODE;
    }

    public static SDKConfig getConfig() {
        if(null==config){
            config = new SDKConfig();
        }
        return config;
    }

    public static void setConfig(SDKConfig config) {
        SDKConfig.config = config;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
