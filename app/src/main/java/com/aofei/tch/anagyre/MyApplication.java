package com.aofei.tch.anagyre;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.aofei.tch.anagyre.bluetooth.model.MyBluetoothService;
import com.aofei.tch.anagyre.other.utils.FileUtils;
import com.aofei.tch.anagyre.other.utils.log.LogCatch;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;
import com.aofei.tch.anagyre.other.utils.log.MyDate;
import com.aofei.tch.anagyre.vs.bean.CompetionInfo;
import com.aofei.tch.anagyre.vs.bean.PlayerInfo;
import com.aofei.tch.anagyre.vs.shared.SaveCompetitionDataPre;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kenway on 16/11/17 19:17
 * Email : xiaokai090704@126.com
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    public static boolean CONNECTED = false;//手机与适配的连接状态

    public static boolean isTest = false;//判断是否是测试,如果是测试,主要进行一些数据的模拟

    public static boolean isMatching = false;//判读是否正在对码中
    @Override
    public void onCreate() {
        super.onCreate();
        //开启将log日志保存到sd卡
        LogCatch.getInstance(getApplicationContext()).stop();
        LogCatch.getInstance(getApplicationContext()).start();
        //初始化比赛信息类
        SaveCompetitionDataPre.init(getApplicationContext());
        //模拟存储比赛信息
//        for (int i=0;i<5;i++){
//            CompetionInfo com = new CompetionInfo();
//            PlayerInfo left = new PlayerInfo();
//            left.setName("lefter"+i);
//            PlayerInfo right = new PlayerInfo();
//            right.setName("right"+i);
//            com.setLeftPlayer(left);
//            com.setRightPlayer(right);
//            List<Integer> listRotate=new ArrayList<>();
//            for (int j=0;j<100;j++){
//              int rotate= (int) (Math.random()*2000+1000);//转速数据为1000-3000
//                listRotate.add(rotate);//添加转速信息,
//
//            }
//            com.setListdata(listRotate);
//            SaveCompetitionDataPre.getInstance().saveCompetioninfo(com);
//        }
        SaveCompetitionDataPre.getInstance().clear();//清除说有比赛数据
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        LogCatch.getInstance(getApplicationContext()).stop();//停止将Log写入文件的线程
    }
}
