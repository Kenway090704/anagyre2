package com.aofei.tch.anagyre.other.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.aofei.tch.anagyre.R;

/**
 * 加载对话框
 * Created by kenway on 16/12/19 11:09
 * Email : xiaokai090704@126.com
 */

public class CustomLoadingDialog extends Dialog {
    private Context mContext;

    public CustomLoadingDialog(Context context) {
        this(context, R.style.dialog_loading);
        mContext=context;
        ImageView imageView = (ImageView) findViewById(R.id.dialog_loading_circle_iv);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_loading_rotate);
        imageView.startAnimation(anim);

    }

    public CustomLoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        // 设置对话框的布局
        setContentView(R.layout.dialog_loading);
        ImageView imageView = (ImageView) findViewById(R.id.dialog_loading_circle_iv);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_loading_rotate);
        imageView.startAnimation(anim);
    }
    /**
     * 开启旋转动画
     */
    public  void show2(){
        ImageView imageView = (ImageView) findViewById(R.id.dialog_loading_circle_iv);
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.anim_loading_rotate);
        imageView.startAnimation(anim);
        show();
    }
}
