package com.company.shenzhou.bean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/4/2 10:31
 * desc：App,线路2线路3，校验是否可以获取直播流（app专用）
 */
public class AppDeviceInfoBean {


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
        private boolean check;
        private String currentLine; //当前设备，播放的线路，需要和本地设备所对应的线路比对

        @Override
        public String toString() {
            return "ResultDTO{" +
                    "check=" + check +
                    ", currentLine='" + currentLine + '\'' +
                    '}';
        }

        public String getCurrentLine() {
            return currentLine;
        }

        public void setCurrentLine(String currentLine) {
            this.currentLine = currentLine;
        }

        public boolean isCheck() {
            return check;
        }

        public void setCheck(boolean check) {
            this.check = check;
        }
    }
}
