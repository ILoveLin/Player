package com.company.shenzhou.mineui.activity;

import android.view.View;
import android.widget.RelativeLayout;

import com.company.shenzhou.R;
import com.company.shenzhou.app.AppActivity;
import com.company.shenzhou.utlis.LogUtils;
import com.company.shenzhou.widget.vlc.MyControlVlcVideoView;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : VLC-线路2-http-Nginx
 */
public final class PlayerLine2Activity extends AppActivity implements View.OnClickListener {
    private static final String TAG = "线路2，界面==";
    public static String mPath01 = "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8";

    private MyControlVlcVideoView mControlPlayView;
    private View.OnTouchListener onTouchVideoListener;
    private RelativeLayout mRootView;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_player_line2;
    }

    @Override
    protected void initView() {
        mControlPlayView = findViewById(R.id.player_control);
        onTouchVideoListener = mControlPlayView.getOnTouchVideoListener();
        //触摸控制亮度和声音,是否可触摸开关
        mRootView = mControlPlayView.getRootView();
        mRootView.setLongClickable(true);  //手势需要--能触摸
        mRootView.setOnTouchListener(onTouchVideoListener);

        mControlPlayView.setPlayerTitle("你好，我是播放器--1");
        mControlPlayView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //开始播放
                mControlPlayView.setStartLive(mPath01);

            }
        }, 500);

        mControlPlayView.setOnPlayerOrClickListener(new MyControlVlcVideoView.onPlayerOrClickListener() {
            @Override
            public void eventPlay() {
                LogUtils.e(TAG + "eventPlay");
                MyControlVlcVideoView.onPlayerOrClickListener.super.eventPlay();
            }

            @Override
            public void eventStop() {
                MyControlVlcVideoView.onPlayerOrClickListener.super.eventStop();
                LogUtils.e(TAG + "eventStop");

            }

            @Override
            public void eventError() {
                MyControlVlcVideoView.onPlayerOrClickListener.super.eventError();
                LogUtils.e(TAG + "eventError");

            }

            @Override
            public void onClickBack() {
                finish();
                MyControlVlcVideoView.onPlayerOrClickListener.super.onClickBack();
                LogUtils.e(TAG + "onClickBack");

            }

            @Override
            public void onClickSetting() {
                MyControlVlcVideoView.onPlayerOrClickListener.super.onClickSetting();
                LogUtils.e(TAG + "onClickSetting");

            }

            @Override
            public void onClickChangeLive() {
                MyControlVlcVideoView.onPlayerOrClickListener.super.onClickChangeLive();
                LogUtils.e(TAG + "onClickChangeLive");

            }

            @Override
            public void onClickMic() {
                MyControlVlcVideoView.onPlayerOrClickListener.super.onClickMic();
                LogUtils.e(TAG + "onClickMic");

            }

            @Override
            public void onClickPhotos() {
                MyControlVlcVideoView.onPlayerOrClickListener.super.onClickPhotos();
                LogUtils.e(TAG + "onClickPhotos");

            }

            @Override
            public void onClickSnapShot() {
                MyControlVlcVideoView.onPlayerOrClickListener.super.onClickSnapShot();
                LogUtils.e(TAG + "onClickSnapShot");

            }

            @Override
            public void onClickRestart() {
                MyControlVlcVideoView.onPlayerOrClickListener.super.onClickRestart();
                LogUtils.e(TAG + "onClickRestart");

            }
        });
    }

    @Override
    protected void initData() {

    }


}