package com.itstyle.common.constants;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/03/23 14:20
 * @Decription 支付的类型
 */
public enum PayType {
    //支付类型
    ALI("支付宝",(short)1),WECHAT("微信",(short)2),UNION("银联",(short)3);
    private Short code;
    private String name;

    PayType(String name,Short code) {
        this.code = code;
        this.name = name;
    }

    public Short getCode() {
        return code;
    }

    public void setCode(Short code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
