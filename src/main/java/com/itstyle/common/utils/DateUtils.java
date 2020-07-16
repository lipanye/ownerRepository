package com.itstyle.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author Lipanye_Arthur@163.com
 * @Date 2020/03/24 13:41
 * @Decription 操作日期类
 */
public class DateUtils {
    private final static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
    private final static SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat sdfDays = new SimpleDateFormat("yyyyMMdd");
    private final static SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final static Date date = new Date();

    /**
     * 获取YYYY格式
     * @return
     */
    public static String getYear() {return sdfYear.format(date);}

    /**
     * 获取 yyyy-MM-dd 格式
     * @return
     */
    public static String getDay() {return sdfDay.format(date);}

    /**
     * 获取 yyyyMMdd 格式
     * @return
     */
    public static String getDays() {return sdfDays.format(date);}

    /**
     * 获取 yyyy-MM-dd HH:mm:ss 格式
     * @return
     */
    public static String getTime() {return sdfTime.format(date);}

    /**
     *日期比较，如果s>=e，返回true，否则返回false
     * @param s
     * @param e
     * @return
     */
    public static boolean compareDate(String s,String e){
        if(formatDate(s) == null || formatDate(e) == null){
            return false;
        }
        return formatDate(s).getTime()>=formatDate(e).getTime() ;
    }

    /**
     * 格式化日期
     * @param date
     * @return
     */
    public static Date formatDate(String date){
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return  fmt.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *校验日期是否合法
     * @param s
     * @return
     */
    public static boolean isValidDate(String s){
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            fmt.parse(s);
            return true;
        } catch (ParseException e) {
            //如果 throw java.text.ParseException或者NullPointerException 就说明格式不对
            return false;
        }
    }

    public static String getTimeStamp(){
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 得到N天之前的日期
     * @param days
     * @return
     */
    public static String getBeforDayDate(String days) {
        int daysInt = Integer.parseInt(days);

        Calendar calendar = Calendar.getInstance();
        //日期减 如果不够减月份会出现变动
        calendar.add(Calendar.DATE,daysInt);
        Date date = calendar.getTime();
        String dateStr = sdfDays.format(date);
        return dateStr;
    }
}
