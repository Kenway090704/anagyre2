package com.aofei.tch.anagyre.bluetooth.taskqueue.listener;

/**
 * VsFragment与MyBluetoothService进行数据交互时的接口
 *
 * Created by kenway on 16/11/21 17:15
 * Email : xiaokai090704@126.com
 */

public interface OnVsKcarveListener {
    /**
     * 获取转速,就每隔一个时间点传入一个转速值
     * @param value  共有八个值,分别对应,每一个球号的转速,
     */
    void  onUpdate(int[] value);
}
