package com.company.shenzhou.utlis;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/5/24 9:02
 * desc：
 */
public class ScreenSizeUtil {

    public static int getScreenWidth(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight();
    }


    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        final float scale = dm.density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dp(Context context, float pxValue) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        final float scale = dm.density;
        return (pxValue - 0.5f)/scale ;
    }


    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
