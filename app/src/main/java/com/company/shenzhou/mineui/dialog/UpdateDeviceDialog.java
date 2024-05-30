package com.company.shenzhou.mineui.dialog;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/5/30 8:43
 * desc   : 填一填
 *          修改设备（信息）对话框
 */

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.company.shenzhou.R;
import com.company.shenzhou.bean.dbbean.DeviceDBBean;
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
 * desc   : 设备输入   修改对话框
 */
public final class UpdateDeviceDialog {

    public static final class Builder
            extends CommonDialog.Builder<Builder>
            implements BaseDialog.OnShowListener {
        private OnListener mListener;
        private TextView makeSure;
        private TextView makeCancle;
        private LinearLayout linear_pop_change;
        private Context context;
        private ClearEditText cet_cme_account, cet_cme_device_name, cet_cme_device_code;
        private ClearEditText cet_cme_password;
        private ClearEditText cet_cme_socket_port;
        private ClearEditText cet_cme_http_port;
        private ClearEditText cet_cme_live_ip;
        private ClearEditText cet_cme_ip_public;
        private ClearEditText cet_cme_msg_mark;
        private ClearEditText cet_cme_live_port;
        private ClearEditText cet_cme_api_version;
        private ClearEditText cet_cme_start_type;
        private ClearEditText mDDNSAccount;
        private ClearEditText mDDNSPassword;
        private ClearEditText mDDNSAddress;
        private ClearEditText cet_cme_line_type;

        public Builder(Context context, DeviceDBBean bean) {
            super(context);
            setCustomView(R.layout.advice_update_dialog);
            this.context = context;
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
            //ddns
            mDDNSAccount = (ClearEditText) findViewById(R.id.cet_cme_ddns_account);
            mDDNSPassword = (ClearEditText) findViewById(R.id.cet_cme_ddns_password);
            mDDNSAddress = (ClearEditText) findViewById(R.id.cet_cme_ddns_url);
            //设备账号
            cet_cme_account = (ClearEditText) findViewById(R.id.cet_cme_account);
            //设备密码
            cet_cme_password = (ClearEditText) findViewById(R.id.cet_cme_password);
            //socket端口号
            cet_cme_socket_port = (ClearEditText) findViewById(R.id.cet_cme_socket_port);
            //http端口号
            cet_cme_http_port = (ClearEditText) findViewById(R.id.cet_cme_http_port);
            //直播端口号
            cet_cme_live_port = (ClearEditText) findViewById(R.id.cet_cme_port);

            //语音版本号
            cet_cme_api_version = (ClearEditText) findViewById(R.id.cet_cme_api_version);
            //设备类型
            cet_cme_start_type = (ClearEditText) findViewById(R.id.cet_cme_start_type);
            cet_cme_start_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onReInputTypeClick(cet_cme_start_type);
                }
            });

            //线路模式
            cet_cme_line_type = (ClearEditText) findViewById(R.id.cet_cme_line_type);
            cet_cme_line_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onReInputChannelClick();
                }
            });

            if (null != bean) {
                cet_cme_device_name.setText("" + bean.getDeviceName());
                cet_cme_device_code.setText("" + bean.getDeviceCode());
                cet_cme_msg_mark.setText("" + bean.getMsgMark());
                cet_cme_live_ip.setText("" + bean.getIp());
                if (null == bean.getIpPublic()) {
                    cet_cme_ip_public.setText("");
                } else {
                    cet_cme_ip_public.setText(bean.getIpPublic());
                }
                if (null == bean.getDDNSAcount()) {
                    mDDNSAccount.setText("");
                } else {
                    mDDNSAccount.setText("" + bean.getDDNSAcount());
                }
                if (null == bean.getDDNSPassword()) {
                    mDDNSPassword.setText("");
                } else {
                    mDDNSPassword.setText("" + bean.getDDNSPassword());
                }
                if (null == bean.getDDNSURL()) {
                    mDDNSAddress.setText("");
                } else {
                    mDDNSAddress.setText("" + bean.getDDNSURL());
                }
                cet_cme_account.setText("" + bean.getAccount());
                cet_cme_password.setText("" + bean.getPassword());
                cet_cme_socket_port.setText("" + bean.getSocketPort());
                cet_cme_http_port.setText("" + bean.getHttpPort());
                cet_cme_live_port.setText("" + bean.getLivePort());
                cet_cme_api_version.setText("" + bean.getApiVersion());
                cet_cme_start_type.setText("" + bean.getDeviceTypeDesc());
                cet_cme_line_type.setText("" + bean.getChannel());

            }

            addOnShowListener(this);
        }

        public ClearEditText getLiveIpPublicView() {
            return cet_cme_ip_public;
        }

        public TextView getDeviceNameView() {
            return cet_cme_device_name;
        }

        public TextView getDeviceCodeView() {
            return cet_cme_device_code;
        }

        public TextView getMessageMarkView() {
            return cet_cme_msg_mark;
        }

        public ClearEditText getLiveIpView() {
            return cet_cme_live_ip;
        }

        public ClearEditText getDDNSAccountView() {
            return mDDNSAccount;
        }

        public ClearEditText getDDNSPasswordView() {
            return mDDNSPassword;
        }

        public ClearEditText getDDNSURLView() {
            return mDDNSAddress;
        }

        public TextView getAccountView() {
            return cet_cme_account;
        }

        public TextView getPasswordView() {
            return cet_cme_password;
        }

        public ClearEditText getSocketPortView() {
            return cet_cme_socket_port;
        }

        public ClearEditText getHttpPortView() {
            return cet_cme_http_port;
        }

        public TextView getLivePortView() {
            return cet_cme_live_port;
        }


        public TextView getApiVersionView() {
            return cet_cme_api_version;
        }

        public ClearEditText getDeviceTypeView() {
            return cet_cme_start_type;
        }

        public ClearEditText getChannelView() {
            return cet_cme_line_type;
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
                        String ddnsAccount = mDDNSAccount.getText().toString().trim();
                        String ddnsPassword = mDDNSPassword.getText().toString().trim();
                        String ddnsUrl = mDDNSAddress.getText().toString().trim();
                        String account = cet_cme_account.getText().toString().trim();
                        String password = cet_cme_password.getText().toString().trim();
                        String socketPort = cet_cme_socket_port.getText().toString().trim();
                        String httpPort = cet_cme_http_port.getText().toString().trim();
                        String livePort = cet_cme_live_port.getText().toString().trim();
                        String apiVersion = cet_cme_api_version.getText().toString().trim();
                        //获取当前类型  是2 的时候表示url链接 账号密码  端口可以为空
                        String typeDesc = cet_cme_start_type.getText().toString().trim();
                        LogUtils.e("dialog===新增设备===typeDesc000=" + typeDesc);
                        String channel = cet_cme_line_type.getText().toString().trim();

                        String deviceTypeHexNum = CommonUtil.getDeviceTypeHexNum(context, typeDesc);
                        String deviceTypeDecNum = CommonUtil.getDeviceTypeDecNum(context, typeDesc);


                        HashMap<String, String> mMap = new HashMap<>();
                        mMap.put("deviceName", deviceName);
                        mMap.put("deviceCode", deviceCode);
                        mMap.put("msgMark", msgMark);
                        mMap.put("liveIp", liveIp);
                        mMap.put("liveIpPublic", liveIpPublic);
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

    public void dismissDialog() {
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog, HashMap<String, String> mMap);

        /**
         * 修改设备的时候
         * 重新修改设备类型的回调
         */
        void onReInputTypeClick(TextView mType);


        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {
        }


        /**
         * 修改设备的时候
         * 工作模式的点击事件
         */
        void onReInputChannelClick();
    }


}