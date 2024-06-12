package com.company.shenzhou.mineui.activity;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.company.shenzhou.R;
import com.company.shenzhou.app.AppActivity;
import com.company.shenzhou.bean.line.LiveLine3SteamPullBean;
import com.company.shenzhou.bean.line.LiveLine3SteamPushBean;
import com.company.shenzhou.bean.line.OperationParamsBean;
import com.company.shenzhou.bean.line.QueryBean;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.global.EnumConfig;
import com.company.shenzhou.mineui.adapter.LogAdapter;
import com.company.shenzhou.mineui.dialog.InputMicDialog;
import com.company.shenzhou.mineui.service.NotificationService;
import com.company.shenzhou.ui.dialog.MessageDialog;
import com.company.shenzhou.utlis.CalculateUtils;
import com.company.shenzhou.utlis.CommonUtil;
import com.company.shenzhou.utlis.CoreUtil;
import com.company.shenzhou.utlis.FileUtil;
import com.company.shenzhou.utlis.JsonUtil;
import com.company.shenzhou.utlis.LogUtils;
import com.company.shenzhou.utlis.ScreenSizeUtil;
import com.company.shenzhou.utlis.SharePreferenceUtil;
import com.company.shenzhou.utlis.SystemUtil;
import com.company.shenzhou.widget.MarqueeTextView;
import com.company.shenzhou.widget.vlc.ENDownloadView;
import com.company.shenzhou.widget.vlc.ENPlayView;
import com.company.shenzhou.widget.vlc.PlayerViewLine3;
import com.google.gson.Gson;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.pedro.encoder.input.audio.MicrophoneMode;
import com.pedro.rtplibrary.rtmp.RtmpOnlyAudio;
import com.tencent.live2.V2TXLiveDef;
import com.tencent.live2.V2TXLivePlayer;
import com.tencent.live2.V2TXLivePlayerObserver;
import com.tencent.live2.impl.V2TXLivePlayerImpl;
import com.tencent.mmkv.MMKV;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : TencentSDK-线路3-WebRTC
 */
public final class PlayerLine3Activity extends AppActivity implements  View.OnClickListener,ConnectCheckerRtmp {
    private static final String TAG = "线路3(SDK),界面==";
    //苹果提供的测试源（点播）
    public String path = "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8";
    private int mPlayState = EnumConfig.PlayState.STATE_STOP;        //视频是否播放的标识符,默认STOP
    private int mRecordTag = EnumConfig.RecodeState.UN_RECODE;       //是否在录像的标识,默认未录像=录像中
    //TCPlayer,第一次走错误回调，是否，重新拉流，默认=false=没有重新拉流过，true=onError之后，重新拉流过了一次，最多冲新拉取2此
    private int OnError2RestartSteam = 0;
    private TextView mRecordView, mChooseLiveUrlView, mVlvErrorTextView;
    private TextView mPhotosView, mBottomTime, mTvMicStatus, mSnapShot, mMicOnLineLayout;
    private MarqueeTextView mTitle;//跑马灯
    private ImageView mBottomVideoFull;
    //数据源
    private String mTypeText = "";//高清
    private RtmpOnlyAudio mRtmpOnlyAudio;
    private TextView mPlayerViewRootTextView;
    private View.OnTouchListener mOnTouchVideoListener;
    private LinearLayout mControlTopLayout, mControlRightLayout;
    private ENPlayView mPlayView;
    private ENDownloadView mVlvLoadingView;
    private boolean isStarting = true;
    private ImageView mBack, mLockView;
    private String mUrlType;
    //默认的内网播放地址  mUrl01
    private String mUrl01, mUrl02, mSocketPort, mDeviceTypeHexNum, mMicName, mSocketOrLiveIP, mCurrentReceiveDeviceCode, mTitleData, mApiVersion;
    private String mIp;
    private Gson mGson;
    private MMKV mMmkv;
    private String mHttpPort;
    private TextView mRootView;
    private String mCurrentTypeNum;    //手术一体机(0B)  的数字描述:0B ;时候 不管是广播还是语音聊天都能听得到声音 默认全部打开
    private String mCurrentTypeDesc;   //手术一体机(0B)  的中文描述:手术一体机
    private long sendCount = 0;
    private long currentIndex = 0;
    private Boolean userRecordTag;
    private Boolean userShotTag;
    private String mPhoneDeviceCode;
    private Intent mIntent;
    private ImageView mHaveVoiceType;
    private NetworkUtils.OnNetworkStatusChangedListener onNetworkStatusChangedListener;
    private ImageView mSetting;
    private String mBeanIP;
    private RecyclerView mAnimRecycleView;
    private LinearLayout mAnimLinearLayout;
    private int mScreenHeight;
    private TextView mAnimTopLayout;
    private LogAdapter mAdapter;
    private ArrayList<String> mLogDataList = new ArrayList<>();
    private ImageView mAnimClearLayout;
    private TextView mAnimEmptyLayout;
    private RelativeLayout mControlBottomLayout;
    private LinearLayout mControlLeftLayout;
    private RelativeLayout mRelativeAll;
    private PlayerViewLine3 mPlayerView;
    //腾讯播放器SDK的播放的View
    private TXCloudVideoView mRenderView;
    //腾讯播放器SDK的播放器
    private V2TXLivePlayer mTencentLivePlayer;
    //腾讯播放器SDK的播放器的回调监听
    //录像, 老徐手机 录像地址-内部存储/Pictures/
    private File recordFile = new File(Environment.getExternalStorageDirectory(), "CME");
    private String filePath = recordFile.getAbsolutePath();
    //动画执行时间
    private static final int CONTROLLER_HIDE_DELAY = 5500;
    //全屏(展开=0),半屏(收缩=1),默认==半屏(收缩=1)
    private int mCurrPageType;
    //时间定时器的订阅,刷新
    private Disposable mPlayerTimeDis;
    private String mRoomNumber;       //当前直播画面的对于的
    private V2TXLiveDef.V2TXLiveLocalRecordingParams recordingParams;
    private boolean mAnimLayoutIsGone = true;     //日志界面动画,true=关闭 false=打开,默认关闭 true
    private String PlayMode = Constants.PlayMode.Normal;//播放模式：默认方式：常规模式
    private String mCurrentMicUrl;
    private String mHttpPath = "";
    /**
     * 开始播放(备选方案)
     * 固定播放地址:-->deviceID是上位机的设备ID
     */
    private boolean mSparePlan;                           //是否开启备用方案, 默认未开启==false
    private String mSpareMicPushSteam = "";               //备用语音推流地址
    private String mSpareLiveSteam = "";                  //备用直播拉流地址
    //private int reTryConnectTime = 0;                     //手动解决，腾讯云SDK，过多重连，造成App不适的字段
    private String mDeviceTypeDecNum;

    private static final int SHOW_TIME = 100;
    private static final int Pusher_Start = 101;
    private static final int Pusher_Stop = 102;
    private static final int Record_Start = 103;
    private static final int Record_Stop = 104;
    private static final int MicConnect_SocketSuccessLogo = 105;
    private static final int Send_Toast = 106;
    private static final int Send_UrlType = 107;
    private static final int Type_Loading_Visible = 108;
    private static final int Type_Loading_InVisible = 109;
    private static final int Show_UrL_Type = 110;
    private static final int MicConnect_SocketErrorLogo = 119;
    private static final int Restart_load_Steam = 117;   //语音通话的时候直播流断了,需要从新加载视频流然后再告诉上位机可以音频拉流
    private static final int PlayerStatue_ShowLoading = 200;   //显示加载框
    private static final int PlayerStatue_HideLoading = 201;   //隐藏加载框状态
    private static final int PlayerStatue_Playing = 202;   //播放状态
    private static final int PlayerStatue_Error = 203;   //错误状态
    private static final int PlayerStart = 205;   //错误状态
    private int CMEAudioType = Constants.CMEAudio.CMEAudioNormal;      //语音连接状态的标识:默认0不做任何操作

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PlayerStart:
                    mBottomTime.setText(msg.obj + "");
                    break;
                case SHOW_TIME:
                    mBottomTime.setText(msg.obj + "");
                    break;
                case Restart_load_Steam:
                    sendRequest2Operation("3", "0");
                    mMicOnLineLayout.setVisibility(VISIBLE);
                    //再次请求上位机voiceID
                    break;
                case Send_Toast://toast 提示
                    toast(msg.obj + "");
                    break;
                case MicConnect_SocketErrorLogo:
                    mTvMicStatus.setText(getResources().getString(R.string.vlc_error));
                    mTvMicStatus.setTextColor(getResources().getColor(R.color.color_007AFF));
                    Drawable errorStart = getResources().getDrawable(R.drawable.icon_mic_error);
                    mTvMicStatus.setCompoundDrawablesWithIntrinsicBounds(null, errorStart, null, null);
                    break;
                case MicConnect_SocketSuccessLogo:
                    mTvMicStatus.setTag("stopStream");
                    mTvMicStatus.setText(getResources().getString(R.string.vlc_open));
                    mTvMicStatus.setTextColor(getResources().getColor(R.color.white));
                    Drawable error = getResources().getDrawable(R.drawable.icon_mic_nor);
                    mTvMicStatus.setCompoundDrawablesWithIntrinsicBounds(null, error, null, null);
                    break;
                case Pusher_Start:
                    if (Objects.equals(PlayMode, Constants.PlayMode.Normal)) {
                        //申请语音通话的时候，才开始轮训查询语音通话表
                        startMicStatueQuery2sQueue();
                    }
                    mTvMicStatus.setTag("startStream");
                    mTvMicStatus.setText(getResources().getString(R.string.vlc_close));
                    //避免语音推流被杀死
                    mIntent = new Intent(PlayerLine3Activity.this, NotificationService.class);
                    startService(mIntent);
                    mTvMicStatus.setTextColor(getResources().getColor(R.color.color_007AFF));
                    Drawable topstart = getResources().getDrawable(R.drawable.icon_mic_pre);
                    mTvMicStatus.setCompoundDrawablesWithIntrinsicBounds(null, topstart, null, null);
                    addLogData2Refresh(true, "语音发送数据");
                    sendRequest2Operation("1", "0");
                    changeAudioStatus(Constants.CMEAudio.CMEAudioJoinAudioList);
                    addLogData2Refresh(false, "Operation=1--> 手机设备发送消息：请求加入列表");
                    break;
                case Pusher_Stop:
                    mTvMicStatus.setTag("stopStream");
                    mTvMicStatus.setText(getResources().getString(R.string.vlc_open));
                    NotificationService.StopNotification();
                    mTvMicStatus.setTextColor(getResources().getColor(R.color.white));
                    Drawable topend = getResources().getDrawable(R.drawable.icon_mic_nor);
                    mTvMicStatus.setCompoundDrawablesWithIntrinsicBounds(null, topend, null, null);
                    stopMicSteam();
                    sendRequest2Operation("6", "0");
                    addLogData2Refresh(true, "语音发送数据");
                    addLogData2Refresh(false, "Operation=6--> 手机设备发送消息：请求从列表中删除");
                    changeAudioStatus(Constants.CMEAudio.CMEAudioNormal);
                    break;
                case Record_Start:
                    setTextColor(getResources().getColor(R.color.colorAccent), getResources().getString(R.string.vlc_video), false);
                    Drawable record_start = getResources().getDrawable(R.drawable.icon_record_pre);
                    mRecordView.setCompoundDrawablesWithIntrinsicBounds(null, record_start, null, null);
                    break;
                case Record_Stop:
                    setTextColor(getResources().getColor(R.color.white), getResources().getString(R.string.vlc_video), true);
                    Drawable record_end = getResources().getDrawable(R.drawable.icon_record_nore);
                    mRecordView.setCompoundDrawablesWithIntrinsicBounds(null, record_end, null, null);
                    break;
                case Send_UrlType:
                    mChooseLiveUrlView.setText(mTypeText + "");
                    Drawable urlTypeSD = getResources().getDrawable(R.drawable.icon_url_type_sd);
                    Drawable urlTypeHD = getResources().getDrawable(R.drawable.icon_url_type_hd);
                    if (getResources().getString(R.string.vlc_HD).equals(mTypeText)) {
                        mChooseLiveUrlView.setCompoundDrawablesWithIntrinsicBounds(null, urlTypeHD, null, null);
                    } else if (getResources().getString(R.string.vlc_SD).equals(mTypeText)) {
                        mChooseLiveUrlView.setCompoundDrawablesWithIntrinsicBounds(null, urlTypeSD, null, null);
                    }
                    break;
                case Show_UrL_Type:   //切换清晰度
                    startSendToast(getResources().getString(R.string.line3_no_choice));
                    break;
                case Type_Loading_Visible:   //加载框 可见
                    mVlvLoadingView.setVisibility(VISIBLE);
                    mVlvLoadingView.start();
                    break;
                case Type_Loading_InVisible: //隐藏 加载框
                    mVlvLoadingView.setVisibility(View.INVISIBLE);
                    mVlvLoadingView.release();
                    break;
                case PlayerStatue_ShowLoading: //显示加载框
                    showPlayerStatue(EnumConfig.PlayerState.PLAYER_STATUE_SHOW_LOADING);
                    break;
                case PlayerStatue_HideLoading: //隐藏加载框状态
                    showPlayerStatue(EnumConfig.PlayerState.PLAYER_STATUE_HIDE_LOADING);
                    break;
                case PlayerStatue_Playing: //播放状态
                    showPlayerStatue(EnumConfig.PlayerState.PLAYER_STATUE_SHOW_PLAYING);
                    break;
                case PlayerStatue_Error: //错误状态
                    showPlayerStatue(EnumConfig.PlayerState.PLAYER_STATUE_SHOW_ERROR);
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_player_line3;
    }

    @Override
    protected void initView() {
        //设置沉浸式观影模式体验
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //永远不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mScreenHeight = ScreenSizeUtil.getScreenHeight(PlayerLine3Activity.this);
        mTypeText = getResources().getString(R.string.vlc_HD);
        mMmkv = MMKV.defaultMMKV();
        mGson = new Gson();
        //腾讯播放器SDK的手势view
        mPlayerView = findViewById(R.id.player_tencent);
        //腾讯播放器SDK播放View
        mRenderView = mPlayerView.findViewById(R.id.play_tx_cloud_view);
        mTvMicStatus = findViewById(R.id.pusher_mic);
        mMicOnLineLayout = findViewById(R.id.tv_tag_mic_online_statue);
        mTitle = findViewById(R.id.tv_top_title);
        mBottomTime = findViewById(R.id.tv_current_time);
        mHaveVoiceType = findViewById(R.id.iv_voice_type);
        mBottomVideoFull = findViewById(R.id.iv_bottom_video_full);
        mPhotosView = findViewById(R.id.photos);
        mRecordView = findViewById(R.id.recordStart);
        mChooseLiveUrlView = findViewById(R.id.change_live);
        mRelativeAll = findViewById(R.id.activity_vlc_player);
        //top 控制布局
        mControlTopLayout = findViewById(R.id.layout_top);
        //bottom 控制布局
        mControlBottomLayout = findViewById(R.id.layout_control_bottom);
        //right 控制布局
        mControlRightLayout = findViewById(R.id.linear_contral);
        //left 控制布局
        mControlLeftLayout = findViewById(R.id.layout_control_left);
        mLockView = findViewById(R.id.iv_left_lock);

        //错误提示
        mVlvErrorTextView = findViewById(R.id.error_text);
        mVlvErrorTextView.setVisibility(View.INVISIBLE);
        //加载的loading
        mVlvLoadingView = findViewById(R.id.loading);
        //点击重新加载的view
        mPlayView = findViewById(R.id.start);
        mSnapShot = findViewById(R.id.snapShot);
        mBack = findViewById(R.id.back);
        mSetting = findViewById(R.id.iv_right_setting);
        mAnimRecycleView = findViewById(R.id.mAnimRecycleView);
        mAnimLinearLayout = findViewById(R.id.anim_linear);
        mAnimTopLayout = findViewById(R.id.anim_tv_log_top);
        mAnimClearLayout = findViewById(R.id.anim_clear_log);
        mAnimEmptyLayout = findViewById(R.id.anim_empty);
        //触摸控制亮度和声音,是否可触摸开关
        mRootView = mPlayerView.getRootView();
        mOnTouchVideoListener = mPlayerView.getOnTouchVideoListener();

        mUrl01 = getIntent().getStringExtra("url01");
        mUrl02 = getIntent().getStringExtra("url02");
        mBeanIP = getIntent().getStringExtra("beanIP");
        mUrlType = getIntent().getStringExtra("urlType");
        mTitleData = getIntent().getStringExtra("mTitle");
        mApiVersion = getIntent().getStringExtra("apiVersion");
        mIp = getIntent().getStringExtra("ip");
        mSparePlan = getIntent().getBooleanExtra("sparePlan", false);
        mSpareLiveSteam = getIntent().getStringExtra("spareLiveSteam");
        mSpareMicPushSteam = getIntent().getStringExtra("spareMicPushSteam");
        mMicName = mMmkv.decodeString(Constants.KEY_MIC_Name, "07");
        mDeviceTypeHexNum = mMmkv.decodeString(Constants.KEY_Device_Type_HexNum, "07");
        mDeviceTypeDecNum = mMmkv.decodeString(Constants.KEY_Device_Type_DecNum, "07");
        mSocketPort = mMmkv.decodeString(Constants.KEY_Device_SocketPort, "8005");
        mHttpPort = mMmkv.decodeString(Constants.KEY_Device_HttpPort, "7001");
        mSocketOrLiveIP = mMmkv.decodeString(Constants.KEY_Device_Ip, "192.168.130.102");
        //通过此字段判断设备类型
        mCurrentTypeNum = mMmkv.decodeString(Constants.KEY_Device_Type_HexNum, "07");
        mCurrentTypeDesc = mMmkv.decodeString(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_V1_YiTiJi));
//        mCurrentReceiveDeviceCode = "b0087fc6fa584b62";
        mCurrentReceiveDeviceCode = mMmkv.decodeString(Constants.KEY_DeviceCode, "937a5f204dc43a14");
        mPhoneDeviceCode = mMmkv.decodeString(Constants.KEY_PhoneDeviceCode, CalculateUtils.getPhoneDeviceCode());
        String phoneDeviceCode = CalculateUtils.getPhoneDeviceCode();
        //RC200 才显示跳转设置ip界面
        LogUtils.e(TAG + "==初始化=====================================");
        LogUtils.e(TAG + "==初始化==mIp==:" + mIp);
        LogUtils.e(TAG + "==初始化==mUrlType==:" + mUrlType);
        LogUtils.e(TAG + "==初始化==mSocketOrLiveIP==:" + mSocketOrLiveIP);
        LogUtils.e(TAG + "==初始化==mSocketPort==:" + mSocketPort);
        LogUtils.e(TAG + "==初始化==mHttpPort==:" + mHttpPort);
        LogUtils.e(TAG + "==初始化==mDeviceTypeNum==:" + mDeviceTypeHexNum);
        LogUtils.e(TAG + "==初始化==mCurrentTypeDesc==:" + mCurrentTypeDesc);
        LogUtils.e(TAG + "==初始化==mCurrentReceiveDeviceCode==:" + mCurrentReceiveDeviceCode);
        LogUtils.e(TAG + "==初始化==mPhoneDeviceCode==:" + mPhoneDeviceCode);
        LogUtils.e(TAG + "==初始化==mPhoneDeviceCode=工具类=:" + phoneDeviceCode);
        LogUtils.e(TAG + "==初始化==mUrl01====" + mUrl01);
        LogUtils.e(TAG + "==初始化==mUrl02====" + mUrl02);
        LogUtils.e(TAG + "==初始化==mSparePlan====" + mSparePlan);
        LogUtils.e(TAG + "==初始化==mSpareLiveSteam====" + mSpareLiveSteam);
        LogUtils.e(TAG + "==初始化==mSpareMicPushSteam====" + mSpareMicPushSteam);
        mRtmpOnlyAudio = new RtmpOnlyAudio(this);
        //解决语音卡顿问题
        mRtmpOnlyAudio.setMicrophoneMode(MicrophoneMode.SYNC);
        //发送起始隐藏布局动画
        mRelativeAll.postDelayed(mHideControllerRunnable, CONTROLLER_HIDE_DELAY);
        //设置播放样式
        setPageType(EnumConfig.PageType.SHRINK);
    }

    @Override
    protected void initData() {
        mTitle.setText("" + mTitleData);
        //设置,播放模式
        if (mSparePlan) {
            PlayMode = Constants.PlayMode.Spare;
            mTitle.setText("备用方案已开启：" + mSpareLiveSteam);
        } else {
            PlayMode = Constants.PlayMode.Normal;
        }
        //初始化的时候默认把日志布局移除屏幕之外
        showCloseLogLayoutAnim("true");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlayerLine3Activity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAnimRecycleView.setLayoutManager(linearLayoutManager);
        mAdapter = new LogAdapter(this);
        mAnimRecycleView.setAdapter(mAdapter);
        mRootView.setLongClickable(true);  //手势需要--能触摸
        mRootView.setOnTouchListener(mOnTouchVideoListener);
        //初始化腾讯WebRTC播放器
        initTencentPlayer();
        //发送请求获取推流地址，进行播放
        sendGetSteamRequest2Play();
        responseListener();

    }


    private void initTencentPlayer() {
        /**
         * V2TXLivePlayer播放器使用地址
         * 文档地址 https://cloud.tencent.com/document/product/454/56045
         * V2TXLiveFillModeFill 【默认值】: 图像铺满屏幕，不留黑边，如果图像宽高比不同于屏幕宽高比，部分画面内容会被裁剪掉。
         *  V2TXLiveFillModeFit: 图像适应屏幕，保持画面完整，但如果图像宽高比不同于屏幕宽高比，会有黑边的存在。
         *  V2TXLiveFillModeScaleFill: 图像拉伸铺满，因此长度和宽度可能不会按比例变化。
         */
        if (null == mTencentLivePlayer) {
            mTencentLivePlayer = new V2TXLivePlayerImpl(PlayerLine3Activity.this);
        } else {
            mTencentLivePlayer.stopPlay();
        }
        //自动模式
        mTencentLivePlayer.setCacheParams(1.0f, 5.0f);
        //极速模式
        //mTencentLivePlayer.setCacheParams(1.0f, 1.0f);
        //流畅模式
        //mTencentLivePlayer.setCacheParams(5.0f, 5.0f);
        //设置完成之后再启动播放
        mTencentLivePlayer.setObserver(new V2TXLivePlayerObserver() {

            //错误回调：推流器出现错误时，会回调该通知
            @Override
            public void onError(V2TXLivePlayer player, int code, String msg, Bundle extraInfo) {

                LogUtils.e(TAG + "播放器回调-->错误回调: player-" + player + ", extraInfo-" + extraInfo);
                //code==-6，请求服务器超时  -8断开连接
//                reTryConnectTime = 0;
                mPlayState = EnumConfig.PlayState.STATE_STOP;
                if (null != mPlayerTimeDis) {
                    mPlayerTimeDis.dispose();
                    mPlayerTimeDis = null;
                    showPlayerTime("00:00");
                }

                if (CMEAudioType == Constants.CMEAudio.CMEAudioConnected) {
                    //直播断了这个方法只走一次,所以这里发送消息,发送socket消息,先断开通话,再移除列表
                    LogUtils.e(TAG + "播放器回调-->播放器回调-Error==此时,phone会主动发送消息,移除语音通讯列表");
                    sendRequest2Operation("5", "0");
                    mHandler.sendEmptyMessage(Pusher_Stop);
                }
                mHandler.sendEmptyMessage(PlayerStatue_Error);
            }


            //连接成功
            @Override
            public void onConnected(V2TXLivePlayer player, Bundle extraInfo) {
                super.onConnected(player, extraInfo);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //开启播放器时间显示
                        createPlayerTimeSub();
                        mPlayState = EnumConfig.PlayState.STATE_PLAY;
                        mHandler.sendEmptyMessage(PlayerStatue_Playing);
                    }
                });
            }

            //播放状态
            @Override
            public void onVideoPlaying(V2TXLivePlayer player, boolean firstPlay, Bundle extraInfo) {
                mPlayState = EnumConfig.PlayState.STATE_PLAY;

            }

            //截图回调
            @Override
            public void onSnapshotComplete(V2TXLivePlayer player, Bitmap image) {
                super.onSnapshotComplete(player, image);
                LogUtils.e(TAG + "播放器回调-->截图-->: player-" + player + " Bitmap-" + image);
                startSendToast(getResources().getString(R.string.vlc_toast05));

                MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), image, "IMG" + Calendar.getInstance().getTime(), null);

            }

            //开始录像
            @Override
            public void onLocalRecordBegin(V2TXLivePlayer player, int code, String storagePath) {
                super.onLocalRecordBegin(player, code, storagePath);
                LogUtils.e(TAG + "播放器回调-->开始录像-->: player-" + player + " code-" + code + " storagePath-" + storagePath);
                startSendToast(getResources().getString(R.string.line3_start_record));

            }

            //录像中
            @Override
            public void onLocalRecording(V2TXLivePlayer player, long durationMs, String storagePath) {
                super.onLocalRecording(player, durationMs, storagePath);
                LogUtils.e(TAG + "播放器回调-->录像中: player-" + player + ", durationMs-" + durationMs + ", storagePath-" + storagePath);

            }

            //录像完成
            @Override
            public void onLocalRecordComplete(V2TXLivePlayer player, int code, String storagePath) {
                super.onLocalRecordComplete(player, code, storagePath);
                LogUtils.e(TAG + "播放器回调-->录像完成-->: player-" + player + " code-" + code + " storagePath-" + storagePath);
                FileUtil.RefreshAlbum(storagePath, true, PlayerLine3Activity.this);
                startSendToast(getResources().getString(R.string.vlc_toast09));


            }
            //onLocalRecordBegin

            //直播播放器分辨率变化通知
            @Override
            public void onVideoResolutionChanged(V2TXLivePlayer player, int width, int height) {
                LogUtils.e(TAG + "播放器回调-->直播播放器分辨率变化通知: player-" + player + " width-" + width + " height-"
                        + height);
            }

            //警告状态
            @Override
            public void onWarning(V2TXLivePlayer v2TXLivePlayer, int i, String s, Bundle bundle) {
                LogUtils.e(TAG + "播放器回调-->警告状态: player-" + v2TXLivePlayer + ", i-" + i + ", s-" + s);

            }

            //自定义视频渲染回调
            @Override
            public void onRenderVideoFrame(V2TXLivePlayer player, V2TXLiveDef.V2TXLiveVideoFrame v2TXLiveVideoFrame) {
                super.onRenderVideoFrame(player, v2TXLiveVideoFrame);
                LogUtils.e(TAG +
                        "播放器回调-->自定义视频渲染回调: player-" + player + ", v2TXLiveVideoFrame-" + v2TXLiveVideoFrame);
            }
        });
        mTencentLivePlayer.setRenderView(mRenderView);
        mTencentLivePlayer.setRenderFillMode(V2TXLiveDef.V2TXLiveFillMode.V2TXLiveFillModeFit);
        //创建，录像的参数
        recordingParams = new V2TXLiveDef.V2TXLiveLocalRecordingParams();
        //音视频都录制
        recordingParams.recordMode = V2TXLiveDef.V2TXLiveRecordMode.V2TXLiveRecordModeBoth;
        //录制信息更新频率，单位毫秒，有效范围：1000-10000。-1表示不用回调
        recordingParams.interval = 5000;


    }


    /**
     * 轮训查询语音通话表
     * <p>
     * 开启2s轮训,一直http查询,查询语音通讯表的状态
     * 根据条件判断Operation的值,做相对于的语言交互通话操作
     * 操作:
     */
    private Disposable mDisposable2s;

    private void startMicStatueQuery2sQueue() {
        if (null == mDisposable2s) {
            LogUtils.e(TAG + "==2s轮询开启,查询语音通话表");
            mDisposable2s = Observable
                    .interval(3, TimeUnit.SECONDS)//定时器操作符，这里三秒打印一个log
                    //取消任务时取消定时唤醒
                    .doOnDispose(() -> {

                    })
                    .subscribe(count -> {
                        //LogUtils.e(TAG + "==轮询:第-" + count + "-次查询语音通话表");
                        startCheckMicStatueRequest();
                    });
        }

    }

    //1.4,开始查询-语音表状态
    private void startCheckMicStatueRequest() {
        OkHttpUtils.get()
                .url(Constants.Live_audioQuery + "?plf=" + Constants.Type_A1_DECIMAL + "" + "&ver=" + mApiVersion)
                .addParams("receiveId", mPhoneDeviceCode)//接收端ID,b0087fc6fa584b62,7babc3216f199968
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e(TAG + "==查询语音通讯表==onError==e:" + e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // LogUtils.e(TAG + "==查询语音通讯表==response:" + response);
                        if (JsonUtil.parseJson2CheckCode(response)) {
                            QueryBean queryBean = mGson.fromJson(response, QueryBean.class);
                            //根据条件判断Operation的值,做相对于的语言交互通话操作
                            doMicOperation(queryBean);
                        } else {
                            //LogUtils.e(TAG + "==查询语音通讯表==onResponse==code=1");
                            startSendToast(getResources().getString(R.string.line3_check_mic_code_fail));

                        }


                    }
                });
    }

    /**
     * 根据条件判断Operation的值,做相对于的语言交互通话操作
     *
     * @param QueryBean
     */
    private void doMicOperation(QueryBean QueryBean) {
        String deviceID = mCurrentReceiveDeviceCode; //上位机设备码
        String phoneID = mPhoneDeviceCode; //手机设备码
        List<com.company.shenzhou.bean.line.QueryBean.DataDTO> dataList = QueryBean.getResult().getData();
        if (null != dataList && 0 != dataList.size()) {
            //1,先排序,根据ID从小到大排序
            Collections.sort(dataList, new Comparator<QueryBean.DataDTO>() {
                @Override
                public int compare(QueryBean.DataDTO o1, QueryBean.DataDTO o2) {
                    return o1.getID() - o2.getID(); //升序
                    //return o2.getID() - o1.getID(); //降序
                }
            });
            //2,for循环查询,是我当前手机用户的对象值
            for (int i = 0; i < dataList.size(); i++) {
                QueryBean.DataDTO bean = dataList.get(i);
                //LogUtils.e(TAG + "==查询语音通讯表==for循环查询==bean==" + bean.toString());
                String sendID = bean.getSendID();       //发送端(上位机发送,所以是上位机设备ID)
                String receiveID = bean.getReceiveID(); //接收端(手机端接收,所以是手机设备ID)
                if (deviceID.equalsIgnoreCase(sendID) && receiveID.equalsIgnoreCase(phoneID)) {
                    String operation = bean.getOperation();
                    int operationData = Integer.parseInt(operation);
                    String errCode = bean.getErrCode();
                    //LogUtils.e(TAG + "==查询语音通讯表==for循环查询==是我操作的对象==当前operation状态:" + operation);
                    //错误代码 0：成功 1：上传音频流到Nginx失败 2: Nginx服务未启动 3：从Nginx取音频流失败
                    if ("1".equals(errCode)) {
                        startSendToast(getResources().getString(R.string.line3_uploading_to_nginx_failed));
                        return;
                    } else if ("2".equals(errCode)) {
                        startSendToast(getResources().getString(R.string.line3_nginx_service_not_started));
                        return;
                    }
                    if ("3".equals(errCode)) {
                        startSendToast(getResources().getString(R.string.line3_error_from_nginx));
                        return;
                    }

                    switch (operation) {
                        //上位机的operation 值的意思
                        //0：表示不做任何事情                忽略
                        //1: 请求加入列表（功能开启）         忽略
                        //2：请上传音频流到Nginx；
                        //3：请从Nginx拉取音频流
                        //4：上位机告知手机用户：语音已连接
                        //5：通话结束
                        //6：请求从列表中删除（功能关闭）
                        //CMEAudioNormal,         //默认状态，啥也没点
                        //CMEAudio JoinAudioList, //加入语音列表
                        //CMEAudioGetPushUzl,     //获取推流地址
                        //CMEAudioPushing,        //推流中
                        //CMEAudioPushSuccess,    //推流成功
                        //CMEAudioConnected,      //两端语音已连接
                        //CMEAudioHangUp,         //挂断
                        case "2":
                            addLogData2Refresh(false, "Operation=2--> 手机设备接收到数据：请上传音频流到Nginx");
                            changeAudioStatus(Constants.CMEAudio.CMEAudioGetPushUzl);
                            break;
                        case "4": //上位机,告知：已连接成功
                            addLogData2Refresh(false, "Operation=4" + "--> 手机设备接收到数据：上位机告知手机用户：语音已连接成功；");
                            changeAudioStatus(Constants.CMEAudio.CMEAudioConnected);
                            break;
                        case "5": //上位机,本地挂断
                            addLogData2Refresh(false, "Operation=5" + "--> 手机设备接收到数据：上位机主动挂断语音；(ps:通话结束)");
                            changeAudioStatus(Constants.CMEAudio.CMEAudioHangUp);
                            break;
                        case "6": //上位机,删除列表
                            addLogData2Refresh(false, "Operation=6--> 手机设备接收到数据：请求从列表中删除");
                            changeAudioStatus(Constants.CMEAudio.CMEAudioNormal);
                            break;
                    }
                }

            }
        } else {
            //LogUtils.e(TAG + "==查询语音通讯表==for循环查询==dataList==null或者==0==直接忽略此消息!");

        }

    }

    /**
     * @param currentOperation 自己定义的操作步骤
     */
    public void changeAudioStatus(int currentOperation) {
        if (currentOperation == CMEAudioType) {
            addLogData2Refresh(false, "自定义操作步骤--> 相同步骤：直接stopSteam，恢复加入语音列表状态");
            return;
        }
        if (Objects.equals(PlayMode, Constants.PlayMode.Normal)) {
            if (currentOperation - 1 != CMEAudioType) {
                stopMicSteam();
                addLogData2Refresh(false, "自定义操作步骤--> 不是正常操作步骤：直接stopSteam，恢复加入语音列表状态");
                CMEAudioType = Constants.CMEAudio.CMEAudioJoinAudioList;
            }
        }
        CMEAudioType = currentOperation;
        switch (currentOperation) {
            //上位机的
            //CMEAudioNormal,         //默认状态，啥也没点
            //CMEAudioJoinAudioList,  //加入语音列表
            //CMEAudioGetPushUzl,     //获取推流地址
            //CMEAudioPushing,        //推流中
            //CMEAudioConnected,      //已连接
            //CMEAudioHangUp,         //挂断
            case Constants.CMEAudio.CMEAudioNormal:
                mMicOnLineLayout.setVisibility(INVISIBLE);
                break;
            case Constants.CMEAudio.CMEAudioJoinAudioList://请求获取推流地址,然后开启推流
                break;
            case Constants.CMEAudio.CMEAudioGetPushUzl:
                sendRequest2PushMicSteam();
                addLogData2Refresh(false, "自定义操作步骤-->获取推流地址，获取推流地址-- http请求获取推流地址");
                break;
            case Constants.CMEAudio.CMEAudioPushing:
                mRtmpOnlyAudio.prepareAudio(256 * 1024, 44100, true, false, false);
                mRtmpOnlyAudio.startStream(mCurrentMicUrl);
                addLogData2Refresh(false, "自定义操作步骤-->推流中，mCurrentMicUrl-->" + mCurrentMicUrl);
                mMicOnLineLayout.setVisibility(VISIBLE);
                mMicOnLineLayout.setText(getResources().getString(R.string.line_on_connecting));
                break;
            case Constants.CMEAudio.CMEAudioPushSuccess:
                mMicOnLineLayout.setVisibility(VISIBLE);
                mMicOnLineLayout.setText(getResources().getString(R.string.line_on_connecting));
                break;
            case Constants.CMEAudio.CMEAudioConnected:
                startSendToast(getResources().getString(R.string.vlc_toast11));
                mMicOnLineLayout.setVisibility(VISIBLE);
                mMicOnLineLayout.setText(getResources().getString(R.string.line_on_the_phone));
                break;
            case Constants.CMEAudio.CMEAudioHangUp:
                stopMicSteam();
                //关闭显示状态
                mMicOnLineLayout.setVisibility(INVISIBLE);
                addLogData2Refresh(false, "自定义操作步骤-->挂断语音");
                changeAudioStatus(Constants.CMEAudio.CMEAudioJoinAudioList);
                break;
        }
    }


    /**
     * 1.3
     * 操作语音通讯表---类似于socket,通讯
     * Operation:操作
     * 0：表示不做任何事情
     * 1: 请求加入列表（功能开启）
     * 2：请上传音频流到Nginx；
     * 3：请从Nginx拉取音频流
     * 5：通话结束
     * 6：请求从列表中删除（功能关闭）
     */
//参数为json
    private void sendRequest2Operation(String Operation, String errorCode) {
        if (Objects.equals(PlayMode, Constants.PlayMode.Spare)) {
            return;
        }
        OperationParamsBean.ExtraDataDTO bean = new OperationParamsBean.ExtraDataDTO();
        bean.setName(mMicName + "_" + SystemUtil.getDeviceBrand());
        OperationParamsBean mBean = new OperationParamsBean();
        mBean.setSendID(mPhoneDeviceCode);
        mBean.setSendType(Constants.Type_A1_DECIMAL + "");
        mBean.setOperation(Operation);
        mBean.setReceiveID(mCurrentReceiveDeviceCode);
        mBean.setExtraData(bean);
        mBean.setErrCode(errorCode);////错误代码 0：成功 1：上传音频流到Nginx失败 2: Nginx服务未启动 3：从Nginx取音频流失败
        String str = mGson.toJson(mBean);
        LogUtils.e(TAG + "==手机主动查询---开始发送http请求,data==:" + str);
        if ("1".equalsIgnoreCase(Operation) || "5".equalsIgnoreCase("5")) {
            addLogData2Refresh(false, "   Json：   " + str);
        }
        OkHttpUtils.postString()
                .url(Constants.Live_audioControl + "?plf=" + Constants.Type_A1_DECIMAL + "" + "&ver=" + mApiVersion)
                .content(str)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e(TAG + "==手机主动查询---操作语音通讯表==请求--错误==e:" + e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.e(TAG + "==手机主动查询---操作语音通讯表==请求--response==" + response);

                        if (JsonUtil.parseJson2CheckCode(response)) {
                            LogUtils.e(TAG + "==手机主动查询---操作语音通讯表==请求--成功:" + response);
                        } else {
                            LogUtils.e(TAG + "==手机主动查询---操作语音通讯表==请求--失败==code=1");
                        }

                    }
                });
    }


    /**
     * 1.1
     * 获取---语音----推流地址
     */
    private void sendRequest2PushMicSteam() {
        LogUtils.e(TAG + "==开始==获取---语音----推流地址");
        LogUtils.e(TAG + "==开始==获取---语音----pusherNumber==" + mPhoneDeviceCode);
        LogUtils.e(TAG + "==开始==获取---语音----pusherplatform==" + Constants.Type_A1_DECIMAL + "");
        LogUtils.e(TAG + "==开始==获取---语音----deviceNumber==" + mCurrentReceiveDeviceCode);
        LogUtils.e(TAG + "==开始==获取---语音----roomNumber==" + mRoomNumber);
        OkHttpUtils.get()
                .url(Constants.Live_Line3_PushUrl + "?plf=" + Constants.Type_A1_DECIMAL + "" + "&ver=" + mApiVersion)
                .addParams("pusherNumber", mPhoneDeviceCode)//移动端ID
                .addParams("pusherplatform", Constants.Type_A1_DECIMAL + "")//推流平台(十进制)
                .addParams("deviceNumber", mCurrentReceiveDeviceCode)//上位机ID（设备码）
                .addParams("roomNumber", mRoomNumber + "")//推流房间号（在拉流地址接口获取到的）
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e(TAG + "==获取---语音----推流地址==onError==e:" + e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.e(TAG + "==获取---语音----推流地址==response:" + response);
                        if (JsonUtil.parseJson2CheckCode(response)) {
                            LiveLine3SteamPushBean micBean = mGson.fromJson(response, LiveLine3SteamPushBean.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (0 == micBean.code) {
                                        mCurrentMicUrl = micBean.result.pushUrl;
                                        if (!mRtmpOnlyAudio.isStreaming()) {//false 表示还未开启推流
                                            if (mRtmpOnlyAudio.prepareAudio()) {
                                                changeAudioStatus(Constants.CMEAudio.CMEAudioPushing);
                                            }
                                        } else {
                                            addLogData2Refresh(false, "Operation=2--> 手机设备接收到数据：已经开启语音推流.....请勿重复推送mCurrentMicUrl：" + mCurrentMicUrl);
                                            LogUtils.e(TAG + "==获取---语音:,已经开启语音推流.....请勿重复推送");
                                        }
                                    } else {
                                        LogUtils.e(TAG + "==获取---语音----推流地址==onResponse==code=1");

                                    }
                                }
                            });

                        } else {
                            LogUtils.e(TAG + "==手机主动查询---操作语音通讯表==请求--失败==code=1");

                        }


                    }
                });
    }

    /**
     * 1.2
     * 获取当前---直播画面---流地址--获取拉流地址
     * 并且播放
     */
    private void sendGetSteamRequest2Play() {
        //获取直播画面播放
        if (Constants.PlayMode.Normal.equals(PlayMode)) {//常规模式
            startPlay();
        } else {//备用模式
            if (null != mDisposable2s) {
                if (null != mDisposable2s) {
                    mDisposable2s.dispose();
                    mDisposable2s = null;
                }
            }
            //开启备用方案
            //备用方案直接拉流和推流，不管其他
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeAudioStatus(Constants.CMEAudio.CMEAudioNormal);
                    mRecordView.setVisibility(VISIBLE);
                    mHandler.sendEmptyMessage(PlayerStatue_ShowLoading);
                    LogUtils.e(TAG + "==备用方案直播画面流地址==mSpareLiveSteam:" + mSpareLiveSteam);
                    mTencentLivePlayer.startLivePlay(mSpareLiveSteam);
                }
            });
        }

    }


    /**
     * 开始播放
     * 获取当前播放地址
     */
    private void startPlay() {
        mHandler.sendEmptyMessage(PlayerStatue_ShowLoading);
        LogUtils.e(TAG + "==获取当前直播画面流地址==pusherClientId==:" + mCurrentReceiveDeviceCode);
        LogUtils.e(TAG + "==获取当前直播画面流地址==mDeviceTypeNum==:" + mDeviceTypeHexNum);
        LogUtils.e(TAG + "==获取当前直播画面流地址==deviceNumber==:" + mCurrentReceiveDeviceCode);
        LogUtils.e(TAG + "==获取当前直播画面流地址==plf==:" + Constants.Type_A1_DECIMAL + "");
        LogUtils.e(TAG + "==获取当前直播画面流地址==ver==:" + mApiVersion);
        mTitle.setText(mTitleData + "");
        OkHttpUtils.get()
                .url(Constants.Live_line3_PullUrl + "?plf=" + Constants.Type_A1_DECIMAL + "" + "&ver=" + mApiVersion)
                .addParams("deviceNumber", mCurrentReceiveDeviceCode)//上位机设备的ID
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e(TAG + "==获取当前直播画面流地址==onError==e:" + e);
                        mHandler.sendEmptyMessage(PlayerStatue_Error);
                        startSendToast(getResources().getString(R.string.line3_failed_to_obtain_the_current_live_stream_address));

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.e(TAG + "==获取当前直播画面流地址==response:" + response);
                        if (JsonUtil.parseJson2CheckCode(response)) {
                            LiveLine3SteamPullBean liveBean = mGson.fromJson(response, LiveLine3SteamPullBean.class);
                            List<LiveLine3SteamPullBean.ResultDTO.PullUrlsDTO> pullUrls = liveBean.result.pullUrls;
                            mRoomNumber = liveBean.result.roomNumber;
                            for (int i = 0; i < pullUrls.size(); i++) {
                                //http和扫码的设备类型是，10进制
                                String pusherPlatform = pullUrls.get(i).pusherPlatform;
                                LogUtils.e(TAG + "==获取当前直播画面流地址==json的设备类型pusherPlatform:" + pusherPlatform);
                                LogUtils.e(TAG + "==获取当前直播画面流地址==当前手机存储的设备的设备类型mDeviceTypeDecNum:" + mDeviceTypeDecNum);
                                if (CalculateUtils.sameDevice(pusherPlatform, mDeviceTypeDecNum)) {
                                    mHttpPath = pullUrls.get(i).pullUrl;
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mRecordView.setVisibility(VISIBLE);
                                    LogUtils.e(TAG + "==获取当前直播画面流地址==mHttpPath:" + mHttpPath);
                                    LogUtils.e(TAG + "==获取当前直播画面流地址==mHttpPath:" + mHttpPath);
                                    if ("".equals(mHttpPath)) {
                                        mHandler.sendEmptyMessage(PlayerStatue_Error);
                                    } else {
                                        mTencentLivePlayer.startLivePlay(mHttpPath);
                                        mTitle.setText("" + mHttpPath);
                                    }
                                }
                            });
                        } else {//当前设备未分配推流地址
                            startSendToastDelayed("当前设备未分配推流地址", 3500);
                            mHandler.sendEmptyMessageDelayed(PlayerStatue_Error, 3000);
                        }
                    }
                });
    }


/**
 *********************************控制面板动画和全屏相关*********************开始***********
 */

    /**
     * 设置播放器:全屏/半屏,显示
     * Js 交互解决手机全屏的画面显示不全
     * 全屏传true,竖屏传false
     */

    public void setVideoWindowType() {
        int orientation = getRequestedOrientation();
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //竖屏
            setPageType(EnumConfig.PageType.SHRINK);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
            setPageType(EnumConfig.PageType.EXPAND);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void responseListener() {
        mBottomVideoFull.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mRecordView.setOnClickListener(this);
        mChooseLiveUrlView.setOnClickListener(this);
        mLockView.setOnClickListener(this);
        mSnapShot.setOnClickListener(this);
        mPhotosView.setOnClickListener(this);
        mPlayView.setOnClickListener(this);
        mTvMicStatus.setOnClickListener(this);
        mSetting.setOnClickListener(this);
        mAnimTopLayout.setOnClickListener(this);
        mAnimClearLayout.setOnClickListener(this);

        //注册网络断开的监听
        registerNetWorkConnectionReceiver();
        mPlayerViewRootTextView = mPlayerView.findViewById(R.id.web_view_onclick);
        /**
         * 点击响应触摸事件,显示/隐藏控制布局
         */
        mPlayerViewRootTextView.setOnClickListener((View v) -> {
            // 先移除之前发送的
            mRelativeAll.removeCallbacks(mShowControllerRunnable);
            mRelativeAll.removeCallbacks(mHideControllerRunnable);
            //LogUtils.e(TAG + "==点击响应触摸事件=:点击响应触摸事件");
            if (mControllerShow) {
                // 隐藏控制面板
                mRelativeAll.post(mHideControllerRunnable);
            } else {
                // 显示控制面板
                mRelativeAll.post(mShowControllerRunnable);
                mRelativeAll.postDelayed(mHideControllerRunnable, CONTROLLER_HIDE_DELAY);
            }
        });


    }


    private void showPlayerStatue(int statue) {
        switch (statue) {
            case EnumConfig.PlayerState.PLAYER_STATUE_SHOW_LOADING:  //显示加载框
                //mTencentPlayerView.setVisibility(INVISIBLE);
                mPlayView.setVisibility(View.INVISIBLE);
                mVlvErrorTextView.setVisibility(View.INVISIBLE);
                mVlvLoadingView.setVisibility(VISIBLE);
                mVlvLoadingView.start();
                break;
            case EnumConfig.PlayerState.PLAYER_STATUE_HIDE_LOADING:  //隐藏加载框状态
                //全部隐藏
                mVlvLoadingView.release();
                mRenderView.setVisibility(VISIBLE);
                mPlayView.setVisibility(INVISIBLE);
                mVlvLoadingView.setVisibility(INVISIBLE);
                mVlvErrorTextView.setVisibility(INVISIBLE);

                break;
            case EnumConfig.PlayerState.PLAYER_STATUE_SHOW_PLAYING:    //播放状态
                mRenderView.setVisibility(VISIBLE);
                mVlvLoadingView.setVisibility(View.INVISIBLE);
                mPlayView.setVisibility(View.INVISIBLE);
                mVlvErrorTextView.setVisibility(View.INVISIBLE);
                mVlvLoadingView.release();

                break;
            case EnumConfig.PlayerState.PLAYER_STATUE_SHOW_ERROR:   //错误状态
                mRenderView.setVisibility(INVISIBLE);
                mVlvLoadingView.release();
                mVlvLoadingView.setVisibility(View.INVISIBLE);
                mPlayView.setVisibility(VISIBLE);
                mVlvErrorTextView.setVisibility(VISIBLE);
                break;

        }
    }

    /**
     * 点击事件
     *
     * @param view
     */
    @SuppressLint("NewApi")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start://开始播放
                sendGetSteamRequest2Play();
                break;
            case R.id.anim_tv_log_top://点击关闭动画
                showCloseLogLayoutAnim("else");
                break;
            case R.id.anim_clear_log://清空日志
                if (mLogDataList.size() == 0) {
                    startSendToast(getResources().getString(R.string.device_dialog_no_data_clear_log));
                    return;
                }
                showClearLogDialog();
                break;
            case R.id.iv_right_setting://RC200 设置ip界面,和打开调试日志界面
                showStartLogLayoutAnim();
                if ("20".equals(mCurrentTypeNum)) {
                    Intent intent1 = new Intent(PlayerLine3Activity.this, PlayerRC200Activity.class);
                    startActivity(intent1);
                } else {

                }
                break;
            case R.id.back://返回
                if (CMEAudioType == Constants.CMEAudio.CMEAudioConnected) {
                    sendRequest2Operation("5", "0");
                }
                if (null != mRtmpOnlyAudio) {
                    sendRequest2Operation("6", "0");
                    stopMicSteam();
                    LogUtils.e(TAG + "==返回:---> ====onClick=====");
                }
                mHandler.sendEmptyMessage(Pusher_Stop);
                //销毁Tencent播放器
                if (mTencentLivePlayer != null) {
                    if (mPlayState == EnumConfig.PlayState.STATE_PLAY) {
                        mTencentLivePlayer.stopPlay();
                    }
                    mTencentLivePlayer = null;
                }
                finish();
                break;
            case R.id.pusher_mic:  //推流
                checkMicName();
                mMicName = mMmkv.decodeString(Constants.KEY_MIC_Name, "");
                break;
            case R.id.photos:  //打开相册
                if (mPlayState == EnumConfig.PlayState.STATE_PLAY) {
                    if (mRecordTag == EnumConfig.RecodeState.RECODE) { //如果录像则关闭录像

                    }
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivity(intent);
                } else {
                    startSendToast(getResources().getString(R.string.vlc_toast02));
                }
                break;
            case R.id.iv_left_lock:  //锁屏
                if (mLockMode) {
                    unlock();
                } else {
                    lock();
                }
                break;
            case R.id.iv_bottom_video_full: //切换全屏
                setVideoWindowType();
                break;

            case R.id.change_live: //切换清晰度
                if (!"7".equals(mUrlType)) {
                    mHandler.sendEmptyMessage(Show_UrL_Type);
                } else {
                    startSendToast(getResources().getString(R.string.vlc_toast03));
                }
                break;
            case R.id.recordStart: //录像
                getStoragePermission(EnumConfig.PlayerOperation.RECORD);
                break;
            case R.id.snapShot://截图
                getStoragePermission(EnumConfig.PlayerOperation.SCREENSHOT);
                break;
        }
    }

    /**
     * 开始截图  开始录像
     *
     * @param type
     */
    private void getStoragePermission(int type) {
        userRecordTag = (Boolean) SharePreferenceUtil.get(PlayerLine3Activity.this, Constants.Sp_Record_Tag, false);
        userShotTag = (Boolean) SharePreferenceUtil.get(PlayerLine3Activity.this, Constants.Sp_Shot_Tag, false);
        XXPermissions.with(this)
                // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
//                .permission(Permission.READ_PHONE_STATE)
//                .permission(Permission.READ_PHONE_STATE)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            if (type == EnumConfig.PlayerOperation.RECORD) {//录像
                                if (mPlayState == EnumConfig.PlayState.STATE_PLAY) {
                                    if (!userRecordTag) {
                                        SharePreferenceUtil.put(PlayerLine3Activity.this, Constants.Sp_Record_Tag, true);
                                        startSendToast(getResources().getString(R.string.vlc_toast17));
                                    } else {
                                        if (isStarting && mTencentLivePlayer.isPlaying() == 1) {
                                            mRecordTag = EnumConfig.RecodeState.RECODE;
                                            mHandler.sendEmptyMessage(Record_Start);
                                            //vlcVideoView.getMediaPlayer().record(directory);
                                            LogUtils.e(TAG + "开始录像");
                                            String nowTimeString = TimeUtils.getNowString().replace(" ", "-").replace(":", "-");
                                            recordingParams.filePath = filePath + "/" + nowTimeString + ".mp4";
                                            LogUtils.e(TAG + "==录像截图功能:recordingParams.filePath ===" + recordingParams.filePath);
                                            mTencentLivePlayer.startLocalRecording(recordingParams);
                                        } else if (mRecordTag == EnumConfig.RecodeState.RECODE) {
                                            vlcRecordOver();
                                        }
                                    }
                                } else {
                                    startSendToast(getResources().getString(R.string.vlc_toast04));
                                }
                            } else {//截图
                                if (mPlayState == EnumConfig.PlayState.STATE_PLAY) {
                                    if (!userShotTag) {
                                        SharePreferenceUtil.put(PlayerLine3Activity.this, Constants.Sp_Shot_Tag, true);
                                        startSendToast(getResources().getString(R.string.vlc_toast18));
                                    } else {
                                        if (mPlayState == EnumConfig.PlayState.STATE_PLAY && mTencentLivePlayer.isPlaying() == 1) {
                                            mTencentLivePlayer.snapshot();
                                        }
                                    }
                                } else {
                                    startSendToast(getResources().getString(R.string.vlc_toast06));
                                }
                            }

                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            startSendToast(getResources().getString(R.string.device_toast02));

                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(getApplicationContext(), permissions);
                        } else {
                            startSendToast(getResources().getString(R.string.device_toast03));

                        }
                    }
                });
    }

    /**
     * 录像结束
     */
    private void vlcRecordOver() {
        mRecordTag = EnumConfig.RecodeState.UN_RECODE;
        mHandler.sendEmptyMessage(Record_Stop);
        startSendToast(getResources().getString(R.string.vlc_toast09));
        mTencentLivePlayer.stopLocalRecording();
        LogUtils.e(TAG + "==录像截图功能:结束录像===mTencentLivePlayer.stopLocalRecording();");

    }

    /**
     * 创建播放器时间显示定时器
     */
    private void createPlayerTimeSub() {
        //currentTime为当前时间的格式化显示,为字符串类型
        if (null == mPlayerTimeDis) {
            mPlayerTimeDis = Observable
                    .interval(1, TimeUnit.SECONDS)
                    //取消任务时取消定时唤醒
                    .doOnDispose(() -> {
                    })
                    .subscribe(count -> {
                        String currentTime = CoreUtil.secToTime(Integer.parseInt(count + ""));
                        showPlayerTime(currentTime);

                    });
        } else {
            mPlayerTimeDis.dispose();
            mPlayerTimeDis = null;
            mPlayerTimeDis = Observable
                    .interval(1, TimeUnit.SECONDS)
                    //取消任务时取消定时唤醒
                    .doOnDispose(() -> {
                    })
                    .subscribe(count -> {
                        String currentTime = CoreUtil.secToTime(Integer.parseInt(count + ""));
                        showPlayerTime(currentTime);

                    });
        }
    }

    //handler消息,显示播放器时间
    private void showPlayerTime(String data) {
        Message timeMessage = mHandler.obtainMessage();
        timeMessage.what = SHOW_TIME;
        timeMessage.obj = data;
        mHandler.sendMessage(timeMessage);

    }

    /**
     * 注册网络监听
     */
    private void registerNetWorkConnectionReceiver() {
        onNetworkStatusChangedListener = new NetworkUtils.OnNetworkStatusChangedListener() {
            @Override
            public void onDisconnected() {
                startSendToast(getResources().getString(R.string.line3_network_disconnection));
                LogUtils.e(TAG + "==网络监测:断开连接了");
                //正在语音通话--->请求关闭通话
                if (CMEAudioType == Constants.CMEAudio.CMEAudioConnected && mRtmpOnlyAudio.isStreaming()
                        || CMEAudioType == Constants.CMEAudio.CMEAudioPushSuccess && mRtmpOnlyAudio.isStreaming()) {
                    sendRequest2Operation("5", "0");
                    mHandler.sendEmptyMessage(Pusher_Stop);

                }
                mHandler.sendEmptyMessage(PlayerStatue_Error);
            }

            @Override
            public void onConnected(NetworkUtils.NetworkType networkType) {
                startSendToast(getResources().getString(R.string.line3_the_network_has_been_restored));
                LogUtils.e(TAG + "==网络监测:NetworkType:" + networkType);
                sendGetSteamRequest2Play();


            }
        };
        NetworkUtils.registerNetworkStatusChangedListener(onNetworkStatusChangedListener);
    }


    /**
     * 开启日志界面动画
     */
    private void showStartLogLayoutAnim() {
        mAnimLayoutIsGone = false;
        ObjectAnimator animator = ObjectAnimator.ofFloat(mAnimLinearLayout, "translationY", mScreenHeight, 0);
        animator.setDuration(300);//100
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimLinearLayout.setVisibility(VISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    /**
     * 关闭日志界面动画
     *
     * @param type=true的时候 标识执行初始化默认关闭动画,else=type的时候执行正常关闭动画逻辑
     */
    private void showCloseLogLayoutAnim(String type) {
        mAnimLayoutIsGone = true;
        mAnimLinearLayout.setVisibility(VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mAnimLinearLayout, "translationY", 0, mScreenHeight);
        if (type.equals("true")) {
            animator.setDuration(20);//100
        } else {
            animator.setDuration(300);//100

        }
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimLinearLayout.setVisibility(VISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }


    /**
     * 语音通话之前校验是否有 昵称
     */
    private void checkMicName() {
        mMicName = mMmkv.decodeString(Constants.KEY_MIC_Name, "");
        if ("".equals(mMicName)) {
            showSetMicNameDialog();
        } else {
            getMicPermission();
        }
    }

    private void showSetMicNameDialog() {
        // 输入对话框
        InputMicDialog.Builder mMicDialog = new InputMicDialog.Builder(this);
        mMicDialog.setTitle(getResources().getString(R.string.device_dialog_mic_name_title))// 标题可以不用填写
                .setHint(getResources().getString(R.string.device_dialog_mic_name_hint))
                .setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setListener((dialog, content) -> {
                    if ("".equals(content)) {
                        startSendToast(getResources().getString(R.string.line3_nicename_not_null));
                        return;
                    }
                    mMicDialog.dismiss();
                    mMmkv.encode(Constants.KEY_MIC_Name, content);
                    mMicName = mMmkv.decodeString(Constants.KEY_MIC_Name, "");
                    getMicPermission();
                })
                .show();


    }

    /**
     * 是否清空日志
     */
    private void showClearLogDialog() {
        new MessageDialog.Builder(this).setTitle(getResources().getString(R.string.mine_exit_title))
                .setMessage(getResources().getString(R.string.device_dialog_clear_log))
                .setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setCanceledOnTouchOutside(false)
                .setListener(dialog -> {
                    mLogDataList.clear();
                    mAnimEmptyLayout.setVisibility(VISIBLE);
                    mAnimRecycleView.setVisibility(View.INVISIBLE);
                })
                .show();


    }


    private void stopMicSteam() {
        if (mRtmpOnlyAudio != null) {
            mRtmpOnlyAudio.stopStream();
            LogUtils.e(TAG + "==stopMicSteam方法:---> stopMicSteam=======");

        }
        if (null != mIntent) {
            stopService(mIntent);
            LogUtils.e(TAG + "==stopMicSteam方法:---> stopService=======");

        }
        mMicOnLineLayout.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mRecordTag == EnumConfig.RecodeState.RECODE) {

                }
                //清空日志
                mLogDataList.clear();
                if (null != mDisposable2s) {
                    mDisposable2s.dispose();
                    mDisposable2s = null;
                }
                if (CMEAudioType == Constants.CMEAudio.CMEAudioConnected) {
                    sendRequest2Operation("5", "0");
                }
                if (null != mRtmpOnlyAudio) {
                    stopMicSteam();
                    sendRequest2Operation("6", "0");
                    LogUtils.e(TAG + "==返回:---> ====onKeyDown=====");


                }
                mHandler.sendEmptyMessage(Pusher_Stop);
                //重置触摸事件
                mRootView.setLongClickable(true);  //手势需要--能触摸
                mRootView.setOnTouchListener(mOnTouchVideoListener);
                finish();
                break;

        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 获取麦克风权限
     */
    private void getMicPermission() {
        if (mPlayState != EnumConfig.PlayState.STATE_PLAY) {
            startSendToast(getResources().getString(R.string.vlc_toast07));
            return;
        }
        if (mCurrentTypeDesc.equals(Constants.Type_HD3) || mCurrentTypeDesc.equals(Constants.Type_HD3_4K)) {
            startSendToast(getResources().getString(R.string.vlc_toast08));
            return;
            //当前直播没有语音功能
        }
        XXPermissions.with(this)
                .permission(Permission.RECORD_AUDIO)
                .request(new OnPermissionCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            if (CommonUtil.isFastClick()) {
                                if ("stopStream".equals(mTvMicStatus.getTag())) {
                                    mHandler.sendEmptyMessage(Pusher_Start);
                                    //开启了备用方案，推送备用方案语音
                                    if (Constants.PlayMode.Spare.equals(PlayMode)) {
                                        if (!mRtmpOnlyAudio.isStreaming()) {//false 表示还未开启推流
                                            if (mRtmpOnlyAudio.prepareAudio()) {
                                                mRtmpOnlyAudio.prepareAudio(256 * 1024, 44100, true, false, false);
                                                mRtmpOnlyAudio.startStream(mSpareMicPushSteam);
                                                LogUtils.e(TAG + "==备用方案,开启语音推流==麦克风推流地址:" + mSpareMicPushSteam);
                                                addLogData2Refresh(false, "Operation=2--> 手机设备接收到数据：开启语音推流==麦克风推流地址mCurrentMicUrl：" + mSpareMicPushSteam);
                                            }
                                        } else {
                                            addLogData2Refresh(false, "Operation=2--> 手机设备接收到数据：已经开启语音推流.....请勿重复推送mCurrentMicUrl：" + mCurrentMicUrl);
                                            LogUtils.e(TAG + "==备用方案:,已经开启语音推流.....请勿重复推送");
                                        }
                                    }
                                } else if ("startStream".equals(mTvMicStatus.getTag())) {
                                    mHandler.sendEmptyMessage(Pusher_Stop);
                                }
                            }

                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            startSendToast(getResources().getString(R.string.device_toast02));
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(getApplicationContext(), permissions);
                        } else {
                            startSendToast(getResources().getString(R.string.device_toast03));
                        }
                    }
                });


    }


    private void startSendToast(String toastStr) {
        Message tempMsg = mHandler.obtainMessage();
        tempMsg.what = Send_Toast;
        tempMsg.obj = toastStr;
        mHandler.sendMessage(tempMsg);
    }

    private void startSendToastDelayed(String toastStr, long time) {
        Message tempMsg = mHandler.obtainMessage();
        tempMsg.what = Send_Toast;
        tempMsg.obj = toastStr;
        mHandler.sendMessageDelayed(tempMsg, time);
    }

    /**
     * @param tag     true=需要填写时间  false=不需要填写时间
     * @param strText 内容
     */
    private void addLogData2Refresh(Boolean tag, String strText) {
        mAnimEmptyLayout.setVisibility(View.INVISIBLE);
        mAnimRecycleView.setVisibility(VISIBLE);
        if (null == mLogDataList) {
            mLogDataList = new ArrayList<String>();
        }
        //添加日志信息,然后刷新列表
        if (!tag) {
            mLogDataList.add("" + strText);
        } else {
            String nowString = TimeUtils.getNowString();
            Calendar calendar = Calendar.getInstance();
            int seconds = calendar.get(Calendar.MILLISECOND);// 毫秒
            mLogDataList.add("TAG_TIME" + nowString + "." + seconds + "    " + strText); //16:05:22.150 语音发送数据/语音接收数据
        }
        for (int i = 0; i < mLogDataList.size(); i++) {
            String s = mLogDataList.get(i);

        }

        mAdapter.setData(mLogDataList);
    }


    @Override
    public void onResume() {
        super.onResume();
        CMEAudioType = Constants.CMEAudio.CMEAudioNormal;
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        super.onPause();
        mHandler.sendEmptyMessage(Type_Loading_InVisible);
        mVlvLoadingView.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销网络监听
        NetworkUtils.unregisterNetworkStatusChangedListener(onNetworkStatusChangedListener);
        //开启了推流,则关闭
        if ("startStream".equals(mTvMicStatus.getTag())) {
            mHandler.sendEmptyMessage(Pusher_Stop);
        }

        if (null != mDisposable2s) {
            mDisposable2s.dispose();
            mDisposable2s = null;
        }
        if (null != mRtmpOnlyAudio) {
            sendRequest2Operation("6", "0");
            stopMicSteam();
            LogUtils.e(TAG + "==返回:---> ====onDestroy=====");
        }

        //销毁Tencent播放器
        if (mTencentLivePlayer != null) {
            if (mPlayState == EnumConfig.PlayState.STATE_PLAY) {
                mTencentLivePlayer.stopPlay();
            }
            mTencentLivePlayer = null;
        }
        changeAudioStatus(Constants.CMEAudio.CMEAudioNormal);

    }


    public void setTextColor(int color, String message, boolean isStarting) {
        mRecordView.setText(message);
        mRecordView.setTextColor(color);
        this.isStarting = isStarting;
    }

    /**
     * 扫描文件
     */
    public final MediaScannerConnection msc = new MediaScannerConnection(this, new MediaScannerConnection.MediaScannerConnectionClient() {
        public void onMediaScannerConnected() {
            Log.v("TAG", "scan onMediaScannerConnected");
            msc.scanFile("/sdcard/image.jpg", "image/jpeg");
        }

        public void onScanCompleted(String path, Uri uri) {
            Log.v("TAG", "scan completed");
            msc.disconnect();
        }
    });


    /**
     * 设置播放样式:全屏或者半屏
     *
     * @param pageType
     */
    public void setPageType(int pageType) {
        mCurrPageType = pageType;
        if (pageType == EnumConfig.PageType.SHRINK) {
            mBottomVideoFull.setImageResource(R.drawable.nur_ic_fangda);
        } else {
            mBottomVideoFull.setImageResource(R.drawable.nur_ic_fangxiao);
        }
    }

    /**
     * 关闭,锁定,控制面板
     */
    public void lock() {
        mLockMode = true;
        mLockView.setImageResource(R.drawable.video_lock_close_ic);
        mControlTopLayout.setVisibility(View.GONE);
        mControlBottomLayout.setVisibility(View.GONE);
        mControlRightLayout.setVisibility(View.GONE);
        mMicOnLineLayout.setVisibility(INVISIBLE);
        //重置触摸事件--关闭触摸事件
        mRootView.setOnTouchListener(null);
        mRootView.setLongClickable(false);  //手势不需要需要--不能触摸
        // 延迟隐藏控制面板
        mRelativeAll.removeCallbacks(mHideControllerRunnable);
        mRelativeAll.postDelayed(mHideControllerRunnable, CONTROLLER_HIDE_DELAY);
    }

    /**
     * 解锁控制面板
     */
    public void unlock() {
        mLockMode = false;
        mLockView.setImageResource(R.drawable.video_lock_open_ic);
        mControlTopLayout.setVisibility(VISIBLE);
        mControlBottomLayout.setVisibility(VISIBLE);
        mControlRightLayout.setVisibility(VISIBLE);

        if (CMEAudioType == Constants.CMEAudio.CMEAudioPushSuccess || CMEAudioType == Constants.CMEAudio.CMEAudioConnected) {
            mMicOnLineLayout.setVisibility(VISIBLE);
        } else {
            mMicOnLineLayout.setVisibility(INVISIBLE);
        }
        //重置触摸事件--解锁触摸事件
        mRootView.setLongClickable(true);  //手势需要--能触摸
        mRootView.setOnTouchListener(mOnTouchVideoListener);
        // 延迟隐藏控制面板
        mRelativeAll.removeCallbacks(mHideControllerRunnable);
        mRelativeAll.postDelayed(mHideControllerRunnable, CONTROLLER_HIDE_DELAY);
    }

    /**
     * 锁定面板
     */
    private boolean mLockMode;   //当前小钥匙 是否打开关闭的标识  true =关闭  false =打开(钥匙打开状态)
    /**
     * 显示面板
     */
    private boolean mControllerShow = true;

    /**
     * 显示控制面板
     */
    private final Runnable mShowControllerRunnable = () -> {
        if (!mControllerShow) {
            showController();
        }
    };

    /**
     * 隐藏控制面板
     */
    private final Runnable mHideControllerRunnable = () -> {
        if (mControllerShow) {
            hideController();
        }
    };


    /**
     * 显示控制面板
     */
    public void showController() {
        if (mControllerShow) {
            return;
        }
        mControllerShow = true;
        //连接着和连接成功都要显示
        if (CMEAudioType == Constants.CMEAudio.CMEAudioPushSuccess && mPlayState == EnumConfig.PlayState.STATE_PLAY
                || CMEAudioType == Constants.CMEAudio.CMEAudioConnected && mPlayState == EnumConfig.PlayState.STATE_PLAY) {
            if (mLockMode) {//true=关闭,false=打开
                mMicOnLineLayout.setVisibility(INVISIBLE);
            } else {
                mMicOnLineLayout.setVisibility(VISIBLE);
            }
        } else {
            mMicOnLineLayout.setVisibility(INVISIBLE);
        }
        ObjectAnimator.ofFloat(mControlTopLayout, "translationY", -mControlTopLayout.getHeight(), 0).start();
        ObjectAnimator.ofFloat(mControlBottomLayout, "translationY", mControlBottomLayout.getHeight(), 0).start();
        ObjectAnimator.ofFloat(mControlRightLayout, "translationX", mControlRightLayout.getWidth() + getResources().getDimension(R.dimen.dp_5), 0).start();

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(200);
        animator.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
            mLockView.setAlpha(alpha);
            if ((int) alpha != 1) {
                return;
            }
            if (mLockView.getVisibility() == INVISIBLE) {
                mLockView.setVisibility(VISIBLE);
            }
        });
        animator.start();
    }


    /**
     * 隐藏控制面板
     */
    public void hideController() {
        if (!mControllerShow) {
            return;
        }
        mControllerShow = false;
        mMicOnLineLayout.setVisibility(INVISIBLE);

        ObjectAnimator.ofFloat(mControlTopLayout, "translationY", 0, -mControlTopLayout.getHeight()).start();
        ObjectAnimator.ofFloat(mControlBottomLayout, "translationY", 0, mControlBottomLayout.getHeight()).start();
        ObjectAnimator.ofFloat(mControlRightLayout, "translationX", 0, mControlRightLayout.getWidth() + getResources().getDimension(R.dimen.dp_5)).start();
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
        animator.setDuration(200);
        animator.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
            mLockView.setAlpha(alpha);
            if (alpha != 0f) {
                return;
            }

            if (mLockView.getVisibility() == VISIBLE) {
                mLockView.setVisibility(INVISIBLE);
            }
        });
        animator.start();
    }

/**
 * ********************************控制面板动画和全屏相关*********************结束***********
 */


    /**
     * ********************************语音推流回调*********************开始***********
     */

    @Override
    public void onConnectionSuccessRtmp() {
        LogUtils.e(TAG + "==语音通话回调:--->语音连接成功");
        /**
         * 此处,如果直播流断了,但是上位机呼叫我,我需要重新开启直播,然后连接
         */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /**
                 * 此处,如果直播流断了,但是上位机呼叫我,我需要重新开启直播,然后连接
                 */
                if (Objects.equals(PlayMode, Constants.PlayMode.Normal)) {
                    if (mPlayState == EnumConfig.PlayState.STATE_PLAY) {
                        addLogData2Refresh(true, "语音发送数据");
                        addLogData2Refresh(false, "Operation=3" + "--> 手机设备发送消息：请求上位机从Nginx拉取音频流；(ps:语音推流连接成功)");
                        LogUtils.e(TAG + "==语音通话回调:--->Success==直接链接成功==");
                    } else {
                        startSendToast(getResources().getString(R.string.vlc_toast12));
                        sendGetSteamRequest2Play();
                        LogUtils.e(TAG + "==语音通话回调:--->重新加载直播流----推流回调中");
//                    mHandler.sendEmptyMessageDelayed(Restart_load_Steam, 1000);
                        LogUtils.e(TAG + "==语音通话回调:--->Success==直播断了,从新加载path==");
                    }
                    sendRequest2Operation("3", "0");
                    mTvMicStatus.setTag("startStream");
                    mTvMicStatus.setText(getResources().getString(R.string.vlc_close));
                    mTvMicStatus.setTextColor(getResources().getColor(R.color.color_007AFF));
                    Drawable topstart = getResources().getDrawable(R.drawable.icon_mic_pre);
                    mTvMicStatus.setCompoundDrawablesWithIntrinsicBounds(null, topstart, null, null);
                    startSendToast(getResources().getString(R.string.line_on_connecting));
                    changeAudioStatus(Constants.CMEAudio.CMEAudioPushSuccess);
                    if (mControllerShow) { //控制布局显示的时候
                        mMicOnLineLayout.setText(getResources().getString(R.string.line_on_connecting));
                        mMicOnLineLayout.setVisibility(VISIBLE);
                    } else {
                        mMicOnLineLayout.setVisibility(INVISIBLE);
                    }
                    //避免语音推流被杀死
                    mIntent = new Intent(PlayerLine3Activity.this, NotificationService.class);
                    startService(mIntent);
                } else {
                    //备用方案推流成功，就默认成功，没有连接中的概念
                    changeAudioStatus(Constants.CMEAudio.CMEAudioConnected);
//                    changeAudioStatus(Constants.CMEAudio.CMEAudioPushSuccess);
                    mMicOnLineLayout.setText(getResources().getString(R.string.line_on_the_phone));
                    if (mControllerShow) { //控制布局显示的时候
                        mMicOnLineLayout.setText(getResources().getString(R.string.line_on_the_phone));
                        mMicOnLineLayout.setVisibility(VISIBLE);
                    } else {
                        mMicOnLineLayout.setVisibility(INVISIBLE);
                    }
                    //避免语音推流被杀死
                    mIntent = new Intent(PlayerLine3Activity.this, NotificationService.class);
                    startService(mIntent);

                }

            }
        });
    }


    @Override
    public void onConnectionFailedRtmp(String reason) {
        runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                changeAudioStatus(Constants.CMEAudio.CMEAudioHangUp);
                LogUtils.e(TAG + "==语音通话回调:--->Failed==失败错误原因==reason:" + reason);//Error send packet: Broken pipe
                LogUtils.e(TAG + "==语音通话回调:--->Failed==失败错误原因==micOnlineStatue:" + CMEAudioType);
                stopMicSteam();
                sendRequest2Operation("5", "1");
                addLogData2Refresh(true, "语音发送数据");
                addLogData2Refresh(false, "Operation=5" + "--> 手机设备,主动挂断语音；(ps:语音推流连接失败)");
                addLogData2Refresh(true, "onConnectionFailedRtmp=连接状态:" + CMEAudioType + ",推流连接失败: stopMicSteam()+主动发送挂断消息==>执行完毕");
                if (CMEAudioType == Constants.CMEAudio.CMEAudioConnected) {
                    stopMicSteam();
                    sendRequest2Operation("5", "1");
                    addLogData2Refresh(true, "语音发送数据");
                    addLogData2Refresh(false, "Operation=5" + "--> 手机设备,主动挂断语音；(ps:语音推流连接失败)");
                    startSendToast(getResources().getString(R.string.vlc_toast13a));
                }
            }
        });
    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {
//        LogUtils.e(TAG + "==语音通话回调:--->bitrate====" + bitrate);

    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //会一直回调onDisconnectRtmp方法
                //changeAudioStatus(Constants.CMEAudio.CMEAudioHangUp);
                addLogData2Refresh(true, "onDisconnectRtmp=连接状态:" + CMEAudioType + ",推流连接失败:走onDisconnectRtmp回调,");
                if (CMEAudioType == Constants.CMEAudio.CMEAudioConnected) {
                    changeAudioStatus(Constants.CMEAudio.CMEAudioHangUp);
                }
                if (CMEAudioType == Constants.CMEAudio.CMEAudioPushing || CMEAudioType == Constants.CMEAudio.CMEAudioPushSuccess) {
                    if (null != mRtmpOnlyAudio) {
                        stopMicSteam();
                    }
                }
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {
        runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                changeAudioStatus(Constants.CMEAudio.CMEAudioHangUp);
                addLogData2Refresh(true, "onAuthErrorRtmp=连接状态:" + CMEAudioType + ",推流连接失败:走onAuthErrorRtmp回调");
                LogUtils.e(TAG + "==语音通话回调:--->Error====错误");
                mMicOnLineLayout.setVisibility(View.INVISIBLE);
                if (null != mRtmpOnlyAudio) {
                    stopMicSteam();
                    LogUtils.e(TAG + "==语音回调:---> onDisconnect==2222==onAuthErrorRtmp=====");
                }
                startSendToast(getResources().getString(R.string.vlc_toast13));
            }
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        LogUtils.e(TAG + "==语音通话回调:--->onAuthSuccessRtmp====");
    }


}