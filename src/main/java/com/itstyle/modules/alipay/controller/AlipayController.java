package com.itstyle.modules.alipay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.itstyle.common.constants.Constants;
import com.itstyle.common.model.Product;
import com.itstyle.modules.alipay.service.IAliPayService;
import com.itstyle.modules.alipay.util.AliPayConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/03/25 14:08
 * @Decription 支付宝支付Controller
 */
@Api(tags = "支付宝支付")
@Controller
@RequestMapping(value = "alipay")
public class AlipayController {
    private static final Logger logger = LoggerFactory.getLogger(AlipayController.class);

    @Autowired
    private IAliPayService aliPayService;

    @ApiOperation("电脑支付")
    @RequestMapping(value = "pcPay",method = RequestMethod.GET)
    public String pcPay(Product product, ModelMap modelMap){
        logger.info(">>>>>>>>>>>电脑支付");
        String form = aliPayService.aliPayPc(product);
        modelMap.addAttribute("form",form);
        return "alipay/pay";
    }

    @ApiOperation("H5支付")
    @RequestMapping(value = "mobilePay",method = RequestMethod.GET)
    public String mobilePay(Product product,ModelMap modelMap){
        logger.info(">>>>>>>>H5支付");
        String form = aliPayService.aliPayMobileH5(product);
        modelMap.addAttribute("form",form);
        return "alipay/pay";
    }

    @ApiOperation("二维码支付")
    @RequestMapping(value = "qrcodePay",method = RequestMethod.POST)
    public String qrcodePay(Product product,ModelMap modelMap){
        logger.info(">>>>>>>>>>二维码支付");
        String message = aliPayService.aliPayQrCode(product);
        if(Constants.SUCCESS.equals(message)){
            String img ="../qrcode/"+product.getOutTradeNo()+".png";
            modelMap.addAttribute("img",img);
        }
        return"alipay/qcpay";
    }

    @ApiOperation("APP支付服务端")
    @RequestMapping(value = "appPay",method = RequestMethod.POST)
    public String appPay(Product product,ModelMap modelMap){
        String orderString = aliPayService.appPay(product);
        modelMap.addAttribute("orderString",orderString);
        return "alipay/pay";
    }

    /**
     * 支付宝支付后台回调(二维码、H5、网站)
     * @param request
     * @param response
     */
    @ApiOperation("支付宝支付回调(二维码、H5、网站)")
    @RequestMapping(value = "pay",method = RequestMethod.POST)
    public void alipay_notify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String message = Constants.SUCCESS;
        Map<String,String> params = new HashMap<>();
        //取出所有参数验证签名
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()){
            String parameterName = parameterNames.nextElement();
            params.put(parameterName,request.getParameter(parameterName));
        }
        //验证签名，校验签名
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, Configs.getAlipayPublicKey(),
                    AliPayConfig.CHARSET,AliPayConfig.SIGN_TYPE);
            //2018/01/26之后新建应用只支持RSA2签名方式，目前已使用RSA签名的应用仍然可以正常调用接口
            //可能使用这个API会导致验签失败，特此记录一下
            //以下是正式环境
            //signVerified = AlipaySignature.rsaCheckV2(params,Configs.getAlipayPublicKey(),AliPayConfig.CHARSET);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            message = Constants.FAIL;
        }
        if(signVerified){
            logger.info("支付宝验证签名成功！");
            //若参数中的appid和填入的appid不相同，抛出异常
            if(!Configs.getAppid().equals(params.get("app_id"))){
                message = Constants.FAIL;
                throw new RuntimeException("appId不一致");
            }else{
                String outTradeNo = params.get("out_trade_no");
                //此处查找订单号对应的订单，并将其金额与数据库中的金额对比，如果对不上也抛出异常
                String status = params.get("trade_status");
                if(status.equals("WAIT_BUY_PAY")){
                    //如果订单状态是正在等待用户付款
                    logger.info("订单号为：{} 的订单当前正在等待用户付款",outTradeNo);
                }else if(status.equals("TRADE_CLOSED")){
                    //如果状态是未付款交易超时关闭或支付完成后全额退款
                    logger.info("订单号为：{} 的订单已被关闭",outTradeNo);
                }else if(status.equals("TRADE_SUCCESS") || status.equals("TRADE_FINISHED")){
                    //如果状态是已经支付成功
                    logger.info("订单号为：{} 的订单成功支付",outTradeNo);
                    //将数据库中的记录更新为成功支付的状态
                }else{

                }
            }
        }else{//如果签名没有验证通过
            message = Constants.FAIL;
            logger.info("签名验签失败！");
        }
        BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        outputStream.write(message.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 支付宝支付PC端前端回调
     * @param request
     * @return
     */
    @ApiOperation("支付宝支付前端回调")
    @RequestMapping("frontRcvResponse")
    public String frontRcvResponse(HttpServletRequest request){
        try {
            //获取支付宝GET过来的反馈信息
            Map<String,String> params = new HashMap<>();
            Map<String,String[]> requestParam = request.getParameterMap();
            for (Iterator<String> iter = requestParam.keySet().iterator();iter.hasNext();){
                String name = iter.next();
                String[] values = requestParam.get(name);
                String valueStr = "";
                for(int i=0;i<values.length;i++){
                    valueStr = (i==values.length-1)?valueStr+values[i]
                            : valueStr+values[i]+",";
                }
                //如果出现乱码，使用下面这行代码
                valueStr = new String(valueStr.getBytes("ISO-8859-1"),"UTF-8");
                params.put(name,valueStr);
            }
            //商户订单号
            String orderNo = new String(request.getParameter("out_trade_no")
                    .getBytes("ISO-8859-1"),"UTF-8");
            //前台回调验证签名 v1 or v2
            boolean signVerified = aliPayService.rsaCheckV1(params);
            if(signVerified){
                logger.info("订单号：{} 验证签名结果成功",orderNo);
                //执行业务逻辑
            }else{
                logger.info("订单号：{} 验证签名结果失败",orderNo);
            }
        }catch (Exception e){
            e.printStackTrace();
            //处理异常信息
        }
        //支付成功，跳转到成功页面
        return "success.html";
    }

    /**
     * 支付宝退款
     * @param product
     * @return
     */
    @ApiOperation("支付宝退款")
    @RequestMapping(value = "aliRefund",method = RequestMethod.POST)
    public String aliRefund(Product product,ModelMap modelMap){
        String message = aliPayService.aliRefund(product);
        modelMap.addAttribute("message",message);
        return "aliRefund.html";
    }

    /**
     * 支付宝关闭订单
     * @param product
     * @param modelMap
     * @return
     */
    @ApiOperation("关闭订单")
    @RequestMapping(value = "aliCloseOrder",method = RequestMethod.POST)
    public String aliCloseOrder(Product product,ModelMap modelMap){
        String message = aliPayService.aliCloseOrder(product);
        modelMap.addAttribute("message",message);
        return "aliCloseOrder.html";
    }

    /**
     * 获取下载对账单的地址
     * @param billDate
     * @param billType
     * @return
     */
    @ApiOperation("下载对账单")
    @RequestMapping(value = "downloadBillUrl",method = RequestMethod.POST)
    @ResponseBody
    public String downloadBillUrl(String billDate,String billType){
        String message = aliPayService.downloadBillUrl(billDate,billType);
        return message;
    }



}
