package com.aofei.tch.anagyre.other.adaper;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aofei.tch.anagyre.R;
import com.aofei.tch.anagyre.other.bean.PlayerInfo;
import com.aofei.tch.anagyre.other.ui.DrawerSideLayout;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by xianwei on 16/12/20.
 */

public class PlayerAdapter extends BaseAdapter implements View.OnTouchListener {

    private Context mContext;

    private PlayerInfo[] mPlayerInfos;
    private PlayerInfo[] mPlayerInfosIgnor;

    public PlayerAdapter(Context mContext, PlayerInfo[] mPlayerInfos, PlayerInfo[] mPlayerInfosIgnor) {
        this.mContext = mContext;
        this.mPlayerInfos = mPlayerInfos;
        this.mPlayerInfosIgnor = mPlayerInfosIgnor;
    }

    @Override
    public int getCount() {
        int i = 0;
        for (int j = 0; j < DrawerSideLayout.MAX_PLAYER_NUM; j++) {
            if (mPlayerInfosIgnor[i] != null) {
                i++;
            }
        }
        return mPlayerInfos.length - i;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.widget_player_noconnect,
                    null);
            holder.iv_nav_small_icon = (RoundedImageView) convertView
                    .findViewById(R.id.iv_nav_small_icon);
            holder.iv_nav_small_icon.setOnTouchListener(this);
            holder.tv_nav_small_name = (TextView) convertView
                    .findViewById(R.id.tv_nav_small_name);
            holder.tv_nav_small_id = (TextView) convertView
                    .findViewById(R.id.tv_nav_small_id);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        for (int i = 0; i <= position; i++) {
            if (mPlayerInfosIgnor[i] != null) {
                position++;
            }
        }
        holder.iv_nav_small_icon.setImageResource(R.mipmap.nav_head_noconnect);
        holder.tv_nav_small_name.setText("未绑定");
        holder.tv_nav_small_id.setText("ID:xxxxxxxxx");
        holder.iv_nav_small_icon.setTag(position);
        PlayerInfo info = mPlayerInfos[position];
        if (info != null) {
            holder.iv_nav_small_icon.setImageResource(info.getPhotoSamllID());
            holder.tv_nav_small_name.setText(info.getName());
            holder.tv_nav_small_id.setText(info.getId());
        }
        return convertView;
    }

    class ViewHolder {
        private RoundedImageView iv_nav_small_icon;
        private TextView tv_nav_small_name;
        private TextView tv_nav_small_id;
    }


    //popupWindow初始位置
    private PopupWindow mPopupWindow;
    private int mPopupWindowTop;
    private int mPopupWindowLeft;
    //是否实现拖动效果
    private boolean isFirstJudge;

    int lastX = 0;
    int lastY = 0;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (mPlayerInfos[(int) view.getTag()] == null) {
            view.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        }
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //给侧滑栏中圆形头像实现拖动效果
                view.getParent().requestDisallowInterceptTouchEvent(true);
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                mPopupWindowLeft = location[0];
                mPopupWindowTop = location[1];
                initPopupWindow(view);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                isFirstJudge = true;
                break;
            case MotionEvent.ACTION_MOVE:

                int dx = (int) (event.getRawX() - lastX);
                int dy = (int) (event.getRawY() - lastY);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                //动态改变popopwindow的位置，实现拖动效果
                if (Math.abs(dy) > 2 * Math.abs(dx) && isFirstJudge) {
                    isFirstJudge = false;
                    mPopupWindow.dismiss();
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
                if (dx != 0 || dy != 0) {
                    isFirstJudge = false;
                    movePopupWindow(view, lastX, lastY);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (mPopupWindow != null) {
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    dismissPopupWindow(view, lastX, lastY);
                }
                break;
        }
        return false;
    }

    /**
     * 初始化popupwindow
     */
    private void initPopupWindow(View view) {
        //通过view获取到相关的参数
        PlayerInfo info = mPlayerInfos[(int) view.getTag()];
        RoundedImageView iv2 = new RoundedImageView(mContext);
        iv2.setBorderColor(mContext.getResources().getColor(info.getColorID()));
        //iv2.setBorderColor(Color.RED);
        iv2.setBorderWidth(R.dimen.round_width);
        iv2.setOval(true);
        iv2.setImageResource(info.getPhotoSamllID());
        //iv2.setImageResource(R.mipmap.headicon1);
        mPopupWindow = new PopupWindow(iv2, view.getWidth(), view.getHeight());
        mPopupWindow.showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.LEFT | Gravity.TOP, mPopupWindowLeft, mPopupWindowTop);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setTouchable(false);
        mPopupWindow.update();
    }

    /**
     * 动态改变popupwindow位置
     *
     * @param view    点击得控件
     * @param centerX 手指中心点X坐标
     * @param centerY 手指中心点Y坐标
     */
    private void movePopupWindow(View view, int centerX, int centerY) {
        mPopupWindow.update(centerX - view.getWidth() / 2, centerY - view.getHeight() / 2, -1, -1);
    }

    /**
     * 清除popupwindow并传递其中心点位置坐标
     */
    private void dismissPopupWindow(View view, int centerX, int centerY) {
        mPopupWindow.dismiss();
        if (mListener != null) {
            mListener.ballLastPosition(mPlayerInfos[(int) view.getTag()], centerX, centerY);
        }
    }

    private DrawerSideLayout.OnDrawerEventListener mListener;

    public void setOnDrawerEventListener(DrawerSideLayout.OnDrawerEventListener mListener) {
        this.mListener = mListener;
    }
}
