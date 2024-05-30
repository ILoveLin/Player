package com.company.shenzhou.mineui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.company.shenzhou.R;
import com.company.shenzhou.ui.dialog.CommonDialog;
import com.company.shenzhou.utlis.CommonUtil;
import com.company.shenzhou.utlis.LogUtils;
import com.hjq.base.BaseDialog;
import com.hjq.widget.view.ClearEditText;

import java.util.HashMap;


/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/02/27
 * desc   : 填一填
 *          新增设备对话框
 */
public final class AddDeviceDialog {
    public static final class Builder
            extends CommonDialog.Builder<Builder>
            implements BaseDialog.OnShowListener {

        private OnListener mListener;
        //        private final EditText mInputView;
        private ClearEditText cet_cme_account, cet_cme_device_name, cet_cme_device_code;
        private ClearEditText cet_cme_password;
        private ClearEditText cet_cme_socket_port;
        private ClearEditText cet_cme_http_port;
        private ClearEditText cet_cme_live_ip;
        private ClearEditText cet_cme_ip_public;
        private ClearEditText cet_cme_msg_mark;
        private ClearEditText cet_cme_live_port;
        private ClearEditText cet_cme_api_version;
        private ClearEditText cet_cme_device_type;
        private ClearEditText cet_cme_ddns_account;
        private ClearEditText cet_cme_ddns_password;
        private ClearEditText cet_cme_ddns_url;
        private ClearEditText cet_cme_line_type;
        private TextView makeSure;
        private TextView makeCancle;
        public Context mContext;
        private LinearLayout linear_pop_change;

        public Builder(Context context) {
            super(context);
            this.mContext = context;
            setCustomView(R.layout.advice_add_dialog);
            //设备名
            cet_cme_device_name = (ClearEditText) findViewById(R.id.cet_cme_device_name);
            //设备码
            cet_cme_device_code = (ClearEditText) findViewById(R.id.cet_cme_device_code);
            //备注信息
            cet_cme_msg_mark = (ClearEditText) findViewById(R.id.cet_cme_note_message);
            //ip
            cet_cme_live_ip = (ClearEditText) findViewById(R.id.cet_cme_ip);
            //公网ip
            cet_cme_ip_public = (ClearEditText) findViewById(R.id.cet_cme_ip_public);

            //设备类型
            cet_cme_device_type = (ClearEditText) findViewById(R.id.cet_cme_start_type);
            cet_cme_device_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onAddReInputDeviceTypeClick(cet_cme_device_type);
                }
            });
            //线路
            cet_cme_line_type = (ClearEditText) findViewById(R.id.cet_cme_line_type);
            cet_cme_line_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onAddChoseModeTypeClick();
                }
            });
            //接口版本
            cet_cme_api_version = (ClearEditText) findViewById(R.id.cet_cme_api_version);
            //直播端口号
            cet_cme_live_port = (ClearEditText) findViewById(R.id.cet_cme_port);
            //socket端口号
            cet_cme_socket_port = (ClearEditText) findViewById(R.id.cet_cme_socket_port);
            //http端口号
            cet_cme_http_port = (ClearEditText) findViewById(R.id.cet_cme_http_port);
            //设备账号
            cet_cme_account = (ClearEditText) findViewById(R.id.cet_cme_account);
            //设备密码
            cet_cme_password = (ClearEditText) findViewById(R.id.cet_cme_password);
            //ddns
            cet_cme_ddns_account = (ClearEditText) findViewById(R.id.cet_cme_ddns_account);
            cet_cme_ddns_password = (ClearEditText) findViewById(R.id.cet_cme_ddns_password);
            cet_cme_ddns_url = (ClearEditText) findViewById(R.id.cet_cme_ddns_url);


            addOnShowListener(this);
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
//            postDelayed(() -> getSystemService(InputMethodManager.class).showSoftInput(mInputView, 0), 500);
        }

        public ClearEditText getDeviceNameView() {
            return cet_cme_device_name;
        }

        public ClearEditText getDeviceCodeView() {
            return cet_cme_device_code;
        }

        public ClearEditText getMessageMarkView() {
            return cet_cme_msg_mark;
        }

        public ClearEditText getLiveIpView() {
            return cet_cme_live_ip;
        }

        public ClearEditText getLiveIpPublicView() {
            return cet_cme_ip_public;
        }

        public ClearEditText getDDNSAccountView() {
            return cet_cme_ddns_account;
        }

        public ClearEditText getDDNSPasswordView() {
            return cet_cme_ddns_password;
        }

        public ClearEditText getDDNSURLView() {
            return cet_cme_ddns_url;
        }

        public ClearEditText getAccountView() {
            return cet_cme_account;
        }

        public ClearEditText getPasswordView() {
            return cet_cme_password;
        }

        public ClearEditText getSocketPortView() {
            return cet_cme_socket_port;
        }

        public ClearEditText getHttpPortView() {
            return cet_cme_http_port;
        }

        public ClearEditText getLivePortView() {
            return cet_cme_live_port;
        }


        public ClearEditText getApiVersionView() {
            return cet_cme_api_version;
        }

        public ClearEditText getDeviceTypeView() {
            return cet_cme_device_type;
        }

        public ClearEditText getChannelView() {
            return cet_cme_line_type;
        }


        public void dismissDialog() {
            autoDismiss();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_ui_confirm:
                    autoDismiss();
                    if (mListener != null) {
                        String deviceName = cet_cme_device_name.getText().toString().trim();
                        String deviceCode = cet_cme_device_code.getText().toString().trim();
                        String msgMark = cet_cme_msg_mark.getText().toString().trim();
                        String liveIp = cet_cme_live_ip.getText().toString().trim();
                        String liveIpPublic = cet_cme_ip_public.getText().toString().trim();
                        String ddnsAccount = cet_cme_ddns_account.getText().toString().trim();
                        String ddnsPassword = cet_cme_ddns_password.getText().toString().trim();
                        String ddnsUrl = cet_cme_ddns_url.getText().toString().trim();
                        String account = cet_cme_account.getText().toString().trim();
                        String password = cet_cme_password.getText().toString().trim();

                        String socketPort = cet_cme_socket_port.getText().toString().trim();
                        String httpPort = cet_cme_http_port.getText().toString().trim();
                        String livePort = cet_cme_live_port.getText().toString().trim();
                        String apiVersion = cet_cme_api_version.getText().toString().trim();
                        //获取当前类型  是2 的时候表示url链接 账号密码  端口可以为空
                        String typeDesc = cet_cme_device_type.getText().toString().trim();
                        String deviceTypeHexNum = CommonUtil.getDeviceTypeHexNum(mContext, typeDesc);
                        String deviceTypeDecNum = CommonUtil.getDeviceTypeDecNum(mContext, typeDesc);
                        String channel = cet_cme_line_type.getText().toString().trim();

                        HashMap<String, String> mMap = new HashMap<>();
                        mMap.put("deviceName", deviceName);
                        mMap.put("deviceCode", deviceCode);
                        mMap.put("msgMark", msgMark);
                        mMap.put("liveIp", liveIp);
                        if (null==liveIpPublic){
                            mMap.put("liveIpPublic", "");
                        }else{
                            mMap.put("liveIpPublic", liveIpPublic);
                        }
                        mMap.put("ddnsAccount", ddnsAccount);
                        mMap.put("ddnsPassword", ddnsPassword);
                        mMap.put("ddnsUrl", ddnsUrl);
                        mMap.put("account", account);
                        mMap.put("password", password);
                        mMap.put("socketPort", socketPort);
                        mMap.put("httpPort", httpPort);
                        mMap.put("livePort", livePort);
                        mMap.put("apiVersion", apiVersion);
                        mMap.put("deviceTypeDesc", typeDesc);
                        mMap.put("deviceTypeHexNum", deviceTypeHexNum);
                        mMap.put("deviceTypeDecNum", deviceTypeDecNum);
                        mMap.put("channel", channel);
                        LogUtils.e("dialog===修改设备===typeDesc=" + typeDesc);
                        LogUtils.e("dialog===修改设备===deviceTypeHexNum=" + deviceTypeHexNum);
                        LogUtils.e("dialog===修改设备===deviceTypeDecNum=" + deviceTypeDecNum);
                        LogUtils.e("dialog===修改设备===channel=" + channel);
                        mListener.onConfirm(getDialog(), mMap);
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
        void onConfirm(BaseDialog dialog, HashMap<String, String> mMap);

        /**
         * 新增设备的时候,
         * 重新选择设备类型
         *
         * @param mTv
         */
        void onAddReInputDeviceTypeClick(TextView mTv);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {
        }

        /**
         * 工作模式的点击事件
         */
        void onAddChoseModeTypeClick();
    }
}