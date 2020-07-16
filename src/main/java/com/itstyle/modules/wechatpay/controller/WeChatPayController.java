package com.itstyle.modules.wechatpay.controller;

import com.itstyle.common.constants.Constants;
import com.itstyle.common.model.Product;
import com.itstyle.common.utils.AddressUtils;
import com.itstyle.modules.wechatpay.service.IWeChatPayService;
import com.itstyle.modules.wechatpay.util.ConfigUtil;
import com.itstyle.modules.wechatpay.util.HttpUtil;
import com.itstyle.modules.wechatpay.util.PayCommonUtil;
import com.itstyle.modules.wechatpay.util.XMLUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import java.io.*;
import java.util.*;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/09 9:05
 * @Decription 微信支付
 */
@Api(tags = "微信支付")
@Controller
@RequestMapping(value = "wechatPay")
public class WeChatPayController {
    private static final Logger logger = LoggerFactory.getLogger(WeChatPayController.class);
    @Value("${wechatpay.notify.url}")
    private String notifyUrl;

    @Autowired
    private IWeChatPayService weChatPayService;

    @ApiOperation(value = "二维码支付(模式一) 根据商品ID预先生成二维码")
    @RequestMapping(value = "qcPay1",method = RequestMethod.POST)
    public String qcPay1(Product product, ModelMap modelMap){
        logger.info("二维码支付(模式一)");
        weChatPayService.wechatPay1(product);
        String qrcodePath = "../qrcode/"+product.getProductId()+".png";
        modelMap.addAttribute("qrcodePath",qrcodePath);
        return "wechatpay/qcpay";
    }

    @ApiOperation(value = "二维码支付(模式二)下单并生成二维码")
    @RequestMapping(value = "qcPay2",method = RequestMethod.POST)
    public String qcPay2(Product product,ModelMap modelMap){
        logger.info("二维码支付(模式二)");
        //参数demo
        product.setProductId("202004091123");
        product.setBody("iphone 10w");
        product.setSpbillCreateIp("192.168.10.49");
        String message = weChatPayService.wechatPay2(product);
        if(Constants.SUCCESS.equals(message)){
            String qrcodePath = "../qrcode/"+product.getProductId()+".png";
            modelMap.addAttribute("qrcodePath",qrcodePath);
        }else{
            //失败处理
        }
        return "wechatpay/qcpay";
    }

    /**
     * 支付后台回调
     * @param request
     * @param response
     */
    @ApiOperation(value = "支付后台回调")
    @RequestMapping(value = "pay",method = RequestMethod.POST)
    public void wechat_notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //读取参数
        InputStream inputStream = request.getInputStream();
        StringBuffer sb = new StringBuffer();
        String s;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        while ((s=reader.readLine())!=null){
            sb.append(s);
        }
        reader.close();
        inputStream.close();

        //解析xml成map
        Map<String,String> map = new HashMap<>();
        map = XMLUtil.doXMLParse(sb.toString());

        //过滤空 设置TreeMap
        SortedMap<Object,Object> packageParams = new TreeMap<>();
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()){
            String parameter = (String) iterator.next();
            String parameterValue = map.get(parameter);

            String v = "";
            if(null !=parameterValue){
                v = parameterValue.trim();
            }
            packageParams.put(parameter,v);

        }

        //账号信息
        String key = ConfigUtil.API_KEY;
        //判断签名是否正确
        if(PayCommonUtil.isTenpaySign("UTF-8",packageParams,key)){
            logger.info("微信支付成功回调");
            /*
                处理业务
             */
            String resXml;
            if("SUCCESS".equals(packageParams.get("result_code"))){
                //支付成功
                String orderNo = (String) packageParams.get("out_trade_no");
                logger.info("微信订单号：{} 付款成功",orderNo);
                //这里根据实际的业务场景 做相应的操作
                //通知微信异步确认成功，必写，不然会一直通知后台，八次之后认为交易失败
                resXml="<xml>"+"<return_code><![CDATA[SUCCESS]]></return_code>"+"<return_msg><!CDATA[OK]></return_msg>"+"</xml>";
            }else{
                logger.info("支付失败，错误信息：{}",packageParams.get("err_code"));
                resXml="<xml>"+"<return_code><![CDATA[FAIL]]></return_code>"+"<return_msg><![CDATA[报文为空]]></return_msg>"+"</xml>";
            }
            /*
                处理业务完毕
             */
            BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(resXml.getBytes());
            outputStream.flush();
            outputStream.close();
        }else {
            logger.info("通知签名验证失败");
            throw new RuntimeException("签名验证失败");
        }
    }

    /**
     * 模式一支付回调URL（生成二维码见qrCodeUtil）
     * @param request
     * @param response
     */
    @ApiOperation("模式一支付回调URL")
    @RequestMapping(value = "bizPayUrl",method = RequestMethod.POST)
    public void bizPayUrl(HttpServletRequest request,HttpServletResponse response) throws Exception {
        logger.info("模式一支付回调URL");
        //读取参数
        InputStream inputStream = request.getInputStream();
        StringBuffer stringBuffer = new StringBuffer();
        String s;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        while ((s=reader.readLine())!=null){
            stringBuffer.append(s);
        }
        inputStream.close();
        reader.close();
        //解析xml成map
        Map<String,String> map = new HashMap<>();
        //过滤空 设置TreeMap
        SortedMap<Object,Object> packageParams = new TreeMap<>();
        Iterator iterator = map.keySet().iterator();
        while(iterator.hasNext()){
            String parameter = (String) iterator.next();
            String parameterValue = map.get(parameter);

            String v = "";
            if(null!=parameterValue){
                v = parameterValue;
            }
            packageParams.put(parameter,v);
        }
        //判断签名是否正确
        if(PayCommonUtil.isTenpaySign("UTF-8",packageParams,ConfigUtil.API_KEY)){
            //统一下单
            SortedMap<Object,Object> params = new TreeMap<>();
            ConfigUtil.commonParams(params);
            //生成一个入库，走业务逻辑
            String out_trade_no = Long.toString(System.currentTimeMillis());
            params.put("body","模式一扫码支付");
            params.put("out_trade_no",out_trade_no);
            params.put("total_fee","100");
            params.put("spbill_create_ip", AddressUtils.getIpAddr(request));
            params.put("notify_url",notifyUrl);
            params.put("trade_type","NATIVE");

            String paramsSign = PayCommonUtil.createSign("UTF-8",params,ConfigUtil.API_KEY);
            params.put("sign",paramsSign);
            String requestXml = PayCommonUtil.getRequestXml(params);

            String resXml = HttpUtil.postData(ConfigUtil.UNIFIED_ORDER_URL,requestXml);

            Map<String,String> payResult = XMLUtil.doXMLParse(resXml);
            String returnCode = payResult.get("return_code");
            if("SUCCESS".equals(returnCode)){
                String resultCode = payResult.get("result_code");
                if("SUCCESS".equals(resultCode)){
                    logger.info("订单号：{} 生成微信支付码成功",out_trade_no);

                    String prePayId = payResult.get("prepay_id");
                    SortedMap<Object,Object> prepayParams = new TreeMap<>();
                    ConfigUtil.commonParams(prepayParams);
                    prepayParams.put("prepay_id",prePayId);
                    prepayParams.put("return_code",returnCode);
                    prepayParams.put("result_code",resultCode);

                    String prepaySign = PayCommonUtil.createSign("UTF-8",prepayParams,ConfigUtil.API_KEY);
                    prepayParams.put("sign",prepaySign);
                    String prepayXml = PayCommonUtil.getRequestXml(prepayParams);

                    //通知微信预下单成功
                    BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
                    outputStream.write(prepayXml.getBytes());
                    outputStream.flush();
                    outputStream.close();

                }else{
                    String errCodeDes = payResult.get("err_code_des");
                    logger.info("订单号：{} 生成微信支付码失败:{}",out_trade_no,errCodeDes);
                }
            }else {
                String returnMsg = map.get("return_msg");
                logger.info("订单号：{} 生成微信支付码失败：{}",out_trade_no,returnMsg);
            }
        }else{
            logger.info("签名错误");
        }
    }

    /**
     * 微信退款
     * @param product
     * @param modelMap
     * @return
     */
    @ApiOperation("微信退款")
    @RequestMapping(value = "wechatPayRefund",method = RequestMethod.POST)
    public String wechatPayRefund(Product product,ModelMap modelMap){
        String message = weChatPayService.wechatPayRefund(product);
        modelMap.addAttribute("message",message);
        return "";
    }

    @ApiOperation("微信关闭订单")
    @RequestMapping(value = "wechatCloseOrder",method = RequestMethod.POST)
    public String wechatCloseOrder(Product product,ModelMap modelMap){
        String message = weChatPayService.wechatCloseOrder(product);
        modelMap.addAttribute("message",message);
        return "";
    }

}
