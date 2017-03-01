package com.aofei.tch.anagyre.main.ui;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.aofei.tch.anagyre.MyApplication;
import com.aofei.tch.anagyre.R;
import com.aofei.tch.anagyre.analysis.ui.AnalysisFragment;
import com.aofei.tch.anagyre.bluetooth.cmd.CMDContacts;
import com.aofei.tch.anagyre.bluetooth.model.MyBluetoothService;
import com.aofei.tch.anagyre.connect.ui.ConnectFragment;
import com.aofei.tch.anagyre.login.ui.LoginFragment;
import com.aofei.tch.anagyre.multiplayer.ui.MultiplayerFragment;
import com.aofei.tch.anagyre.other.ui.BaseFragment;
import com.aofei.tch.anagyre.other.utils.Debug;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;
import com.aofei.tch.anagyre.other.utils.ScreenUtils;
import com.aofei.tch.anagyre.other.widget.CustomLoadingDialog;
import com.aofei.tch.anagyre.vs.ui.VSFragment;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * 主页面
 */
public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";
    //==============底部按钮===============
    private RadioButton rd_connect, rd_analysis, rd_vs, rd_mulPlayer, rd_login;
    private CheckBox rd_DeviceconState;
    private RadioGroup radioGroup;
    //=============蓝牙适配器==============
    BluetoothAdapter mBluetoothAdapter;
    //==============数据源================
    private BaseFragment[] fragments;
    public ConnectFragment connectFragment;
    private AnalysisFragment analysisFragment;
    private VSFragment vsFragment;
    private MultiplayerFragment multiplayerFragment;
    private LoginFragment loginFragment;
    //===========上次点击的fragment索引===============
    private int lastIndex = 0;

    private BluetoothDevice mBluetoothDevice;


    //============获取service================//
    public static MyBluetoothService myService;//获取服务对象
    //============扫描时加载对话框=============//
    private CustomLoadingDialog loadingDialog;
    /**
     * 与Service进行绑定
     */
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            /**获取到MyService的对象*/
            myService = ((MyBluetoothService.BlueBinder) iBinder).getService();
            //获取到服务实例
            LogUtils.e(TAG, Debug.line(new Exception()) + "获取服务实例");

            //
            if (fragments[0] != null) {
                fragments[0].onHiddenChanged(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    //============存放扫描到蓝牙对象=================
    List<BluetoothDevice> deviceList;//为了防止扫描到重复的蓝牙


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置横屏
        //hide all
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        /**
         *绑定Service
         * */
        Intent intent = new Intent(this, MyBluetoothService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);


        deviceList = new ArrayList<>();
        //判断是否支持蓝牙
        if (!isSupportBluetooth()) {
            Toast.makeText(HomeActivity.this, "你的设备不支持蓝牙,无法使用该应用!", Toast.LENGTH_LONG).show();
        }
        //初始化控件
        initViews();
        /**初始化fragments*/
        //使用tran.add,hide,show来实现fragment的切换
        initHideOrShowFragment();
        //初始化监听事件
        initEvent();
        //注册监听蓝牙状态的广播
        registerBoradcastReceiver();

        //注册EventBus  onDestroy中反注册EventBus
        EventBus.getDefault().register(this);
        LogUtils.e(TAG, "xiewei");

    }

    /**
     * 初始化控件(HomeActivity底部RadioButton)
     */
    private void initViews() {
        //==================内容部分======================//
        rd_connect = (RadioButton) findViewById(R.id.rd_home_connect);
        rd_analysis = (RadioButton) findViewById(R.id.rd_home_analysis);
        rd_vs = (RadioButton) findViewById(R.id.rd_home_vs);
        rd_mulPlayer = (RadioButton) findViewById(R.id.rd_home_mulPlayer);
        rd_DeviceconState = (CheckBox) findViewById(R.id.rd_home_DeviceConState);
        rd_login = (RadioButton) findViewById(R.id.rd_home_login);
        radioGroup = (RadioGroup) findViewById(R.id.rg_home);
        rd_connect.setChecked(true);


        int height = ScreenUtils.getScreenHeightPx(this);
        Rect r = new Rect(0, 0, height / 14, height / 14);
        Drawable[] dr = rd_DeviceconState.getCompoundDrawables();
        dr[1].setBounds(r);
        rd_DeviceconState.setCompoundDrawables(null, dr[1], null, null);
        RadioButton[] rbs = new RadioButton[5];
        rbs[0] = (RadioButton) findViewById(R.id.rd_home_connect);
        rbs[1] = (RadioButton) findViewById(R.id.rd_home_analysis);
        rbs[2] = (RadioButton) findViewById(R.id.rd_home_vs);
        rbs[3] = (RadioButton) findViewById(R.id.rd_home_mulPlayer);
        rbs[4] = (RadioButton) findViewById(R.id.rd_home_login);

        Drawable[] drs;
        for (RadioButton rb : rbs) {
            //挨着给每个RadioButton加入drawable限制边距以控制显示大小
            drs = rb.getCompoundDrawables();
            //获取drawables
            //定义一个Rect边界
            drs[1].setBounds(r);
            rb.setCompoundDrawables(null, drs[1], null, null);
            //添加限制给控件
        }


    }

    /**
     * 控件的监听事件
     */
    private void initEvent() {

    }

    /**
     * 使用隐藏与显示实现fragment的切换
     */
    public void initHideOrShowFragment() {
        connectFragment = new ConnectFragment();
        analysisFragment = new AnalysisFragment();
        vsFragment = new VSFragment();
        multiplayerFragment = new MultiplayerFragment();
        loginFragment = new LoginFragment();
        //fragment数据
        fragments = new BaseFragment[]{
                connectFragment, analysisFragment, vsFragment, multiplayerFragment, loginFragment};
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            //这个空间就是我们放置fragment的地方
            tran.add(R.id.home_frameLayout, fragments[i]);
            // 默认隐藏
            tran.hide(fragments[i]);
        }
        // 默认显示第一个
        tran.show(fragments[0]);
        // 提交事务
        tran.commit();
        // 监听底部RadioButton
        initHideOrShowRadioButton();

        isFirst = true;

    }

    /**
     * 监听底部RadioButton(使用隐藏与显示实现fragment的切换)
     */
    private void initHideOrShowRadioButton() {

        // 取本地记录，看用户是否登录
        final SharedPreferences share = getSharedPreferences("login", MODE_PRIVATE);

        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int index = -1;
                    switch (checkedId) {
                        default:
                        case R.id.rd_home_connect:
                            index = 0;
                            break;
                        case R.id.rd_home_analysis:
                            index = 1;
                            break;
                        case R.id.rd_home_vs:
                            index = 2;
                            break;
                        case R.id.rd_home_mulPlayer:
                            index = 3;
                            break;

                        case R.id.rd_home_login:
                            final boolean hasLogin1 = share.getBoolean("hasLogin", false);
                            // 若没登录，则跳转至登录页面
                            if (!hasLogin1) {
//                                Intent intent = new Intent(HomeActivity.this, L_DengLuActivity.class);
//                                startActivity(intent);
//                                return;
                            }
                            index = 4;
                            break;
                        case R.id.rd_home_DeviceConState:
                            //什么都不执行
                            return;
                    }
                    //
                    if (MyApplication.CONNECTED) {
                        rd_DeviceconState.setChecked(true);
                    } else {
                        rd_DeviceconState.setChecked(false);
                    }

                    // 判断若点击的是上次的页面则不作操作
                    if (index == lastIndex) {
                        return;
                    }

                    FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
                    // 隐藏上一个fragment
                    if (lastIndex != -1) {

                        if (lastIndex == 0) {

                            CheckBox cb_conncet = connectFragment.getCb_conncet();
                            CheckBox cb_delete = connectFragment.getCb_delete();
                            cb_conncet.setChecked(false);
                            cb_delete.setChecked(false);
                        }

                        tran.hide(fragments[lastIndex]);

                    }

                    // 显示点击的fragment
                    tran.show(fragments[index]);
                    tran.commit();
                    // 记录上一个索引
                    lastIndex = index;
                }
            });
        }
    }

    /**
     * 使用EventBus将扫描到的BluetoothDevice回传到HomeActivity
     * 没有Name的不显示,不会重复显示
     *
     * @param device
     */

    public void onEventMainThread(BluetoothDevice device) {
        boolean isSame = false;
        String msg = "onEventMainThread收到了消息：" + device.getName() + ", address==" + device.getAddress();
        LogUtils.e(TAG, msg);
        //遍历集合,判断是否已经有形同的了
        for (BluetoothDevice dev : deviceList) {
            //如果在集合中有相同的
            if (dev.getAddress().equals(device.getAddress())) {
                isSame = true;
            }
        }
        if (!isSame && ("REALONG".equalsIgnoreCase(device.getName()) || "bl3231-spp".equalsIgnoreCase(device.getName()))) {
            deviceList.add(device);
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();//当找到REALONG
            }

        }
    }

    boolean isFirst = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
            tran.show(fragments[3]);
            tran.hide(fragments[3]);
            tran.show(fragments[0]);
            isFirst = false;
        }

        loadingDialog = new CustomLoadingDialog(this);
        loadingDialog.setCancelable(false);
        //在用户交互时判读蓝牙是否打开,如果没有打开,要求打开
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);
        }

        /**发送扫描适配器的命令,先判断是否连接,
         * 1.如果已连接就不要在发送了,
         * 2.如果没有连接,发送扫描请求
         * 3.使用全局变量来判读档期手机与适配器是否连接
         * MyApplication.CONNECTED; true---已经连接   false---未连接
         * */
        if (mBluetoothAdapter != null && !MyApplication.CONNECTED) {
            //发送命令让手机与适配器进行连接,
            deviceList.clear();//在重新扫描时清空集合
            Intent intent = new Intent(this, MyBluetoothService.class);
            intent.putExtra(TAG, CMDContacts.RESQUEST_DISCOVER_BlUETOOTH);
            startService(intent);
            loadingDialog.show();

        }
        //每次进入后如果没有扫描
        if (dialogNone != null) {
            dialogNone.dismiss();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialogNone != null) {
            dialogNone.dismiss();
            LogUtils.e(TAG, "onPause=== dialogNone.dismiss();");
        }
        if (dialogDevice != null) {
            dialogDevice.dismiss();
            LogUtils.e(TAG, "onPause===dialogDevice.dismiss();");
        }
        //将退出对话框消失
        if (exitDialog != null) {
            exitDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.e(TAG, "onStop执行啦");

    }


    /**
     * 判断手机或平板是否支持蓝牙
     */
    private boolean isSupportBluetooth() {
        //判读设备是否支持蓝牙
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        return true;

    }

    /**
     * =======================监听蓝牙与适配器连接状态的变化========================
     */
    private BroadcastReceiver stateChangeReceiver = new StateChangeReceiver();
    private IntentFilter stateChangeFilter;
    private IntentFilter connectedFilter;
    private IntentFilter disConnectedFilter;
    private IntentFilter stopscan;//停止扫描
    private IntentFilter bonding;

    /**
     * 注册状态变化的广播
     */
    //没有扫描到适配器的对话框
    private AlertDialog dialogNone, dialogDevice;

    private void registerBoradcastReceiver() {
        stateChangeFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        connectedFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        disConnectedFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        stopscan = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        bonding = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(stateChangeReceiver, stateChangeFilter);
        registerReceiver(stateChangeReceiver, connectedFilter);
        registerReceiver(stateChangeReceiver, disConnectedFilter);
        registerReceiver(stateChangeReceiver, stopscan);
        registerReceiver(stateChangeReceiver, bonding);
        /**要记得在退出程序的时候,要反注册这个广播接收器*/
    }

    int scanCount = 0;

    class StateChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            /**蓝牙连接*/
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

                //更新UI---底部RadioButton 连接状态的改变
                rd_DeviceconState.setChecked(true);
                rd_DeviceconState.setText("设备已连");
                Toast.makeText(HomeActivity.this, "连接成功", Toast.LENGTH_LONG).show();
            }
            /**蓝牙连接断开*/
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                MyApplication.CONNECTED = false;//蓝牙连接断开
                rd_DeviceconState.setChecked(false);
                rd_DeviceconState.setText("设备断开");
                Toast.makeText(HomeActivity.this, "连接断开...", Toast.LENGTH_LONG).show();

                connectFragment.onRemoveAllViews();//清空ConnectFragment的界面
                LogUtils.e(TAG, "BluetoothDevice.ACTION_ACL_DISCONNECTED,开始重新连接");
            }

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (mBluetoothDevice.getBondState()) {
                    case BluetoothDevice.BOND_BONDING://正在配对
                        LogUtils.e(TAG, "正在配对...");


                        break;
                    case BluetoothDevice.BOND_BONDED://配对结束
                        LogUtils.e(TAG, "完成配对...");

                        //启动服务去处理任务
                        Intent intent2 = new Intent(HomeActivity.this, MyBluetoothService.class);
                        intent2.putExtra(TAG, CMDContacts.RESQUEST_CONNECT_ADAPTER);
                        intent2.putExtra("device", mBluetoothDevice);//将要连接的设备传递过去
                        startService(intent2);
                        Toast.makeText(HomeActivity.this, "正在连接适配器...", Toast.LENGTH_SHORT).show();//可以使用进度的一个Dialog来实现

                        break;
                    case BluetoothDevice.BOND_NONE://取消配对/未配对

                        LogUtils.e(TAG, "取消配对...");

                        //开始重新扫描蓝牙
                        if (deviceList.size() != 0) {
                            LogUtils.e(TAG, "重新开始配对");
                            createDeviceDialog();//弹出设备对话框
                        }


                    default:
                        break;
                }
            }


            //蓝牙扫描结束
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) && !MyApplication.CONNECTED) {
                /**如果没有扫描到设备显示*/
                loadingDialog.dismiss();//扫描对话框消失
                if (deviceList.size() == 0) {
                    LogUtils.e(TAG, "没有扫描到RELONG适配器");
                    final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setMessage("未找到RELONG适配器,重新扫描");
                    builder.setTitle("提示");
                    builder.setCancelable(false);//使用者必须要点击确认才行
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //开始重新扫描蓝牙
                            Intent intent = new Intent(HomeActivity.this, MyBluetoothService.class);
                            intent.putExtra(TAG, CMDContacts.RESQUEST_DISCOVER_BlUETOOTH);
                            startService(intent);
                            dialog.dismiss();
                            loadingDialog.show2();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    });
                    dialogNone = builder.create();
                    //将退出对话框隐藏
                    if (exitDialog != null) {
                        exitDialog.dismiss();
                    }
                    dialogNone.show();


                } else {
                    createDeviceDialog();//弹出设备对话框
                }

            }

        }

    }

    /**
     * 弹出设备对话框
     */
    private void createDeviceDialog() {
        String[] strs = new String[deviceList.size()];
        for (int i = 0; i < deviceList.size(); i++) {
            strs[i] = deviceList.get(i).getName() + " (" + deviceList.get(i).getAddress() + ")";
            LogUtils.e(TAG + Debug.line(new Exception()), "DeviceName==" + deviceList.get(i).getName() + " ,Address:" + deviceList.get(i).getAddress());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("请选择要连接RELONG适配器");
        builder.setCancelable(false);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setSingleChoiceItems(
                strs, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mBluetoothDevice = deviceList.get(which);
                        try {
                            // 连接之前先判断是否已经配对  true--执行连接  false--先执行配对
                            if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                                Method creMethod = BluetoothDevice.class.getMethod("createBond");
                                LogUtils.e("TAG", "开始配对");
                                creMethod.invoke(mBluetoothDevice);
                                dialog.dismiss();

                            } else {
                                //do somethings

                                //启动服务去处理任务
                                Intent intent = new Intent(HomeActivity.this, MyBluetoothService.class);
                                intent.putExtra(TAG, CMDContacts.RESQUEST_CONNECT_ADAPTER);
                                intent.putExtra("device", deviceList.get(which));//将要连接的设备传递过去
                                startService(intent);
                                Toast.makeText(HomeActivity.this, "正在连接适配器...", Toast.LENGTH_SHORT).show();//可以使用进度的一个Dialog来实现
                                dialog.dismiss();


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        builder.setNegativeButton("取消", null);
        builder.setNeutralButton("重新扫描", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                //重新扫描
                Intent intent = new Intent(HomeActivity.this, MyBluetoothService.class);
                intent.putExtra(TAG, CMDContacts.RESQUEST_DISCOVER_BlUETOOTH);
                startService(intent);
                dialog.dismiss();
                //扫描适配器的对话框出现
                loadingDialog.show2();
            }
        });
        dialogDevice = builder.create();
        if (exitDialog != null) {
            exitDialog.dismiss();
        }
        dialogDevice.show();
    }

    /**
     * 点击返回键弹出对话框
     */
    private Dialog exitDialog;

    @Override
    public void onBackPressed() {
        //先隐藏加载对话框

        loadingDialog.dismiss();

        // 传入style取出对话框自带标题栏
        exitDialog = new Dialog(this, R.style.dialog_exit);
        // 设置对话框的布局
        exitDialog.setContentView(R.layout.dialog_exit);
        // 监听退出按钮
        final Button btnleft = (Button) exitDialog.findViewById(R.id.dialog_btn_exit_exit);
        // 监听最小化按钮
        final Button btnmiddle = (Button) exitDialog.findViewById(R.id.dialog_btn_exit_min);
        // 监听取消按钮
        final Button btnright = (Button) exitDialog.findViewById(R.id.dialog_btn_exit_cancel);
        // 默认选中取消按钮
//        btnright.setPressed(true);
        btnleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnleft.setPressed(true);
                btnright.setPressed(false);
                btnmiddle.setPressed(false);
                finish();
            }
        });
        btnmiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnleft.setPressed(false);
                btnright.setPressed(false);
                btnmiddle.setPressed(true);
                // 模拟home键操作
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);

                exitDialog.dismiss();
            }
        });
        btnright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnleft.setPressed(false);
                btnmiddle.setPressed(false);
                btnright.setPressed(true);
                exitDialog.dismiss();
            }
        });

        // 显示对话框
        exitDialog.show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, "onDestroy执行啦");
        //当activity 消失的时候结束服务
        Intent intent = new Intent(this, MyBluetoothService.class);
        stopService(intent);
        //反注册蓝牙状态改变的广播
        unregisterReceiver(stateChangeReceiver);
        //反注册EventBus
        EventBus.getDefault().unregister(this);
        //解绑服务
        unbindService(conn);
        //断开连接
        MyApplication.CONNECTED = false;
        if (exitDialog != null) {
            exitDialog.dismiss();
        }
    }

}




