package com.itstyle.modules.unionpay.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/17 10:37
 * @Decription基础配置参数
 */
public class UnionConfig {
    public static String merId = "777290058110048";
    //默认配置的是UTF-8
    public static String encoding_UTF8 = "UTF-8";

    public static String encoding_GBK = "GBK";
    //全渠道固定值
    public static String version = "5.0.0";
    public static String frontUrl;

    //商户发送交易时间，格式：yyyyMMddHHmmss
    public static String getCurrentTime(){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }
    //AN8..40商户订单号，不能含"-"或"_"
    public static String getOrderId(){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

}
