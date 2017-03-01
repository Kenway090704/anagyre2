package com.aofei.tch.anagyre.bluetooth.state;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 *
 * 该类中的方法可以监听到连接的蓝牙是否断开
 * Created by kenway on 16/11/22 14:48
 * Email : xiaokai090704@126.com
 */

public class ConnectState {

    private void registerBoradcastReceiver(Context context) {
        IntentFilter stateChangeFilter = new IntentFilter(
                BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter connectedFilter = new IntentFilter(
                BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter disConnectedFilter = new IntentFilter(
                BluetoothDevice.ACTION_ACL_DISCONNECTED);
        context.registerReceiver(stateChangeReceiver, stateChangeFilter);
        context.registerReceiver(stateChangeReceiver, connectedFilter);
        context.registerReceiver(stateChangeReceiver, disConnectedFilter);
    }

    private BroadcastReceiver stateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_CONNECTED .equals(action)) {
                //蓝牙是连接状态
                //这里可以设置UI
            }
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED .equals(action) ) {
                //蓝牙是连接状态
                //这里可以设置UI
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                //蓝牙是连接状态
                //这里可以设置UI
            }
        }
    };
}
