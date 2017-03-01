package com.aofei.tch.anagyre.bluetooth.taskqueue.task;

import com.aofei.tch.anagyre.bluetooth.taskqueue.base.RequestTask;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnDeletePlayerTaskListener;
import com.aofei.tch.anagyre.other.utils.Debug;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 剔除好友的任务
 * Created by kenway on 16/11/25 14:18
 * Email : xiaokai090704@126.com
 */

public class DeletePlayerTask extends RequestTask {
    public  static  final  String TAG="DeletePlayerTask";
    byte[] deletePlayerCode={(byte)0xAA,(byte)0xA4,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};   //需要传入球号,数组中的第三个
    private  byte deleteNumber;//要删除的球号
    private  OutputStream  os=null;//将命令数组写入
    private  InputStream   is=null;//读取数据
    private OnDeletePlayerTaskListener onDeletePlayerTaskListener;
    public DeletePlayerTask(String name, OutputStream os, InputStream is) {
        super(name);
        this.is=is;
        this.os=os;
    }
    /**
     * 要删除的球号
     * @param deleteNumber
     */
    public  void setDeleteNumber(int deleteNumber){

        this.deleteNumber= (byte) deleteNumber;

    }
    /**
     *   设置删除命令的监听
     * @param onDeletePlayerTaskListener
     */
    public  void  setOnDeletePlayerTaskListener(OnDeletePlayerTaskListener onDeletePlayerTaskListener){
        this.onDeletePlayerTaskListener=onDeletePlayerTaskListener;
    }
    @Override
    public void run() {
        LogUtils.e(TAG + Debug.line(new Exception()), "DeletePlayerTask----删除好友 ");

            if (os != null && is != null) {
                try {
                    deletePlayerCode[2] = deleteNumber;//将要删除的球号加入命令中
                    os.write(deletePlayerCode);
                    int len;
                    while (is != null) {
                        len = is.read();//查看返回值
                        if (len == 0xBA) {
                            len = is.read();  //判断适配器是否接受到命令
                            if (len == 0x09 ) {
                                  len=is.read();

                                if (0xB4==len){

                                    int[] b = new int[7];
                                    StringBuffer stringBuffer=new StringBuffer();
                                    for (int i=0;i<7;i++){
                                          b[i] =  is.read();
                                        stringBuffer.append(b[i]+" ");
                                    }
                                    //删除命令执行成功
                                    LogUtils.e(TAG,"deleteNumber="+deleteNumber+" b[0]=="+b[0]);
                                    if (deleteNumber == b[0]) {
                                        LogUtils.e(TAG,"删除 "+b[0]+" 号球成功,适配器响应码"+186+" "+9+" "+180+"  "+stringBuffer);
                                        onDeletePlayerTaskListener.onDeletePlayerSuccessful(b[0]);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                   } catch(IOException e){
                e.printStackTrace();
               }
               } else {
                    onDeletePlayerTaskListener.onDeletePlayerFail();//删除失败,is=null os=null
              }




    }


}
