package com.itstyle.common.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/03/23 13:31
 * @Decription
 */
@Controller
public class IndexController {

    /**
     *页面跳转
     * @param url
     * @return
     */
    @RequestMapping("{url}.shtml")
    public String page(@PathVariable("url") String url){
        return url;
    }

    /**
     * 页面跳转一级目录k
     * @param module
     * @param url
     * @return
     */
    @RequestMapping("{module}/{url}.shtml")
    public String page(@PathVariable("module") String module,@PathVariable("url") String url){
        return module+"/"+url;
    }

    /**
     * 页面跳转二级目录
     * @param module
     * @param sub
     * @param url
     * @return
     */
    @RequestMapping("{module}/{sub}/{url}.shtml")
    public String page(@PathVariable("module") String module,@PathVariable("sub") String sub,
                       @PathVariable("url") String url){
        return module+"/"+sub+"/"+url;
    }


}
