package com.aofei.tch.anagyre.bluetooth.taskqueue.base;

import android.util.Log;

import com.aofei.tch.anagyre.other.utils.log.LogUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * 任务管理
 * @作者： HP 创建于on 2016/11/23.
 * @邮箱：xiaokai090704@126.com
 */
public class  RequestTaskManager
{
    private  static  final String TAG="RequestTaskManager";
    //请求线程队列
    private LinkedList<RequestTask> requestTasks;
    //任务不能重复
    private Set<String> taskIdSet;

    private  static  RequestTaskManager requestTaskManager;
    //使用单例模式实现任务管理类
    public  static  synchronized  RequestTaskManager getInstance(){
        if (null==requestTaskManager){
            requestTaskManager=new RequestTaskManager();
        }
        return  requestTaskManager;
    }
    private  RequestTaskManager(){
        requestTasks=new LinkedList<RequestTask>();
        taskIdSet=new HashSet<String>();
    }

    /**
     * 添加任务
     * 如果添加的任务在列表中已经存在，则不会再添加
     * @param requestTask
     */
    public  void addRequestTask(RequestTask requestTask){
        synchronized (requestTasks){
            if (!isTaskRepeat(requestTask.getTaskName())){
                LogUtils.e(TAG,requestTask.getTaskName()+"任务添加到队列");
                requestTasks.addLast(requestTask);
            }
        }
    }

    /**
     * 判断任务是否重复
     * @param name
     * @return ture---重复  false---不重复
     */
    public  boolean isTaskRepeat(String name){
        synchronized (taskIdSet){
            if (taskIdSet.contains(name)){
                return true;
            }else {
                Log.e(TAG,"请求管理器增加请求任务"+name);
                taskIdSet.add(name);
                return false;
            }
        }
    }

    public  RequestTask getRequestTask(){
        synchronized (requestTasks){
            if (requestTasks.size()>0){
                RequestTask requestTask = requestTasks.removeFirst();
                taskIdSet.remove(requestTask.getTaskName());//当取出任务的时候,将其移除
                Log.e(TAG,"请求管理器取出任务"+requestTask.getTaskName());
                return  requestTask;
            }
        }
        return  null;
    }
}
