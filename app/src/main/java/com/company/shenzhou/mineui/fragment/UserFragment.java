package com.company.shenzhou.mineui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.company.shenzhou.R;
import com.company.shenzhou.app.TitleBarFragment;
import com.company.shenzhou.bean.dbbean.UserDBBean;
import com.company.shenzhou.bean.dbbean.UserDBRememberBean;
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
import com.hjq.base.BaseDialog;
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
    private int currentUserType;
    private String currentUsername;
    private ArrayList<UserDBBean> mDataList = new ArrayList<>();
    ;
    private TextView mLoginUsername;
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
        mLoginUsername = findViewById(R.id.tv_username);
        mAdapter = new UserAdapter(getAttachActivity());
        mAdapter.setOnChildClickListener(R.id.linear_item, this);
        mAdapter.setOnChildClickListener(R.id.slide_switch, this);
        mAdapter.setOnChildClickListener(R.id.update_password, this);
        mAdapter.setOnChildClickListener(R.id.delete, this);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void initData() {
        currentUserType = (int) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_UserType, 0);
        currentUsername = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Username, "");
        LogUtils.e(TAG + "======db==currentUserType==" + currentUserType);
        LogUtils.e(TAG + "======db==currentUsername==" + currentUsername);
        mLoginUsername.setText(currentUsername);

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
                if (currentUserType == Constants.GeneralUser) { //普通用户
                    toast(getResources().getString(R.string.toast_10));
                } else if (currentUserType == Constants.PermissionUser) {//权限用户
                    showAddUserDialog();
                } else if (currentUserType == Constants.AdminUser) {//超级管理员
                    showAddUserDialog();

                }
            }
        });

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
        int userType = bean.getUserRole();
        //ItemClick
        if (viewId == R.id.linear_item) {
            SwitchButton mSwitchButton = itemView.findViewById(R.id.slide_switch);
            //这么写是避免 computing a layout or scrolling  Bug
            if (mRecyclerView.isComputingLayout()) {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (userType == 0) {
                            //这么写是避免 computing a layout or scrolling  Bug
                            mSwitchButton.setChecked(false);
                        } else {
                            mSwitchButton.setChecked(true);
                        }
                    }
                });
            } else {
                if (userType == 0) {
                    //这么写是避免 computing a layout or scrolling  Bug
                    mSwitchButton.setChecked(false);
                } else {
                    mSwitchButton.setChecked(true);
                }

            }
            //切换管理员和普通用户
        } else if (viewId == R.id.slide_switch) {
            if (0 == userType) {
                childView.setClickable(false);
            } else {
                childView.setClickable(true);
            }
            //点击切换按钮，设置是否是权限用户
            SwitchButton mSwitchButton = itemView.findViewById(R.id.slide_switch);
            mSwitchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton button, boolean isChecked) {


                    //0普通用户、1权限用户、2超级管理员  默认为0-普通用户
                    if (currentUserType == Constants.AdminUser && userType < Constants.AdminUser) {  //只有超级管理员才可以设置权限
                        mSwitchButton.setEnabled(true);
                        if (isChecked) {
                            bean.setUserRole(Constants.AdminUser);
                        } else {
                            bean.setUserRole(Constants.GeneralUser);
                        }
                        UserDBBeanUtils.updateData(bean);
                        List list = UserDBBeanUtils.queryAll(UserDBBean.class);
                        mAdapter.setData(list);
                    }
                }
            });
            //修改密码
        } else if (viewId == R.id.update_password) {
            switch (currentUserType) {  //当前用户权限 ---->只有超级用户才可以修改，其他用户去我的里面修改
                case Constants.GeneralUser:  //普通用户
                    toast(getResources().getString(R.string.toast_01));
                    break;
                case Constants.PermissionUser:  //权限用户
                    if (currentUserType > userType) {
                        //修改密码
                        showChangePasswordDialog(bean, Constants.PermissionUser + "");
                    } else {
                        toast(getResources().getString(R.string.toast_02));
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
            switch (currentUserType) {
                case Constants.GeneralUser:  //普通用户
                    toast(getResources().getString(R.string.toast_03));
                    break;
                case Constants.PermissionUser:  //权限用户
                    if (currentUserType > userType) {  //大于权限才可以删除
                        showDeleteDialog(bean, Constants.PermissionUser, position);
                    } else if (currentUserType == userType) {
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
                case Constants.AdminUser:  //超级用户
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
     * @param type
     */
    private void showChangePasswordDialog(UserDBBean bean, String type) {

        // 输入对话框
        InputDialog.Builder builder = new InputDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.item_change_password))
                // 提示可以不用填写
                // 确定按钮文本
                .setConfirm(getString(R.string.common_confirm))
                .setHint(getResources().getString(R.string.mine_new_password))
                // 设置 null 表示不显示取消按钮
                .setCancel(getString(R.string.common_cancel))
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String newPassword) {
                        //对DB做修改或者增加的操作
                        bean.setPassword(newPassword);
                        UserDBBeanUtils.updateData(bean);
                        List list = UserDBBeanUtils.queryAll(UserDBBean.class);
                        mAdapter.setData(list);
                        if (type.equals(Constants.AdminUser + "")) {
                            SharePreferenceUtil.put(getActivity(), SharePreferenceUtil.Current_Admin_ChangePassword, true);
                        }
                        toast(getResources().getString(R.string.toast_09));
                    }

                })
                .show();

    }


    /**
     * 删除自己对话框
     *
     * @param bean
     */
    private void showDeleteMineDialog(UserDBBean bean) {
        MessageDialog.Builder mExitDialog = new MessageDialog.Builder(getActivity());
        // 标题可以不用填写
        mExitDialog.setTitle(getResources().getString(R.string.mine_exit_title))
                // 内容必须要填写
                .setMessage(getResources().getString(R.string.make_sure_deleted_myself))
                // 确定按钮文本
                .setConfirm(getResources().getString(R.string.common_confirm))
                // 设置 null 表示不显示取消按钮
                .setCancel(getString(R.string.common_cancel))
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setCanceledOnTouchOutside(false)
                .setListener(new MessageDialog.OnListener() {

                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        UserDBBeanUtils.deleteData(bean);
                        List list = UserDBBeanUtils.queryAll(UserDBBean.class);
                        mAdapter.setData(list);
                        SharePreferenceUtil.put(getActivity(), Constants.Is_Logined, false);
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        String name = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Username, "");
                        int type = (int) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_UserType, Constants.GeneralUser);
                        LogUtils.e("TAG====是否确认删除你自己==username===" + name + "==type==" + type);

                    }
                })
                .show();


    }

    /**
     * 删除用户
     *
     * @param bean
     * @param type     用户类型
     * @param position 当前角标
     */

    private void showDeleteDialog(UserDBBean bean, int type, int position) {
        MessageDialog.Builder mExitDialog = new MessageDialog.Builder(getActivity());
        // 标题可以不用填写
        mExitDialog.setTitle(getResources().getString(R.string.mine_exit_title))
                // 内容必须要填写
                .setMessage(getResources().getString(R.string.make_sure_deleted_user))
                // 确定按钮文本
                .setConfirm(getResources().getString(R.string.common_confirm))
                // 设置 null 表示不显示取消按钮
                .setCancel(getString(R.string.common_cancel))
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setCanceledOnTouchOutside(false)
                .setListener(new MessageDialog.OnListener() {

                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        UserDBBeanUtils.deleteData(bean);
                        mAdapter.removeItem(position);
                        mAdapter.notifyItemRemoved(position);
                        mAdapter.notifyItemRangeChanged(0, mDataList.size());

                    }

                })
                .show();

    }

    /**
     * 新增用户对话框
     */
    private void showAddUserDialog() {

        // 输入对话框
        new Input2AddUserDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.mine_change_password))
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
                            UserDBBean a = (UserDBBean) mDataList.get(i);
                            LogUtils.e(TAG + "存储的数据" + a.getUsername());
                            LogUtils.e(TAG + "存储的数据" + a.getPassword());
                            LogUtils.e(TAG + "存储的数据" + a.rememberPassword);
                        }
                        mAdapter.setData(mDataList);
                    }
                })
                .show();

    }
}