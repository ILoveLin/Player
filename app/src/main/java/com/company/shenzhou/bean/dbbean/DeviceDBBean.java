package com.company.shenzhou.bean.dbbean;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/5/19 10:29
 * desc：更新数据库的时候需要更新app.gradle的greendao节点
 */
@Entity
public class DeviceDBBean {
    //主键
    @Id(autoincrement = true)
    private Long id;            //这个主键ID是需要绑定用户表中的deviceID,确保是这个设备下,离线模式能通过id查询绑定用户
    //设备标题
    private String tag;      //cmeplayer  设备列表有多少设备,是和当前用户绑定的,不同用户通过tag查询获取属于自己当前tag的设备列表
    //设备名
    private String deviceName;  //设备名字:智能一体机
    /**
     * <~~~~设备码~~~~>
     * 备注说明一下:
     * <p>
     * 存入数据库的是:16字节16位的字符串(比如:937a5f204dc43a14)
     * socket通讯的是:接收和获取到的是-->16进制的32位字符串设备码(比如:39333761356632303464633433613134)
     * str2HexStr()----->16位转32位
     * hexStr2Str()----->32位转16位
     * <p>
     * 详细说明:
     * 上位机传递过来的是16字节16位的字符串(937a5f204dc43a14),
     * 我们接收线程中,通过API,CalculateUtils.byteArrayToHexString(mSettingDataPacket.getData()).trim();
     * 把16进制的字符串转成16进制的32位字符串设备码(39333761356632303464633433613134),
     * 但是展示的时候是需要展示16位的字符串
     * 所以我们在发消息的时候,在CalculateUtils.getSendByteData()方法里面,对Received_ID(数据库获取到的是,16位参数),通过CalculateUtils.str2HexStr()转换成32位的16进制字符串,来发消息
     * 当我们收到消息的时候,因为通过byteArrayToHexString,所以结果是16进制的32位字符串设备码(39333761356632303464633433613134),
     * 所以当我们授权的时候,需要 CalculateUtils.hexStr2Str(),32位转换成16位的字符串,然后在存入数据库
     */
    //设备码(设备唯一标识)
    private String deviceCode;  //  这个是智能搜索之后返回过来的设备码//  这个是智能搜索之后返回过来的设备码//  这个是智能搜索之后返回过来的设备码

    //备注信息
    private String msgMark;
    //设备ip
    private String ip;        //这个字段是授权接入成功之后socket获取到的通讯ip,这个字段一般情况下都是使用这个ip来socket通讯和直播都是用此ip
    //直播ip
    private String LiveIp;        //这个字段是授权接入成功之后返还的json,ip字段,叫直播ip,一般情况下不适用而是直接使用ip,不要混淆了
    /**
     * 新增的DDNS配置字段
     */
    //DDNS账号
    private String DDNSAcount;
    //DDNS密码
    private String DDNSPassword;
    //DDNS URL
    private String DDNSURL;
    //DDNS服务器IP(182.86.70.211 公司自己的)
    private String DDNSServerIP01;
    //DDNS备用服务器IP(111.230.237.90 腾讯的)
    private String DDNSServerIP02;
    //DDS 服务器,端口号
    private String DDNSServerPort;
    //DDS 备用字段01
    private String DDNSSpare01;
    //DDS 备用字段02
    private String DDNSSpare02;
    //DDS 备用字段03
    private String DDNSSpare03;

    //设备账号(直播)
    private String account;
    //设备密码(直播)
    private String password;

    //设备socket端口
    private String socketPort;
    //http 请求端口
    private String httpPort;
    //设备直播端口
    private String livePort;
    //设备语音端口
    private String micPort;

    //设备标题
    private String title;

    //设备类型(耳鼻喉治疗台)-->的中文描述
    private String deviceTypeDesc;  //!!!!此处是设备中文说明比如,一代一体机,耳鼻喉治疗台等等            备注:type和endotype不是相等的
    /**
     * 00-工作站， 01-HD3摄像机，02-冷光源，03-气腹机，04-冲洗机，05-4K摄像机，06-耳鼻喉控制板，
     * 07-一代一体机，8-耳鼻喉治疗台，9-妇科治疗台，10-泌尿治疗台
     * A0-iOS，A1-Android，FF-所有设备
     */


    /**
     * 备注:上位机和类型Dialog的position的对照表
     * * * * * * * * ("HD3", "HD3-4K", "一代一体机","耳鼻喉治疗台","妇科治疗台","泌尿治疗台","工作站","神州转播")  //对接协议 0:播放HD3,1:播放一体机,2:播放url链接地址
     * * * * * * * *   0      1        2               3           4           5           6       7
     * 对应上位机:      01     05       7/07            8/08        9/09        10          00      FF
     */
    //设备类型(8)-->的数字描述
    private String deviceTypeNum;  //!!!!此处是设备中文说明对应的数字,比如type=一代一体机  数字对应07   备注:type和endotype不是相等的//以前的字段，默认16进制

    private String deviceTypeHexNum;  //!!!!此处是设备中文说明对应的数字,比如type=一代一体机  数字对应07    字符串格式：07或者0A，是16进制的字符串
    private String deviceTypeDecNum;  //!!!!此处是设备中文说明对应的数字,比如type=一代一体机  数字对应07    字符串格式：07，是十进制的字符串


    //工作站类型
    private String endoType;
    //直播通道:线路1 p2p，线路2 Nginx，线路3 WebRTC

    //扫码出来呢channel的数字也是0-1-2；
    //后台接口存的数字是：0-1-2:分别表示线路1；线路2；线路3；App里面显示的是线路1，2，3
    private String channel;
    //备用字段00
    private String usemsg00;  //00号备用字段
    private String usemsg01;  //01号备用字段
    //是否被选中
    private Boolean mSelected;    //是否被选中  默认未选中  :0未选中,1被选中(设备搜索的时候需要用到)
    //API版本号
    private String apiVersion;    //当前设备http请求，请求的接口版本号（不同设备可能是不同的版本）
    //公网Ip地址
    private String ipPublic;

    //备用方案是否开启备用方案
    private boolean sparePlan ;      //默认未开启==false
    //备用方案 语音推流地址
    private String spareMicPushSteam;
    ///备用方案 直播流，推流地址
    private String spareLiveSteam;


    /**
     * 设备唯一值
     * 最新修改唯一值的方案
     * <p>
     * 和ios商讨之后的结果
     * 其他设备:deviceID（上位机设备码）+deviceType(设备类型数字）+通道------>b0087fc6fa584b6208通道1(08是耳鼻喉治疗台)
     * 同一个设备，不同的通道算一个新的设备
     * RC200设备:RC200默认是空，可重复添加设备。
     * <p>
     * <p>
     * Android本地实际的操作
     * 其他设备:deviceID（上位机设备码）+deviceType(设备类型中文说明）+通道--->b0087fc6fa584b62耳鼻喉治疗台通道1
     * 同一个设备，不同的通道算一个新的设备
     * RC200设备:但是我们这边用,当前设备ip(RC200的ip地址)+deviceType(设备类型中文说明)+username(手机用户登入名),比如:192.168.71.159RC200admin
     * <p>
     * <p>
     */
    private String acceptAndInsertDB;

    /**
     * //存入数据库的的时候,这个字段为空,之后登入成功,需要绑定当前登入的用户名,这样在切换用户搜一搜的时候根据用户,来显示是否已经添加过
     * //每个用户搜一搜之后只能显示当前知己,已添加过的设备
     * 每次登入的时候,判断,已添加过的设备,list包含这个名字,则不存,反之,需要主动存入当前用户名
     */
    @Convert(columnType = String.class, converter = DownBindNameConverter.class)
    private List<DownBindNameListBean> downBingNameList;

    @Override
    public String toString() {
        return "DeviceDBBean{" +
                "id=" + id +
                ", tag='" + tag + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceCode='" + deviceCode + '\'' +
                ", msgMark='" + msgMark + '\'' +
                ", ip='" + ip + '\'' +
                ", LiveIp='" + LiveIp + '\'' +
                ", DDNSAcount='" + DDNSAcount + '\'' +
                ", DDNSPassword='" + DDNSPassword + '\'' +
                ", DDNSURL='" + DDNSURL + '\'' +
                ", DDNSServerIP01='" + DDNSServerIP01 + '\'' +
                ", DDNSServerIP02='" + DDNSServerIP02 + '\'' +
                ", DDNSServerPort='" + DDNSServerPort + '\'' +
                ", DDNSSpare01='" + DDNSSpare01 + '\'' +
                ", DDNSSpare02='" + DDNSSpare02 + '\'' +
                ", DDNSSpare03='" + DDNSSpare03 + '\'' +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", socketPort='" + socketPort + '\'' +
                ", httpPort='" + httpPort + '\'' +
                ", livePort='" + livePort + '\'' +
                ", micPort='" + micPort + '\'' +
                ", title='" + title + '\'' +
                ", deviceTypeDesc='" + deviceTypeDesc + '\'' +
                ", deviceTypeNum='" + deviceTypeNum + '\'' +
                ", deviceTypeHexNum='" + deviceTypeHexNum + '\'' +
                ", deviceTypeDecNum='" + deviceTypeDecNum + '\'' +
                ", endoType='" + endoType + '\'' +
                ", channel='" + channel + '\'' +
                ", usemsg00='" + usemsg00 + '\'' +
                ", usemsg01='" + usemsg01 + '\'' +
                ", mSelected=" + mSelected +
                ", apiVersion='" + apiVersion + '\'' +
                ", ipPublic='" + ipPublic + '\'' +
                ", sparePlan=" + sparePlan +
                ", spareMicPushSteam='" + spareMicPushSteam + '\'' +
                ", spareLiveSteam='" + spareLiveSteam + '\'' +
                ", acceptAndInsertDB='" + acceptAndInsertDB + '\'' +
                ", downBingNameList=" + downBingNameList +
                '}';
    }

    @Keep
    public DeviceDBBean(Long id, String tag, String deviceName, String deviceCode, String msgMark, String ip, String LiveIp, String DDNSAcount, String DDNSPassword, String DDNSURL,
                        String DDNSServerIP01, String DDNSServerIP02, String DDNSServerPort, String DDNSSpare01, String DDNSSpare02, String DDNSSpare03, String account, String password,
                        String socketPort, String httpPort, String livePort, String micPort, String title, String deviceTypeDesc, String deviceTypeNum, String endoType, String channel,
                        String usemsg01, String usemsg00, Boolean mSelected, String acceptAndInsertDB, List<DownBindNameListBean> downBingNameList) {
        this.id = id;
        this.tag = tag;
        this.deviceName = deviceName;
        this.deviceCode = deviceCode;
        this.msgMark = msgMark;
        this.ip = ip;
        this.LiveIp = LiveIp;
        this.DDNSAcount = DDNSAcount;
        this.DDNSPassword = DDNSPassword;
        this.DDNSURL = DDNSURL;
        this.DDNSServerIP01 = DDNSServerIP01;
        this.DDNSServerIP02 = DDNSServerIP02;
        this.DDNSServerPort = DDNSServerPort;
        this.DDNSSpare01 = DDNSSpare01;
        this.DDNSSpare02 = DDNSSpare02;
        this.DDNSSpare03 = DDNSSpare03;
        this.account = account;
        this.password = password;
        this.socketPort = socketPort;
        this.httpPort = httpPort;
        this.livePort = livePort;
        this.micPort = micPort;
        this.title = title;
        this.deviceTypeDesc = deviceTypeDesc;
        this.deviceTypeNum = deviceTypeNum;
        this.endoType = endoType;
        this.channel = channel;
        this.usemsg00 = usemsg00;
        this.usemsg01 = usemsg01;
        this.mSelected = mSelected;
        this.acceptAndInsertDB = acceptAndInsertDB;
        this.downBingNameList = downBingNameList;
    }

    @Generated(hash = 1828217020)
    public DeviceDBBean() {
    }

    @Generated(hash = 1328319281)
    public DeviceDBBean(Long id, String tag, String deviceName, String deviceCode, String msgMark, String ip, String LiveIp, String DDNSAcount, String DDNSPassword, String DDNSURL, String DDNSServerIP01,
            String DDNSServerIP02, String DDNSServerPort, String DDNSSpare01, String DDNSSpare02, String DDNSSpare03, String account, String password, String socketPort, String httpPort, String livePort,
            String micPort, String title, String deviceTypeDesc, String deviceTypeNum, String deviceTypeHexNum, String deviceTypeDecNum, String endoType, String channel, String usemsg00, String usemsg01,
            Boolean mSelected, String apiVersion, String ipPublic, boolean sparePlan, String spareMicPushSteam, String spareLiveSteam, String acceptAndInsertDB,
            List<DownBindNameListBean> downBingNameList) {
        this.id = id;
        this.tag = tag;
        this.deviceName = deviceName;
        this.deviceCode = deviceCode;
        this.msgMark = msgMark;
        this.ip = ip;
        this.LiveIp = LiveIp;
        this.DDNSAcount = DDNSAcount;
        this.DDNSPassword = DDNSPassword;
        this.DDNSURL = DDNSURL;
        this.DDNSServerIP01 = DDNSServerIP01;
        this.DDNSServerIP02 = DDNSServerIP02;
        this.DDNSServerPort = DDNSServerPort;
        this.DDNSSpare01 = DDNSSpare01;
        this.DDNSSpare02 = DDNSSpare02;
        this.DDNSSpare03 = DDNSSpare03;
        this.account = account;
        this.password = password;
        this.socketPort = socketPort;
        this.httpPort = httpPort;
        this.livePort = livePort;
        this.micPort = micPort;
        this.title = title;
        this.deviceTypeDesc = deviceTypeDesc;
        this.deviceTypeNum = deviceTypeNum;
        this.deviceTypeHexNum = deviceTypeHexNum;
        this.deviceTypeDecNum = deviceTypeDecNum;
        this.endoType = endoType;
        this.channel = channel;
        this.usemsg00 = usemsg00;
        this.usemsg01 = usemsg01;
        this.mSelected = mSelected;
        this.apiVersion = apiVersion;
        this.ipPublic = ipPublic;
        this.sparePlan = sparePlan;
        this.spareMicPushSteam = spareMicPushSteam;
        this.spareLiveSteam = spareLiveSteam;
        this.acceptAndInsertDB = acceptAndInsertDB;
        this.downBingNameList = downBingNameList;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceCode() {
        return this.deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getMsgMark() {
        return this.msgMark;
    }

    public void setMsgMark(String msgMark) {
        this.msgMark = msgMark;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLiveIp() {
        return this.LiveIp;
    }

    public void setLiveIp(String LiveIp) {
        this.LiveIp = LiveIp;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSocketPort() {
        return this.socketPort;
    }

    public void setSocketPort(String socketPort) {
        this.socketPort = socketPort;
    }

    public String getLivePort() {
        return this.livePort;
    }

    public void setLivePort(String livePort) {
        this.livePort = livePort;
    }

    public String getMicPort() {
        return this.micPort;
    }

    public void setMicPort(String micPort) {
        this.micPort = micPort;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeviceTypeDesc() {
        return this.deviceTypeDesc;
    }

    public void setDeviceTypeDesc(String deviceTypeDesc) {
        this.deviceTypeDesc = deviceTypeDesc;
    }

    public String getDeviceTypeNum() {
        return this.deviceTypeNum;
    }

    public void setDeviceTypeNum(String deviceTypeNum) {
        this.deviceTypeNum = deviceTypeNum;
    }

    public String getEndoType() {
        return this.endoType;
    }

    public void setEndoType(String endoType) {
        this.endoType = endoType;
    }

    public String getUsemsg01() {
        return this.usemsg01;
    }

    public void setUsemsg01(String usemsg01) {
        this.usemsg01 = usemsg01;
    }


    public String getUsemsg00() {
        return this.usemsg00;
    }

    public void setUsemsg00(String usemsg00) {
        this.usemsg00 = usemsg00;
    }

    public Boolean getMSelected() {
        return this.mSelected;
    }

    public void setMSelected(Boolean mSelected) {
        this.mSelected = mSelected;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAcceptAndInsertDB() {
        return this.acceptAndInsertDB;
    }

    public void setAcceptAndInsertDB(String acceptAndInsertDB) {
        this.acceptAndInsertDB = acceptAndInsertDB;
    }

    public String getHttpPort() {
        return this.httpPort;
    }

    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }

    public List<DownBindNameListBean> getDownBingNameList() {
        return this.downBingNameList;
    }

    public void setDownBingNameList(List<DownBindNameListBean> downBingNameList) {
        this.downBingNameList = downBingNameList;
    }

    public String getDDNSAcount() {
        return this.DDNSAcount;
    }

    public void setDDNSAcount(String DDNSAcount) {
        this.DDNSAcount = DDNSAcount;
    }

    public String getDDNSPassword() {
        return this.DDNSPassword;
    }

    public void setDDNSPassword(String DDNSPassword) {
        this.DDNSPassword = DDNSPassword;
    }

    public String getDDNSServerIP01() {
        return this.DDNSServerIP01;
    }

    public void setDDNSServerIP01(String DDNSServerIP01) {
        this.DDNSServerIP01 = DDNSServerIP01;
    }

    public String getDDNSServerIP02() {
        return this.DDNSServerIP02;
    }

    public void setDDNSServerIP02(String DDNSServerIP02) {
        this.DDNSServerIP02 = DDNSServerIP02;
    }

    public String getDDNSServerPort() {
        return this.DDNSServerPort;
    }

    public void setDDNSServerPort(String DDNSServerPort) {
        this.DDNSServerPort = DDNSServerPort;
    }

    public String getDDNSSpare01() {
        return this.DDNSSpare01;
    }

    public void setDDNSSpare01(String DDNSSpare01) {
        this.DDNSSpare01 = DDNSSpare01;
    }

    public String getDDNSSpare02() {
        return this.DDNSSpare02;
    }

    public void setDDNSSpare02(String DDNSSpare02) {
        this.DDNSSpare02 = DDNSSpare02;
    }

    public String getDDNSSpare03() {
        return this.DDNSSpare03;
    }

    public void setDDNSSpare03(String DDNSSpare03) {
        this.DDNSSpare03 = DDNSSpare03;
    }

    public String getDDNSURL() {
        return this.DDNSURL;
    }

    public void setDDNSURL(String DDNSURL) {
        this.DDNSURL = DDNSURL;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Boolean getmSelected() {
        return mSelected;
    }

    public void setmSelected(Boolean mSelected) {
        this.mSelected = mSelected;
    }

    public String getApiVersion() {
        return this.apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getIpPublic() {
        return this.ipPublic;
    }

    public void setIpPublic(String ipPublic) {
        this.ipPublic = ipPublic;
    }

    public boolean getSparePlan() {
        return this.sparePlan;
    }

    public void setSparePlan(boolean sparePlan) {
        this.sparePlan = sparePlan;
    }

    public String getSpareMicPushSteam() {
        return this.spareMicPushSteam;
    }

    public void setSpareMicPushSteam(String spareMicPushSteam) {
        this.spareMicPushSteam = spareMicPushSteam;
    }

    public String getSpareLiveSteam() {
        return this.spareLiveSteam;
    }

    public void setSpareLiveSteam(String spareLiveSteam) {
        this.spareLiveSteam = spareLiveSteam;
    }

    public String getDeviceTypeHexNum() {
        return this.deviceTypeHexNum;
    }

    public void setDeviceTypeHexNum(String deviceTypeHexNum) {
        this.deviceTypeHexNum = deviceTypeHexNum;
    }

    public String getDeviceTypeDecNum() {
        return this.deviceTypeDecNum;
    }

    public void setDeviceTypeDecNum(String deviceTypeDecNum) {
        this.deviceTypeDecNum = deviceTypeDecNum;
    }


}
