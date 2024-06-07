package com.company.shenzhou.mineui.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.company.shenzhou.R;
import com.company.shenzhou.app.AppActivity;
import com.company.shenzhou.utlis.LogUtils;
import com.hjq.widget.view.ClearEditText;
import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : 可进行拷贝的副本
 */
public final class PingActivity extends AppActivity {
    private static final String TAG = "PingActivity，界面==";
    private ClearEditText mAddress;
    private TextView mPingTimes;
    private TextView mPingAddress;
    private TextView mPacketLost;
    private TextView mAverageTime;
    private TextView mMinTime;
    private TextView mMaxTime;
    private StringBuffer stringBuffer;
    private TextView mDetailContentView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ping;
    }

    @Override
    protected void initView() {
        mAddress = findViewById(R.id.et_ip);
        mDetailContentView = findViewById(R.id.tv_detail_content);
        mPingAddress = findViewById(R.id.ip_adress);
        mPingTimes = findViewById(R.id.times);
        mPacketLost = findViewById(R.id.packet_lost);
        mAverageTime = findViewById(R.id.average_time);
        mMinTime = findViewById(R.id.min_time);
        mMaxTime = findViewById(R.id.max_time);

        setOnClickListener(R.id.btn_start_ping, R.id.tv_detail);
    }

    @Override
    protected void initData() {
        stringBuffer = new StringBuffer();
        mDetailContentView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_start_ping) {
            initDefaultData();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Ping.onAddress(mAddress.getText().toString()).setTimeOutMillis(1000).setTimes(5).doPing(new Ping.PingListener() {
                            @Override
                            public void onResult(PingResult pingResult) {
                                stringBuffer.append(pingResult);
                                LogUtils.e(TAG + "Ping:pingResult==:" + pingResult);

                            }

                            @Override
                            public void onFinished(PingStats pingStats) {
                                stringBuffer.append("onFinished:").append(pingStats);
                                LogUtils.e(TAG + "Ping:onFinished==:" + pingStats);
                                runOnUiThread(new Runnable() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void run() {
                                        mPingAddress.setText(getResources().getString(R.string.ping_ip_address) + pingStats.getAddress());
                                        mPingTimes.setText(getResources().getString(R.string.ping_no_pings) + pingStats.getNoPings());
                                        mPacketLost.setText(getResources().getString(R.string.ping_packet_lost) + pingStats.getPacketsLost());
                                        mAverageTime.setText(getResources().getString(R.string.ping_average_time_taken) + pingStats.getAverageTimeTaken());
                                        mMinTime.setText(getResources().getString(R.string.ping_min_time_taken) + pingStats.getMinTimeTaken());
                                        mMaxTime.setText(getResources().getString(R.string.ping_max_time_taken) + pingStats.getMinTimeTaken());
                                        mDetailContentView.setText(stringBuffer.toString());

                                    }
                                });
                            }

                            @Override
                            public void onError(Exception e) {
                                LogUtils.e(TAG + "Ping:onError==" + e);
                                stringBuffer.append("onError:").append(e);
                                runOnUiThread(new Runnable() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void run() {
                                        mPingAddress.setText(getResources().getString(R.string.ping_ip_address) + mAddress.getText().toString());
                                        mPingTimes.setText(getResources().getString(R.string.ping_no_pings) + 5);
                                        mPacketLost.setText(getResources().getString(R.string.ping_packet_lost) + 5);
                                        mAverageTime.setText(getResources().getString(R.string.ping_average_time_taken) + 0);
                                        mMinTime.setText(getResources().getString(R.string.ping_min_time_taken) + 0);
                                        mMaxTime.setText(getResources().getString(R.string.ping_max_time_taken) + 0);
                                        mDetailContentView.setText(stringBuffer.toString());
                                    }
                                });

                            }
                        });
                    } catch (Exception e) {
                        stringBuffer.append("onError,Exception:").append(e);
                        throw new RuntimeException(e);
                    }
                }
            }.start();
        } else if (id == R.id.tv_detail) {
            if (mDetailContentView.getVisibility() == View.GONE) {
                mDetailContentView.setVisibility(View.VISIBLE);
            } else {
                mDetailContentView.setVisibility(View.GONE);
            }
            LogUtils.e(TAG + "stringBuffer==" + stringBuffer.toString());


        }
    }

    @SuppressLint("SetTextI18n")
    private void initDefaultData() {
        mPingAddress.setText(getResources().getString(R.string.ping_ip_address) + getResources().getString(R.string.ping_testing));
        mPingTimes.setText(getResources().getString(R.string.ping_no_pings) + getResources().getString(R.string.ping_testing));
        mPacketLost.setText(getResources().getString(R.string.ping_packet_lost) + getResources().getString(R.string.ping_testing));
        mAverageTime.setText(getResources().getString(R.string.ping_average_time_taken) + getResources().getString(R.string.ping_testing));
        mMinTime.setText(getResources().getString(R.string.ping_min_time_taken) + getResources().getString(R.string.ping_testing));
        mMaxTime.setText(getResources().getString(R.string.ping_max_time_taken) + getResources().getString(R.string.ping_testing));
        mDetailContentView.setText(getResources().getString(R.string.ping_testing));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}