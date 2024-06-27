package com.company.shenzhou.mineui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.company.shenzhou.R;
import com.company.shenzhou.action.StatusAction;
import com.company.shenzhou.app.TitleBarFragment;
import com.company.shenzhou.bean.dbbean.UserDBBean;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.mineui.MainActivity;
import com.company.shenzhou.mineui.activity.LoginActivity;
import com.company.shenzhou.mineui.adapter.UserAdapter;
import com.company.shenzhou.mineui.dialog.Input2AddUserDialog;
import com.company.shenzhou.playerdb.manager.UserDBBeanUtils;
import com.company.shenzhou.ui.dialog.InputDialog;
import com.company.shenzhou.ui.dialog.MessageDialog;
import com.company.shenzhou.utlis.LogUtils;
import com.company.shenzhou.utlis.SharePreferenceUtil;
import com.company.shenzhou.widget.StatusLayout;
import com.company.shenzhou.widget.SwipeMenuLayout;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.hjq.widget.view.SwitchButton;

import java.util.ArrayList;
import java.util.Objects;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : 用户界面
 */
public final class UserFragment extends TitleBarFragment<MainActivity> implements StatusAction, BaseAdapter.OnChildClickListener {
    private static final String TAG = "UserFragment，界面==";
    private WrapRecyclerView mRecyclerView;
    private StatusLayout mStatusView;
    private UserAdapter mAdapter;
    private int mLoginUserRole;
    private String mLoginUsername;
    private ArrayList<UserDBBean> mDataList = new ArrayList<>();
    private TextView mUsernameView;
    private TitleBar mTitleBar;
    //刷新界面列表数据
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            showComplete();
            mAdapter.setData(mDataList);
            if (mDataList.isEmpty()) {
                showUserEmpty();
            }
        }
    };

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_fragment;
    }

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.rv_status_list);
        mStatusView = findViewById(R.id.status_layout);
        mTitleBar = findViewById(R.id.title_bar);
        mUsernameView = findViewById(R.id.tv_username);
        mAdapter = new UserAdapter(getAttachActivity());
        mAdapter.setOnChildClickListener(R.id.linear_item, this);
        mAdapter.setOnChildClickListener(R.id.slide_switch, this);
        mAdapter.setOnChildClickListener(R.id.update_password, this);
        mAdapter.setOnChildClickListener(R.id.delete, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        mLoginUserRole = (int) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_UserRole, Constants.GeneralUser);
        mLoginUsername = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_Username, "");
        LogUtils.e(TAG + "==当前登入用户权限等级：0=普通用户，1=权限用户，2=超级管理员==" + mLoginUserRole);
        LogUtils.e(TAG + "==当前登入用户名：==" + mLoginUsername);
        mUsernameView.setText(mLoginUsername);
        startThreadSetRecycleViewData();
        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
            }

            @Override
            public void onTitleClick(View view) {
            }

            @Override
            public void onRightClick(View view) {
                //普通用户
                if (mLoginUserRole == Constants.GeneralUser) {
                    toast(getResources().getString(R.string.toast_10));
                    //权限用户
                } else if (mLoginUserRole == Constants.PermissionUser) {
                    showAddUserDialog();
                    //超级管理员
                } else if (mLoginUserRole == Constants.AdminUser) {
                    showAddUserDialog();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public void onChildClick(RecyclerView recyclerView, View childView, int position) {
        View itemView = Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(position);
        int viewId = childView.getId();
        UserDBBean bean = mAdapter.getItem(position);
        int itemRole = bean.getUserRole();
        //ItemClick
        if (viewId == R.id.linear_item) {
            SwitchButton mSwitchButton = itemView.findViewById(R.id.slide_switch);
            //这么写是避免 computing a layout or scrolling  Bug
            if (mRecyclerView.isComputingLayout()) {
                mRecyclerView.post(() -> {
                    if (itemRole == Constants.GeneralUser) {
                        //这么写是避免 computing a layout or scrolling  Bug
                        mSwitchButton.setChecked(false);
                    } else {
                        mSwitchButton.setChecked(true);
                    }
                });
            } else {
                if (itemRole == Constants.GeneralUser) {
                    //这么写是避免 computing a layout or scrolling  Bug
                    mSwitchButton.setChecked(false);
                } else {
                    mSwitchButton.setChecked(true);
                }
            }
            //切换管理员和普通用户
        } else if (viewId == R.id.slide_switch) {
            //点击切换按钮，设置是否是权限用户
            SwitchButton mSwitchButton = itemView.findViewById(R.id.slide_switch);
            mSwitchButton.setOnCheckedChangeListener((button, isChecked) -> {
                //0普通用户、1权限用户、2超级管理员  默认为0-普通用户
                if (mLoginUserRole == Constants.AdminUser && itemRole < Constants.AdminUser) {  //只有超级管理员才可以设置权限
                    mSwitchButton.setEnabled(true);
                    if (isChecked) {
                        bean.setId(bean.id);
                        bean.setUserRole(Constants.PermissionUser);
                    } else {
                        bean.setId(bean.id);
                        bean.setUserRole(Constants.GeneralUser);
                    }
                    UserDBBeanUtils.updateData(bean);
                    mDataList = (ArrayList) UserDBBeanUtils.queryAll(UserDBBean.class);
                    mHandler.post(() ->  mAdapter.setItem(position, bean));
                    LogUtils.e(TAG + "==bean.toString====" + bean.toString());

                }
            });
            //修改密码
        } else if (viewId == R.id.update_password) {
            switch (mLoginUserRole) {  //当前用户权限 ---->只有超级用户才可以修改，其他用户去我的里面修改
                case Constants.AdminUser:  //超级用户
                    showChangePasswordDialog(bean, position, Constants.AdminUser + "");
                    break;
                case Constants.PermissionUser:  //权限用户
                    if (itemRole == Constants.AdminUser) {
                        toast(getResources().getString(R.string.toast_02));
                        //修改密码
                    } else if (itemRole == Constants.PermissionUser) {
                        toast(getResources().getString(R.string.toast_01));
                    } else {
                        showChangePasswordDialog(bean, position, Constants.GeneralUser + "");
                    }
                    break;
                case Constants.GeneralUser:  //普通用户
                    toast(getResources().getString(R.string.toast_01));
                    break;
            }
            //删除用户
        } else if (viewId == R.id.delete) {
            if (itemView != null) {
                SwipeMenuLayout mSwipeMenuLayout = itemView.findViewById(R.id.swipeMenuLay);
                //关闭侧边
                mSwipeMenuLayout.quickClose();
            }
            //当前用户权限
            switch (mLoginUserRole) {
                //超级用户
                case Constants.AdminUser:
                    showDeleteDialog(bean, Constants.PermissionUser, position);
                    break;
                //权限用户
                case Constants.PermissionUser:
                    //大于权限才可以删除
                    if (mLoginUserRole > itemRole) {
                        showDeleteDialog(bean, Constants.PermissionUser, position);
                    } else if (mLoginUserRole == itemRole) {
                        String currentUsername = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Username, "");
                        if (currentUsername.equals(bean.getUsername())) {
                            showDeleteMineDialog(bean, position);
                        } else {
                            toast(getResources().getString(R.string.toast_04));
                        }
                    } else {
                        toast(getResources().getString(R.string.toast_05));
                    }
                    break;
                //普通用户
                case Constants.GeneralUser:
                    toast(getResources().getString(R.string.toast_03));
                    break;
            }
        }

    }


    /**
     * 修改密码对话框
     *
     * @param bean
     * @param type 当前被修改用户的    权限等级，因为超级用户只能被修改一次密码的机会，所以如果是admin 需要添加sp 标识
     */
    private void showChangePasswordDialog(UserDBBean bean, int position, String type) {
        new InputDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.item_change_password))
                .setConfirm(getString(R.string.common_confirm))
                .setHint(getResources().getString(R.string.mine_new_password))
                .setCancel(getString(R.string.common_cancel))
                .setListener((dialog, newPassword) -> {
                    //对DB做修改或者增加的操作
                    bean.setPassword(newPassword);
                    UserDBBeanUtils.updateData(bean);
                    mHandler.post(() ->  mAdapter.setItem(position, bean));
                    //超级用户只能被修改一次密码的机会   的标识
                    if (type.equals(Constants.AdminUser + "")) {
                        SharePreferenceUtil.put(getActivity(), SharePreferenceUtil.Current_Admin_ChangePassword, true);
                    }
                    toast(getResources().getString(R.string.toast_09));
                }).show();
    }

    /**
     * 删除自己对话框
     *
     * @param bean
     */
    private void showDeleteMineDialog(UserDBBean bean, int position) {
        new MessageDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.mine_exit_title))
                .setMessage(getResources().getString(R.string.make_sure_deleted_myself))
                .setConfirm(getResources().getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setCanceledOnTouchOutside(false)
                .setListener(dialog -> {
                    UserDBBeanUtils.deleteData(bean);


                    mHandler.post(() ->  mAdapter.setItem(position, bean));
                    SharePreferenceUtil.put(getActivity(), Constants.Is_LoginEd, false);
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    String name = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Username, "");
                    int type = (int) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_UserRole, Constants.GeneralUser);
                    LogUtils.e("TAG====是否确认删除你自己==username===" + name + "==type==" + type);
                }).show();


    }

    /**
     * 删除用户
     *
     * @param bean
     * @param type     用户类型
     * @param position 当前角标
     */
    private void showDeleteDialog(UserDBBean bean, int type, int position) {
        new MessageDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.mine_exit_title))
                .setMessage(getResources().getString(R.string.make_sure_deleted_user))
                .setConfirm(getResources().getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setCanceledOnTouchOutside(false)
                .setListener(dialog -> {
                    UserDBBeanUtils.deleteData(bean);
                    startThreadSetRecycleViewData();
                }).show();

    }

    /**
     * 新增用户对话框
     */
    private void showAddUserDialog() {
        // 输入对话框
        new Input2AddUserDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.add_user))
                // 确定按钮文本
                .setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setListener((dialog, username, password) -> {
                    //对DB做修改或者增加的操作
                    boolean isExist = UserDBBeanUtils.queryListIsExist(username);
                    if ("".equals(username)) {
                        toast(getResources().getString(R.string.toast_07));
                    } else if (isExist) {
                        toast(getResources().getString(R.string.toast_08));
                    } else {
                        UserDBBean bean = new UserDBBean();
                        bean.setUsername(username);
                        bean.setPassword(password);
                        bean.setTag(username);
                        bean.setRememberPassword(false);
                        bean.setRememberPrivacy(true);
                        bean.setUserRole(Constants.GeneralUser);
                        UserDBBeanUtils.insertOrReplaceData(bean);
                        startThreadSetRecycleViewData();
                    }
                }).show();

    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusView;
    }

    private void startThreadSetRecycleViewData() {
        new Thread(() -> {
            mDataList = (ArrayList) UserDBBeanUtils.queryList();
            LogUtils.e(TAG + "存储的数据" + mDataList.size());
            for (int i = 0; i < mDataList.size(); i++) {
                UserDBBean a = mDataList.get(i);
                LogUtils.e(TAG + "==bean.toString====" + a.toString());
            }
            mHandler.sendEmptyMessage(1);
        }).start();

    }

}