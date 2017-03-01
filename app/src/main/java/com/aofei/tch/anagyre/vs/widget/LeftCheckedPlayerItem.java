package com.aofei.tch.anagyre.vs.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aofei.tch.anagyre.R;
import com.aofei.tch.anagyre.vs.bean.PlayerInfo;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

/**
 * 双人对战中左边布局自定义类
 * Created by kenway on 16/12/12 11:06
 * Email : xiaokai090704@126.com
 */

public class LeftCheckedPlayerItem extends PercentRelativeLayout {
    public LeftCheckedPlayerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * 控件初始化
     *
     * @param context
     */
    private ImageView player1_photo_show;
    private ImageView player1_tuoluo_show;
    private ImageView vs_player1_result;
    private RoundedImageView iv_vs_content_player1;
    private RoundedImageView vs_player1_roundedImageView;
    private  PercentLinearLayout vs_player1_info;
    private TextView vs_player1_name, vs_player1_ID, vs_player1_exp;
    private Button btn_fragment_vs_start;

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.widget_leftchecked, this);
        player1_photo_show = (ImageView) view.findViewById(R.id.player1_photo_show);
        player1_tuoluo_show = (ImageView) view.findViewById(R.id.player1_tuoluo_show);
        vs_player1_result = (ImageView) view.findViewById(R.id.vs_player1_result);
        iv_vs_content_player1 = (RoundedImageView) view.findViewById(R.id.iv_vs_content_player1);//圆形图标,拖动的那个区域

        vs_player1_info= (PercentLinearLayout) view.findViewById(R.id.vs_player1_info);
        vs_player1_roundedImageView = (RoundedImageView) view.findViewById(R.id.vs_player1_roundedImageView);
        vs_player1_name = (TextView) view.findViewById(R.id.vs_player1_name);
        vs_player1_ID = (TextView) view.findViewById(R.id.vs_player1_ID);
        vs_player1_exp = (TextView) view.findViewById(R.id.vs_player1_exp);

        btn_fragment_vs_start = (Button) view.findViewById(R.id.btn_fragment_vs_start);//开始按钮
    }

    /**
     * 当拖动的控件进入左边区域时,使用相关信息刷新界面
     * @param info
     */
    public void setPlayerInfo(PlayerInfo info) {
        player1_photo_show.setImageResource(info.getPhotoBigID());
        player1_tuoluo_show.setVisibility(VISIBLE);
        vs_player1_result.setVisibility(INVISIBLE);
        iv_vs_content_player1.setBorderColor(getResources().getColor(info.getColorID()));
        iv_vs_content_player1.setImageResource(info.getPhotoSamllID());
        vs_player1_info.setVisibility(INVISIBLE);
        vs_player1_roundedImageView.setBorderColor(getResources().getColor(info.getColorID()));
        vs_player1_roundedImageView.setImageResource(info.getPhotoSamllID());
        vs_player1_name.setText(info.getName());
        vs_player1_ID.setText(info.getId());
        vs_player1_exp.setText(info.getExValue());


    }

    /**
     * 回复原始状态
     */
    public  void init(){


        player1_photo_show.setImageResource(R.mipmap.tuoluo_open_bg);
        player1_tuoluo_show.setVisibility(INVISIBLE);
        vs_player1_result.setVisibility(INVISIBLE);

        iv_vs_content_player1.setBorderColor(Color.argb(0,0,0,0));
        iv_vs_content_player1.setImageResource(R.mipmap.play_fight_big);
        iv_vs_content_player1.setVisibility(VISIBLE);

        vs_player1_info.setVisibility(INVISIBLE);


        vs_player1_name.setText("未连接");
        vs_player1_ID.setText("ID");
        vs_player1_exp.setText("经验值");

        btn_fragment_vs_start.setVisibility(VISIBLE);

    }

    /**
     * 比赛开始中,各个控件的状态
     */
    public  void startGameingViews(){

        vs_player1_result.setVisibility(INVISIBLE);
        iv_vs_content_player1.setVisibility(INVISIBLE);

        vs_player1_info.setVisibility(VISIBLE);


        btn_fragment_vs_start.setVisibility(INVISIBLE);
    }

    /**
     * 比赛结束
     * @param isWin    胜---true  输----false
     */
    public  void  gameEnd(boolean isWin){
        if(isWin){
            vs_player1_result.setImageResource(R.mipmap.vs_win);
        }else {
            vs_player1_result.setImageResource(R.mipmap.vs_lost);
        }
        vs_player1_result.setVisibility(VISIBLE);


        iv_vs_content_player1.setVisibility(INVISIBLE);

        vs_player1_info.setVisibility(VISIBLE);

        btn_fragment_vs_start.setVisibility(INVISIBLE);
    }

    /**
     * 获取圆形头像
     * @return
     */
    public RoundedImageView getRoundedImageViewLeft() {
        return iv_vs_content_player1;
    }

    /**
     * 获取开始按钮
     * @return
     */
    public Button getButtonStart() {
        return btn_fragment_vs_start;
    }
}
