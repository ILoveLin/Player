package com.company.shenzhou.mineui.activity;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.DeviceUtils;
import com.company.shenzhou.R;
import com.company.shenzhou.aop.SingleClick;
import com.company.shenzhou.app.AppActivity;
import com.company.shenzhou.app.AppApplication;
import com.company.shenzhou.bean.dbbean.UserDBRememberBean;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.mineui.MainActivity;
import com.company.shenzhou.playerdb.manager.UserDBRememberBeanUtils;
import com.company.shenzhou.ui.adapter.GuideAdapter;
import com.company.shenzhou.utlis.LogUtils;
import com.company.shenzhou.utlis.MD5ChangeUtil;
import com.company.shenzhou.utlis.ScreenSizeUtil;
import com.company.shenzhou.utlis.SharePreferenceUtil;
import com.blankj.utilcode.util.DeviceUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseDialog;
import com.hjq.language.MultiLanguages;

import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/09/21
 * desc   : 应用引导页
 */
public final class GuideActivity extends AppActivity {
    private static final String TAG = "guide，界面==";

    private ViewPager2 mViewPager;
    private CircleIndicator3 mIndicatorView;
    private View mCompleteView;
    private GuideAdapter mAdapter;
    private Boolean userAgreementTag;
    private Boolean isLogined;
    private String userUrl = "http://www.szcme.com/EMAIL/NOTICE-USER.HTML";  //用户协议
    private String privacyUrl = "http://www.szcme.com/EMAIL/NOTICE-b.HTML";  //隐私条款

    @Override
    protected int getLayoutId() {
        return R.layout.guide_activity;
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.vp_guide_pager);
        mIndicatorView = findViewById(R.id.cv_guide_indicator);
        mCompleteView = findViewById(R.id.btn_guide_complete);
        setOnClickListener(mCompleteView);
    }

    @Override
    protected void initData() {
        mAdapter = new GuideAdapter(this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.registerOnPageChangeCallback(mCallback);
        mIndicatorView.setViewPager(mViewPager);
        userAgreementTag = (Boolean) SharePreferenceUtil.get(GuideActivity.this, Constants.Sp_UserAgreement_Tag, false);
        isLogined = (Boolean) SharePreferenceUtil.get(this, Constants.Is_Logined, false);
        LogUtils.e(TAG + "====userAgreementTag==:" + userAgreementTag);
        LogUtils.e(TAG + "====isLogined==:" + isLogined);
        if (!userAgreementTag) {
            showUserAgreementDialog();
        }
    }

    /**
     * 第一次安装app，需要让用户同意用户协议和隐私条款
     */
    private void showUserAgreementDialog() {
        // 获取当前的语种
        String appLanguage = MultiLanguages.getAppLanguage().toString();
        SpannableString mSpanned = new SpannableString(getResources().getString(R.string.guide_user_agreement));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                BrowserActivity.start(GuideActivity.this, "http://www.szcme.com/EMAIL/NOTICE-USER.HTML");
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //"http://www.szcme.com/EMAIL/NOTICE-b.HTML";  //隐私条款
                BrowserActivity.start(GuideActivity.this, "http://www.szcme.com/EMAIL/NOTICE-b.HTML");
            }
        };
        //因为字长不一致，需要而外适配
        if (appLanguage.startsWith("en_CN")) {
            mSpanned.setSpan(clickableSpan,
                    58, 77, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mSpanned.setSpan(clickableSpan2,
                    83, 98, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            mSpanned.setSpan(clickableSpan,
                    26, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //33是-和-39是-》
            mSpanned.setSpan(clickableSpan2,
                    33, 39, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        int screenWidth = ScreenSizeUtil.getScreenWidth(this);
        int heightWidth = ScreenSizeUtil.getScreenHeight(this);
        double v = screenWidth * 0.7;
        double h = heightWidth * 0.9;
        // 自定义对话框
        BaseDialog.Builder<BaseDialog.Builder<?>> builderBuilder = new BaseDialog.Builder<>(this);
        builderBuilder
                .setContentView(R.layout.dialog_useragreement)
                .setAnimStyle(BaseDialog.ANIM_SCALE)
                .setCanceledOnTouchOutside(false)
                .setWidth((int) v)
                .setText(R.id.tv_content, mSpanned)
                .setOnClickListener(R.id.btn_dialog_custom_ok, new BaseDialog.OnClickListener<View>() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        /**
                         * 为了上华为应用市场必须明确bugly的使用隐私和目的
                         */
                        SharePreferenceUtil.put(GuideActivity.this, SharePreferenceUtil.Bugly_CanUse, true);
                        AppApplication.getInstance().intBugly();
                        SharePreferenceUtil.put(GuideActivity.this, Constants.Sp_UserAgreement_Tag, true);
                        String deviceId = DeviceUtils.getUniqueDeviceId();
//                        String deviceId = FileUtils.getSDDeviceTxt();
                        String mSend_IDBy32 = MD5ChangeUtil.Md5_32(deviceId);
                        LogUtils.e("App--GuideActivity,==02==deviceId:" + deviceId);
                        LogUtils.e("App--GuideActivity,==02==mSend_IDBy32:" + mSend_IDBy32);

                        if ("".equals(mSend_IDBy32)) {
                            LogUtils.e("App--GuideActivity,==02==获取手机唯一标识码失败");
                        } else {
                            mmkv.encode(Constants.KEY_PhoneDeviceCode, mSend_IDBy32);
                            LogUtils.e("App--GuideActivity,==02==获取手机唯一标识码成功:" + mSend_IDBy32);
                            LogUtils.e("App--GuideActivity,==02==获取手机唯一标识码成功:已存入MMKV:" + mSend_IDBy32);
                        }
                        dialog.dismiss();
                        setCurrentUserMsg();
                    }
                }).show();


        //注意：此时必须加这一句，不然点击事件不会生效
        TextView tv_content = builderBuilder.findViewById(R.id.tv_content);
        tv_content.setMovementMethod(LinkMovementMethod.getInstance());


    }

    /**
     * 因为系统默认给的账号密码admin 权限是超级用户:2
     */
    private void setCurrentUserMsg() {
        SharePreferenceUtil.put(GuideActivity.this, SharePreferenceUtil.Current_Username, "admin");
        SharePreferenceUtil.put(GuideActivity.this, SharePreferenceUtil.Current_Password, "admin");
        SharePreferenceUtil.put(GuideActivity.this, SharePreferenceUtil.Current_UserType, 2);
        SharePreferenceUtil.put(GuideActivity.this, SharePreferenceUtil.Current_ID, 1);
        //超级用户只能修改一次密码
        SharePreferenceUtil.put(GuideActivity.this, SharePreferenceUtil.Current_Admin_ChangePassword, false);

        //存入数据库
        long ID = 1;
        UserDBRememberBean userDBBean = new UserDBRememberBean();
//        UserDBBean userDBBean = new UserDBBean();
        userDBBean.setUsername("admin");
        userDBBean.setPassword("admin");
        userDBBean.setTag("admin");
        userDBBean.setUserType(2);
        userDBBean.setId(ID);
        UserDBRememberBeanUtils.insertOrReplaceData(userDBBean);
//        UserDBUtils.insertOrReplaceData(userDBBean);
        boolean isExist = UserDBRememberBeanUtils.queryListIsExist("admin");
        LogUtils.e("DB=====isExist===" + isExist);
        String str = "admin";
        List<UserDBRememberBean> userDBRememberBeans = UserDBRememberBeanUtils.queryListByMessage(str);
        for (int i = 0; i < userDBRememberBeans.size(); i++) {
            String username = userDBRememberBeans.get(i).getUsername();
            String password = userDBRememberBeans.get(i).getPassword();
            LogUtils.e("DB=====username===" + username + "==password==" + password);
        }
        LogUtils.e("DB=====isExist===" + isExist);
    }


    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mCompleteView) {

            if (!isLogined) {  //未登入,跳转登入界面
                Intent intent = new Intent();
                startActivity(LoginActivity.class);
                mmkv.encode(Constants.KEY_Login_Tag, false);//是否登入成功
                SharePreferenceUtil.put(GuideActivity.this, Constants.SP_IS_FIRST_IN, false);
                SharePreferenceUtil.put(GuideActivity.this, Constants.Sp_UserAgreement_Tag, true);
                finish();
            } else {
                SharePreferenceUtil.put(GuideActivity.this, Constants.SP_IS_FIRST_IN, false);   //false 不是第一次登入了
                SharePreferenceUtil.put(GuideActivity.this, Constants.Is_Logined, false);
                MainActivity.start(getContext());
                mmkv.encode(Constants.KEY_Login_Tag, true);//是否登入成功
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.unregisterOnPageChangeCallback(mCallback);
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }

    private final ViewPager2.OnPageChangeCallback mCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mViewPager.getCurrentItem() != mAdapter.getCount() - 1 || positionOffsetPixels <= 0) {
                return;
            }

            mIndicatorView.setVisibility(View.VISIBLE);
            mCompleteView.setVisibility(View.INVISIBLE);
            mCompleteView.clearAnimation();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state != ViewPager2.SCROLL_STATE_IDLE) {
                return;
            }

            boolean lastItem = mViewPager.getCurrentItem() == mAdapter.getCount() - 1;
            mIndicatorView.setVisibility(lastItem ? View.INVISIBLE : View.VISIBLE);
            mCompleteView.setVisibility(lastItem ? View.VISIBLE : View.INVISIBLE);
            if (lastItem) {
                // 按钮呼吸动效
                ScaleAnimation animation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(350);
                animation.setRepeatMode(Animation.REVERSE);
                animation.setRepeatCount(Animation.INFINITE);
                mCompleteView.startAnimation(animation);
            }
        }
    };
}