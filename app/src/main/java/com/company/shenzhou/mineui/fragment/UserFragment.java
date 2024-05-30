package com.company.shenzhou.mineui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.company.shenzhou.R;
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
import com.company.shenzhou.widget.SwipeMenuLayout;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.hjq.widget.view.SwitchButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : 用户界面
 */
public final class UserFragment extends TitleBarFragment<MainActivity> implements BaseAdapter.OnChildClickListener {
    private static final String TAG = "UserFragment，界面==";
    private WrapRecyclerView mRecyclerView;
    private UserAdapter mAdapter;
    private int mLoginUserRole;
    private String mLoginUsername;
    private ArrayList<UserDBBean> mDataList = new ArrayList<>();
    private TextView mViewLoginUsername;
    private TitleBar mTitleBar;

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
        mTitleBar = findViewById(R.id.title_bar);
        mViewLoginUsername = findViewById(R.id.tv_username);
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
        mViewLoginUsername.setText(mLoginUsername);
        mDataList = (ArrayList) UserDBBeanUtils.queryAll(UserDBBean.class);
        mAdapter.setData(mDataList);
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
                    for (int i = 0; i < mDataList.size(); i++) {
                        UserDBBean a = mDataList.get(i);
                        LogUtils.e(TAG + "==bean.toString====" + a.toString());
                    }
                    mAdapter.setData(mDataList);
                }
            });
            //修改密码
        } else if (viewId == R.id.update_password) {
            switch (mLoginUserRole) {  //当前用户权限 ---->只有超级用户才可以修改，其他用户去我的里面修改
                case Constants.GeneralUser:  //普通用户
                    toast(getResources().getString(R.string.toast_01));
                    break;
                case Constants.PermissionUser:  //权限用户
                    if (itemRole == Constants.AdminUser) {
                        toast(getResources().getString(R.string.toast_02));
                        //修改密码
                    } else if (itemRole == Constants.PermissionUser) {
                        toast(getResources().getString(R.string.toast_01));
                    } else {
                        showChangePasswordDialog(bean, Constants.GeneralUser + "");
                    }
                    break;
                case Constants.AdminUser:  //超级用户
                    showChangePasswordDialog(bean, Constants.AdminUser + "");
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
                //普通用户
                case Constants.GeneralUser:
                    toast(getResources().getString(R.string.toast_03));
                    break;
                //权限用户
                case Constants.PermissionUser:
                    //大于权限才可以删除
                    if (mLoginUserRole > itemRole) {
                        showDeleteDialog(bean, Constants.PermissionUser, position);
                    } else if (mLoginUserRole == itemRole) {
                        String currentUsername = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Username, "");
                        if (currentUsername.equals(bean.getUsername())) {
                            showDeleteMineDialog(bean);
                        } else {
                            toast(getResources().getString(R.string.toast_04));
                        }
                    } else {
                        toast(getResources().getString(R.string.toast_05));
                    }
                    break;
                //超级用户
                case Constants.AdminUser:
                    if ("admin".equals(bean.getUsername())) {  //超级用户不能删除自己
                        toast(getResources().getString(R.string.toast_06));
                    } else {
                        showDeleteDialog(bean, Constants.PermissionUser, position);
                    }
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
    private void showChangePasswordDialog(UserDBBean bean, String type) {
        new InputDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.item_change_password))
                .setConfirm(getString(R.string.common_confirm))
                .setHint(getResources().getString(R.string.mine_new_password))
                .setCancel(getString(R.string.common_cancel))
                .setListener((dialog, newPassword) -> {
                    //对DB做修改或者增加的操作
                    bean.setPassword(newPassword);
                    UserDBBeanUtils.updateData(bean);
                    List list = UserDBBeanUtils.queryAll(UserDBBean.class);
                    mAdapter.setData(list);
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
    private void showDeleteMineDialog(UserDBBean bean) {
        new MessageDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.mine_exit_title))
                .setMessage(getResources().getString(R.string.make_sure_deleted_myself))
                .setConfirm(getResources().getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setCanceledOnTouchOutside(false)
                .setListener(dialog -> {
                    UserDBBeanUtils.deleteData(bean);
                    List list = UserDBBeanUtils.queryAll(UserDBBean.class);
                    mAdapter.setData(list);
                    SharePreferenceUtil.put(getActivity(), Constants.Is_Logined, false);
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
                    mAdapter.removeItem(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyItemRangeChanged(0, mDataList.size());

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
                        mDataList = (ArrayList) UserDBBeanUtils.queryAll(UserDBBean.class);
                        LogUtils.e(TAG + "存储的数据" + mDataList.size());
                        for (int i = 0; i < mDataList.size(); i++) {
                            UserDBBean a = mDataList.get(i);
                            LogUtils.e(TAG + "==bean.toString====" + a.toString());
                        }
                        mAdapter.setData(mDataList);
                    }
                }).show();

    }
}