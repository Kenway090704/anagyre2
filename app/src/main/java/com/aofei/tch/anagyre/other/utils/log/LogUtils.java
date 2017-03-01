package com.aofei.tch.anagyre.other.utils.log;

import android.util.Log;

/**
 * 打印log日志的工具类
 * Created by Xiaokao on 2016/11/17
 */
public class LogUtils {
    public static final boolean isDebug = true;//默认是true,发布正式班的时候要将其变为false
    /**
     * 错误级别
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }
    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }
    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg);
        }
    }
    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg);
        }
    }
}
