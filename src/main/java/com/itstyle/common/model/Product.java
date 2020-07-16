package com.itstyle.common.model;

import java.io.Serializable;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/03/23 15:21
 * @Decription产品订单信息
 */
public class Product implements Serializable {
    private static final long serialVersionUID = -5992196282041232149L;
    private String productId;//商品ID
    private String subject; //订单名称
    private String body;//商品描述
    private String totalFee;//总金额(单位是分)
    private String outTradeNo;//订单号(唯一)
    private String spbillCreateIp;//发起人IP地址
    private String attach;//附件数据主要用于商户携带订单的自定义数据
    private String payType;//支付类型(1-支付宝 2-微信 3-银联)
    private Short payWay;//支付方式(1-PC、平板 2-手机)
    private String frontUrl;//前台回调地址，非扫码直接使用
    private String refundReason; //退款原因
    public Product() {
    }

    public Product(String productId, String subject, String body, String totalFee,
                   String outTradeNo, String spbillCreateIp, String attach, String payType,
                   Short payWay, String frontUrl,String refundReason) {
        this.productId = productId;
        this.subject = subject;
        this.body = body;
        this.totalFee = totalFee;
        this.outTradeNo = outTradeNo;
        this.spbillCreateIp = spbillCreateIp;
        this.attach = attach;
        this.payType = payType;
        this.payWay = payWay;
        this.frontUrl = frontUrl;
        this.refundReason = refundReason;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getSpbillCreateIp() {
        return spbillCreateIp;
    }

    public void setSpbillCreateIp(String spbillCreateIp) {
        this.spbillCreateIp = spbillCreateIp;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Short getPayWay() {
        return payWay;
    }

    public void setPayWay(Short payWay) {
        this.payWay = payWay;
    }

    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }
}
