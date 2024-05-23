package com.company.shenzhou.bean.line;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2023/7/11 14:41
 * desc：获取直播流地址的bean--我们只获取语音通话上传Ngix的地址
 */
public class MicLiveSteamBean {

    private DataDTO data;
    private int code;
    private String msg;

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

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

    public static class DataDTO {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
