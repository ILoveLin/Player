package com.company.shenzhou.mineui.activity;

import android.view.View;
import android.widget.RelativeLayout;

import com.company.shenzhou.R;
import com.company.shenzhou.app.AppActivity;
import com.company.shenzhou.utlis.LogUtils;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : VLC-线路2-http-Nginx
 */
public final class PlayerLine2Activity extends AppActivity implements View.OnClickListener {
    private static final String TAG = "线路2，界面==";
    public static String mPath01 = "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8";

    private View.OnTouchListener onTouchVideoListener;
    private RelativeLayout mRootView;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_player_line2;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }


}