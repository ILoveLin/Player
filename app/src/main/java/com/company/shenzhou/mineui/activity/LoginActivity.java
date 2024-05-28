package com.company.shenzhou.mineui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.company.shenzhou.R;
import com.company.shenzhou.aop.SingleClick;
import com.company.shenzhou.app.AppActivity;
import com.company.shenzhou.bean.dbbean.UserDBBean;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.mineui.MainActivity;
import com.company.shenzhou.mineui.popup.LoginListPopup;
import com.company.shenzhou.other.KeyboardWatcher;
import com.company.shenzhou.playerdb.manager.UserDBBeanUtils;
import com.company.shenzhou.utlis.CommonUtil;
import com.company.shenzhou.utlis.LogUtils;
import com.company.shenzhou.utlis.ScreenSizeUtil;
import com.company.shenzhou.utlis.SharePreferenceUtil;
import com.hjq.base.action.AnimAction;
import com.hjq.widget.view.PasswordEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : 登入界面
 */
public final class LoginActivity extends AppActivity implements KeyboardWatcher.SoftKeyboardStateListener {
    private static final String TAG = "登入，界面==";
    private ImageView mLogoView;
    private ImageButton username_right;
    private LinearLayout mBodyLayout;
    private LinearLayout linear_top;
    private EditText mPhoneView;
    private PasswordEditText mPasswordView;
    private CheckBox mCheckboxRememberPrivacy;
    private CheckBox mCheckBoxRememberPasswor;
    private View mBlankView;
    private int mPhoneViewWidth;
    private final float mLogoScale = 0.8f;   //缩放比例
    private final int mAnimTime = 300;      //动画时间
    private static final int ChooseUser = 100;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == ChooseUser) {  //点击用户列表之后的操作
                LogUtils.e(TAG + "=====ChooseUser--是否存在=====" + UserDBBeanUtils.queryListIsExist((String) msg.obj));
                if (UserDBBeanUtils.queryListIsExist((String) msg.obj)) {
                    username_right.setTag("close");
                    username_right.setImageResource(R.drawable.login_icon_down);
                    if (UserDBBeanUtils.queryListIsExist((String) msg.obj)) {
                        UserDBBean bean = UserDBBeanUtils.queryListByName((String) msg.obj);
                        username_right.setTag("close");
                        username_right.setImageResource(R.drawable.login_icon_down);
                        if (bean.rememberPassword) {
                            mCheckBoxRememberPasswor.setChecked(true);
                            mPhoneView.setText(bean.getUsername());
                            mPasswordView.setText(bean.getPassword());
                        } else {
                            mPhoneView.setText(bean.getUsername());
                            mPasswordView.setText("");
                            mCheckBoxRememberPasswor.setChecked(false);
                        }
                    }
                }
            }
        }
    };
    private String username;
    private String password;

    @Override
    protected int getLayoutId() {
        return R.layout.login_activity;
    }

    @Override
    protected void initView() {
        mLogoView = findViewById(R.id.iv_login_logo);
        username_right = findViewById(R.id.username_right);
        mBodyLayout = findViewById(R.id.ll_login_body);
        linear_top = findViewById(R.id.linear_top);
        mPhoneView = findViewById(R.id.et_login_phone);
        mPasswordView = findViewById(R.id.et_login_password);
        mCheckboxRememberPrivacy = findViewById(R.id.checkbox_privacy);
        mCheckBoxRememberPasswor = findViewById(R.id.remember_password_checkbox);
        mBlankView = findViewById(R.id.v_login_blank);
        mPhoneView.getViewTreeObserver().addOnDrawListener(() -> mPhoneViewWidth = mPhoneView.getWidth());
        username_right.setImageResource(R.drawable.login_icon_down);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        //没有创建admin用户,才创建超级用户，并且创建之后设置账号密码
        checkAdminIsEmptyOrCreated();
        //登入界面动画适配
        post(() -> {
            // 因为在小屏幕手机上面，因为计算规则的因素会导致动画效果特别夸张，所以不在小屏幕手机上面展示这个动画效果
            if (mBlankView.getHeight() > mBodyLayout.getHeight()) {
                // 只有空白区域的高度大于登录框区域的高度才展示动画
                KeyboardWatcher.with(LoginActivity.this)
                        .setListener(LoginActivity.this);
            }
        });
        //初始化设置用户账号密码
        initCurrentUserData();
        //适配登入界面layout
        switchLoginBtnLayout();
        setOnClickListener(R.id.et_login_phone, R.id.username_right, R.id.checkbox_privacy, R.id.tv_privacy_desc1, R.id.tv_privacy_desc2, R.id.remember_password_checkbox, R.id.btn_login_commit);

    }

    @SuppressLint("NonConstantResourceId")
    @SingleClick
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_login_phone://弹出用户列表
            case R.id.username_right://弹出用户列表
                showUserListDialog();
                break;
            case R.id.checkbox_privacy://是否阅读了两个协议
                mCheckboxRememberPrivacy.setChecked(mCheckboxRememberPrivacy.isChecked());
                break;
            case R.id.tv_privacy_desc1://用户协议
                BrowserActivity.start(LoginActivity.this, "http://www.szcme.com/EMAIL/NOTICE-USER.HTML");
                break;
            case R.id.tv_privacy_desc2://隐私政策
                BrowserActivity.start(LoginActivity.this, "http://www.szcme.com/EMAIL/NOTICE-b.HTML");
            case R.id.checkbox://记住密码
                mCheckBoxRememberPasswor.setChecked(mCheckBoxRememberPasswor.isChecked());
                break;
            case R.id.btn_login_commit:  //登入
                checkData2Login();
                break;
            default:
                break;
        }
    }

    private void checkData2Login() {
        username = mPhoneView.getText().toString().trim();
        password = Objects.requireNonNull(mPasswordView.getText()).toString().trim();
        if ("".equals(username) && TextUtils.isEmpty(username)) {
            toast(getResources().getString(R.string.login_username_not_null));
            return;
        }
        if (!mCheckboxRememberPrivacy.isChecked()) {
            toast(getResources().getString(R.string.login_chose_agreement));
            return;
        }

        checkDBDataToChangeCurrentUserMsg();

    }

    /**
     * 查询数据库然后更新当前用户信息
     */
    private void checkDBDataToChangeCurrentUserMsg() {
        boolean isExist = UserDBBeanUtils.queryListIsExist(username);
        LogUtils.e(TAG + "==isExist==" + isExist);
        if (isExist) {  //存在
            UserDBBean userRememberBean = UserDBBeanUtils.queryListByMessageToGetPassword(username);
            String dbUsername = userRememberBean.getUsername().trim();
            String dbPassword = userRememberBean.getPassword().trim();
            int dbUsertype = userRememberBean.getUserRole();
            Long id = userRememberBean.getId();
            LogUtils.e(TAG + "==username==" + dbUsername + "==password==" + dbPassword + "==usertype==" + dbUsertype + "==id==" + id);
            if (password.equals(dbPassword)) {  //判断数据库密码和输入密码是否一致,之后更新SP的当前用户信息
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Username, dbUsername);
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Password, dbPassword);
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_UserType, dbUsertype);
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_ID, id);
                SharePreferenceUtil.put(LoginActivity.this, Constants.SP_IS_FIRST_IN, false);   //false 不是第一次登入了
                SharePreferenceUtil.put(LoginActivity.this, Constants.Is_Logined, true);   //false  登录的标志 true表示登录了
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_ToastShow, "Yes");  //为了解决第一次登入toast的bug
                mmkv.encode(Constants.KEY_Login_Tag, true);//是否登入成功
                String currentPhone = mPhoneView.getText().toString();
                String currentPassword = Objects.requireNonNull(mPasswordView.getText()).toString();
                //提交数据的时候,对当前用户是否记住密码更新DB
                boolean Exist = UserDBBeanUtils.queryListIsExist(currentPhone);
                LogUtils.e(TAG + "======db==username==" + dbUsername);
                LogUtils.e(TAG + "======db==password==" + dbPassword);
                if (mCheckBoxRememberPasswor.isChecked()) {
                    if (!currentPassword.isEmpty()) {  //密码不为空的时候才做操作
                        if (Exist) { //存在
                            UserDBBean userDBBean = UserDBBeanUtils.queryListByName(currentPhone);
                            userDBBean.setId(userDBBean.getId());
                            userDBBean.setRememberPassword(true);
                            UserDBBeanUtils.updateData(userDBBean);
                        }
                    }
                } else {
                    if (!currentPassword.isEmpty()) {  //密码不为空的时候才做操作
                        if (Exist) { //存在
                            UserDBBean userDBBean = UserDBBeanUtils.queryListByName(currentPhone);
                            userDBBean.setId(userDBBean.getId());
                            userDBBean.setRememberPassword(false);
                            UserDBBeanUtils.updateData(userDBBean);
                        }
                    }
                }
                if (mCheckboxRememberPrivacy.isChecked()) {
                    if (Exist) { //存在
                        UserDBBean userDBBean = UserDBBeanUtils.queryListByName(currentPhone);
                        userDBBean.setId(userDBBean.getId());
                        userDBBean.setRememberPrivacy(true);
                        UserDBBeanUtils.updateData(userDBBean);
                    }
                }

                //进入主界面
                MainActivity.start(LoginActivity.this);
                finish();
            } else {
                toast(getResources().getString(R.string.login_password_error));
            }
        } else {
            toast(getResources().getString(R.string.login_accort_no_exit));
        }
    }

    /**
     * 显示用户列表
     */
    private void showUserListDialog() {
        List<UserDBBean> list = UserDBBeanUtils.queryAll(UserDBBean.class);
        ArrayList<String> nameList = CommonUtil.getNameList(list);
        LogUtils.e(TAG + "======Tag==list==" + list.size());
        LogUtils.e(TAG + "======Tag==" + username_right.getTag());
        if ("close".equals(username_right.getTag())) {
            username_right.setTag("open");
            username_right.setImageResource(R.drawable.login_icon_up);

        } else {
            username_right.setTag("close");
            username_right.setImageResource(R.drawable.login_icon_down);
        }
        LoginListPopup.Builder historyBuilder = new LoginListPopup.Builder(LoginActivity.this);
        historyBuilder.setList(nameList)
                .setGravity(Gravity.CENTER)
                .setAutoDismiss(true)
                .setOutsideTouchable(false)
                .setWidth(mPhoneViewWidth + 60)
                .setXOffset(-30)
                .setHeight(650)
                .setAnimStyle(AnimAction.ANIM_SCALE)
                .setListener((LoginListPopup.OnListener<String>) (popupWindow, position, str) -> {
                    Message tempMsg = mHandler.obtainMessage();
                    tempMsg.what = ChooseUser;
                    tempMsg.obj = str;
                    mHandler.sendMessage(tempMsg);

                })
                .showAsDropDown(mPhoneView);
        if (historyBuilder.getPopupWindow() != null) {
            historyBuilder.getPopupWindow().addOnDismissListener(popupWindow -> {
                username_right.setTag("close");
                username_right.setImageResource(R.drawable.login_icon_down);
            });
        }


    }


    //没有创建admin用户,才创建超级用户，并且创建之后设置账号密码
    private void checkAdminIsEmptyOrCreated() {
        boolean flag = UserDBBeanUtils.queryAdminIsExist();
        //数据库不存在，就创建admin
        if (!flag) {
            //存入数据库
            long ID = 1;
            UserDBBean userDBBean = new UserDBBean();
            userDBBean.setUsername("admin");
            userDBBean.setPassword("admin");
            userDBBean.setTag("admin");
            userDBBean.setUserRole(2);
            userDBBean.setRememberPrivacy(false);
            userDBBean.setRememberPassword(false);
            userDBBean.setId(ID);
            UserDBBeanUtils.insertOrReplaceData(userDBBean);
            String str = "admin";
            List<UserDBBean> userDBBeans = UserDBBeanUtils.queryListByMessage(str);
            for (int i = 0; i < userDBBeans.size(); i++) {
                String username = userDBBeans.get(i).getUsername();
                String password = userDBBeans.get(i).getPassword();
                LogUtils.e(TAG + "=====username==" + username + "==password==" + password);
            }
            SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Username, "admin");
            SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Password, "admin");
            SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_UserType, 2);
            SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_ID, 1);
            //超级用户只能修改一次密码
            SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Admin_ChangePassword, false);
        }


    }

    /**
     * 初始化设置用户账号密码
     */
    @SuppressLint("SetTextI18n")
    private void initCurrentUserData() {
        //获取上一次退出用户的username,如果为空默认选择 admin 用户名
        String name = mmkv.decodeString(Constants.KEY_Exit_Name, "");
        String index = TextUtils.isEmpty(name) ? "admin" : name;
        UserDBBean mBean = UserDBBeanUtils.queryListByMessageToGetPassword(index);
        LogUtils.e(TAG + "=====当前登入的用户==bean==" + mBean.toString());
        if (mBean.rememberPassword) {
            //记住密码直接sp里面取,注意更改当前用户信息,需要实时更新Sp
            mPhoneView.setText(mBean.getUsername());
            mPasswordView.setText(mBean.getPassword());
            mCheckBoxRememberPasswor.setChecked(true);
        } else {
            mPhoneView.setText(mBean.getUsername());
            mPasswordView.setText("");
            mCheckBoxRememberPasswor.setChecked(false);
        }
        //设置是否勾选用户协议和隐私政策
        mCheckboxRememberPrivacy.setChecked(mBean.rememberPrivacy);

    }

    /**
     * 适配登入界面layout
     */

    private void switchLoginBtnLayout() {
        int screenWidth = ScreenSizeUtil.getScreenWidth(this);
        int Height = ScreenSizeUtil.getScreenHeight(this);
        LogUtils.e(TAG + "===screenWidth=======宽=========" + screenWidth + ";====高==" + Height);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams mImageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (screenWidth == 1080) {
            mParams.topMargin = 0;
            LogUtils.e(TAG + "==========screenWidth=========" + screenWidth);
            mImageParams.topMargin = 80;
            mBodyLayout.setLayoutParams(mParams);
            linear_top.setLayoutParams(mImageParams);
        }

    }

    /**
     * 实现方法
     *
     * @param keyboardHeight 软键盘高度
     */
    @Override
    public void onSoftKeyboardOpened(int keyboardHeight) {
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int[] location = new int[2];
        // 获取这个 View 在屏幕中的坐标（左上角）
        mBodyLayout.getLocationOnScreen(location);
        //int x = location[0];
        int y = location[1];
        int bottom = screenHeight - (y + mBodyLayout.getHeight());
        if (keyboardHeight > bottom) {
            // 执行位移动画
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBodyLayout, "translationY", 0, -(keyboardHeight - bottom));
            objectAnimator.setDuration(mAnimTime);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator.start();
            // 执行缩小动画
            mLogoView.setPivotX(mLogoView.getWidth() / 2f);
            mLogoView.setPivotY(mLogoView.getHeight());
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mLogoView, "scaleX", 1.0f, mLogoScale);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mLogoView, "scaleY", 1.0f, mLogoScale);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(mLogoView, "translationY", 0.0f, -(keyboardHeight - bottom));
            animatorSet.play(translationY).with(scaleX).with(scaleY);
            animatorSet.setDuration(mAnimTime);
            animatorSet.start();
        }
    }

    @Override
    public void onSoftKeyboardClosed() {
        // 执行位移动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBodyLayout, "translationY", mBodyLayout.getTranslationY(), 0);
        objectAnimator.setDuration(mAnimTime);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
        if (mLogoView.getTranslationY() == 0) {
            return;
        }
        // 执行放大动画
        mLogoView.setPivotX(mLogoView.getWidth() / 2f);
        mLogoView.setPivotY(mLogoView.getHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mLogoView, "scaleX", mLogoScale, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mLogoView, "scaleY", mLogoScale, 1.0f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mLogoView, "translationY", mLogoView.getTranslationY(), 0);
        animatorSet.play(translationY).with(scaleX).with(scaleY);
        animatorSet.setDuration(mAnimTime);
        animatorSet.start();
    }

}