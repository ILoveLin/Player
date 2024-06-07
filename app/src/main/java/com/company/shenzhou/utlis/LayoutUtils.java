package com.company.shenzhou.utlis;

import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/6/4 15:35
 * desc：线路3，两路流（镜下和术野）切换画面（修改布局，动画）的布局代码
 * //因为横竖屏切换之后，宽高会错乱，初始化的时候标识横竖屏的值
 * <p>
 * 竖屏：
 * 手机的左边是顶点，坐标是x=0，y=0，x向右是正，y向下是正
 *
 * 横屏：此时手机横屏摆放
 * 手机的左边是顶点，坐标是x=0，y=0，x向右是正，y向下是正
 *
 * 横屏：此时手机竖屏摆放，此时不做考虑
 * <p>
 * x向右是正，y向下是正
 */
public class LayoutUtils {
    private static final String TAG = "LayoutUtils==";


    /**
     * 竖屏
     * 设备view 移动到最顶端，术野View在底端
     *
     * @param deviceView     设备镜下直播View
     * @param elseView       术野直播View
     * @param mHalfShuHeight 初始化界面的时候，计算出来的值：竖屏下的百分之50值
     */
    public static void deviceView2TopLayout(TXCloudVideoView deviceView, TXCloudVideoView elseView, int mHalfShuHeight) {
        LogUtils.e(TAG + "deviceView2TopLayout==");
        LogUtils.e(TAG + "deviceView2TopLayout==" + mHalfShuHeight);
        RelativeLayout.LayoutParams mRelativeParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mRelativeParams.height = mHalfShuHeight;
        deviceView.setLayoutParams(mRelativeParams);
        elseView.setLayoutParams(mRelativeParams);
        deviceView.setX(0);
        deviceView.setY(0);
        elseView.setX(0);
        elseView.setY(mHalfShuHeight);

    }


    /**
     * 竖屏
     * 术野view 移动到最顶端，设备View在底端
     *
     * @param deviceView  设备镜下直播View
     * @param elseView    术野直播View
     * @param mHalfHeight 初始化界面的时候，计算出来的值：竖屏下的百分之50值
     */
    //竖屏，view 移动到最顶端
    public static void elseView2TopLayout(TXCloudVideoView deviceView, TXCloudVideoView elseView, int mHalfHeight) {
        LogUtils.e(TAG + "elseView2TopLayout==");
        LogUtils.e(TAG + "elseView2TopLayout==" + mHalfHeight);
        RelativeLayout.LayoutParams mRelativeParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mRelativeParams.height = mHalfHeight;
        deviceView.setLayoutParams(mRelativeParams);
        elseView.setLayoutParams(mRelativeParams);
        deviceView.setX(0);
        deviceView.setY(mHalfHeight);
        elseView.setX(0);
        elseView.setY(0);
    }


    /**
     * 横屏
     * 术野View，在最左边，最大（占比百分之70，设备的View占比百分之30）
     *
     * @param deviceView   设备镜下直播View
     * @param elseView     术野直播View
     * @param mHeng7Width  初始化界面的时候，计算出来的值：横屏下的百分之70值
     * @param mHeng3Width  初始化界面的时候，计算出来的值：横屏下的百分之30值
     * @param mHeng3Height 初始化界面的时候，计算出来的值：横屏下的百分之30值
     * @param mHalfHengYIndex  横屏之后，小屏幕需要对齐的Y值：所以需要计算：已知横屏后（长时竖屏高的百分之70，根据16：9）算出高x，  （然后屏幕高-x）/2-->求出横屏需要setY的值
     *                        初始化界面的时候，计算出来的值：横屏下的百分之30值
     */
    public static void elseView2BigLayout(TXCloudVideoView deviceView, TXCloudVideoView elseView, int mHeng7Width,int mHeng7Height,int mHeng3Width, int mHeng3Height,int mHalfHengYIndex) {
        LogUtils.e(TAG + "elseView2BigLayout==");
        LogUtils.e(TAG + "mHeng7Width==" + mHeng7Width);
        LogUtils.e(TAG + "mHeng3Width==" + mHeng3Width);
        LogUtils.e(TAG + "mHeng3Height==" + mHeng3Height);

        RelativeLayout.LayoutParams mRelativeParams1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mRelativeParams1.width = mHeng7Width;
        mRelativeParams1.height = mHeng7Height;
        RelativeLayout.LayoutParams mRelativeParams2 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mRelativeParams2.width = mHeng3Width;
        mRelativeParams2.height = mHeng3Height;
        deviceView.setLayoutParams(mRelativeParams1);
        elseView.setLayoutParams(mRelativeParams2);
        elseView.setX(mHeng7Width);
        elseView.setY(mHalfHengYIndex);
        //elseView.setY(0);
        deviceView.setX(0);
        deviceView.setY(mHalfHengYIndex);
        //deviceView.setY(0);


    }

    /**
     * 横屏
     * 设备View，在最左边，最大（占比百分之70，术野的View占比百分之30）
     *
     * @param deviceView   设备镜下直播View
     * @param elseView     术野直播View
     * @param mHeng7Width  初始化界面的时候，计算出来的值：横屏下的百分之70值
     * @param mHeng3Width  初始化界面的时候，计算出来的值：横屏下的百分之30值
     * @param mHeng3Height 初始化界面的时候，计算出来的值：横屏下的百分之30值
     */
    public static void deviceView2BigLayout(TXCloudVideoView deviceView, TXCloudVideoView elseView, int mHeng7Width,  int mHeng7Height,int mHeng3Width, int mHeng3Height,int mHalfHengYIndex) {
        LogUtils.e(TAG + "elseView2BigLayout==");
        LogUtils.e(TAG + "mHeng7Width==" + mHeng7Width);
        LogUtils.e(TAG + "mHeng3Width==" + mHeng3Width);
        LogUtils.e(TAG + "mHeng3Height==" + mHeng3Height);

        RelativeLayout.LayoutParams mRelativeParams1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mRelativeParams1.width = mHeng7Width;
        mRelativeParams1.height = mHeng7Height;
        RelativeLayout.LayoutParams mRelativeParams2 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mRelativeParams2.width = mHeng3Width;
        mRelativeParams2.height = mHeng3Height;
        elseView.setLayoutParams(mRelativeParams1);
        deviceView.setLayoutParams(mRelativeParams2);
        elseView.setX(0);
        elseView.setY(mHalfHengYIndex);
        //elseView.setY(0);
        deviceView.setX(mHeng7Width);
        deviceView.setY( mHalfHengYIndex);
        //deviceView.setY( 0);


    }
}
