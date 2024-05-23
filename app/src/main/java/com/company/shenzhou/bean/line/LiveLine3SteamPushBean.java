package com.company.shenzhou.bean.line;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2023/7/11 14:58
 *
 * 线路3 腾讯云WebRTC
 * desc：获取(语音推流)地址
 *
 */
public class LiveLine3SteamPushBean {

    @SerializedName("code")
    public int code;
    @SerializedName("msg")
    public String msg;
    @SerializedName("result")
    public ResultDTO result;

    public static class ResultDTO {
        @SerializedName("pushUrl")
        public String pushUrl;
        @SerializedName("roomNumber")
        public String roomNumber;
        @SerializedName("sessionNumber")
        public String sessionNumber;
        @SerializedName("validTime")
        public String validTime;
    }
}
