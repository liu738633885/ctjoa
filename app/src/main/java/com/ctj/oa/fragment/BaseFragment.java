package com.ctj.oa.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lewis on 16/6/15.
 */
public abstract class BaseFragment extends Fragment {
    //获取fragment布局文件ID
    protected abstract int getLayoutId();

    protected abstract void initView(View view, Bundle savedInstanceState);

    protected View parentView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (parentView == null) {
            parentView = inflater.inflate(getLayoutId(), container, false);
        }
        initView(parentView, savedInstanceState);

        //防止嵌套在 viewpager 崩溃
        ViewGroup parent = (ViewGroup) parentView.getParent();
        if (parent != null) {
            parent.removeView(parentView);
        }
        return parentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(Boolean b) {
    }

    public void baseRefresh() {
    }
}
