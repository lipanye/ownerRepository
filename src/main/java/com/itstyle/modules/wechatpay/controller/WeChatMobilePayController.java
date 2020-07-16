package com.itstyle.modules.wechatpay.controller;

import com.itstyle.common.model.Product;
import com.itstyle.common.utils.AddressUtils;
import com.itstyle.common.utils.DateUtils;
import com.itstyle.modules.wechatpay.service.IWeChatPayService;
import com.itstyle.modules.wechatpay.util.ConfigUtil;
import com.itstyle.modules.wechatpay.util.HttpUtil;
import com.itstyle.modules.wechatpay.util.PayCommonUtil;
import com.itstyle.modules.wechatpay.util.XMLUtil;
import com.itstyle.modules.wechatpay.util.mobile.MobileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/03 14:28
 * @Decription 微信H5支付
 */
@Api(tags = "微信H5支付")
@Controller
@RequestMapping(value = "wechatMobile")
public class WeChatMobilePayController {
    private static final Logger logger = LoggerFactory.getLogger(WeChatMobilePayController.class);

    @Autowired
    private IWeChatPayService weChatPayService;

    @Value("${server.context.url}")
    private String serverUrl;


    @ApiOperation("H5支付（需要公众号内支付）")
    @RequestMapping(value = "pay",method = RequestMethod.POST)
    public String pay(Product product, ModelMap modelMap){
        logger.info("H5支付(需要公众号内支付)");
        String url = weChatPayService.weChatPayMobile(product);
        return "redirect:"+url;
    }

    @ApiOperation(value = "公众号H5支付主页")
    @RequestMapping(value = "payPage",method = RequestMethod.GET)
    public String pay(HttpServletRequest request,HttpServletResponse response){
        return "wechatpay/payPage";
    }

    @ApiOperation(value = "纯H5支付（不建议在APP端使用）")
    @RequestMapping(value = "h5pay",method = RequestMethod.POST)
    public String h5pay(Product product){
        logger.info("纯H5支付（不建议在APP端使用）");
        String mweb_url = weChatPayService.wechatPayH5(product);
        if(StringUtils.isNotBlank(mweb_url)){
            return "redirect:"+mweb_url;
        }else{
            return "redirect:/wechatMobile/error";
        }
    }

    @ApiOperation(value = "小程序支付(需要HTTPS)")
    @RequestMapping(value = "smallRoutine",method = RequestMethod.POST)
    public String smallRoutine(Product product){
        logger.info("小程序支付(需要HTTPS)，不需要支付目录和授权域名");
        String url =weChatPayService.weChatPayMobile(product);
        return "redirect:"+url;
    }

    @ApiOperation("预下单")
    @RequestMapping(value = "dopay",method = RequestMethod.POST)
    public String dopay(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String orderNo = request.getParameter("outTradeNo");
        String totalFee = request.getParameter("totalFee");
        //获取code 在微信调用时会自动加上这个参数，无需设置
        String code = request.getParameter("code");
        //获取用户openid(jsapi必须c传openid)
        String openid = MobileUtil.getOpenId(code);
        //回调接口
        String notifyUrl = serverUrl+"/wechatMobile/WXPayBack";
        //交易类型H5支付，也可以是小程序支付参数
        String tradeType = "JSAPI";
        SortedMap<Object,Object> packageParams = new TreeMap<>();
        ConfigUtil.commonParams(packageParams);
        //商品描述
        packageParams.put("body","报告");
        //商户订单号
        packageParams.put("out_trade_no",orderNo);
        //总金额
        packageParams.put("total_fee",totalFee);
        //发起人IP地址
        packageParams.put("spbill_create_ip", AddressUtils.getIpAddr(request));
        //回调地址
        packageParams.put("notify_url",notifyUrl);
        //交易类型
        packageParams.put("trade_type",tradeType);
        //用户openId
        packageParams.put("openid",openid);
        //签名
        String sign = PayCommonUtil.createSign("UTF-8",packageParams,ConfigUtil.API_KEY);
        packageParams.put("sign",sign);
        String requestXml = PayCommonUtil.getRequestXml(packageParams);
        String resXml = HttpUtil.postData(ConfigUtil.UNIFIED_ORDER_URL,requestXml);
        Map map = XMLUtil.doXMLParse(resXml);
        String returnCode = (String) map.get("return_code");
        String returnMsg = (String) map.get("return_msg");
        StringBuffer buffer = new StringBuffer();
        if("SUCCESS".equals(returnCode)){
            String resultCode = (String) map.get("result_code");
            String errCodeDes = (String) map.get("err_code_des");
            if("SUCCESS".equals(resultCode)){
                //获取预支付交易会话标识
                String prepayId = (String) map.get("prepay_id");
                String packages = "prepay_id="+prepayId;
                SortedMap<Object,Object> finalPackage = new TreeMap<>();
                String timestamp = DateUtils.getTimeStamp();
                String nonceStr = packageParams.get("nonce_str").toString();
                finalPackage.put("appId",ConfigUtil.APP_ID);
                finalPackage.put("timeStamp",timestamp);
                finalPackage.put("nonceStr",nonceStr);
                finalPackage.put("package",packages);
                finalPackage.put("signType","MD5");
                //这里很重要 参数一定要正确 狗日的腾讯 参数到这里就成大写了
                //可能报错信息(支付验证签名失败，get_brand_wcpay_request:fail)
                sign = PayCommonUtil.createSign("UTF-8",finalPackage,ConfigUtil.API_KEY);
                buffer.append("redirect:/wechatMobile/payPage?");
                buffer.append("timeStamp="+timestamp+"&nonceStr="+nonceStr+"&package="+packages);
                buffer.append("&signType=MD5"+"&paySign="+sign+"&appid="+ConfigUtil.APP_ID);
                buffer.append("&orderNo="+orderNo+"&totalFee="+totalFee);
            }else {
                logger.info("订单号:{},错误信息:{}",orderNo,errCodeDes);
                buffer.append("redirect:/wechatMobile/error?code=0&orderNo="+orderNo); //该订单已支付
            }
        }else {
            logger.info("订单号:{},错误信息:{}",orderNo,returnMsg);
            buffer.append("redirect:/wechatMobile/error?code=1&orderNo="+orderNo);//系统错误
        }
        return buffer.toString();
    }

    /**
     * 手机支付完成回调
     * @param request
     * @param response
     */
    @ApiOperation(value = "手机支付完成回调")
    @RequestMapping(value = "wechatPayBack",method = RequestMethod.POST)
    public void wechatPayBack(HttpServletRequest request,HttpServletResponse response){
        String resXml="";
        //解析XML
        try {
            Map<String,String> map = MobileUtil.parseXml(request);
            String return_code = map.get("return_code");
            String out_trade_no = map.get("out_trade_no");
            if("SUCCESS".equals(return_code)){
                if(out_trade_no != null){
                    //处理订单逻辑
                    logger.info("微信手机支付回调成功订单号：{}",out_trade_no);
                    resXml = "<xml>"+"<return_code><![CDATA[SUCCESS]]></return_code>"+"<return_msg><![CDATA[OK]]></return_msg>"+"</xml>";
                }
            }else{
                logger.info("微信手机支付回调失败订单号：{}",out_trade_no);
                resXml="<xml>"+"<return_code><![CDATA[FAIL]]></return_code>"+"<return_msg><![CDATA[报文为空]]></return_msg>"+"</xml>";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("手机支付回调通知失败",e);
            resXml="<xml>"+"<return_code><![CDATA[FAIL]]></return_code>"+"<return_msg><![CDATA[报文为空]]></return_msg>"+"</xml>";
        }
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(resXml.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
