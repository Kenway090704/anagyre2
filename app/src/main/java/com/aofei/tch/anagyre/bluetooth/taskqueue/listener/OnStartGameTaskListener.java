package com.aofei.tch.anagyre.bluetooth.taskqueue.listener;

/**
 * 开始游戏任务的一个接口对象
 */
public interface OnStartGameTaskListener {
    void onStartGameing();//执行中

    void onStartGameFail();//开始有关系失败

    void onStartGameSuccessful();//开始游戏

    /**
     * @param gameData gameData.length=8  1--1号球的转速   2--2号球的转速  以此类推
     */
    void onGameRotateDate(int[] gameData);//将比赛数据通过接口回调给服务中

    /**
     *  当比赛过程中,球与蓝牙适配器(板子)连接不稳定
     * @param playerNum
     */
    void  onConnectInstability(int playerNum); //Connection is not stable  instability

    /**
     *   比赛结束

     * @param winPlayerRank   获胜的参赛者--第一名
     */
    void onGameEnd(int winPlayerRank);

    void onStartGameNoResponse();//连接无响应  ---保留,看是否有需要
}