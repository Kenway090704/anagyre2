package com.aofei.tch.anagyre.vs.bean;

import com.aofei.tch.anagyre.R;

/**
 * 玩家信息
 * Created by kenway on 16/12/6 14:18
 * Email : xiaokai090704@126.com
 */

public class PlayerInfo {
    private String name;//玩家姓名
    private String id;//玩家id
    private String exValue;//经验值
    private int colorID=R.color.white;//玩家代表的颜色
    private int ballNum;//玩家与适配器对码时的球号
    private int photoBigID= R.mipmap.player_bg;//玩家大头像
    private int photoSamllID=R.mipmap.photo_head;//玩家小头像
    public PlayerInfo() {

    }
    @Override
    public String toString() {
        return "PlayerInfo{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", exValue=" + exValue +
                ", color=" + colorID +
                ", ballNum=" + ballNum +
                ", photoBigID=" + photoBigID +
                ", photoSamllID=" + photoSamllID +
                '}';
    }

    /**
     * 玩家经验值
     * @return
     */
    public String getExValue() {
        return exValue;
    }

    public void setExValue(String exValue) {
        this.exValue = exValue;
    }
    /**
     * 玩家球号
     * @return
     */
    public int getBallNum() {
        return ballNum;
    }

    public void setBallNum(int ballNum) {
        this.ballNum = ballNum;
    }

    public int getPhotoBigID() {
        return photoBigID;
    }

    public void setPhotoBigID(int photoBigID) {
        this.photoBigID = photoBigID;
    }

    public int getPhotoSamllID() {
        return photoSamllID;
    }

    public void setPhotoSamllID(int photoSamllID) {
        this.photoSamllID = photoSamllID;
    }

    /**
     * 玩家姓名
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    /**
     * 玩家id
     * @return
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    /**
     * 玩家经验值
     * @return
     */
//    public int getValue() {
//        return exValue;
//    }
//
//    public void setValue(int exValue) {
//        this.exValue = exValue;
//    }

    /**
     * 玩家球号对应的颜色
     * @return
     */
    public int getColorID() {
        return colorID;
    }

    public void setColor(int colorID) {
        this.colorID = colorID;
    }








}
