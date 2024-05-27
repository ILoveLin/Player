package com.company.shenzhou.mineui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.shenzhou.R;
import com.company.shenzhou.app.AppAdapter;
import com.company.shenzhou.bean.dbbean.UserDBRememberBean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/5/27 17:57
 * desc：用户列表 adapter
 */
public final class UserAdapter extends AppAdapter<UserDBRememberBean> {

    public UserAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mTextView;

        private ViewHolder() {
            super(R.layout.status_item);
            mTextView = findViewById(R.id.tv_status_text);
        }

        @Override
        public void onBindView(int position) {
            mTextView.setText(getItem(position).getPassword()+"");
        }
    }
}