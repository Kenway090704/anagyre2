package com.aofei.tch.anagyre.connect.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.aofei.tch.anagyre.connect.util.SpeedScroller;
import com.aofei.tch.anagyre.other.utils.Debug;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;

import java.lang.reflect.Field;


/**
 * Created by wujian 15/9/27.
 */
public class ClipViewPager extends ViewPager {

    public static final String TAG = "ClipViewPager";

    public ClipViewPager(Context context) {
        super(context);
    }

    public ClipViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private View preView;
    private int preIndex;
     private int  preev =0 ;
private int tag = -1;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {


            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                preView = viewOfClickOnScreen(ev);
                if (preView!=null) {
                    tag = (int) preView.getTag();
                }

                preev = (int) ev.getX();
                LogUtils.e(TAG, Debug.line(new Exception()) + "hang, NowTag = "  + "preev = "+ preev);

            }

        if (ev.getAction() == MotionEvent.ACTION_UP) {


            View view = viewOfClickOnScreen(ev);


            LogUtils.e(TAG, Debug.line(new Exception()) + "hang, NowTag = "  + "ev = "+ ev);

            if (view != null) {

                if (preev != 0 ) {
                    LogUtils.e(TAG, Debug.line(new Exception()) + "hang, Curre = " + getCurrentItem() + "num = "+ getChildCount()+"tag: " + tag + "reX "+ preev + "X "+ev.getX());
                    if ( (preev > (int)ev.getRawX() && getCurrentItem() == getChildCount() - 1 && ((tag == getCurrentItem() - 1) ||tag == -1)) || (preev < ev.getRawX() && getCurrentItem() == 0 && tag == 1)    ) {
                        LogUtils.e(TAG, Debug.line(new Exception()) + "hang, NowTag = "  + "ev = "+ ev);

                    } else {

                        int index = (int) view.getTag();

                        if (getCurrentItem() != index && index == preIndex) {


                            setCurrentItem(index);


                        }

                    }
                } else {
                    int index = (int) view.getTag();

                    if (getCurrentItem() != index && index == preIndex) {


                        setCurrentItem(index);


                    }
                }


            }
        }


        return super.dispatchTouchEvent(ev);
    }


    /**
     * @param ev
     * @return
     */
    private View viewOfClickOnScreen(MotionEvent ev) {
        int childCount = getChildCount();


        float x = ev.getRawX();
        float y = ev.getRawY();


//        LogUtils.e(TAG, Debug.line(new Exception()) + "hang, count = " + getChildCount());


        int[] location = new int[2];
        for (int i = 0; i < childCount; i++) {

            View v = findViewWithTag(i);
            if (v == null) {
                return null;
            }
            v.getLocationOnScreen(location);
            int minX = location[0];
            int minY = getTop();

            int maxX = location[0] + v.getWidth();

            int maxY = getBottom();


            if ((x > minX && x < maxX) && (y > minY && y < maxY)) {
                LogUtils.e(TAG, Debug.line(new Exception()) + "hang, Tag = " + v.getTag());
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    preIndex = (int) v.getTag();
                }
                return v;
            }
        }
        return null;
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int height = 0;
//
//      int ww =  ScreenUtils.getScreenHeightPx(getContext());
//      int hh = ScreenUtils.getScreenWidthPx(getContext());
//     int a =    ScreenUtils.getDiptoPx(getContext(),ww);
//      int b =  ScreenUtils.getDiptoPx(getContext(),hh);
//
////        findViewById(R.id.)
//
////     int wwV = ScreenUtils.getViewHeightPx(this);
////        int hhV = ScreenUtils.getViewWidthPx(this);
//
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//            int h = child.getMeasuredHeight(); //player_img
//            int w = child.getMeasuredWidth();
//
//            int c =    ScreenUtils.getDiptoPx(getContext(),h);
//            int d =  ScreenUtils.getDiptoPx(getContext(),w);
//
//            if (h > height)
//                height = h;
//        }
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }


    /**
     * 利用java反射机制，将自定义Scroll和ViewPager结合来调节ViewPager的滑动效果
     **/
    public void setSpeedScroller(int duration) {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            SpeedScroller scroller = new SpeedScroller(this.getContext(),
                    new AccelerateInterpolator());
            mScroller.set(this, scroller);
            scroller.setmDuration(duration);
        } catch (NoSuchFieldException e) {

        } catch (IllegalArgumentException e) {

        } catch (IllegalAccessException e) {

        }
    }
}