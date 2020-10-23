package com.yorhp.commonlibrary.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import log.LogUtils;

/**
 * 作者：Tyhj on 2018/10/24 01:07
 * 邮箱：tyhj5@qq.com
 * github：github.com/tyhjh
 * description：
 */

public class TimeUtil {
    Long currentTime = 0L;

    public void setTime() {
        currentTime = System.currentTimeMillis();
    }

    public void spendTime(String tag) {
        LogUtils.e(tag + "，" + (System.currentTimeMillis() - currentTime));
        setTime();
    }

    public static boolean isTokenOk(long time) {
        long day = (System.currentTimeMillis() - time) / (1000 * 3600 * 24);
        if (day > 10) {
            return false;
        } else {
            return true;
        }
    }


    public static String getTimeTxt(long time) {
        Date date = new Date(time);
        SimpleDateFormat daydf = new SimpleDateFormat("MM月dd日");
        SimpleDateFormat yeardf = new SimpleDateFormat("yyyy年");
        long hour = (System.currentTimeMillis() - time) / (1000 * 3600);
        String day = daydf.format(date);
        String year = yeardf.format(date);
        switch ((int) hour) {
            case 0:
                return "刚刚";
            case 1:
                return "一个小时前";
            case 2:
                return "两个小时前";
            default:
                break;
        }


        if (year.equals(getNowYearTime())) {
            if (day.equals(getNowTime())) {
                return "今天";
            } else if (day.equals(getLastTime())) {
                return "昨天";
            }
            return day;
        } else {
            return year + day;
        }


    }


    public static boolean isToday(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }


    public static String time(Long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        String str = format.format(date);
        return str;
    }

    public static String getTime(Long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }


    //获取当年时间
    public static String getNowYearTime() {
        String time;
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy年");
        time = df.format(now);
        return time;
    }


    //获取当天时间
    public static String getNowTime() {
        String time;
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
        time = df.format(now);
        return time;
    }

    //明天的时间
    public static String getLastTime() {
        String time;
        Date next = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(next);
        calendar.add(calendar.DATE, -1);//把日期往后增加一天.整数往后推,负数往前移动
        next = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
        time = df.format(next);
        return time;
    }

}
