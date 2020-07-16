package com.itstyle.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/03/20 15:23
 * @Decription swagger配置类，该类里面应该是固定的，用来设置文档的主要信息
 */
@Configuration
@EnableSwagger2
public class Swagger2 {
    @Bean
    public Docket webApi(){
        //版本类型是swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("支付后台API接口文档")
                //通过调用自定义的apiInfo()方法，来获取文档的主要信息
                .apiInfo(apiInfo())
                .select()
                //扫描改包下面的所有的API注解
                .apis(RequestHandlerSelectors.basePackage("com.itstyle.modules.web"))
                .paths(PathSelectors.any()).build();
    }

    @Bean
    public Docket alipayApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("支付宝API接口文档")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.itstyle.modules.alipay"))
                .paths(PathSelectors.any()).build();
    }

    @Bean
    public Docket wechatpayApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("微信支付API接口文档")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.itstyle.modules.wechatpay"))
                .paths(PathSelectors.any()).build();
    }

    @Bean
    public Docket unionpayApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("银联支付API接口文档")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.itstyle.modules.unionpay"))
                .paths(PathSelectors.any()).build();
    }



    /**
     * 创建该API的基本信息（这些基本信息会展示在文档页面中）
     * 访问地址：http://项目实际地址/swagger-ui.html
     * @return
     */
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder().title("支付系统")
                .description("微信、支付宝、银联支付服务")
                .termsOfServiceUrl("")
                .contact(new Contact("spring pay","","Lipanye_Arthur@163.com"))
                .version("1.0").build();
    }


}
