package com.aofei.tch.anagyre.bluetooth.taskqueue.base;


import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 任务管理线程
 * @作者： HP 创建于on 2016/11/23.
 * @邮箱：xiaokai090704@126.com
 */
public class RequestTaskManagerThread implements Runnable
{
   private  static  final  String TAG="RTaskManagerThread";
   private  RequestTaskManager requestTaskManager;
    //创建一个可以重用固定线程数的线程池
    private ExecutorService pool;
    //线程池大小
    private  final  int POOL_SIZE=5;
    //轮询时间
    private  final  int SLEEP_TIME=1000;
    //是否停止
    private  boolean isStop=false;

    public RequestTaskManagerThread(){
        this.requestTaskManager = RequestTaskManager.getInstance();
        pool = Executors.newFixedThreadPool(POOL_SIZE);
    }


    @Override
    public void run()
    {
        while (!isStop){
            RequestTask requestTask= requestTaskManager.getRequestTask();
            if (requestTask!=null){
                Log.e(TAG,"开始执行任务");
                pool.execute(requestTask);
            }else {
                //如果当期任务队列中没有请求任务requestTask
                try
                {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        if(isStop){
            pool.shutdown();
        }
    }

    /**
     * 停止线程池中的线程，也就是停止任务
     * @param isStop
     */
    public void setStop(boolean isStop){
        this.isStop=isStop;
    }
}
