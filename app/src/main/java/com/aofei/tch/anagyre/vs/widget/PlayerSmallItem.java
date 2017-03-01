package com.aofei.tch.anagyre.vs.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aofei.tch.anagyre.R;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;
import com.aofei.tch.anagyre.vs.bean.PlayerInfo;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by kenway on 16/12/6 14:30
 * Email : xiaokai090704@126.com
 */

public class PlayerSmallItem extends LinearLayout {
    private  static final  String TAG="PlayerSmallItem";
    private RoundedImageView imageView;
    private TextView tvName;
    private  TextView tvId;
    private PlayerInfo info;
    public PlayerSmallItem(Context context) {
        super(context);
        initView(context);
    }
    public PlayerSmallItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);

    }



    /**
     * 初始化布局
     * @param context
     */
    private  void initView(Context context){
        View view=  LayoutInflater.from(context).inflate(R.layout.widget_player_noconnect,this);
             //获取屏幕的宽高
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        imageView= (RoundedImageView) view.findViewById(R.id.iv_nav_small_icon);

        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.height= (int) (screenWidth*0.070);
        layoutParams.width=  (int) (screenWidth*0.070);
        imageView.setLayoutParams(layoutParams);


        tvName= (TextView) view.findViewById(R.id.tv_nav_small_name);
        tvId= (TextView) view.findViewById(R.id.tv_nav_small_id);
    }


    /**
     * 获取控件的信息
     * @return
     */
    public PlayerInfo getPlayerInfo() {
        return info;
    }

    /**
     * 设置控件的信息
     * @param info
     */
    public void setPlayerInfo(PlayerInfo info) {
        this.info = info;
        Log.e(TAG,"PhotoSamllID"+info.getPhotoSamllID()+" ");
        imageView.setImageResource(info.getPhotoSamllID());
        imageView.setBorderColor(getResources().getColor(info.getColorID()));
        tvName.setText(info.getName());
        tvId.setText(info.getId());
    }

    /**
     * 获取头像控件
     * @return
     */
    public ImageView getImageView() {
        return imageView;
    }


}
