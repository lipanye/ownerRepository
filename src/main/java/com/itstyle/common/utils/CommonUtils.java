package com.itstyle.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/03/25 8:56
 * @Decription 算法工具类
 */
public class CommonUtils {
    /**
     * 参数为空格式化
     * @param formatStr
     * @return
     */
    private static String formatStr(String formatStr){
        if(StringUtils.isEmpty(formatStr)){
            formatStr="0.0";
        }
        return formatStr;
    }

    /**
     * 除法
     * @param arg1
     * @param arg2
     * @return
     */
    public static BigDecimal divide(String arg1,String arg2){
        arg1=formatStr(arg1);
        arg2=formatStr(arg2);
        BigDecimal bigDecimal3 = new BigDecimal("0.00");
        if(Double.parseDouble(arg2)!=0){
            BigDecimal bigDecimal1 = new BigDecimal(arg1);
            BigDecimal bigDecimal2 = new BigDecimal(arg2);
            /**
             * divide()，三个参数：第一个参数是除数，第二个参数是保留几位小数，第三个参数是使用的模式
             * 使用的模式：
             * ROUND_UP、ROUND_DOWN、ROUND_CEILING、ROUND_FLOOR、ROUND_HALF_UP、ROUND_HALF_DOWN
             * ROUND_HALF_EVEN 银行家舍入法、ROUND_UNNECESSARY
             * 具体的各模式的区别，参考：
             * https://www.cnblogs.com/yingchen/p/5459501.html
             */
            bigDecimal3 =bigDecimal1.divide(bigDecimal2,2,BigDecimal.ROUND_HALF_EVEN);
        }
        return bigDecimal3;
    }

    /**
     * 乘法
     * @param arg1
     * @param arg2
     * @return
     */
    public static BigDecimal mul(String arg1,String arg2){
        arg1=formatStr(arg1);
        arg2=formatStr(arg2);
        BigDecimal bigDecimal1 = new BigDecimal(arg1);
        BigDecimal bigDecimal2 = new BigDecimal(arg2);
        BigDecimal bigDecimal3 = bigDecimal1.multiply(bigDecimal2);
        return bigDecimal3;
    }
    /**
     * 减法
     * @param arg1
     * @param arg2
     * @return
     */
    public static BigDecimal sub(String arg1,String arg2){
        arg1=formatStr(arg1);
        arg2=formatStr(arg2);
        BigDecimal bigDecimal1 = new BigDecimal(arg1);
        BigDecimal bigDecimal2 = new BigDecimal(arg2);
        BigDecimal bigDecimal3 = bigDecimal1.subtract(bigDecimal2);
        return bigDecimal3;
    }

    /**
     * 加法
     * @param arg1
     * @param arg2
     * @return
     */
    public static BigDecimal add(String arg1,String arg2){
        arg1=formatStr(arg1);
        arg2=formatStr(arg2);
        BigDecimal bigDecimal1 = new BigDecimal(arg1);
        BigDecimal bigDecimal2 = new BigDecimal(arg2);
        BigDecimal bigDecimal3 = bigDecimal1.add(bigDecimal2);
        return  bigDecimal3;
    }

    /**
     *四舍五入保留n位小数，先四舍五入在使用double值自动去零
     * @param arg
     * @param scare
     * @return
     */
    public static String setScare(BigDecimal arg,int scare){
        BigDecimal bigDecimal = arg.setScale(scare,BigDecimal.ROUND_HALF_UP);
        return String.valueOf(bigDecimal.doubleValue());
    }

    /**
     * 四舍五入保留2位小数，先四舍五入再使用double值自动去零
     * @param arg
     * @return
     */
    public static String setDifScare(String arg){
        BigDecimal bigDecimal = new BigDecimal(arg);
        BigDecimal bigDecimal11 =bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);
        return bigDecimal11.toString();
    }

    /**
     * 四舍五入保留N位小数，先四舍五入再使用double值自动去零（String类型参数）
     * @param arg
     * @param i
     * @return
     */
    public static String setDifScare(String arg,int i){
        BigDecimal bigDecimal = new BigDecimal(arg);
        BigDecimal b1 = bigDecimal.setScale(i,BigDecimal.ROUND_HALF_UP);
        return b1.toString();
    }

    /**
     * 转换为百分数 先四舍五入再使用double自动去零
     * @param arg
     * @return
     */
    public static String setFenScare(BigDecimal arg){
        BigDecimal bigDecimal = arg.setScale(3,BigDecimal.ROUND_HALF_UP);
        String scare = String.valueOf(mul(bigDecimal.toString(),"100").doubleValue());
        String fenScare = scare+"%";
        return fenScare;
    }

    /**
     *使用正则表达式去掉多余的.和0
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s){
        if(s.indexOf(".")>0){
            //去掉多余的0
            s = s.replaceAll("0+?$","");
            //如果最后一位是.则去掉
            s = s.replaceAll("[.]$","");
        }
        return s;
    }

}
