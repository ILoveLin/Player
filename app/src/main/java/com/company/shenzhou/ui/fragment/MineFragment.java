package com.company.shenzhou.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.view.View;

import com.company.shenzhou.aop.SingleClick;
import com.company.shenzhou.app.TitleBarFragment;
import com.company.shenzhou.ui.dialog.InputDialog;
import com.company.shenzhou.ui.dialog.MessageDialog;
import com.company.shenzhou.R;
import com.company.shenzhou.ui.activity.AboutActivity;
import com.company.shenzhou.ui.activity.BrowserActivity;
import com.company.shenzhou.ui.activity.DialogActivity;
import com.company.shenzhou.ui.activity.GuideActivity;
import com.company.shenzhou.ui.activity.HomeActivity;
import com.company.shenzhou.ui.activity.ImagePreviewActivity;
import com.company.shenzhou.ui.activity.ImageSelectActivity;
import com.company.shenzhou.ui.activity.LoginActivity;
import com.company.shenzhou.ui.activity.PasswordForgetActivity;
import com.company.shenzhou.ui.activity.PasswordResetActivity;
import com.company.shenzhou.ui.activity.PersonalDataActivity;
import com.company.shenzhou.ui.activity.PhoneResetActivity;
import com.company.shenzhou.ui.activity.RegisterActivity;
import com.company.shenzhou.ui.activity.SettingActivity;
import com.company.shenzhou.ui.activity.StatusActivity;
import com.company.shenzhou.ui.activity.VideoPlayActivity;
import com.company.shenzhou.ui.activity.VideoSelectActivity;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 我的 Fragment
 */
public final class MineFragment extends TitleBarFragment<HomeActivity> {

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mineold_fragment;
    }

    @Override
    protected void initView() {
        setOnClickListener(R.id.btn_mine_dialog, R.id.btn_mine_hint, R.id.btn_mine_login, R.id.btn_mine_register, R.id.btn_mine_forget,
                R.id.btn_mine_reset, R.id.btn_mine_change, R.id.btn_mine_personal, R.id.btn_mine_setting, R.id.btn_mine_about,
                R.id.btn_mine_guide, R.id.btn_mine_browser, R.id.btn_mine_image_select, R.id.btn_mine_image_preview,
                R.id.btn_mine_video_select, R.id.btn_mine_video_play, R.id.btn_mine_crash, R.id.btn_mine_pay);
    }

    @Override
    protected void initData() {

    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_mine_dialog) {

            startActivity(DialogActivity.class);

        } else if (viewId == R.id.btn_mine_hint) {

            startActivity(StatusActivity.class);

        } else if (viewId == R.id.btn_mine_login) {

            startActivity(LoginActivity.class);

        } else if (viewId == R.id.btn_mine_register) {

            startActivity(RegisterActivity.class);

        } else if (viewId == R.id.btn_mine_forget) {

            startActivity(PasswordForgetActivity.class);

        } else if (viewId == R.id.btn_mine_reset) {

            startActivity(PasswordResetActivity.class);

        } else if (viewId == R.id.btn_mine_change) {

            startActivity(PhoneResetActivity.class);

        } else if (viewId == R.id.btn_mine_personal) {

            startActivity(PersonalDataActivity.class);

        } else if (viewId == R.id.btn_mine_setting) {

            startActivity(SettingActivity.class);

        } else if (viewId == R.id.btn_mine_about) {

            startActivity(AboutActivity.class);

        } else if (viewId == R.id.btn_mine_guide) {

            startActivity(GuideActivity.class);

        } else if (viewId == R.id.btn_mine_browser) {

            new InputDialog.Builder(getAttachActivity())
                    .setTitle("跳转到网页")
                    .setContent("https://www.jianshu.com/u/f7bb67d86765")
                    .setHint("请输入网页地址")
                    .setConfirm(getString(R.string.common_confirm))
                    .setCancel(getString(R.string.common_cancel))
                    .setListener((dialog, content) -> BrowserActivity.start(getAttachActivity(), content))
                    .show();

        } else if (viewId == R.id.btn_mine_image_select) {

            ImageSelectActivity.start(getAttachActivity(), new ImageSelectActivity.OnPhotoSelectListener() {

                @Override
                public void onSelected(List<String> data) {
                    toast("选择了" + data.toString());
                }

                @Override
                public void onCancel() {
                    toast("取消了");
                }
            });

        } else if (viewId == R.id.btn_mine_image_preview) {

            ArrayList<String> images = new ArrayList<>();
            images.add("https://www.baidu.com/img/bd_logo.png");
            images.add("https://avatars1.githubusercontent.com/u/28616817");
            ImagePreviewActivity.start(getAttachActivity(), images, images.size() - 1);

        } else if (viewId == R.id.btn_mine_video_select) {

            VideoSelectActivity.start(getAttachActivity(), new VideoSelectActivity.OnVideoSelectListener() {

                @Override
                public void onSelected(List<VideoSelectActivity.VideoBean> data) {
                    toast("选择了" + data.toString());
                }

                @Override
                public void onCancel() {
                    toast("取消了");
                }
            });

        } else if (viewId == R.id.btn_mine_video_play) {

            new VideoPlayActivity.Builder()
                    .setVideoTitle("速度与激情特别行动")
                    .setVideoSource("http://vfx.mtime.cn/Video/2019/06/29/mp4/190629004821240734.mp4")
                    .setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    .start(getAttachActivity());

        } else if (viewId == R.id.btn_mine_crash) {

            // 上报错误到 Bugly 上
            CrashReport.postCatchedException(new IllegalStateException("are you ok?"));
            // 关闭 Bugly 异常捕捉
            CrashReport.closeBugly();
            throw new IllegalStateException("are you ok?");

        } else if (viewId == R.id.btn_mine_pay) {

            new MessageDialog.Builder(getAttachActivity())
                    .setTitle("捐赠")
                    .setMessage("如果你觉得这个开源项目很棒，希望它能更好地坚持开发下去，可否愿意花一点点钱（推荐 10.24 元）作为对于开发者的激励")
                    .setConfirm("支付宝")
                    .setCancel(null)
                    //.setAutoDismiss(false)
                    .setListener(dialog -> {
                        BrowserActivity.start(getAttachActivity(), "https://github.com/getActivity/Donate");
                        toast("AndroidProject 因为有你的支持而能够不断更新、完善，非常感谢支持！");
                        postDelayed(() -> {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("alipays://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2FFKX04202G4K6AVCF5GIY66%3F_s%3Dweb-other"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                toast("打开支付宝失败，你可能还没有安装支付宝客户端");
                            }
                        }, 2000);
                    })
                    .show();
        }
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}