package com.itstyle.modules.wechatpay.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/01 16:08
 * @Decription 微信相关配置参数
 */
public class ConfigUtil {
    private static Configuration configs;
    /**
     * 服务号应用ID
     */
    public static String APP_ID;
    /**
     * 服务号的应用密钥
     */
    public static String APP_SECRET;
    /**
     * 服务号配置Token
     */
    public static String TOKEN;
    /**
     * 商户号
     */
    public static String MCH_ID;
    /**
     * API密钥
     */
    public static String API_KEY;
    /**
     * 签名加密方式
     */
    public static String SIGN_TYPE;
    /**
     * 微信支付证书
     */
    public static String CERT_PATH;

    public static synchronized void init(String filePath) {
        if(configs!=null){
            return;
        }
        try {
            configs = new PropertiesConfiguration(filePath);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        if(configs == null){
            throw new IllegalStateException("can't find file by path："+filePath);
        }
        APP_ID = configs.getString("APP_ID");
        APP_SECRET = configs.getString("APP_SECRET");
        TOKEN = configs.getString("TOKEN");
        MCH_ID = configs.getString("MCH_ID");
        API_KEY = configs.getString("API_KEY");
        SIGN_TYPE = configs.getString("SIGN_TYPE");
        CERT_PATH = configs.getString("CERT_PATH");
    }

    /**
     * 微信基础接口地址
     */
    // 获取token接口(GET)
    public final static String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    // oauth2授权接口(GET)
    public final static String OAUTH2_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    // 刷新access_token接口（GET）
    public final static String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
    // 菜单创建接口（POST）
    public final static String MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
    // 菜单查询（GET）
    public final static String MENU_GET_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
    // 菜单删除（GET）
    public final static String MENU_DELETE_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";

    /**
     * 微信支付接口地址
     */
    // 微信支付统一接口(POST)
    public final static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    // 微信退款接口(POST)
    public final static String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    // 订单查询接口(POST)
    public final static String CHECK_ORDER_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    // 关闭订单接口(POST)
    public final static String CLOSE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/closeorder";
    // 退款查询接口(POST)
    public final static String CHECK_REFUND_URL = "https://api.mch.weixin.qq.com/pay/refundquery";
    // 对账单接口(POST)
    public final static String DOWNLOAD_BILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";
    // 短链接转换接口(POST)
    public final static String SHORT_URL = "https://api.mch.weixin.qq.com/tools/shorturl";
    // 接口调用上报接口(POST)
    public final static String REPORT_URL = "https://api.mch.weixin.qq.com/payitil/report";

    /**
     *基础参数
     * @param packageParams
     */
    public static void commonParams(SortedMap<Object,Object> packageParams){
        //账号信息
        String appId = ConfigUtil.APP_ID;
        String mchId = ConfigUtil.MCH_ID;
        //生成随机字符串
        String currTime = PayCommonUtil.getCurrTime();
        String strTime = currTime.substring(8,currTime.length());
        String strRandom = PayCommonUtil.buildRandom(4)+"";
        String noceStr = strTime+strRandom;
        packageParams.put("appid",appId);
        packageParams.put("mch_id",mchId);
        packageParams.put("nonce_str",noceStr);
    }

    /**
     * 该接口主要用于扫码原生支付模式二中的二维码链接转成短链接(weixin://wxpay/xxxx)，减小二维码数据量，提升扫描速度和精准度
     * @param urlCode
     */
    public static String shortUrl(String urlCode) {
        SortedMap<Object,Object> packageParams = new TreeMap<>();
        ConfigUtil.commonParams(packageParams);
        //URL链接
        packageParams.put("long_url",urlCode);
        String sign = PayCommonUtil.createSign("UTF-8",packageParams,ConfigUtil.API_KEY);
        packageParams.put("sign",sign);
        String requestXml = PayCommonUtil.getRequestXml(packageParams);
        String responseXml = HttpUtil.postData(ConfigUtil.SHORT_URL,requestXml);
        try {
            Map map = XMLUtil.doXMLParse(responseXml);
            String returnCode = (String) map.get("return_code");
            if("SUCCESS".equals(returnCode)){
                String resultCode = (String) map.get("result_code");
                if("SUCCESS".equals(resultCode)){
                    urlCode = (String) map.get("short_url");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlCode;
    }
}
