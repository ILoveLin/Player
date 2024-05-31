package com.company.shenzhou.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.company.shenzhou.R;
import com.company.shenzhou.aop.Log;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.http.glide.GlideApp;
import com.company.shenzhou.http.model.RequestHandler;
import com.company.shenzhou.http.model.RequestServer;
import com.company.shenzhou.manager.ActivityManager;
import com.company.shenzhou.mineui.service.ReceiveSocketService;
import com.company.shenzhou.other.AppConfig;
import com.company.shenzhou.other.CrashHandler;
import com.company.shenzhou.other.DebugLoggerTree;
import com.company.shenzhou.other.MaterialHeader;
import com.company.shenzhou.other.SmartBallPulseFooter;
import com.company.shenzhou.other.TitleBarStyle;
import com.company.shenzhou.other.ToastLogInterceptor;
import com.company.shenzhou.other.ToastStyle;
import com.company.shenzhou.playerdb.DaoMaster;
import com.company.shenzhou.playerdb.DaoSession;
import com.company.shenzhou.playerdb.manager.MyGreenDaoDbHelper;
import com.company.shenzhou.utlis.LogUtils;
import com.company.shenzhou.utlis.MD5ChangeUtil;
import com.company.shenzhou.utlis.SharePreferenceUtil;
import com.didichuxing.doraemonkit.DoKit;
import com.blankj.utilcode.util.DeviceUtils;
import com.hjq.bar.TitleBar;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.EasyConfig;
import com.hjq.language.MultiLanguages;
import com.hjq.toast.ToastUtils;
import com.hjq.umeng.UmengClient;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveBaseListener;
import com.xdandroid.hellodaemon.DaemonEnv;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.MemoryCookieStore;
import com.zhy.http.okhttp.https.HttpsUtils;

import org.greenrobot.greendao.identityscope.IdentityScopeType;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import timber.log.Timber;
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 应用入口
 */
public final class AppApplication extends Application {

    private static MMKV mmkv;
    private static AppApplication mApplication;


    private static AppApplication application;

    public AppApplication() {
        application = this;
    }

    public static synchronized AppApplication getInstance() {
        if (application == null) {
            application = new AppApplication();
        }
        return application;
    }

    @Log("启动耗时")
    @Override
    public void onCreate() {
        super.onCreate();
        initSdk(application);
    }

    @Override
    protected void attachBaseContext(Context base) {
        // 绑定语种
        super.attachBaseContext(MultiLanguages.attach(base));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 清理所有图片内存缓存
        GlideApp.get(this).onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // 根据手机内存剩余情况清理图片内存缓存
        GlideApp.get(this).onTrimMemory(level);
    }

    /**
     * 初始化一些第三方框架
     */
    public void initSdk(AppApplication application) {
        //初始化Didi调试日志框架
        new DoKit.Builder(application)
                .build();

        //初始化DK播放器框架
        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                //使用使用IjkPlayer解码
                .setPlayerFactory(IjkPlayerFactory.create())
                .build());
        //初始化MMKV存储框架
        MMKV.initialize(application);
        mmkv = MMKV.defaultMMKV();
        //设置第一次启动App的时候,是否第一次初始化过,接收线程
        mmkv.encode(Constants.KEY_SOCKET_RECEIVE_FIRST_IN, false);
        int i2 = mmkv.decodeInt(Constants.KEY_BROADCAST_SERVER_PORT);
        int i4 = mmkv.decodeInt(Constants.KEY_LOCAL_RECEIVE_PORT);  //实时记录本地监听的端口
        if ("".equals(i2 + "") || i2 == 0) {
            mmkv.encode(Constants.KEY_BROADCAST_SERVER_PORT, Constants.BROADCAST_SERVER_PORT);
        }
        if ("".equals(i4 + "") || i4 == 0) {
            mmkv.encode(Constants.KEY_LOCAL_RECEIVE_PORT, Constants.LOCAL_RECEIVE_PORT); //默认给广播接收的端口
        }

        //初始化数据库
        initGreenDao();
        //初始化国际化
        MultiLanguages.init(this);

        Boolean mCanUse = (Boolean) SharePreferenceUtil.get(application, SharePreferenceUtil.Bugly_CanUse, false);
        LogUtils.e("App-initLiveService--Bugly_CanUse====" + mCanUse);
        boolean b = mmkv.decodeBool(Constants.KEY_SOCKET_RECEIVE_FIRST_IN);
        LogUtils.e("App-initLiveService--避免初始化的时候开启多次线程-标识====" + b);

        if (mCanUse) {
            initLiveService(application);
        }
        //初始化腾讯快直播SDK
        initTencentLive();

        //Okhttp请求头
        //请求工具的拦截器  ,可以设置证书,设置可访问所有的https网站,参考https://www.jianshu.com/p/64cc92c52650
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .cookieJar(new CookieJarImpl(new MemoryCookieStore()))                  //内存存储cookie
                .connectTimeout(5000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new MyInterceptor(this))                      //拦截器,可以添加header 一些信息
                .readTimeout(5000L, TimeUnit.MILLISECONDS)
                .hostnameVerifier(new HostnameVerifier() {//允许访问https网站,并忽略证书
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

        OkHttpUtils.initClient(okHttpClientBuilder.build());


        // 设置标题栏初始化器
        TitleBar.setDefaultStyle(new TitleBarStyle());

        // 设置全局的 Header 构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((cx, layout) ->
                new MaterialHeader(this).setColorSchemeColors(ContextCompat.getColor(this, R.color.common_accent_color)));
        // 设置全局的 Footer 构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((cx, layout) -> new SmartBallPulseFooter(this));
        // 设置全局初始化器
        SmartRefreshLayout.setDefaultRefreshInitializer((cx, layout) -> {
            // 刷新头部是否跟随内容偏移
            layout.setEnableHeaderTranslationContent(true)
                    // 刷新尾部是否跟随内容偏移
                    .setEnableFooterTranslationContent(true)
                    // 加载更多是否跟随内容偏移
                    .setEnableFooterFollowWhenNoMoreData(true)
                    // 内容不满一页时是否可以上拉加载更多
                    .setEnableLoadMoreWhenContentNotFull(false)
                    // 仿苹果越界效果开关
                    .setEnableOverScrollDrag(false);
        });

        // 初始化吐司
        ToastUtils.init(this, new ToastStyle());
        // 设置调试模式
        ToastUtils.setDebugMode(AppConfig.isDebug());
        // 设置 Toast 拦截器
        ToastUtils.setInterceptor(new ToastLogInterceptor());

//        // 本地异常捕捉
        CrashHandler.register(this);
//
//        // 友盟统计、登录、分享 SDK
//        UmengClient.init(application, AppConfig.isLogEnable());
//
//        // Bugly 异常捕捉
//        CrashReport.initCrashReport(application, AppConfig.getBuglyId(), AppConfig.isDebug());

        // Activity 栈管理初始化
        ActivityManager.getInstance().init(this);


        // 网络请求框架初始化
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        EasyConfig.with(okHttpClient)
                // 是否打印日志
                .setLogEnabled(AppConfig.isLogEnable())
                // 设置服务器配置
                .setServer(new RequestServer())
                // 设置请求处理策略
                .setHandler(new RequestHandler(this))
                // 设置请求重试次数
                .setRetryCount(1)
                .setInterceptor((api, params, headers) -> {
                    // 添加全局请求头
                    headers.put("token", "66666666666");
                    headers.put("deviceOaid", UmengClient.getDeviceOaid());
                    headers.put("versionName", AppConfig.getVersionName());
                    headers.put("versionCode", String.valueOf(AppConfig.getVersionCode()));
                    // 添加全局请求参数
                    // params.put("6666666", "6666666");
                })
                .into();

        // 设置 Json 解析容错监听
        GsonFactory.setJsonCallback((typeToken, fieldName, jsonToken) -> {
            // 上报到 Bugly 错误列表
            CrashReport.postCatchedException(new IllegalArgumentException(
                    "类型解析异常：" + typeToken + "#" + fieldName + "，后台返回的类型为：" + jsonToken));
        });

        // 初始化日志打印
        if (AppConfig.isLogEnable()) {
            Timber.plant(new DebugLoggerTree());
        }

        // 注册网络状态变化监听
        ConnectivityManager connectivityManager = ContextCompat.getSystemService(this, ConnectivityManager.class);
        if (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onLost(@NonNull Network network) {
                    Activity topActivity = ActivityManager.getInstance().getTopActivity();
                    if (!(topActivity instanceof LifecycleOwner)) {
                        return;
                    }

                    LifecycleOwner lifecycleOwner = ((LifecycleOwner) topActivity);
                    if (lifecycleOwner.getLifecycle().getCurrentState() != Lifecycle.State.RESUMED) {
                        return;
                    }

                    ToastUtils.show(R.string.common_network_error);
                }
            });
        }
    }

    /**
     * 初始化腾讯快直播SDK
     */
    private void initTencentLive() {
        String licenceURL = "https://license.vod2.myqcloud.com/license/v2/1255750344_1/v_cube.license"; // 获取到的 licence url
        String licenceKey = "05fcb2597e0e53dfa98cd026c388455e"; // 获取到的 licence key
        TXLiveBase.getInstance().setLicence(this, licenceURL, licenceKey);
        TXLiveBase.setListener(new TXLiveBaseListener() {
            @Override
            public void onLicenceLoaded(int result, String reason) {
                LogUtils.e("App==腾讯直播初始化" + "onLicenceLoaded: result:" + result + ", reason:" + reason);
            }
        });

    }


    private void initGreenDao() {
//        //创建数据库shop.db(可版本升级保留原数据)
        MyGreenDaoDbHelper helper = new MyGreenDaoDbHelper(this, "playerdb", null);
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        //获取dao对象管理者
        mSession = daoMaster.newSession(IdentityScopeType.None);

    }
    public static DaoSession mSession;


    public static DaoSession getDaoSession() {
        return mSession;
    }

    public void intBugly() {
        LogUtils.e("intSDK--初始化SDK");
        /**
         * 为了保证运营数据的准确性，建议不要在异步线程初始化Bugly。
         * 第三个参数为SDK调试模式开关，调试模式的行为特性如下：
         *
         * 输出详细的Bugly SDK的Log；
         * 每一条Crash都会被立即上报；
         * 自定义日志将会在Logcat中输出。
         */
        CrashReport.initCrashReport(getApplicationContext(), "ab805dbd30", false);
        //设置bugly相关参数
        String deviceId = DeviceUtils.getUniqueDeviceId();
        String mSend_IDBy32 = MD5ChangeUtil.Md5_32(deviceId);
        //设置设备唯一ID
        CrashReport.setDeviceId(getApplicationContext(), mSend_IDBy32 + "");
        //通过UserStrategy设置,手机型号和厂商
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        String manufacturer = DeviceUtils.getManufacturer();
        String model = DeviceUtils.getModel();
        strategy.setDeviceModel(manufacturer + "_" + model);
        initLiveService(application);
        LogUtils.e("intSDK--初始化SDK方法执行完毕!");
    }

    /**
     * 保活服务
     */
    private void initLiveService(AppApplication application) {
        String string = mmkv.decodeString(Constants.KEY_PhoneDeviceCode, "ec3fdc8a06d4fe08c93437560c4ce460");
        LogUtils.e("App-initLiveService--初始化监听服务-开始?");
        LogUtils.e("App-initLiveService(初始化监听服务),获取手机唯一标识码:" + string);
        //初始化
        WeakReference<Context> appWeakReference = new WeakReference<>(application);
        DaemonEnv.initialize(appWeakReference.get(), ReceiveSocketService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        //是否 任务完成, 不再需要服务运行?
        ReceiveSocketService.sShouldStopService = false;
        //开启服务
        DaemonEnv.startServiceMayBind(ReceiveSocketService.class);
        LogUtils.e("App-initLiveService--初始化监听服务-结束");


    }


}