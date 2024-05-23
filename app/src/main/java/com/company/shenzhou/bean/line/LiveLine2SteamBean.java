package com.company.shenzhou.bean.line;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/3/22 10:01
 * desc：
 * 线路2
 *
 *
 */
public class LiveLine2SteamBean {


    private int code;
    private String msg;
    private ResultDTO result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultDTO getResult() {
        return result;
    }

    public void setResult(ResultDTO result) {
        this.result = result;
    }

    public static class ResultDTO {
        private String pullUrl; //视频拉流地址
        private String pushUrl; //语音推流地址

        public String getPushUrl() {
            return pushUrl;
        }

        public void setPushUrl(String pushUrl) {
            this.pushUrl = pushUrl;
        }

        public String getPullUrl() {
            return pullUrl;
        }

        public void setPullUrl(String pullUrl) {
            this.pullUrl = pullUrl;
        }
    }
}
