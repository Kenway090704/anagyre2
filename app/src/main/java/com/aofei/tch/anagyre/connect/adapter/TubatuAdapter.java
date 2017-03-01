package com.aofei.tch.anagyre.connect.adapter;

import android.content.Context;
import android.content.Intent;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aofei.tch.anagyre.R;
import com.aofei.tch.anagyre.bluetooth.cmd.CMDContacts;
import com.aofei.tch.anagyre.bluetooth.model.MyBluetoothService;
import com.aofei.tch.anagyre.bluetooth.taskqueue.listener.OnDeletePlayerTaskListener;
import com.aofei.tch.anagyre.connect.ui.ConnectFragment;
import com.aofei.tch.anagyre.main.ui.HomeActivity;
import com.aofei.tch.anagyre.other.utils.Debug;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;
import com.aofei.tch.anagyre.vs.bean.PlayerInfo;
import com.aofei.tch.anagyre.vs.listener.OnConnectToVsListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;
//import java.util.logging.Handler;

import static android.content.ContentValues.TAG;

/**
 * Created by wujian on 2016/3/23.
 * RecyclingPagerAdapter是Jake WhartonAndroid大神封装的可用于复用的PagerAdapter。
 */
public class TubatuAdapter extends RecyclingPagerAdapter {


    private List<PlayerInfo> mList;
    private Context mContext;


    private MyBluetoothService service;
//    HashMap<Integer, View> imap = new HashMap<>();

    private int index;
    private int ballposition;

    TextView tv_con_num;
    ClipViewPager mViewPager;

    private OnConnectToVsListener toVsListener;

    private boolean isDeleting;

    public void setOnConnectToVsListener(OnConnectToVsListener toVsListener) {
        this.toVsListener = toVsListener;
        LogUtils.e(TAG,"与VsFragment通信接口已建立");
    }


    public TubatuAdapter(Context context, List<PlayerInfo> mList) {
        mContext = context;

        this.mList = mList;
        notifyDataSetChanged();

        //删除任务的回调接口

    }


    //删除好友的任务监听
    private OnDeletePlayerTaskListener onDeletePlayerTaskListener =
            new OnDeletePlayerTaskListener() {
                @Override
                public void onDeletePlayering() {

                }

                @Override
                public void onDeletePlayerFail() {

                }

                @Override
                public void onDeletePlayerSuccessful(final int deleteNumber) {


                    if (deleteNumber == index) { //删除的球号

                        tv_con_num = ((HomeActivity) mContext).connectFragment.getTv_con_num();
                        mViewPager = ((HomeActivity) mContext).connectFragment.getmViewPager();

                        if (mList.size() >= 2) {
                            LogUtils.e(TAG, Debug.line(new Exception()) + "hang,Tag==" + index + " deleteNumber== " + deleteNumber);
                            ((HomeActivity) mContext).runOnUiThread(new Runnable() {

                                @Override
                                public void run() {


                                    if (ballposition == 0 || ballposition == mList.size() - 1) {
                                        ConnectFragment.isDeleteAnimation = true;
                                        ConnectFragment.isDelete = true;
                                        if (ballposition == 0) {
                                            ConnectFragment.isDeletePosition = -(mList.size() - 2);
                                            ConnectFragment.isStartorLeft = false;

                                        } else if (ballposition == mList.size() - 1) {
                                            ConnectFragment.isDeletePosition = -1;
                                            ConnectFragment.isStartorLeft = true;
                                        }
                                    }
                                    mList.remove(ballposition);
                                    mViewPager.removeAllViews();

                                    ConnectFragment.totalBall = mList.size() - 1;

                                    notifyDataSetChanged();

                                    tv_con_num.setText(mList.size() + "");
                                    mViewPager.setOffscreenPageLimit(mList.size());

                                    if (toVsListener!=null){
                                        toVsListener.update(mList);//删除玩家数据,然后在VsFragment中更新
                                    }
                                    if (ballposition == 0) {

                                        mViewPager.setCurrentItem(0);

                                    } else {
                                        mViewPager.setCurrentItem(ballposition - 1);
                                    }


                                    ConnectFragment.isDelete = false;





                                }


                            });

                        } else {


                            ((HomeActivity) mContext).runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    View view = mViewPager.findViewWithTag(ballposition);

                                    CheckBox cb_delete = ((HomeActivity) mContext).connectFragment.getCb_delete();
                                    cb_delete.setChecked(false);
                                    cb_delete.setBackgroundResource(R.mipmap.delete);
                                    cb_delete.setText("");


                                    ImageView imageV = (ImageView) view.findViewById(R.id.player_photo_img);
                                    ImageView imageBg = (ImageView) view.findViewById(R.id.player_background);
//                                    RoundedImageView roundImage = (RoundedImageView) view.findViewById(R.id.roundedImageView);
                                    ImageView deleteV = (ImageView) view.findViewById(R.id.deleteIcon);
                                    deleteV.setVisibility(view.INVISIBLE);
                                    tv_con_num.setText("0");
                                    imageBg.setImageResource(R.mipmap.no_player);
                                    imageV.setVisibility(view.INVISIBLE);


                                    mList.clear();
                                    notifyDataSetChanged();
                                    PlayerInfo info = new PlayerInfo();
                                    ConnectFragment.getBallColor(0,info);
                                    info.setBallNum(0);
                                    mList.add(info);
                                    notifyDataSetChanged();


                                    if (toVsListener!=null){
                                        toVsListener.update(mList);//删除玩家数据,然后在VsFragment中更新
                                    }

                                }
                            });


                        }





                    } else {
                        LogUtils.e(TAG, "不匹配");
                    }

                    isDeleting = false;
                }

                @Override
                public void onDeletePlayerNoResponse() {
                }
            };

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        ImageView imageView = null;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.widget_player_img, null);

            convertView.setTag(position);
//            imap.put(position, convertView);
        } else {
//            convertView = imap.get(position);
        }

        imageView = (ImageView) convertView.findViewById(R.id.player_photo_img);
        ImageView bgimageV = (ImageView) convertView.findViewById(R.id.player_background);

        TextView username = (TextView) convertView.findViewById(R.id.playername);

        LogUtils.e(TAG,"mList.get(position).getName() = "+mList.get(position).getName()+"mList.get(position).getId()="+mList.get(position).getId());



        username.setText(mList.get(position).getName());
        TextView playerID = (TextView) convertView.findViewById(R.id.player_textID);
        playerID.setText(mList.get(position).getId());
        TextView exp = (TextView) convertView.findViewById(R.id.textView2);
        exp.setText(mList.get(position).getExValue()+"");


        RoundedImageView roundImage = (RoundedImageView) convertView.findViewById(R.id.roundedImageView);
        roundImage.setImageResource(mList.get(position).getPhotoSamllID());
        roundImage.setBorderColor(mContext.getResources().getColor(mList.get(position).getColorID()));
        LogUtils.e(TAG,"颜色为"+mList.get(position).getColorID());
        roundImage.setBorderWidth(5.0f);
        if (mList.get(position).getBallNum() != 0) {
            imageView.setVisibility(convertView.VISIBLE);
            bgimageV.setImageResource(R.mipmap.player_bg);

        }

        ImageView deleteImage = (ImageView) convertView.findViewById(R.id.deleteIcon);

        final View finalConvertView = convertView;

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (isDeleting == false) {

                    isDeleting = true;
                    Intent intent = new Intent(mContext, MyBluetoothService.class);
                    intent.putExtra(HomeActivity.TAG, CMDContacts.RESQUEST_DELETE_ANAGYRE);
                    intent.putExtra("deleteNum", mList.get(position).getBallNum());//将要删除的球号传递到service

                    service = ((HomeActivity) mContext).myService;//获取Service
                    service.setOnDeletePlayerTaskListener(onDeletePlayerTaskListener);

                    mContext.startService(intent);


                    index = mList.get(position).getBallNum();//f
                    ballposition = position;
                }else {
                    Toast.makeText(mContext,"正在删除请稍后...",Toast.LENGTH_LONG).show();
                }
            }
        });


        return convertView;
    }


    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public void destroyItem(View container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return PagerAdapter.POSITION_NONE;
    }

}