package com.aofei.tch.anagyre.other.utils.log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

/**
 * 获得日期类
 * Created by HP on 2016/11/28.
 */

public class MyDate {

    private static final String TAG = "MyDate";

    public static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;
    }

    public static String getDateEN() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = format1.format(new Date(System.currentTimeMillis()));
        return date1;
    }

    public static String getDateHMS() {
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
        String date1 = format1.format(new Date(System.currentTimeMillis()));
        return date1;
    }

    /**
     * 获取该时间的 "yyyy-MM-dd"
     *
     * @param time
     * @return "yyyy-MM-dd"
     */
    public static String getTimeStr(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(time));
        return date;
    }

    /**
     * 获取几天前的日期
     *
     * @param day --几天  0=<day<=30
     * @return yyyy-MM-dd
     */
    public static String getBefomeSomeDay(int day) {
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);//获取当前年
        int month = cal.get(Calendar.MONTH) + 1;//获取当前月
        int nowday = cal.get(Calendar.DAY_OF_MONTH);//获取当前日
        cal.set(Calendar.DAY_OF_YEAR, nowday - day);
        int beforeDay = cal.get(Calendar.DAY_OF_MONTH);
        int beforeMonth = month;
        int beforeYear = year;
        //判断前面的日期如果大于当前日期,那么月分减一
        if (beforeDay > nowday) {
            beforeMonth -= 1;
        }
        //判断前面的月如果大于当前月,那么年份分减一
        if (beforeMonth > month) {
            beforeYear -= 1;
        }
        cal.set(beforeYear, beforeMonth - 1, beforeDay);
        Date date = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateBefore = format.format(date);
        return dateBefore;
    }

}

