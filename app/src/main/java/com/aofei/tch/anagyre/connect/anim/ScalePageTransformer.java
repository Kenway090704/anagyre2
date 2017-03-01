package com.aofei.tch.anagyre.connect.anim;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.aofei.tch.anagyre.connect.ui.ConnectFragment;

public class ScalePageTransformer implements ViewPager.PageTransformer {
    public static final String TAG = "ScalePageTransformer";

    public static final float MAX_SCALE = 1.2f;
    public static final float MIN_SCALE = 0.8f;

    /**
     * 核心就是实现transformPage(View page, float position)这个方法
     **/
    @Override
    public void transformPage(View page, float position) {

//        LogUtils.e(TAG, Debug.line(new Exception()) + "hang, position = " + position + " tag = " + page.getTag());

        if (ConnectFragment.isDelete == true) {

//            &&(position == ConnectFragment.isDeletePosition || position == (ConnectFragment.isDeletePosition - 1))
            if (ConnectFragment.isStartorLeft == false) {

                if ((int)page.getTag() == 0){
                    position = 0;
                }else {
                    position = -1;
                }

            } else if (ConnectFragment.isStartorLeft == true && ((int) page.getTag()) == ConnectFragment.totalBall) {
//                if (position == ConnectFragment.isDeletePosition || position >= 1) {
                    position = 0;
//                }
            } else if (ConnectFragment.isStartorLeft == true && ((int) page.getTag()) != ConnectFragment.totalBall)
                position = -1;

        }
        if (position < -1) {
            position = -1;
        } else if (position > 1) {
            position = 1;
        }
        float tempScale = position < 0 ? 1 + position : 1 - position;

        float slope = (MAX_SCALE - MIN_SCALE) / 1;
        //一个公式
        float scaleValue = MIN_SCALE + tempScale * slope;

        if (scaleValue != 1.2f){
            page.setAlpha(0.8f);
        }else {
            page.setAlpha(1.0f);
        }

        page.setScaleX(scaleValue);
        page.setScaleY(scaleValue);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {

            page.getParent().requestLayout();
        }

    }
}
