package com.company.shenzhou.mineui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.shenzhou.R;
import com.company.shenzhou.app.AppAdapter;
import com.company.shenzhou.bean.dbbean.UserDBBean;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.utlis.LogUtils;
import com.company.shenzhou.utlis.SharePreferenceUtil;
import com.company.shenzhou.widget.SwipeMenuLayout;
import com.hjq.widget.view.SwitchButton;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/5/27 17:57
 * desc：用户列表 adapter
 */
public final class UserAdapter extends AppAdapter<UserDBBean> {
    private final Context mContext;

    public UserAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

//    public void setListAndNotifyDataSetChanged() {
//        List currentList = UserDBBeanUtils.queryAll(UserDBRememberBean.class);
//        setData(currentList);
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mUserrRole, mUpdatePassword;
        private final TextView mUserName;
        private final SwipeMenuLayout mSwipeMenuLayout;
        private final SwitchButton mSwitchButton;

        private ViewHolder() {
            super(R.layout.item_swipemenulayout_user);
            mSwipeMenuLayout = findViewById(R.id.swipeMenuLay);
            mUserName = findViewById(R.id.tv_text);
            mUpdatePassword = findViewById(R.id.update_password);
            mUserrRole = findViewById(R.id.tv_text_type);
            mSwitchButton = findViewById(R.id.slide_switch);
        }

        @Override
        public void onBindView(int position) {
            UserDBBean bean = getItem(position);
            int currentUserType = (int) SharePreferenceUtil.get(mContext, SharePreferenceUtil.Current_UserRole, Constants.GeneralUser);
            mUserName.setText(bean.getUsername());
            //0普通  1权限  2超级用户
            switch (bean.getUserRole()) {
                case Constants.GeneralUser:
                    mUserrRole.setText(mContext.getResources().getString(R.string.level_01));
                    mSwitchButton.setChecked(false);
                    mSwitchButton.setVisibility(View.VISIBLE);
                    mUpdatePassword.setVisibility(View.VISIBLE);
                    break;
                case Constants.PermissionUser:
                    mUserrRole.setText(mContext.getResources().getString(R.string.level_02));
                    mSwitchButton.setChecked(true);
                    mSwitchButton.setVisibility(View.VISIBLE);
                    mUpdatePassword.setVisibility(View.VISIBLE);
                    break;
                case Constants.AdminUser:
                    mUserrRole.setText(mContext.getResources().getString(R.string.level_03));
                    mSwitchButton.setVisibility(View.GONE);
                    mUpdatePassword.setVisibility(View.GONE);
                    break;
            }

            //设置是否是权限用户
            //0普通用户、1权限用户、2超级管理员  默认为0==普通用户
            //进来的时候判断当前系统用户权限和当前item权限 然后做相对于的权限判断c操作
            if (currentUserType == 2 && bean.getUserRole() == 2) {
                mSwitchButton.setEnabled(false);  //超级用户对自己不能使用
            } else if (currentUserType == 2 && bean.getUserRole() < 2) {
                mSwitchButton.setEnabled(true);
            } else {
                mSwitchButton.setEnabled(false);
            }
            LogUtils.e("TAG====adapter==currentUserType===" + currentUserType + "==userItemType==" + bean.getUserRole());
            //设置item 的用户名
            mUserName.setText(getItem(position).getUsername());

        }
    }
}