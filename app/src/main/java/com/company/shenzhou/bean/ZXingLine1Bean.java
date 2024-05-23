package com.company.shenzhou.bean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/4/1 9:42
 * desc  线路1的数据bean
 */
public class ZXingLine1Bean {
    /**
     * 原来的
     * {
     * "DDNSPwd" : "szcme24683579",
     * "DDNSUrl" : "c198d4aae21caa00.com";,
     * "DDNSUser" : "c198d4aae21caa00",
     * "channel" : 0,
     * "deviceID" : "c9dbbd334cff73d2",
     * "deviceType" : 8,
     * "endoType" : 3,
     * "httpPort" : 7001,
     * "ip" : "",
     * "ipPublic" : "",
     * "makemsg" : "ENT",                //最新版删除了
     * "micport" : 0,                    //最新版删除了
     * "type" : 1,                       //最新版删除了
     * "password" : "root1",
     * "port" : 7788,
     * "socketPort" : 7006,
     * "title" : "ENT-LIVE",
     * "username" : "root1"
     * }
     * <p>
     * <p>
     * 最新缩写版本，其中line直播线路等于1的时候才会出现3个DDNS字段
     * <p>
     * {
     * "dpw": "szcme24683579",             //DDNSPwd
     * "durl": "c198d4aae21caa00.com",     //DDNSUrl
     * "dun": "c198d4aae21caa00",          //DDNSUser
     * "ln": 0,                            //channel
     * "id": "0fd7899cd217a995",           //deviceID
     * "dtp": 9,                           //deviceType
     * "etp": 4,                           //endoType
     * "htp": 7001,                        //httpPort
     * "ip": "",                           //ip
     * "ip2": "",                          //ipPublic
     * "pw": "root",                       //password
     * "pt": 7788,                         //port
     * "spt": 7006,                        //socketPort
     * "ti": "GDT-LIVE",                   //title
     * "un": "root",                       //username
     * "v": "1.0.0.0"                      //api版本号
     * }
     */

    private String dpw;
    private String durl;
    private String dun;
    private String ln;
    private String id;
    private String dtp;
    private String etp;
    private String htp;
    private String ip;
    private String ip2;
    private String pw;
    private String pt;
    private String spt;
    private String ti;
    private String un;
    private String v;

    @Override
    public String toString() {
        return "ZXingLine23Bean{" +
                "dpw='" + dpw + '\'' +
                ", durl='" + durl + '\'' +
                ", dun='" + dun + '\'' +
                ", ln='" + ln + '\'' +
                ", id='" + id + '\'' +
                ", dtp='" + dtp + '\'' +
                ", etp='" + etp + '\'' +
                ", htp='" + htp + '\'' +
                ", ip='" + ip + '\'' +
                ", ip2='" + ip2 + '\'' +
                ", pw='" + pw + '\'' +
                ", pt='" + pt + '\'' +
                ", spt='" + spt + '\'' +
                ", ti='" + ti + '\'' +
                ", un='" + un + '\'' +
                ", v='" + v + '\'' +
                '}';
    }

    public String getDpw() {
        return dpw;
    }

    public void setDpw(String dpw) {
        this.dpw = dpw;
    }

    public String getDurl() {
        return durl;
    }

    public void setDurl(String durl) {
        this.durl = durl;
    }

    public String getDun() {
        return dun;
    }

    public void setDun(String dun) {
        this.dun = dun;
    }

    public String getLn() {
        return ln;
    }

    public void setLn(String ln) {
        this.ln = ln;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDtp() {
        return dtp;
    }

    public void setDtp(String dtp) {
        this.dtp = dtp;
    }

    public String getEtp() {
        return etp;
    }

    public void setEtp(String etp) {
        this.etp = etp;
    }

    public String getHtp() {
        return htp;
    }

    public void setHtp(String htp) {
        this.htp = htp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp2() {
        return ip2;
    }

    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getPt() {
        return pt;
    }

    public void setPt(String pt) {
        this.pt = pt;
    }

    public String getSpt() {
        return spt;
    }

    public void setSpt(String spt) {
        this.spt = spt;
    }

    public String getTi() {
        return ti;
    }

    public void setTi(String ti) {
        this.ti = ti;
    }

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }




}
