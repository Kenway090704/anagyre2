package com.aofei.tch.anagyre.vs.bean;

import com.aofei.tch.anagyre.other.utils.log.MyDate;

import java.io.Serializable;
import java.util.List;

/**
 * 比赛信息基类
 * Created by kenway on 16/12/20 09:16
 * Email : xiaokai090704@126.com
 */

public class CompetionInfo implements Serializable {
    private static final String TAG = "CompetionInfo";
    /**
     * 默认为比赛时时间
     */
    private String name = "(" + MyDate.getDateHMS() + ")"; //比赛名称
    private PlayerInfo leftPlayer;//左边玩家的信息
    private PlayerInfo rightPlayer;//右边玩家的信息
    private List<Integer> listdata;//比赛数据
    private long timeGameEnd = System.currentTimeMillis();//比赛结束时间

    @Override
    public String toString() {
        return "CompetionInfo{" +
                "name='" + name + '\'' +
                ", leftPlayer='" + leftPlayer + '\'' +
                ", rightPlayer='" + rightPlayer + '\'' +
                ", listdata=" + listdata +
                ", timeGameEnd=" + timeGameEnd +
                '}';
    }

    public long gettimeGameEnd() {
        return timeGameEnd;
    }

    public void settimeGameEnd(long timeGameEnd) {
        this.timeGameEnd = timeGameEnd;
    }

    public String getName() {
        return name;
    }

//    public void setName(String name) {
//        this.name = name;
//    }

    public PlayerInfo getLeftPlayer() {
        return leftPlayer;
    }


    public void setLeftPlayer(PlayerInfo leftPlayer) {
        this.leftPlayer = leftPlayer;
    }

    public PlayerInfo getRightPlayer() {
        return rightPlayer;
    }

    public void setRightPlayer(PlayerInfo rightPlayer) {
        this.rightPlayer = rightPlayer;
    }

    /**
     * 得到该场比赛的转速数据,偶数为左边玩家的转速,奇数为又边玩家的转速
     *
     * @return
     */
    public List<Integer> getListdata() {
        return listdata;
    }

    public void setListdata(List<Integer> listdata) {
        this.listdata = listdata;
    }
}
