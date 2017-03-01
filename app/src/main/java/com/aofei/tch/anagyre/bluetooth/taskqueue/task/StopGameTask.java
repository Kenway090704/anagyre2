package com.aofei.tch.anagyre.bluetooth.taskqueue.task;

import com.aofei.tch.anagyre.bluetooth.taskqueue.base.RequestTask;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnStopGameTaskListener;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 停止任务
 * Created by kenway on 16/11/30 16:37
 * Email : xiaokai090704@126.com
 */

public class StopGameTask  extends RequestTask{

    private  static  final  String TAG="StopGameTask";
    byte[] stopGameCode={(byte)0xAA,(byte)0xA6,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    private OutputStream os=null;
    private InputStream is=null;
    public StopGameTask(String name, OutputStream os, InputStream is) {
        super(name);
        this.os=os;
        this.is=is;
    }
    private OnStopGameTaskListener onStopGameTaskListener;
    public  void   setOnStopGameTaskListener(OnStopGameTaskListener onStopGameTaskListener){
        this.onStopGameTaskListener=onStopGameTaskListener;
    }
    @Override
    public void run() {

        if (os != null && is != null) {
            try {
                os.write(stopGameCode);
                LogUtils.e(TAG,"结束比赛命令写入到适配中...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }





    }




}
