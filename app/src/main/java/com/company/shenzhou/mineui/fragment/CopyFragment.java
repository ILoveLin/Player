package com.company.shenzhou.mineui.fragment;

import com.company.shenzhou.R;
import com.company.shenzhou.app.AppFragment;
import com.company.shenzhou.ui.activity.CopyActivity;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : 可进行拷贝的副本
 */
public final class CopyFragment extends AppFragment<CopyActivity> {

    public static CopyFragment newInstance() {
        return new CopyFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.copy_fragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}