package com.aofei.tch.anagyre.bluetooth.taskqueue.base;

import android.util.Log;

/**
 *
 * 任务类，表示执行元素
 * @作者： HP 创建于on 2016/11/23.
 * @邮箱：xiaokai090704@126.com
 */
public abstract class RequestTask implements Runnable
{
    private static  final String  TAG="RequestTask";
    public  String name;

    public  RequestTask(String name){
        this.name=name;
    }

    /**获取任务的Name*/
    public  String getTaskName(){
        return name;
    }
}
