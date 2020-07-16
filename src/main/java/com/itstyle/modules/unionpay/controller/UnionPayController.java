package com.itstyle.modules.unionpay.controller;

import com.itstyle.common.constants.PayWay;
import com.itstyle.common.model.Product;
import com.itstyle.modules.unionpay.service.IUnionPayService;
import com.itstyle.modules.unionpay.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/16 16:40
 * @Decription 银联支付
 */
@Api(tags = "银联支付")
@Controller
@RequestMapping(value = "unionpay")
public class UnionPayController {
    private static final Logger logger = LoggerFactory.getLogger(UnionPayController.class);

    @Autowired
    private IUnionPayService unionPayService;

    @ApiOperation(value = "电脑支付")
    @RequestMapping(value = "pcPay",method = RequestMethod.POST)
    public String pcPay(Product product, ModelMap modelMap){
        logger.info("电脑支付");
        product.setPayWay(PayWay.PC.getCode());
        String form = unionPayService.unionPay(product);
        modelMap.addAttribute("form",form);
        return "unionpay/pay";
    }

    @ApiOperation(value = "手机H5支付")
    @RequestMapping(value = "mobilePay",method = RequestMethod.POST)
    public String mobilePay(Product product,ModelMap modelMap){
        logger.info("手机H5支付");
        product.setPayWay(PayWay.MOBILE.getCode());
        String form = unionPayService.unionPay(product);
        modelMap.addAttribute("form",form);
        return "unionpay/pay";
    }

    /**
     * 银联支付回调，通知我们是否成功
     * @param request
     * @param response
     */
    @ApiOperation(value = "银联回调通知")
    @RequestMapping(value = "pay",method = RequestMethod.POST)
    public void union_notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("银联接收后台通知开始");
        String encoding = request.getParameter(SDKConstants.param_encoding);
        //获取银联通知服务器发送的后台通知参数
        Map<String,String> requestParam = getAllRequestParam(request);
        //打印参数
        logger.info(requestParam.toString());
        Map<String,String> valideDate = null;
        if(null != requestParam && !requestParam.isEmpty()){
            Iterator<Map.Entry<String,String>> it = requestParam.entrySet().iterator();
            valideDate = new HashMap<>(requestParam.size());
            while (it.hasNext()){
                Map.Entry<String,String> e = it.next();
                String key = e.getKey();
                String value = new String(e.getValue().getBytes(encoding),encoding);
                valideDate.put(key,value);
            }
        }
        //验证签名前不要修改requestParam中的键值对的内容,否则会验签不通过
        if(!AcpService.validate(valideDate,encoding)){
            logger.info("银联验证签名结果[失败]");
        }else{
            logger.info("银联验证签名结果[成功]");
            //订单号
            String outTradeNo = valideDate.get("orderId");
            //辅助信息（字段穿透）
            String reqReserved = valideDate.get("reqReserved");
            logger.info("处理相关业务逻辑，{},{}",outTradeNo,reqReserved);
            //返回给银联服务器http 200 状态码
            response.getWriter().print("ok");
        }
    }

    /**
     * 获取请求参数中的详细信息
     * @param request
     * @return
     */
    private Map<String, String> getAllRequestParam(final HttpServletRequest request) {
        Map<String,String> res = new HashMap<>();
        Enumeration<?> parameterNames = request.getParameterNames();
        if(null != parameterNames){
            while (parameterNames.hasMoreElements()){
                String key = (String) parameterNames.nextElement();
                String value = request.getParameter(key);
                res.put(key,value);
                //在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                if(null == res.get(key) || "".equals(res.get(key))){
                    res.remove(key);
                }
            }
        }
        return res;
    }


}
