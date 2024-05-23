package com.company.shenzhou.bean.line;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/4/12 11:23
 * desc：
 */
public class line23CheckSteamBean {
    private String deviceNumber;
    private String appPassword;
    private String appUsername;

    @Override
    public String toString() {
        return "line23CheckSteamBean{" +
                "deviceNumber='" + deviceNumber + '\'' +
                ", appPassword='" + appPassword + '\'' +
                ", appUsername='" + appUsername + '\'' +
                '}';
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public String getAppPassword() {
        return appPassword;
    }

    public void setAppPassword(String appPassword) {
        this.appPassword = appPassword;
    }

    public String getAppUsername() {
        return appUsername;
    }

    public void setAppUsername(String appUsername) {
        this.appUsername = appUsername;
    }
}
