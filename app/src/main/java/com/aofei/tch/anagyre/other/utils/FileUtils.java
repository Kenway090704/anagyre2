package com.aofei.tch.anagyre.other.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * 本地文件操作工具类
 * Created by xiaokai
 */
public class FileUtils {

    /**
     * SD卡根目录/aofei/cache
     */
    public static final File DIR_CACHE = getDir("cache");
    /**
     * SD卡根目录/aofei/image
     */
    public static final File DIR_IMAGE = getDir("image");
    public static final File DIR_APK = getDir("apk");
    public static final File DIR_DATA = getDir("data");
    public static final File DIR_VIDEO = getDir("video");
    public  static  final  File DIR_LOG=getDir("log");

    /**
     * 获取SDCare根目录
     *
     * @return
     */
    public static File getSDCardDir() {
        String state = Environment.getExternalStorageState();
        // 如果内存卡已挂载
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File storageDirectory = Environment.getExternalStorageDirectory();
            return storageDirectory;
        }

        throw new RuntimeException("没有找到内存卡！");
    }


    /**
     * 在SD根目录下创建应用目录
     *
     * @return
     */
    public static File getAppDir() {
        File file = new File(getSDCardDir(), "aofei");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 创建应用目录下的指定目录
     *
     * @param name
     * @return
     */
    public static File getDir(String name) {

        File file = new File(getAppDir(), name);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 安装apk
     * @param context
     * @param apk
     */
    public static void install(Context context, File apk) {

        Uri uri = Uri.fromFile(apk);
        // 核心是下面几句代码
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


}
