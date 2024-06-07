package com.company.shenzhou.mineui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.company.shenzhou.R;
import com.company.shenzhou.app.AppActivity;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.ui.dialog.SelectDialog;
import com.company.shenzhou.utlis.LogUtils;
import com.hjq.widget.layout.SettingBar;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : 后台管理
 */
public final class BackstageManagerActivity extends AppActivity {
    private static final String TAG = "后台管理，界面==";
    private SettingBar mApiMode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_backstage_manager;
    }

    @Override
    protected void initView() {
        mApiMode = findViewById(R.id.bar_debug_api);
        setOnClickListener(R.id.bar_debug_api, R.id.bar_debug_ping);
        initApiSettingBar();

    }

    private void initApiSettingBar() {
        //右边文字太长，跑马灯效果
        TextView leftView = mApiMode.getLeftView();
        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        leftParams.gravity = Gravity.CENTER_VERTICAL;
        leftView.setLayoutParams(leftParams);
        mApiMode.getRightView().setSelected(true);
        mApiMode.getRightView().setSingleLine(true);
        mApiMode.getRightView().setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mApiMode.getRightView().setMarqueeRepeatLimit(-1);
        mApiMode.getRightView().setFocusable(true);
        mApiMode.getRightView().setFocusableInTouchMode(true);
        mApiMode.getRightView().requestFocus();
        mApiMode.getRightView().requestFocusFromTouch();
    }


    @Override
    protected void initData() {
        if (Constants.BaseUrl.equals(Constants.ReleaseBaseUrl)) {
            mApiMode.setRightText(getResources().getString(R.string.back_debug_api_release) + " " + Constants.ReleaseBaseUrl);
        } else {
            mApiMode.setRightText(getResources().getString(R.string.back_debug_api_test) + " " + Constants.TestBaseUrl);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bar_debug_api) {
            showAPIDialog();
        } else if (id == R.id.bar_debug_ping) {
            startActivity(new Intent(BackstageManagerActivity.this, PingActivity.class));
        }
    }


    private void showAPIDialog() {
        new SelectDialog.Builder(BackstageManagerActivity.this)
                .setTitle(getResources().getString(R.string.mine_exit_title))
                .setList(getResources().getString(R.string.back_debug_api_release), getResources().getString(R.string.back_debug_api_test))
                .setSingleSelect()
                .setSelect(0)
                .setCanceledOnTouchOutside(true)
                .setBackgroundDimEnabled(true)
                .setListener((SelectDialog.OnListener<String>) (dialog, data) -> {
                    String position = data.toString().substring(1, 2);
                    if ("0".equals(position)) {
                        Constants.setHttpReleaseMode(true);
                        mApiMode.setRightText(getResources().getString(R.string.back_debug_api_release) + " " + Constants.ReleaseBaseUrl);
                        LogUtils.e(TAG + "http==正式==" + Constants.BaseUrl + " " + Constants.ReleaseBaseUrl);
                    } else {
                        Constants.setHttpReleaseMode(false);
                        mApiMode.setRightText(getResources().getString(R.string.back_debug_api_test) + " " + Constants.TestBaseUrl);
                        LogUtils.e(TAG + "http==测试==" + Constants.BaseUrl + " " + Constants.TestBaseUrl);
                    }
                    ToastUtils.showShort(getResources().getString(R.string.device_toast04));

                }).show();
    }


}