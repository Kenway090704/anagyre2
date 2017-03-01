package com.aofei.tch.anagyre.other.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * 关于屏幕的工具类
 * 1.可以判断横屏还是竖屏
 * Created by kenway on 16/11/21 10:01
 * Email : xiaokai090704@126.com
 */

public class ScreenUtils {
    /**
     * 该方法是判断是否为横屏
     * @param context
     * @return  true--横屏  false--竖屏
     */
    public static boolean isScreenChange(Context context) {
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation ; //获取屏幕方向
        if(ori == mConfiguration.ORIENTATION_LANDSCAPE){
            return true;
        }else if(ori == mConfiguration.ORIENTATION_PORTRAIT){
            return false;
        }
        return false;
    }
    /**
     *
     * @param context
     * @return  int---像素
     */
    public  static  int getScreenHeightPx(Context context){
        //取得窗口属性
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric= new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        int width=metric.widthPixels;
        int height= metric.heightPixels;//获取的是像数
         return  height;
    }
  /**
     * 获取屏幕宽的像素
     * @param context
     * @return  int---像素
     */
    public  static  int getScreenWidthPx(Context context){
        //取得窗口属性
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric= new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        int width=metric.widthPixels;
         return  width;
    }

    /**
     * 将Px装转换为Dip
     * @param context
     * @param pxValue  int ---px
     * @return   int ---dip
     */
     public  static  int getPxtoDip(Context context,int pxValue){
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int)(pxValue / scale + 0.5f);
      }
    /**
     * 将Dip装转换为Px
     * @param context
     * @param pxValue  int ---px
     * @return   int ---dip
     */
     public  static  int getDiptoPx(Context context,int pxValue){
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int)(pxValue / scale + 0.5f);
      }

    /**
     * 获取控件的高
     * @param view
     * @return px
     */
    public  static  int getViewHeightPx(View view){
        //取得窗口属性
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height =view.getMeasuredHeight();
        return  height;
    }
    /**
     * 获取控件的宽
     * @param view
     * @return px
     */
    public  static  int getViewWidthPx(View view){
        //取得窗口属性
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int width =view.getMeasuredWidth();
        return  width;
    }
}
