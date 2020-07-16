package com.itstyle.common.constants;

//import org.springframework.util.ClassUtils;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/03/23 14:38
 * @Decription
 */
public class Constants {
    /**
     * 文件分割符
     */
    public static final String SF_FILE_SEPARATOR=System.getProperty("file.separator");
    /**
     * 行分隔符
     */
    public static final String SF_LINE_SEPARATOR=System.getProperty("line.separator");
    /**
     * 路径分隔符
     */
    public static final String SF_PATH_SEPARATOR=System.getProperty("path.separator");

    //ClassUtils.getDefaultClassLoader().getResource("static").getPath() 会报空指针，所以暂时不用
   // public static final String QRCODE_PATH = ClassUtils.getDefaultClassLoader().getResource("static").getPath()+SF_FILE_SEPARATOR+"qrcode";

    /**
     * 微信账单 相关字段 用于load文本到数据库
     */
   /* public static final String WECHAT_BILL = "tradetime, ghid, mchid, submch, deviceid, wxorder, bzorder, " +
            "openid, tradetype, tradestatus, bank, currency, totalmoney, redpacketmoney, wxrefund, bzrefund," +
            " refundmoney, redpacketrefund, refundtype, refundstatus, productname, bzdatapacket, fee, rate";

    public static final String PATH_BASE_INFO_XML = SF_FILE_SEPARATOR+"WEB-INF"+SF_FILE_SEPARATOR+"xmlConfig"+SF_FILE_SEPARATOR;

    public static final String CURRENT_USER="UserInfo";*/
    public static final String SUCCESS ="success";
    public static final String FAIL = "fail";


}
