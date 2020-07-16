package com.itstyle.modules.wechatpay.service.impl;

import com.alipay.api.internal.util.XmlUtils;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.itstyle.common.constants.Constants;
import com.itstyle.common.model.Product;
import com.itstyle.common.utils.CommonUtils;
import com.itstyle.common.utils.DateUtils;
import com.itstyle.modules.wechatpay.service.IWeChatPayService;
import com.itstyle.modules.wechatpay.util.*;
import net.sf.json.JSONObject;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import weixin.popular.api.SnsAPI;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/03 15:01
 * @Decription
 */
@Service
public class WeChatPayServiceImpl implements IWeChatPayService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatPayServiceImpl.class);
    @Value("${wechatpay.notify.url}")
    private String notifyUrl;
    @Value("${server.context.url}")
    private String serverUrl;

    private static final String qrcode_path = System.getProperty("user.dir")+"\\src\\main\\resources\\static\\qrcode";

    @Override
    public String weChatPayMobile(Product product) {
        String totalFee = product.getTotalFee();
        //redirect_url 需要在微信支付端添加认证网址
        totalFee = CommonUtils.subZeroAndDot(totalFee);
        String redirectUrl = serverUrl+"wechatMobile/dopay?outTradeNo="+product.getOutTradeNo()+"&totalFee="+totalFee;
        return SnsAPI.connectOauth2Authorize(ConfigUtil.APP_ID,redirectUrl,true,null);
    }

    @Override
    public String wechatPayH5(Product product) {
        logger.info("订单号，{} 发起H5支付",product.getOutTradeNo());
        String mweb_url = "";
        try {
            //账号信息
            //key
            String key = ConfigUtil.API_KEY;
            //交易类型 H5 支付
            String trade_type = "MWEB";
            SortedMap<Object,Object> packageParams = new TreeMap<>();
            ConfigUtil.commonParams(packageParams);
            //商品ID
            packageParams.put("product_id",product.getProductId());
            //商品描述
            packageParams.put("body",product.getBody());
            //订单号
            packageParams.put("out_trade_no",product.getOutTradeNo());
            String totalFee = product.getTotalFee();
            totalFee = CommonUtils.subZeroAndDot(totalFee);
            //总金额
            packageParams.put("totalFee",totalFee);
            //H5支付要求商户在线统一下单接口中上传用户真是ip地址 spbill_create_ip
            //发起人IP地址
            packageParams.put("spbill_create_ip",product.getSpbillCreateIp());
            //回调地址
            packageParams.put("notify_url",notifyUrl);
            //交易类型
            packageParams.put("trade_type",trade_type);
            //H5支付专用
            JSONObject value = new JSONObject();
            value.put("type","WAP");
            //WAP网站URL地址
            value.put("wap_url","http://lipanye.iok");
            //WAP网站名
            value.put("wap_name","xxxx");
            JSONObject scene_info = new JSONObject();
            scene_info.put("h5_info",value);
            packageParams.put("scene_info",scene_info);

            String sign = PayCommonUtil.createSign("UTF-8",packageParams,key);
            packageParams.put("sign",sign);

            String requestXml = PayCommonUtil.getRequestXml(packageParams);
            String resXml = HttpUtil.postData(ConfigUtil.UNIFIED_ORDER_URL,requestXml);
            Map map = XMLUtil.doXMLParse(requestXml);
            String returnCode = (String) map.get("return_code");
            if("SUCCESS".equals(returnCode)){
                String resultCode = (String) map.get("result_code");
                if("SUCCESS".equals(resultCode)){
                    logger.info("订单号，{} 发起H5支付成功",product.getOutTradeNo());
                    mweb_url = (String) map.get("mweb_url");
                }else{
                    String errCodeDes = (String) map.get("err_code_des");
                    logger.info("订单号：{},发起H5支付（系统失败）：{}",product.getOutTradeNo(),errCodeDes);
                }
            }else{
                String returnMsg = (String) map.get("return_msg");
                logger.info("订单号：{} 发起H5支付（通信）失败：{}",product.getOutTradeNo(),returnMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("订单号：{} 发起H5支付失败（系统异常）",product.getOutTradeNo(),e);
        }

        return mweb_url;
    }

    @Override
    public void wechatPay1(Product product) {
        //商户支付回调URL设置指引：进入公众平台 --> 微信支付 --> 开发配置 --> 扫码支付 --> 修改 加入回调URL
        SortedMap<Object,Object> packageParams = new TreeMap<>();
        //商品ID
        packageParams.put("product_id",product.getProductId());
        //商品描述
        packageParams.put("body",product.getBody());
        //订单号
        packageParams.put("out_trade_no",product.getOutTradeNo());
        String totalFee = product.getTotalFee();
        totalFee = CommonUtils.subZeroAndDot(totalFee);
        //总金额
        packageParams.put("totalFee",totalFee);
        //回调地址
        packageParams.put("notify_url",notifyUrl);
        packageParams.put("timestamp",PayCommonUtil.getCurrTime());
        //封装通用参数
        ConfigUtil.commonParams(packageParams);
        //生成签名
        String sign = PayCommonUtil.createSign("UTF-8",packageParams,ConfigUtil.API_KEY);
        //组装二维码信息(注意全角和半角的区别)
        StringBuffer qrCode = new StringBuffer();
        qrCode.append("weixin://wxpay/bizpayurl?");
        qrCode.append("appid="+ConfigUtil.APP_ID);
        qrCode.append("&mch_id="+ConfigUtil.MCH_ID);
        qrCode.append("&nonce_str="+packageParams.get("nonce_str"));
        qrCode.append("&product_id="+product.getProductId());
        qrCode.append("&time_stamp="+packageParams.get("timestamp"));
        qrCode.append("&sign="+sign);
        String qrcodePath = qrcode_path+ Constants.SF_FILE_SEPARATOR+product.getProductId()+".png";
        /**
         * 生成二维码
         * 1、这里如果是一个单独的服务的话，建议直接返回qrCode，调用方自己生成二维码
         * 2、如果真要生成，生成到系统的绝对路径
         */
        ZxingUtils.getQRCodeImge(qrCode.toString(),256,qrcodePath);
    }

    /**
     * 微信支付要求商户订单号保持唯一性(建议根据当前系统时间加随机序列号来生成订单)
     * 重新发起一笔支付要使用原订单号，避免重复支付：已支付或者已调用关闭、撤销的订单号不能重新发起支付
     * 注意：支付金额和商品描述必须一样，下单后金额或描述有改变也会出现重复订单号
     * @param product
     * @return
     */
    @Override
    public String wechatPay2(Product product) {
        logger.info("订单号：{} 生成微信支付码",product.getOutTradeNo());
        String message = Constants.SUCCESS;
        String imgPath = qrcode_path+Constants.SF_FILE_SEPARATOR+product.getOutTradeNo()+".png";
        try {
            //账号信息
            //key
            String key = ConfigUtil.API_KEY;
            //交易类型原生扫码支付
            String tradeType ="NATIVE";
            SortedMap<Object,Object> packageParams = new TreeMap<>();
            ConfigUtil.commonParams(packageParams);
            //商品ID
            packageParams.put("product_id",product.getProductId());
            //商品描述
            packageParams.put("body",product.getBody());
            //商户订单号
            packageParams.put("out_trade_no",product.getOutTradeNo());
            String totalFee = product.getTotalFee();
            totalFee = CommonUtils.subZeroAndDot(totalFee);
            //总金额
            packageParams.put("totalFee",totalFee);
            //发起人IP地址
            packageParams.put("spbill_create_ip",product.getSpbillCreateIp());
            //回调地址
            packageParams.put("notify_url",notifyUrl);
            //交易类型
            packageParams.put("trade_type",tradeType);
            String sign = PayCommonUtil.createSign("UTF-8",packageParams,ConfigUtil.API_KEY);
            packageParams.put("sign",sign);
            String requestXml = PayCommonUtil.getRequestXml(packageParams);
            String resultXml = HttpUtil.postData(ConfigUtil.UNIFIED_ORDER_URL,requestXml);
            Map map = XMLUtil.doXMLParse(resultXml);
            String returnCode = (String) map.get("return_code");
            if("SUCCESS".equals(returnCode)){
                String resultCode = (String) map.get("result_code");
                if("SUCCESS".equals(resultCode)){
                    logger.info("订单号：{} 生成微信支付码成功",product.getOutTradeNo());
                    String urlCode = (String) map.get("code_url");
                    //转换为短链接
                    String shotUrl = ConfigUtil.shortUrl(urlCode);
                    //生成二维码
                    ZxingUtils.getQRCodeImge(shotUrl,256,imgPath);
                }else{
                    String errCodeDes = (String) map.get("err_code_des");
                    logger.info("订单号：{} 生成微信支付码(系统)失败，{}",product.getOutTradeNo(),errCodeDes);
                    message = Constants.FAIL;
                }
            }else{
                String returnMsg = (String) map.get("return_msg");
                logger.info("订单号，{} 生成微信支付码（通信）失败 {}",product.getOutTradeNo(),returnMsg);
                message = Constants.FAIL;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("订单号：{} 生成微信支付码失败（系统异常）",product.getOutTradeNo(),e);
            message = Constants.FAIL;
        }
        return message;
    }

    @Override
    public String wechatPayRefund(Product product) {
        logger.info("订单号：{} 微信退款",product.getOutTradeNo());
        String message = Constants.SUCCESS;
        try{
            //账号信息
            //商户号
            String mch_id= ConfigUtil.MCH_ID;
            String key = ConfigUtil.API_KEY;

            SortedMap<Object,Object> packageParam = new TreeMap<>();
            ConfigUtil.commonParams(packageParam);
            //订单号
            packageParam.put("out_trade_no",product.getOutTradeNo());
            //退款单号
            packageParam.put("out_refund_no",product.getOutTradeNo());
            //总金额
            String totalFee = product.getTotalFee();
            totalFee = CommonUtils.subZeroAndDot(totalFee);
            packageParam.put("total_fee",totalFee);
            //退款金额
            packageParam.put("refund_fee",totalFee);
            //操作员账号，默认商户号
            packageParam.put("op_user_id",mch_id);
            String sign = PayCommonUtil.createSign("UTF-8",packageParam,key);
            packageParam.put("sign",sign);
            String requestXml = PayCommonUtil.getRequestXml(packageParam);
            String wechatPost = ClientCustomSSL.doRefund(ConfigUtil.REFUND_URL,requestXml);
            Map map = XMLUtil.doXMLParse(wechatPost);
            String returnCode = (String) map.get("return_code");
            if("SUCCESS".equals(returnCode)){
                String resultCode = (String) map.get("result_code");
                if("SUCCESS".equals(resultCode)){
                    logger.info("订单号：{} 退款成功并删除二维码",product.getOutTradeNo());
                }else {
                    String errCodeDes = (String) map.get("err_code_des");
                    logger.info("订单号：{} 微信退款失败，{}",product.getOutTradeNo(),errCodeDes);
                    message = Constants.FAIL;
                }
            }else {
                String returnMsg = (String) map.get("return_msg");
                logger.info("订单号：{} 退款失败：{}",product.getOutTradeNo(),returnMsg);
                message = Constants.FAIL;
            }
        }catch (Exception e){
            logger.error("订单号：{} 微信支付退款失败，订单异常",product.getOutTradeNo(),e);
            message = Constants.FAIL;
        }
        return message;
    }

    @Override
    public String wechatCloseOrder(Product product) {
        logger.info("订单号：{} 微信关闭订单",product.getOutTradeNo());
        String message = Constants.SUCCESS;
        try {
            String outTradeNo = product.getOutTradeNo();
            SortedMap<Object,Object> packageParams = new TreeMap<>();
            ConfigUtil.commonParams(packageParams);
            packageParams.put("out_trade_no",outTradeNo);
            String sign = PayCommonUtil.createSign("UTF-8",packageParams,ConfigUtil.API_KEY);
            packageParams.put("sign",sign);
            String requestXml = PayCommonUtil.getRequestXml(packageParams);
            String resXml = HttpUtil.postData(ConfigUtil.CLOSE_ORDER_URL,requestXml);
            Map map = XMLUtil.doXMLParse(resXml);
            String returnCode = (String) map.get("return_code");
            if("SUCCESS".equals(returnCode)){
                String resultCode = (String) map.get("result_code");
                if("SUCCESS".equals(resultCode)){
                    logger.info("订单号：{} 订单关闭成功",outTradeNo);
                }else{
                    String errCode = (String) map.get("err_code");
                    String errCodeDes = (String) map.get("err_code_des");
                    //订单不存在或者已关闭
                    if("ORDERNOTEXIST".equals(errCode) || "ORDERCLOSED".equals(errCode)){
                        logger.info("订单号：{} 微信关闭订单{}",outTradeNo,errCodeDes);
                    }else{
                        logger.info("订单号：{} 微信关闭订单失败：{}",outTradeNo,errCodeDes);
                        message = Constants.FAIL;
                    }
                }
            }else{
                String returnMsg = (String) map.get("return_msg");
                logger.info("订单号：{} 微信关闭订单失败：{}",outTradeNo,returnMsg);
                message = Constants.FAIL;
            }
        }catch (Exception e){
            logger.error("订单号，{} 微信关闭订单异常",product.getOutTradeNo(),e);
            message = Constants.FAIL;
        }
        return message;
    }

    @Override
    public void saveBill() {
        try {
            String key = ConfigUtil.API_KEY;
            SortedMap<Object,Object> packageParams = new TreeMap<>();
            ConfigUtil.commonParams(packageParams);
            //ALL，返回当日所有订单信息，默认值SUCCESS：返回当日成功支付的订单，REFUND：返回当日退款订单
            packageParams.put("bill_type","ALL");
            //压缩账单
            //packageParams.put("tar_type","GZIP");
            //账单日期
            //获取两天以前的账单
            //String billDate = DateUtils.getBeforDayDate("2");
            packageParams.put("bill_date","20200416");
            String sign = PayCommonUtil.createSign("UTF-8",packageParams,ConfigUtil.API_KEY);
            packageParams.put("sign",sign);
            String requestXml = PayCommonUtil.getRequestXml(packageParams);
            String resXml = HttpUtil.postData(ConfigUtil.DOWNLOAD_BILL_URL,requestXml);
            if(resXml.startsWith("<xml>")){
                Map map = XMLUtil.doXMLParse(resXml);
                String returnMsg = (String) map.get("return_msg");
                logger.info("微信查询订单失败：{}",returnMsg);
            }else{
                //入库操作
            }
        }catch (Exception e){
            logger.error("微信查询订单异常，{}",e);
        }
    }

    /**
     * SUCCESS  -->> 支付成功
     * REFUND   -->> 转入退款
     * NOTPAY   -->> 未支付
     * CLOSED   -->> 已关闭
     * REVOKED  -->> 已撤销(刷卡支付)
     * USERPAYING -->> 用户支付中
     * PAYERROR -->> 支付失败(其他原因，如银行返回失败等)
     * 支付状态机制请见下单API
     */
    @Override
    public void queryOrder(Product product) {
        try {
            //账号信息
            String key = ConfigUtil.API_KEY;
            SortedMap<Object,Object> packageParam = new TreeMap<>();
            ConfigUtil.commonParams(packageParam);
            packageParam.put("out_trade_no",product.getOutTradeNo());
            String sign = PayCommonUtil.createSign("UTF-8",packageParam,key);
            packageParam.put("sign",sign);

            String requestXml = PayCommonUtil.getRequestXml(packageParam);
            String resXml = HttpUtil.postData(ConfigUtil.CHECK_ORDER_URL,requestXml);
            Map map = XMLUtil.doXMLParse(resXml);
            String returnCode = (String) map.get("return_code");
            if("SUCCESS".equals(returnCode)){
                String resultCode = (String) map.get("result_code");
                if("SUCCESS".equals(resultCode)){
                    String tradeState = (String) map.get("trade_state");
                    logger.info("订单号：{} 查询订单返回结果：{}",product.getOutTradeNo(),tradeState);
                }else{
                    String errCodeDes = (String) map.get("err_code_des");
                    logger.info("订单号：{} 查询订单失败:{}",product.getOutTradeNo(),errCodeDes);
                }
            }else{
                String returnMsg = (String) map.get("return_msg");
                logger.info("订单号：{} 查询订单失败：{}",product.getOutTradeNo(),returnMsg);
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.info("订单号：{} 查询订单异常:{}",product.getOutTradeNo(),e);
        }
    }
}
