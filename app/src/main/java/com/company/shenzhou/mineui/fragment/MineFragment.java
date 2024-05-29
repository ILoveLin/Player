package com.company.shenzhou.mineui.fragment;

import static com.company.shenzhou.other.AppConfig.getVersionName;

import android.content.Intent;
import android.view.View;

import com.company.shenzhou.R;
import com.company.shenzhou.aop.SingleClick;
import com.company.shenzhou.app.TitleBarFragment;
import com.company.shenzhou.bean.dbbean.UserDBBean;
import com.company.shenzhou.bean.dbbean.UserDBRememberBean;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.mineui.MainActivity;
import com.company.shenzhou.mineui.activity.BrowserActivity;
import com.company.shenzhou.mineui.activity.How2UseActivity;
import com.company.shenzhou.mineui.activity.LoginActivity;
import com.company.shenzhou.mineui.activity.PowerExplainActivity;
import com.company.shenzhou.mineui.dialog.Input2PasswordDialog;
import com.company.shenzhou.mineui.dialog.MessageAboutDialog;
import com.company.shenzhou.playerdb.manager.UserDBBeanUtils;
import com.company.shenzhou.ui.dialog.InputDialog;
import com.company.shenzhou.ui.dialog.MessageDialog;
import com.company.shenzhou.utlis.FileUtil;
import com.company.shenzhou.utlis.LogUtils;
import com.company.shenzhou.utlis.SharePreferenceUtil;
import com.hjq.base.BaseDialog;
import com.hjq.widget.layout.SettingBar;
import com.tencent.mmkv.MMKV;

import java.util.Calendar;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : 可我的界面
 */
public final class MineFragment extends TitleBarFragment<MainActivity> {

    private SettingBar mLoginUse;
    private SettingBar mLoginUseLevel;
    private SettingBar mSpaceSize;
    private MMKV mmkv;

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mine_my_fragment;
    }


    @Override
    protected void initView() {
        //登录的用户
        mLoginUse = findViewById(R.id.bar_mine_username);
        //用户权限等级
        mLoginUseLevel = findViewById(R.id.bar_mine_power_level);
        //可用空间
        mSpaceSize = findViewById(R.id.bar_mine_use_pace);
        //关于
        setOnClickListener(R.id.bar_mine_about, R.id.bar_mine_power_explain, R.id.bar_mine_how_use, R.id.bar_mine_use,
                R.id.bar_mine_secret, R.id.bar_mine_mic_name, R.id.bar_mine_change_password, R.id.bar_mine_exit);


    }

    @Override
    protected void initData() {
        mmkv = MMKV.defaultMMKV();
        String username = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Username, "");
        //0普通  1权限  2超级用户
        int userType = (int) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_UserType, 2);
        String romAvailableSize = FileUtil.getROMAvailableSize(getActivity());
        String romTotalSize = FileUtil.getROMTotalSize(getActivity());
        LogUtils.e("总空间==" + romTotalSize);
        LogUtils.e("可用空间==" + romAvailableSize);
        mSpaceSize.setRightText(romAvailableSize);
        mLoginUse.setRightText(username);
        switch (userType) {
            case Constants.GeneralUser:
                mLoginUseLevel.setRightText(getResources().getString(R.string.mine_nor_user));
                break;
            case Constants.PermissionUser:
                mLoginUseLevel.setRightText(getResources().getString(R.string.mine_permissions));
                break;
            case Constants.AdminUser:
                mLoginUseLevel.setRightText(getResources().getString(R.string.mine_super_user));
                break;
        }

    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        //关于
        if (viewId == R.id.bar_mine_about) {
            showAboutDialog();
            //权限说明
        } else if (viewId == R.id.bar_mine_power_explain) {
            startActivity(PowerExplainActivity.class);
            //操作手册
        } else if (viewId == R.id.bar_mine_how_use) {
            startActivity(How2UseActivity.class);
            //用户协议
        } else if (viewId == R.id.bar_mine_use) {
            BrowserActivity.start(getActivity(), "http://www.szcme.com/EMAIL/NOTICE-USER.HTML");
            //隐私条款
        } else if (viewId == R.id.bar_mine_secret) {
            //隐私条款
            BrowserActivity.start(getActivity(), "http://www.szcme.com/EMAIL/NOTICE-b.HTML");
            //修改昵称
        } else if (viewId == R.id.bar_mine_mic_name) {
            updateNiceName();
            //修改密码
        } else if (viewId == R.id.bar_mine_change_password) {
            updatePassword();
            //退出登录
        } else if (viewId == R.id.bar_mine_exit) {
            ExitAppDialog();

        }
    }

    private void ExitAppDialog() {
        // 消息对话框
        new MessageDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.mine_exit_title))
                // 内容必须要填写
                .setMessage(getResources().getString(R.string.mine_exit_now))
                // 确定按钮文本
                .setConfirm(getResources().getString(R.string.mine_exit_confirm))
                // 设置 null 表示不显示取消按钮
                .setCancel(getString(R.string.mine_exit_cancel))
                // 设置点击按钮后不关闭对话框
                .setCanceledOnTouchOutside(false)
                .setListener(new MessageDialog.OnListener() {

                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        SharePreferenceUtil.put(getAttachActivity(), Constants.Is_Logined, false);
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        String name = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_Username, "");
                        String password = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_Password, "");
                        MMKV mmkv = MMKV.defaultMMKV();
                        mmkv.encode(Constants.KEY_Login_Tag, false);
                        mmkv.encode(Constants.KEY_Exit_Name, name);
                        LogUtils.e("TAG====退出==username==" + name + "==password==" + password);

                    }

                })
                .show();

    }

    private void updatePassword() {
        String mCurrentUsername = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_Username, "");
        LogUtils.e("TAG==current系统用户==" + mCurrentUsername);
        int mCurrenType = (int) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_UserType, 0);
        UserDBBean mBean = UserDBBeanUtils.queryListByMessageToGetPassword(mCurrentUsername);
        LogUtils.e("TAG==Username==" + mBean.getUsername() + "====password==" + mBean.getPassword() +
                "====Type==" + mBean.getUserRole() + "====mBean.getId()==" + mBean.getId());
        // 输入对话框
        new Input2PasswordDialog.Builder(getActivity())
                // 标题可以不用填写
                .setTitle(getResources().getString(R.string.mine_change_password))
                // 确定按钮文本
                .setConfirm(getString(R.string.common_confirm))
                // 设置 null 表示不显示取消按钮
                .setCancel(getString(R.string.common_cancel))
                .setListener(new Input2PasswordDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String oldpassword, String newpassword) {
                        //对DB做修改或者增加的操作
                        String dbPassword = mBean.getPassword();
                        if (oldpassword.equals(dbPassword)) {  //输入的旧密码和DB中密码相同
                            LogUtils.e("TAG==添加前mCurrentUsername==" + mCurrentUsername + "====添加前oldpassword==" + oldpassword +
                                    "====mCurrenType==" + mCurrenType + "====mBean.getId()==" + mBean.getId());
                            SharePreferenceUtil.put(getAttachActivity(), SharePreferenceUtil.Current_Username, mCurrentUsername);
                            SharePreferenceUtil.put(getAttachActivity(), SharePreferenceUtil.Current_Password, newpassword);
                            SharePreferenceUtil.put(getAttachActivity(), SharePreferenceUtil.Current_ID, mBean.getId() );
                            UserDBBean userDBBean = new UserDBBean();
                            userDBBean.setUsername(mCurrentUsername);
                            userDBBean.setPassword(newpassword);
                            userDBBean.setUserRole(mCurrenType);
                            userDBBean.setId(mBean.getId());
                            //添加失败
                            UserDBBeanUtils.updateData(userDBBean);
                            UserDBBean mBean = UserDBBeanUtils.queryListByMessageToGetPassword(mCurrentUsername);
                            LogUtils.e("TAG=修改DB后这个用户的=Username==" + mBean.getUsername() + "====password==" + mBean.getPassword()
                                    + "====Role==" + mBean.getUserRole() + "====mBean.getId()==" + mBean.getId());
                            toast(getResources().getString(R.string.mine_toast03));
                        } else {
                            toast(getResources().getString(R.string.mine_toast04));
                        }
                    }

                })
                .show();

    }

    private void updateNiceName() {
        String mMicName = mmkv.decodeString(Constants.KEY_MIC_Name, "");

        // 输入对话框
        InputDialog.Builder builder = new InputDialog.Builder(getActivity());
        // 标题可以不用填写
        if ("".equals(mMicName)) {
            builder.setHint(getResources().getString(R.string.device_dialog_mic_name_hint));

        } else {
            builder.setHint(getResources().getString(R.string.mine_current_nickname) + mMicName);

        }
        builder.setTitle(getResources().getString(R.string.device_dialog_mic_name_title))
                // 提示可以不用填写
                // 确定按钮文本
                .setConfirm(getString(R.string.common_confirm))
                // 设置 null 表示不显示取消按钮
                .setCancel(getString(R.string.common_cancel))
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                        mmkv = MMKV.defaultMMKV();
                        mmkv.encode(Constants.KEY_MIC_Name, content);

                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                })
                .show();


    }


    private void showAboutDialog() {
        String showCopyrightYear = "";
        String versionName = getVersionName();
        int year = Calendar.getInstance().get(Calendar.YEAR);

        if ("2020".equals(year + "")) {
            showCopyrightYear = "2020";
        } else {
            showCopyrightYear = "2020" + "-" + year;
        }
        String version = getResources().getString(R.string.mine_version);
        String copyright = getResources().getString(R.string.mine_property_in_copyright);
        String update = getResources().getString(R.string.mine_updated_date);
        new MessageAboutDialog.Builder(getActivity())
                .setVersion(version + versionName)
                .setCopyright(copyright + showCopyrightYear)
                .setUpdateDate(update + " 2024 " + getResources().getString(R.string.mine_updated_year) + "5" + getResources().getString(R.string.mine_updated_month))
                .setConfirm(getResources().getString(R.string.common_confirm))
                .show();


    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}