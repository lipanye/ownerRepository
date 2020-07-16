package com.itstyle.common.constants;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/03/23 14:10
 * @Decription 支付途径
 */
public enum PayWay {
    PC("PC,平板",(short)1),MOBILE("手机",(short)2);
    private Short code;
    private String name;

    PayWay(String name,Short code) {
        this.code = code;
        this.name = name;
    }

    public static String getName(Short code,String name){
        for(PayWay c : PayWay.values()){
            if(c.getCode().equals(code)){
                return name;
            }
        }
        return null;
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
