package com.company.shenzhou.mineui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.shenzhou.R;
import com.company.shenzhou.app.AppAdapter;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/5/27 17:57
 * desc：播放界面日志 adapter
 */
public final class LogAdapter extends AppAdapter<String> {
    private static final String TAG = "LogAdapter，界面==";

    private final Context mContext;

    public LogAdapter(Context context) {
        super(context);
        this.mContext = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LogAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        public TextView tv_log_data;

        private ViewHolder() {
            super(R.layout.item_vlc_log);
            tv_log_data = findViewById(R.id.tv_log_data);

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindView(int position) {
            String data = getItem(position);
            if (data.startsWith("TAG_TIME")) {
                int i = data.indexOf(" ");
                String substring = data.substring(i, data.length());
                int i3 = substring.indexOf("语");
                SpannableStringBuilder builder = new SpannableStringBuilder(substring);
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_9A9A9A));
                if (-1 != i3) {
                    builder.setSpan(foregroundColorSpan, 0, i3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    tv_log_data.setText(builder);
                } else {
                    tv_log_data.setText(data);
                }

            } else {
                tv_log_data.setText(data);
            }

        }
    }
}