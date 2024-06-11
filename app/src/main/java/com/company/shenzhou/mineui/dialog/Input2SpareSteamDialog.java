package com.company.shenzhou.mineui.dialog;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.StringRes;

import com.company.shenzhou.R;
import com.company.shenzhou.bean.dbbean.DeviceDBBean;
import com.hjq.base.BaseDialog;


/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 输入对话框
 */
public final class Input2SpareSteamDialog {

    public static final class Builder
            extends CommonSpareDialog.Builder<Builder>
            implements BaseDialog.OnShowListener {

        private OnListener mListener;
        private final EditText mLiveSteam, mMicSteam;

        public Builder(Context context) {
            super(context);
            setCustomView(R.layout.input_2_steam_dialog);
            mLiveSteam = findViewById(R.id.cet_cme_old_password);
            mMicSteam = findViewById(R.id.cet_cme_new_password);
            addOnShowListener(this);
        }

        public Builder setHint(@StringRes int id) {
            return setHint(getString(id));
        }
        public Builder setHint(CharSequence text) {
            mLiveSteam.setHint(text);
            return this;
        }
        public Builder setDataBean(DeviceDBBean bean) {
            mLiveSteam.setText(bean.getSpareLiveSteam());
            mMicSteam.setText(bean.getSpareMicPushSteam());
            return this;
        }
        public Builder setNewHint(CharSequence text) {
            mMicSteam.setHint(text);
            return this;
        }
        public Builder setContent(@StringRes int id) {
            return setContent(getString(id));
        }
        public Builder setContent(CharSequence text) {
            mLiveSteam.setText(text);
            int index = mLiveSteam.getText().toString().length();
            if (index > 0) {
                mLiveSteam.requestFocus();
                mLiveSteam.setSelection(index);
            }
            return this;
        }
        public Builder setNewContent(CharSequence text) {
            mMicSteam.setText(text);
            int index = mMicSteam.getText().toString().length();
            if (index > 0) {
                mMicSteam.requestFocus();
                mMicSteam.setSelection(index);
            }
            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * {@link BaseDialog.OnShowListener}
         */
        @Override
        public void onShow(BaseDialog dialog) {
            postDelayed(() -> getSystemService(InputMethodManager.class).showSoftInput(mLiveSteam, 0), 500);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.relative_scan:
                case R.id.iv_scan_noe:
                case R.id.tv_scan_tow:
                    if (mListener != null) {
                        mListener.onScan(getDialog(), mLiveSteam.getText().toString(), mMicSteam.getText().toString());
                    }
                    break;
                case R.id.tv_ui_confirm:
                    if (mListener != null) {
                        mListener.onConfirm(getDialog(), mLiveSteam.getText().toString(), mMicSteam.getText().toString());
                    }
                    break;
                case R.id.tv_ui_cancel:
                    autoDismiss();
                    if (mListener != null) {
                        mListener.onCancel(getDialog());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public interface OnListener {
        /**
         * 点击扫码功能
         */
        void onScan(BaseDialog dialog, String content,String newContent);

        /**
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog, String content,String newContent);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {}
    }
}