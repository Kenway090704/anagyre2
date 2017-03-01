package com.aofei.tch.anagyre.other.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.aofei.tch.anagyre.R;
import com.aofei.tch.anagyre.other.adaper.PlayerAdapter;
import com.aofei.tch.anagyre.other.bean.PlayerInfo;

/**
 * Created by xianwei on 16/12/20.
 */

public class DrawerSideLayout extends RelativeLayout implements CompoundButton.OnCheckedChangeListener {

    public final static int MAX_PLAYER_NUM = 8;

    private CheckBox cb_drawer_head_match;
    private CheckBox cb_drawer_head_delete;
    private ListView lv_drawer_player;
    private PlayerInfo[] mPlayerInfos;
    private PlayerInfo[] mPlayerInfosIgnor;
    private PlayerAdapter mPlayerAdapter;

    private void initView(Context context) {
        View.inflate(context, R.layout.widget_drawer_side_layout, this);
        cb_drawer_head_match = (CheckBox) findViewById(R.id.cb_drawer_head_match);
        cb_drawer_head_delete = (CheckBox) findViewById(R.id.cb_drawer_head_delete);
        lv_drawer_player = (ListView) findViewById(R.id.lv_drawer_player);
        cb_drawer_head_match.setOnCheckedChangeListener(this);
        cb_drawer_head_delete.setOnCheckedChangeListener(this);
        mPlayerInfos = new PlayerInfo[MAX_PLAYER_NUM];
        mPlayerInfosIgnor = new PlayerInfo[MAX_PLAYER_NUM];
        mPlayerAdapter = new PlayerAdapter(context, mPlayerInfos, mPlayerInfosIgnor);
        lv_drawer_player.setAdapter(mPlayerAdapter);
    }

    public DrawerSideLayout(Context context) {
        super(context);
        initView(context);
    }

    public DrawerSideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DrawerSideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 数据刷新，更新ui
     */
    public void notifyDataSetChanged() {
        for (int i = 0; i < MAX_PLAYER_NUM; i++) {
            if (mPlayerInfosIgnor[i] != null && mPlayerInfos[i] == null) {
                mPlayerInfosIgnor[i] = null;
            }
        }
        mPlayerAdapter.notifyDataSetChanged();
    }

    /**
     * 设置球的参数
     * @param mPlayerInfos 数组长度应为8位
     */
    public void setData(PlayerInfo[] mPlayerInfos) {
        this.mPlayerInfos = mPlayerInfos;
        for (int i = 0; i < MAX_PLAYER_NUM; i++) {
            if (mPlayerInfosIgnor[i] != null && mPlayerInfos[i] == null) {
                mPlayerInfosIgnor[i] = null;
            }
        }
        mPlayerAdapter.notifyDataSetChanged();
    }

    /**
     * 添加参与比赛的球
     *
     * @param info 球的信息
     */
    public void addPlayer(PlayerInfo info) {
        mPlayerInfosIgnor[info.getBallNum()] = info;
        mPlayerAdapter.notifyDataSetChanged();
    }

    /**
     * 添加参与比赛的球
     *
     * @param ballNum 参与比赛的球号
     */
    public void addPlayer(int ballNum) {
        mPlayerInfosIgnor[ballNum] = mPlayerInfos[ballNum];
        mPlayerAdapter.notifyDataSetChanged();
    }

    /**
     * 去除参与比赛的球
     *
     * @param info 球的信息
     */
    public void removePlayer(PlayerInfo info) {
        mPlayerInfosIgnor[info.getBallNum()] = null;
        mPlayerAdapter.notifyDataSetChanged();
    }

    /**
     * 去除参与比赛的球
     *
     * @param ballNum 参与比赛的球号
     */
    public void removePlayer(int ballNum) {
        mPlayerInfosIgnor[ballNum] = null;
        mPlayerAdapter.notifyDataSetChanged();
    }

    /**
     * 去除所有参与比赛的球
     */
    public void clearPlayer() {
        for (int i = 0; i < MAX_PLAYER_NUM; i++) {
            mPlayerInfosIgnor[i] = null;
        }
        mPlayerAdapter.notifyDataSetChanged();
    }

    private OnDrawerEventListener mListener;

    public interface OnDrawerEventListener {
        void matchStateChange(boolean isChecked);

        void deleteStateChange(boolean isChecked);

        void ballLastPosition(PlayerInfo info, int centerX, int centerY);
    }

    public void setOnDrawerEventListener(OnDrawerEventListener mListener) {
        this.mListener = mListener;
        mPlayerAdapter.setOnDrawerEventListener(mListener);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_drawer_head_match:
                if (mListener != null) {
                    mListener.matchStateChange(isChecked);
                }
                break;
            case R.id.cb_drawer_head_delete:
                if (mListener != null) {
                    mListener.deleteStateChange(isChecked);
                }
                break;
            default:

                break;
        }
    }
}
