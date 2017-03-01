package com.aofei.tch.anagyre.bluetooth.model;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.aofei.tch.anagyre.MyApplication;
import com.aofei.tch.anagyre.bluetooth.cmd.CMDContacts;
import com.aofei.tch.anagyre.bluetooth.taskqueue.base.RequestTaskManager;
import com.aofei.tch.anagyre.bluetooth.taskqueue.base.RequestTaskManagerThread;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnConnectTaskListener;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnDeletePlayerTaskListener;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnMatchCodeTaskListener;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnStartGameTaskListener;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnStopGameTaskListener;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnStopMatchCodeTaskListener;
import com.aofei.tch.anagyre.bluetooth.taskqueue.task.ConnectAdapterTask;
import com.aofei.tch.anagyre.bluetooth.taskqueue.task.DeletePlayerTask;
import com.aofei.tch.anagyre.bluetooth.taskqueue.task.MatchCodeTask;
import com.aofei.tch.anagyre.bluetooth.taskqueue.task.StartGameTask;
import com.aofei.tch.anagyre.bluetooth.taskqueue.task.StopGameTask;
import com.aofei.tch.anagyre.bluetooth.taskqueue.task.StopMacthCodeTask;
import com.aofei.tch.anagyre.main.ui.HomeActivity;
import com.aofei.tch.anagyre.other.utils.Debug;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.greenrobot.event.EventBus;


/**
 *
 *  xiaokai
 * 蓝牙服务.主要是实现
 * Created by xiaokai on 2016/9/7.
 */
public  class MyBluetoothService extends Service {
    public static final String TAG = "MyBluetoothService";

    /**==========声明蓝牙适配器,蓝牙广播============**/
    private BluetoothAdapter mBluetoothAdapter;//本地蓝牙适配器
    private MyBlueBroadcastReceiver myBroadcastReceiver;//蓝牙广播接受器

    /**=================任务管理==================**/
    //1.new 一个管理线程队列
    RequestTaskManager manager;
    //2.new 一个线程池，并启动
    RequestTaskManagerThread taskManagerThread;
    //3.当手机与适配器连接后的流
    private InputStream inputStream;
    private OutputStream outputStream;
    /**================任务监听==================**/
    //连接任务的监听
  OnConnectTaskListener onConnectTaskListener =
            new OnConnectTaskListener() {
                @Override
                public void onConecting() {
                    LogUtils.e(TAG, "正在连接中");
                }

                @Override
                public void onConnectedFail() {
                    LogUtils.e(TAG, "连接失败");
                }

                @Override
                public void onConnectedSuccessful(OutputStream os, InputStream is) {
                    //  从这里可以拿到is,os
                    LogUtils.e(TAG, "连接成功");
                    outputStream = os;
                    inputStream = is;
                }
                @Override
                public void onConnectedNoResponse() {
                    LogUtils.e(TAG, "连接时间超过5s");

                }

                @Override
                public void onConnectedPlayers(int[] ballInfo) {
                    //在这个接口可以获得适配器上已经连接的玩家信息
                     onMatchCodeTaskListener.onMacthedCodeSuccessful(ballInfo);//使用该接口来实现对码球的信息传送到ConnectFragment

                }

            };
    /**================注册接口回调==================**/
    //ConnectFragment相关接口
          //=======对码任务===========//
    private OnMatchCodeTaskListener onMatchCodeTaskListener;
    public  void   setOnConnectMatchListener(OnMatchCodeTaskListener onMatchCodeTaskListener){
        this.onMatchCodeTaskListener=onMatchCodeTaskListener;
    }
         //==========取消对码任务=========//
    private OnStopMatchCodeTaskListener onStopMatchCodeTaskListener;
    public  void  setOnStopMatchCodeTaskListener( OnStopMatchCodeTaskListener onStopMatchCodeTaskListener){
        this.onStopMatchCodeTaskListener=onStopMatchCodeTaskListener;
    }
         //=========删除好友任务=========//
    private OnDeletePlayerTaskListener onDeletePlayerTaskListener ;
    public  void  setOnDeletePlayerTaskListener(OnDeletePlayerTaskListener onDeletePlayerTaskListener){
        this.onDeletePlayerTaskListener=onDeletePlayerTaskListener;
    }

        //=========双人对战任务监听=========//
    private OnStartGameTaskListener onStartGameTaskListener ;
    public  void setOnStartGameTaskListener(OnStartGameTaskListener onStartGameTaskListener){
        this.onStartGameTaskListener=onStartGameTaskListener;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BlueBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(TAG, Debug.line(new Exception()) + TAG + "初始化完成");
        //******************广播*************************//
        myBroadcastReceiver = new MyBlueBroadcastReceiver();//搜索蓝牙设备的广播接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//开始扫描的广播
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//结束扫描的广播
        registerReceiver(myBroadcastReceiver, filter);//注册蓝牙广播
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//获得系统默认的蓝牙适配器
        //******************任务*************************//
        //1.new 一个管理线程队列
        manager = RequestTaskManager.getInstance();
        //2.new 一个线程池，并启动
        taskManagerThread = new RequestTaskManagerThread();
        //3.开启线程池
        new Thread(taskManagerThread).start();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        //===============理手机发送的命令的模块================//
        String cmd = intent.getStringExtra(HomeActivity.TAG);
              //*************扫描蓝牙的任务***************//
        /**
         * 当扫描完蓝牙设备后,在HomeActivity中会弹出对话框,让用户选择连接到哪个蓝牙托盘
         * 选择某一个后,就会发送一条命令给服务,那么服务收到这条命令后开始执行连接过程
         * */
        if (CMDContacts.RESQUEST_DISCOVER_BlUETOOTH.equals(cmd) && mBluetoothAdapter != null) {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.startDiscovery();
            }
            LogUtils.e(TAG, Debug.line(new Exception()) + "开始扫描蓝牙设备");
        }
              //*************连接适配器的任务***************//
        if (CMDContacts.RESQUEST_CONNECT_ADAPTER.equals(cmd) && mBluetoothAdapter != null) {
            BluetoothDevice device = intent.getParcelableExtra("device");
            if (mBluetoothAdapter == null) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
               //**********当前未连接,才执行此命令*************//
            if (!MyApplication.CONNECTED) {
                LogUtils.e(TAG, "准备连接 Name==" + device.getName() + ",Address==" + device.getAddress());
                if (manager == null) {
                    manager = RequestTaskManager.getInstance();
                }
                ConnectAdapterTask connectAdapterTask = new ConnectAdapterTask("connect", device);
                connectAdapterTask.setOnConnectTaskListener(onConnectTaskListener);
                manager.addRequestTask(connectAdapterTask);
            } else {
                //可以提示用户当前为连接状态
                LogUtils.e(TAG, "已经连接不需要再次连接");
            }


        }

        //===================对码任务======================//
        if (CMDContacts.RESQUEST_MATCH_ANAGYRE.equals(cmd) && mBluetoothAdapter != null) {
            LogUtils.e(TAG,Debug.line(new Exception())+"开始对码任务");
            MatchCodeTask matchCodeTask = new MatchCodeTask("matchCode", outputStream, inputStream);
            //这里可以使用提取出来的接口
            matchCodeTask.setOnMatchCodeTaskListener(onMatchCodeTaskListener);
            manager.addRequestTask(matchCodeTask);
        }

        //===================停止对码任务=================//
        if (CMDContacts.RESQUEST_STOP_MATCH_ANAGYRE.equals(cmd) && mBluetoothAdapter != null) {
            LogUtils.e(TAG,Debug.line(new Exception())+"取消对码任务");
            StopMacthCodeTask stopMacthCodeTask = new StopMacthCodeTask("stopMatchCode", outputStream, inputStream);
            if (onDeletePlayerTaskListener!=null){
                stopMacthCodeTask.setOnStopMatchCodeTaskListener(onStopMatchCodeTaskListener);//设置监听
            }

            manager.addRequestTask(stopMacthCodeTask);
        }


        //===================删除选手任务=================//
        if (CMDContacts.RESQUEST_DELETE_ANAGYRE.equals(cmd) && mBluetoothAdapter != null) {
           int num= intent.getIntExtra("deleteNum",-1);
            LogUtils.e(TAG,"删除的是"+num);
            DeletePlayerTask deletePlayerTask = new DeletePlayerTask("DeletePlayerTask", outputStream, inputStream);
            deletePlayerTask.setOnDeletePlayerTaskListener(onDeletePlayerTaskListener);//设置监听
            if (num==-1){
                LogUtils.e(TAG,"删除出错"+num);
            }
              deletePlayerTask.setDeleteNumber(num);   //这里需要传入要删除的球号
            manager.addRequestTask(deletePlayerTask);
        }
        //===================二人对战游戏开始=================//
        if (CMDContacts.RESQUEST_START_VS_COMPETITION.equals(cmd)&& mBluetoothAdapter != null) {
            //还需要两个参数,就是哪两个球号
            int play1BallNums=intent.getIntExtra("player1Num",0);
            int play2BallNums=intent.getIntExtra("player2Num",0);
            LogUtils.e(TAG,"参与比赛的球号为 player1Num"+play2BallNums+", player2Num="+play2BallNums);
            if (play1BallNums!=0&&play1BallNums!=0){
                StartGameTask startGameTask = new StartGameTask("StartGameTask", outputStream, inputStream);
                startGameTask.setPlayrNums(play1BallNums,play2BallNums);//设置参与比赛的球号
                startGameTask.setOnStartGameTaskListener(onStartGameTaskListener);
                manager.addRequestTask(startGameTask);
            }else {
                LogUtils.e(TAG,Debug.line(new Exception())+"比赛球号出错");
            }
        }

        //=================对战结束任务================//
        if(CMDContacts.RESQUEST_STOP_VS_COMPETITION.equals(cmd)&&mBluetoothAdapter!=null){
            //这里可能需要判断是否正在比赛中,比赛中才执行代码
            StopGameTask stopGameTask=new StopGameTask("StopGameTask",outputStream,inputStream);
            manager.addRequestTask(stopGameTask);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 搜索周边蓝牙
     */
    public class MyBlueBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //扫描到蓝牙设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //当扫描到设备后,使用EventBus将这个对象post 到HomeActivity
                EventBus.getDefault().post(device);
                if (("realong".equalsIgnoreCase(device.getName())||"bl3231-spp".equalsIgnoreCase(device.getName()))&&mBluetoothAdapter.isDiscovering()){
                       mBluetoothAdapter.cancelDiscovery();
                }

            }
//            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                mBluetoothAdapter.cancelDiscovery();
//            }
        }
    }
   public  class  BlueBinder extends Binder{
        /**
         * 获取服务的实例
         * @return
         */
        public  MyBluetoothService getService(){
            return MyBluetoothService.this;
        }
    }

    /**
     * 当退出程序时,停止服务,释放资源
     * @param name
     * @return
     */
    @Override
    public boolean stopService(Intent name) {
        LogUtils.e(TAG, "stopService");
        unregisterReceiver(myBroadcastReceiver);
        myBroadcastReceiver = null;
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(myBroadcastReceiver);
        stopSelf();
        LogUtils.e(TAG, Debug.line(new Exception()) +"onDestroy");
          //当服务结束的时候,释放资源
        if (inputStream!=null){
            try {
                inputStream.close();
                inputStream=null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outputStream!=null){
            try {
                outputStream.close();
                outputStream=null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
