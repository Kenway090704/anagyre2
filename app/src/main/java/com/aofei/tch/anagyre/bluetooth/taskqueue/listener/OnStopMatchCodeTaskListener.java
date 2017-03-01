package com.aofei.tch.anagyre.bluetooth.taskqueue.listener;

/**
 * 取消对码任务的一个接口对象
 */
public   interface  OnStopMatchCodeTaskListener{
        void onStopMacthing();//停止对码
        void onStopMacthedCodeFail();//停止对码任务失败
        void onStopMacthedCodeSuccessful();//停止对码任务成功
        void onStopMacthedCodeNoResponse();//对码任务无响应 ---保留,看是否有需要
    }