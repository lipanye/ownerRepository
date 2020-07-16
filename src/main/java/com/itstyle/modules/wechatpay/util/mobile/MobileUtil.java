package com.itstyle.modules.wechatpay.util.mobile;

import com.google.gson.Gson;
import com.itstyle.modules.wechatpay.util.ConfigUtil;
import org.apache.http.HttpConnection;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/04/03 16:45
 * @Decription 微信H5支付工具类
 */
public class MobileUtil {

    /**
     * 获取用户Openid
     * @param code
     * @return
     */
    public static String getOpenId(String code) {
        if(code != null){
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token?"
                    + "appid="+ ConfigUtil.APP_ID
                    + "&secret="+ ConfigUtil.APP_SECRET + "&code="
                    +code + "&grant_type=authorization_code";
            String returnData = getReturnData(url);
            Gson gson = new Gson();
            OpenIdClass openIdClass = gson.fromJson(returnData,OpenIdClass.class);
            if(openIdClass.getOpenid()!=null){
                return openIdClass.getOpenid();
            }
        }
        return "";
    }

    private static String getReturnData(String urlString) {
        String res = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                    "utf-8"));
            String line;
            if((line = reader.readLine())!=null){
                res+=reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 回调 request 参数解析为map格式
     * @param request
     * @return
     */
    public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        //将解析结果存储在HashMap
        Map<String,String> map = new HashMap<>();
        //读取输入流
        InputStream inputStream = request.getInputStream();
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        //得到xml根元素
        Element root = document.getRootElement();
        //得到根元素的所有子节点
        List<Element> elementList = root.elements();
        for (Element element: elementList) {
            map.put(element.getName(),element.getText());
        }
        //释放资源
        inputStream.close();
        inputStream = null;
        return map;
    }
}
