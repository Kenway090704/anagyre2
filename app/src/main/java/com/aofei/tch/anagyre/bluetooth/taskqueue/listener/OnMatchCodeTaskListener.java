package com.aofei.tch.anagyre.bluetooth.taskqueue.listener;

public   interface  OnMatchCodeTaskListener{
        void onMacthing();//正在对码中
        void onMacthedCodeFail(String msg);//对码失败

    /***
     *
     * @param ballInfo  共有十个字节, 0--球号  1-4--球的ID 5-8--球的配件值 9--结束符A5
     */
        void onMacthedCodeSuccessful(int[] ballInfo);//对码成功
        void onMacthedCodeNoResponse();//连接无响应  ---保留,看是否有需要
        void   onMatchCodeFull();//配对已满

    /**
     * 对码结束
     */
    void  onMacthCodeFinished();
    }