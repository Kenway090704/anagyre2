package com.aofei.tch.anagyre.multiplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aofei.tch.anagyre.R;

/**
 * Created by xianwei on 16/12/13.
 */

public class PlayerInfoLeft extends RelativeLayout {

    private ImageView iv_info_left_icon;
    private ImageView iv_info_left_blood;
    private TextView tv_info_left_name;
    private TextView tv_info_left_id;
    private TextView tv_info_left_blood_num;

    public PlayerInfoLeft(Context context) {
        super(context);
        initView(context);
    }

    public PlayerInfoLeft(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.widget_player_noconnect_mulplayer_left,this);
        iv_info_left_icon = (ImageView) findViewById(R.id.iv_info_left_icon);
        iv_info_left_blood = (ImageView) findViewById(R.id.iv_info_left_blood);
        tv_info_left_name = (TextView) findViewById(R.id.tv_info_left_name);
        tv_info_left_id = (TextView) findViewById(R.id.tv_info_left_id);
        tv_info_left_blood_num = (TextView) findViewById(R.id.tv_info_left_blood_num);
    }

    public void setData(){

    }
}
