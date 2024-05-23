package com.company.shenzhou.bean.rc200;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2023/9/6 9:29
 * desc：
 */
public class RC200SettingBean {

    private String imageFormat;
    private int rs232Baudrate;
    private String rs232Parity;
    private String quality;
    private int ethernetStatic;
    private String ethernetIp;
    private String ethernetMask;
    private String ethernetGateway;
    private int hdmiOutPower;
    private String hdmiOutColor;
    private String hdmiOutMode;
    private List<UsersDTO> users;
    private int audioAllowStream;
    private int audioAllowListen;
    private String pingTestAddress;
    private List<?> qualityProfiles;
    private String ethernetMac;
    private String hostname;
    private Object audioMicGain;
    private Object audioOutVolume;

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public int getRs232Baudrate() {
        return rs232Baudrate;
    }

    public void setRs232Baudrate(int rs232Baudrate) {
        this.rs232Baudrate = rs232Baudrate;
    }

    public String getRs232Parity() {
        return rs232Parity;
    }

    public void setRs232Parity(String rs232Parity) {
        this.rs232Parity = rs232Parity;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public int getEthernetStatic() {
        return ethernetStatic;
    }

    public void setEthernetStatic(int ethernetStatic) {
        this.ethernetStatic = ethernetStatic;
    }

    public String getEthernetIp() {
        return ethernetIp;
    }

    public void setEthernetIp(String ethernetIp) {
        this.ethernetIp = ethernetIp;
    }

    public String getEthernetMask() {
        return ethernetMask;
    }

    public void setEthernetMask(String ethernetMask) {
        this.ethernetMask = ethernetMask;
    }

    public String getEthernetGateway() {
        return ethernetGateway;
    }

    public void setEthernetGateway(String ethernetGateway) {
        this.ethernetGateway = ethernetGateway;
    }

    public int getHdmiOutPower() {
        return hdmiOutPower;
    }

    public void setHdmiOutPower(int hdmiOutPower) {
        this.hdmiOutPower = hdmiOutPower;
    }

    public String getHdmiOutColor() {
        return hdmiOutColor;
    }

    public void setHdmiOutColor(String hdmiOutColor) {
        this.hdmiOutColor = hdmiOutColor;
    }

    public String getHdmiOutMode() {
        return hdmiOutMode;
    }

    public void setHdmiOutMode(String hdmiOutMode) {
        this.hdmiOutMode = hdmiOutMode;
    }

    public List<UsersDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UsersDTO> users) {
        this.users = users;
    }

    public int getAudioAllowStream() {
        return audioAllowStream;
    }

    public void setAudioAllowStream(int audioAllowStream) {
        this.audioAllowStream = audioAllowStream;
    }

    public int getAudioAllowListen() {
        return audioAllowListen;
    }

    public void setAudioAllowListen(int audioAllowListen) {
        this.audioAllowListen = audioAllowListen;
    }

    public String getPingTestAddress() {
        return pingTestAddress;
    }

    public void setPingTestAddress(String pingTestAddress) {
        this.pingTestAddress = pingTestAddress;
    }

    public List<?> getQualityProfiles() {
        return qualityProfiles;
    }

    public void setQualityProfiles(List<?> qualityProfiles) {
        this.qualityProfiles = qualityProfiles;
    }

    public String getEthernetMac() {
        return ethernetMac;
    }

    public void setEthernetMac(String ethernetMac) {
        this.ethernetMac = ethernetMac;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Object getAudioMicGain() {
        return audioMicGain;
    }

    public void setAudioMicGain(Object audioMicGain) {
        this.audioMicGain = audioMicGain;
    }

    public Object getAudioOutVolume() {
        return audioOutVolume;
    }

    public void setAudioOutVolume(Object audioOutVolume) {
        this.audioOutVolume = audioOutVolume;
    }

    public static class UsersDTO {
        private String name;
        private String password;
        private String role;
        private long loginTime;
        private long createTime;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public long getLoginTime() {
            return loginTime;
        }

        public void setLoginTime(long loginTime) {
            this.loginTime = loginTime;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}
