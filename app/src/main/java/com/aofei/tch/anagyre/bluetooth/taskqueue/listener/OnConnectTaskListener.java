package com.aofei.tch.anagyre.bluetooth.taskqueue.listener;

import java.io.InputStream;
import java.io.OutputStream;

/**
     * 连接适配器的一个接口对象
     */
    public interface OnConnectTaskListener {
        void onConecting();//正在与适配器进行连接

        void onConnectedFail();//连接失败

        void onConnectedSuccessful(OutputStream os, InputStream is);//连接成功

        void onConnectedNoResponse();//连接无响应  ---保留,看是否有需要

       /**
        * 获取已经保存的玩家信息
        * @param ballInfo  玩家信息
       */
        void  onConnectedPlayers(int[] ballInfo);
    }