package com.company.shenzhou.global;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/28 14:15
 * desc：
 */
public class Constants {
    /**
     * 本地数据库
     * 0=普通用户，最低权限
     * 1=权限用户，中等权限
     * 0=超级管理员，最高权限
     */
    public static final int GeneralUser = 0;               //普通用户
    public static final int PermissionUser = 1;            //权限用户
    public static final int AdminUser = 2;                 //超级管理员

    //引导页
    public static final String SP_IS_FIRST_IN = "sp_is_first_in";  //是否第一次登入
    public static final String Sp_UserAgreement_Tag = "sp_useragreement_tag";  //用户是否同意了----用户协议的标识,默认flase
    public static final String Sp_Record_Tag = "Sp_Record_Tag";  //初次安装的时候,录像的时候,第一次申请权限,出现录像成功bug
    public static final String Sp_Shot_Tag = "Sp_Shot_Tag";         //初次安装的时候,录像的时候,第一次申请权限,出现截图成功bug
    public static final String Is_Logined = "is_logined";          //是否已经登入   false  未登录

    //系统和请求头
    public static final String Token = "token";
    public static final String Device = "android";


    public enum MicStatue {
        //通话成功
        SUCESS,//成功
        ERROR,//失败
        INTERRUPT,//终止
        PREPARE//准备中
    }

    public enum MicOperation {


        //0：表示不做任何事情                忽略
        //1: 请求加入列表（功能开启）         忽略
        //2：请上传音频流到Nginx；
        //3：请从Nginx拉取音频流
        //5：通话结束
        //6：请求从列表中删除（功能关闭）
        //通话成功动作
        MicOperation0,
        MicOperation1,
        MicOperation2,
        MicOperation3,
        MicOperation4,
        MicOperation5,
        MicOperation6,
    }

    public class CMEAudio {
        //通话成功动作
//        CMEAudioNormal,         //默认状态，啥也没点
//        CMEAudioJoinAudioList, //加入语音列表
//        CMEAudioGetPushUzl, //获取推流地址
//        CMEAudioPushing,    //推流中
//        CMEAudioPushSuccess,    //推流成功
//        CMEAudioConnected,  //已连接
//        CMEAudioHangUp,     //挂断

        public static final int CMEAudioNormal = 1;
        public static final int CMEAudioJoinAudioList = 2;
        public static final int CMEAudioGetPushUzl = 3;
        public static final int CMEAudioPushing = 4;
        public static final int CMEAudioPushSuccess = 5;
        public static final int CMEAudioConnected = 6;
        public static final int CMEAudioHangUp = 7;

    }

    public class PlayMode {
        public static final String Normal = "Normal";   //常规
        public static final String Spare = "Spare";     //备用

    }

    public class ApiVersion {
        public static final String V1 = "1.0.0.0";

    }


    /**
     * VLC播放模式
     * 默认:0==模式一(常规socket通讯),1==通道123(http模式),2==通道456(腾讯云转播)
     */
    public static final String KEY_VLC_PLAYER_CHANNEL = "KEY_VLC_PLAYER_MODE";          //0=模式1,1=模式2,2=模式3;(VLC播放模式)
    public static final String PLAYER_CHANNEL2 = "0";                                   //线路1:(常规socket通讯,p2p);线路2:(http模式);线路3:(腾讯云);
    public static final String PLAYER_CHANNEL1 = "1";                                   //线路1:(常规socket通讯,p2p);线路2:(http模式);线路3:(腾讯云);
    public static final String PLAYER_CHANNEL3 = "2";                                   //线路1:(常规socket通讯,p2p);线路2:(http模式);线路3:(腾讯云);
    //正式服务器  https://111.77.154.44
    //public static final String BaseUrl = "http://www.cmejx.com:50002/api";
    public static final String BaseUrl = "https://www.cmejx.com:60002/api";
    //测试服务器
    //public static final String BaseUrl = "https://www.cmejx.com:55000/api";
    /**
     *
     * 线路 2 的接口
     */

    /**
     * 推流平台传的推流平台都是10进制的
     * 1.1（线路2） 获取推流地址-get
     */
    public static final String Live_Line2_PushUrl = BaseUrl + "/line2/getpushUrl";
    /**
     * 1.2 （线路2） 获取拉流地址
     */
    public static final String Live_Line2_PullUrl = BaseUrl + "/line2/getpullUrl";

    /**
     *
     * 线路 3 的接口
     */
    /**
     * 1.1 （线路3） 获取拉流地址----播放视频--会返回roomNumber
     */
    public static final String Live_line3_PullUrl = BaseUrl + "/stream/getpullUrl";
    /**
     * 推流平台传的推流平台都是10进制的
     * 1.2（线路3） 获取推流地址----打开语音
     */
    public static final String Live_Line3_PushUrl = BaseUrl + "/stream/getpushurl";


    /**
     * 1.3 操作语音通讯表-post
     * http://192.168.66.164:7002/live/audioControl
     * <p>
     * sendID=发送端ID
     * receiveID=接收端ID
     * Operation=1=操作:
     * //0：表示不做任何事情
     * //1: 请求加入列表（功能开启）
     * //2：请上传音频流到Nginx；
     * //3：请从Nginx拉取音频流
     * //5：通话结束
     * //6：请求从列表中删除（功能关闭）
     * ExtraData={ "name": "王文_android"}=接收端ID
     */
    public static final String Live_audioControl = BaseUrl + "/liveControl/audioControl";

    /**
     * 1.4 查询语音通讯表-get
     * http://192.168.66.164:7002/live/audioQuery
     * <p>
     * receiveID=接收端ID
     */
    public static final String Live_audioQuery = BaseUrl + "/liveControl/audioQuery";


    //App,线路2线路3，校验是否可以获取直播流（app专用）
    public static final String Live_checkAppDeviceInfo = BaseUrl + "/liveControl/checkAppDeviceInfo";

    /**
     * #### 神州医疗Socket通讯端口配置信息
     * #### CMEPlayer项目
     * #### 广播服务端口8006
     * #### 本地监听端口8005
     * #### Socket通讯服务器端口授权设备返回的socket端口值-->Stp:socke udp接收端口;
     * #### 不管是广播还是socket通讯,本地都是监听默认值端口
     * ####
     * ####
     * #### iEndo项目
     * #### 广播服务端口7006
     * #### 本地监听端口7005
     * #### Socket通讯服务器端口授权设备返回的socket端口值-->Stp:socke udp接收端口;
     * #### 不管是广播还是socket通讯,本地都是监听默认值端口
     */
    public static final String BROADCASTER = "szcme";                //广播发起者名称--暂时固定szcme
    //    public static final String BROADCAST_IP = "192.168.135.255";      //广播地址
    public static final String BROADCAST_IP = "255.255.255.255";      //广播地址
    //默认值都是一样的,iendo 7开头  cmeplayer 8开头
    public static final int BROADCAST_SERVER_PORT = 7006;                     //广播服务端端口--->默认值
    public static final int LOCAL_RECEIVE_PORT = 8005;                        //本地监听端口--->默认值  7021

    /**
     * MMKV  存储的Key
     * 说明:
     * 未登录的情况下-->iEnd项目本地监听端口设置为默认值7005,服务端通讯端口默认值设置为7006
     * <p>
     * 登录的情况下-->iEnd项目本地监听端口设置为授权之后返回的(Stp:socke udp接收端口；),并且服务端通讯和本地监听都是公用这个端口
     * <p>
     * 需要注意的是,退出的时候,需要手动吧KEY_LOCAL_RECEIVE_PORT的值设置为默认值7005,再切换监听线程
     */

    //广播约定端口  iend设定为7006 CMEPlayer设定为8005 -->广播的时候本地监听端口可以随意,但是发送固定的     MMKV的Key
    public static final String KEY_BROADCAST_SERVER_PORT = "KEY_BROADCAST_SERVER_PORT";

    //本地监听的端口                                                                                  MMKV的Key
    public static final String KEY_LOCAL_RECEIVE_PORT = "KEY_LOCAL_RECEIVE_PORT";

    //第一次开启接收线程 避免初始化的时候开启多次线程                                                    MMKV的Key
    public static final String KEY_SOCKET_RECEIVE_FIRST_IN = "KEY_SOCKET_RECEIVE_FIRST_IN";


    /**
     * 登录成功之后,存储当前设备信息
     */
    public static final String KEY_Device_Ip = "KEY_Device_Ip";      //选中设备的ip
    public static final String KEY_Device_Type_HexNum = "KEY_Device_Type_HexNum";      //选中设备的类型,此处是数字比如07  RC200默认给22，默认是十六进制的
    public static final String KEY_Device_Type_DecNum = "KEY_Device_Type_DecNum";      //选中设备的类型,此处是数字比如07  RC200默认给22，是10进制的
    public static final String KEY_Device_Type_Desc = "KEY_Device_Type_Desc";      //选中设备的类型,此处是数字比如一代一体机
    public static final String KEY_Login_Tag = "KEY_Login_Tag";      //选中设备的时候,是否登入成功
    public static final String KEY_Device_SocketPort = "KEY_Device_SocketPort";      //选中设备的port
    public static final String KEY_Device_HttpPort = "KEY_Device_HttpPort";      //选中设备的http请求端口
    public static final String KEY_DeviceCode = "KEY_DeviceCode";      //选中设备(上位机)的设备码
    public static final String KEY_Exit_Name = "KEY_Exit_Name";      //登入用户,退出登入时,存入上一次登入的用户名
    public static final String KEY_PhoneDeviceCode = "KEY_PhoneDeviceCode";      //当前设备唯一标识码
    public static final String KEY_RC200_Session = "KEY_RC200_Session";      //RC200  每次获取到session的值

    /**
     * 语音通话 全局-语音昵称-的Key
     */
    public static final String KEY_MIC_Name = "KEY_MIC_Name";      //全局-语音昵称-的Key
    /**
     * 耳鼻喉治疗台 测试使用SessionNum-的Key
     */
    public static final String KEY_SessionNum = "KEY_SessionNum";      //测试使用Session最后拼接的数字

    /**
     * 线路几 的标识
     */
    public static final String DialogPositionChannel = "DialogPositionChannel";      //线路几 的标识

    /**
     * 点击选中设备,进入直播界面,需要存储的数据
     * 模式一
     */
    public static final String KEY_Url01 = "KEY_Url01";      //直播地址默认01
    public static final String KEY_Url02 = "KEY_Url02";      //直播地址默认02
    public static final String KEY_BeanIP = "KEY_BeanIP";      //数据bean的IP
    public static final String KEY_UrlType = "KEY_UrlType";      //直播地址默认01
    public static final String KEY_Title = "KEY_Title";             //
    public static final String KEY_Ip = "KEY_Ip";                     //
    public static final String KEY_MicPort = "KEY_MicPort";              //
    public static final String KEY_DDNS_Account = "KEY_DDNS_Account";              //
    public static final String KEY_DDNS_Password = "KEY_DDNS_Password";              //
    public static final String KEY_DDNS_Address = "KEY_DDNS_Address";              //

    /**
     * toast 提示语
     */

    public static final String UDP_CASE_ID_DIFFERENT = "两设备之间选择病历不一致，请重新确认";                 //当前病例ID和操作病例ID不相等,不能操作!
    public static final String HAVE_NO_PERMISSION = "暂无权限";
    //    public static final String HAVE_HAND_FAIL = "暂无权限";
    public static final String HAVE_HAND_FAIL_OFFLINE = "远程设备连接失败,信息可能无法同步";


    /**
     * 协议命令cmd-->用来区分那个socket回调的消息
     */
    public static final String UDP_HAND = "30";                            //握手----所有指令之前必须握手
    public static final String UDP_FD = "FD";                              //网络发现（UDP广播）
    public static final String UDP_FC = "FC";                              //授权接入

    public static final String UDP_41 = "41";                              //语音接入
    public static final String UDP_42 = "42";                              //语音广播通知命令,监听到重新获取vioceID
    public static final String UDP_FE = "FE";                              //程序退出命令  -->退出登录的时候发消息


    public static final String KET_MIC_CURRENT_VOICE_ID = "KET_MIC_CURRENT_VOICE_ID";   //语音通话当前的voiceID,默认255,上位机当前需要播放声音的ID
    public static final String KET_MIC_VOICE_ID_FOR_ME = "KET_MIC_VOICE_ID_FOR_ME";   //语音通话,上位机分配给我的ID

    public static final String UDP_CUSTOM_TOAST = "UDP_CUSTOM_TOAST";            //自定义命令     toast
    public static final String UDP_CUSTOM_RESTART = "UDP_CUSTOM_RESTART";        //自定义命令     监听线程异常需要重启


    /**
     * 填写设备Dialog的设备类型标识
     */
//    public static final String Type_FuKeTable = "妇科治疗台";                 //妇科治疗台
//    public static final String Type_V1_YiTiJi = "一代一体机";                 //一代一体机
//    public static final String Type_EarNoseTable = "耳鼻喉治疗台";            //耳鼻喉治疗台
//    public static final String Type_MiNiaoTable = "泌尿治疗台";             //泌尿治疗台
//
    //默认DDNS 配置信息

    public static final String Config_DDNS_Account = "";                       //Account
    public static final String Config_DDNS_Password = "";                      //Password
    public static final String Config_DDNS_Url = "";                           //Url
    //设备类型数据库备用方案字段的默认值
    public static final Boolean Device_Common_Default_Spare = false;        //备用方案：是否开启，默认未开启=false
    public static final String Device_Common_Default_LiveSteam = "";        //备用方案：直播流
    public static final String Device_Common_Default_MicSteam = "";        //备用方案：语音流

    //HD3
    public final static String Type_HD3 = "HD3";                                             //HD3
    public static String Type_HD3_TW = "HD3";                                             //HD3
    public static String Type_HD3_EN = "HD3";                                             //HD3
    public static final String Type_HD3_Remark = "备注信息";                             //HD3的备注信息
    public static final String Type_HD3_ip = "192.168.1.10";                                 //ip
    public static final String Type_HD3_ip_public = "";                                //ip-public
    public static final String Type_HD3_Account = "admin";                                   //账号
    public static final String Type_HD3_Password = "12345";                                  //密码
    public static final String Type_HD3_HttpPort = "7001";                                   //HttpPort
    public static final String Type_HD3_LivePort = "554";                                     //LivePort   默认80改成了默认554
    public static final String Type_HD3_SocketPort = "";                                     //SocketPort
    public static final String Type_HD3_MicPort = "";                                        //MicPort
    public static final String Type_HD3_DeviceTypeDesc = "HD3";                              //deviceTypeDesc

    //HD3_4K
    public final static String Type_HD3_4K = "HD3-4K";                                          //HD3-4K
    public static String Type_HD3_4K_TW = "HD3-4K";                                             //HD3-4K
    public static String Type_HD3_4K_EN = "HD3-4K";                                             //HD3-4K
    public static final String Type_HD3_4K_Remark = "备注信息";                          //HD3-4K的备注信息
    public static final String Type_HD3_4K_ip = "192.168.1.10";                                 //ip
    public static final String Type_HD3_4K_ip_public = "";                                //ip-public
    public static final String Type_HD3_4K_Account = "admin";                                   //账号
    public static final String Type_HD3_4K_Password = "12345";                                  //密码
    public static final String Type_HD3_4K_HttpPort = "7001";                                   //HttpPort
    public static final String Type_HD3_4K_LivePort = "554";                                     //LivePort  默认80改成了默认554
    public static final String Type_HD3_4K_SocketPort = "";                                     //SocketPort
    public static final String Type_HD3_4K_MicPort = "";                                        //MicPort
    public static final String Type_HD3_4K_DeviceTypeDesc = "HD3-4K";                           //deviceTypeDesc


    //RC200
    public final static String Type_RC200 = "RC200";                                          //RC200
    public static String Type_RC200_TW = "RC200";                                             //RC200
    public static String Type_RC200_EN = "RC200";                                             //RC200
    public static final String Type_RC200_Session = "123456";                          //RC200默认的session
    public static final String Type_RC200_Remark = "备注信息";                          //RC200的备注信息
    public static final String Type_RC200_ip = "192.168.1.200";                                 //ip
    public static final String Type_RC200_ip_public = "";                                //ip-public
    public static final String Type_RC200_Account = "admin";                                   //账号
    public static final String Type_RC200_Password = "12345";                                  //密码
    public static final String Type_RC200_HttpPort = "3333";                                   //HttpPort
    public static final String Type_RC200_LivePort = "3333";                                     //LivePort RC200在固定端口3333监听普通http请求，3334监听https (SSL连接)请求。
    public static final String Type_RC200_SocketPort = "";                                     //SocketPort
    public static final String Type_RC200_MicPort = "";                                        //MicPort
    public static final String Type_RC200_DeviceTypeDesc = "RC200";                           //deviceTypeDesc


    //智能一体机
    public static final String Type_V1_YiTiJi = "智能一体机";                                 //以前的,一代一体机
    public static final String Type_V1_YiTiJi_TW = "智能一体机";                              //一代一体机
    public static final String Type_V1_YiTiJi_EN = "智能一体机";                              //一代一体机
    public static final String Type_V1_YiTiJi_Remark = "备注信息";                            //一代一体机的备注信息
    public static final String Type_V1_YiTiJi_ip = "192.168.1.200";                          //ip
    public static final String Type_V1_YiTiJi_ip_public = "";                                //ip-public
    public static final String Type_V1_YiTiJi_Account = "root";                              //账号
    public static final String Type_V1_YiTiJi_Password = "root";                             //密码
    public static final String Type_V1_YiTiJi_HttpPort = "7001";                             //HttpPort
    public static final String Type_V1_YiTiJi_LivePort = "7788";                             //LivePort
    public static final String Type_V1_YiTiJi_SocketPort = "7006";                           //SocketPort
    public static final String Type_V1_YiTiJi_MicPort = "7789";                              //MicPort
    public static final String Type_V1_YiTiJi_DeviceTypeDesc = "智能一体机";                      //deviceTypeDesc

    //手术一体机
    public static final String Type_Operation_YiTiJi = "手术一体机";
    public static final String Type_Operation_YiTiJi_TW = "手术一体机";                             //老徐,的手术一体机
    public static final String Type_Operation_YiTiJi_EN = "手术一体机";                              //手术一体机
    public static final String Type_Operation_YiTiJi_Remark = "手术一体机的备注信息";                 //手术一体机的备注信息
    public static final String Type_Operation_YiTiJi_ip = "192.168.1.200";                          //ip
    public static final String Type_Operation_YiTiJi_ip_public = "";                                //ip-public
    public static final String Type_Operation_YiTiJi_Account = "root";                              //账号
    public static final String Type_Operation_YiTiJi_Password = "root";                             //密码
    public static final String Type_Operation_YiTiJi_HttpPort = "7001";                             //HttpPort
    public static final String Type_Operation_YiTiJi_LivePort = "7788";                             //LivePort
    public static final String Type_Operation_YiTiJi_SocketPort = "7006";                           //SocketPort
    public static final String Type_Operation_YiTiJi_MicPort = "7789";                              //MicPort
    public static final String Type_Operation_YiTiJi_Default_DeviceType = "0B";                      //设备类型
    public static final String Type_Operation_YiTiJi_DeviceTypeDesc = "手术一体机";                //deviceTypeDesc

    //耳鼻--喉治疗台
    public static final String Type_EarNoseTable = "耳鼻喉治疗台";                           //耳鼻喉治疗台
    public static final String Type_EarNoseTable_TW = "耳鼻喉治疗台";                                             //耳鼻喉治疗台
    public static final String Type_EarNoseTable_EN = "耳鼻喉治疗台";                                             //耳鼻喉治疗台
    public static final String Type_EarNoseTable_Remark = "备注信息";             //耳鼻喉治疗台的备注信息
    public static final String Type_EarNoseTable_ip = "192.168.1.200";                        //ip
    public static final String Type_EarNoseTable_ip_public = "";                                //ip-public
    public static final String Type_EarNoseTable_Account = "root";                            //账号
    public static final String Type_EarNoseTable_Password = "root";                           //密码
    public static final String Type_EarNoseTable_HttpPort = "7001";                           //HttpPort
    public static final String Type_EarNoseTable_LivePort = "7788";                           //LivePort
    public static final String Type_EarNoseTable_SocketPort = "7006";                         //SocketPort
    public static final String Type_EarNoseTable_MicPort = "7789";                            //MicPort
    public static final String Type_EarNoseTable_DeviceTypeDesc = "耳鼻喉治疗台";                   //deviceTypeDesc

    //泌尿--喉治疗台
    public static final String Type_MiNiaoTable = "泌尿治疗台";                                 //泌尿治疗台
    public static final String Type_MiNiaoTable_TW = "泌尿治疗台";                                             //泌尿治疗台
    public static final String Type_MiNiaoTable_EN = "泌尿治疗台";                                             //泌尿治疗台
    public static final String Type_MiNiaoTable_Remark = "备注信息";                 //泌尿治疗台的备注信息
    public static final String Type_MiNiaoTable_ip = "192.168.1.200";                          //ip
    public static final String Type_MiNiaoTable_ip_public = "";                                //ip-public
    public static final String Type_MiNiaoTable_Account = "root";                              //账号
    public static final String Type_MiNiaoTable_Password = "root";                             //密码
    public static final String Type_MiNiaoTable_HttpPort = "7001";                             //HttpPort
    public static final String Type_MiNiaoTable_LivePort = "7788";                             //LivePort
    public static final String Type_MiNiaoTable_SocketPort = "7006";                           //SocketPort
    public static final String Type_MiNiaoTable_MicPort = "7789";                              //MicPort
    public static final String Type_MiNiaoTable_DeviceTypeDesc = "泌尿治疗台";                      //deviceTypeDesc

    //妇科--治疗台
    public static final String Type_FuKeTable = "妇科治疗台";                                 //妇科治疗台
    public static final String Type_FuKeTable_TW = "妇科治疗台";                                             //妇科治疗台
    public static final String Type_FuKeTable_EN = "妇科治疗台";                                             //妇科治疗台
    public static final String Type_FuKeTable_Remark = "备注信息";                 //妇科治疗台备注信息
    public static final String Type_FuKeTable_ip = "192.168.1.200";                          //ip
    public static final String Type_FuKeTable_ip_public = "";                                //ip-public
    public static final String Type_FuKeTable_Account = "root";                              //账号
    public static final String Type_FuKeTable_Password = "root";                             //密码
    public static final String Type_FuKeTable_HttpPort = "7001";                             //HttpPort
    public static final String Type_FuKeTable_LivePort = "7788";                             //LivePort
    public static final String Type_FuKeTable_SocketPort = "7006";                           //SocketPort
    public static final String Type_FuKeTable_MicPort = "7789";                              //MicPort
    public static final String Type_FuKeTable_DeviceTypeDesc = "妇科治疗台";                      //deviceTypeDesc

    //工作站
    public static final String Type_Work_Station = "工作站";                                           //工作站
    public static String Type_Work_Station_TW = "工作站";                                             //工作站
    public static final String Type_Work_Station_EN = "工作站";                                             //工作站
    public static final String Type_Work_Station_Remark = "备注信息";                           //工作站的备注信息
    public static final String Type_Work_Station_ip = "192.168.1.200";                                 //ip
    public static final String Type_Work_Station_ip_public = "";                                //ip-public
    public static final String Type_Work_Station_Account = "root";                                     //账号
    public static final String Type_Work_Station_Password = "root";                                    //密码
    public static final String Type_Work_Station_HttpPort = "7001";                                    //HttpPort
    public static final String Type_Work_Station_LivePort = "7788";                                    //LivePort
    public static final String Type_Work_Station_SocketPort = "7006";                                  //SocketPort
    public static final String Type_Work_Station_MicPort = "7789";                                     //MicPort
    public static final String Type_Work_Station_DeviceTypeDesc = "工作站";                             //deviceTypeDesc


    //神州转播
    public static final String Type_Custom_Url = "神州转播";                                            //神州转播
    public static final String Type_Custom_Url_TW = "神州转播";                                             //神州转播
    public static final String Type_Custom_Url_EN = "神州转播";                                             //神州转播
    public static final String Type_Custom_Url_Remark = "神州转播备注信息";                            //神州转播的备注信息
    public static final String Type_Custom_Url_ip = "";                                                //ip
    public static final String Type_Custom_Url_ip_public = "";                                //ip-public
    public static final String Type_Custom_Url_Account = "";                                           //账号
    public static final String Type_Custom_Url_Password = "";                                          //密码
    public static final String Type_Custom_Url_HttpPort = "";                                          //HttpPort
    public static final String Type_Custom_Url_LivePort = "";                                          //LivePort
    public static final String Type_Custom_Url_SocketPort = "";                                        //SocketPort
    public static final String Type_Custom_Url_MicPort = "";                                           //MicPort
    public static final String Type_Custom_Url_DeviceTypeDesc = "神州转播";                             //deviceTypeDesc


    /**
     * 协议里面设备类型,文档里面和传输都用16进制表示
     * int用十六进制表示
     * 统一用十六进制表示 协议返回的也是16进制
     */
    public static final int Type_00 = 0x00;     //工作站
    public static final int Type_01 = 0x01;     //HD3摄像机(HD3)
    public static final int Type_02 = 0x02;     //冷光源
    public static final int Type_03 = 0x03;     //气腹机
    public static final int Type_04 = 0x04;     //冲洗机
    public static final int Type_05 = 0x05;     //4K摄像机(4K-HD3)
    public static final int Type_06 = 0x06;     //耳鼻喉控制板(喷,洗,吹,加热,除污)
    public static final int Type_07 = 0x07;     //智能一体机
    public static final int Type_08 = 0x08;     //耳鼻喉治疗台
    public static final int Type_09 = 0x09;     //妇科治疗台
    public static final int Type_0A = 0x0A;     //泌尿治疗台
    public static final int Type_0B = 0x0B;     //手术一体机
    public static final int Type_A0 = 0xA0;     //iOS
    public static final int Type_A1 = 0xA1;     //Android
    public static final int Type_A1_DECIMAL = 161;     //Android----http请求的所有接口，设备类型都是传十进制，A1=161
    public static final int Type_A2 = 0xA2;     //RC200
    public static final int Type_FF = 0xFF;     //神州转播

    /**
     * 重点
     * 重点
     * 重点
     * 协议里面设备类型,String表示字段
     * socket  通讯用的是16进制的字符串
     * 扫码和接口传递的是十进制的字符串
     * 这两者要可以相互转换
     * 重点
     * 重点
     * 重点
     */
    /**
     * * Send_Type(Received_Type)	发送(接收方)方设备类型
     * * 设备类型：对应关系
     * * 00-工作站， 01-HD3摄像机，02-冷光源，03-气腹机，04-冲洗机，
     * * 05-4K摄像机，06-耳鼻喉控制板，07-(智能一体机)一代一体机，8-耳鼻喉治疗台，
     * * 9-妇科治疗台，0x0A-泌尿治疗台，0B-手术一体机， A2-RC200，A0-iOS，A1-Android，
     * * FF-所有设备(神州转播)
     * * 更多设备类型依次类推，平台最大可连接255种受控设备
     * <p>
     * 我们手动吧 8  9  改成了 08 09 所以需要在这里替换下
     * <p>
     * string从随机之开始到校验和处结束的String
     */
    //设备类型的，16进制字符串
    public static final String Type_HexString_00 = "00";     //工作站
    public static final String Type_HexString_01 = "01";     //HD3摄像机(HD3)
    public static final String Type_HexString_02 = "02";     //冷光源
    public static final String Type_HexString_03 = "03";     //气腹机
    public static final String Type_HexString_04 = "04";     //冲洗机
    public static final String Type_HexString_05 = "05";     //4K摄像机(4K-HD3)
    public static final String Type_HexString_06 = "06";     //耳鼻喉控制板(喷,洗,吹,加热,除污)
    public static final String Type_HexString_07 = "07";     //智能一体机
    public static final String Type_HexString_08 = "08";     //耳鼻喉治疗台
    public static final String Type_HexString_09 = "09";     //妇科治疗台
    public static final String Type_HexString_0A = "0A";     //泌尿治疗台
    public static final String Type_HexString_0B = "0B";     //手术一体机 (socket),二维码里面是十进制(11)
    public static final String Type_HexString_A0 = "A0";     //iOS
    public static final String Type_HexString_A1 = "A1";     //Android
    public static final String Type_HexString_A2 = "A2";     //RC200
    public static final String Type_HexString_FF = "FF";     //神州转播


    //设备类型的，10进制字符串
    public static final String Type_DecString_00 = "00";     //工作站
    public static final String Type_DecString_01 = "01";     //HD3摄像机(HD3)
    public static final String Type_DecString_02 = "02";     //冷光源
    public static final String Type_DecString_03 = "03";     //气腹机
    public static final String Type_DecString_04 = "04";     //冲洗机
    public static final String Type_DecString_05 = "05";     //4K摄像机(4K-HD3)
    public static final String Type_DecString_06 = "06";     //耳鼻喉控制板(喷,洗,吹,加热,除污)
    public static final String Type_DecString_07 = "07";     //智能一体机
    public static final String Type_DecString_08 = "08";     //耳鼻喉治疗台
    public static final String Type_DecString_09 = "09";     //妇科治疗台
    public static final String Type_DecString_0A = "10";     //泌尿治疗台
    public static final String Type_DecString_0B = "11";     //手术一体机 (socket),二维码里面是十进制(11)
    public static final String Type_DecString_A0 = "160";     //iOS
    public static final String Type_DecString_A1 = "161";     //Android
    public static final String Type_DecString_A2 = "162";     //RC200
    public static final String Type_DecString_FF = "255";     //神州转播


    public static final String Type_00_DESC = "工作站";                                           //0x00
    public static final String Type_01_DESC = "HD3摄像机";                                        //0x01
    public static final String Type_02_DESC = "冷光源";                                           //0x02
    public static final String Type_03_DESC = "气腹机";                                           //0x03
    public static final String Type_04_DESC = "冲洗机";                                           //0x04
    public static final String Type_05_DESC = "4K摄像机";                                         //0x05
    public static final String Type_06_DESC = "耳鼻喉控制板";                                      //0x06
    public static final String Type_07_DESC = "智能一体机";                                        //0x07
    public static final String Type_08_DESC = "耳鼻喉治疗台";                                      //0x08
    public static final String Type_09_DESC = "妇科治疗台";                                        //0x09
    public static final String Type_0A_DESC = "泌尿治疗台";                                        //0x0A
    public static final String Type_A0_DESC = "iOS";                                              //0xA0
    public static final String Type_A1_DESC = "Android";                                          //0xA1
    public static final String Type_A2_DESC = "RC200";                                          //0xA1
    public static final String Type_0B_DESC = "手术一体机";                                        //0x0B


    //国际化语言区分
    public static final String Language_Chinese = "简体中文";     //Android
    public static final String Language_Chinese_TW = "繁体中文";     //Android
    public static final String Language_English = "English";     //Android


}
