package com.aofei.tch.anagyre.other.utils.log;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.aofei.tch.anagyre.other.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by HP on 2016/11/28.
 */

public class LogCatch {
    private static final String TAG = "LogCatch";
    private static LogCatch INSTANCE = null;
    private static String PATH_LOGCAT;
    public String FILE_PATH = null;
    private LogDumper mLogDumper = null;
    private int mPId;
    private static String logdir = FileUtils.DIR_LOG.getName();//改为应用名称

    public void init(Context context) {

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
//            PATH_LOGCAT = Environment.getExternalStorageDirectory()
//                    .getAbsolutePath() + File.separator + logdir;
//           LogUtils.d(TAG,"path1 = " + PATH_LOGCAT);

            PATH_LOGCAT = FileUtils.DIR_LOG.getAbsolutePath();//app中的log 包  /storage/sdcard0/aofei/log
            LogUtils.d(TAG, "path1 = " + PATH_LOGCAT);
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
                    + File.separator + logdir;
            LogUtils.d(TAG, "path2 = " + PATH_LOGCAT);
        }
        File file = new File(PATH_LOGCAT);

        if (!file.exists()) {
            file.mkdirs();
            LogUtils.d(TAG, "mkdir");
        } else {
            LogUtils.d(TAG, "dir exists");
        }
    }

    public String GetFileInfo() {
        FILE_PATH = PATH_LOGCAT + "/" + logdir + "-"
                + MyDate.getFileName() + ".log";
        return FILE_PATH;
    }

    public static LogCatch getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogCatch(context);
        }
        return INSTANCE;
    }

    private LogCatch(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
    }

    public void start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
        LogUtils.e(TAG,"将日志写入到sd卡已经执行了");
        mLogDumper.start();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {
        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;
        public LogDumper(String pid, String dir) {
            mPID = pid;
            LogUtils.d(TAG, "Current mPid = " + mPID);
            try {
                out = new FileOutputStream(new File(dir, logdir + "-"
                        + MyDate.getFileName() + ".log"));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
//                e.printStackTrace();
                LogUtils.e(TAG, Log.getStackTraceString(e));

            }

            /**
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *
             * */

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
            //cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";

        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((MyDate.getDateEN() + "  " + line + "\n")
                                .getBytes());
                    }
                }
                LogUtils.d(TAG, "finish write");
            } catch (IOException e) {
                LogUtils.e(TAG, Log.getStackTraceString(e));
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        LogUtils.e(TAG, Log.getStackTraceString(e));
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        /**使用方法*/
                        LogUtils.e(TAG, Log.getStackTraceString(e));
//                        e.printStackTrace();
                    }
                    out = null;
                }

            }
        }
    }
}