package com.aofei.tch.anagyre.other.utils;

/**
 * Created by kenway on 16/11/20 19:06
 * Email : xiaokai090704@126.com
 */

public class DateUtils {
    /**
     * 将字节数组转换为十六进制
     *
     * @param bytes
     * @return String
     */
    public static String bytes2hex02(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
            {
                tmp = "0" + tmp;
            }
            sb.append(tmp).append(" ");
        }
        return sb.toString();

    }

    /**
     * 获取球号对应的值
     * @param ballNum
     * @return
     */
    public static int ballToInt(int ballNum) {
        int result = -1;

        switch (ballNum) {
            case 1:
                result = 1;
                break;
            case 2:
                result = 2;
                break;
            case 3:
                result = 4;
                break;
            case 4:
                result = 8;
                break;
            case 5:
                result = 16;
                break;
            case 6:
                result = 32;
                break;
            case 7:
                result = 64;
                break;
            case 8:
                result = 128;
                break;
        }

        return result;
    }

    public  int[] getPlayers(int ballsNum){
        int [] vsPlayers=new int[2];
            if (ballsNum<13){
               int player1= ballsNum/2;
                int player2=ballsNum-player1;
            }
        return vsPlayers;
    }

}
