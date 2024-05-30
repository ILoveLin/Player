package com.company.shenzhou.mineui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.shenzhou.R;
import com.company.shenzhou.app.AppActivity;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.mineui.MainActivity;
import com.company.shenzhou.utlis.LogUtils;
import com.company.shenzhou.utlis.SharePreferenceUtil;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : 闪屏界面
 */
public final class SplashActivity extends AppActivity {
    private Boolean isFirstIn;
    private Boolean isLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.splash_activity;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        ImageView ivSplash = findViewById(R.id.iv_splash);
        TextView tv_company = findViewById(R.id.tv_company);
        tv_company.setText("Copyright©" + getResources().getString(R.string.mine_company));
        //是否第一次进入app
        isFirstIn = (Boolean) SharePreferenceUtil.get(this, Constants.SP_IS_FIRST_IN, true);
        //是否登入
        isLogin = (Boolean) SharePreferenceUtil.get(this, Constants.Is_Logined, false);
        // 从浅到深,从百分之10到百分之百
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(1500);// 设置动画时间
        ivSplash.setAnimation(aa);// 给image设置动画
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    switchGoing();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    protected void initData() {
    }

    //判断进入那个activity
    private void switchGoing() {
        LogUtils.e("login==isLogin==" + isLogin);
        LogUtils.e("login==isFirstIn==" + isFirstIn);
        if (isFirstIn) {
            SharePreferenceUtil.put(SplashActivity.this, Constants.SP_IS_FIRST_IN, true);
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this, GuideActivity.class);
            startActivity(intent);
            finish();
        } else {  //不是第一次进App,判断是否登陆过
            if (!isLogin) {  //登入成功 ,false==未登录
                startActivity(LoginActivity.class);
            } else {   //已经登陆
                startActivity(MainActivity.class);
            }
            finish();
        }

    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 隐藏状态栏和导航栏
                .hideBar(BarHide.FLAG_HIDE_BAR);
    }

    @Override
    public void onBackPressed() {
        //禁用返回键
        //super.onBackPressed();
    }

    @Override
    protected void initActivity() {
        // 问题及方案：https://www.cnblogs.com/net168/p/5722752.html
        // 如果当前 Activity 不是任务栈中的第一个 Activity
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            // 如果当前 Activity 是通过桌面图标启动进入的
            if (intent != null && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                    && Intent.ACTION_MAIN.equals(intent.getAction())) {
                // 对当前 Activity 执行销毁操作，避免重复实例化入口
                finish();
                return;
            }
        }
        super.initActivity();
    }

    @Deprecated
    @Override
    protected void onDestroy() {
        // 因为修复了一个启动页被重复启动的问题，所以有可能 Activity 还没有初始化完成就已经销毁了
        // 所以如果需要在此处释放对象资源需要先对这个对象进行判空，否则可能会导致空指针异常
        super.onDestroy();
    }
}