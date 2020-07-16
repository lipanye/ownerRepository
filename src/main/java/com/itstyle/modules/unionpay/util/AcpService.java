package com.itstyle.modules.unionpay.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/17 17:36
 * @Decription
 */
public class AcpService {
    private static final Logger logger = LoggerFactory.getLogger(AcpService.class);

    /**
     * 请求报文签名(使用配置文件中配置的私钥证书加密)<br></>
     * 功能：对请求报文进行签名，并计算赋值certid.signature字段并返回<br></>
     * @param requestData 请求报文 map
     * @param encoding_utf8 上送请求报文域 encoding字段的值
     * @return 签名后的map对象
     */
    public static Map<String, String> sign(Map<String, String> requestData, String encoding_utf8) {
        Map<String,String> submitData = SDKUtil.filterBlank(requestData);
        SDKUtil.sign(submitData,encoding_utf8);
        return submitData;
    }

    /**
     * 功能：前台交易构造HTTP POST 自动提交表单
     * @param requestFrontUrl 表单提交地址
     * @param submitFormData 以Map形式存储的表单键值
     * @param encoding
     * @return
     */
    public static String createAutoFormHtml(String requestFrontUrl, Map<String, String> submitFormData, String encoding) {
        StringBuffer sf = new StringBuffer();
        sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="+encoding+"\"/></head><body>");
        sf.append("<form id=\"pay_form\" action=\""+requestFrontUrl+"\" method=\"post\">");
        if(null!=submitFormData && 0!=submitFormData.size()){
            Set<Map.Entry<String,String>> set = submitFormData.entrySet();
            Iterator<Map.Entry<String,String>> it = set.iterator();
            while (it.hasNext()){
                Map.Entry<String,String> ey = it.next();
                String key = ey.getKey();
                String value = ey.getValue();
                sf.append("<input type=\"hidden\" name=\""+key+"\" value=\""+value+"\"/>");
            }
        }
        sf.append("</form>");
        sf.append("</body>");
        sf.append("<script type=\"text/javascript\">");
        sf.append("document.all.pay_form.submit();");
        sf.append("</script>");
        sf.append("</html>");
        return sf.toString();
    }

    /**
     * 验证签名（SHA-1摘要算法）
     * @param rspData 返回报文数据
     * @param encoding 上送请求报文域encoding字段的值
     * @return
     */
    public static boolean validate(Map<String, String> rspData, String encoding) {
        logger.info("验签处理开始");
        if(SDKUtil.isEmpty(encoding)){
            encoding="UTF-8";
        }
        String stringSign = rspData.get(SDKConstants.param_signature);
        //从返回报文中获取certId，然后去证书静态Map中查询对应验签证书对象
        String certId = rspData.get(SDKConstants.param_certId);
        logger.info("对返回报文串验签使用的验签公钥序列号：["+certId+"]");
        //将Map信息转换成key1=value1&key2=value2的形式
        String stringData = SDKUtil.coverMap2String(rspData);

        logger.info("待验签返回报文串：["+stringData+"]");
        try {
            return SecureUtil.validateSignBySoft(CertUtil.getValidateKey(certId),
                    SecureUtil.base64Deode(stringSign.getBytes(encoding)),SecureUtil.sha1X16(stringData,encoding));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(),e);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return false;
    }

    /**
     * 后台交易提交请求报文并接受同步应答报文
     * @param reqData
     * @param url
     * @param encoding_utf8
     * @return
     */
    public static Map<String, String> post(Map<String, String> reqData, String url, String encoding_utf8) {
        Map<String,String> rspData = new HashMap<>();
        logger.info("请求银联地址："+url);
        //发送后台请求数据
        HttpClient hc = new HttpClient(url,3000,3000);
        try {
            int status = hc.send(reqData,encoding_utf8);
            if(200 == status){
                String resultString = hc.getResult();
                if(null!=resultString && !"".equals(resultString)){
                    //将返回结果转为map
                    Map<String,String> tmpRspData = SDKUtil.convertResultStringToMap(resultString);
                    rspData.putAll(tmpRspData);
                }
            }
        } catch (Exception e) {
           logger.error(e.getMessage(),e);
        }
        return rspData;
    }
}
