package com.aofei.tch.anagyre.bluetooth.taskqueue.task;

import com.aofei.tch.anagyre.bluetooth.taskqueue.base.RequestTask;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnStopMatchCodeTaskListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 停止对码
 * Created by kenway on 16/11/25 14:13
 * Email : xiaokai090704@126.com
 */

public class StopMacthCodeTask extends RequestTask {
    private  static  final  String TAG="StopMacthCodeTask";
    byte[] stopMatchCode={(byte)0xAA,(byte)0xA3,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    private OutputStream os=null;
    private InputStream is=null;
    public StopMacthCodeTask(String name, OutputStream os, InputStream is) {
        super(name);
        this.os=os;
        this.is=is;
    }
    private OnStopMatchCodeTaskListener onStopMatchCodeTaskListener;
    public  void   setOnStopMatchCodeTaskListener(OnStopMatchCodeTaskListener onStopMatchCodeTaskListener){
        this.onStopMatchCodeTaskListener=onStopMatchCodeTaskListener;
    }
    @Override
    public void run() {

            if (os != null && is != null) {
                try {
                    os.write(stopMatchCode);

//                    int len;
//                    while (is != null) {
//                        len = is.read();//查看返回值
//                        LogUtils.e(TAG + Debug.line(new Exception()), "StopMacthCodeTask----停止对码"+len);
//                        if (len == 186) {
//                            LogUtils.e(TAG,"停止对码成功");
//                            len = is.read();
//                            if (len == 9 && is.read() == 179) {
//                                onStopMatchCodeTaskListener.onStopMacthedCodeSuccessful();
//                                MyApplication.isMatching = false;    //结束命令
//                                break;
//                            }
//                        }
//                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                onStopMatchCodeTaskListener.onStopMacthedCodeFail();//命令执行失败,请先连接适配器
            }





    }

}
