package com.company.shenzhou.bean.line;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2023/7/11 14:58
 *
 * 线路3 腾讯云WebRTC
 * desc：获取(直播画面)拉流地址
 *
 */
public class LiveLine3SteamPullBean {

    @SerializedName("code")
    public String code;
    @SerializedName("msg")
    public String msg;
    @SerializedName("result")
    public ResultDTO result;

    public static class ResultDTO {
        @SerializedName("roomNumber")
        public String roomNumber;
        @SerializedName("pullUrls")
        public List<PullUrlsDTO> pullUrls;

        public static class PullUrlsDTO {
            @SerializedName("pullUrl")
            public String pullUrl;
            @SerializedName("pusherNumber")
            public String pusherNumber;
            @SerializedName("pusherPlatform")
            public String pusherPlatform;
            @SerializedName("validTime")
            public String validTime;
        }
    }
}
