package com.company.shenzhou.mineui.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.company.shenzhou.R;
import com.company.shenzhou.bean.socket.MicSocketResponseBean;
import com.company.shenzhou.bean.socket.SocketRefreshEvent;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.utlis.CalculateUtils;
import com.company.shenzhou.utlis.LogUtils;
import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;
import com.xdandroid.hellodaemon.AbsWorkService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import io.reactivex.disposables.Disposable;


/**
 * 保活的Service通讯服务
 * <p>
 * 一直开启这监听线程,监听Socket
 */

public class ReceiveSocketService extends AbsWorkService {

    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static boolean isFirstIn = false;
    public static Disposable sDisposable;
    private Gson mGson;
    private static final String TAG = "Socket监听===";
    private ReceiveThread receiveThread;


    public void stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable != null) sDisposable.dispose();
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();

    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     *
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }


    /**
     * ***************************************************************************通讯模块**************************************************************************
     */
    private volatile static boolean isRuning = true;
//    private String mAppIP;


    @Override
    public void startWork(Intent intent, int flags, int startId) {

//        sDisposable = Observable
//                .interval(3, TimeUnit.SECONDS)//定时器操作符，这里三秒打印一个log
//                //取消任务时取消定时唤醒
//                .doOnDispose(() -> {
//                    LogUtils.e("ReceiveSocketService--数据监听服务--接收线程--doOnDispose");
//                    cancelJobAlarmSub();
//                })
//                .subscribe(count -> {
//                    LogUtils.e("ReceiveSocketService--数据监听服务--接收线程--每 3 秒采集一次数据... count = " + count);
//                    if (count > 0 && count % 18 == 0)
//                        LogUtils.e("ReceiveSocketService--数据监听服务--接收线程--保存数据到磁盘。 saveCount = " + (count / 18 - 1));
//                });

        /**
         * App启动的时候初始化第一次默认端口线程
         */
        initFirstThread();

    }

    /**
     * eventbus 刷新数据--重启监听线程
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        switch (event.getUdpCmd()) {
            case Constants.UDP_CUSTOM_RESTART://重启监听线程

                //此处重启监听线程
                MMKV mmkv = MMKV.defaultMMKV();
                boolean b = mmkv.decodeBool(Constants.KEY_Login_Tag);
                String mSocketPort = mmkv.decodeString(Constants.KEY_Device_SocketPort);
                if (b) {//如果是登录状态,重启登入时候的监听
                    ReceiveSocketService receiveSocketService = new ReceiveSocketService();
                    receiveSocketService.setSettingReceiveThread(Integer.parseInt(mSocketPort), getApplicationContext());
                } else {//不是登录状态,重启广播搜索监听
                    ReceiveSocketService receiveSocketService = new ReceiveSocketService();
                    receiveSocketService.initFirstThread();
                }
                break;
        }

    }

    public static String getAppName(Context context) {
        return context.getString(R.string.app_name);

    }

    /**
     * 此为接收线程具体解析
     * ip 本地app的ip地址
     * port 本地监听的端口
     */
    public class ReceiveThread extends Thread {
        private int mLocalReceivePort;
        private int count = 0;
        private WeakReference<Context> appWeakReference;
        private MMKV mmkv;
        private String mPhoneDeviceCode;

        DatagramSocket mSettingDataSocket = null;
        DatagramPacket mSettingDataPacket = null;

        /**
         * @param port             开启线程监听,传入的port端口
         * @param context
         * @param mPhoneDeviceCode 手机唯一标识码
         */
        public ReceiveThread(int port, Context context, String mPhoneDeviceCode) {
            this.mLocalReceivePort = port;
            this.appWeakReference = new WeakReference<>(context);
            this.mPhoneDeviceCode = mPhoneDeviceCode;
            mGson = new Gson();
            mmkv = MMKV.defaultMMKV();

        }

        public void run() {
            byte[] receiveData = new byte[1024];
            mSettingDataPacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                if (mSettingDataSocket == null) {
                    mSettingDataSocket = new DatagramSocket(null);
                    mSettingDataSocket.setReuseAddress(true);
                    mSettingDataSocket.bind(new InetSocketAddress(mLocalReceivePort));
                }
            } catch (Exception e) {
                e.printStackTrace();
                SocketRefreshEvent event1 = new SocketRefreshEvent();
                event1.setUdpCmd(Constants.UDP_CUSTOM_RESTART);
                EventBus.getDefault().post(event1);

            }
            while (true) {
                if (isRuning) {
                    try {
                        LogUtils.e(TAG + "轮询中:------>接收,上位机数据");
                        LogUtils.e(TAG + "轮询中:------>接收,上位机数据");
                        LogUtils.e(TAG + "                                                                                      ");

                        mSettingDataSocket.receive(mSettingDataPacket);
//                        此处用byte来接受
//                        int localPort = mSettingDataSocket.getLocalPort();
//                        int a = 0xaac5;
//                        byte[] data = mSettingDataPacket.getData();
//                        for (int i = 0; i < data.length; i++) {
//                            byte datum = data[i];
//                            if (datum == 0xaa) {
//                                continue;
//                            }
//                            if (data[i]==0xC5){
//                                continue;
//                            }
//                        }
//                        int ff = Integer.parseInt("A1", 16);
//                        int cc = 12;
//                        byte aa = 11;
//                        String bb = Integer.toHexString(aa);
//                        String xx = Integer.toOctalString(aa);
//                        LogUtils.e("TAG==current系统用户===" + bb);
//                        LogUtils.e("TAG==current系统用户==aa=" + aa);


                        /**
                         * 此处做处理
                         * 实时获取当前本地设置的监听端口和服务器端口是否一致,不一致关闭多余线程,优化性能
                         */
//                      if (stringint == mSettingDataPacket.getPort()) {
//                      此处把byte数组转成十六进制字符串来接受
                        String rec = CalculateUtils.byteArrayToHexString(mSettingDataPacket.getData()).trim();

                        //过滤不是发送给我的消息全部不接受
                        int length = mSettingDataPacket.getLength() * 2;

                        String resultData = rec.substring(0, length);
//                                937a5f204dc43a14                   设备码   数据库存入的
//                                39333761356632303464633433613134   设备码   广播接收到的
                        if (mSettingDataPacket != null) {
                            String hostAddressIP = mSettingDataPacket.getAddress().getHostAddress();
//                            String mRun2End4 = CalculateUtils.getReceiveRun2End4String(resultData);//随机数之后到data结尾的String
                            String deviceType = CalculateUtils.getSendDeviceType(resultData);
                            String deviceOnlyCode = CalculateUtils.getSendDeviceOnlyCode(resultData);
                            String currentCMD = CalculateUtils.getCMD(resultData);
                            SocketRefreshEvent event = new SocketRefreshEvent();
                            int port = mSettingDataPacket.getPort();
                            //设置接收端口
                            event.setReceivePort(mLocalReceivePort + "");
                            Context context = appWeakReference.get();
                            Boolean dataIfForMe = CalculateUtils.getDataIfForMe(resultData, context, mPhoneDeviceCode);
                            String dataString = CalculateUtils.getReceiveDataString(resultData);
                            //7B227265636F72646964223A2233354530227D
                            //07B227265636F72646964223A2233354530227D
                            //16进制直接转换成为字符串
                            String appName = getAppName(context);                   //本地监听的端口           //本地监听的端口默认值8005
                            int currentLocalReceivePort = mmkv.decodeInt(Constants.KEY_LOCAL_RECEIVE_PORT, Constants.LOCAL_RECEIVE_PORT);  //实时记录本地监听的端口
                            if (currentLocalReceivePort == mLocalReceivePort) {
                                LogUtils.e(TAG + "线程,两者监听端口一致" + "线程:" + currentThread().getName() + "currentLocalReceivePort:" + currentLocalReceivePort + "==mLocalReceivePort:" + mLocalReceivePort);

                            } else {
                                LogUtils.e(TAG + "线程设置的监听的port=:" + mLocalReceivePort + ",当前需要监听的port==:" + currentLocalReceivePort);
                                LogUtils.e(TAG + "线程,两者监听端口不一致" + "线程:" + currentThread().getName() + "退出!");
                                break;
                            }
                            LogUtils.e(TAG + "线程,两者监听端口一致" + "线程正常执行:" + currentThread().getName() + "currentLocalReceivePort:" + currentLocalReceivePort + "==mLocalReceivePort:" + mLocalReceivePort);

                            LogUtils.e(TAG + "AppName==:" + appName);
                            LogUtils.e(TAG + "线程名字(当前)==" + currentThread().getName());
                            LogUtils.e(TAG + "本地监听port==:" + mLocalReceivePort);
                            LogUtils.e(TAG + "命令CMD==:" + currentCMD);
                            LogUtils.e(TAG + "上位机ip==:" + hostAddressIP);
                            LogUtils.e(TAG + "上位机port==:" + port);
                            LogUtils.e(TAG + "上位机deviceType==:" + deviceType);
                            LogUtils.e(TAG + "上位机deviceCode==:" + deviceOnlyCode);
                            //16进制直接转换成为字符串
                            String str = CalculateUtils.hexStr2Str(dataString);
                            LogUtils.e(TAG + "接收数据 ?? ==:" + dataIfForMe);
                            LogUtils.e(TAG + "协议String==:" + resultData);
                            LogUtils.e(TAG + "协议 data ==:" + dataString);
                            if (dataIfForMe) {
                                switch (currentCMD) {
                                    case Constants.UDP_FD: //广播
                                        try {
                                            LogUtils.e(TAG + "回调==广播");
                                            event.setTga(true);
                                            event.setData(resultData);
                                            event.setIp(hostAddressIP);
                                            event.setUdpCmd(Constants.UDP_FD);
                                            EventBus.getDefault().post(event);
                                        } catch (Exception e) {
                                            LogUtils.e(TAG + "回调==广播,Exception:" + e);

                                        }


                                        break;
                                    case Constants.UDP_FC://授权接入
                                        try {
                                            LogUtils.e(TAG + "回调==授权接入");
                                            //获取到病例的ID是十六进制的,需要转成十进制
                                            event.setTga(true);
                                            event.setData(resultData);
                                            event.setIp(hostAddressIP);
                                            event.setUdpCmd(Constants.UDP_FC);
                                            EventBus.getDefault().post(event);
                                        } catch (Exception e) {
                                            LogUtils.e(TAG + "回调==授权接入,Exception:" + e);

                                        }
                                        break;
                                    case Constants.UDP_HAND://握手
                                        try {
                                            LogUtils.e(TAG + "回调形式:--->握手");
                                            //判断数据是否是发个自己的
                                            Long startTime = System.currentTimeMillis();
                                            //设备在线握手成功
                                            event.setTga(true);
                                            event.setData(startTime + "");
                                            event.setIp(hostAddressIP);
                                            event.setReceivePort(mLocalReceivePort + "");
                                            event.setUdpCmd(Constants.UDP_HAND);
                                            EventBus.getDefault().postSticky(event);
                                        } catch (Exception e) {
                                            LogUtils.e(TAG + "回调形式:--->握手==Exception====");
                                        }
                                        break;
                                    case Constants.UDP_41://语音接入
                                        try {
                                            LogUtils.e("语音通话:" + "UDP_41==str==" + str);
                                            LogUtils.e(TAG + "回调形式:--->语音接入:" + str);
                                            MicSocketResponseBean micSocketResponseBean = mGson.fromJson(str, MicSocketResponseBean.class);
                                            mmkv.encode(Constants.KET_MIC_VOICE_ID_FOR_ME, micSocketResponseBean.getVoiceID() + "");
                                            event.setTga(true);
                                            event.setErrCode(micSocketResponseBean.getErrCode());
                                            event.setData(micSocketResponseBean.getUrl());
                                            event.setIp(micSocketResponseBean.getOperation());
                                            event.setUdpCmd(Constants.UDP_41);
                                            event.setStr(str);
                                            EventBus.getDefault().postSticky(event);
                                        } catch (Exception e) {
                                            LogUtils.e(TAG + "回调形式:--->语音接入==Exception==str==" + str);
                                        }

                                        break;
                                    case Constants.UDP_42://语音接入,语音广播通知命令,监听到重新获取vioceID
                                        LogUtils.e("语音通话:" + "UDP_42==str==" + str);
                                        try {
                                            LogUtils.e(TAG + "回调形式:--->语音接入,语音广播通知命令:" + str);
                                            event.setTga(true);
                                            event.setData("");
                                            event.setIp("");
                                            event.setUdpCmd(Constants.UDP_42);
                                            EventBus.getDefault().postSticky(event);
                                        } catch (Exception e) {
                                            LogUtils.e(TAG + "回调形式:--->语音接入==Exception==str==" + str);
                                        }

                                        break;


                                }
                            }

                            LogUtils.e(TAG + "                                                                                      ");
                            LogUtils.e(TAG + "!=======================华丽的分割线===========================!");
                            LogUtils.e(TAG + "!*******************************************************************************!");
                            LogUtils.e(TAG + "!*******************************************************************************!");
                            LogUtils.e(TAG + "!=======================华丽的分割线===========================!");

//                            }

//                            } else {
////                                SocketRefreshEvent event1 = new SocketRefreshEvent();
////                                event1.setUdpCmd(Constants.UDP_CUSTOM_RESTART);
////                                EventBus.getDefault().post(event1);
//                                SocketRefreshEvent event = new SocketRefreshEvent();
//                                event.setUdpCmd(Constants.UDP_CUSTOM_TOAST);
//                                event.setData("");
//                                event.setData("code=0,监听port端口不一致,退出多余的监听线程!!");
//                                EventBus.getDefault().post(event);
//                                LogUtils.e(TAG + "code=0,监听port端口不一致,退出多余的监听线程!!");
//                                break;//不相等的直接跳出接收,关闭线程
//                            }

                        }

                    } catch (Exception e) {
//                        SocketRefreshEvent event1 = new SocketRefreshEvent();
//                        event1.setUdpCmd(Constants.UDP_CUSTOM_RESTART);
//                        EventBus.getDefault().post(event1);
//                        SocketRefreshEvent event = new SocketRefreshEvent();
//                        event.setUdpCmd(Constants.UDP_CUSTOM_TOAST);
//                        event.setData("");
//                        //java.lang.NullPointerException:
//                        // Attempt to invoke virtual method 'boolean java.lang.String.equals(java.lang.Object)' on a null object reference
//                        event.setData("code=1,循环监听异常,错误,退出监听线程!!");
//                        LogUtils.e(TAG + "code=1,循环监听异常,错误,退出监听线程!!");
//                        EventBus.getDefault().post(event);
                        e.printStackTrace();
                        LogUtils.e(TAG + "异常-->code=1,循环监听解析,异常:");
                    }
                }
            }


        }
    }

    /**
     * App启动的时候初始化第一次默认端口线程
     *
     * @param
     */
    public void initFirstThread() {
        LogUtils.e("App-server-initLiveService--初始化监听服务--initFirstThread开启了");

        MMKV kv = MMKV.defaultMMKV();
        int mLocalReceivePort = kv.decodeInt(Constants.KEY_LOCAL_RECEIVE_PORT);//默认8005
        //手机设备码
        String mPhoneDeviceCode = kv.decodeString(Constants.KEY_PhoneDeviceCode, CalculateUtils.getPhoneDeviceCode());
        LogUtils.e("App-server-initFirstThread(接收线程里面初始化了),获取手机唯一标识码mPhoneDeviceCode:" + mPhoneDeviceCode);
        //是否开启过接收线程,开启过为true,避免初始化的时候创建三个接受线程
        boolean b = kv.decodeBool(Constants.KEY_SOCKET_RECEIVE_FIRST_IN);
        if (!b) {
            LogUtils.e("App-server-initLiveService--初始化监听服务--initFirstThread开启了====避免初始化的时候创建三个接受线程");
            LogUtils.e(TAG + "server-第一次初始化监听服务,:" + "本地监听的port=" + mLocalReceivePort);
            kv.encode(Constants.KEY_LOCAL_RECEIVE_PORT, mLocalReceivePort); //当前设置的,本地广播监听端口
            kv.encode(Constants.KEY_SOCKET_RECEIVE_FIRST_IN, true);
            receiveThread = new ReceiveThread(mLocalReceivePort, getApplicationContext(), mPhoneDeviceCode);
            receiveThread.start();
            LogUtils.e(TAG + "App-server-第一次初始化监听服务,receiveThread.getName():" + receiveThread.getName());

        } else {
            LogUtils.e(TAG + "App-server-initLiveService--服务已存在,不开启-当前接受线程名::" + receiveThread.getName());

        }


    }

    /**
     * 用户设置可广播端口开启的接收线程
     *
     * @param localReceivePort 设置本地监听端口
     * @param context          上下文
     */
    public void setSettingReceiveThread(int localReceivePort, Context context) {
        MMKV kv = MMKV.defaultMMKV();
        //手机设备码
        String mPhoneDeviceCode = kv.decodeString(Constants.KEY_PhoneDeviceCode, CalculateUtils.getPhoneDeviceCode());
        kv.encode(Constants.KEY_SOCKET_RECEIVE_FIRST_IN, true);   //第一次开启接收线程 避免初始化的时候开启多次线程
        kv.encode(Constants.KEY_LOCAL_RECEIVE_PORT, localReceivePort); //设置的,本地监听端口
        //当前开启的接收端口
        ReceiveThread receiveThread = new ReceiveThread(localReceivePort, context, mPhoneDeviceCode);
        receiveThread.start();
        LogUtils.e(TAG + "用户设置,重新开启监听服务," + "localReceivePort=" + localReceivePort);

    }


    /**
     * ***************************************************************************通讯模块**************************************************************************
     */

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopService();
    }

    /**
     * 任务是否正在运行?
     *
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        //若还没有取消订阅, 就说明任务仍在运行.
        return sDisposable != null && !sDisposable.isDisposed();
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
    }

    /**
     * 将获取到的int型ip转成string类型
     */
    private static String getIpString(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        isRuning = true;
    }

    @Override
    public void onDestroy() {
        isFirstIn = false;
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

}
