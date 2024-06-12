package com.company.shenzhou.utlis;

import android.content.Context;

import com.company.shenzhou.global.Constants;
import com.tencent.mmkv.MMKV;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/3 15:40
 * desc：
 * 使用端口复用,
 * 1,解决发送数据的时候只能使用随机端口的问题
 * 2,解决发送数据的时候和接受数据不能公用一个端口问题
 * <p>
 * DatagramSocket mSendSocket = new DatagramSocket(null);
 * mSendSocket.setReuseAddress(true);
 * mSendSocket.bind(new InetSocketAddress(Constants.SEND_PORT));
 */
public class SocketUtils {

    private static final String TAG = "Socket发送消息===";

    /**
     * @param data 字节数组    广播 授权,使用的是设置的端口,其他的点对点消息,按照协议data的port的走
     */
    public static void startSendBroadcastMessage(byte[] data, Context mContext) {
//        WifiManager manager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiManager.MulticastLock lock = manager.createMulticastLock("test wifi");
        //申请广播开启
//        lock.acquire();
        InetAddress mAddress = null;
        //点对点消息,握手
        try {
            mAddress = InetAddress.getByName(Constants.BROADCAST_IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        InetAddress finalMAddress = mAddress;

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
//                    byte[] sendData = data.getBytes();
                     MMKV kv = MMKV.defaultMMKV();
                    int mServerSendPort = kv.decodeInt(Constants.KEY_BROADCAST_SERVER_PORT);
                    int mLocalReceivePort = kv.decodeInt(Constants.KEY_LOCAL_RECEIVE_PORT);
                    LogUtils.e("SocketUtils===发送消息==点对点==hand==key=mCastSendPort=" + mServerSendPort);

                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, finalMAddress, mServerSendPort);
                    for (int i = 0; i < 5; i++) {
                        LogUtils.e(TAG + "广播消息--->发送第:" + i + "次广播,端口:" + mServerSendPort + "本地监听端口:" + mLocalReceivePort);

                        Thread.sleep(15);
                        //固定端口
                        DatagramSocket mSendBroadcastSocket = new DatagramSocket(null);
                        mSendBroadcastSocket.setReuseAddress(true);
                        mSendBroadcastSocket.bind(new InetSocketAddress(mLocalReceivePort));
                        mSendBroadcastSocket.send(mSendPacket);
                        mSendBroadcastSocket.setBroadcast(true);
                        mSendBroadcastSocket.close();

                        //随机端口
//                        DatagramSocket mSendBroadcastSocket = new DatagramSocket();
//                        mSendBroadcastSocket.send(mSendPacket);
//                        mSendBroadcastSocket.setBroadcast(true);
//                        mSendBroadcastSocket.close();
                    }
                    //释放资源
//                    lock.release();

                } catch (Exception e) {

                }
            }
        }.start();

    }


    /**
     * 发送握手消息
     *
     * @param data              协议完整的hexString数据
     * @param ip                目标地址
     * @param serverReceivePort 目标端口
     */
    public static void startSendHandMessage(byte[] data, String ip, int serverReceivePort, Context mContext) {
//        WifiManager manager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiManager.MulticastLock lock = manager.createMulticastLock("test wifi");
//        lock.acquire();    //申请开启
        LogUtils.e(TAG + "startSendHandMessage--当前发送的ip:" + ip + "端口:" + serverReceivePort);

        if (!CommonUtil.isCorrectIp2(ip)) {
            LogUtils.e(TAG + "startSendHandMessage---Error:ip不规则:" + ip);
            return;
        }
        LogUtils.e(TAG + "startSendHandMessage--ip格式:规则");

        if (!CommonUtil.isHttpUrl(ip)) {
            LogUtils.e(TAG + "startSendHandMessage---Error:不是url:" + ip);
            return;
        } else {
            LogUtils.e(TAG + "startSendHandMessage--是url" + ip);

        }
        MMKV mmkv = MMKV.defaultMMKV();
        int mLocalReceivePort = mmkv.decodeInt(Constants.KEY_LOCAL_RECEIVE_PORT);
        InetAddress mAddress = null;
        //点对点消息,握手
        try {
            mAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        InetAddress finalMAddress = mAddress;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    LogUtils.e(TAG + "hand消息--->" + "发送,ip:" + finalMAddress + ",port:" + serverReceivePort + ",data:" + data);
                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, finalMAddress, serverReceivePort);
//                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, finalMAddress, serverReceivePort);
//                    for (int i = 0; i < 5; i++) {
                    //随机端口
//                    DatagramSocket mSendSocket = new DatagramSocket();
//                    mSendSocket.send(mSendPacket);
//                    mSendSocket.close();
                    //固定端口
                    DatagramSocket mSendSocket = new DatagramSocket(null);
                    mSendSocket.setReuseAddress(true);
                    mSendSocket.bind(new InetSocketAddress(mLocalReceivePort));
                    mSendSocket.send(mSendPacket);
                    mSendSocket.close();
                    LogUtils.e(TAG + "hand消息--->" + " finalMAddress:" +finalMAddress);
                    LogUtils.e(TAG + "hand消息--->" + " mSendSocket.close();" );

                    //释放资源
//                    lock.release();
//                    }
                } catch (Exception e) {
                    LogUtils.e(TAG + "hand消息--->" + "发生Exception:" + e);

                }
            }
        }.start();
    }


    /**
     * @param data              字节数组
     * @param ip                ip
     * @param serverReceivePort 接收端的port
     *                          广播 授权,使用的是设置的端口,其他的点对点消息,按照协议data的port的走
     */
    public static void startSendPointMessage(byte[] data, String ip, int serverReceivePort, Context mContext) {
//        WifiManager manager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiManager.MulticastLock lock = manager.createMulticastLock("test wifi");
//        申请开启
//        lock.acquire();


        if (!CommonUtil.isCorrectIp2(ip)) {
            LogUtils.e(TAG + "startSendPointMessage---Error:ip不规则:" + ip);

            return;
        }
        if (!CommonUtil.isHttpUrl(ip)) {
            LogUtils.e(TAG + "startSendPointMessage---Error:不是url:" + ip);
            return;
        } else {
            LogUtils.e(TAG + "startSendPointMessage--是url" + ip);

        }
        LogUtils.e(TAG + "startSendPointMessage--规则规则规则规则");
        MMKV mmkv = MMKV.defaultMMKV();
        int mLocalReceivePort = mmkv.decodeInt(Constants.KEY_LOCAL_RECEIVE_PORT);
        InetAddress mAddress = null;
        //点对点消息,握手
        try {
            mAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        InetAddress finalMAddress = mAddress;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
//
                    LogUtils.e(TAG + "point消息--->" + "发送,ip:" + finalMAddress + ",port:" + serverReceivePort + ",本地告知上位机Port:" + mLocalReceivePort + ",data:" + data);

                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, finalMAddress, serverReceivePort);
//                    for (int i = 0; i < 5; i++) {
                    //随机端口
//                    DatagramSocket mSendSocket = new DatagramSocket();
//                    mSendSocket.send(mSendPacket);
//                    mSendSocket.close();
                    //固定端口
                    DatagramSocket mSendSocket = new DatagramSocket(null);
                    mSendSocket.setReuseAddress(true);
                    mSendSocket.bind(new InetSocketAddress(mLocalReceivePort));
                    mSendSocket.send(mSendPacket);
                    mSendSocket.close();
                    //释放资源
//                    lock.release();
//                    }
                } catch (Exception e) {
                    LogUtils.e(TAG + "point消息--->" + "发生Exception:" + e);
                }
            }
        }.start();


    }
}
