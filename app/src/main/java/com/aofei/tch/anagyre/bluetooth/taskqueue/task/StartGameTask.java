package com.aofei.tch.anagyre.bluetooth.taskqueue.task;

import com.aofei.tch.anagyre.bluetooth.taskqueue.base.RequestTask;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnDeletePlayerTaskListener;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnStartGameTaskListener;
import com.aofei.tch.anagyre.other.utils.DateUtils;
import com.aofei.tch.anagyre.other.utils.Debug;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;
import com.aofei.tch.anagyre.vs.ui.VSFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 开始游戏任务
 * Created by kenway on 16/11/25 14:14
 * Email : xiaokai090704@126.com
 */

public class StartGameTask extends RequestTask {
    private static final String TAG = "StartGameTask";
    //这里需要替换参与比赛的球的号码
    byte[] startGameCode = {(byte) 0xAA, (byte) 0xA5, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    //球的配件值
    byte[] acValueCode = {(byte) 0xAA, (byte) 0xAC, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    byte[] rankCode = {(byte) 0xAA, (byte) 0xAE, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    private OutputStream os = null;//将命令数组写入
    private InputStream is = null;//读取数据
    private boolean isValid=true;

    public StartGameTask(String name, OutputStream os, InputStream is) {
        super(name);
        this.os = os;
        this.is = is;
    }

    private int playrNums;
    private int player1, player2;
    private int winPlayer;

    /**
     * 设置比赛玩家的球号
     */
    public void setPlayrNums(int player1, int player2) {
        this.player1 = player1;
        this.player2 = player2;
        LogUtils.e(TAG, " 参加比赛的球号为  player1 " + player1 + ", player2 " + player2);
        this.playrNums = DateUtils.ballToInt(player1) + DateUtils.ballToInt(player2);
    }
    //开始游戏任务的监听
    private OnStartGameTaskListener onStartGameTaskListener;

    public void setOnStartGameTaskListener(OnStartGameTaskListener onStartGameTaskListener) {
        this.onStartGameTaskListener = onStartGameTaskListener;
    }
    //删除玩家的监听
    private OnDeletePlayerTaskListener onDeletePlayerTaskListener;

    public void setOnDeletePlayerTaskListener(OnDeletePlayerTaskListener onDeletePlayerTaskListener) {
        this.onDeletePlayerTaskListener = onDeletePlayerTaskListener;
    }

    boolean isTwoHasData = false;

    //开始游戏时要执行的代码
    @Override
    public void run() {
        int choose = 0;
        LogUtils.e(TAG + Debug.line(new Exception()), "StartGameTask---准备开始比赛");
        if (os != null && is != null) {
            try {
                LogUtils.e(TAG + Debug.line(new Exception()), "准备开始比赛.....");
                startGameCode[2] = (byte) playrNums;
                os.write(startGameCode);//将命令写入
                int len;
                //这里还需要判断是否在对码的状态,全局变量判断
                while (is != null) {
                    len = is.read();//查看返回值
                    if (len == 0xBA) {
                        len = is.read();
                        //判断适配器是否接受到命令
                        if (len == 0x09) {
                            len = is.read();
                             if (len == 0xB5) {
                                //适配器接受到命令,开始比赛
                                StringBuffer stringBuffer = new StringBuffer();
                                for (int i = 0; i < 7; i++) {
                                    stringBuffer.append(is.read() + " ");
                                }
                                LogUtils.e(TAG + Debug.line(new Exception()), "StartGameTask---比赛开始: 186 9 181 " + stringBuffer);
                                onStartGameTaskListener.onStartGameSuccessful();
                                VSFragment.isPlayGaming = true;
                            }
                            //中转器回复的配件值
                             if (len == 0xBC) {
                                StringBuffer stringBuffer = new StringBuffer();
                                int[] data = new int[7];
                                for (int i = 0; i < 7; i++) {
                                    data[i] = is.read();
                                    stringBuffer.append(data[i] + " ");
                                }

                                acValueCode[2] = (byte) data[0];
                                os.write(acValueCode);

                                 //配件值可以使用接口拿到
                                LogUtils.e(TAG,"回复中转器的配件值"+  acValueCode[2]);
                                LogUtils.e(TAG + Debug.line(new Exception()), "球的配件值: 186 9 188 " + stringBuffer);
                            }
                            //停止比赛的命令(手动停止)
                            if (len == 0xB6) {
                                //适配器接受到命令
                                StringBuffer stringBuffer = new StringBuffer();
                                for (int i = 0; i < 7; i++) {
                                    stringBuffer.append(is.read() + " ");
                                }
                                LogUtils.e(TAG + Debug.line(new Exception()), "比赛停止: 186 9 182 " + stringBuffer);
                                VSFragment.isPlayGaming = false;
                                onStartGameTaskListener.onGameEnd(-1);
                                break;
                            }
                            //比赛结束
                            if (len == 0xBE) {
                                StringBuffer stringBuffer = new StringBuffer();
                                int[] data = new int[7];
                                for (int i = 0; i < 7; i++) {
                                    data[i] = is.read();
                                    stringBuffer.append(data[i] + " ");
                                }
                                winPlayer = data[0];
                                os.write(rankCode);//将配件值数写入
                                LogUtils.e(TAG + Debug.line(new Exception()), "比赛结束   排名 " + stringBuffer );
                                VSFragment.isPlayGaming = false;
                                onStartGameTaskListener.onGameEnd(winPlayer);
                                break;
                            }
                        }
                        //得到球的转速,并通过接口回调给VsFragment  B7---203
                        if (len == 0x13) {
                            if (is.read() == 0xBD) {
                                isValid=true;
                                int[] gameData1 = new int[17];
                                StringBuffer s2 = new StringBuffer();
                                for (int i = 0; i < 17; i++) {
                                    gameData1[i] = is.read();
                                    s2.append(gameData1[i] + " ");
                                }
                                LogUtils.e(TAG, "获取的数据:" + "186 19 183 " + s2.toString());
                                //====判断一下数据是否有效,适配器已更改,后面这里可以不用
                                for (int i = 0; i < 17; i++) {
                                    if ((player1*2-2)!=i||(player1*2-1)!=i||(player2*2-2)!=i||(player2*2-1!=i)||i!=(gameData1.length-1)){
                                    }else {
                                       if (gameData1[i]!=0){
                                           LogUtils.e(TAG,"数据无效");
                                           isValid=false;
                                       }
                                    }
                                }
                               if (!isValid){
                                   continue;
                                   //当数据为非法的数据时,不提交
                               }
                                /**=====如果获取的数据为FF则表示该球连接不稳定====*/
                                if (gameData1[player1 * 2 - 2] == 0xFF && gameData1[player1 * 2 - 1] == 0xFF) {
                                    onStartGameTaskListener.onConnectInstability(player1);
                                }
                                if (gameData1[player2 * 2 - 2] == 0xFF && gameData1[player2 * 2 - 1] == 0xFF) {
                                    onStartGameTaskListener.onConnectInstability(player2);
                                }
                                //当两个球都有转速时,才开始绘制k线图  获取的数据:186 19 183 186 9 188 2 180 255 129 255 0 165 186 19 189 0 0 244 6
                                if (gameData1[player1 * 2 - 2] != 0x00 && gameData1[player1 * 2 - 1] != 0x00
                                        && gameData1[player1 * 2 - 2] != 0xFF && gameData1[player1 * 2 - 1] != 0xFF
                                        && gameData1[player2 * 2 - 2] != 0x00 && gameData1[player2 * 2 - 1] != 0x00
                                        && gameData1[player2 * 2 - 2] != 0xFF && gameData1[player2 * 2 - 1] != 0xFF
                                        ) {

                                    LogUtils.e(TAG,gameData1[player1 * 2 - 2]+","+ gameData1[player1 * 2 - 1]+" ,"+gameData1[player2 * 2 - 2]+" ,"+gameData1[player2 * 2 - 1]);

                                    LogUtils.e(TAG, "比赛开始——--数据开始回传");
                                    //通过接口可以告知VsFramgent比赛开始
                                    isTwoHasData = true;
                                }

                                if (isTwoHasData&VSFragment.isPlayGaming) {
                                    //断是否为有效数据
                                    if (gameData1[gameData1.length - 1] == 0xA5) {
                                        //数据解析
                                        int[] data = new int[8];
                                        StringBuffer stringBuffer = new StringBuffer();
                                        for (int i = 0; i < data.length; i++) {
                                            if ((player1 - 1) == i || (player2 - 1) == i) {
                                                if (gameData1[2 * i + 1] == 0) {
                                                    data[i] = 0;
                                                } else {
                                                    data[i] = (int) (60000 / ((gameData1[2 * i]) * 0.0106 + (gameData1[2 * i + 1]) * 2.728));
                                                }
                                            } else {
                                                data[i] = 0;
                                            }
                                            stringBuffer.append(data[i] + " ,");
                                        }
                                        stringBuffer.append("\n");
                                        LogUtils.e(TAG, "给到VSFragment的数据" + stringBuffer.toString());

                                            onStartGameTaskListener.onGameRotateDate(data);
                                        /**========如果有球号,数据为0 则该局比赛结束,适配器也可以判断,下次测试时可以不用===============*/
                                        /**都为零的时候,需要判断上一组数据*/
                                        if ((gameData1[player1 * 2 - 2] == 0x00 && gameData1[player1 * 2 - 1] == 0x00)
                                                && (gameData1[player2 * 2 - 2] == 0x00 && gameData1[player2 * 2 - 1] == 0x00)) {
                                            LogUtils.e(TAG, "两个球同时为零!");
                                            isTwoHasData = false;
                                        }
                                        /**判断是否结束,然后将获胜的玩家通过接口传递到界面*/
                                        if (gameData1[player1 * 2 - 2] == 0x00 && gameData1[player1 * 2 - 1] == 0x00
                                                && gameData1[player2 * 2 - 2] != 0x00 && gameData1[player2 * 2 - 1] != 0x00
                                                && gameData1[player2 * 2 - 2] != 0xFF && gameData1[player2 * 2 - 1] != 0xFF) {
                                            isTwoHasData = false;
                                            LogUtils.e(TAG, "player2获胜---" + player2);
//
                                        }
                                        if (gameData1[player1 * 2 - 2] != 0xFF && gameData1[player1 * 2 - 1] != 0xFF
                                                && gameData1[player1 * 2 - 2] != 0x00 && gameData1[player1 * 2 - 1] != 0x00
                                                && gameData1[player2 * 2 - 2] == 0x00 && gameData1[player2 * 2 - 1] == 0x00) {
                                            isTwoHasData = false;
                                            LogUtils.e(TAG, "player1获胜---" + player1);
//
                                        }
                                    }
                                }
                            }


                        }

                    }


                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //提示用户设备断开连接,请先连接适配器
            LogUtils.e(TAG, Debug.line(new Exception()) + "当前未连接适配器");
            onStartGameTaskListener.onStartGameFail();
        }


    }

}
