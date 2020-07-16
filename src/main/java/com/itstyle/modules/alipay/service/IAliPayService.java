package com.itstyle.modules.alipay.service;

import com.itstyle.common.model.Product;

import java.util.Map;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/03/25 14:13
 * @Decription 扫码支付以及H5手机支付
 */
public interface IAliPayService {
    /**
     *阿里支付预下单
     *如果你调用的是当面付预下单接口(alipay.trade.precreate)，
     * 调用成功后订单实际上是没有生成，因为创建一笔订单买家、卖家、金额三要素
     *预下单并没有创建订单，所以根据商户订单号操作订单，比如查询或者关闭，会报错订单不存在
     *当用户扫码后订单才会创建，用户扫码之前二维码有效期2小时，扫码之后有效期根据timeout_express时间指定
     * 2018年起，扫码支付申请需要门店拍照等等，申请流程复杂了
     * @param product
     * @return
     */
    String aliPayQrCode(Product product);

    /**
     * 手机H5支付、腾讯相关软件下不支持，使用UC等浏览器打开
     * 方法一：
     *  对于页面跳转类API，SDK不会也无法像系统调用类API一样自动请求支付宝并获得结果，而是在接受request请求对象后
     *  为开发者生成前台页面请求需要的完整form表单的html（包含自动提交脚本），商户直接将这个表单的String
     *  输出到http response中即可
     * 方法二：
     *  如果是远程调用返回消费放一个form表单，然后调用方刷新到页面自动提交即可
     * @param product
     * @return
     */
    String aliPayMobileH5(Product product);

    /**
     * 网站支付
     * @param product
     * @return
     */
    String aliPayPc(Product product);

    /**
     *  app支付
     * @param product
     * @return
     */
    String appPay(Product product);

    /**
     * 验证签名1
     * @param params
     * @return
     */
    boolean rsaCheckV1(Map<String, String> params);

    /**
     * 验证签名2
     * @param params
     * @return
     */
    boolean rsaCheckV2(Map<String, String> params);

    /**
     * 支付宝退款
     * @param product
     * @return
     */
    String aliRefund(Product product);

    /**
     * 关闭订单
     * @param product
     * @return
     */
    String aliCloseOrder(Product product);

    /**
     * 下载对账单x
     * @param billDate(账单时间：日账单格式为yyyy-MM-dd，月账单格式为yyyy-MM)
     * @param billType(trade、signcustomer：trade指商户基于支付宝交易收单的业务账单；signcustomer是指基于商户支付宝余额收入及支出等资金变动的业务账单)
     * @return
     */
    String downloadBillUrl(String billDate, String billType);
}
