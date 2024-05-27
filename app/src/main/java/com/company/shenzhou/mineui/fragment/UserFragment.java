package com.company.shenzhou.mineui.fragment;

import com.company.shenzhou.R;
import com.company.shenzhou.app.TitleBarFragment;
import com.company.shenzhou.mineui.MainActivity;

/**
 * author : Android 轮子哥
 * time   : 2018/10/18
 * desc   : 用户界面
 */
public final class UserFragment extends TitleBarFragment<MainActivity> {

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_fragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}