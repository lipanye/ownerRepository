package com.itstyle.modules.wechatpay.service;

import com.itstyle.common.model.Product;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/03 15:00
 * @Decription
 */
public interface IWeChatPayService {
    /**
     * 公众号支付，返回一个url地址
     * @param product
     * @return
     */
    String weChatPayMobile(Product product);

    /**
     * H5支付 唤醒微信App 进行支付
     * 申请入口：登陆商户平台-->产品中心-->我的产品-->支付产品-->h5支付
     * @param product
     * @return
     */
    String wechatPayH5(Product product);

    /**
     * 微信支付下单(模式一)
     * @param product
     */
    void wechatPay1(Product product);

    /**
     * 微信支付下单（模式二）
     * @param product
     * @return
     */
    String wechatPay2(Product product);

    /**
     * 微信支付退款
     * @param product
     * @return
     */
    String wechatPayRefund(Product product);

    /**
     * 关闭订单
     * @param product
     * @return
     */
    String wechatCloseOrder(Product product);

    /**
     * 下载微信账单
     */
    void saveBill();

    /**
     * 查询订单
     */
    void queryOrder(Product product);
}
