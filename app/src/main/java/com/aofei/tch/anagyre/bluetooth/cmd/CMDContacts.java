package com.aofei.tch.anagyre.bluetooth.cmd;

/**
 * 这是一个命令的常量类
 * Created by kenway on 16/11/21 15:15
 * Email : xiaokai090704@126.com
 */

public class CMDContacts {
    //****配置蓝牙和对码*****//
    /**扫描适配器的命令*/

    public  static  final  String RESQUEST_DISCOVER_BlUETOOTH="RESQUEST_DISCOVER_BlUETOOTH";

    /**连接适配器,(中间转换的板子)*/
    public static final String RESQUEST_CONNECT_ADAPTER ="resquest_connect_adapter";
    public  static  final  byte[] RESQUEST_CONNECT_APAPTER_BYTES={(byte) 176,(byte) 171,0,0,0,0,0,0,0,0};
    /**手机发送对码命令(陀螺与盘子对码,一条命令与一个陀螺对码)*/
    public static final String RESQUEST_MATCH_ANAGYRE="resquest_match_anagyre";
    public  static  final  byte[]RESQUEST_MATCH_ANAGYRE_BYTES={(byte) 176,(byte) 172,0,0,0,0,0,0,0,0};
    /**手机发送停止对码命令*/
    public static final String RESQUEST_STOP_MATCH_ANAGYRE="resquest_disconnect_anagyre";
    public  static  final  byte[]RESQUEST_STOP_MATCH_ANAGYRE_BYTES={(byte) 176,(byte) 173,0,0,0,0,0,0,0,0};
    /**剔除对好码用户*/
    public static final String RESQUEST_DELETE_ANAGYRE="resquest_delete_anagyre";
    public  static  final  byte[]RESQUEST_DELETE_ANAGYRE_BYTES={(byte) 176,(byte) 174,0,0,0,0,0,0,0,0};
    /**开始比赛*/
      //*****双人对战******//
    public static  final  String RESQUEST_START_VS_COMPETITION ="start_vs_competition";
    public  static  final byte[] RESQUEST_START_VS_COMPETITION_BYTES={(byte) 176,(byte) 175,0,0,0,0,0,0,0,0};
    /**开始比赛*/
    //*****双人对战******//
    public static  final  String RESQUEST_STOP_VS_COMPETITION ="stop_vs_competition";
    public  static  final byte[] RESQUEST_STOP_VS_COMPETITION_BYTES={(byte) 176,(byte) 175,0,0,0,0,0,0,0,0};




}
