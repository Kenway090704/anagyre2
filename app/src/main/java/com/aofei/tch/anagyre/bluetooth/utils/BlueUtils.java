package com.aofei.tch.anagyre.bluetooth.utils;

import android.bluetooth.BluetoothAdapter;
import android.support.v4.content.res.TypedArrayUtils;

import java.util.Arrays;

/**
 * 蓝牙工具类
 * 获取本机蓝牙
 * Created by kenway on 16/11/25 17:28
 * Email : xiaokai090704@126.com
 */

public class BlueUtils  {
    /**
     * 获取安装app的Mac地址
     * @return String
     */
    public static  String  getSystemMac(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter==null){
            return null;
        }
        return bluetoothAdapter.getAddress();

    }

    /**
     * 获取本机的Mac地址以字符数组的形式
     * @return   String[]
     */
    public  static  String[] getSystemMacForArray(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter==null){
            return null;
        }
        String[] strings= bluetoothAdapter.getAddress().split(":");
        return  strings;
    }

    /**
     * 获取Mac地址的最后四位
     * @param mac
     * @return 只有四位的字节数组
     */
    public  static  String[] getMac4Bit(String mac){
        String[] strings= mac.split(":");
        String[] result=new String[4];
        int j=0;
        for (int i=strings.length-4;i<strings.length;i++){
            result[j++]=strings[i];
        }
         return  result;
    }

    /**
     * 得到十六进制字符串的的对应byte值
     * @param hex 只限两位
     * @return
     */
    public  static  byte  getStringHexToInt(String hex){
       return (byte)Integer.parseInt(hex,16);
    }
    /**
     * 获取本机的mac地址最后四位ID,以byte[] 形式
     * @return  byte[]  ---只有最后四位ID(共六位)
     */

    public  static  byte[]  getSystemMac4byByte(){
      byte[]  result=new byte[4];
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter==null){
            return null;
        }
        String[] strings= bluetoothAdapter.getAddress().split(":");
        int j=0;
        for (int i=strings.length-4;i<strings.length;i++){
            result[j++]=getStringHexToInt(strings[i]);
        }
        return  result;
    }

}
