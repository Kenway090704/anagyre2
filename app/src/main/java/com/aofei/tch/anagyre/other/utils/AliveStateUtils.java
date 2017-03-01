package com.aofei.tch.anagyre.other.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 *  判断app是否在后台
 * Created by kenway on 16/11/21 14:20
 * Email : xiaokai090704@126.com
 */

public class AliveStateUtils {

    /**
     * 程序是否在前台运行
     *
     * @return true--是前台  false---后台
     */
    public static boolean isAppOnForeground(Context context) {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }
}
