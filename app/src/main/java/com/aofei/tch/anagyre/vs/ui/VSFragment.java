package com.aofei.tch.anagyre.vs.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.aofei.tch.anagyre.MyApplication;
import com.aofei.tch.anagyre.R;
import com.aofei.tch.anagyre.bluetooth.cmd.CMDContacts;
import com.aofei.tch.anagyre.bluetooth.model.MyBluetoothService;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnStartGameTaskListener;
import com.aofei.tch.anagyre.connect.ui.ConnectFragment;
import com.aofei.tch.anagyre.main.ui.HomeActivity;
import com.aofei.tch.anagyre.multiplayer.utils.DensityUtil;
import com.aofei.tch.anagyre.other.ui.BaseFragment;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;
import com.aofei.tch.anagyre.vs.bean.CompetionInfo;
import com.aofei.tch.anagyre.vs.bean.PlayerInfo;
import com.aofei.tch.anagyre.vs.listener.OnConnectToVsListener;
import com.aofei.tch.anagyre.vs.shared.SaveCompetitionDataPre;
import com.aofei.tch.anagyre.vs.widget.LeftCheckedPlayerItem;
import com.aofei.tch.anagyre.vs.widget.PlayerSmallItem;
import com.aofei.tch.anagyre.vs.widget.RightCheckedPlayerItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kenway on 16/11/18 10:42
 * Email : xiaokai090704@126.com
 */

public class VSFragment extends BaseFragment implements View.OnTouchListener, OnConnectToVsListener, View.OnClickListener {
    private static final String TAG = "VSFragment";
    public static boolean isPlayGaming = true;
    /**
     * ========================xiaokai===========================
     */
    private DrawerLayout mDrawerLayour;
    //==========主布局===============//
    private LinearLayout mRelativeLayout;//content layout
    private LeftCheckedPlayerItem left_palyer_layout;
    private RightCheckedPlayerItem right_palyer_layout;
    private Button btn_start, btn_reset;
    private RoundedImageView iv_vs_player1, iv_vs_player2;
    private Button btn_review;//回放按钮
    private ImageView vs_content_right_bar;

    //==========侧滑栏================//
    private LinearLayout right_drawer;            //drawer layout
    private CheckBox cb_navgation_head_macth, cb_navgation_head_detele;       //drawer head
    private LinearLayout drawer_layout_players;             //drawer players Layout
    private PlayerSmallItem[] drawItems = new PlayerSmallItem[8];
    private int mDrawerWidth;          //drawer width
    private float scrollWidth;        //drawer scroll width
    private List<PlayerInfo> list;           //侧滑栏中玩家信息集合

    //=============设备参数(屏幕宽高)==========//
    private static int screenWidth;
    private static int screenHeight;
    //=============比赛回放================//
    private SaveCompetitionDataPre pre;
    private CompetionInfo competionInfo;//该场比赛对象
    private List<Integer> listData;
    /**
     * ========================end===========================
     */
    private MyBluetoothService service;
    //startGame intetface
    private OnStartGameTaskListener onStartGameTaskListener = new OnStartGameTaskListener() {
        @Override
        public void onStartGameing() {

        }

        @Override
        public void onStartGameFail() {

        }

        @Override
        public void onStartGameSuccessful() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "比赛开始", Toast.LENGTH_SHORT).show();
                    if (mDrawerLayour.isDrawerOpen(GravityCompat.END)) {
                        mDrawerLayour.closeDrawer(GravityCompat.END);
                    }

                    left_palyer_layout.startGameingViews();//开始比赛时左边的布局变化
                    right_palyer_layout.startGameingViews();//
                    btn_reset.setText("下一场");
                    btn_reset.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removeEntry();//清空K线图的布局
                            //停止比赛
                            if (isPlayGaming) {
                                isStartTimer = false;
                                Intent intent = new Intent(getActivity(), MyBluetoothService.class);
                                intent.putExtra(HomeActivity.TAG, CMDContacts.RESQUEST_STOP_VS_COMPETITION);
                                getActivity().startService(intent);
                            }
                            resetPlayer();//重置玩家状态
                        }
                    });

                    //当比赛开始的时候,创建比赛对象
                    competionInfo = new CompetionInfo();
                    listData = new ArrayList();
                }
            });

        }

        @Override
        public void onGameRotateDate(final int[] gameData) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /**使用数据更新UI*/
                    if (choiceplayer1 != null && choiceplayer2 != null && isPlayGaming) {
                        int leftBallNum = choiceplayer1.getPlayerInfo().getBallNum();//左边的球号
                        int rightBallNum = choiceplayer2.getPlayerInfo().getBallNum();//右边的球号
                        LogUtils.e(TAG, "leftBallNum===" + leftBallNum + "rightBallNum==" + rightBallNum);
                        LogUtils.e(TAG, "VSFragment得到了转速!" + gameData[leftBallNum - 1] + " , " + gameData[rightBallNum - 1]);


                        if (isStartTimer) {


                            dyValue = (int) Math.abs(gameData[leftBallNum - 1] - yValue);
                            dyValue1 = (int) Math.abs(gameData[rightBallNum - 1] - yValue1);
                        } else {
                            removeEntry();
                            dyValue = 0;
                            dyValue1 = 0;
                        }
                        yValue = gameData[leftBallNum - 1];
                        yValue1 = gameData[rightBallNum - 1];
                        if (!isStartTimer) {
                            isFirstData = true;
                            mHandler.post(mTimer);
                            isStartTimer = true;
                        }
                        //将 转速保存

                        listData.add(gameData[leftBallNum - 1]);
                        listData.add(gameData[rightBallNum - 1]);

                    }


                }
            });

        }

        @Override
        public void onConnectInstability(final int playerNum) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(getActivity(), playerNum + "号球连接不稳定!", Toast.LENGTH_SHORT);
                    LogUtils.e(TAG, playerNum + "号球连接不稳定");
                }
            });

        }

        @Override
        public void onGameEnd(final int winPlayerNum) {
            //VS中出现获胜者--停止比赛


            isStartTimer = false;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (choiceplayer1 != null && choiceplayer2 != null && winPlayerNum != -1) {
                        //判断是哪一个获胜   winPlayerNum==-1时,是玩家主动结束比赛
                        if (choiceplayer1.getPlayerInfo().getBallNum() == winPlayerNum) {
                            LogUtils.e(TAG, "获胜者为" + winPlayerNum);
                            left_palyer_layout.gameEnd(true);
                            right_palyer_layout.gameEnd(false);
                            Toast.makeText(getActivity(), " 比赛结束: " + choiceplayer1.getPlayerInfo().getName() + "获胜", Toast.LENGTH_SHORT).show();
                        } else {
                            LogUtils.e(TAG, "获胜者为" + winPlayerNum);
                            left_palyer_layout.gameEnd(false);
                            right_palyer_layout.gameEnd(true);
                            Toast.makeText(getActivity(), " 比赛结束: " + choiceplayer2.getPlayerInfo().getName() + "获胜", Toast.LENGTH_SHORT).show();
                        }

                        //将玩家信息 转速保存
                        competionInfo.setLeftPlayer(choiceplayer1.getPlayerInfo());
                        competionInfo.setRightPlayer(choiceplayer2.getPlayerInfo());
                        competionInfo.setListdata(listData);//将转速信息保存到本场比赛的类中
                        pre.saveCompetioninfo(competionInfo);
                    } else {

                    }
                }
            });

        }

        @Override
        public void onStartGameNoResponse() {

        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_vs_xk;
    }

    @Override
    protected void initViews(View root) {
        //ConectFragment与VsFragment通信接口
        ConnectFragment fragment = ((HomeActivity) getActivity()).connectFragment;//
        fragment.setOnConnectToVsListener(this);
        initOtherViews(root);//k线以外的控件 初始化
        initChartViews(root);//k线图的控件   初始化

        pre = SaveCompetitionDataPre.getInstance();//获取保存比赛数据的实体类
    }

    /**
     * k线以外的其他控件
     *
     * @param root
     */
    private void initOtherViews(View root) {

        mDrawerLayour = (DrawerLayout) root.findViewById(R.id.drawer_layout);
        mDrawerLayour.setScrimColor(Color.TRANSPARENT);

        //=================content layout=================//
        mRelativeLayout = (LinearLayout) root.findViewById(R.id.content_frame);
        //left player
        left_palyer_layout = (LeftCheckedPlayerItem) root.findViewById(R.id.vs_left_palyer_Layout);
        iv_vs_player1 = left_palyer_layout.getRoundedImageViewLeft();
        btn_start = left_palyer_layout.getButtonStart();//开始按钮,后面只给一个
        //right player
        right_palyer_layout = (RightCheckedPlayerItem) root.findViewById(R.id.vs_right_palyer_Layout);
        iv_vs_player2 = right_palyer_layout.getRoundedImageViewRight();
        btn_reset = right_palyer_layout.getButtonReset();
        vs_content_right_bar = (ImageView) root.findViewById(R.id.vs_content_right_bar);
        btn_review = (Button) root.findViewById(R.id.btn_fragment_vs_review);
        /**这里先让这个按钮隐藏*/
//        btn_review.setVisibility(View.GONE);
        //=================drawer layout=================//
        right_drawer = (LinearLayout) root.findViewById(R.id.right_drawer);
        cb_navgation_head_macth = (CheckBox) root.findViewById(R.id.cb_navgation_head_macth);
        drawer_layout_players = (LinearLayout) root.findViewById(R.id.drawer_layout_players);
        list = new ArrayList<>();//存放玩家信息的集合

        //=================ScreenWidth and ScreenHeight=================//
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;


        //=================drawer layout width =================//
        //动态改变侧滑宽度
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) right_drawer.getLayoutParams();
        params.width = (int) (screenWidth * 0.120);
        right_drawer.setLayoutParams(params);

        measureView(right_drawer);
        mDrawerWidth = right_drawer.getMeasuredWidth();// 侧滑栏的宽度=270, heigth=586
    }

    @Override
    protected void initEnvent() {

        //给抽屉设置监听
        mDrawerLayour.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //The slideOffset range is 0.0-1.0 a relatively whole the width ratio of the drawer
                scrollWidth = slideOffset * mDrawerWidth;
                //主布局缩小并且移动
                mRelativeLayout.setScaleX(1 - scrollWidth / screenWidth);
                mRelativeLayout.setScaleY(1 - scrollWidth / screenWidth);
                mRelativeLayout.setTranslationX(-scrollWidth / 2);

                vs_content_right_bar.setVisibility(View.INVISIBLE);
                if (slideOffset == 0 || slideOffset == 1) {
                    vs_content_right_bar.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                vs_content_right_bar.setVisibility(View.VISIBLE);
                vs_content_right_bar.setImageResource(R.mipmap.vs_right_close_bar);
                vs_content_right_bar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDrawerLayour.closeDrawer(GravityCompat.END);
                    }
                });
            }

            @Override
            public void onDrawerClosed(final View drawerView) {
                vs_content_right_bar.setVisibility(View.VISIBLE);
                vs_content_right_bar.setImageResource(R.mipmap.vs_right_open_bar);
                vs_content_right_bar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDrawerLayour.openDrawer(GravityCompat.END);
                    }
                });
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        //侧滑栏头部设置点击事件
//        cb_navgation_head_macth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (MyApplication.CONNECTED == true) {
//
//                    if (ishasVsFrament&&b) {
//                        if (list.size() < 8) {
//                            //开始对码
////                            Toast.makeText(getActivity(), "开始对码...", Toast.LENGTH_LONG).show();
//                              Intent intent = new Intent(getActivity(), MyBluetoothService.class);
//                              intent.putExtra(HomeActivity.TAG, CMDContacts.RESQUEST_MATCH_ANAGYRE);
//                              getActivity().startService(intent);
//                        } else {
////                            Toast.makeText(getActivity(), "适配器已满,无法添加玩家", Toast.LENGTH_LONG).show();
//                              cb_navgation_head_macth.setChecked(false);
//                        }
//
//                    } else {
//
//                        if (MyApplication.isMatching) {
//                            //停止对码
//                            Intent intent = new Intent(getActivity(), MyBluetoothService.class);
//                            intent.putExtra(HomeActivity.TAG, CMDContacts.RESQUEST_STOP_MATCH_ANAGYRE);
//                            getActivity().startService(intent);
//                        }
//                    }
//                } else {
//                    cb_navgation_head_macth.setChecked(false);
////                    Toast.makeText(getActivity(), "还没有和适配器对接", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
        //点击开始获取比赛数据
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //在比赛前,先判断是否已经停止对码,和停止删除任务
                if (MyApplication.isMatching) {
                    Toast.makeText(getActivity(), "请停止对码,才能开始比赛!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), MyBluetoothService.class);
                    intent.putExtra(HomeActivity.TAG, CMDContacts.RESQUEST_STOP_MATCH_ANAGYRE);
                    service = ((HomeActivity) getActivity()).myService;//获取Service
                    getActivity().startService(intent);
                } else {
                    //获取当前选中的是哪两个球
                    if (choiceplayer1 != null && choiceplayer2 != null) {
                        int player1Num = choiceplayer1.getPlayerInfo().getBallNum();
                        int player2Num = choiceplayer2.getPlayerInfo().getBallNum();
                        LogUtils.e(TAG, "参与比赛的球号为  player1=" + player1Num + " , player2 = " + player2Num);
                        //开始比赛  startService Mode
                        Intent intent = new Intent(getActivity(), MyBluetoothService.class);
                        intent.putExtra(HomeActivity.TAG, CMDContacts.RESQUEST_START_VS_COMPETITION);
                        intent.putExtra("player1Num", player1Num);
                        intent.putExtra("player2Num", player2Num);
                        service = ((HomeActivity) getActivity()).myService;//获取与HomeActivity绑定的Actviy
                        service.setOnStartGameTaskListener(onStartGameTaskListener);
                        getActivity().startService(intent);
                    } else {
                        mDrawerLayour.openDrawer(GravityCompat.END);
                        Toast.makeText(getContext(), "需要两位玩家才可以开始", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });


        //重置
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPlayer();//重置
            }
        });
        vs_content_right_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayour.openDrawer(Gravity.RIGHT);
            }
        });
        //回放
        btn_review.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_fragment_vs_review:
                getGameReviewData();
                break;
        }
    }

    /**
     * 获取比赛回放数据
     */
    private void getGameReviewData() {

        SaveCompetitionDataPre pre = SaveCompetitionDataPre.getInstance();
        //获取数据
        List<CompetionInfo> infoList = pre.getDataList();//获取所有的比赛信息


        if (null != infoList && infoList.size() != 0) {
            Collections.reverse(infoList);//将获取的比赛信息集合反转
            showGameReviewPopup(infoList);
        } else {
            Toast.makeText(getActivity(), "没有比赛信息", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 展示比赛信息的所有场次
     */
    private PopupWindow mPromoteNumPopupWindow;

    /**
     * 跳出选择所有比赛的信息框
     */
    private void showGameReviewPopup(final List<CompetionInfo> infoList) {
        int[] location = new int[2];
        btn_review.getLocationOnScreen(location);
        int mPopupWindowLeft = location[0] - 50;
        int mPopupWindowBottom = location[1];
        ListView lv = new ListView(getContext());
        ArrayList<String> mNum = new ArrayList<>();
        //遍历这个集合获取每一场比赛的结果
        for (CompetionInfo info : infoList) {
            mNum.add(info.getName());
        }
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_promote_num, mNum);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPromoteNumPopupWindow.dismiss();
                //执行画图和两边玩家的信息展示,让btn_review消失
                CompetionInfo info = infoList.get(i);//获取比赛信息
                List<Integer> listRotates = info.getListdata();//获取比赛转速
                PlayerInfo leftPlayer = info.getLeftPlayer(); //左边玩家信息
                PlayerInfo rightPlayer = info.getRightPlayer(); //右边玩家信息
                left_palyer_layout.setPlayerInfo(leftPlayer);//刷新左右界面
                right_palyer_layout.setPlayerInfo(rightPlayer);
                //开启画图部分
                LogUtils.e(TAG, info.getLeftPlayer().getName() + " ," + info.getRightPlayer().getName() + info.getListdata().toString());
            }
        });
        lv.setDividerHeight(1);
        lv.setVerticalScrollBarEnabled(false);
        int height = 6 * DensityUtil.dip2px(getContext(), 25);
        mPromoteNumPopupWindow = new PopupWindow(lv, btn_review.getWidth() + 100, height);
        mPromoteNumPopupWindow.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.mipmap.promote_extend_frame)));
        mPromoteNumPopupWindow.setFocusable(true);
        mPromoteNumPopupWindow.setTouchable(true);
        mPromoteNumPopupWindow.setOutsideTouchable(true);
        mPromoteNumPopupWindow.showAtLocation(btn_review, Gravity.LEFT | Gravity.TOP, mPopupWindowLeft, mPopupWindowBottom + btn_review.getHeight());
    }

    /**
     * 重置
     */
    private void resetPlayer() {
        btn_reset.setText("重置");
        left_palyer_layout.init();//left layout reset
        right_palyer_layout.init();//right layout reset
        //重置--侧滑栏布局还原
        if (choiceplayer1 != null) {
            choiceplayer1.setVisibility(View.VISIBLE);
            choiceplayer1 = null;
        }
        if (choiceplayer2 != null) {
            choiceplayer2.setVisibility(View.VISIBLE);
            choiceplayer2 = null;
        }
        player2isHas = false;
        player1isHas = false;
        mDrawerLayour.openDrawer(GravityCompat.END);
    }

    private static final int PLAYER_ONE = 0;
    private static final int PLAYER_TWO = 1;

    /**
     * 重置
     */
    private void resetPlayer(int side) {
        btn_reset.setText("重置");

        switch (side) {
            case PLAYER_ONE:
                left_palyer_layout.init();//left layout reset
                //重置--侧滑栏布局还原
                if (choiceplayer1 != null) {
                    choiceplayer1.setVisibility(View.VISIBLE);
                    choiceplayer1 = null;
                }
                player1isHas = false;
                break;
            case PLAYER_TWO:
                right_palyer_layout.init();//right layout reset
                if (choiceplayer2 != null) {
                    choiceplayer2.setVisibility(View.VISIBLE);
                    choiceplayer2 = null;
                }
                player2isHas = false;
                break;
            default:
                break;
        }
        mDrawerLayour.openDrawer(GravityCompat.END);
    }

    /**
     * ===================K线图外的其他代码===================
     */
    //popupWindow初始位置
    private PopupWindow mPopupWindow;
    private int mPopupWindowTop;
    private int mPopupWindowLeft;
    //是否实现拖动效果
    private boolean isFirstJudge;

    int lastX = 0;
    int lastY = 0;

    //从侧滑栏中拖动出来的两个玩家 到 主布局中
    private PlayerSmallItem choiceplayer1;//拖动的到左边圆圈的玩家
    private PlayerSmallItem choiceplayer2;//拖动的到右边的圆圈玩家
    private boolean player1isHas;//left area is or not has player
    private boolean player2isHas;//right area is or not has player

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //给侧滑栏中圆形头像实现拖动效果
                if (view.getId() == R.id.iv_nav_small_icon) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    mPopupWindowLeft = location[0];
                    mPopupWindowTop = location[1];
                    initPopupWindow(view);

                }
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
        PlayerSmallItem playerSmallItem = (PlayerSmallItem) view.getTag();
        PlayerInfo info = playerSmallItem.getPlayerInfo();
        RoundedImageView iv2 = new RoundedImageView(getContext());
        iv2.setBorderColor(getResources().getColor(info.getColorID()));
        iv2.setBorderWidth(R.dimen.round_width);
        iv2.setOval(true);
        iv2.setImageResource(info.getPhotoSamllID());

        mPopupWindow = new PopupWindow(iv2, view.getWidth(), view.getHeight());
        mPopupWindow.showAtLocation(mDrawerLayour, Gravity.LEFT | Gravity.TOP, mPopupWindowLeft, mPopupWindowTop);
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

        //=========第一个的位置==============//
        LeftLayoutUpdate(view, centerX, centerY);

        //=========第二个的位置==============//
        RightLayoutUpdate(view, centerX, centerY);

    }

    /**
     * 主布局中右边玩家布局的更新
     *
     * @param view    被拖动的控件
     * @param centerX 这个控件的中心位置 X
     * @param centerY 这个控件的中心位置 Y
     */
    private void RightLayoutUpdate(View view, int centerX, int centerY) {
        if (iv_vs_player2 == null) {
            LogUtils.e(TAG, "iv_vs_player2控件为空");
            iv_vs_player2 = right_palyer_layout.getRoundedImageViewRight();
        }
        measureView(iv_vs_player2);
        int getMeasuredWidth2 = iv_vs_player2.getMeasuredWidth();
        int getMeasuredHeight2 = iv_vs_player2.getMeasuredHeight();
        int[] location2 = new int[2];
        iv_vs_player2.getLocationOnScreen(location2);

        //判断是否在指定区域,比控件范围大20dp
        if (centerX > location2[0] - 20 && centerX < getMeasuredWidth2 + location2[0] + 20 &&
                centerY > location2[1] - 20 && centerY < getMeasuredHeight2 + location2[1] + 20 && !player2isHas) {
            Toast.makeText(getContext(), "控件已经拖动到右边区域", Toast.LENGTH_SHORT).show();

            choiceplayer2 = (PlayerSmallItem) view.getTag();
            choiceplayer2.setVisibility(View.GONE);
            PlayerInfo info = choiceplayer2.getPlayerInfo();
            //对右边玩家布局进行数据更新
            right_palyer_layout.setPlayerInfo(info);
            player2isHas = true;
        }
    }

    /**
     * 主布局中左边边玩家布局的更新
     *
     * @param view    被拖动的控件
     * @param centerX 这个控件的中心位置 X
     * @param centerY 这个控件的中心位置 Y
     */
    private void LeftLayoutUpdate(View view, int centerX, int centerY) {
        if (iv_vs_player1 == null) {
            iv_vs_player1 = left_palyer_layout.getRoundedImageViewLeft();
        }
        measureView(iv_vs_player1);
        int getMeasuredWidth = iv_vs_player1.getMeasuredWidth();
        int getMeasuredHeight = iv_vs_player1.getMeasuredHeight();
        int[] location = new int[2];
        iv_vs_player1.getLocationOnScreen(location);
        //判断是否在指定区域,比控件范围大20dp
        if (centerX > location[0] - 20 && centerX < getMeasuredWidth + location[0] + 20 &&
                centerY > location[1] - 20 && centerY < getMeasuredHeight + location[1] + 20 && !player1isHas) {
            Toast.makeText(getContext(), "控件已经拖动到左边区域", Toast.LENGTH_SHORT).show();
            choiceplayer1 = (PlayerSmallItem) view.getTag();
            choiceplayer1.setVisibility(View.GONE);
            PlayerInfo info = choiceplayer1.getPlayerInfo();
            //对左边布局进行数据更新
            left_palyer_layout.setPlayerInfo(info);
            player1isHas = true;//是否已经有选中玩家
        }
    }

    /**
     * 更新侧滑栏(使用接口回调与Connect进行通信)
     */
    @Override
    public void update(List<PlayerInfo> infoList) {
        /**===================后面都可以删除掉  start===========================*/
        if (choiceplayer1 != null) {
            boolean isPlayer1Delete = true;
            for (PlayerInfo info : infoList) {
                if (info.getBallNum() == choiceplayer1.getPlayerInfo().getBallNum()) {
                    isPlayer1Delete = false;
                    break;
                }
            }
            if (isPlayer1Delete) {
                resetPlayer(PLAYER_ONE);
            }
        }
        if (choiceplayer2 != null) {
            boolean isPlayer2Delete = true;
            for (PlayerInfo info : infoList) {
                if (info.getBallNum() == choiceplayer2.getPlayerInfo().getBallNum()) {
                    isPlayer2Delete = false;
                    break;
                }
            }
            if (isPlayer2Delete) {
                resetPlayer(PLAYER_TWO);
            }
        }
        LogUtils.e(TAG, "VSFragment--共有" + infoList.size() + "位");
        //只有一个参数且球号为0 则是没有玩家
        if (infoList.get(0).getBallNum() != 0) {
            if (list == null) {
                list = new ArrayList<>();
            }
            list.clear();
            list.addAll(infoList);
            LogUtils.e(TAG, "list.size=" + list.size());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    drawer_layout_players.removeAllViews();
                    for (int i = 0; i < drawItems.length; i++) {
                        if (i < list.size()) {


                            if (choiceplayer1 != null && choiceplayer1.getPlayerInfo().getBallNum() == list.get(i).getBallNum()) {
                                drawItems[i] = choiceplayer1;
                                drawer_layout_players.addView(choiceplayer1);
                                choiceplayer1.setVisibility(View.GONE);
                                continue;
                            }
                            if (choiceplayer2 != null && choiceplayer2.getPlayerInfo().getBallNum() == list.get(i).getBallNum()) {
                                drawItems[i] = choiceplayer2;
                                drawer_layout_players.addView(choiceplayer2);
                                choiceplayer2.setVisibility(View.GONE);
                                continue;
                            }

                            drawItems[i] = new PlayerSmallItem(getContext());
                            drawItems[i].setPlayerInfo(list.get(i));
                            drawItems[i].getImageView().setTag(drawItems[i]);
                            drawer_layout_players.addView(drawItems[i]);
                            drawItems[i].getImageView().setOnTouchListener(VSFragment.this);
                        } else {
                            drawItems[i] = new PlayerSmallItem(getContext());
                            drawer_layout_players.addView(drawItems[i]);
                        }

                    }
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    drawer_layout_players.removeAllViews();
                    for (int i = 0; i < drawItems.length; i++) {
                        LogUtils.e(TAG, "当前玩家共有0人");
                        drawItems[i] = new PlayerSmallItem(getContext());
                        drawer_layout_players.addView(drawItems[i]);
                    }
                }
            });
        }


    }

    @Override
    public void MatchCodeState(int state) {
        switch (state) {
            case 0:
                //将侧滑栏中的对码按钮状态改为 未选中 即停止对码
                cb_navgation_head_macth.setChecked(false);
                LogUtils.e(TAG, "侧滑栏中的按钮变为Nomacth");
                break;
            case 1:
                //将侧滑栏中的对码按钮状态改为 选中 即正在对码
                cb_navgation_head_macth.setChecked(true);
                LogUtils.e(TAG, "侧滑栏中的按钮变为macth");
                break;
        }
    }

    /**
     * 此方法可以多次被不同的View对象调用。
     * 在调用该方法后，
     * 使用View对象的getMessuredHeight()获高度（单位px）
     *
     * @param child 需要测量高度和宽度的View对象，
     */
    private void measureView(View child) {
        if (child == null) {
            return;
        }
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0,
                params.width);
        int lpHeight = params.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight,
                    View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }


    /**
     * ==========================k线图区域===========================
     */
    private int i;
    private Button play;
    private LineChart mChart;
    private BarChart bChart;
    private final Handler mHandler = new Handler();
    private float yValue;//k线图
    private float yValue1;

    private int dyValue;//柱状图
    private int dyValue1;
    //timer是否启动
    private boolean isStartTimer;
    private boolean isFirstData;

    /**
     * 线性布局初始化
     *
     * @param root
     */
    private void initChartViews(View root) {
/**********设置线状图**********/
        mChart = (LineChart) root.findViewById(R.id.chart1);            //线性图
        mChart.setDrawGridBackground(false);                            //设置格状背景
        mChart.getDescription().setEnabled(false);

        mChart.getLegend().setEnabled(false);
        // add an empty data object
        LineData data1 = new LineData();
        data1.setDrawValues(false);
        mChart.setData(data1);
        YAxis leftAxis1 = mChart.getAxisLeft();
        leftAxis1.removeAllLimitLines();          // reset all limit lines to avoid overlapping lines
        leftAxis1.setTextSize(8f);
        leftAxis1.setTextColor(Color.WHITE);
        leftAxis1.setDrawGridLines(true);
        leftAxis1.setAxisMinimum(0);
        leftAxis1.setAxisMaximum(5);

        XAxis xAxis1 = mChart.getXAxis();
        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis1.setTextSize(8f);
        xAxis1.setTextColor(Color.WHITE);
        xAxis1.setDrawGridLines(false);
        mChart.getXAxis().setDrawLabels(true);
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis();  //
        mChart.invalidate();


        /**********设置柱状图**********/
        //mChart = (LineChart) root.findViewById(R.id.chart1);
        bChart = (BarChart) root.findViewById(R.id.chart2);
        bChart.setDrawValueAboveBar(false);   //顶部数值
        bChart.setDrawGridBackground(false);  //设置格装背景
        setupChart(bChart);

        //    数据对象
        BarData data2 = new BarData();
        data2.setDrawValues(false);
        //bChart.setNoDataText();              //设置无数据时显示字符
        bChart.setData(data2);
        bChart.getXAxis().setDrawLabels(false);       //设置X轴标签
        bChart.getXAxis().setDrawGridLines(false);   // 设置X轴网格
        bChart.getLegend().setEnabled(false);

        YAxis leftAxis2 = bChart.getAxisLeft();
        leftAxis2.removeAllLimitLines();
        leftAxis2.setTextSize(8f);
        //leftAxis.setTextColor(color.DKGRAY);
        leftAxis2.setDrawGridLines(false);             //设置Y轴网络
        bChart.getAxisRight().setEnabled(false);
        bChart.getAxisLeft().setEnabled(false);


        bChart.invalidate();
    }

    /**
     * 折线图数据更新
     */
    private Runnable mTimer;

    @Override
    protected void initData() {
        mTimer = new Runnable() {
            public void run() {
                if (isStartTimer) {

                    addEntry();
                    addEntry1(bChart, 20, 100);
                    mHandler.postDelayed(this, 500);
                }
            }
        };
//        isFirstData = true;
//        isStartTimer = true;
//        mHandler.post(mTimer);

    }

    private int mLineChartXPoint;

    private void addEntry() {

        LineData data = mChart.getData();
        data.setDrawValues(false);

        ILineDataSet set = data.getDataSetByIndex(0);
        ILineDataSet set1 = data.getDataSetByIndex(1);
        ILineDataSet set2 = data.getDataSetByIndex(2);
        ILineDataSet set3 = data.getDataSetByIndex(3);

        //set.addEntry(...);     // can be called as well

        if (set == null) {
            set = createSet("DataSet 1", getResources().getColor(choiceplayer1.getPlayerInfo().getColorID()));
            data.addDataSet(set);
        }

        if (set1 == null) {
            set1 = createSet("DataSet 2", getResources().getColor(choiceplayer2.getPlayerInfo().getColorID()));
            data.addDataSet(set1);
        }
        if (set2 == null) {
            set2 = createSet("DataSet 3", Color.BLACK);
            data.addDataSet(set2);
        }

        if (set3 == null) {
            set3 = createSet("DataSet 4", Color.BLACK);
            data.addDataSet(set3);
        }


        /*****************随机数产生的假数据******************/
//        yValue = (int) ((float) (Math.random() * 6000) );
//        yValue1 = (int) ((float) (Math.random() * 6000) );


        /**-----------------------------------------数据入口-----------------------------------------*/
        if (isFirstData) {
            mLineChartXPoint = -18;
            for (int i = 0; i < 19; i++) {
                data.addEntry(new Entry(mLineChartXPoint, yValue / 1000), 0);
                data.addEntry(new Entry(mLineChartXPoint, yValue1 / 1000), 1);
                data.addEntry(new Entry(mLineChartXPoint, yValue / 1000), 2);
                data.addEntry(new Entry(mLineChartXPoint, yValue1 / 1000), 3);
                mLineChartXPoint++;
            }
            isFirstData = false;
        }

        //有一个数组越界的异常
        else if (data.getDataSetByIndex(0).getEntryForIndex(0).getX() < 0) {
            data.getDataSetByIndex(0).removeEntry(0);
            data.getDataSetByIndex(1).removeEntry(0);
            data.getDataSetByIndex(2).removeEntry(0);
            data.getDataSetByIndex(3).removeEntry(0);
        }

        data.addEntry(new Entry(mLineChartXPoint, yValue / 1000), 0);
        data.addEntry(new Entry(mLineChartXPoint, yValue1 / 1000), 1);
        mLineChartXPoint++;

        XAxis xAxis1 = mChart.getXAxis();
        xAxis1.removeAllLimitLines();
        LimitLine mPlayer1Line = new LimitLine(mLineChartXPoint - 1);
        mPlayer1Line.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        mPlayer1Line.setTextColor(getResources().getColor(choiceplayer1.getPlayerInfo().getColorID()));
        mPlayer1Line.setTextSize(15f);
        mPlayer1Line.setLabel((int) yValue + "");

        LimitLine mPlayer2Line = new LimitLine(mLineChartXPoint - 1);
        mPlayer2Line.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        mPlayer2Line.setYOffset(20);
        mPlayer2Line.setLineColor(Color.BLACK);
        mPlayer2Line.setTextColor(getResources().getColor(choiceplayer2.getPlayerInfo().getColorID()));
        mPlayer2Line.setTextSize(15f);
        mPlayer2Line.setLabel((int) yValue1 + "");

        xAxis1.addLimitLine(mPlayer1Line);
        xAxis1.addLimitLine(mPlayer2Line);

        data.notifyDataChanged();

        LogUtils.e(TAG, "转速接入数据接口");
        // let the chart know it's data has changed
        mChart.notifyDataSetChanged();
        mChart.setVisibleXRangeMaximum(100);
        //mChart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
        // this automatically refreshes the chart (calls invalidate())
        mChart.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
    }

    private void removeEntry() {
        LineData data = mChart.getData();
        data.clearValues();
        mChart.invalidate();
        XAxis xAxis1 = mChart.getXAxis();
        xAxis1.removeAllLimitLines();
        BarData data1 = bChart.getData();
        data1.clearValues();
        bChart.invalidate();

        mData1.clear();
        mData2.clear();
    }

    private LineDataSet createSet(String name, int color) {
        LineDataSet set = new LineDataSet(null, name);
        set.setLineWidth(1.5f);                // 设置线条粗细
        set.setCircleRadius(0.1f);             // 设置点的粗细
        set.setColor(color);                   // 设置线条颜色
        set.setCircleColor(color);
        set.setDrawCircles(false);
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(15f);
        set.setDrawCircleHole(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);  // 设置画线样式为贝塞尔曲线
        if (color == Color.BLACK) {
            set.setLineWidth(2.5f);
        }
        return set;
    }

    /**
     * ============================柱状图=================================
     */
    private void setupChart(BarLineChartBase chart) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);

        // enable scaling and dragging
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setTextSize(8f);
        leftAxis.setTextColor(Color.DKGRAY);
        //   leftAxis.setValueFormatter(new DefaultAxisValueFormatter(100));
        leftAxis.setDrawGridLines(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(8f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setDrawGridLines(false);

//        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setVisibleXRangeMaximum(20);
        chart.getAxisLeft().setAxisMinimum(0);
    }

    ArrayList<Integer> mData1 = new ArrayList<>();
    ArrayList<Integer> mData2 = new ArrayList<>();

    private void addEntry1(BarChart chart, int count, float range) {
        float groupSpace = 0.08f;
        float barSpace = 0.03f; // x4 DataSet
        float barWidth = 0.1f; // x4 DataSet
        // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"

        int groupCount = count;
        int startYear = 0;
        int endYear = startYear + groupCount;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        if (mData1.size() == 0) {

            for (int i = 0; i < 19; i++) {
                mData1.add(0);
                mData2.add(0);
            }
        }
        mData1.add(dyValue);
        mData2.add(dyValue1);
        for (int i = startYear; i < endYear; i++) {
            yVals1.add(new BarEntry(i, mData1.get(mData1.size() - 20 + i)));
            yVals2.add(new BarEntry(i, mData2.get(mData2.size() - 20 + i)));
        }

        chart.getAxisLeft().setAxisMinimum(0);
        BarDataSet set1, set2;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {

            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) chart.getData().getDataSetByIndex(1);

        } else {
            // create 4 DataSets
            set1 = new BarDataSet(yVals1, "Player A");
            set1.setColor(getResources().getColor(choiceplayer1.getPlayerInfo().getColorID()));

            set2 = new BarDataSet(yVals2, "Player B");
            set2.setColor(getResources().getColor(choiceplayer2.getPlayerInfo().getColorID()));

            BarData data = new BarData(set1, set2);
            data.setDrawValues(false);
            data.setValueFormatter(new LargeValueFormatter());
            chart.setData(data);
        }
        set1.setValues(yVals1);
        set2.setValues(yVals2);
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
        // specify the width each bar should have
        chart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        chart.getXAxis().setAxisMinimum(startYear);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        chart.getXAxis().setAxisMaximum(startYear + chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        chart.groupBars(startYear, groupSpace, barSpace);
        chart.invalidate();
    }

    /**
     * fragment销毁
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        DrawerLayout drawer = (DrawerLayout) root.findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (list != null) {
            list.clear();
            list = null;
        }
    }


}
