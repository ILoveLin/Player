package com.company.shenzhou.mineui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

import com.company.shenzhou.R;
import com.hjq.base.BaseDialog;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/21
 *    desc   : 项目通用 Dialog 布局封装
 */
public final class CommonSpareDialog {

    @SuppressWarnings("unchecked")
    public static class Builder<B extends CommonSpareDialog.Builder<?>>
            extends BaseDialog.Builder<B> {

        private boolean mAutoDismiss = true;

        private final ViewGroup mContainerLayout;
        private final TextView mTitleView;

        private final TextView mCancelView;
        private final View mLineView;
        private final TextView mConfirmView;
        private final RelativeLayout mRelativeScan;
        private final TextView tv_scan;
        private final ImageView iv_scan;
        public Builder(Context context) {
            super(context);

            setContentView(R.layout.ui_dialog2_spare);
            setAnimStyle(BaseDialog.ANIM_IOS);
            setGravity(Gravity.CENTER);
            mRelativeScan  = findViewById(R.id.relative_scan);
            iv_scan  = findViewById(R.id.iv_scan_noe);
            tv_scan  = findViewById(R.id.tv_scan_tow);
            mContainerLayout = findViewById(R.id.ll_ui_container);
            mTitleView = findViewById(R.id.tv_ui_title);
            mCancelView  = findViewById(R.id.tv_ui_cancel);
            mLineView = findViewById(R.id.v_ui_line);
            mConfirmView  = findViewById(R.id.tv_ui_confirm);
            setOnClickListener(mCancelView, mConfirmView,mRelativeScan,iv_scan,tv_scan);

        }

        public B setCustomView(@LayoutRes int id) {
            return setCustomView(LayoutInflater.from(getContext()).inflate(id, mContainerLayout, false));
        }

        public B setCustomView(View view) {
            mContainerLayout.addView(view, 1);
            return (B) this;
        }

        public B setTitle(@StringRes int id) {
            return setTitle(getString(id));
        }
        public B setTitle(CharSequence text) {
            mTitleView.setText(text);
            return (B) this;
        }

        public B setCancel(@StringRes int id) {
            return setCancel(getString(id));
        }
        public B setCancel(CharSequence text) {
            mCancelView.setText(text);
            mLineView.setVisibility((text == null || "".equals(text.toString())) ? View.GONE : View.VISIBLE);
            return (B) this;
        }

        public B setConfirm(@StringRes int id) {
            return setConfirm(getString(id));
        }
        public B setConfirm(CharSequence text) {
            mConfirmView.setText(text);
            return (B) this;
        }

        public B setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return (B) this;
        }

        public void autoDismiss() {
            if (mAutoDismiss) {
                dismiss();
            }
        }
    }
}