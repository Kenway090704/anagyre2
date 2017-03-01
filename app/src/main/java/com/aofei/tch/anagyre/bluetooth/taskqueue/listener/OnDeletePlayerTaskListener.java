package com.aofei.tch.anagyre.bluetooth.taskqueue.listener;

/**
 * 删除好友任务的接口对象
 */
public   interface  OnDeletePlayerTaskListener{
        void onDeletePlayering();//正在删除中
        void onDeletePlayerFail();//删除失败
        void onDeletePlayerSuccessful(int  deleteNumber);//删除成功
        void onDeletePlayerNoResponse();//连接无响应  ---保留,看是否有需要
    }