package com.company.shenzhou.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.company.shenzhou.aop.Log;
import com.company.shenzhou.aop.SingleClick;
import com.company.shenzhou.app.AppActivity;
import com.company.shenzhou.http.api.GetCodeApi;
import com.company.shenzhou.http.api.PhoneApi;
import com.company.shenzhou.http.model.HttpData;
import com.company.shenzhou.manager.InputTextManager;
import com.company.shenzhou.ui.dialog.TipsDialog;
import com.company.shenzhou.R;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.toast.ToastUtils;
import com.hjq.widget.view.CountdownView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/20
 *    desc   : 设置手机号
 */
public final class PhoneResetActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    private static final String INTENT_KEY_IN_CODE = "code";

    @Log
    public static void start(Context context, String code) {
        Intent intent = new Intent(context, PhoneResetActivity.class);
        intent.putExtra(INTENT_KEY_IN_CODE, code);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private EditText mPhoneView;
    private EditText mCodeView;
    private CountdownView mCountdownView;
    private Button mCommitView;

    /** 验证码 */
    private String mVerifyCode;

    @Override
    protected int getLayoutId() {
        return R.layout.phone_reset_activity;
    }

    @Override
    protected void initView() {
        mPhoneView = findViewById(R.id.et_phone_reset_phone);
        mCodeView = findViewById(R.id.et_phone_reset_code);
        mCountdownView = findViewById(R.id.cv_phone_reset_countdown);
        mCommitView = findViewById(R.id.btn_phone_reset_commit);

        setOnClickListener(mCountdownView, mCommitView);

        mCodeView.setOnEditorActionListener(this);

        InputTextManager.with(this)
                .addView(mPhoneView)
                .addView(mCodeView)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {
        mVerifyCode = getString(INTENT_KEY_IN_CODE);
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mCountdownView) {

            if (mPhoneView.getText().toString().length() != 11) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_phone_input_error);
                return;
            }

            if (true) {
                toast(R.string.common_code_send_hint);
                mCountdownView.start();
                return;
            }

            // 获取验证码
            EasyHttp.post(this)
                    .api(new GetCodeApi()
                            .setPhone(mPhoneView.getText().toString()))
                    .request(new HttpCallback<HttpData<Void>>(this) {

                        @Override
                        public void onSucceed(HttpData<Void> data) {
                            toast(R.string.common_code_send_hint);
                            mCountdownView.start();
                        }
                    });
        } else if (view == mCommitView) {

            if (mPhoneView.getText().toString().length() != 11) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_phone_input_error);
                return;
            }

            if (mCodeView.getText().toString().length() != getResources().getInteger(R.integer.sms_code_length)) {
                ToastUtils.show(R.string.common_code_error_hint);
                return;
            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

            if (true) {
                new TipsDialog.Builder(this)
                        .setIcon(TipsDialog.ICON_FINISH)
                        .setMessage(R.string.phone_reset_commit_succeed)
                        .setDuration(2000)
                        .addOnDismissListener(dialog -> finish())
                        .show();
                return;
            }

            // 更换手机号
            EasyHttp.post(this)
                    .api(new PhoneApi()
                            .setPreCode(mVerifyCode)
                            .setPhone(mPhoneView.getText().toString())
                            .setCode(mCodeView.getText().toString()))
                    .request(new HttpCallback<HttpData<Void>>(this) {

                        @Override
                        public void onSucceed(HttpData<Void> data) {
                            new TipsDialog.Builder(getActivity())
                                    .setIcon(TipsDialog.ICON_FINISH)
                                    .setMessage(R.string.phone_reset_commit_succeed)
                                    .setDuration(2000)
                                    .addOnDismissListener(dialog -> finish())
                                    .show();
                        }
                    });
        }
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击提交按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }
}