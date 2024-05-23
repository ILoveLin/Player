package com.company.shenzhou.mineui.fragment.user;

import com.company.shenzhou.R;
import com.company.shenzhou.app.AppFragment;
import com.company.shenzhou.mineui.MainActivity;
import com.company.shenzhou.ui.activity.CopyActivity;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 用户界面
 */
public final class UserFragment extends AppFragment<MainActivity> {

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
}