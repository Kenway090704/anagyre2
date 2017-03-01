package com.aofei.tch.anagyre.bluetooth.taskqueue.task;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.aofei.tch.anagyre.MyApplication;
import com.aofei.tch.anagyre.bluetooth.taskqueue.base.RequestTask;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnConnectTaskListener;
import com.aofei.tch.anagyre.connect.ui.ConnectFragment;
import com.aofei.tch.anagyre.other.utils.Debug;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * 连接适配器的任务
 * Created by kenway on 16/11/24 09:47
 * Email : xiaokai090704@126.com
 */

public class ConnectAdapterTask extends RequestTask {
    private static final String TAG = "ConnectAdapterTask";
    byte[] connectBytes = {(byte) 0xAA, (byte) 0xA1, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    /**
     * @param name             任务名(保留)----可以"connect"
     * @param mBluetoothDevice 要连接的适配器蓝牙对象
     */
    public ConnectAdapterTask(String name, BluetoothDevice mBluetoothDevice) {
        super(name);
        this.mBluetoothDevice = mBluetoothDevice;
    }

    /**
     * 设置监听
     */
    private OnConnectTaskListener onConnectTaskListener;//连接监听

    public void setOnConnectTaskListener(OnConnectTaskListener onConnectTaskListener) {
        this.onConnectTaskListener = onConnectTaskListener;
    }

    //使用异步线程来实现连接蓝牙设备
    private int connetTime = 0;
    private BluetoothDevice mBluetoothDevice;
    public static BluetoothSocket socket;
    public BluetoothAdapter mBluetoothAdapter;

    //执行的内容
    @Override
    public void run() {
        /**如果五秒钟依然没有连接成功或者失败的响应,那么使用无响应的接口*/
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (MyApplication.CONNECTED) {
                } else {
                    onConnectTaskListener.onConnectedNoResponse();
                }
            }
        };
        timer.schedule(timerTask, 5000);//五秒钟没有反应就提示
        LogUtils.e(TAG + Debug.line(new Exception()), "ConnectAdapterTask----连接适配器中...");

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        //***********停止扫描***********//
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        try {
            //通过BluetoothDevice获取蓝牙设备之间的Socket
            socket = mBluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (Exception e) {
            LogUtils.e(TAG, Log.getStackTraceString(e));
            LogUtils.e(TAG, "Socket" + e);
        }
        while (!MyApplication.CONNECTED && connetTime <= 10) {
            connectDevice();//进行连接
        }
    }

    /**
     * 可能以后会用
     * 取消连接的
     **/
    public void cancel() {
        try {
            socket.close();
            socket = null;
        } catch (Exception e) {
            LogUtils.e(TAG, Log.getStackTraceString(e));
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 获取连接后的两个流
     *
     * @return 读取流
     */
    public InputStream getInputStream() {
        if (MyApplication.CONNECTED) {
            return is;
        } else {
            return null;
        }
    }

    /***
     * @return 写入流
     */
    public OutputStream getOutputStream() {
        if (MyApplication.CONNECTED) {
            return os;
        } else {
            return null;
        }
    }

    private InputStream is;
    private OutputStream os;

    /**
     * 连接过程
     */
    private void connectDevice() {
        try {
        } catch (Exception e) {
            LogUtils.e(TAG, Log.getStackTraceString(e));
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                onConnectTaskListener.onConecting();
                socket.connect();
                os = socket.getOutputStream();//获取读取流
                is = socket.getInputStream();//获取输入流
                //发送字节数组与适配器进行握手
                os.write(connectBytes);
                while (is != null) {
                    if (!MyApplication.CONNECTED) {
                        int len = is.read();
                        if (len == 186) {
                            len = is.read();
                            if (len == 9) {
                                StringBuffer stringBuffer = new StringBuffer();
                                for (int i = 0; i < 8; i++) {
                                    stringBuffer.append(is.read() + " ");
                                }
                                //连接状态的全局变量要实现握手以后才可以改变
                                MyApplication.CONNECTED = true;
                                LogUtils.e(TAG, 186 + "  " + 9 + "  " + stringBuffer.toString());
                                onConnectTaskListener.onConnectedSuccessful(os, is);


                                break;
                            }
                              //  获取已经连接的球的信息.这里需要看后面协议是怎么样的
                            if (len == 0x0D && is.read() == 0xBB) {
                                int[] ballInfo = new int[8];
                                onConnectTaskListener.onConnectedPlayers(ballInfo);

                            }
                        }

                    }
                }
            }
        } catch (IOException e) {
            onConnectTaskListener.onConnectedFail();//连接失败
            connetTime++;
            MyApplication.CONNECTED = false;//连接状态
            try {
                if (socket != null) {
                    socket.close();
                    socket = null;
                }
            } catch (IOException e2) {
                Log.e(TAG, "Cannot close connection when connection failed");
            }
        } finally {
            try {
                if (!MyApplication.CONNECTED && is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
