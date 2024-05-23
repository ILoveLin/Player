package com.company.shenzhou.mineui.fragment.device;

import android.app.Activity;

import com.company.shenzhou.R;
import com.company.shenzhou.app.AppFragment;
import com.company.shenzhou.mineui.MainActivity;
import com.company.shenzhou.ui.activity.CopyActivity;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 设备界面
 */
public final class DeviceFragment extends AppFragment<MainActivity> {

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
}