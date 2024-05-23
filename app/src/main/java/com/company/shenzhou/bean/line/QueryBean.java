package com.company.shenzhou.bean.line;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2023/7/11 15:05
 * desc：查询语音通讯表返回结果Bean
 */
public class QueryBean {

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
        private List<DataDTO> data;

        public List<DataDTO> getData() {
            return data;
        }

        public void setData(List<DataDTO> data) {
            this.data = data;
        }
    }

    public static class DataDTO {
        @Override
        public String toString() {
            return "DataDTO{" +
                    "create_date='" + create_date + '\'' +
                    ", iD=" + id +
                    ", sendID='" + sendId + '\'' +
                    ", receiveID='" + receiveId + '\'' +
                    ", operation='" + operation + '\'' +
                    ", extraData='" + extraData + '\'' +
                    ", createDate='" + createDate + '\'' +
                    '}';
        }

        private String create_date;
        @SerializedName("id")
        private int id;
        @SerializedName("sendId")
        private String sendId;
        @SerializedName("sendType")
        private String sendType;
        @SerializedName("receiveId")
        private String receiveId;
        @SerializedName("operation")
        private String operation;
        @SerializedName("ErrCode")
        private String errCode;
        @SerializedName("extraData")
        private String extraData;
        @SerializedName("createDate")
        private String createDate;

        public String getSendType() {
            return sendType;
        }

        public void setSendType(String sendType) {
            sendType = sendType;
        }

        public String getErrCode() {
            return errCode;
        }

        public void setErrCode(String errCode) {
            errCode = errCode;
        }

        public String getCreate_date() {
            return create_date;
        }

        public void setCreate_date(String create_date) {
            this.create_date = create_date;
        }

        public int getID() {
            return id;
        }

        public void setID(int iD) {
            this.id = iD;
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

        public String getExtraData() {
            return extraData;
        }

        public void setExtraData(String extraData) {
            this.extraData = extraData;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }
    }
}
