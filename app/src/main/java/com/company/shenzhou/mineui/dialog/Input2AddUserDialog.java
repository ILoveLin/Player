package com.company.shenzhou.mineui.dialog;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.StringRes;

import com.company.shenzhou.R;
import com.company.shenzhou.ui.dialog.CommonDialog;
import com.hjq.base.BaseDialog;


/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 输入对话框
 */
public final class Input2AddUserDialog {

    public static final class Builder
            extends CommonDialog.Builder<Builder>
            implements BaseDialog.OnShowListener {

        private OnListener mListener;
        private final EditText mInputOldView,mInputNewView;

        public Builder(Context context) {
            super(context);
            setCustomView(R.layout.input_2_add_user_dialog);
            mInputOldView = findViewById(R.id.cet_cme_old_password);
            mInputNewView = findViewById(R.id.cet_cme_new_password);

            addOnShowListener(this);
        }

        public Builder setHint(@StringRes int id) {
            return setHint(getString(id));
        }
        public Builder setHint(CharSequence text) {
            mInputOldView.setHint(text);
            return this;
        }
        public Builder setNewHint(CharSequence text) {
            mInputNewView.setHint(text);
            return this;
        }
        public Builder setContent(@StringRes int id) {
            return setContent(getString(id));
        }
        public Builder setContent(CharSequence text) {
            mInputOldView.setText(text);
            int index = mInputOldView.getText().toString().length();
            if (index > 0) {
                mInputOldView.requestFocus();
                mInputOldView.setSelection(index);
            }
            return this;
        }
        public Builder setNewContent(CharSequence text) {
            mInputNewView.setText(text);
            int index = mInputNewView.getText().toString().length();
            if (index > 0) {
                mInputNewView.requestFocus();
                mInputNewView.setSelection(index);
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
            postDelayed(() -> getSystemService(InputMethodManager.class).showSoftInput(mInputOldView, 0), 500);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_ui_confirm:
                    autoDismiss();
                    if (mListener != null) {
                        mListener.onConfirm(getDialog(), mInputOldView.getText().toString(),mInputNewView.getText().toString());
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
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog, String content,String newContent);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {}
    }
}