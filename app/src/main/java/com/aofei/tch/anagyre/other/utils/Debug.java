package com.aofei.tch.anagyre.other.utils;

/**
 * 可以获取测试打印的日志是第几行,和方法名是什么!
 */
public class Debug {
    /**
     * 该方法可以获得当前Log在类中是第几行
     * */
    public static int line(Exception e) {
        StackTraceElement[] trace = e.getStackTrace();
        if (trace == null || trace.length == 0)
            return -1; //
        return trace[0].getLineNumber();
    }
    public static String fun(Exception e) {
        StackTraceElement[] trace = e.getStackTrace();
        if (trace == null)
            return ""; //
        return trace[0].getMethodName();
    }
}
