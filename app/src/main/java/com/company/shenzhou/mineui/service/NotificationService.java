package com.company.shenzhou.mineui.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.company.shenzhou.R;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.utlis.LogUtils;
import com.tencent.mmkv.MMKV;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc：开启后台NotificationService    避免语音推流被杀死
 */
public class NotificationService extends Service {
    private static final String TAG = "NotificationService";

    //常驻通知Id,唯一标识号。
    private static final int notificationId = 10086;
    private MMKV mmkv;


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(TAG + "NotificationService===onCreate");
        mmkv = MMKV.defaultMMKV();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    //通知管理者
    private static NotificationManager mNotificationManager;

    private void showNotification() {
        // PendingIntent如果用户选择此通知，则启动我们的活动(PendingIntent可以看作是对Intent的一个封装，
        // 但它不是立刻执行某个行为，而是满足某些条件或触发某些事件后才执行指定的行为)
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,
        //因为不同模式,此处需要判断播放模式,点击后跳转Activity
        String mChannel = mmkv.decodeString(Constants.KEY_VLC_PLAYER_CHANNEL, Constants.PLAYER_CHANNEL2);
        String isRC200 = mmkv.decodeString(Constants.KEY_Device_Type_HexNum, "0");
        //线路1:(常规socket通讯,p2p);线路2:(http模式);频道3:(腾讯云);   //接口和扫码返回的是012，对应的是线路123，app会转换成123存储
        Intent intent = getIntent(mChannel, isRC200);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //通知C:\Users\Administrator\Desktop\jks
        Notification mNotification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //(1)8.0以上初始化通知
            //渠道名
            String CHANNEL_NAME = "CME Mic";
            // 渠道Id
            String CHANNEL_ID = "123456";
            NotificationChannel mChannel1 = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);//NotificationManager.IMPORTANCE_HIGH 渠道重要级
            mChannel1.enableLights(true);
            mChannel1.setLightColor(Color.RED);
            mChannel1.setShowBadge(true);
            mChannel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(mChannel1);
//            mNotificationManager.createNotificationChannel(mChannel);
            mNotification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(getString(R.string.notification_call)).setContentText(getString(R.string.notification_call_online)).setOngoing(true)//设置为ture，表示它为一个正在进行的通知。他们通常是用来表示 一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待
//                    // ,因此占用设备(如一个文件下载, 同步操作,主动网络连接)
                    .setSmallIcon(R.mipmap.icon_logo)//小图标（显示在状态栏）
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_logo))//大图标（显示在通知上）
                    .setContentIntent(pendingIntent)//设置内容意图
                    .build();

            LogUtils.e("NotificationService===显示通知==8.0以上↑↑↑↑↑↑");
            //(2)发送通知
        } else {
            //(1)8.0以下初始化通知
            mNotification = new Notification.Builder(this).setContentTitle(getString(R.string.notification_call)).setContentText(getString(R.string.notification_call_online)).setOngoing(true)//设置为ture，表示它为一个正在进行的通知。他们通常是用来表示 一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待
//                    // ,因此占用设备(如一个文件下载, 同步操作,主动网络连接)
                    .setSmallIcon(R.mipmap.icon_logo).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_logo)).setContentIntent(pendingIntent)//设置内容意图
                    .build();
            LogUtils.e("NotificationService===显示通知==8.0以下↓↓↓↓");
            //(2)发送通知
        }
        mNotificationManager.notify(notificationId, mNotification);
        startForeground(notificationId, mNotification);


    }

    /**
     * 获取相对于直播界面的intent
     */
    @Nullable
    private Intent getIntent(String mChannel, String isRC200) {
        Intent intent = null;
//        if (mChannel != null && mChannel.equals(Constants.PLAYER_CHANNEL2)) {//Channel==1:  p2p Nginx
//            //RC200默认 22
//            if ("22".equals(isRC200)) {
//                intent = new Intent(this, IjkPlayerRC200Activity.class);
//            } else {
//                intent = new Intent(this, VlcPlayerLine2Activity.class);
//            }
//        } else if (mChannel != null && mChannel.equals(Constants.PLAYER_CHANNEL1)) {//Channel==0:http 转播
//            //RC200默认 22
//            if ("22".equals(isRC200)) {
//                intent = new Intent(this, IjkPlayerRC200Activity.class);
//            } else {
//                intent = new Intent(this, VlcPlayerLine1Activity.class);
//
//            }
//        } else if (mChannel != null && mChannel.equals(Constants.PLAYER_CHANNEL3)) {//Channel==2:腾讯云
//            intent = new Intent(this, TencentLine3SDKActivity.class);
//        }
        return intent;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("NotificationService===onDestroy");
        if (null != mNotificationManager) {
            mNotificationManager.cancel(notificationId);
        }
        mmkv = null;

    }
}