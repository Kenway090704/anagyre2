package com.aofei.tch.anagyre.vs.listener;

import com.aofei.tch.anagyre.vs.bean.PlayerInfo;

import java.util.List;

/**
 * VsFragment与ConnectFragment通信的接口
 * 让两个Fragment中的数据同步
 */
public interface OnConnectToVsListener{
    /**
     * VsFragment 侧滑栏 更新
     * @param infoList
     */
        void update(List<PlayerInfo> infoList);

    /**
     * 同步VsFragment与ConnectFragment 对码状态
     * @param state  state==0 停止对码  state==1 对码
     */
        void MatchCodeState(int state);
    }