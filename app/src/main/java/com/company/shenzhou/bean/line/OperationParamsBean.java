package com.company.shenzhou.bean.line;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2023/7/11 15:04
 * desc：1.3,操作语音通讯表的请求参数bean
 */
public class OperationParamsBean {

    private String sendId;
    private String sendType;
    private String receiveId;
    private String operation;
    private String errCode;
    private ExtraDataDTO extraData;

    @Override
    public String toString() {
        return "OperationParamsBean{" +
                "sendID='" + sendId + '\'' +
                ", sendType='" + sendType + '\'' +
                ", receiveID='" + receiveId + '\'' +
                ", operation='" + operation + '\'' +
                ", errCode='" + errCode + '\'' +
                ", extraData=" + extraData +
                '}';
    }

    public String getSendID() {
        return sendId;
    }

    public void setSendID(String sendID) {
        this.sendId = sendID;
    }

    public String getReceiveID() {
        return receiveId;
    }

    public void setReceiveID(String receiveID) {
        this.receiveId = receiveID;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public ExtraDataDTO getExtraData() {
        return extraData;
    }

    public void setExtraData(ExtraDataDTO extraData) {
        this.extraData = extraData;
    }

    public static class ExtraDataDTO {
        private String Name;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            this.Name = name;
        }

        @Override
        public String toString() {
            return "ExtraDataDTO{" +
                    "Name='" + Name + '\'' +
                    '}';
        }
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
}
