package com.company.shenzhou.mineui.fragment;

import com.company.shenzhou.R;
import com.company.shenzhou.app.TitleBarFragment;
import com.company.shenzhou.mineui.MainActivity;

/**
 * author : Android 轮子哥
 * time   : 2018/10/18
 * desc   : 设备界面
 */
public final class DeviceFragment extends TitleBarFragment<MainActivity> {

    public static DeviceFragment newInstance() {
        return new DeviceFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.device_fragment;
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