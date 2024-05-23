package com.company.shenzhou.bean.line;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/3/18 15:11
 * desc：
 */
public class TestLiveSteamBean {


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
        private String pushUrl;
        private String roomNumber;
        private String sessionNumber;

        public String getPushUrl() {
            return pushUrl;
        }

        public void setPushUrl(String pushUrl) {
            this.pushUrl = pushUrl;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public void setRoomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
        }

        public String getSessionNumber() {
            return sessionNumber;
        }

        public void setSessionNumber(String sessionNumber) {
            this.sessionNumber = sessionNumber;
        }
    }
}
