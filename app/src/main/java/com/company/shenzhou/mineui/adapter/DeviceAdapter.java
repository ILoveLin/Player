package com.company.shenzhou.mineui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.shenzhou.R;
import com.company.shenzhou.app.AppAdapter;
import com.company.shenzhou.bean.dbbean.DeviceDBBean;
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
 * desc：设备列表 adapter
 */
public final class DeviceAdapter extends AppAdapter<DeviceDBBean> {
    private static final String TAG = "DeviceAdapter，==";

    private final Context mContext;

    public DeviceAdapter(Context context) {
        super(context);
        this.mContext = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceAdapter.ViewHolder();
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
            DeviceDBBean bean = getItem(position);
            LogUtils.e(TAG + "==adapter====");
        }
    }
}