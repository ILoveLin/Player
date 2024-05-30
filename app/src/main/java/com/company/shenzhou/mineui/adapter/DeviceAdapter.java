package com.company.shenzhou.mineui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.shenzhou.R;
import com.company.shenzhou.app.AppAdapter;
import com.company.shenzhou.bean.dbbean.DeviceDBBean;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.utlis.LogUtils;
import com.company.shenzhou.widget.SwipeMenuLayout;

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

        public SwipeMenuLayout swipeMenuLay;
        public RelativeLayout linear_item;
        public TextView mDeviceName, mLine;
        public TextView mDeviceIpOrDDNSURL;
        public TextView mDeviceMark;
        public TextView mDeviceCode;
        public Button delBtn;
        public Button reInputBtn;
        public Button play_mode;
        public ImageView mImageChose;


        private ViewHolder() {
            super(R.layout.item_swipemenulayout_device);
            LogUtils.e(TAG + "==adapter====");
            mImageChose = findViewById(R.id.iv_current_chose_image);
            mDeviceName = findViewById(R.id.tv_video_title);
            mDeviceMark = findViewById(R.id.tv_video_make);
            mDeviceIpOrDDNSURL = findViewById(R.id.tv_video_type);
            mDeviceCode = findViewById(R.id.tv_current_device_code);
            mLine = findViewById(R.id.tv_line);

            swipeMenuLay = findViewById(R.id.swipeMenuLay);
            linear_item = findViewById(R.id.linear_item);
            delBtn = findViewById(R.id.delete_device);
            reInputBtn = findViewById(R.id.update_device);
            play_mode = findViewById(R.id.play_mode);
        }

        @Override
        public void onBindView(int position) {
            DeviceDBBean bean = getItem(position);
            if (null != bean) {
                LogUtils.e("adapter===bean====" + bean.toString());
                mDeviceName.setText("" + bean.getDeviceName());
                mLine.setVisibility(View.VISIBLE);
                if ("".equals(bean.getChannel()) || null == bean.getChannel()) {
                    mLine.setText("未选择");
                } else {
                    String str = bean.getChannel();
                    if (str.contains("1") || str.contains("一") || str.contains("one") || str.contains("ONE")) {
                        mLine.setText("" + mContext.getResources().getString(R.string.device_work_type_01));
                    } else if (str.contains("2") || str.contains("二") || str.contains("tow") || str.contains("TOW")) {
                        mLine.setText("" + mContext.getResources().getString(R.string.device_work_type_02));
                    } else if (str.contains("3") || str.contains("三") || str.contains("three") || str.contains("THREE")) {
                        mLine.setText("" + mContext.getResources().getString(R.string.device_work_type_03));
                    }

                }
                mDeviceMark.setText(mContext.getResources().getString(R.string.device_mark) + ":" + bean.getMsgMark());
                if (null == bean.getIp() || "".equals(bean.getIp())) {
                    //ip 为空的时候，显示DDNSAddress
                    mDeviceIpOrDDNSURL.setText("DDNS:" + bean.getDDNSURL());

                } else {
                    mDeviceIpOrDDNSURL.setText("IP:" + bean.getIp());
                }

                if (null == bean.getDeviceCode() || "".equals(bean.getDeviceCode())) {
                    mDeviceCode.setText("ID:" + mContext.getResources().getString(R.string.device_kong));

                } else {
                    mDeviceCode.setText("ID:" + bean.getDeviceCode());
                }
                String typeDesc = bean.getDeviceTypeDesc();
                switch (typeDesc) {
                    case Constants.Type_Operation_YiTiJi://手术一体机
                        mImageChose.setImageResource(R.drawable.icon_operation_yitiji);
                        break;
                    case Constants.Type_HD3://HD3
                        mImageChose.setImageResource(R.drawable.icon_hd3);
                        break;
                    case Constants.Type_HD3_4K://HD3-4K
                        mImageChose.setImageResource(R.drawable.icon_hd3_4k);
                        break;
                    case Constants.Type_RC200://RC200
                        mImageChose.setImageResource(R.drawable.icon_rc200);
                        break;
                    case Constants.Type_V1_YiTiJi: //智能一体机
                        mImageChose.setImageResource(R.drawable.icon_smart_yitiji);
                        break;
                    case Constants.Type_EarNoseTable: //耳鼻喉治疗台
                        mImageChose.setImageResource(R.drawable.icon_erbihou);
                        break;
                    case Constants.Type_FuKeTable: //妇科治疗台
                        mImageChose.setImageResource(R.drawable.icon_fuke);
                        break;
                    case Constants.Type_MiNiaoTable: //泌尿治疗台
                        mImageChose.setImageResource(R.drawable.icon_miniao);
                        break;
                    case Constants.Type_Work_Station_EN: //工作站
                        mImageChose.setImageResource(R.drawable.icon_workstation);
                        break;
                    case Constants.Type_Custom_Url: //神州转播
                        mLine.setVisibility(View.INVISIBLE);
                        mImageChose.setImageResource(R.drawable.ic_cme_bg_shenzhou_zhuanbo);
                        break;
                }

            } else {
                mDeviceMark.setText("null啦~~~~");

            }

        }
    }
}