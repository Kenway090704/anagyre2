package com.aofei.tch.anagyre.vs.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.aofei.tch.anagyre.other.utils.log.LogUtils;
import com.aofei.tch.anagyre.other.utils.log.MyDate;
import com.aofei.tch.anagyre.vs.bean.CompetionInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 比赛数据存储类
 * gson把List转换成json类型，再利用SharedPreferences保存的。
 * Created by kenway on 16/12/19 14:51
 * Email : xiaokai090704@126.com
 */

public class SaveCompetitionDataPre {

    private static final String TAG = "SaveCompetitionDataPre";
    private  static  final  String SHARED_PREFES_NAME="Competitions-Data";
    private  static  final  String SHARED_TAG__NAME="competitions";
    private  static  final  int SAVE_DAY=1;//比赛数据保存天数
    private  static SaveCompetitionDataPre pre;
    private  static  Context mContext;
    private SharedPreferences preferencesData;
    private SharedPreferences.Editor editorData;

    /**
     * 获取单例
     * @return
     */
    public static  SaveCompetitionDataPre getInstance(){
        if (null==pre){
            synchronized (SaveCompetitionDataPre.class){
                pre=new SaveCompetitionDataPre(mContext,SHARED_PREFES_NAME);
            }
        }
        return  pre;
    }
    /**
     * 初始化这个管理类
     * @param context
     */
    public static void init(Context context) {
        mContext= context;
    }
    /**
     * @param mContext       上下文对象
     * @param preferenceName sharePerference的名字
     */
    private SaveCompetitionDataPre(Context mContext, String preferenceName) {
        preferencesData = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editorData = preferencesData.edit();
    }



    /**
     *保存某一场比赛的结果与信息
     * @param com  当前场次比赛
     */
    public  void saveCompetioninfo(CompetionInfo com){
        if (null == com ) {
            return;
        }
       List<CompetionInfo> list=  getDataList();
        if (null==list){
            list=new ArrayList<>();
        }
        list.add(com);
        Gson gson = new Gson();

        for (int i = 0; i < list.size(); i++) {
            //去除昨天的比赛数据,(只要日期不是今天都会去除)
            if (MyDate.getBefomeSomeDay(SAVE_DAY).equals(MyDate.getTimeStr(list.get(i).gettimeGameEnd()))) {
                list.remove(i);
                i--;
            }
        }
        LogUtils.e(TAG,"list.size()="+list.size());
        //转换城json数据,再保存
        String strJson = gson.toJson(list);
        editorData.putString(SHARED_TAG__NAME, strJson);
        editorData.commit();
    }

    /**
     * 获取所有比赛的对象
     * @param
     * @return
     */
    public List<CompetionInfo> getDataList() {
        List<CompetionInfo> datalist = new ArrayList<>();
        String strJson = preferencesData.getString(SHARED_TAG__NAME, null);
        if (null == strJson) {
            return datalist;
        }
        Type listType = new TypeToken<ArrayList<CompetionInfo>>() {
        }.getType();
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, listType);
        for (int i = 0; i < datalist.size(); i++) {
            //去除昨天的比赛数据,(只要日期不是今天都会去除)
            if (!MyDate.getFileName().equals(MyDate.getTimeStr(datalist.get(i).gettimeGameEnd()))) {
                datalist.remove(i);
                i--;
            }
        }

         strJson = gson.toJson(datalist);
         editorData.putString(SHARED_TAG__NAME, strJson);
         editorData.commit();
         return datalist;
    }

    /**
     * 清空所有比赛数据
     */
    public  void clear(){
        List<CompetionInfo> list=new ArrayList<>();
        Gson gson = new Gson();
        //转换城json数据,再保存
        String strJson = gson.toJson(list);
        editorData.putString(SHARED_TAG__NAME, strJson);
        editorData.commit();
    }

    /**
     * 判断当前存储了多少字节
     * @return  bytes.length--比赛信息存储了多少字节
     */
    public  long  hasBytes(){
        String strJson = preferencesData.getString(SHARED_TAG__NAME, null);
        byte[] bytes= strJson.getBytes();
        return  bytes.length;
    }

    /**
     * 获取list
     *
     * @param tag
     * @param
     * @return
     */
    public List<CompetionInfo> getDataList(String tag) {
        List<CompetionInfo> datalist = new ArrayList<>();
        String strJson = preferencesData.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Type listType = new TypeToken<ArrayList<CompetionInfo>>() {
        }.getType();
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, listType);

        for (int i = 0; i < datalist.size(); i++) {
            //去除昨天的比赛数据,(只要日期不是今天都会去除)
            if (!MyDate.getFileName().equals(MyDate.getTimeStr(datalist.get(i).gettimeGameEnd()))) {
                datalist.remove(i);
                i--;
            }
        }
        Collections.reverse(datalist);//将最后保存的放在最前面
        return datalist;
    }
    /**
     * 保存List数据--只保存当日的数据
     * @param tag      该场比赛的名字---"Competions"(固定)
     * @param dataList 比赛数据
     * @param
     */
    public void saveDataList(String tag, List<CompetionInfo> dataList) {
        if (null == dataList || dataList.size() <= 0) {
            return;
        }
        Gson gson = new Gson();
        //转换城json数据,再保存
        String strJson = gson.toJson(dataList);
        editorData.putString(tag, strJson);
        editorData.commit();
    }
}
