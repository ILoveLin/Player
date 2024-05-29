package com.company.shenzhou.utlis;

import android.content.Context;
import android.util.Log;

import com.company.shenzhou.R;
import com.company.shenzhou.bean.RefreshEvent;
import com.company.shenzhou.bean.ZXingLine1Bean;
import com.company.shenzhou.bean.ZXingLine23Bean;
import com.company.shenzhou.bean.dbbean.DeviceDBBean;
import com.company.shenzhou.bean.dbbean.DownBindNameListBean;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.playerdb.manager.DeviceDBUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/5/29 15:45
 * desc：华为扫码之后 处理属于的工具类
 */
public class HuaweiScanPlus {
    private static final String TAG = "HuaweiScanPlus，==";

    public static void getJsonData(Context mContext, String currentUsername, String result) {
        //最新版
        try {
            Gson gson = new Gson();
            String line = JsonUtil.parseJson2CheckLine(result);
            String apiVersion = "";
            //线路1
            if ("0".equals(line)) {
                LogUtils.e(TAG + "==扫码json===line====:" + line);
                Type type = new TypeToken<ZXingLine1Bean>() {
                }.getType();
                ZXingLine1Bean jsonBean = gson.fromJson(result, type);
                DeviceDBBean insertBean = new DeviceDBBean();
                //AES解密,账号密码
                String passWord = AesUtils.encrypt(jsonBean.getPw());
                String userName = AesUtils.encrypt(jsonBean.getUn());
                //设置线路
                String currentStrLine = "" + mContext.getResources().getString(R.string.device_work_type_01);
                insertBean.setChannel(currentStrLine);

                //("HD3", "HD3-4K", "一代一体机","耳鼻喉治疗台","妇科治疗台","泌尿治疗台","工作站","神州转播")
                //此处是设备类型选择的position
                //  0         1        2               3           4           5           6       7   ,
                //此处是设备类型对应上位机的类型
                /**
                 * 00-工作站， 01-HD3摄像机，02-冷光源，03-气腹机，04-冲洗机，05-4K摄像机(HD3-4K)，06-耳鼻喉控制板，
                 * 07-一代一体机，8-耳鼻喉治疗台，9-妇科治疗台，10-泌尿治疗台
                 * A0-iOS，A1-Android，FF-所有设备
                 */
                String deviceType = jsonBean.getDtp();
                //特此说明:默认情况下是2位,比如工作站00和0  手机端给的就是默认00
                switch (deviceType) {
                    case "00": //工作站
                    case "0": //工作站
                        insertBean.setDeviceTypeDesc(Constants.Type_Work_Station);
                        insertBean.setDeviceName(Constants.Type_Work_Station);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_00);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_00);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_00);

                        break;
                    case Constants.Type_DecString_01: //HD3摄像机
                        insertBean.setDeviceTypeDesc(Constants.Type_HD3);
                        insertBean.setDeviceName(Constants.Type_HD3);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_01);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_01);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_01);
                        break;
                    case Constants.Type_DecString_05: //4K摄像机(HD3-4K)
                        insertBean.setDeviceTypeDesc(Constants.Type_HD3_4K);
                        insertBean.setDeviceName(Constants.Type_HD3_4K);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_05);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_05);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_05);
                        break;
                    case Constants.Type_DecString_07: //(智能一体机)一代一体机
                        insertBean.setDeviceTypeDesc(Constants.Type_V1_YiTiJi);
                        insertBean.setDeviceName(Constants.Type_V1_YiTiJi);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_07);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_07);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_07);
                        break;
                    case "08": //耳鼻喉治疗台
                    case "8": //耳鼻喉治疗台
                        insertBean.setDeviceTypeDesc(Constants.Type_EarNoseTable);
                        insertBean.setDeviceName(Constants.Type_EarNoseTable);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_08);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_08);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_08);
                        break;
                    case "9": //妇科治疗台
                    case "09": //妇科治疗台
                        insertBean.setDeviceTypeDesc(Constants.Type_FuKeTable);
                        insertBean.setDeviceName(Constants.Type_FuKeTable);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_09);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_09);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_09);
                        break;
                    case Constants.Type_DecString_0A: //泌尿治疗台
                        insertBean.setDeviceTypeDesc(Constants.Type_MiNiaoTable);
                        insertBean.setDeviceName(Constants.Type_MiNiaoTable);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_0A);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_0A);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_0A);
                        break;
                    case Constants.Type_DecString_FF: //神州转播
                        insertBean.setDeviceTypeDesc(Constants.Type_Custom_Url);
                        insertBean.setDeviceName(Constants.Type_Custom_Url);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_FF);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_FF);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_FF);
                        break;
                    case Constants.Type_DecString_0B: //手术一体机(扫码是十进制)
                        insertBean.setDeviceTypeDesc(Constants.Type_Operation_YiTiJi);
                        insertBean.setDeviceName(Constants.Type_Operation_YiTiJi);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_0B);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_0B);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_0B);
                        break;
                    case Constants.Type_DecString_A2: //RC200
                        insertBean.setDeviceTypeDesc(Constants.Type_RC200);
                        insertBean.setDeviceName(Constants.Type_RC200);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_A2);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_A2);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_A2);
                        break;

                }
                String singleDeviceCodeTag = "";
                //RC200需要单独区分因为唯一标识不同,RC200:
                // 所以这里我们用当前可用ip+deviceType+当前登入的用户名（admin）,比如:192.168.71.159RC200admin
                if ("A2".equals(deviceType)) {
                    singleDeviceCodeTag = jsonBean.getIp() + "RC200" + currentUsername;

                } else {
                    //设置唯一标识key:deviceOnlyCode16 + bean.getType()+bean.getChan   //b0087fc6fa584b62耳鼻喉治疗台 b0087fc6fa584b62耳鼻喉治疗台
                    //最新版本需要添加线路，设置唯一标识key:deviceOnlyCode16 + bean.getType() +bena.getChanel  //b0087fc6fa584b62耳鼻喉治疗台线路3 b0087fc6fa584b62耳鼻喉治疗台线路3
                    singleDeviceCodeTag = jsonBean.getId() + insertBean.getDeviceTypeDesc() + currentStrLine;
                    insertBean.setAcceptAndInsertDB(singleDeviceCodeTag);
                }


                /**
                 * 此处根据  设置唯一标识key:deviceOnlyCode16 + bean.getType()+bean.getChannel(),判断此数据数据库是否存在
                 *  RC200唯一key:ip+deviceType+当前登入的用户名（admin）,比如:192.168.71.159RC200admin
                 * 存在--->更新
                 * 不存在-->新增
                 */

                insertBean.setTag(currentUsername);
                if (null == jsonBean.getDun() || "".equals(jsonBean.getDun())) {
                    insertBean.setDDNSAcount("");
                } else {
                    insertBean.setDDNSAcount(jsonBean.getDun() + "");
                }
                if (null == jsonBean.getDpw() || "".equals(jsonBean.getDpw())) {
                    insertBean.setDDNSPassword("");
                } else {
                    insertBean.setDDNSPassword(jsonBean.getDpw() + "");
                }
                if (null == jsonBean.getDurl() || "".equals(jsonBean.getDurl())) {
                    insertBean.setDDNSURL("");
                } else {
                    insertBean.setDDNSURL(jsonBean.getDurl() + "");
                }

                insertBean.setDeviceCode(jsonBean.getId());
                insertBean.setEndoType(jsonBean.getEtp());
                insertBean.setIp(jsonBean.getIp());
                insertBean.setMsgMark("备注信息");
                //http请求端口
                if ("".equals(jsonBean.getHtp()) || null == jsonBean.getHtp()) {
                    insertBean.setHttpPort("7001");
                } else {
                    insertBean.setHttpPort(jsonBean.getHtp());
                }
                //语音通讯端口
                insertBean.setMicPort("7789");

                //socket通讯端口
                if ("".equals(jsonBean.getSpt()) || null == jsonBean.getSpt()) {
                    insertBean.setSocketPort("7006");
                } else {
                    insertBean.setSocketPort(jsonBean.getSpt());
                }
                //直播端口
                if ("".equals(jsonBean.getPt()) || null == jsonBean.getPt()) {
                    insertBean.setLivePort("7788");
                } else {
                    insertBean.setLivePort(jsonBean.getPt());
                }
                insertBean.setPassword(passWord);
                insertBean.setTitle(jsonBean.getTi());
                insertBean.setAccount(userName + "");
                //设置版本号
                if ("".equals(jsonBean.getV())) {
                    apiVersion = Constants.ApiVersion.V1;
                } else {
                    apiVersion = jsonBean.getV();

                }
                insertBean.setApiVersion(apiVersion);
                //添加到绑定列表
                ArrayList<DownBindNameListBean> downNameList = new ArrayList<>();
                DownBindNameListBean nameBean = new DownBindNameListBean();
                //绑定谁添加的设备--用户名
                nameBean.setDownBindName(currentUsername);
                downNameList.add(nameBean);
                insertBean.setDownBingNameList(downNameList);
                Log.e("扫码结果", "========" + insertBean);

                List<DeviceDBBean> queryBeanByTag = DeviceDBUtils.getQueryBeanByTag(mContext, singleDeviceCodeTag);
                int size = queryBeanByTag.size();

                if (null != queryBeanByTag && size != 0) {
                    //更新数据
                    DeviceDBBean deviceDBBean = queryBeanByTag.get(0);
                    //获取ID,做刷新
                    Long id = deviceDBBean.getId();
                    insertBean.setId(id);
                    DeviceDBUtils.insertOrReplaceData(mContext, insertBean);
                    EventBus.getDefault().post(new RefreshEvent("refresh", mContext.getResources().getString(R.string.device_update_success)));
                } else {
                    DeviceDBUtils.insertOrReplaceData(mContext, insertBean);
                    EventBus.getDefault().post(new RefreshEvent("refresh", mContext.getResources().getString(R.string.device_add_success)));
                }
            } else {
                //线路2，和线路3
                LogUtils.e(TAG + "==扫码json====line====:" + line);
                Type type = new TypeToken<ZXingLine23Bean>() {
                }.getType();
                ZXingLine23Bean jsonBean = gson.fromJson(result, type);
                DeviceDBBean insertBean = new DeviceDBBean();
                //AES解密,账号密码
                String passWord = AesUtils.encrypt(jsonBean.getPw());
                String userName = AesUtils.encrypt(jsonBean.getUn());

                //设置线路
                String currentStrLine = "";
                if ("1".equals(jsonBean.getLn())) {
                    currentStrLine = "" + mContext.getResources().getString(R.string.device_work_type_02);
                    insertBean.setChannel(currentStrLine);
                } else if ("2".equals(jsonBean.getLn())) {
                    currentStrLine = "" + mContext.getResources().getString(R.string.device_work_type_03);
                    insertBean.setChannel(currentStrLine);
                }

                //("HD3", "HD3-4K", "一代一体机","耳鼻喉治疗台","妇科治疗台","泌尿治疗台","工作站","神州转播")
                //此处是设备类型选择的position
                //  0         1        2               3           4           5           6       7   ,
                //此处是设备类型对应上位机的类型
                /**
                 * 00-工作站， 01-HD3摄像机，02-冷光源，03-气腹机，04-冲洗机，05-4K摄像机(HD3-4K)，06-耳鼻喉控制板，
                 * 07-一代一体机，8-耳鼻喉治疗台，9-妇科治疗台，10-泌尿治疗台
                 * A0-iOS，A1-Android，FF-所有设备
                 */
                String deviceType = jsonBean.getDtp();
                //特此说明:默认情况下是2位,比如工作站00和0  手机端给的就是默认00
                switch (deviceType) {
                    case "00": //工作站
                    case "0": //工作站
                        insertBean.setDeviceTypeDesc(Constants.Type_Work_Station);
                        insertBean.setDeviceName(Constants.Type_Work_Station);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_00);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_00);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_00);
                        break;
                    case Constants.Type_DecString_01: //HD3摄像机
                        insertBean.setDeviceTypeDesc(Constants.Type_HD3);
                        insertBean.setDeviceName(Constants.Type_HD3);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_01);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_01);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_01);
                        break;
                    case Constants.Type_DecString_05: //4K摄像机(HD3-4K)
                        insertBean.setDeviceTypeDesc(Constants.Type_HD3_4K);
                        insertBean.setDeviceName(Constants.Type_HD3_4K);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_05);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_05);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_05);
                        break;
                    case Constants.Type_DecString_07: //智能一体机
                        insertBean.setDeviceTypeDesc(Constants.Type_V1_YiTiJi);
                        insertBean.setDeviceName(Constants.Type_V1_YiTiJi);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_07);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_07);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_07);
                        break;
                    case "08": //耳鼻喉治疗台
                    case "8": //耳鼻喉治疗台
                        insertBean.setDeviceTypeDesc(Constants.Type_EarNoseTable);
                        insertBean.setDeviceName(Constants.Type_EarNoseTable);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_08);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_08);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_08);
                        break;
                    case "9": //妇科治疗台
                    case "09": //妇科治疗台
                        insertBean.setDeviceTypeDesc(Constants.Type_FuKeTable);
                        insertBean.setDeviceName(Constants.Type_FuKeTable);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_09);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_09);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_09);
                        break;
                    case Constants.Type_DecString_0A: //泌尿治疗台
                        insertBean.setDeviceTypeDesc(Constants.Type_MiNiaoTable);
                        insertBean.setDeviceName(Constants.Type_MiNiaoTable);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_0A);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_0A);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_0A);
                        break;
                    case Constants.Type_DecString_FF: //神州转播
                        insertBean.setDeviceTypeDesc(Constants.Type_Custom_Url);
                        insertBean.setDeviceName(Constants.Type_Custom_Url);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_FF);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_FF);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_FF);
                        break;
                    case Constants.Type_DecString_0B: //手术一体机(扫码是十进制)
                        insertBean.setDeviceTypeDesc(Constants.Type_Operation_YiTiJi);
                        insertBean.setDeviceName(Constants.Type_Operation_YiTiJi);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_0B);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_0B);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_0B);
                        break;
                    case Constants.Type_DecString_A2: //RC200
                        insertBean.setDeviceTypeDesc(Constants.Type_RC200);
                        insertBean.setDeviceName(Constants.Type_RC200);
                        insertBean.setDeviceTypeNum(Constants.Type_HexString_A2);
                        insertBean.setDeviceTypeHexNum(Constants.Type_HexString_A2);
                        insertBean.setDeviceTypeDecNum(Constants.Type_DecString_A2);
                        break;

                }
                String singleDeviceCodeTag = "";
                //RC200需要单独区分因为唯一标识不同,RC200:
                // 所以这里我们用当前可用ip+deviceType+当前登入的用户名（admin）,比如:192.168.71.159RC200admin
                if ("A2".equals(deviceType)) {
                    singleDeviceCodeTag = jsonBean.getIp() + "RC200" + currentUsername;

                } else {
                    //设置唯一标识key:deviceOnlyCode16 + bean.getType()+bean.getChan   //b0087fc6fa584b62耳鼻喉治疗台 b0087fc6fa584b62耳鼻喉治疗台
                    //最新版本需要添加线路，设置唯一标识key:deviceOnlyCode16 + bean.getType() +bena.getChanel  //b0087fc6fa584b62耳鼻喉治疗台线路3 b0087fc6fa584b62耳鼻喉治疗台线路3
                    singleDeviceCodeTag = jsonBean.getId() + insertBean.getDeviceTypeDesc() + currentStrLine;
                    insertBean.setAcceptAndInsertDB(singleDeviceCodeTag);
                }


                LogUtils.e(TAG + "==WebView日志:singleDeviceCodeTag====:" + singleDeviceCodeTag);

                /**
                 * 此处根据  设置唯一标识key:deviceOnlyCode16 + bean.getType()+bean.getChannel(),判断此数据数据库是否存在
                 *  RC200唯一key:ip+deviceType+当前登入的用户名（admin）,比如:192.168.71.159RC200admin
                 * 存在--->更新
                 * 不存在-->新增
                 */

                insertBean.setTag(currentUsername);
                //线路2 3没有DDNS
                insertBean.setDDNSAcount("");
                insertBean.setDDNSPassword("");
                insertBean.setDDNSURL("");
                insertBean.setDeviceCode(jsonBean.getId());
                insertBean.setEndoType(jsonBean.getEtp());
                insertBean.setIp(jsonBean.getIp());
                insertBean.setMsgMark("备注信息");
                //http请求端口
                if ("".equals(jsonBean.getHtp()) || null == jsonBean.getHtp()) {
                    insertBean.setHttpPort("7001");
                } else {
                    insertBean.setHttpPort(jsonBean.getHtp());
                }
                //语音通讯端口
                insertBean.setMicPort("7789");

                //socket通讯端口
                if ("".equals(jsonBean.getSpt()) || null == jsonBean.getSpt()) {
                    insertBean.setSocketPort("7006");
                } else {
                    insertBean.setSocketPort(jsonBean.getSpt());
                }
                //直播端口
                if ("".equals(jsonBean.getPt()) || null == jsonBean.getPt()) {
                    insertBean.setLivePort("7788");
                } else {
                    insertBean.setLivePort(jsonBean.getPt());
                }
                insertBean.setPassword(passWord + "");
                insertBean.setTitle(jsonBean.getTi());
                insertBean.setAccount(userName + "");
                //设置版本号
                if ("".equals(jsonBean.getV())) {
                    apiVersion = Constants.ApiVersion.V1;
                } else {
                    apiVersion = jsonBean.getV();

                }
                insertBean.setApiVersion(apiVersion);
                //添加到绑定列表
                ArrayList<DownBindNameListBean> downNameList = new ArrayList<>();
                DownBindNameListBean nameBean = new DownBindNameListBean();
                //绑定谁添加的设备--用户名
                nameBean.setDownBindName(currentUsername);
                downNameList.add(nameBean);
                insertBean.setDownBingNameList(downNameList);
                Log.e("扫码结果", "========" + insertBean.toString());

                List<DeviceDBBean> queryBeanByTag = DeviceDBUtils.getQueryBeanByTag(mContext, singleDeviceCodeTag);
                int size = queryBeanByTag.size();
                if (null != queryBeanByTag && size != 0) {
                    //更新数据
                    DeviceDBBean deviceDBBean = queryBeanByTag.get(0);
                    //获取ID,做刷新
                    Long id = deviceDBBean.getId();
                    insertBean.setId(id);
                    DeviceDBUtils.insertOrReplaceData(mContext, insertBean);
                    EventBus.getDefault().post(new RefreshEvent("refresh", mContext.getResources().getString(R.string.device_update_success)));

                } else {
                    DeviceDBUtils.insertOrReplaceData(mContext, insertBean);
                    EventBus.getDefault().post(new RefreshEvent("refresh", mContext.getResources().getString(R.string.device_update_success)));

                }


            }


        } catch (Exception e) {
            Log.e("扫码结果", "=====e===" + e);
            EventBus.getDefault().post(new RefreshEvent("error", mContext.getResources().getString(R.string.device_the_scan_code_is_abnormal)));
        }
    }


    public static void getCustomUrl(Context mContext, String currentUsername, String result) {
        DeviceDBBean videoDBBean = new DeviceDBBean();
        videoDBBean.setDeviceName("神州转播");
        videoDBBean.setDeviceCode("");
        videoDBBean.setAccount("");
        videoDBBean.setPassword("");
        videoDBBean.setTitle(Constants.Type_Custom_Url);
        videoDBBean.setMsgMark(Constants.Type_Custom_Url_Remark);
        videoDBBean.setLivePort(Constants.Type_Custom_Url_LivePort);
        videoDBBean.setIp(result.trim());
        videoDBBean.setTag(currentUsername);
        ArrayList<DownBindNameListBean> downNameList = new ArrayList<>();
        DownBindNameListBean nameBean = new DownBindNameListBean();
        //绑定谁添加的设备--用户名
        nameBean.setDownBindName(currentUsername);
        downNameList.add(nameBean);
        videoDBBean.setDownBingNameList(downNameList);
        videoDBBean.setMicPort("");
        videoDBBean.setHttpPort("");
        videoDBBean.setSocketPort("");
        videoDBBean.setDeviceTypeNum(Constants.Type_HexString_FF);
        videoDBBean.setDeviceTypeHexNum(Constants.Type_HexString_FF);
        videoDBBean.setDeviceTypeDecNum(Constants.Type_DecString_FF);
        videoDBBean.setDeviceTypeDesc(Constants.Type_Custom_Url_DeviceTypeDesc);
        DeviceDBUtils.insertOrReplaceData(mContext, videoDBBean);
        EventBus.getDefault().post(new RefreshEvent("refresh", mContext.getResources().getString(R.string.device_update_success)));

    }


}
