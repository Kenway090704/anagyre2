package com.aofei.tch.anagyre.multiplayer.ui;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aofei.tch.anagyre.R;
import com.aofei.tch.anagyre.multiplayer.utils.DensityUtil;
import com.aofei.tch.anagyre.multiplayer.widget.GifView;
import com.aofei.tch.anagyre.other.ui.DrawerSideLayout;
import com.aofei.tch.anagyre.other.ui.BaseFragment;
import com.aofei.tch.anagyre.vs.widget.CustomDrawerLayout;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;

/**
 * Created by kenway on 16/11/18 10:50
 * Email : xiaokai090704@126.com
 */

public class MultiplayerFragment extends BaseFragment implements View.OnClickListener {

    //==========主布局===============//
    private PercentRelativeLayout prl_mulplayer_num;
    private PercentLinearLayout pll_mulplayer_content;
    private TextView tv_mulplayer_num;
    private PopupWindow mPromoteNumPopupWindow;
    private CustomDrawerLayout cdl_mulplayer;
    private ImageView iv_mulplayer_drawer_bar;
    private GifView gv_mulplayer;
    private Button bt_mulplayer_start;

    //==========侧边栏布局===============//
    private DrawerSideLayout dsl_mulplayer_drawer;

    //=============设备参数(屏幕宽高)==========//
    private int screenWidth;
    private int[] mGifResource;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mulplayer;
    }

    @Override
    protected void initViews(View root) {
        cdl_mulplayer = (CustomDrawerLayout) root.findViewById(R.id.cdl_mulplayer);
        cdl_mulplayer.setScrimColor(Color.TRANSPARENT);
        prl_mulplayer_num = (PercentRelativeLayout) root.findViewById(R.id.prl_mulplayer_num);
        pll_mulplayer_content = (PercentLinearLayout) root.findViewById(R.id.pll_mulplayer_content);
        tv_mulplayer_num = (TextView) root.findViewById(R.id.tv_mulplayer_num);
        iv_mulplayer_drawer_bar = (ImageView) root.findViewById(R.id.iv_mulplayer_drawer_bar);
        gv_mulplayer = (GifView) root.findViewById(R.id.gv_mulplayer);
        bt_mulplayer_start = (Button) root.findViewById(R.id.bt_mulplayer_start);
        dsl_mulplayer_drawer = (DrawerSideLayout) root.findViewById(R.id.dsl_mulplayer_drawer);
        //=================ScreenWidth and ScreenHeight=================//
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
        mGifResource = new int[]{R.raw.fight_two, R.raw.fight_three, R.raw.fight_four, R.raw.fight_five, R.raw.fight_six, R.raw.fight_seven, R.raw.fight_eight};
    }

    @Override
    protected void initEnvent() {
        prl_mulplayer_num.setOnClickListener(this);
        bt_mulplayer_start.setOnClickListener(this);
        cdl_mulplayer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //The slideOffset range is 0.0-1.0 a relatively whole the width ratio of the drawer
                float scrollWidth = slideOffset * dsl_mulplayer_drawer.getWidth();
                //主布局缩小并且移动
                pll_mulplayer_content.setScaleX(1 - scrollWidth / screenWidth);
                pll_mulplayer_content.setScaleY(1 - scrollWidth / screenWidth);
                pll_mulplayer_content.setTranslationX(-scrollWidth / 2);
                iv_mulplayer_drawer_bar.setVisibility(View.INVISIBLE);
                if (slideOffset == 0 || slideOffset == 1) {
                    iv_mulplayer_drawer_bar.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                iv_mulplayer_drawer_bar.setVisibility(View.VISIBLE);
                iv_mulplayer_drawer_bar.setImageResource(R.mipmap.vs_right_close_bar);
                iv_mulplayer_drawer_bar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cdl_mulplayer.closeDrawer(GravityCompat.END);
                    }
                });
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                iv_mulplayer_drawer_bar.setVisibility(View.VISIBLE);
                iv_mulplayer_drawer_bar.setImageResource(R.mipmap.vs_right_open_bar);
                iv_mulplayer_drawer_bar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cdl_mulplayer.openDrawer(GravityCompat.END);
                    }
                });
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.prl_mulplayer_num:
                showPromoteNumPopup();
                break;
            case R.id.bt_mulplayer_start:
                starGif();
                break;
            default:

                break;
        }
    }

    /**
     * 跳出选择晋级人数框
     */
    private void showPromoteNumPopup() {
        int[] location = new int[2];
        tv_mulplayer_num.getLocationOnScreen(location);
        int mPopupWindowLeft = location[0];
        prl_mulplayer_num.getLocationOnScreen(location);
        int mPopupWindowBottom = location[1];
        ListView lv = new ListView(getContext());
        ArrayList<String> mNum = new ArrayList<>();
        mNum.add("1");
        mNum.add("2");
        mNum.add("3");
        mNum.add("4");
        mNum.add("5");
        mNum.add("6");
        mNum.add("7");
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_promote_num, mNum);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tv_mulplayer_num.setText(i + 1 + "");
                mPromoteNumPopupWindow.dismiss();
            }
        });
        lv.setDividerHeight(0);
        lv.setVerticalScrollBarEnabled(false);
        int height = 7 * DensityUtil.dip2px(getContext(), 25);
        mPromoteNumPopupWindow = new PopupWindow(lv, tv_mulplayer_num.getWidth(), height);
        mPromoteNumPopupWindow.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.mipmap.promote_extend_frame)));
        mPromoteNumPopupWindow.setFocusable(true);
        mPromoteNumPopupWindow.setTouchable(true);
        mPromoteNumPopupWindow.setOutsideTouchable(true);
        mPromoteNumPopupWindow.showAtLocation(cdl_mulplayer, Gravity.LEFT | Gravity.TOP, mPopupWindowLeft, mPopupWindowBottom - height);
    }

    /**
     * 启动GIF动画
     */
    private void starGif() {
        gv_mulplayer.setVisibility(View.VISIBLE);
        int mGifId = Integer.parseInt(tv_mulplayer_num.getText().toString()) - 1;
        gv_mulplayer.setPaused(true);
        gv_mulplayer.setMovieResource(mGifResource[mGifId]);
        gv_mulplayer.setPaused(false);
    }
}
