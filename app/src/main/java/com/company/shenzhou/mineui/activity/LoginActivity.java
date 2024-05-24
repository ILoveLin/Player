package com.company.shenzhou.mineui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.company.shenzhou.R;
import com.company.shenzhou.aop.SingleClick;
import com.company.shenzhou.app.AppActivity;
import com.company.shenzhou.bean.dbbean.UserDBRememberBean;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.mineui.MainActivity;
import com.company.shenzhou.mineui.popup.LoginListPopup;
import com.company.shenzhou.other.KeyboardWatcher;
import com.company.shenzhou.playerdb.manager.UserDBRememberBeanUtils;
import com.company.shenzhou.utlis.CommonUtil;
import com.company.shenzhou.utlis.LogUtils;
import com.company.shenzhou.utlis.ScreenSizeUtil;
import com.company.shenzhou.utlis.SharePreferenceUtil;
import com.hjq.base.BasePopupWindow;
import com.hjq.base.action.AnimAction;
import com.hjq.widget.view.PasswordEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 登入界面
 */
public final class LoginActivity extends AppActivity implements KeyboardWatcher.SoftKeyboardStateListener {
    private static final String TAG = "登入，界面==";
    private boolean isRemember;
    private ImageView mLogoView;
    private ImageButton username_right;
    private LinearLayout mBodyLayout;
    private LinearLayout linear_top;
    private RelativeLayout relative_username_00;
    private RelativeLayout relative_root;
    private EditText mPhoneView;
    private PasswordEditText mPasswordView;
    private CheckBox mCheckboxPrivacy;
    private CheckBox checkbox;
    private Button mCommitView;
    private View mBlankView;
    private LinearLayout linear_login_root;
    private int mPhoneViewWidth;
    private final float mLogoScale = 0.8f;   //缩放比例
    private final int mAnimTime = 300;      //动画时间
    private static final int ChooseUser = 100;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ChooseUser:  //点击用户列表之后的操作
                    LogUtils.e(TAG + "=====ChooseUser--是否存在=====" + UserDBRememberBeanUtils.queryListIsExist((String) msg.obj));
                    if (UserDBRememberBeanUtils.queryListIsExist((String) msg.obj)) {
                        UserDBRememberBean userDBRememberBean = UserDBRememberBeanUtils.queryListByName((String) msg.obj);
                        username_right.setTag("close");
                        username_right.setImageResource(R.drawable.login_icon_down);
                        if ("Yes".equals(userDBRememberBean.getRemember())) {
                            checkbox.setChecked(true);
                            mPhoneView.setText("" + userDBRememberBean.getUsername());
                            mPasswordView.setText("" + userDBRememberBean.getPassword());
                        } else {
                            mPhoneView.setText("" + userDBRememberBean.getUsername());
                            mPasswordView.setText("");
                            checkbox.setChecked(false);
                        }
                    }
                    break;
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
        relative_username_00 = findViewById(R.id.relative_username_00);
        relative_root = findViewById(R.id.relative_root);
        mPhoneView = findViewById(R.id.et_login_phone);
        mPasswordView = findViewById(R.id.et_login_password);
        mCheckboxPrivacy = findViewById(R.id.checkbox_privacy);
        checkbox = findViewById(R.id.checkbox);
        mCommitView = findViewById(R.id.btn_login_commit);
        mBlankView = findViewById(R.id.v_login_blank);
        linear_login_root = findViewById(R.id.linear_login_root);


        mPhoneView.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                mPhoneViewWidth = mPhoneView.getWidth();

            }
        });


    }

    @Override
    protected void initData() {
        isRemember = (boolean) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_RememberPassword, false);
        //没有创建admin用户,才创建超级用户，并且创建之后设置账号密码
        checkAdminIsEmptyOrCreated();
        //登入界面动画适配
        post(new Runnable() {
            @Override
            public void run() {
                // 因为在小屏幕手机上面，因为计算规则的因素会导致动画效果特别夸张，所以不在小屏幕手机上面展示这个动画效果
                if (mBlankView.getHeight() > mBodyLayout.getHeight()) {
                    // 只有空白区域的高度大于登录框区域的高度才展示动画
                    KeyboardWatcher.with(LoginActivity.this)
                            .setListener(LoginActivity.this);
                }
            }
        });


        //初始化设置用户账号密码
        initDefaultUserData();

        //设置默认admin账号的用户名
        mPhoneView.setText("admin");
        mPasswordView.setText("");

        int i = ScreenSizeUtil.dp2px(this, 80);
        int screenWidth = ScreenSizeUtil.getScreenWidth(this) - ScreenSizeUtil.dp2px(this, 80);
        LogUtils.e(TAG + "==========i=========" + i);
        LogUtils.e(TAG + "==========screenWidth=========" + screenWidth);
        //显示用户列表
        showUserListDialog();
        //适配登入界面layout
        switchLoginBtnLayout();

        setOnClickListener(R.id.checkbox_privacy, R.id.tv_privacy_desc1, R.id.tv_privacy_desc2, R.id.checkbox, R.id.btn_login_commit);


    }

    @SingleClick
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checkbox_privacy://是否阅读了两个协议
                if (mCheckboxPrivacy.isChecked()) {
                    mCheckboxPrivacy.setChecked(true);
                } else {
                    mCheckboxPrivacy.setChecked(false);
                }
                break;
            case R.id.tv_privacy_desc1://用户协议
                BrowserActivity.start(LoginActivity.this, "http://www.szcme.com/EMAIL/NOTICE-USER.HTML");
                break;
            case R.id.tv_privacy_desc2://隐私政策
                BrowserActivity.start(LoginActivity.this, "http://www.szcme.com/EMAIL/NOTICE-b.HTML");
            case R.id.checkbox://记住密码
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_RememberPassword, checkbox.isChecked());  //是否记住密码
                if (checkbox.isChecked()) {
                    checkbox.setChecked(true);
                } else {
                    checkbox.setChecked(false);
                }
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
        password = mPasswordView.getText().toString().trim();
        if ("".equals(username) && TextUtils.isEmpty(username)) {
            toast(getResources().getString(R.string.login_username_not_null));
            return;
        }
        if (!mCheckboxPrivacy.isChecked()) {
            toast(getResources().getString(R.string.login_chose_agreement));
            return;
        }

        checkDBDataToChangeCurrentUserMsg();

    }

    /**
     * 查询数据库然后更新当前用户信息
     */
    private void checkDBDataToChangeCurrentUserMsg() {
        boolean isExist = UserDBRememberBeanUtils.queryListIsExist(username);
        LogUtils.e(TAG + "==isExist==" + isExist);
        if (isExist) {  //存在
//            List<UserDBRememberBean> mList = UserDBRememberBeanUtils.queryListByMessage(username);
//            UserDBRememberBean
            UserDBRememberBean userRememberBean = UserDBRememberBeanUtils.queryListByMessageToGetPassword(username);
            String dbusername = userRememberBean.getUsername().toString().trim();
            String dbpassword = userRememberBean.getPassword().toString().trim();
            int dbusertype = userRememberBean.getUserType();
            Long id = userRememberBean.getId();
            LogUtils.e(TAG + "==username==" + dbusername + "==password==" + dbpassword + "==usertype==" + dbusertype + "==id==" + id);
            if (password.equals(dbpassword)) {  //判断数据库密码和输入密码是否一致,之后更新SP的当前用户信息
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_RememberPassword, checkbox.isChecked());  //是否记住密码
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Username, dbusername);
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Password, dbpassword);
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_UserType, dbusertype);
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_ID, id);
                SharePreferenceUtil.put(LoginActivity.this, Constants.SP_IS_FIRST_IN, false);   //false 不是第一次登入了
                SharePreferenceUtil.put(LoginActivity.this, Constants.Is_Logined, true);   //false  登录的标志 true表示登录了
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_ToastShow, "Yes");  //为了解决第一次登入toast的bug
                mmkv.encode(Constants.KEY_Login_Tag, true);//是否登入成功

                String name = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_Username, "");
                String pass = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_Password, "");
                int type = (int) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_UserType, 0);

                String currentPhone = mPhoneView.getText().toString();
                String currentPassword = mPasswordView.getText().toString();
                //提交数据的时候,对当前用户是否记住密码更新DB
                boolean isExistt = UserDBRememberBeanUtils.queryListIsExist(currentPhone);
                if (checkbox.isChecked()) {
                    SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Username, dbusername);
                    SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Password, dbpassword);
                    LogUtils.e(TAG + "======db==username==" + dbusername);
                    LogUtils.e(TAG + "======db==password==" + dbpassword);
                    if (!"".equals(currentPassword)) {  //密码不为空的时候才做操作
                        if (isExistt) { //存在
                            UserDBRememberBean userDBRememberBean = UserDBRememberBeanUtils.queryListByName(currentPhone);
                            userDBRememberBean.setId(userDBRememberBean.getId());
                            userDBRememberBean.setRemember("Yes");
                            UserDBRememberBeanUtils.updateData(userDBRememberBean);
                        }
                    }
                } else {
                    if (!"".equals(currentPassword)) {  //密码不为空的时候才做操作
                        if (isExistt) { //存在
                            UserDBRememberBean userDBRememberBean = UserDBRememberBeanUtils.queryListByName(currentPhone);
                            userDBRememberBean.setId(userDBRememberBean.getId());
                            userDBRememberBean.setRemember("No");
                            UserDBRememberBeanUtils.updateData(userDBRememberBean);
                        }
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
        List<UserDBRememberBean> list = UserDBRememberBeanUtils.queryAll(UserDBRememberBean.class);
        LogUtils.e(TAG + "==========数据库======list==" + list.size());
        ArrayList<String> nameList = CommonUtil.getNameList(list);

        username_right.setImageResource(R.drawable.login_icon_down);
        username_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                historyBuilder.getPopupWindow().addOnDismissListener(new BasePopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss(BasePopupWindow popupWindow) {
                        username_right.setTag("close");
                        username_right.setImageResource(R.drawable.login_icon_down);
                    }
                });

            }
        });
    }


    //没有创建admin用户,才创建超级用户，并且创建之后设置账号密码
    private void checkAdminIsEmptyOrCreated() {
        boolean flag = UserDBRememberBeanUtils.queryAdminIsExist();
        LogUtils.e(TAG + "=====isExist==" + flag);
        //数据库不存在，就创建admin
        if (!flag) {
            //存入数据库
            long ID = 1;
            UserDBRememberBean userDBBean = new UserDBRememberBean();
            userDBBean.setUsername("admin");
            userDBBean.setPassword("admin");
            userDBBean.setTag("admin");
            userDBBean.setUserType(2);
            userDBBean.setId(ID);
            UserDBRememberBeanUtils.insertOrReplaceData(userDBBean);
            LogUtils.e(TAG + "=====isExist==" + flag);
            String str = "admin";
            List<UserDBRememberBean> userDBRememberBeans = UserDBRememberBeanUtils.queryListByMessage(str);
            for (int i = 0; i < userDBRememberBeans.size(); i++) {
                String username = userDBRememberBeans.get(i).getUsername();
                String password = userDBRememberBeans.get(i).getPassword();
                LogUtils.e(TAG + "=====username==" + username + "==password==" + password);
            }
            LogUtils.e(TAG + "=====isExist==" + flag);
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
    private void initDefaultUserData() {
        LogUtils.e(TAG + "=====isExist==a=" + isRemember);
        if (isRemember) {
            //记住密码直接sp里面取,注意更改当前用户信息,需要实时更新Sp
            String rememberName = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_Username, "");
            String rememberPassword = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_Password, "");
            mPhoneView.setText(rememberName);
            mPasswordView.setText(rememberPassword);
            checkbox.setChecked(true);

        } else {
            String name = mmkv.decodeString(Constants.KEY_Exit_Name, "");
            mPasswordView.setText("");
            mPhoneView.setText("" + name);
            checkbox.setChecked(false);
        }

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