package com.company.shenzhou.mineui.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.company.shenzhou.R;
import com.company.shenzhou.app.AppActivity;
import com.company.shenzhou.utlis.CommonUtil;
import com.company.shenzhou.utlis.LogUtils;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : 专门写测试的界面
 */
public final class TestActivity extends AppActivity {
    private static final String TAG = "TestActivity，界面==";
    private RelativeLayout mDeviceView, mTopView;
    private RelativeLayout mElseView;
    private LinearLayout mTopLinearView;
    private TextView mOneView, mTowView, mAllView;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mHalfHeight;
    private String mCurrPageType = "竖屏";  //1=横屏，2=竖屏
    private LinearLayout mRootView;
    private int mHeng7Width;
    private int mHeng3Width;
    private int mHengHeight;
    private int mHengWidth;
    private int mShuHeight;
    private int mHalfShuHeight;
    private int mShuWidth;
    private int mHeng3Height;
    private int mHeng7Height;
    private int mHalfHengYIndex;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_01_test;
    }

    @Override
    protected void initView() {
        //设置沉浸式观影模式体验
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //永远不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mTopView = findViewById(R.id.top_relative);
        mRootView = findViewById(R.id.root);
        mDeviceView = findViewById(R.id.player_tencent);
        mElseView = findViewById(R.id.player_tencent2);
        mOneView = findViewById(R.id.one);
        mTowView = findViewById(R.id.tow);
        mAllView = findViewById(R.id.quanping);
        mScreenWidth = CommonUtil.getScreenWidth(TestActivity.this);
        mScreenHeight = CommonUtil.getScreenHeight(TestActivity.this);
        mHalfHeight = mScreenHeight / 2;
        //因为横竖屏切换之后，宽高会对调，所以初始化的时候标识出横竖屏的值，省去每次切换横竖屏调取API
        //竖屏的宽高
        mShuWidth = mScreenWidth;
        mShuHeight = mScreenHeight;
        mHalfShuHeight = mScreenHeight / 2;
        //横屏之后
        //横屏之后，小屏幕需要对齐：所以需要计算：已知横屏后（长时竖屏高的百分之70，根据16：9）算出高x，  （然后屏幕高-x）/2
        mHengWidth = mScreenHeight;
        mHengHeight = mScreenWidth;
        mHeng7Width = (int) (mHengWidth * 0.7);
        mHeng3Width = mHengWidth - mHeng7Width;
        mHeng3Height = (9 * mHeng3Width) / 16;
        mHeng7Height = (9 * mHeng7Width) / 16;
        mHalfHengYIndex = (mHengHeight - mHeng7Height) / 2;
        LogUtils.e(TAG + "========竖屏的数值=======");
        LogUtils.e(TAG + "mScreenWidth==" + mScreenWidth);//1440
        LogUtils.e(TAG + "mScreenHeight==" + mScreenHeight);//2560
        LogUtils.e(TAG + "mHalfHeight==" + mHalfHeight);//1280
        LogUtils.e(TAG + "========横屏的数值=======");
        LogUtils.e(TAG + "mHengWidth==" + mHengWidth);//2560
        LogUtils.e(TAG + "mHengHeight==" + mHengHeight);//1440
        LogUtils.e(TAG + "mHeng7Width==" + mHeng7Width);//1792
        LogUtils.e(TAG + "mHeng3Width==" + mHeng3Width); //768
        LogUtils.e(TAG + "mHeng3Height==" + mHeng3Height); //432
        LogUtils.e(TAG + "mHeng7Height==" + mHeng7Height); //1008
        LogUtils.e(TAG + "mHalfHengIndex==" + mHalfHengYIndex); //504
        LogUtils.e(TAG + "===================");
        deviceView2TopLayout();
        setOnClickListener(R.id.player_tencent, R.id.player_tencent2, R.id.one, R.id.tow, R.id.quanping);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        mScreenWidth = CommonUtil.getScreenWidth(TestActivity.this);
        mScreenHeight = CommonUtil.getScreenHeight(TestActivity.this);
        mHalfHeight = mScreenHeight / 2;
        LogUtils.e(TAG + "mScreenWidth==" + mScreenWidth);
        LogUtils.e(TAG + "mScreenHeight==" + mScreenHeight);
        LogUtils.e(TAG + "mHalfHeight==" + mHalfHeight);
        LogUtils.e(TAG + "mCurrPageType==" + mCurrPageType);
        switch (v.getId()) {
            case R.id.one://红色的上（大）
                ToastUtils.showShort("1被点击了");
                if ("竖屏".equals(mCurrPageType)) {
                    deviceView2TopLayout();
                } else if ("横屏".equals(mCurrPageType)) {
                    //红色的上（大）
                    deviceView2BigLayout();

                }
                break;
            case R.id.tow://黄色的上（大）
                ToastUtils.showShort("2被点击了");
                if ("竖屏".equals(mCurrPageType)) {
                    elseView2TopLayout();
                } else if ("横屏".equals(mCurrPageType)) {
                    //黄色的上（大）
                    elseView2BigLayout();
                }
                break;
            case R.id.quanping:
                setVideoWindowType();
                break;
            case R.id.player_tencent:
                ToastUtils.showShort("红色被点击了");
                break;
            case R.id.player_tencent2:
                ToastUtils.showShort("黄色被点击了");
                break;
        }

    }

    private boolean hengAnimFinish = true;
    private boolean ShuAnimFinish = true;

    /**
     * 横屏
     * 术野View，在最左边，最大（占比百分之70，设备的View占比百分之30）
     * 黄色的上（大）redHengYellow2Top
     */
    private void elseView2BigLayout() {
        LogUtils.e(TAG + "redHengYellow2Top=m7Width=");
        LogUtils.e(TAG + "mScreenWidth==" + mScreenWidth);
        LogUtils.e(TAG + "mScreenHeight==" + mScreenHeight);
        LogUtils.e(TAG + "=mHeng7Width=" + mHeng7Width);
        LogUtils.e(TAG + "=mHeng3Width=" + mHeng3Width);
        RelativeLayout.LayoutParams mRelativeParams1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mRelativeParams1.width = mHeng7Width;
        mRelativeParams1.height = mHeng7Height;
        RelativeLayout.LayoutParams mRelativeParams2 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mRelativeParams2.width = mHeng3Width;
        mRelativeParams2.height = mHeng3Height;
        mDeviceView.setLayoutParams(mRelativeParams1);
        mElseView.setLayoutParams(mRelativeParams2);
//        mRedPlayederView1.setX(mHeng7Width);
//        mRedPlayederView1.setY(0);
//        mYellowPlayerView2.setX(0);
//        mYellowPlayerView2.setY(0);
        mElseView.setTranslationX(mHeng7Width);
        mElseView.setTranslationY(mHalfHengYIndex);
        mDeviceView.setTranslationX(0);
        mDeviceView.setTranslationY(mHalfHengYIndex);

        //大块的做缩放和移动的动画


    }

    /**
     * 设置播放器:全屏/半屏,显示
     * Js 交互解决手机全屏的画面显示不全
     * 全屏传true,竖屏传false
     */

    public void setVideoWindowType() {
        LogUtils.e(TAG + "======切换==hengAnimFinish=" + hengAnimFinish);
        LogUtils.e(TAG + "======切换==ShuAnimFinish=" + ShuAnimFinish);

        if (!hengAnimFinish || !ShuAnimFinish) {
            ToastUtils.showShort("动画未执行完毕，不能切换");
            return;
        }
        int orientation = getRequestedOrientation();
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //竖屏
            mCurrPageType = "竖屏";
            LogUtils.e(TAG + "======切换==竖屏=");
            deviceView2TopLayout();

        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
            mCurrPageType = "横屏";
            //红色的上（大）
            LogUtils.e(TAG + "======切换==横屏=");
//            deviceView2BigLayout();

            RelativeLayout.LayoutParams mRelativeParams1 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mRelativeParams1.width = mHeng7Width;
            mRelativeParams1.height = mHeng7Height;
            RelativeLayout.LayoutParams mRelativeParams2 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mRelativeParams2.width = mHeng3Width;
            mRelativeParams2.height = mHeng3Height;
            int mHeight = mShuWidth - mHeng3Height;
            LogUtils.e(TAG + "=mHeight=" + mHeight);
            mDeviceView.setLayoutParams(mRelativeParams1);
            mElseView.setLayoutParams(mRelativeParams2);
            mDeviceView.setTranslationX(0);
            mDeviceView.setTranslationY(mHalfHengYIndex);
            mElseView.setTranslationX(0);
            mElseView.setTranslationY(mHeight);
            //使用PropertyValuesHolder来分别设置translationX和translationY的动画效果
            PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofFloat("translationX", 0, mHeng7Width);
            PropertyValuesHolder pvhTranslateY = PropertyValuesHolder.ofFloat("translationY", mHeight, mHalfHengYIndex);
            // 创建一个动画集合，并将两个PropertyValuesHolder加入到集合中
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mElseView, pvhTranslateX, pvhTranslateY);
            // 设置动画时长
            animator.setDuration(8000);
            // 启动动画
            animator.start();
            hengAnimFinish = false;
            mRootView.postDelayed(() -> hengAnimFinish = true, 1200);


        }
    }

    /**
     * 横屏
     * 设备View，在最左边，最大（占比百分之70，术野的View占比百分之30）
     * redHengBig2Top
     */
    private void deviceView2BigLayout() {
        LogUtils.e(TAG + "redHengYellow2Top==");
        LogUtils.e(TAG + "mScreenWidth==" + mScreenWidth);
        LogUtils.e(TAG + "mScreenHeight==" + mScreenHeight);
        LogUtils.e(TAG + "=mHeng7Width=" + mHeng7Width);
        LogUtils.e(TAG + "=mHeng3Width=" + mHeng3Width);
        RelativeLayout.LayoutParams mRelativeParams1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mRelativeParams1.width = mHeng7Width;
        mRelativeParams1.height = mHeng7Height;
        RelativeLayout.LayoutParams mRelativeParams2 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mRelativeParams2.width = mHeng3Width;
        mRelativeParams2.height = mHeng3Height;
        mElseView.setLayoutParams(mRelativeParams1);
        mDeviceView.setLayoutParams(mRelativeParams2);
        mElseView.setTranslationX(0);
        mElseView.setTranslationY(mHalfHengYIndex);
        mDeviceView.setTranslationX(mHeng7Width);
        mDeviceView.setTranslationY(mHalfHengYIndex);


    }

    //竖屏，设备view 移动到最顶端
    private void deviceView2TopLayout() {
        LogUtils.e(TAG + "redShuBig2Top==");
        LogUtils.e(TAG + "mScreenWidth==" + mScreenWidth);
        LogUtils.e(TAG + "mScreenHeight==" + mScreenHeight);
        LogUtils.e(TAG + "=mShuWidth=" + mShuWidth);
        LogUtils.e(TAG + "=mShuHeight=" + mShuHeight);
        LogUtils.e(TAG + "=mHalfShuHeight=" + mHalfShuHeight);
        LogUtils.e(TAG + "=mHalfHeight=" + mHalfHeight);
        RelativeLayout.LayoutParams mRelativeParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mRelativeParams.height = mHalfShuHeight;
        mDeviceView.setLayoutParams(mRelativeParams);
        mElseView.setLayoutParams(mRelativeParams);
        mDeviceView.setX(0);
        mDeviceView.setY(0);
        mElseView.setX(0);
        mElseView.setY(mHalfShuHeight);

    }

    //竖屏，术野view 移动到最顶端
    private void elseView2TopLayout() {
        LogUtils.e(TAG + "yellowShuBig2Top==");
        LogUtils.e(TAG + "mScreenWidth==" + mScreenWidth);
        LogUtils.e(TAG + "mScreenHeight==" + mScreenHeight);
        LogUtils.e(TAG + "=mShuWidth=" + mShuWidth);
        LogUtils.e(TAG + "=mShuHeight=" + mShuHeight);
        LogUtils.e(TAG + "=mHalfShuHeight=" + mHalfShuHeight);
        LogUtils.e(TAG + "=mHalfHeight=" + mHalfHeight);
        RelativeLayout.LayoutParams mRelativeParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mRelativeParams.height = mHalfHeight;
        mDeviceView.setLayoutParams(mRelativeParams);
        mElseView.setLayoutParams(mRelativeParams);

        mDeviceView.setX(0);
        mDeviceView.setY(mHalfHeight);
        mElseView.setX(0);
        mElseView.setY(0);
    }


}