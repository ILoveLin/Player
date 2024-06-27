package com.company.shenzhou.mineui.fragment;

import static com.company.shenzhou.other.AppConfig.getVersionName;

import android.content.Intent;
import android.view.View;

import com.company.shenzhou.R;
import com.company.shenzhou.aop.SingleClick;
import com.company.shenzhou.app.TitleBarFragment;
import com.company.shenzhou.bean.dbbean.UserDBBean;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.mineui.MainActivity;
import com.company.shenzhou.mineui.activity.BackstageManagerActivity;
import com.company.shenzhou.mineui.activity.BrowserActivity;
import com.company.shenzhou.mineui.activity.How2UseActivity;
import com.company.shenzhou.mineui.activity.LoginActivity;
import com.company.shenzhou.mineui.activity.PlayerLine2Activity;
import com.company.shenzhou.mineui.activity.PowerExplainActivity;
import com.company.shenzhou.mineui.activity.TestActivity;
import com.company.shenzhou.mineui.dialog.Input2PasswordDialog;
import com.company.shenzhou.mineui.dialog.AboutDialog;
import com.company.shenzhou.playerdb.manager.UserDBBeanUtils;
import com.company.shenzhou.ui.dialog.InputDialog;
import com.company.shenzhou.ui.dialog.MessageDialog;
import com.company.shenzhou.utlis.FileUtil;
import com.company.shenzhou.utlis.LogUtils;
import com.company.shenzhou.utlis.SharePreferenceUtil;
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
    private static final String TAG = "MineFragment，界面==";
    private SettingBar mLoginUseView, mLoginUseLevelView, mSpaceSizeView, mMicNameView;
    private MMKV mmkv;
    private String mLoginUserMicName;

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
        mLoginUseView = findViewById(R.id.bar_mine_username);
        //用户权限等级
        mLoginUseLevelView = findViewById(R.id.bar_mine_power_level);
        //可用空间
        mSpaceSizeView = findViewById(R.id.bar_mine_use_pace);
        //语音昵称
        mMicNameView = findViewById(R.id.bar_mine_mic_name);
        //关于
        setOnClickListener(R.id.bar_mine_about, R.id.bar_mine_power_explain, R.id.bar_mine_how_use, R.id.bar_mine_use,
                R.id.bar_mine_secret, R.id.bar_mine_mic_name, R.id.bar_backstage_manager, R.id.bar_mine_change_password, R.id.bar_mine_exit);

    }

    @Override
    protected void initData() {
        mmkv = MMKV.defaultMMKV();
        mLoginUserMicName = mmkv.decodeString(Constants.KEY_MIC_Name, "");
        String username = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_Username, "");
        //0普通  1权限  2超级用户
        int mLoginRole = (int) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_UserRole, 2);
        String romAvailableSize = FileUtil.getROMAvailableSize(getActivity());
        String romTotalSize = FileUtil.getROMTotalSize(getActivity());
        LogUtils.e(TAG + "总空间==" + romTotalSize);
        LogUtils.e(TAG + "可用空间==" + romAvailableSize);
        mSpaceSizeView.setRightText(romAvailableSize);
        mLoginUseView.setRightText(username);
        mMicNameView.setRightText(mLoginUserMicName);
        switch (mLoginRole) {
            case Constants.GeneralUser:
                mLoginUseLevelView.setRightText(getResources().getString(R.string.mine_nor_user));
                break;
            case Constants.PermissionUser:
                mLoginUseLevelView.setRightText(getResources().getString(R.string.mine_permissions));
                break;
            case Constants.AdminUser:
                mLoginUseLevelView.setRightText(getResources().getString(R.string.mine_super_user));
                break;
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        //关于
        if (viewId == R.id.bar_mine_about) {
            showAboutDialog();
            //startActivity(new Intent(getActivity(), TestActivity.class));
            startActivity(new Intent(getActivity(), PlayerLine2Activity.class));
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
            //后台管理
        } else if (viewId == R.id.bar_backstage_manager) {
            BackstageManagerDialog();
            //修改密码
        } else if (viewId == R.id.bar_mine_change_password) {
            updatePassword();
            //退出登录
        } else if (viewId == R.id.bar_mine_exit) {
            ExitAppDialog();

        }
    }

    private void BackstageManagerDialog() {
        new InputDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.back_debug_Manager))
                .setHint(getResources().getString(R.string.common_password_input_error))
                .setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setListener((dialog, content) -> {
                    if ("szcme".equals(content)) {
                        startActivity(new Intent(getActivity(), BackstageManagerActivity.class));
                    } else {
                        toast(getResources().getString(R.string.device_search_toast02));
                    }
                })
                .show();
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
                .setListener(dialog -> {
                    SharePreferenceUtil.put(getAttachActivity(), Constants.Is_LoginEd, false);
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    String name = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_Username, "");
                    String password = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_Password, "");
                    MMKV mmkv = MMKV.defaultMMKV();
                    mmkv.encode(Constants.KEY_Login_Tag, false);
                    mmkv.encode(Constants.KEY_Exit_Name, name);
                    LogUtils.e("TAG====退出==username==" + name + "==password==" + password);
                }).show();

    }

    private void updatePassword() {
        String mCurrentUsername = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_Username, "");
        int mLoginRole = (int) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_UserRole, 0);
        UserDBBean mBean = UserDBBeanUtils.queryListByMessageToGetPassword(mCurrentUsername);
        LogUtils.e("TAG==Username==" + mBean.getUsername() + "====password==" + mBean.getPassword() + "====Type==" + mBean.getUserRole() + "====mBean.getId()==" + mBean.getId());
        // 输入对话框
        new Input2PasswordDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.mine_change_password))
                .setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setListener((dialog, oldpassword, newpassword) -> {
                    //对DB做修改或者增加的操作
                    String dbPassword = mBean.getPassword();
                    if (oldpassword.equals(dbPassword)) {  //输入的旧密码和DB中密码相同
                        LogUtils.e(TAG + "==添加前的用户名==" + mCurrentUsername + "==添加前密码==" + oldpassword + "==角色等级==" + mLoginRole + "==userId==" + mBean.getId());
                        SharePreferenceUtil.put(getAttachActivity(), SharePreferenceUtil.Current_Username, mCurrentUsername);
                        SharePreferenceUtil.put(getAttachActivity(), SharePreferenceUtil.Current_Password, newpassword);
                        SharePreferenceUtil.put(getAttachActivity(), SharePreferenceUtil.Current_ID, mBean.getId());
                        UserDBBean userDBBean = new UserDBBean();
                        userDBBean.setUsername(mCurrentUsername);
                        userDBBean.setPassword(newpassword);
                        userDBBean.setUserRole(mLoginRole);
                        userDBBean.setId(mBean.getId());
                        UserDBBeanUtils.updateData(userDBBean);
                        UserDBBean updateBean = UserDBBeanUtils.queryListByMessageToGetPassword(mCurrentUsername);
                        LogUtils.e(TAG + "==修改后的用户名==" + updateBean.getUsername() + "==修改后密码==" + updateBean.getPassword() + "==修改后的角色等级==" + updateBean.getUserRole() + "==修改后的用户名==" + updateBean.getId());
                        toast(getResources().getString(R.string.mine_toast03));
                    } else {
                        toast(getResources().getString(R.string.mine_toast04));
                    }
                }).show();

    }

    private void updateNiceName() {
        // 输入对话框
        InputDialog.Builder builder = new InputDialog.Builder(getActivity());
        // 标题可以不用填写
        if ("".equals(mLoginUserMicName)) {
            builder.setHint(getResources().getString(R.string.device_dialog_mic_name_hint));

        } else {
            builder.setHint(getResources().getString(R.string.mine_current_nickname) + mLoginUserMicName);

        }
        builder.setTitle(getResources().getString(R.string.device_dialog_mic_name_title))
                .setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setListener((dialog, content) -> {
                    mmkv = MMKV.defaultMMKV();
                    mmkv.encode(Constants.KEY_MIC_Name, content);
                    mMicNameView.setRightText(content);
                }).show();


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
        new AboutDialog.Builder(getActivity())
                .setVersion(version + versionName)
                .setCopyright(copyright + showCopyrightYear)
                .setUpdateDate(update + " 2024 " + getResources().getString(R.string.mine_updated_year) + " 8 " + getResources().getString(R.string.mine_updated_month))
                .setConfirm(getResources().getString(R.string.common_confirm))
                .show();
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}