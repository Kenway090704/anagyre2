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
 * 双人对战中右边布局自定义类
 * Created by kenway on 16/12/12 11:06
 * Email : xiaokai090704@126.com
 */

public class RightCheckedPlayerItem extends PercentRelativeLayout {
    public RightCheckedPlayerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * 控件初始化
     *
     * @param context
     */
    private ImageView player2_photo_show;
    private ImageView player2_tuoluo_show;
    private ImageView vs_player2_result;
    private RoundedImageView iv_vs_content_player2;
    private RoundedImageView vs_player2_roundedImageView;
    private  PercentLinearLayout vs_player2_info;
    private TextView vs_player2_name, vs_player2_ID, vs_player2_exp;
    private Button btn_fragment_vs_reset;

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.widget_rightchecked, this);
        player2_photo_show = (ImageView) view.findViewById(R.id.player2_photo_show);
        player2_tuoluo_show = (ImageView) view.findViewById(R.id.player2_tuoluo_show);
        vs_player2_result = (ImageView) view.findViewById(R.id.vs_player2_result);
        iv_vs_content_player2 = (RoundedImageView) view.findViewById(R.id.iv_vs_content_player2);//圆形图标,拖动的那个区域

        vs_player2_info= (PercentLinearLayout) view.findViewById(R.id.vs_player2_info);
        vs_player2_roundedImageView = (RoundedImageView) view.findViewById(R.id.vs_player2_roundedImageView);
        vs_player2_name = (TextView) view.findViewById(R.id.vs_player2_name);
        vs_player2_ID = (TextView) view.findViewById(R.id.vs_player2_ID);
        vs_player2_exp = (TextView) view.findViewById(R.id.vs_player2_exp);

        btn_fragment_vs_reset = (Button) view.findViewById(R.id.btn_fragment_vs_reset);//开始按钮
    }

    /**
     * 当拖动的控件进入左边区域时,使用相关信息刷新界面
     * @param info
     */
    public void setPlayerInfo(PlayerInfo info) {
        player2_photo_show.setImageResource(info.getPhotoBigID());
        player2_tuoluo_show.setVisibility(VISIBLE);
        vs_player2_result.setVisibility(INVISIBLE);
        iv_vs_content_player2.setBorderColor(getResources().getColor(info.getColorID()));
        iv_vs_content_player2.setImageResource(info.getPhotoSamllID());

        vs_player2_info.setVisibility(INVISIBLE);
        vs_player2_roundedImageView.setBorderColor(getResources().getColor(info.getColorID()));
        vs_player2_roundedImageView.setImageResource(info.getPhotoSamllID());
        vs_player2_name.setText(info.getName());
        vs_player2_ID.setText(info.getId());
        vs_player2_exp.setText(info.getExValue());

    }

    /**
     * 回复原始状态
     */
    public  void init(){


        player2_photo_show.setImageResource(R.mipmap.tuoluo_open_bg);
        player2_tuoluo_show.setVisibility(INVISIBLE);
        vs_player2_result.setVisibility(INVISIBLE);

        iv_vs_content_player2.setBorderColor(Color.argb(0,0,0,0));
        iv_vs_content_player2.setImageResource(R.mipmap.play_fight_big);
        iv_vs_content_player2.setVisibility(VISIBLE);

        vs_player2_info.setVisibility(INVISIBLE);
        vs_player2_name.setText("未连接");
        vs_player2_ID.setText("ID");
        vs_player2_exp.setText("经验值");

        btn_fragment_vs_reset.setVisibility(VISIBLE);

    }

    /**
     * 比赛开始中,各个控件的状态
     */
    public  void startGameingViews(){

        vs_player2_result.setVisibility(INVISIBLE);
        iv_vs_content_player2.setVisibility(INVISIBLE);

        vs_player2_info.setVisibility(VISIBLE);

        btn_fragment_vs_reset.setText("下一场");
        btn_fragment_vs_reset.setVisibility(VISIBLE);
    }

    /**
     * 比赛结束
     * @param isWin    胜---true  输----false
     */
    public  void  gameEnd(boolean isWin){
        if(isWin){
            vs_player2_result.setImageResource(R.mipmap.vs_win);
        }else {
            vs_player2_result.setImageResource(R.mipmap.vs_lost);
        }
        vs_player2_result.setVisibility(VISIBLE);


        iv_vs_content_player2.setVisibility(INVISIBLE);

        vs_player2_info.setVisibility(VISIBLE);


        btn_fragment_vs_reset.setVisibility(VISIBLE);
    }

    /**
     * 获取圆形头像
     * @return
     */
    public RoundedImageView getRoundedImageViewRight() {
        return iv_vs_content_player2;
    }

    /**
     * 获取开始按钮
     * @return
     */
    public Button getButtonReset() {
        return btn_fragment_vs_reset;
    }
}
