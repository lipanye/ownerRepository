package com.itstyle.common.config;

import com.alipay.demo.trade.config.Configs;
import com.itstyle.modules.wechatpay.util.ConfigUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/03/23 10:21
 * @Decription 启动加载支付宝、微信、银联相关参数配置
 */
@Component
public class InitPay implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        //初始化 支付宝-微信-银联相关参数，涉及机密此文件不会提交,请自行配置相关参数并加载
        //支付宝
        Configs.init("zfbinfo.properties");
        //微信
        ConfigUtil.init("wechatinfo.properties");
        //SDKConfig.getConfig().loadPropertiesFromSrc();//银联
    }
}
