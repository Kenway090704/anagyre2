package com.aofei.tch.anagyre.bluetooth.taskqueue.task;

import com.aofei.tch.anagyre.MyApplication;
import com.aofei.tch.anagyre.bluetooth.taskqueue.base.RequestTask;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnMatchCodeTaskListener;
import com.aofei.tch.anagyre.bluetooth.utils.BlueUtils;
import com.aofei.tch.anagyre.connect.ui.ConnectFragment;
import com.aofei.tch.anagyre.other.utils.DateUtils;
import com.aofei.tch.anagyre.other.utils.Debug;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 对码的任务
 * Created by kenway on 16/11/24 09:47
 * Email : xiaokai090704@126.com
 */

public class MatchCodeTask extends RequestTask {
    private static final String TAG = "MatchCodeTask";
    byte[] matchCode = {(byte) 0xAA, (byte) 0xA2, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    byte[] response={(byte) 0xAA, (byte) 0xAB, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    private OutputStream os = null;//将命令数组写入
    private InputStream is = null;//读取数据
    private OnMatchCodeTaskListener onMatchCodeTaskListener;
    byte[] bytes;
   private   List<Integer> list;
    private  boolean isHas;//是否已经对码了
    /**
     * 设置监听
     *
     * @param onMatchCodeTaskListener
     */
    public void setOnMatchCodeTaskListener(OnMatchCodeTaskListener onMatchCodeTaskListener) {
        this.onMatchCodeTaskListener = onMatchCodeTaskListener;
    }

    /**
     * @param name 任务名(保留)----可以"matchcode"
     * @param os   已经建立连接时流
     * @param is
     */
    public MatchCodeTask(String name, OutputStream os, InputStream is) {
        super(name);
        this.os = os;
        this.is = is;
        bytes= BlueUtils.getSystemMac4byByte();//获取本机的Mac地址后四位
        list=new ArrayList<>();
    }
    @Override
    public void run() {

            LogUtils.e(TAG + Debug.line(new Exception()), "MatchCodeTask----正在对码");

            if (os != null && is != null) {
                try {
                    //将本机的Mac地址后四位写入适配器
                    for (int i=0;i<4;i++){
                        matchCode[i+2]= bytes[i];
                    }
                    LogUtils.e(TAG,"本机mac地址"+ DateUtils.bytes2hex02(bytes)+"");
                    os.write(matchCode);
                    MyApplication.isMatching = true;
                    int len;
                    while (is != null && MyApplication.isMatching) {
                        len = is.read();//查看返回值
                        if (len == 186) {
                            len = is.read();
                            LogUtils.e(TAG,"获取的数据"+len);
                            if (len == 9 ) {
                                len=is.read();
                                if (len==178){
                                    StringBuffer stringBuffer=new StringBuffer();
                                    for (int i=0;i<7;i++){
                                        stringBuffer.append(is.read()+" ");
                                    }
                                    LogUtils.e(TAG,"可以开始对码,适配器响应码"+186+" "+9+" "+178+" "+stringBuffer);
                                    onMatchCodeTaskListener.onMacthing();//可以对码
                                }
                                if (len==179){
                                    StringBuffer stringBuffer=new StringBuffer();
                                    for (int i=0;i<7;i++){
                                        stringBuffer.append(is.read()+" ");
                                    }
                                    LogUtils.e(TAG,"停止对码成功,适配器响应码"+186+" "+9+" "+179+"  "+stringBuffer);
                                    onMatchCodeTaskListener.onMacthCodeFinished();
                                    MyApplication.isMatching=false;
                                    break;
                                }

                            }
                            //对码成功的球相关信息,最多可以添加8个球
                            if (len == 0x0D && is.read() == 0xBB) {
                               int[] ballInfo = new int[12 - 2];
                                //这个就是对码成功的球的信息
                                StringBuffer stringBuffer=new StringBuffer();
                                for (int i=0;i<10;i++){
                                    int data=is.read();
                                    stringBuffer.append(data+" ");
                                    ballInfo[i]= data;
                                }
                                LogUtils.e(TAG,"对码成功"+186+" "+12+" "+187+" "+stringBuffer);

                                //回复对码确认命令
                                response[2]= (byte) ballInfo[0];
                                os.write(response);
                                 //使用全局的一个集合来判断是否已经存在该球了
                                for (int i = 0; i< ConnectFragment.list.size(); i++){
                                    if (ballInfo[0]==ConnectFragment.list.get(i).getBallNum()){
                                        isHas=true;
                                    }
                                }
                                if (!isHas){
                                    onMatchCodeTaskListener.onMacthedCodeSuccessful(ballInfo);
                                }else {
                                    isHas=false;
                                }
                            }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                String msg = "适配器未连接,请先连接适配";
                onMatchCodeTaskListener.onMacthedCodeFail(msg);//对码失败,请先连接适配器
            }

        }



}
