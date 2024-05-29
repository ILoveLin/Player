package com.company.shenzhou.bean;

/**
 * LoveLin
 * <p>
 * Describe
 */
public class RefreshEvent {
    private String type;
    private String toastStr;

    public RefreshEvent(String type) {
        this.type = type;
    }

    public RefreshEvent(String type, String toastStr) {
        this.type = type;
        this.toastStr = toastStr;
    }

    public String getToastStr() {
        return toastStr;
    }

    public void setToastStr(String toastStr) {
        this.toastStr = toastStr;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
