package com.itstyle.modules.unionpay.service;

import com.itstyle.common.model.Product;

import java.util.Map;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/17 9:37
 * @Decription
 */
public interface IUnionPayService {
    /**
     * 银联支付
     * @param product
     * @return
     */
    String unionPay(Product product);

    /**
     * 前台回调验证
     * @param validateData
     * @param encoding
     * @return
     */
    String validate(Map<String,String> validateData, String encoding);

    /**
     * 对账单下载
     */
    void fileTransfer();

}
