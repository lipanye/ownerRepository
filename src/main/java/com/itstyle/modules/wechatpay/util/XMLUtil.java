package com.itstyle.modules.wechatpay.util;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/07 14:13
 * @Decription XML解析
 */
public class XMLUtil {

    public static Map doXMLParse(String strXml) throws IOException, JDOMException {
        //过滤关键词，防止XXE漏洞攻击
        strXml = filterXXE(strXml);
        strXml = strXml.replaceFirst("encoding=\".*\"","encoding=\"UTF-8\"");
        if(null == strXml || "".equals(strXml)){
            return null;
        }
        Map map = new HashMap();
        InputStream in = new ByteArrayInputStream(strXml.getBytes("UTF-8"));
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(in);
        Element element = document.getRootElement();
        List list = element.getChildren();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            Element e = (Element) iterator.next();
            String key = e.getName();
            String value = "";
            List children = e.getChildren();
            if(children.isEmpty()){
                value = e.getTextNormalize();
            }else{
                value = getChildrenText(children);
            }
            map.put(key,value);
        }
        //关闭流
        in.close();
        return map;
    }

    /**
     * 获取子节点的xml
     * @param children
     * @return
     */
    private static String getChildrenText(List children) {
        StringBuffer buffer = new StringBuffer();
        if(!children.isEmpty()){
            Iterator iterator = children.iterator();
            while (iterator.hasNext()){
                Element element = (Element) iterator.next();
                String name = element.getName();
                String value = element.getTextNormalize();
                List list = element.getChildren();
                buffer.append("<"+name+">");
                if(!list.isEmpty()){
                    buffer.append(getChildrenText(children));
                }
                buffer.append("<"+value+">");
                buffer.append("<"+name+">");
            }
        }
        return buffer.toString();
    }

    /**
     * 通过DOCTYPE和ENTITY来加载本地受保护的文件、替换掉即可
     * 漏洞原理：https://my.oschina.net/u/574353/blog/1841103
        * 防止 XXE漏洞 注入实体攻击
        * 过滤用户提交的XML数据
        * 过滤关键词：<!DOCTYPE 和 <!ENTITY，或者SYSTEM和PUBLIC
     * @param strXml
     * @return
     */
    private static String filterXXE(String strXml) {
        strXml = strXml.replace("DOCTYPE","").replace("SYSTEM","")
                .replace("ENTITY","").replace("PUBLIC","");
        return strXml;
    }
}
