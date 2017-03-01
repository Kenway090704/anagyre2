package com.aofei.tch.anagyre.other.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 基本Fragment
 * Created by Xiaokao on 2016/11/17
 */
public abstract class BaseFragment extends Fragment {

    /**
     * 根布局
     */
    protected View root;
    @Override
    public void onResume() {
        super.onResume();
        initData();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 加载根布局
        root = inflater.inflate(getLayoutId(), null);

        // 初始化控件
        initViews(root);
        // 初始化监听事件
        initEnvent();
        return root;
    }

    /**
     * 获取布局的id
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化视图
     */
    protected abstract void initViews(View root);

    /**
     * 初始化视图的监听事件
     */
    protected abstract void initEnvent();

    /**
     * 初始化数据
     */
    protected abstract void initData();

}
