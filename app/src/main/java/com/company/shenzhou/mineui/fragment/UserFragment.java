package com.company.shenzhou.mineui.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.shenzhou.R;
import com.company.shenzhou.app.TitleBarFragment;
import com.company.shenzhou.mineui.MainActivity;
import com.company.shenzhou.ui.adapter.StatusAdapter;
import com.hjq.base.BaseAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : 用户界面
 */
public final class UserFragment extends TitleBarFragment<MainActivity> implements OnRefreshLoadMoreListener, BaseAdapter.OnItemClickListener {
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;

    private StatusAdapter mAdapter;
    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_fragment;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_status_refresh);
        mRecyclerView = findViewById(R.id.rv_status_list);

        mAdapter = new StatusAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }
}