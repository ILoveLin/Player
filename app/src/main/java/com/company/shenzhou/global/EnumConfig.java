package com.company.shenzhou.global;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2023/4/20 8:44
 * desc：
 */
public class EnumConfig {
    //播放模式
    public class VoiceType {
        public final static int HAVE_VOICE = 0;
        public final static int HAVE_NO_VOICE = 1;
    }

    //播放样式 展开、缩放
    public class PageType {
        public final static int EXPAND = 0;
        public final static int SHRINK = 1;
    }

    //WebRTC TCPlayer 是否开启备用方案的状态:默认为关闭(0),
    public class TCPlayerSpareType {
        public final static int CLOSE = 0;//此时弹窗显示,开启备用方案文字
        public final static int OPEN = 1; //此时弹窗显示,关闭备用方案文字
    }


    //录像状态状态
    public class RecodeState {
        public final static int RECODE = 0;   //录像中
        public final static int UN_RECODE = 1; //未录像
    }


    //播放状态
    public class PlayState {
        public static final int STATE_PLAY = 1;
        public static final int STATE_PAUSE = 2;
        public static final int STATE_LOAD = 3;
        public static final int STATE_RESUME = 4;
        public static final int STATE_STOP = 5;
    }

    //播放View状态显示加载,播放,错误view
    //播放状态
    public class PlayerState {
        public static final int PLAYER_STATUE_SHOW_LOADING = 1;        //显示加载view
        public static final int PLAYER_STATUE_SHOW_PLAYING = 2;          //显示播放view
        public static final int PLAYER_STATUE_SHOW_ERROR = 3;          //显示错误view
        public static final int PLAYER_STATUE_HIDE_LOADING = 4;         //隐藏加载view

    }



    //网络模式 局域网,ddns外网
    public class NetType {
        public final static int WIFI = 0;   //局域网
        public final static int DDNS = 1; //ddns外网
    }

    //网络模式中, 具体的步骤
    public class NetIndex {
        public final static int WIFI_A = 0; //局域网-第一步
        public final static int DDNS_A = 1; //ddns外网-第一步
        public final static int DDNS_B = 2; //ddns外网-第二步
    }


    //网络模式中, 具体的步骤
    public class PlayerOperation {
        public final static int RECORD = 0;     //录像操作
        public final static int SCREENSHOT = 1; //截图操作
    }

}
