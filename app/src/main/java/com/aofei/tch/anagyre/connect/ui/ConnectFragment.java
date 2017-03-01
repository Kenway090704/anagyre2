package com.aofei.tch.anagyre.connect.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aofei.tch.anagyre.MyApplication;
import com.aofei.tch.anagyre.R;
import com.aofei.tch.anagyre.bluetooth.cmd.CMDContacts;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnMatchCodeTaskListener;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnStopMatchCodeTaskListener;
import com.aofei.tch.anagyre.bluetooth.taskqueue.task.StopGameTask;
import com.aofei.tch.anagyre.connect.adapter.ClipViewPager;
import com.aofei.tch.anagyre.connect.adapter.TubatuAdapter;
import com.aofei.tch.anagyre.connect.anim.ScalePageTransformer;
import com.aofei.tch.anagyre.bluetooth.model.MyBluetoothService;
import com.aofei.tch.anagyre.connect.listener.OnDisconnectlistener;
import com.aofei.tch.anagyre.main.ui.HomeActivity;
import com.aofei.tch.anagyre.other.ui.BaseFragment;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;
import com.aofei.tch.anagyre.vs.bean.PlayerInfo;
import com.aofei.tch.anagyre.vs.listener.OnConnectToVsListener;
import com.aofei.tch.anagyre.vs.ui.VSFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kenway on 16/11/18 10:28
 * Email : xiaokai090704@126.com
 */

public class ConnectFragment extends BaseFragment implements OnDisconnectlistener{
    public static boolean isDelete;
    public static int isDeletePosition;
    public static boolean isStartorLeft;
    public static int totalBall;
    public static boolean isDeleteAnimation;

    private final static String TAG = "ConnectFragment";
    private Context activity = getActivity();//获取上下文对象
    private final static float TARGET_HEAP_UTILIZATION = 0.75f;

    private TubatuAdapter mPagerAdapter;
    private ClipViewPager mViewPager;

    private AnimationDrawable anim;
    private RunAnim runAnim;
    private boolean isAnim = false;
    private int preItem;
    public static List<PlayerInfo> list;
    //*****底部控件********//
    private CheckBox cb_conncet, cb_delete;
    private TextView tv_con_num;
    private boolean isFirstCode = false;


    public CheckBox getCb_delete() {
        return cb_delete;
    }

    public CheckBox getCb_conncet() {
        return cb_conncet;
    }

    public ClipViewPager getmViewPager() {
        return mViewPager;
    }

    public TextView getTv_con_num() {
        return tv_con_num;
    }


    /**
     * 广播接收器
     */
    //*****服务的实例******//
    private MyBluetoothService service;
    //对码任务的监听
    private OnMatchCodeTaskListener onMatchCodeTaskListener =
            new OnMatchCodeTaskListener() {
                @Override
                public void onMacthing() {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toVsListener.MatchCodeState(1);//将对码状态与VsFragment同步

                            Toast.makeText(getActivity(), "开始对码", Toast.LENGTH_LONG).show();
                        }
                    });

                }

                @Override
                public void onMacthedCodeFail(String msg) {

                }

                @Override
                public void onMacthedCodeSuccessful(final int[] ballInfo) {

                    for (PlayerInfo info : list) {
                        if (info.getBallNum() == ballInfo[0]) {
                            LogUtils.e(TAG, "重复添加球号");
                            return;
                        }
                    }


                    //获取对码信息
                    //listView.add();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < ballInfo.length; i++) {
                        stringBuffer.append(ballInfo[i] + " ");
                    }
                    LogUtils.e(TAG, "对码成功" + stringBuffer);
                    final PlayerInfo info = new PlayerInfo();
                    info.setBallNum(ballInfo[0]);
                    info.setId("ID:" + ballInfo[1] + ":" + ballInfo[2] + ":" + ballInfo[3] + ":" + ballInfo[4]);
                    info.setName("player" + ballInfo[0]);
                    info.setExValue("经验值:" + ballInfo[5] + ":" + ballInfo[6] + ":" + ballInfo[7] + ":" + ballInfo[8] + " V:" + ballInfo[9]);
                    getBallColor(ballInfo[0], info);

                    if (list.size() < 8) {

                        if (list.get(0).getBallNum() == 0) {

//                            list.remove(0);
//                            list.add(info);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    tv_con_num.setText("1");
                                    list.remove(0);
                                    mPagerAdapter.notifyDataSetChanged();
                                    list.add(info);
                                    mPagerAdapter.notifyDataSetChanged();
                                    toVsListener.update(list);//将数据回调到VSFragment

                                }
                            });

//                            isFirstCode = true;
                        } else {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    info.setBallNum(ballInfo[0]);
                                    list.add(info);
                                    tv_con_num.setText(list.size() + "");
                                    mViewPager.removeAllViews();
                                    mPagerAdapter.notifyDataSetChanged();

                                    toVsListener.update(list);//将数据回调到VSFragment

                                    mViewPager.setOffscreenPageLimit(list.size());
                                    mViewPager.setCurrentItem(list.size() - 1);
                                }
                            });
                        }


                    }

                }

                @Override
                public void onMacthedCodeNoResponse() {

                }

                @Override
                public void onMatchCodeFull() {
                    LogUtils.e(TAG, "适配器已满");

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            cb_conncet.setChecked(false);
                        }
                    });


                }

                @Override
                public void onMacthCodeFinished() {
                    getActivity().runOnUiThread(new Runnable() {


                        @Override
                        public void run() {
                            toVsListener.MatchCodeState(0);//将停止对码与VsFragment同步

                            Toast.makeText(getActivity(), "停止对码", Toast.LENGTH_SHORT).show();
                            cb_conncet.setChecked(false);

                        }
                    });
                }
            };

    //取消对码任务监听
    private OnStopMatchCodeTaskListener onStopMatchCodeTaskListener =
            new OnStopMatchCodeTaskListener() {
                @Override
                public void onStopMacthing() {

                }

                @Override
                public void onStopMacthedCodeFail() {

                }

                @Override
                public void onStopMacthedCodeSuccessful() {
                }

                @Override
                public void onStopMacthedCodeNoResponse() {

                }
            };
    //******与VsFragment通信接口**********//
    private OnConnectToVsListener toVsListener;

    public void setOnConnectToVsListener(OnConnectToVsListener toVsListener) {
        this.toVsListener = toVsListener;
        LogUtils.e(TAG, "与VsFragment通信接口已建立");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_connect;
    }

    /**
     * 将接口在没有使用时就传递给service
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        service = ((HomeActivity) getActivity()).myService;//获取Service
       if (null!=service){
           service.setOnConnectMatchListener(onMatchCodeTaskListener);
           LogUtils.e(TAG,"接口已经给到了service");
       }
    }

    @Override
    protected void initViews(View root) {
        list = new ArrayList<>();

        /**获取底部控件*/
        cb_conncet = (CheckBox) root.findViewById(R.id.cb_connect);
        cb_delete = (CheckBox) root.findViewById(R.id.cb_delete);
        tv_con_num = (TextView) root.findViewById(R.id.tv_con_num);


        /**获取viewPager*/
        mViewPager = (ClipViewPager) root.findViewById(R.id.viewpager);
        /**调节ViewPager的滑动速度**/
        mViewPager.setSpeedScroller(200);

        /**给ViewPager设置缩放动画，这里通过PageTransformer来实现**/
        mViewPager.setPageTransformer(true, (ViewPager.PageTransformer) new ScalePageTransformer());

        /**
         * 需要将整个页面的事件分发给ViewPager，不然的话只有ViewPager中间的view能滑动，其他的都不能滑动，
         * 这是肯定的，因为ViewPager总体布局就是中间那一块大小，其他的子布局都跑到ViewPager外面来了
         */
        root.findViewById(R.id.page_container).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mViewPager.dispatchTouchEvent(event);
            }
        });

        mPagerAdapter = new TubatuAdapter(getActivity(), list);

        mViewPager.setAdapter(mPagerAdapter);



    }

    /**
     * 如果连接成功收
     */
    @Override
    protected void initEnvent() {


        cb_delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (list.get(0).getBallNum() == 0) {
                    return;
                }
//                if (VSFragment.isPlayGaming == true) {
//
//                    Intent intent = new Intent(getActivity(), MyBluetoothService.class);
//                    intent.putExtra(HomeActivity.TAG, CMDContacts.RESQUEST_STOP_VS_COMPETITION);
//                    getActivity().startService(intent);
//                } else {
                     LogUtils.e(TAG,"MyApplication.isMatching=="+MyApplication.isMatching);

                    if (b) {


                        if (MyApplication.isMatching) {
                            //发送停止对码的的任务
                            Intent intent = new Intent(getActivity(), MyBluetoothService.class);
                            intent.putExtra(HomeActivity.TAG, CMDContacts.RESQUEST_STOP_MATCH_ANAGYRE);
                            service = ((HomeActivity) getActivity()).myService;//获取Service
                            service.setOnStopMatchCodeTaskListener(onStopMatchCodeTaskListener);
                            getActivity().startService(intent);
                        }

                        for (int i = 0; i < list.size(); i++) {

                            View view = mViewPager.getChildAt(i);
                            ImageView deleteImage = (ImageView) view.findViewById(R.id.deleteIcon);
                            deleteImage.setVisibility(view.VISIBLE);
                        }
                    } else {

                        for (int i = 0; i < list.size(); i++) {
                            View view = mViewPager.getChildAt(i);
                            ImageView deleteImage = (ImageView) view.findViewById(R.id.deleteIcon);
                            deleteImage.setVisibility(view.INVISIBLE);
                        }

                    }


//                }


            }
        });


        cb_conncet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (MyApplication.CONNECTED == true) {
                    if (b) {
                        if (cb_delete.isChecked() == true) {
                            for (int i = 0; i < list.size(); i++) {
                                View view = mViewPager.getChildAt(i);
                                ImageView deleteImage = (ImageView) view.findViewById(R.id.deleteIcon);
                                deleteImage.setVisibility(view.INVISIBLE);
                            }

                            cb_delete.setChecked(false);
                        }


                        if (list.size() < 8) {
                            //开始对码

                            Intent intent = new Intent(getActivity(), MyBluetoothService.class);
                            intent.putExtra(HomeActivity.TAG, CMDContacts.RESQUEST_MATCH_ANAGYRE);
                            service = ((HomeActivity) getActivity()).myService;//获取Service
                            service.setOnConnectMatchListener(onMatchCodeTaskListener);//设置监听
                            getActivity().startService(intent);
                        } else {
                            Toast.makeText(getActivity(), "适配器已满", Toast.LENGTH_LONG).show();
                            cb_conncet.setChecked(false);
                        }

                    } else {

                        if (MyApplication.isMatching) {
                            //停止对码
                            Intent intent = new Intent(getActivity(), MyBluetoothService.class);
                            intent.putExtra(HomeActivity.TAG, CMDContacts.RESQUEST_STOP_MATCH_ANAGYRE);
                            service = ((HomeActivity) getActivity()).myService;//获取Service
                            service.setOnStopMatchCodeTaskListener(onStopMatchCodeTaskListener);
                            getActivity().startService(intent);
                        }
                    }
                } else {
                    cb_conncet.setChecked(false);
                    Toast.makeText(getActivity(), "还没有和适配器对接", Toast.LENGTH_LONG).show();
                }
            }
        });


        //点击红色的X执行的代码
        //停止对码


    }


    private boolean isFirst = true;

    @Override
    protected void initData() {
//       if (service!=null){
//           service = ((HomeActivity) getActivity()).myService;//获取Service
//           service.setOnStopMatchCodeTaskListener(onStopMatchCodeTaskListener);
//           LogUtils.e(TAG,"接口已经给到了service");
//       }

        mPagerAdapter.setOnConnectToVsListener(toVsListener);//将接口传递给adapter
        if (isFirst) {

            PlayerInfo info = new PlayerInfo();
            info.setBallNum(0);
            getBallColor(0, info);

            list.add(info);

            isFirst = false;
        } else {

        }


        /**这里需要将setOffscreenPageLimit的值设置成数据源的总个数，如果不加这句话，会导致左右切换异常；**/
        preItem = list.size() / 2;
        mPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(list.size() / 2);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int index = mViewPager.getCurrentItem();

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                index = position;

                if (position == 0 && ConnectFragment.isDeleteAnimation == true) {
                    View view = mViewPager.findViewWithTag(index);

                    ImageView imageV = (ImageView) view.findViewById(R.id.player_photo_img);
                    ImageView imageBg = (ImageView) view.findViewById(R.id.player_background);
                    imageV.setBackgroundResource(0);
                    imageBg.setImageResource(R.mipmap.tuoluo_open_bg);


                    imageV.setBackgroundResource(R.drawable.anim);
                    anim = (AnimationDrawable) imageV.getBackground();
                    runAnim = new RunAnim();
                    imageV.setImageDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
                    runAnim.execute("");
                    ConnectFragment.isDeleteAnimation = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("测试代码", "onPageSelected选中了" + position);


                index = position;
                if (ConnectFragment.isDeleteAnimation == true) {

                    View view = mViewPager.findViewWithTag(index);

                    ImageView imageV = (ImageView) view.findViewById(R.id.player_photo_img);
                    ImageView imageBg = (ImageView) view.findViewById(R.id.player_background);
                    imageV.setBackgroundResource(0);
                    imageBg.setImageResource(R.mipmap.tuoluo_open_bg);


                    imageV.setBackgroundResource(R.drawable.anim);
                    anim = (AnimationDrawable) imageV.getBackground();
                    runAnim = new RunAnim();
                    imageV.setImageDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
                    runAnim.execute("");
                    ConnectFragment.isDeleteAnimation = false;
                }

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPageScrollStateChanged(int state) {

                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    //正在滑动   pager处于正在拖拽中
                } else {
                    if (state == ViewPager.SCROLL_STATE_SETTLING) {
                        //pager正在自动沉降，相当于松手后，pager恢复到一个完整pager的过程
                    } else {
                        if (state == ViewPager.SCROLL_STATE_IDLE) {
                            if (isAnim == true && index != preItem) {
                                View view = mViewPager.findViewWithTag(preItem);
                                if (view != null) {
                                    final ImageView imageV = (ImageView) view.findViewById(R.id.player_photo_img);
                                    final ImageView imageBg = (ImageView) view.findViewById(R.id.player_background);
                                    imageV.setBackground(new ColorDrawable(Color.argb(0, 0, 0, 0)));
                                    imageV.setBackgroundResource(R.drawable.anim_close);
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AnimationDrawable anima = (AnimationDrawable) imageV.getBackground();
                                    anima.start();

                                        }
                                    });
                                    thread.start();
                                    imageV.setImageResource(R.mipmap.tuoluo);
                                    imageBg.setImageResource(R.mipmap.player_bg);
                                }
                            }

                            if (list.get(0).getBallNum() != 0) {
                                if (isAnim == false) isAnim = true;

                                View view = mViewPager.findViewWithTag(index);

                                ImageView imageV = (ImageView) view.findViewById(R.id.player_photo_img);
                                ImageView imageBg = (ImageView) view.findViewById(R.id.player_background);
                                imageBg.setImageResource(R.mipmap.tuoluo_open_bg);

                                imageV.setBackgroundResource(R.drawable.anim);
                                anim = (AnimationDrawable) imageV.getBackground();
                                runAnim = new RunAnim();
                                imageV.setImageDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
                                runAnim.execute("");
                                preItem = index;
                            }
                        }
                    }
                }

            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();

        cb_conncet.setChecked(false);
        cb_delete.setChecked(false);
    }



    /**
     * 当activity销毁时，释放资源
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onRemoveAllViews() {
           //要执行界面清空操作

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                list.clear();
                mPagerAdapter.notifyDataSetChanged();
                PlayerInfo info = new PlayerInfo();
                info.setBallNum(0);
                getBallColor(0, info);

                list.add(info);
                mPagerAdapter.notifyDataSetChanged();
                tv_con_num.setText("0");

                toVsListener.update(list);//清空VsFragment的玩家信息
            }
        });
    }


    class RunAnim extends AsyncTask<String, String, String> {
        protected String doInBackground(String... params) {
            if (!anim.isRunning()) {
                anim.stop();
                anim.start();
            }
            return "";
        }
    }


    public static void getBallColor(int ballID, PlayerInfo info) {
        switch (ballID) {
            case 0:

                info.setColor(R.color.white);
                info.setPhotoSamllID(R.mipmap.nav_head_noconnect);
                info.setId("ID:");
                info.setExValue("经验值:");
                info.setName("未连接用户");
                break;
            case 1:
                info.setColor(R.color.red);
                info.setPhotoSamllID(R.mipmap.player1);
                break;
            case 2:
                info.setColor(R.color.green);
                info.setPhotoSamllID(R.mipmap.player2);

                break;
            case 3:
                info.setColor(R.color.blue);
                info.setPhotoSamllID(R.mipmap.player3);

                break;
            case 4:
                info.setColor(R.color.fuchsia);
                info.setPhotoSamllID(R.mipmap.player4);

                break;
            case 5:
                info.setColor(R.color.yellow);
                info.setPhotoSamllID(R.mipmap.player5);

                break;
            case 6:
                info.setColor(R.color.lightcyan);
                info.setPhotoSamllID(R.mipmap.player6);

                break;
            case 7:
                info.setColor(R.color.orange);
                info.setPhotoSamllID(R.mipmap.player7);
                break;
            case 8:
                info.setColor(R.color.pink);
                info.setPhotoSamllID(R.mipmap.player8);
                break;
        }
    }

}

