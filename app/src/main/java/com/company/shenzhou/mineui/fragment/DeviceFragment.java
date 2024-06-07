package com.company.shenzhou.mineui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.company.shenzhou.R;
import com.company.shenzhou.action.StatusAction;
import com.company.shenzhou.app.TitleBarFragment;
import com.company.shenzhou.bean.AppDeviceInfoBean;
import com.company.shenzhou.bean.RefreshEvent;
import com.company.shenzhou.bean.dbbean.DeviceDBBean;
import com.company.shenzhou.bean.dbbean.DownBindNameListBean;
import com.company.shenzhou.bean.line.line23CheckSteamBean;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.mineui.MainActivity;
import com.company.shenzhou.mineui.activity.PlayerLine1Activity;
import com.company.shenzhou.mineui.activity.PlayerLine2Activity;
import com.company.shenzhou.mineui.activity.PlayerLine3Activity;
import com.company.shenzhou.mineui.activity.PlayerRC200Activity;
import com.company.shenzhou.mineui.activity.SearchDeviceActivity;
import com.company.shenzhou.mineui.adapter.DeviceAdapter;
import com.company.shenzhou.mineui.dialog.AddDeviceDialog;
import com.company.shenzhou.mineui.dialog.Input2SteamDialog;
import com.company.shenzhou.mineui.dialog.UpdateDeviceDialog;
import com.company.shenzhou.mineui.popup.ListIconPopup;
import com.company.shenzhou.playerdb.manager.DeviceDBUtils;
import com.company.shenzhou.ui.dialog.MessageDialog;
import com.company.shenzhou.ui.dialog.SelectDialog;
import com.company.shenzhou.utlis.CommonUtil;
import com.company.shenzhou.utlis.HuaweiScanPlus;
import com.company.shenzhou.utlis.JsonUtil;
import com.company.shenzhou.utlis.LogUtils;
import com.company.shenzhou.utlis.SharePreferenceUtil;
import com.company.shenzhou.widget.StatusLayout;
import com.company.shenzhou.widget.SwipeMenuLayout;
import com.google.gson.Gson;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.widget.view.ClearEditText;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : 设备界面
 * <p>
 * 设备唯一标识码==mDeviceDBBean.getDeviceCode() + mDeviceDBBean.getDeviceTypeDesc() + mDeviceDBBean.getChannel()
 * eg：iofad78efadf4ae8f智能一体机线路1
 * 每次新增或者修改数据的时候，都需要判断数据库是否存在当前bean
 * 存在就提示用户，不存在就新增或者更新
 * 填一填的设备数据是否存在，默认不存在=false
 * 对话框修改设备数据之后，对修改后的设备数据校验是否存在，默认不存在=false
 * <p>
 */
public final class DeviceFragment extends TitleBarFragment<MainActivity> implements StatusAction, BaseAdapter.OnChildClickListener {
    private static final String TAG = "DeviceFragment，界面==";
    private TextView mCameraDescView;
    private TextView mReadDescView;
    private StatusLayout mStatusView;
    private RecyclerView mRecyclerView;
    private TitleBar mTitleBar;
    private MMKV mmkv;
    private Gson mGson;
    private DeviceAdapter mAdapter;
    //当前登入用户的 username
    private String mLoginUsername;
    //此处查询数据库所有设备,在根据name,筛选出当前用户名创建(绑定)的所有设备
    private DownBindNameListBean indexBean;
    private List<DeviceDBBean> mDataList = new ArrayList<>();
    //扫码出来呢channel的数字也是0-1-2；
    //后台接口存的数字是：0-1-2:分别表示线路1；线路2；线路3；
    // App里面显示的是线路1-p2p，2-Nginx，3-WebRTC
    private String mChannel;
    private String account, password, liveIpPublic, liveIp, makeMessageMark, livePort, apiVersion, deviceTypeDesc, deviceName, deviceTypeDecNum;
    private String deviceCode, socketPort, httpPort, deviceTypeHexNum, currentSystemLanguage, mItemClickUserName, mItemClickPassword;
    private String mItemClickIp;
    private String mItemClickLivePort;
    private String mDDNSAccount;
    private String mDDNSPassword;
    private String mDDNSURL;

    private TextView mAccountView, mPasswordView, mMessageView, mLivePortView, mApiVersionView, mDeviceTypeDescView, mDeviceNameView, mDeviceCodeView, mSocketPortView, mHttpPortView;
    private ClearEditText mDeviceTypeView, mLiveIpView, mLiveIpPublicView, mDDNSAccountView, mDDNSPasswordView, mDDNSURLView;
    //设备对话框,信息填写或者更新的动作是否完成?, 默认没有填写完成==false,反之亦然
    private boolean isDeviceDialogInfoInputOrUpdateComplete = false;
    //(某个设备详细信息输入对话框)输入对话框是否存在的标志,存在（某个设备信息输入对话框）只刷新数据,不存在弹出对话框,默认不存在,默认不存在,默认不存在
    //防止点击设备类别  弹出多个DeviceDialog的Bug,默认不存在=false
    private boolean isDeviceDialogExist = false;
    private DeviceDBBean mDeviceDBBean;

    private static final int REQUEST_CODE_SCAN_ONE = 0X01;
    //刷新界面列表数据
    private static final int Refresh_RecycleView = 0x10;
    //只刷新某个设备详细信息对话框的数据
    private static final int Refresh_DeviceDialogInfo = 0x11;
    //设置某个设备详细信息对话框的数据--->说明是第一次创建某个设备详细信息对话框,此处设置默认数据
    private static final int Set_DeviceDialogInfo = 0x12;
    //添加，设备信息对话框:addDeviceDialogBuilder
    private AddDeviceDialog.Builder addBuilder;
    //修改，设备信息对话框：updateDeviceDialogBuilder
    private UpdateDeviceDialog.Builder updateBuilder;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Set_DeviceDialogInfo:             //设置,设备对话框的数据
                    setDeviceDialogInfo();
                    break;
                case Refresh_DeviceDialogInfo:         //刷新,设备对话框的数据
                    refreshDeviceDialogInfo();
                    break;
                case Refresh_RecycleView:              //刷新界面列表数据
                    showComplete();
                    mAdapter.setData(mDataList);
                    if (mDataList.isEmpty()) {
                        showEmpty();
                    }
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e(TAG + "==onResume=====当前用户下,绑定的设备==currentUsername==" + mLoginUsername);
        startThreadSetRecycleViewData();
    }

    public static DeviceFragment newInstance() {
        return new DeviceFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.device_fragment;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mTitleBar = findViewById(R.id.title_bar);
        mStatusView = findViewById(R.id.status_layout);
        mCameraDescView = findViewById(R.id.tv_01_camera);
        mReadDescView = findViewById(R.id.tv_02_read);
        mRecyclerView = findViewById(R.id.device_recycleview);
    }

    @Override
    protected void initData() {
        mmkv = MMKV.defaultMMKV();
        mGson = GsonFactory.getSingletonGson();
        mLoginUsername = (String) SharePreferenceUtil.get(Objects.requireNonNull(getAttachActivity()), SharePreferenceUtil.Current_Username, "");
        //此处查询数据库所有设备,在根据name,筛选出当前用户名创建(绑定)的所有设备
        indexBean = new DownBindNameListBean();
        //绑定谁添加的设备--用户名
        indexBean.setDownBindName(mLoginUsername);
        mAdapter = new DeviceAdapter(getAttachActivity());
        mAdapter.setOnChildClickListener(R.id.linear_item, this);
        mAdapter.setOnChildClickListener(R.id.delete_device, this);
        mAdapter.setOnChildClickListener(R.id.update_device, this);
        mAdapter.setOnChildClickListener(R.id.play_mode, this);
        mRecyclerView.setAdapter(mAdapter);
        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {

            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                new ListIconPopup.Builder(getActivity()).setList(getResources().getString(R.string.device_search), getResources().getString(R.string.device_scan), getResources().getString(R.string.device_writing))
                        .setListener((ListIconPopup.OnListener<String>) (popupWindow, position, s) -> {
                            if (s.equals(getResources().getString(R.string.device_scan))) {
                                getPermission2StartHWScanKit();
                            } else if (s.equals(getResources().getString(R.string.device_writing))) {
                                //先选择类型，在弹出设备dialog
                                isDeviceDialogInfoInputOrUpdateComplete = false;
                                //弹出类别对话框
                                showDeviceTypeDialog();
                            } else {
                                startActivity(SearchDeviceActivity.class);
                            }
                        }).setYOffset(-30).showAsDropDown(mTitleBar.getRightView());
            }
        });

    }

    @Override
    public void onChildClick(RecyclerView recyclerView, View childView, int position) {
        View itemView = Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(position);
        SwipeMenuLayout swipeMenuLay = itemView.findViewById(R.id.swipeMenuLay);
        int viewId = childView.getId();
        DeviceDBBean bean = mAdapter.getItem(position);
        //onItemClick
        if (viewId == R.id.linear_item) {
            toast("onItemClick");
            //默认:0==线路1(常规socket通讯),1==线路2(http模式),2==线路3(腾讯云转播)
            getLineDialog(bean);
            //删除设备
        } else if (viewId == R.id.delete_device) {
            swipeMenuLay.quickClose();
            showDeleteDialog(bean);
            //修改设备信息
        } else if (viewId == R.id.update_device) {
            swipeMenuLay.quickClose();
            showUpdateDialog(bean);
            //备用方案
        } else if (viewId == R.id.play_mode) {
            swipeMenuLay.quickClose();
            showSparePlanDialog(bean);
        }
    }


    // ** * * * * * * * * * * * * * * ** ** ** * 点击事件* * * * * * * * * * * ** * * * * * * * * * *
    //** * * * * * * * * * * * * * * * * * *  开始-开始-开始* * * * * * * * * * * * * * * * * * * * *
    //选择线路
    private void getLineDialog(DeviceDBBean bean) {
        //扫码出来呢channel的数字也是0-1-2；
        //后台接口存的数字是：0-1-2:分别表示线路1；线路2；线路3；
        // App里面显示的是线路1-p2p，2-Nginx，3-WebRTC
        LogUtils.e(TAG + "选择通道==之前选择的currentPosition=bean=" + bean.toString());
        LogUtils.e(TAG + "选择通道==bean.getChannel()==" + bean.getChannel());
        if (getResources().getString(R.string.device_line_01).equals(bean.getChannel())) {
            LogUtils.e(TAG + "选择通道==线路1");
            chosePlayerLine1Activity(bean);
        } else {
            LogUtils.e(TAG + "选择通道==线路2，3");
            checkLine23Info(bean);
        }
    }

    private void checkLine23Info(DeviceDBBean bean) {
        if ("".equals(bean.getApiVersion())) {
            toast(getResources().getString(R.string.device_api_version_is_null));
            return;
        }
        line23CheckSteamBean mBean = new line23CheckSteamBean();
        mBean.setDeviceNumber(bean.getDeviceCode());
        mBean.setAppPassword(bean.getAccount());
        mBean.setAppUsername(bean.getPassword());
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String json = mGson.toJson(mBean);
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
                    Request request = new Request.Builder()
                            .url(Constants.Live_checkAppDeviceInfo + "?plf=" + Constants.Type_A1_DECIMAL + "" + "&ver=" + bean.getApiVersion())
                            .post(body)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            LogUtils.e(TAG + "==校验====e=:" + e);
                            toast(getResources().getString(R.string.device_check_error));
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String body = response.body().string();
                            LogUtils.e(TAG + "==校验====body=:" + body);
                            if (JsonUtil.parseJson2CheckCode(body)) {
                                AppDeviceInfoBean queryBean = mGson.fromJson(body, AppDeviceInfoBean.class);
                                if (!queryBean.getResult().isCheck()) {
                                    toast(getResources().getString(R.string.device_no_permission_to_look));
                                    return;
                                }
                                //接口和扫码，线路是012，对应线路123
                                if (getResources().getString(R.string.device_line_02).equals(bean.getChannel())) {
                                    LogUtils.e(TAG + "选择通道==线路2");
                                    if (!("1".equals(queryBean.getResult().getCurrentLine()))) {
                                        toast(getResources().getString(R.string.device_check_fail));
                                        return;
                                    }
                                    chosePlayerLine2Activity(bean);
                                } else if (getResources().getString(R.string.device_line_03).equals(bean.getChannel())) {
                                    LogUtils.e(TAG + "选择通道==线路3");
                                    if (!("2".equals(queryBean.getResult().getCurrentLine()))) {
                                        toast(getResources().getString(R.string.device_check_fail));
                                        return;
                                    }
                                    chosePlayerLine3Activity(bean);
                                }
                            } else {
                                toast(getResources().getString(R.string.device_check_error));
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    /**
     * 根据线路的不同，选择不同的界面
     * 线路1
     *
     * @param bean 当前item的数据Bean
     */
    private void chosePlayerLine1Activity(DeviceDBBean bean) {
        LogUtils.e(TAG + "OnItemClic==k" + "username=:" + bean.getAccount() + ",  password=:" + bean.getPassword() + ",  ip:" + bean.getIp() + ",  备注:" + bean.getMsgMark() + ",  端口:"
                + bean.getLivePort() + ",  类型:" + bean.getDeviceTypeDesc());
        LogUtils.e(TAG + "OnItemClick==" + "DDNSAccount=:" + bean.getDDNSAcount() + ",  DDNSPassword=:" + bean.getDDNSPassword() + ",  getDDNSURL:" + bean.getDDNSURL());
        /**
         * HD3      rtsp://username:password@ip/MediaInput/h264/stream_1 ------   --HD3，高清:端口是80不用添加端口，不是80，就需要手动添加
         * HD3      rtsp://username:password@ip/MediaInput/h264/stream_2 ------   --HD3，标清
         * 一体机   rtsp://username:password@ip：port/session0.mpg ------           --一体机， 高清
         * 一体机   rtsp://username:password@ip：port/session1.mpg ------          --一体机， 标清
         * url      http://www.cme8848.com/live/cme.m3u8                          eg:链接地址=用户输入的url链接
         * url      http://www.cme8848.com/live/flv                               eg:链接地址=用户输入的url链接
         */
        mItemClickUserName = bean.getAccount();
        mItemClickPassword = bean.getPassword();
        mItemClickIp = bean.getIp();
        mItemClickLivePort = bean.getLivePort();
        String mItemDDNSAccount = bean.getDDNSAcount();
        String mItemDDNSPassword = bean.getDDNSPassword();
        String mItemDDNSAddress = bean.getDDNSURL();
        String currentUrl01;
        String currentUrl02;
        Intent intent = new Intent(getActivity(), PlayerLine1Activity.class);
        mmkv.encode(Constants.KEY_VLC_PLAYER_CHANNEL, Constants.Line1);
        //存入当前选中设备的  socketPort
        mmkv.encode(Constants.KEY_Device_SocketPort, bean.getSocketPort());
        mmkv.encode(Constants.KEY_Device_HttpPort, bean.getHttpPort());
        mmkv.encode(Constants.KEY_Device_Ip, bean.getIp());
        mmkv.encode(Constants.KEY_DeviceCode, bean.getDeviceCode());
        //       ("HD3", "HD3-4K", "一代一体机","耳鼻喉治疗台","妇科治疗台","泌尿治疗台","工作站","神州转播")  //对接协议 0:播放HD3,1:播放一体机,2:播放url链接地址
        //          0      1        2               3           4           5           6       7
        //对应上位机:01     05       07              8           9           10          00      FF
        LogUtils.e(TAG + "==OnItemClick==设备类型：====" + bean.getDeviceTypeDesc());
        switch (bean.getDeviceTypeDesc()) {
            case Constants.Type_HD3: //HD3  高清:端口是80不用添加端口，不是80，就需要手动添加
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/MediaInput/h264/stream_1";
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/MediaInput/h264/stream_2";
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("urlType", "0");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("mItemDDNSAccount", mItemDDNSAccount);
                intent.putExtra("mItemDDNSPassword", mItemDDNSPassword);
                intent.putExtra("mItemDDNSAddress", mItemDDNSAddress);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_01);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_01);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_type_HD3));
                startActivity(intent);
                break;
            case Constants.Type_HD3_4K: //HD3  高清:端口是80不用添加端口，不是80，就需要手动添加
                //HD3改成554端口,现在不管是内网还是外网都需要+ livePort
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/MediaInput/h264/stream_1";
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/MediaInput/h264/stream_2";
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("urlType", "1");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("mItemDDNSAccount", mItemDDNSAccount);
                intent.putExtra("mItemDDNSPassword", mItemDDNSPassword);
                intent.putExtra("mItemDDNSAddress", mItemDDNSAddress);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_05);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_05);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_type_HD3_4K));
                startActivity(intent);
                break;
            case Constants.Type_RC200: //RC200
                startRC200Activity(bean);
                break;
            case Constants.Type_V1_YiTiJi: //一代一体机
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("urlType", "02");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("mItemDDNSAccount", mItemDDNSAccount);
                intent.putExtra("mItemDDNSPassword", mItemDDNSPassword);
                intent.putExtra("mItemDDNSAddress", mItemDDNSAddress);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_07);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_07);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_V1_YiTiJi));
                startActivity(intent);
                break;
            case Constants.Type_Operation_YiTiJi: //手术一体机
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("urlType", "02");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("mItemDDNSAccount", mItemDDNSAccount);
                intent.putExtra("mItemDDNSPassword", mItemDDNSPassword);
                intent.putExtra("mItemDDNSAddress", mItemDDNSAddress);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_0B);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_0B);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_Operation_YiTiJi));
                startActivity(intent);
                break;
            case Constants.Type_EarNoseTable: //耳鼻喉治疗台
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //高清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("urlType", "3");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("mItemDDNSAccount", mItemDDNSAccount);
                intent.putExtra("mItemDDNSPassword", mItemDDNSPassword);
                intent.putExtra("mItemDDNSAddress", mItemDDNSAddress);
                mmkv.encode(Constants.KEY_Url01, currentUrl01);
                mmkv.encode(Constants.KEY_Url02, currentUrl02);
                mmkv.encode(Constants.KEY_BeanIP, mItemClickIp);
                mmkv.encode(Constants.KEY_UrlType, "3");
                mmkv.encode(Constants.KEY_Title, bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                mmkv.encode(Constants.KEY_Ip, bean.getIp());
                mmkv.encode(Constants.KEY_MicPort, bean.getMicPort());
                mmkv.encode(Constants.KEY_DDNS_Account, mItemDDNSAccount);
                mmkv.encode(Constants.KEY_DDNS_Password, mItemDDNSPassword);
                mmkv.encode(Constants.KEY_DDNS_Address, mItemDDNSAddress);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_08);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_08);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_EarNoseTable));
                startActivity(intent);
                break;
            case Constants.Type_FuKeTable: //妇科治疗台
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("urlType", "4");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("mItemDDNSAccount", mItemDDNSAccount);
                intent.putExtra("mItemDDNSPassword", mItemDDNSPassword);
                intent.putExtra("mItemDDNSAddress", mItemDDNSAddress);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_09);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_09);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_FuKeTable));
                startActivity(intent);
                break;
            case Constants.Type_MiNiaoTable: //泌尿治疗台
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("urlType", "5");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("mItemDDNSAccount", mItemDDNSAccount);
                intent.putExtra("mItemDDNSPassword", mItemDDNSPassword);
                intent.putExtra("mItemDDNSAddress", mItemDDNSAddress);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_0A);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_0A);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_MiNiaoTable));
                startActivity(intent);
                break;
            case Constants.Type_Work_Station_EN: //工作站
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("urlType", "6");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("mItemDDNSAccount", mItemDDNSAccount);
                intent.putExtra("mItemDDNSPassword", mItemDDNSPassword);
                intent.putExtra("mItemDDNSAddress", mItemDDNSAddress);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_00);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_00);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_Work_Station));
                startActivity(intent);
                break;
            case Constants.Type_Custom_Url: //神州转播
                currentUrl01 = bean.getIp();
                currentUrl02 = bean.getIp();
                intent.putExtra("beanIP", mItemClickIp);
                String replace1 = currentUrl01.replace(" ", "");
                String replace2 = currentUrl02.replace(" ", "");
                intent.putExtra("url01", replace1);
                intent.putExtra("url02", replace2);
                intent.putExtra("urlType", "7");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("mItemDDNSAccount", mItemDDNSAccount);
                intent.putExtra("mItemDDNSPassword", mItemDDNSPassword);
                intent.putExtra("mItemDDNSAddress", mItemDDNSAddress);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, "FF");
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_FF);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_FF);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_Custom_Url));
                startActivity(intent);
                break;
            default:
                toast(getResources().getString(R.string.device_toast01));
                break;
        }
    }


    /**
     * 更具工作模式选择不同的界面
     * 线路2
     *
     * @param bean 当前item的数据Bean
     */
    private void chosePlayerLine2Activity(DeviceDBBean bean) {
        LogUtils.e(TAG + "跳转播放界面" + "username=:" + bean.getAccount() + ",  password=:" + bean.getPassword() + ",  ip:" + bean.getIp() + ",  备注:" + bean.getMsgMark() + ",  端口:"
                + bean.getLivePort() + ",  类型:" + bean.getDeviceTypeDesc());
        LogUtils.e(TAG + "跳转播放界面" + "DDNSAcount=:" + bean.getDDNSAcount() + ",  DDNSPassword=:" + bean.getDDNSPassword() + ",  getDDNSURL:" + bean.getDDNSURL());
        /**
         * HD3      rtsp://username:password@ip/MediaInput/h264/stream_1 ------   --HD3，高清:端口是80不用添加端口，不是80，就需要手动添加
         * HD3      rtsp://username:password@ip/MediaInput/h264/stream_2 ------   --HD3，标清
         * 一体机   rtsp://username:password@ip：port/session0.mpg ------           --一体机， 高清
         * 一体机   rtsp://username:password@ip：port/session1.mpg ------          --一体机， 标清
         * url      http://www.cme8848.com/live/cme.m3u8                          eg:链接地址=用户输入的url链接
         * url      http://www.cme8848.com/live/flv                               eg:链接地址=用户输入的url链接
         */
        mItemClickUserName = bean.getAccount();
        mItemClickPassword = bean.getPassword();
        mItemClickIp = bean.getIp();
        mItemClickLivePort = bean.getLivePort();
        String currentUrl01;
        String currentUrl02;
        Intent intent = new Intent(getActivity(), PlayerLine2Activity.class);
        mmkv.encode(Constants.KEY_VLC_PLAYER_CHANNEL, Constants.Line2);
        //存入当前选中设备的  socketPort
        mmkv.encode(Constants.KEY_Device_SocketPort, bean.getSocketPort());
        mmkv.encode(Constants.KEY_Device_HttpPort, bean.getHttpPort());
        mmkv.encode(Constants.KEY_Device_Ip, bean.getIp());
        mmkv.encode(Constants.KEY_DeviceCode, bean.getDeviceCode());

        //是否开启备用方案, 默认未开启==false
        boolean sparePlan = bean.getSparePlan();
        String spareLiveSteam = bean.getSpareLiveSteam();
        String spareMicPushSteam = bean.getSpareMicPushSteam();
        //       ("HD3", "HD3-4K", "一代一体机","耳鼻喉治疗台","妇科治疗台","泌尿治疗台","工作站","神州转播")  //对接协议 0:播放HD3,1:播放一体机,2:播放url链接地址
        //          0      1        2               3           4           5           6       7
        //对应上位机:01     05       07              8           9           10          00      FF
        String apiVersion = CommonUtil.getApiVersion(bean);
        LogUtils.e(TAG + "==OnItemClick==设备类型：====" + bean.getDeviceTypeDesc());
        switch (bean.getDeviceTypeDesc()) {
            case Constants.Type_HD3: //HD3  高清:端口是80不用添加端口，不是80，就需要手动添加
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/MediaInput/h264/stream_1";
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/MediaInput/h264/stream_2";
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("urlType", "0");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_01);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_01);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_type_HD3));
                startActivity(intent);
                break;
            case Constants.Type_HD3_4K: //HD3  高清:端口是80不用添加端口，不是80，就需要手动添加
                //HD3改成554端口,现在不管是内网还是外网都需要+ livePort
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/MediaInput/h264/stream_1";
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/MediaInput/h264/stream_2";
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("urlType", "1");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_05);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_05);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_type_HD3_4K));
                startActivity(intent);
                break;
            case Constants.Type_RC200: //RC200
                startRC200Activity(bean);
                break;
            case Constants.Type_V1_YiTiJi: //一代一体机
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("urlType", "02");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_07);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_07);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_V1_YiTiJi));
                startActivity(intent);
                break;
            case Constants.Type_Operation_YiTiJi: //手术一体机
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("urlType", "02");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_0B);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_0B);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_Operation_YiTiJi));
                startActivity(intent);
                break;
            case Constants.Type_EarNoseTable: //耳鼻喉治疗台
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //高清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("urlType", "3");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_08);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_08);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_EarNoseTable));
                startActivity(intent);
                break;
            case Constants.Type_FuKeTable: //妇科治疗台
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("urlType", "4");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_09);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_09);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_FuKeTable));
                startActivity(intent);
                break;
            case Constants.Type_MiNiaoTable: //泌尿治疗台
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("urlType", "5");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_0A);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_0A);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_MiNiaoTable));
                startActivity(intent);
                break;
            case Constants.Type_Work_Station_EN: //工作站
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("urlType", "6");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, "00");
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_00);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_00);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_Work_Station));
                startActivity(intent);
                break;
            case Constants.Type_Custom_Url: //神州转播
                currentUrl01 = bean.getIp();
                currentUrl02 = bean.getIp();
                intent.putExtra("beanIP", mItemClickIp);
                String replace1 = currentUrl01.replace(" ", "");
                String replace2 = currentUrl02.replace(" ", "");
                intent.putExtra("url01", replace1);
                intent.putExtra("url02", replace2);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("urlType", "7");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_FF);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_FF);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_Custom_Url));
                startActivity(intent);
                break;
            default:
                toast(getResources().getString(R.string.device_toast01));
                break;
        }
    }

    /**
     * 更具工作模式选择不同的界面
     * 线路3
     *
     * @param bean 当前item的数据Bean
     */
    private void chosePlayerLine3Activity(DeviceDBBean bean) {
        LogUtils.e(TAG + "跳转播放界面" + "username=:" + bean.getAccount() + ",  password=:" + bean.getPassword() + ",  ip:" + bean.getIp() + ",  备注:" + bean.getMsgMark() + ",  端口:"
                + bean.getLivePort() + ",  类型:" + bean.getDeviceTypeDesc());
        LogUtils.e(TAG + "跳转播放界面" + "DDNSAcount=:" + bean.getDDNSAcount() + ",  DDNSPassword=:" + bean.getDDNSPassword() + ",  getDDNSURL:" + bean.getDDNSURL());
        /**
         * HD3      rtsp://username:password@ip/MediaInput/h264/stream_1 ------   --HD3，高清:端口是80不用添加端口，不是80，就需要手动添加
         * HD3      rtsp://username:password@ip/MediaInput/h264/stream_2 ------   --HD3，标清
         * 一体机   rtsp://username:password@ip：port/session0.mpg ------           --一体机， 高清
         * 一体机   rtsp://username:password@ip：port/session1.mpg ------          --一体机， 标清
         * url      http://www.cme8848.com/live/cme.m3u8                          eg:链接地址=用户输入的url链接
         * url      http://www.cme8848.com/live/flv                               eg:链接地址=用户输入的url链接
         */
        mItemClickUserName = bean.getAccount();
        mItemClickPassword = bean.getPassword();
        mItemClickIp = bean.getIp();
        mItemClickLivePort = bean.getLivePort();
        String currentUrl01;
        String currentUrl02;
        Intent intent = new Intent(getActivity(), PlayerLine3Activity.class);
        mmkv.encode(Constants.KEY_VLC_PLAYER_CHANNEL, Constants.Line3);
        //存入当前选中设备的  socketPort
        mmkv.encode(Constants.KEY_Device_SocketPort, bean.getSocketPort());
        mmkv.encode(Constants.KEY_Device_HttpPort, bean.getHttpPort());
        mmkv.encode(Constants.KEY_Device_Ip, bean.getIp());
        mmkv.encode(Constants.KEY_DeviceCode, bean.getDeviceCode());

        //是否开启备用方案, 默认未开启==false
        boolean sparePlan = bean.getSparePlan();
        String spareLiveSteam = bean.getSpareLiveSteam();
        String spareMicPushSteam = bean.getSpareMicPushSteam();

        // 对接协议 0:播放HD3,1:播放一体机,2:播放url链接地址
        //       ("HD3", "HD3-4K", "一代一体机","耳鼻喉治疗台","妇科治疗台","泌尿治疗台","工作站","神州转播")
        //          0      1        2               3           4           5           6       7
        //对应上位机:01     05       07              8           9           10          00      FF
        LogUtils.e(TAG + "==OnItemClick==设备类型：====" + bean.getDeviceTypeDesc());
        //如果ApiVersion等于空，默认给1.0.0.0
        String apiVersion = CommonUtil.getApiVersion(bean);
        switch (bean.getDeviceTypeDesc()) {
            case Constants.Type_HD3: //HD3  高清:端口是80不用添加端口，不是80，就需要手动添加
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/MediaInput/h264/stream_1";
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/MediaInput/h264/stream_2";
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("urlType", "0");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_01);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_01);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_type_HD3));
                startActivity(intent);
                break;
            case Constants.Type_HD3_4K: //HD3  高清:端口是80不用添加端口，不是80，就需要手动添加
                //HD3改成554端口,现在不管是内网还是外网都需要+ livePort
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/MediaInput/h264/stream_1";
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/MediaInput/h264/stream_2";
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("urlType", "1");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_05);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_05);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_type_HD3_4K));
                startActivity(intent);
                break;
            case Constants.Type_RC200: //RC200
                startRC200Activity(bean);
                break;
            case Constants.Type_V1_YiTiJi: //一代一体机
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("urlType", "02");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_07);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_07);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_V1_YiTiJi));
                startActivity(intent);
                break;
            case Constants.Type_Operation_YiTiJi: //手术一体机
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("urlType", "02");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_0B);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_0B);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_Operation_YiTiJi));
                startActivity(intent);
                break;
            case Constants.Type_EarNoseTable: //耳鼻喉治疗台
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //高清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("urlType", "3");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_08);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_08);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_EarNoseTable));
                startActivity(intent);
                break;
            case Constants.Type_FuKeTable: //妇科治疗台
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("urlType", "4");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_09);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_09);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_FuKeTable));
                startActivity(intent);
                break;
            case Constants.Type_MiNiaoTable: //泌尿治疗台
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("urlType", "5");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_0A);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_0A);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_MiNiaoTable));
                startActivity(intent);
                break;
            case Constants.Type_Work_Station_EN: //工作站
                currentUrl01 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session0.mpg";  //高清
                currentUrl02 = "rtsp://" + mItemClickUserName + ":" + mItemClickPassword + "@" + mItemClickIp + ":" + mItemClickLivePort + "/session1.mpg";  //标清
                intent.putExtra("url01", currentUrl01);
                intent.putExtra("url02", currentUrl02);
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                intent.putExtra("urlType", "6");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_00);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_00);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_Work_Station));
                startActivity(intent);
                break;
            case Constants.Type_Custom_Url: //神州转播
                currentUrl01 = bean.getIp();
                currentUrl02 = bean.getIp();
                intent.putExtra("beanIP", mItemClickIp);
                intent.putExtra("apiVersion", apiVersion);
                String replace1 = currentUrl01.replace(" ", "");
                String replace2 = currentUrl02.replace(" ", "");
                intent.putExtra("url01", replace1);
                intent.putExtra("url02", replace2);
                intent.putExtra("urlType", "7");
                intent.putExtra("mTitle", bean.getDeviceName() + " (" + mItemClickIp + ")");
                intent.putExtra("ip", bean.getIp());
                intent.putExtra("socketPort", bean.getSocketPort());
                intent.putExtra("httpPort", bean.getHttpPort());
                intent.putExtra("micport", bean.getMicPort());
                intent.putExtra("sparePlan", sparePlan);
                intent.putExtra("spareLiveSteam", spareLiveSteam);
                intent.putExtra("spareMicPushSteam", spareMicPushSteam);
                mmkv.encode(Constants.KEY_Device_Type_HexNum, Constants.Type_HexString_FF);
                mmkv.encode(Constants.KEY_Device_Type_DecNum, Constants.Type_DecString_FF);
                mmkv.encode(Constants.KEY_Device_Type_Desc, getResources().getString(R.string.device_Custom_Url));
                startActivity(intent);
                break;
            default:
                toast(getResources().getString(R.string.device_toast01));
                break;
        }
    }

    private void startRC200Activity(DeviceDBBean bean) {
        Intent intent = new Intent(getActivity(), PlayerRC200Activity.class);
        // http://192.168.67.200:3333/api/stream/video?session=1234567      直播拼接
        //http://192.168.1.10/api/begin                                     获取session地址
        // String url = "http://192.168.67.200:3333/api/begin";
        //http://192.168.67.200/3333/api/begin
        intent.putExtra("mTitle", bean.getDeviceName() + " (" + "ip:" + mItemClickIp + ")");
        intent.putExtra("ip", bean.getIp());
        intent.putExtra("account", bean.getAccount());
        intent.putExtra("password", bean.getPassword());
        intent.putExtra("sparePlan", bean.getSparePlan());
        intent.putExtra("spareLiveSteam", bean.getSpareLiveSteam());
        intent.putExtra("spareMicPushSteam", bean.getSpareMicPushSteam());
        startActivity(intent);
    }

    /**
     * 修改对话框
     * bean=>当前被修改的item数据bean
     * bean=>当前被修改的item数据bean
     */
    private void showUpdateDialog(DeviceDBBean bean) {
        isDeviceDialogInfoInputOrUpdateComplete = false;
        updateBuilder = new UpdateDeviceDialog.Builder(getActivity(), bean);
        LogUtils.e(TAG + "修改对话框--对话框的数据:" + bean.toString());
        updateBuilder.setTitle(getResources().getString(R.string.change_device));
        mDeviceTypeDescView = updateBuilder.getDeviceTypeView();
        // 内容必须要填写
        // 确定按钮文本
        updateBuilder.setConfirm(getString(R.string.common_confirm));
        // 设置 null 表示不显示取消按钮
        updateBuilder.setCancel(getString(R.string.common_cancel));
        updateBuilder.setCanceledOnTouchOutside(false);
        updateBuilder.setListener(new UpdateDeviceDialog.OnListener() {
            @Override
            public void onConfirm(BaseDialog dialog, HashMap<String, String> mMap) {
                //对DB做修改或者增加的操作
                getMsgDialogData2Bean(mMap, bean);
                LogUtils.e(TAG + "修改对话框===数据输入完毕===" + isDeviceDialogInfoInputOrUpdateComplete);
                LogUtils.e(TAG + "修改对话框===修改之后,Map数据===" + mMap.toString());
                //{makeMessage=一体机, password=root, port=7788, ip=192.168.1.200, title=一体机的标题, type=一体机, account=root}
                //修改数据需要设置id,和把之前的downBingNameList 赋值上去
                if (isDeviceDialogInfoInputOrUpdateComplete) {
                    String deviceName = bean.getDeviceName();
                    String deviceTypeDesc = mDeviceDBBean.getDeviceTypeDesc();
                    String deviceCode = null == mMap.get("deviceCode") ? "" : mMap.get("deviceCode");
                    String singleIndex = deviceCode + deviceTypeDesc + mDeviceDBBean.getChannel();
                    LogUtils.e(TAG + "修改对话框===修改之前,deviceCode==" + mDeviceDBBean.getDeviceCode());
                    LogUtils.e(TAG + "修改对话框===修改之后,deviceCode==" + deviceCode);
                    //RC200需要单独区分因为唯一标识不同,RC200标识: 用当前可用ip+deviceType+当前登入的用户名（admin）,比如:192.168.71.159RC200admin作为标识
                    if ("RC200".equals(deviceName)) {
                        //备注:此处存在一个问题,如果修改的时候切换了-设备类型,设备码就是空串,因为使用的都是默认值
                        mDeviceDBBean.setAcceptAndInsertDB(mDeviceDBBean.getIp() + "RC200" + mLoginUsername + mDeviceDBBean.getChannel());
                        mDeviceDBBean.setDeviceTypeNum(Constants.Type_HexString_A2);
                        mDeviceDBBean.setDeviceTypeHexNum(Constants.Type_HexString_A2);
                        mDeviceDBBean.setDeviceTypeDecNum(Constants.Type_DecString_A2);
                    } else {//其他设备
                        //设置唯一标识key:deviceOnlyCode16 + bean.getType()   //比如:b0087fc6fa584b62耳鼻喉治疗台
                        //备注:此处存在一个问题,如果修改的时候切换了-设备类型,设备码就是空串,因为使用的都是默认值
                        mDeviceDBBean.setAcceptAndInsertDB(singleIndex);
                        mDeviceDBBean.setDeviceTypeNum(mDeviceDBBean.getDeviceTypeNum());
                        mDeviceDBBean.setDeviceTypeHexNum(mDeviceDBBean.getDeviceTypeHexNum());
                        mDeviceDBBean.setDeviceTypeDecNum(mDeviceDBBean.getDeviceTypeDecNum());
                        mDeviceDBBean.setDeviceTypeDesc(mDeviceDBBean.getDeviceTypeDesc());
                    }
                    mDeviceDBBean.setId(bean.getId());
                    List<DownBindNameListBean> downBingNameList = bean.getDownBingNameList();
                    mDeviceDBBean.setDownBingNameList(downBingNameList);
                    LogUtils.e(TAG + "修改对话框==00==singleIndex==" + singleIndex);
                    List<DeviceDBBean> singleIndexTag = DeviceDBUtils.getQueryBeanByTag(getActivity(), singleIndex);
                    if (null != singleIndexTag && !singleIndexTag.isEmpty()) {
                        Long id = singleIndexTag.get(0).getId();
                        LogUtils.e(TAG + "修改对话框==修改之后,id==01==" + id);
                        LogUtils.e(TAG + "修改对话框==修改之后,id==02==" + bean.getId());
                        //自增长ID相同，说明可以更新设备信息，反之不同，说明当前数据库当前singleIndex设备已存在，不允许修改成当期singleIndex的设备。
                        if (Objects.equals(id, bean.getId())) {
                            DeviceDBUtils.insertOrReplaceInTx(getActivity(), mDeviceDBBean);
                            toast(getResources().getString(R.string.device_update_success));
                        } else {
                            toast(getResources().getString(R.string.device_update_fail));
                        }
                    } else {
                        DeviceDBUtils.insertOrReplaceInTx(getActivity(), mDeviceDBBean);
                        toast(getResources().getString(R.string.device_update_success));
                    }
                    updateBuilder.dismiss();
                    //此处查询所有数据库所有设备,再根据name,筛选出当前用户名绑定的设备
                    startThreadSetRecycleViewData();
                }
            }

            /**
             * 重新选择类别
             * @param mType
             */
            @Override
            public void onReInputTypeClick(TextView mType) {
                mDeviceTypeDescView = mType;
                showReSelectDeviceTypeDialog();
            }

            /**
             * 修改设备的时候,
             * 重新选择工作模式
             */
            @Override
            public void onReInputChannelClick() {
                showChannelDialog(updateBuilder.getChannelView());
            }
        }).show();


    }

    //从新选择,设备类别选择对话框
    private void showReSelectDeviceTypeDialog() {
        // 单选对话框
        new SelectDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.chose_device_type))
                .setList(getResources().getString(R.string.device_type_HD3), getResources().getString(R.string.device_type_HD3_4K), getResources().getString(R.string.device_type_RC200),
                        getResources().getString(R.string.device_V1_YiTiJi), getResources().getString(R.string.device_Operation_YiTiJi), getResources().getString(R.string.device_EarNoseTable),
                        getResources().getString(R.string.device_FuKeTable), getResources().getString(R.string.device_MiNiaoTable),
                        getResources().getString(R.string.device_Work_Station), getResources().getString(R.string.device_Custom_Url))
                .setSingleSelect()
                .setSelect(0)
                .setListener((SelectDialog.OnListener<String>) (dialog, data) -> {
                    String substring = data.toString().substring(1, 2);
                    isDeviceDialogExist = true;  //说明设备信息对话框,存在,只做数据的刷新
                    updateDeviceMessageDialogData(substring);
                })
                .setBackgroundDimEnabled(true)
                .show();
    }

    /**
     * 删除用户
     *
     * @param bean
     */
    private void showDeleteDialog(DeviceDBBean bean) {
        new MessageDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.mine_exit_title))
                .setMessage(getResources().getString(R.string.device_dialog_delete_title))
                .setConfirm(getResources().getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setListener(dialog -> {
                    DeviceDBUtils.deleteData(getActivity(), bean);
                    startThreadSetRecycleViewData();
                }).show();
    }

    /**
     * 是否开启：备用方案
     */
    private void showSparePlanDialog(DeviceDBBean bean) {
        if (getResources().getString(R.string.device_line_01).equals(bean.getChannel())) {
            toast(getResources().getString(R.string.device_line_one_not_supported));
            return;
        }
        LogUtils.e(TAG + "修改=========bean.getSparePlan()====" + bean.getSparePlan());
        //备用方案：默认未开启==false
        if (bean.getSparePlan()) {
            new Input2SteamDialog.Builder(getActivity())
                    .setTitle(getString(R.string.rc200_setting))
                    .setDataBean(bean)
                    .setConfirm(getString(R.string.device_item_play_mode_close))
                    .setCancel(getString(R.string.common_cancel))
                    .setCanceledOnTouchOutside(false)
                    .setListener((dialog, liveSteam, micSteam) -> {
                        bean.setSparePlan(false);
                        toast(getResources().getString(R.string.device_close_success));
                        bean.setSpareLiveSteam("");
                        bean.setSpareMicPushSteam("");
                        DeviceDBUtils.insertOrReplaceInTx(getActivity(), bean);
                    }).show();
        } else {
            // 输入对话框
            new Input2SteamDialog.Builder(getActivity())
                    .setTitle(getString(R.string.rc200_setting))
                    .setConfirm(getString(R.string.device_item_play_mode_start))
                    .setCancel(getString(R.string.common_cancel))
                    .setCanceledOnTouchOutside(false)
                    .setListener((dialog, liveSteam, micSteam) -> {
                        if ("".equals(liveSteam) || "".equals(micSteam)) {
                            toast(getResources().getString(R.string.device_steam_address_not_null));
                            return;
                        }
                        toast(getResources().getString(R.string.device_open_success));
                        bean.setSparePlan(true);
                        bean.setSpareLiveSteam(liveSteam);
                        bean.setSpareMicPushSteam(micSteam);
                        DeviceDBUtils.insertOrReplaceInTx(getActivity(), bean);
                    }).show();

        }

    }

    // ** * * * * * * * * * * * * * * ** ** ** * 点击事件* * * * * * * * * * * ** * * * * * * * * * *
    //** * * * * * * * * * * * * * * * * * *  stop-stop-stop * * * * * * * * * * * * * * * * * * * * *

    // ** * * * * * * * * * * * * * * * 填一填，新增设备和刷新设备数据* * * * * * * * * * * ** * * * * *
    //** * * * * * * * * * * * * * * * * * *  开始-开始-开始* * * * * * * * * * * * * * * * * * * * *

    /**
     * 设置数据-设备详细信息
     */
    @SuppressLint("SetTextI18n")
    private void setDeviceDialogInfo() {
        isDeviceDialogExist = true;    //防止点击视频类别  弹出多个Dialog的Bug
        if (null == addBuilder) {
            addBuilder = new AddDeviceDialog.Builder(getActivity());
        }
        mDeviceNameView = addBuilder.getDeviceNameView();
        mDeviceCodeView = addBuilder.getDeviceCodeView();
        mMessageView = addBuilder.getMessageMarkView();
        mLiveIpView = addBuilder.getLiveIpView();
        mLiveIpPublicView = addBuilder.getLiveIpPublicView();
        mDDNSAccountView = addBuilder.getDDNSAccountView();
        mDDNSPasswordView = addBuilder.getDDNSPasswordView();
        mDDNSURLView = addBuilder.getDDNSURLView();
        mAccountView = addBuilder.getAccountView();
        mPasswordView = addBuilder.getPasswordView();
        mSocketPortView = addBuilder.getSocketPortView();
        mHttpPortView = addBuilder.getHttpPortView();
        mLivePortView = addBuilder.getLivePortView();
        mApiVersionView = addBuilder.getApiVersionView();
        mDeviceTypeView = addBuilder.getDeviceTypeView();
        ClearEditText mLineView = addBuilder.getChannelView();
        mLineView.setText(getResources().getString(R.string.device_line_01));
        mDeviceNameView.setText(deviceName);
        mDeviceCodeView.setText(deviceCode);
        mMessageView.setText(makeMessageMark);
        mLiveIpView.setText(liveIp);
        mLiveIpPublicView.setText(liveIpPublic);
        mDDNSAccountView.setText(mDDNSAccount);
        mDDNSPasswordView.setText(mDDNSPassword);
        mDDNSURLView.setText(mDDNSURL);
        if (getResources().getString(R.string.device_Custom_Url).equals(deviceTypeDesc)) {
            mLiveIpView.setText("");
            mLiveIpPublicView.setText("");
            CommonUtil.showSoftInputFromWindow(getAttachActivity(), mLiveIpView);
        } else {
            mLiveIpView.setText(liveIp);
            mLiveIpPublicView.setText(liveIpPublic);
        }
        mAccountView.setText(account);
        mPasswordView.setText(password);
        mSocketPortView.setText(socketPort);
        mHttpPortView.setText(httpPort);
        mLivePortView.setText(livePort);
        String currentApiVersion = null == apiVersion ? Constants.ApiVersion.V1 : apiVersion;
        mApiVersionView.setText(currentApiVersion);
        mDeviceTypeView.setText(deviceTypeDesc);

        addBuilder.setTitle(getResources().getString(R.string.add_device));
        // 确定按钮文本
        addBuilder.setConfirm(getString(R.string.common_confirm));
        // 设置 null 表示不显示取消按钮
        addBuilder.setCancel(getString(R.string.common_cancel));
        addBuilder.setBackgroundDimEnabled(true);
        addBuilder.setCanceledOnTouchOutside(false);
        addBuilder.setListener(new AddDeviceDialog.OnListener() {
            @Override
            public void onConfirm(BaseDialog dialog, HashMap<String, String> mMap) {
                getMsgDialogData2Bean(mMap, null);
                LogUtils.e(TAG + "新增设备==数据输入完毕==" + isDeviceDialogInfoInputOrUpdateComplete);
                if (isDeviceDialogInfoInputOrUpdateComplete) {
                    //获取唯一标识码
                    String deviceTypeDesc = mDeviceDBBean.getDeviceTypeDesc();
                    String deviceCode = null == mMap.get("deviceCode") ? "" : mMap.get("deviceCode");
                    String singleIndex = deviceCode + deviceTypeDesc + mDeviceDBBean.getChannel();
                    LogUtils.e(TAG + "新增设备对话框==修改之后,id==singleIndex==" + singleIndex);
                    //判断需要新增的设备，数据库，是否存在
                    List<DeviceDBBean> singleIndexTag = DeviceDBUtils.getQueryBeanByTag(getActivity(), singleIndex);
                    if (singleIndexTag != null && !singleIndexTag.isEmpty()) {
                        //新增设备的时候，数据库存在singleIndex的数据，说明设备已存在
                        toast(getResources().getString(R.string.device_add_fail));
                    } else {
                        DeviceDBUtils.insertOrReplaceInTx(getActivity(), mDeviceDBBean);
                        toast(getResources().getString(R.string.device_toast05));
                    }
                    startThreadSetRecycleViewData();
                    addBuilder.dismissDialog();
                    isDeviceDialogExist = false;

                }
                //确认新增设备的时候,选择了了工作模式,之后再添加设备会出现保存上一次工作模式类型,这里手动清除解决bug
                addBuilder.getChannelView().setText("");

            }

            @Override
            public void onAddReInputDeviceTypeClick(TextView mTv) {
                mDeviceTypeDescView = mTv;
                LogUtils.e(TAG + "新增设备==" + mDeviceTypeDescView);
                //类别输入对话框
                showDeviceTypeDialog();
            }

            @Override
            public void onCancel(BaseDialog dialog) {
                isDeviceDialogExist = false;
            }

            /**
             * 新增设备的时候
             * 重新修改工作模式
             */
            @Override
            public void onAddChoseModeTypeClick() {
                showChannelDialog(addBuilder.getChannelView());
            }
        }).show();
    }

    /**
     * 刷新数据-设备详细信息
     */
    private void refreshDeviceDialogInfo() {
        if (null != updateBuilder) {
            mDeviceNameView = updateBuilder.getDeviceNameView();
            mDeviceCodeView = updateBuilder.getDeviceCodeView();
            mMessageView = updateBuilder.getMessageMarkView();
            mLiveIpView = updateBuilder.getLiveIpView();
            mLiveIpPublicView = updateBuilder.getLiveIpPublicView();
            mDDNSAccountView = updateBuilder.getDDNSAccountView();
            mDDNSPasswordView = updateBuilder.getDDNSPasswordView();
            mDDNSURLView = updateBuilder.getDDNSURLView();
            mAccountView = updateBuilder.getAccountView();
            mPasswordView = updateBuilder.getPasswordView();
            mSocketPortView = updateBuilder.getSocketPortView();
            mLivePortView = updateBuilder.getLivePortView();
            mApiVersionView = updateBuilder.getApiVersionView();
            mDeviceTypeView = updateBuilder.getDeviceTypeView();
            mDeviceNameView.setText(deviceName);
            mDeviceCodeView.setText(deviceCode);
            mMessageView.setText(makeMessageMark);
            mLiveIpView.setText(liveIp);
            mLiveIpPublicView.setText(liveIpPublic);
            mDDNSAccountView.setText(mDDNSAccount);
            mDDNSPasswordView.setText(mDDNSPassword);
            mDDNSURLView.setText(mDDNSURL);
            if (Constants.Type_Custom_Url.equals(deviceTypeDesc)) {
                mLiveIpView.setText("");
                mLiveIpPublicView.setText("");
                CommonUtil.showSoftInputFromWindow(getAttachActivity(), mLiveIpView);
            } else {
                mLiveIpView.setText(liveIp);
                mLiveIpPublicView.setText(liveIpPublic);
            }
            mAccountView.setText(account);
            mPasswordView.setText(password);
            mSocketPortView.setText(socketPort);
            mLivePortView.setText(livePort);
            String currentApiVersion = null == apiVersion ? Constants.ApiVersion.V1 : apiVersion;
            mApiVersionView.setText(currentApiVersion);
            mDeviceTypeView.setText(deviceTypeDesc);
        }

        if (null != addBuilder) {
            mDeviceNameView = addBuilder.getDeviceNameView();
            mDeviceCodeView = addBuilder.getDeviceCodeView();
            mMessageView = addBuilder.getMessageMarkView();
            mLiveIpView = addBuilder.getLiveIpView();
            mLiveIpPublicView = addBuilder.getLiveIpPublicView();
            mDDNSAccountView = addBuilder.getDDNSAccountView();
            mDDNSPasswordView = addBuilder.getDDNSPasswordView();
            mDDNSURLView = addBuilder.getDDNSURLView();
            mAccountView = addBuilder.getAccountView();
            mPasswordView = addBuilder.getPasswordView();
            mSocketPortView = addBuilder.getSocketPortView();
            mLivePortView = addBuilder.getLivePortView();
            mApiVersionView = addBuilder.getApiVersionView();
            mDeviceTypeView = addBuilder.getDeviceTypeView();
            mDeviceNameView.setText(deviceName);
            mDeviceCodeView.setText(deviceCode);
            mMessageView.setText(makeMessageMark);
            mLiveIpView.setText(liveIp);
            mLiveIpPublicView.setText(liveIpPublic);
            mDDNSAccountView.setText(mDDNSAccount);
            mDDNSPasswordView.setText(mDDNSPassword);
            mDDNSURLView.setText(mDDNSURL);
            if (Constants.Type_Custom_Url.equals(deviceTypeDesc)) {
                mLiveIpView.setText("");
                mLiveIpPublicView.setText("");
                CommonUtil.showSoftInputFromWindow(getAttachActivity(), mLiveIpView);
            } else {
                mLiveIpView.setText(liveIp);
                mLiveIpPublicView.setText(liveIpPublic);
            }
            mAccountView.setText(account);
            mPasswordView.setText(password);
            mSocketPortView.setText(socketPort);
            mLivePortView.setText(livePort);
            String currentApiVersion = null == apiVersion ? Constants.ApiVersion.V1 : apiVersion;
            mApiVersionView.setText(currentApiVersion);
            mDeviceTypeView.setText(deviceTypeDesc);
        }

    }

    /**
     * 设备类别选择对话框
     */
    private void showDeviceTypeDialog() {
        isDeviceDialogExist = false;
        new SelectDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.chose_device_type))
                .setList(getResources().getString(R.string.device_type_HD3), getResources().getString(R.string.device_type_HD3_4K), getResources().getString(R.string.device_type_RC200),
                        getResources().getString(R.string.device_V1_YiTiJi), getResources().getString(R.string.device_Operation_YiTiJi), getResources().getString(R.string.device_EarNoseTable),
                        getResources().getString(R.string.device_FuKeTable), getResources().getString(R.string.device_MiNiaoTable),
                        getResources().getString(R.string.device_Work_Station), getResources().getString(R.string.device_Custom_Url))
                .setSingleSelect()
                .setSelect(0)
                .setCanceledOnTouchOutside(true)
                .setBackgroundDimEnabled(true)
                .setListener((SelectDialog.OnListener<String>) (dialog, data) -> {
                    String substring = data.toString().substring(1, 2);
                    //确定了设备类型,更新信息输入对话框的信息
                    updateDeviceMessageDialogData(substring);
                }).show();
    }

    /**
     * 选择线路
     */
    private void showChannelDialog(ClearEditText mModeTypeView) {
        // 单选对话框
        new SelectDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.device_line))
                .setList(getResources().getString(R.string.device_line_01), getResources().getString(R.string.device_line_02), getResources().getString(R.string.device_line_03))
                .setSingleSelect()
                .setSelect(2)
                .setCanceledOnTouchOutside(false)
                .setListener((SelectDialog.OnListener<String>) (dialog, data) -> {
                    String mPosition = data.toString().substring(1, 2);
                    LogUtils.e(TAG + "选择通道==数据==" + data);
                    LogUtils.e(TAG + "选择通道==Position==" + mPosition);
                    //默认:0==线路1(常规socket通讯),1==线路2(http模式),2==线路3(WebRTC)
                    if ("0".equals(mPosition)) {
                        mModeTypeView.setText(getResources().getString(R.string.device_line_01));
                    } else if ("1".equals(mPosition)) {
                        mModeTypeView.setText(getResources().getString(R.string.device_line_02));
                    } else if ("2".equals(mPosition)) {
                        mModeTypeView.setText(getResources().getString(R.string.device_line_03));
                    }
                }).setBackgroundDimEnabled(true)
                .show();

    }

    /**
     * 获取设备信息输入框的数据
     * 然后设置到DeviceDBBean mBean 方便后续操作
     *
     * @param mMap
     * @param bean 修改的话,需要传入bean,新增时候传入null
     */

    private void getMsgDialogData2Bean(HashMap<String, String> mMap, DeviceDBBean bean) {
        //获取Dialog传递过来的数据
        deviceName = mMap.get("deviceName");
        deviceCode = mMap.get("deviceCode");
        makeMessageMark = mMap.get("msgMark");
        liveIp = mMap.get("liveIp");
        liveIpPublic = mMap.get("liveIpPublic");
        mDDNSAccount = mMap.get("ddnsAccount");
        mDDNSPassword = mMap.get("ddnsPassword");
        mDDNSURL = mMap.get("ddnsUrl");
        account = mMap.get("account");
        password = mMap.get("password");
        socketPort = mMap.get("socketPort");
        httpPort = mMap.get("httpPort");
        livePort = mMap.get("livePort");
        apiVersion = mMap.get("apiVersion");
        deviceTypeDesc = mMap.get("deviceTypeDesc");
        deviceTypeHexNum = mMap.get("deviceTypeHexNum");
        deviceTypeDecNum = mMap.get("deviceTypeDecNum");
        mChannel = mMap.get("channel");
        LogUtils.e(TAG + "getMsgDialogData2Bean==通道==" + mChannel);
        //解决手动切换之后判断条件失效问题
        if ("神州轉播".equals(deviceTypeDesc) || "神州转播".equals(deviceTypeDesc) || "rebroadcast by shenzhou".equals(deviceTypeDesc)) {
            if ("".equals(liveIp)) {
                toast(getResources().getString(R.string.device_toast07));
            } else if ("".equals(makeMessageMark)) {
                toast(getResources().getString(R.string.device_toast08));
            } else {
                isDeviceDialogInfoInputOrUpdateComplete = true;
                getMsgData2Bean(bean);
            }
        } else {
            if ("".equals(account)) {
                toast(getResources().getString(R.string.device_toast10));
            } else if ("".equals(password)) {
                toast(getResources().getString(R.string.device_toast11));
            } else if ("".equals(liveIp)) {
                toast(getResources().getString(R.string.device_toast07));
            } else if ("".equals(makeMessageMark)) {
                toast(getResources().getString(R.string.device_toast08));
            } else if ("".equals(livePort)) {
                toast(getResources().getString(R.string.device_toast12));
            } else if ("".equals(deviceTypeDesc)) {
                toast(getResources().getString(R.string.device_toast09));
            } else {
                isDeviceDialogInfoInputOrUpdateComplete = true;
                //修改对话框
                getMsgData2Bean(bean);
            }
        }
    }

    /**
     * 设备输入对话框信息,转换成DeviceDBBean
     * <p>
     * 设备唯一标识码==mDeviceDBBean.getDeviceCode() + mDeviceDBBean.getDeviceTypeDesc() + mDeviceDBBean.getChannel()
     * eg：iofad78efadf4ae8f智能一体机线路1
     * 每次新增或者修改数据的时候，都需要判断数据库是否存在当前bean
     * 存在就提示用户，不存在就新增或者更新
     * 填一填的设备数据是否存在，默认不存在=false
     * 对话框修改设备数据之后，对修改后的设备数据校验是否存在，默认不存在=false
     *
     * @param bean 当前设备的数据bean
     */
    private void getMsgData2Bean(DeviceDBBean bean) {
        mDeviceDBBean = new DeviceDBBean();
        mDeviceDBBean.setDeviceName(deviceName);
        mDeviceDBBean.setDeviceCode(deviceCode);
        mDeviceDBBean.setMsgMark(makeMessageMark);
        mDeviceDBBean.setChannel(mChannel);
        mDeviceDBBean.setIp(liveIp);
        if (null == mDDNSAccount) {
            mDeviceDBBean.setDDNSAcount("");
        } else {
            mDeviceDBBean.setDDNSAcount(mDDNSAccount);
        }
        if (null == mDDNSPassword) {
            mDeviceDBBean.setDDNSPassword("");
        } else {
            mDeviceDBBean.setDDNSPassword(mDDNSPassword);
        }
        if (null == mDDNSURL) {
            mDeviceDBBean.setDDNSURL("");
        } else {
            mDeviceDBBean.setDDNSURL(mDDNSURL);
        }
        mDeviceDBBean.setAccount(account);
        mDeviceDBBean.setPassword(password);
        mDeviceDBBean.setSocketPort(socketPort);
        mDeviceDBBean.setHttpPort(httpPort);
        mDeviceDBBean.setLivePort(livePort);
        String currentApiVersion = null == apiVersion ? Constants.ApiVersion.V1 : apiVersion;
        mDeviceDBBean.setApiVersion(currentApiVersion);
        mDeviceDBBean.setDeviceTypeDesc(deviceTypeDesc);
        mDeviceDBBean.setDeviceTypeNum(deviceTypeHexNum);
        mDeviceDBBean.setDeviceTypeHexNum(deviceTypeHexNum);
        mDeviceDBBean.setDeviceTypeDecNum(deviceTypeDecNum);
        mDeviceDBBean.setTag(mLoginUsername);
        //填一填新增数据的时候，需要判断当前设备，数据库是否已经存在
        String singleIndex = mDeviceDBBean.getDeviceCode() + mDeviceDBBean.getDeviceTypeDesc() + mDeviceDBBean.getChannel();
        LogUtils.e(TAG + "此设备==singleIndex==" + singleIndex);
        LogUtils.e(TAG + "此设备==singleIndex=2=" + mDeviceDBBean.toString());
        List<DeviceDBBean> singleIndexTag = DeviceDBUtils.getQueryBeanByTag(getActivity(), singleIndex);
        if (null != singleIndexTag && !singleIndexTag.isEmpty()) {
            LogUtils.e(TAG + "此设备存在==");
        } else {
            LogUtils.e(TAG + "此设备不存在==需要新增");
            mDeviceDBBean.setAcceptAndInsertDB(mDeviceDBBean.getDeviceCode() + mDeviceDBBean.getDeviceTypeDesc() + mDeviceDBBean.getChannel());
            //新增设备的时候：设备类型数据库备用方案字段的默认值
            mDeviceDBBean.setSparePlan(Constants.Device_Common_Default_Spare);
            mDeviceDBBean.setSpareLiveSteam(Constants.Device_Common_Default_LiveSteam);
            mDeviceDBBean.setSpareMicPushSteam(Constants.Device_Common_Default_MicSteam);
            //todo 新增
            if (null == bean) {//新增类型,修改设备类型的时候不用更改 nameList
                ArrayList<DownBindNameListBean> downNameList = new ArrayList<>();
                DownBindNameListBean nameBean = new DownBindNameListBean();
                //绑定谁添加的设备--用户名
                nameBean.setDownBindName(mLoginUsername);
                downNameList.add(nameBean);
                mDeviceDBBean.setDownBingNameList(downNameList);
            }
        }
    }

    /**
     * 确定了设备类型,更新信息输入对话框的默认信息
     * <p>
     * 上位机对呀type_num
     * <p>
     * * 00-工作站， 01-HD3摄像机，02-冷光源，03-气腹机，04-冲洗机，05-4K摄像机，06-耳鼻喉控制板，
     * * 07-一代一体机，8-耳鼻喉治疗台，9-妇科治疗台，10-泌尿治疗台
     * * A0-iOS，A1-Android，A2-RC200，A3-网页直播，A4-后台管理，A4-CMEPlayer win平台，FF-所有设备
     */
    private void updateDeviceMessageDialogData(String dialogPosition) {
        /**
         * HD3      rtsp://username:password@ip/MediaInput/h264/stream_1 ------     --HD3，高清
         * HD3      rtsp://username:password@ip/MediaInput/h264/stream_2 ------     --HD3，标清
         * 一体机   rtsp://username:password@ip port/session0.mpg ------            --一体机，标清
         * 一体机   rtsp://username:password@ip：port/session1.mpg ------            --一体机，高清
         * url      http://www.cme8848.com/live/cme.m3u8                            eg:链接地址=用户输入的url链接
         * url      http://www.cme8848.com/live/flv                                 eg:链接地址=用户输入的url链接
         */
        //设备类型数据库备用方案字段的默认值
        //Boolean spareStatue = Constants.Device_Common_Default_Spare;
        //String spareLiveSteam = Constants.Device_Common_Default_LiveSteam;
        //String spareMicSteam = Constants.Device_Common_Default_MicSteam;
        LogUtils.e(TAG + "当前设备类型=====" + dialogPosition);
        switch (dialogPosition) {
            case "0":   //HD3
                deviceName = getResources().getString(R.string.device_type_HD3);
                deviceCode = "";
                makeMessageMark = getResources().getString(R.string.device_mark_message);
                liveIp = Constants.Type_HD3_ip;
                liveIpPublic = Constants.Type_HD3_ip_public;
                mDDNSAccount = Constants.Config_DDNS_Account;
                mDDNSPassword = Constants.Config_DDNS_Account;
                mDDNSURL = Constants.Config_DDNS_Url;
                account = Constants.Type_HD3_Account;
                password = Constants.Type_HD3_Password;
                socketPort = Constants.Type_HD3_SocketPort;
                httpPort = Constants.Type_HD3_HttpPort;
                livePort = Constants.Type_HD3_LivePort;
                apiVersion = Constants.ApiVersion.V1;
                deviceTypeDesc = getResources().getString(R.string.device_type_HD3);
                deviceTypeHexNum = Constants.Type_HexString_00;
                deviceTypeDecNum = Constants.Type_DecString_00;
                if (!isDeviceDialogExist) {   //不存在
                    mHandler.sendEmptyMessage(Set_DeviceDialogInfo);  //设置数据
                } else {
                    mHandler.sendEmptyMessage(Refresh_DeviceDialogInfo); //刷新数据
                }
                break;
            case "1":   //HD3-4K
                deviceName = getResources().getString(R.string.device_type_HD3_4K);
                deviceCode = "";
                makeMessageMark = getResources().getString(R.string.device_mark_message);
                liveIp = Constants.Type_HD3_4K_ip;
                liveIpPublic = Constants.Type_HD3_4K_ip_public;
                mDDNSAccount = Constants.Config_DDNS_Account;
                mDDNSPassword = Constants.Config_DDNS_Account;
                mDDNSURL = Constants.Config_DDNS_Url;
                account = Constants.Type_HD3_4K_Account;
                password = Constants.Type_HD3_4K_Password;
                socketPort = Constants.Type_HD3_4K_SocketPort;
                httpPort = Constants.Type_HD3_4K_HttpPort;
                livePort = Constants.Type_HD3_4K_LivePort;
                apiVersion = Constants.ApiVersion.V1;
                deviceTypeDesc = getResources().getString(R.string.device_type_HD3_4K);
                deviceTypeHexNum = Constants.Type_HexString_05;
                deviceTypeDecNum = Constants.Type_DecString_05;
                if (!isDeviceDialogExist) {   //不存在
                    mHandler.sendEmptyMessage(Set_DeviceDialogInfo);  //设置数据
                } else {
                    mHandler.sendEmptyMessage(Refresh_DeviceDialogInfo); //刷新数据
                }
                break;
            case "2":   //RC200
                deviceName = getResources().getString(R.string.device_type_RC200);
                deviceCode = "";
                makeMessageMark = getResources().getString(R.string.device_mark_message);
                liveIpPublic = Constants.Type_RC200_ip_public;
                mDDNSAccount = Constants.Config_DDNS_Account;
                mDDNSPassword = Constants.Config_DDNS_Account;
                mDDNSURL = Constants.Config_DDNS_Url;
                account = Constants.Type_RC200_Account;
                password = Constants.Type_RC200_Password;
                socketPort = Constants.Type_RC200_SocketPort;
                httpPort = Constants.Type_RC200_HttpPort;
                livePort = Constants.Type_RC200_LivePort;
                apiVersion = Constants.ApiVersion.V1;
                deviceTypeDesc = getResources().getString(R.string.device_type_RC200);
                deviceTypeHexNum = Constants.Type_HexString_A2;
                deviceTypeDecNum = Constants.Type_DecString_A2;
                if (!isDeviceDialogExist) {   //不存在
                    mHandler.sendEmptyMessage(Set_DeviceDialogInfo);  //设置数据
                } else {
                    mHandler.sendEmptyMessage(Refresh_DeviceDialogInfo); //刷新数据
                }
                break;
            case "3":   //智能一体机,最开始老版本的一体机
                deviceName = getResources().getString(R.string.device_V1_YiTiJi);
                deviceCode = "";
                makeMessageMark = getResources().getString(R.string.device_mark_message);
                liveIp = Constants.Type_V1_YiTiJi_ip;
                liveIpPublic = Constants.Type_V1_YiTiJi_ip_public;
                mDDNSAccount = Constants.Config_DDNS_Account;
                mDDNSPassword = Constants.Config_DDNS_Account;
                mDDNSURL = Constants.Config_DDNS_Url;
                account = Constants.Type_V1_YiTiJi_Account;
                password = Constants.Type_V1_YiTiJi_Password;
                socketPort = Constants.Type_V1_YiTiJi_SocketPort;
                httpPort = Constants.Type_V1_YiTiJi_HttpPort;
                livePort = Constants.Type_V1_YiTiJi_LivePort;
                apiVersion = Constants.ApiVersion.V1;
                deviceTypeDesc = getResources().getString(R.string.device_V1_YiTiJi);
                deviceTypeHexNum = Constants.Type_HexString_07;
                deviceTypeDecNum = Constants.Type_DecString_07;
                if (!isDeviceDialogExist) {   //不存在
                    mHandler.sendEmptyMessage(Set_DeviceDialogInfo);  //设置数据
                } else {
                    mHandler.sendEmptyMessage(Refresh_DeviceDialogInfo); //刷新数据
                }
                break;
            case "4":   //手术一体机
                deviceName = getResources().getString(R.string.device_Operation_YiTiJi);
                deviceCode = "";
                makeMessageMark = getResources().getString(R.string.device_mark_message);
                liveIp = Constants.Type_Operation_YiTiJi_ip;
                liveIpPublic = Constants.Type_Operation_YiTiJi_ip_public;
                mDDNSAccount = Constants.Config_DDNS_Account;
                mDDNSPassword = Constants.Config_DDNS_Account;
                mDDNSURL = Constants.Config_DDNS_Url;
                account = Constants.Type_Operation_YiTiJi_Account;
                password = Constants.Type_Operation_YiTiJi_Password;
                socketPort = Constants.Type_Operation_YiTiJi_SocketPort;
                httpPort = Constants.Type_Operation_YiTiJi_HttpPort;
                livePort = Constants.Type_Operation_YiTiJi_LivePort;
                apiVersion = Constants.ApiVersion.V1;
                deviceTypeDesc = getResources().getString(R.string.device_Operation_YiTiJi);
                deviceTypeHexNum = Constants.Type_HexString_0B;
                deviceTypeDecNum = Constants.Type_DecString_0B;
                if (!isDeviceDialogExist) {   //不存在
                    mHandler.sendEmptyMessage(Set_DeviceDialogInfo);  //设置数据
                } else {
                    mHandler.sendEmptyMessage(Refresh_DeviceDialogInfo); //刷新数据
                }
                break;
            case "5"://耳鼻喉治疗台
                deviceName = getResources().getString(R.string.device_EarNoseTable);
                deviceCode = "";
                makeMessageMark = getResources().getString(R.string.device_mark_message);
                liveIp = Constants.Type_EarNoseTable_ip;
                liveIpPublic = Constants.Type_EarNoseTable_ip_public;
                mDDNSAccount = Constants.Config_DDNS_Account;
                mDDNSPassword = Constants.Config_DDNS_Account;
                mDDNSURL = Constants.Config_DDNS_Url;
                account = Constants.Type_EarNoseTable_Account;
                password = Constants.Type_EarNoseTable_Password;
                socketPort = Constants.Type_EarNoseTable_SocketPort;
                httpPort = Constants.Type_EarNoseTable_HttpPort;
                livePort = Constants.Type_EarNoseTable_LivePort;
                apiVersion = Constants.ApiVersion.V1;
                deviceTypeDesc = getResources().getString(R.string.device_EarNoseTable);
                deviceTypeHexNum = Constants.Type_HexString_08;
                deviceTypeDecNum = Constants.Type_DecString_08;
                if (!isDeviceDialogExist) {   //不存在
                    mHandler.sendEmptyMessage(Set_DeviceDialogInfo);  //设置数据
                } else {
                    mHandler.sendEmptyMessage(Refresh_DeviceDialogInfo); //刷新数据
                }
                break;
            case "6": //妇科治疗台
                deviceName = getResources().getString(R.string.device_FuKeTable);
                deviceCode = "";
                makeMessageMark = getResources().getString(R.string.device_mark_message);
                liveIp = Constants.Type_FuKeTable_ip;
                liveIpPublic = Constants.Type_FuKeTable_ip_public;
                mDDNSAccount = Constants.Config_DDNS_Account;
                mDDNSPassword = Constants.Config_DDNS_Account;
                mDDNSURL = Constants.Config_DDNS_Url;
                account = Constants.Type_FuKeTable_Account;
                password = Constants.Type_FuKeTable_Password;
                socketPort = Constants.Type_FuKeTable_SocketPort;
                httpPort = Constants.Type_FuKeTable_HttpPort;
                livePort = Constants.Type_FuKeTable_LivePort;
                apiVersion = Constants.ApiVersion.V1;
                deviceTypeDesc = getResources().getString(R.string.device_FuKeTable);
                deviceTypeHexNum = Constants.Type_HexString_09;
                deviceTypeDecNum = Constants.Type_DecString_09;
                if (!isDeviceDialogExist) {   //不存在
                    mHandler.sendEmptyMessage(Set_DeviceDialogInfo);  //设置数据
                } else {
                    mHandler.sendEmptyMessage(Refresh_DeviceDialogInfo); //刷新数据
                }
                break;
            case "7"://泌尿治疗台
                deviceName = getResources().getString(R.string.device_MiNiaoTable);
                deviceCode = "";
                makeMessageMark = getResources().getString(R.string.device_mark_message);
                liveIp = Constants.Type_MiNiaoTable_ip;
                liveIpPublic = Constants.Type_MiNiaoTable_ip_public;
                mDDNSAccount = Constants.Config_DDNS_Account;
                mDDNSPassword = Constants.Config_DDNS_Account;
                mDDNSURL = Constants.Config_DDNS_Url;
                account = Constants.Type_MiNiaoTable_Account;
                password = Constants.Type_MiNiaoTable_Password;
                socketPort = Constants.Type_MiNiaoTable_SocketPort;
                httpPort = Constants.Type_MiNiaoTable_HttpPort;
                livePort = Constants.Type_MiNiaoTable_LivePort;
                apiVersion = Constants.ApiVersion.V1;
                deviceTypeDesc = getResources().getString(R.string.device_MiNiaoTable);
                deviceTypeHexNum = Constants.Type_HexString_0A;
                deviceTypeDecNum = Constants.Type_DecString_0A;
                if (!isDeviceDialogExist) {   //不存在
                    mHandler.sendEmptyMessage(Set_DeviceDialogInfo);  //设置数据
                } else {
                    mHandler.sendEmptyMessage(Refresh_DeviceDialogInfo); //刷新数据
                }
                break;
            case "8"://工作站
                deviceName = getResources().getString(R.string.device_Work_Station);
                deviceCode = "";
                makeMessageMark = getResources().getString(R.string.device_mark_message);
                liveIp = Constants.Type_Work_Station_ip;
                liveIpPublic = Constants.Type_Work_Station_ip_public;
                mDDNSAccount = Constants.Config_DDNS_Account;
                mDDNSPassword = Constants.Config_DDNS_Account;
                mDDNSURL = Constants.Config_DDNS_Url;
                account = Constants.Type_Work_Station_Account;
                password = Constants.Type_Work_Station_Password;
                socketPort = Constants.Type_Work_Station_SocketPort;
                httpPort = Constants.Type_Work_Station_HttpPort;
                livePort = Constants.Type_Work_Station_LivePort;
                apiVersion = Constants.ApiVersion.V1;
                deviceTypeDesc = getResources().getString(R.string.device_Work_Station);
                deviceTypeHexNum = Constants.Type_HexString_00;
                deviceTypeDecNum = Constants.Type_DecString_00;
                if (!isDeviceDialogExist) {   //不存在
                    mHandler.sendEmptyMessage(Set_DeviceDialogInfo);  //设置数据
                } else {
                    mHandler.sendEmptyMessage(Refresh_DeviceDialogInfo); //刷新数据
                }
                break;
            case "9":   //神州转播(自定义url)
                deviceName = getResources().getString(R.string.device_Custom_Url);
                deviceCode = "";
                makeMessageMark = getResources().getString(R.string.device_mark_message);
                liveIp = Constants.Type_Custom_Url_ip;
                liveIpPublic = Constants.Type_Custom_Url_ip_public;
                mDDNSAccount = Constants.Config_DDNS_Account;
                mDDNSPassword = Constants.Config_DDNS_Account;
                mDDNSURL = Constants.Config_DDNS_Url;
                account = Constants.Type_Custom_Url_Account;
                password = Constants.Type_Custom_Url_Password;
                socketPort = Constants.Type_Custom_Url_SocketPort;
                httpPort = Constants.Type_Custom_Url_HttpPort;
                livePort = Constants.Type_Custom_Url_LivePort;
                apiVersion = Constants.ApiVersion.V1;
                deviceTypeDesc = getResources().getString(R.string.device_Custom_Url);
                deviceTypeHexNum = Constants.Type_HexString_FF;
                deviceTypeDecNum = Constants.Type_DecString_FF;
                if (!isDeviceDialogExist) {   //不存在
                    mHandler.sendEmptyMessage(Set_DeviceDialogInfo);  //设置数据
                } else {
                    mHandler.sendEmptyMessage(Refresh_DeviceDialogInfo); //刷新数据
                }
                break;
        }

    }

    // ** * * * * * * * * * * * * * * * 填一填，新增设备和刷新设备数据* * * * * * * * * * * ** * * * * *
    //** * * * * * * * * * * * * * * * * * *  stop-stop-stop* * * * * * * * * * * * * * * * * * * * *


    //** * * * * * * * * * * * * * * * * * * * * 华为扫码相关code* * * * * * * * * * * * * * * * * * *
    //** * * * * * * * * * * * * * * * * * * * * 开始-开始-开始* * * * * * * * * * * * * * * * * * * *

    /**
     * 扫一扫
     * 华为扫码
     * 获取相机权限
     */
    private void getPermission2StartHWScanKit() {
        mCameraDescView.setVisibility(View.VISIBLE);
        mReadDescView.setVisibility(View.GONE);
        XXPermissions.with(this)
                .permission(Permission.CAMERA).request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        LogUtils.e(TAG + "权限申请成功");
                        mCameraDescView.setVisibility(View.GONE);
                        mReadDescView.setVisibility(View.GONE);
                        if (all) {
                            HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).setViewType(1).setErrorCheck(true).create();
                            ScanUtil.startScan(getActivity(), REQUEST_CODE_SCAN_ONE, options);
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        LogUtils.e(TAG + "权限申请失败");
                        mCameraDescView.setVisibility(View.GONE);
                        mReadDescView.setVisibility(View.GONE);
                        if (never) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            go2SystemSettingDialog(permissions);
                        } else {
                            toast(getResources().getString(R.string.device_toast03));
                        }
                    }
                });

    }

    /**
     * 华为扫码相关
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.e(TAG + "扫码结果:resultCode==" + resultCode);
        LogUtils.e(TAG + "扫码结果:requestCode==" + requestCode);
        if (requestCode != 1) {
            LogUtils.e(TAG + "扫码结果:=扫描失败==");
            toast("扫描失败!");
            return;
        }
        int errorCode = 100;
        try {
            errorCode = data.getIntExtra(ScanUtil.RESULT_CODE, ScanUtil.SUCCESS);
        } catch (Exception e) {
            LogUtils.e(TAG + "扫码结果:Exception=e=" + e);
            return;
        }
        //Default View
        //暂无存储权限
        if (errorCode == ScanUtil.ERROR_NO_READ_PERMISSION) {
            getPermissionRead2StartHWScanKit();
        } else {
            HmsScan hmsScan = data.getParcelableExtra(ScanUtil.RESULT);
            if (hmsScan == null) {
                LogUtils.e(TAG + "扫码结果:解析错误");
                toast(getResources().getString(R.string.device_parsing_error));
            } else {
                try {
                    String result = hmsScan.getOriginalValue();
                    LogUtils.e(TAG + "扫码结果:" + result);
                    if (!"".equals(result)) {
                        if (JsonUtil.isGoodJson(result)) {  //是json数据 HD3  或者一体机的格式
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    HuaweiScanPlus.getJsonData(getAttachActivity(), mLoginUsername, result);
                                }
                            }.start();
                        } else {//暂时认定为自定义url链接
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    HuaweiScanPlus.getCustomUrl(getAttachActivity(), mLoginUsername, result);
                                }
                            }.start();
                        }
                    }
                } catch (Exception e) {
                    toast(getResources().getString(R.string.device_the_scan_code_is_abnormal));
                }
            }
        }

    }

    /**
     * 扫一扫
     * 华为扫码
     * 获取读取--相册权限--之后,开启华为扫码
     */
    private void getPermissionRead2StartHWScanKit() {
        mCameraDescView.setVisibility(View.GONE);
        mReadDescView.setVisibility(View.VISIBLE);
        XXPermissions.with(this)
                .permission(Permission.READ_EXTERNAL_STORAGE)  //正式版本
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        mCameraDescView.setVisibility(View.GONE);
                        mReadDescView.setVisibility(View.GONE);
                        if (all) {
                            HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).setViewType(1).setErrorCheck(true).create();
                            ScanUtil.startScan(getActivity(), REQUEST_CODE_SCAN_ONE, options);
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        mCameraDescView.setVisibility(View.GONE);
                        mReadDescView.setVisibility(View.GONE);
                        if (never) {
                            toast(getResources().getString(R.string.device_toast02));
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(getAttachActivity(), permissions);
                        } else {
                            toast(getResources().getString(R.string.device_toast03));
                        }
                    }
                });
    }

    /**
     * 多次拒绝权限之后，跳转设置界面，也需要告知用户
     */

    private void go2SystemSettingDialog(List<String> permissions) {
        new MessageDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.mine_exit_title))
                .setMessage(getResources().getString(R.string.permission_setting_description))
                .setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setCanceledOnTouchOutside(false)
                .setListener(dialog -> XXPermissions.startPermissionActivity(getAttachActivity(), permissions))
                .show();
    }
    //** * * * * * * * * * * * * * * * * * * * * 华为扫码相关code* * * * * * * * * * * * * * * * * * *
    //** * * * * * * * * * * * * * * * * * * * * 结束-结束-结束* * * * * * * * * * * * * * * * * * * *

    //开启线程，读取数据库数据，然后刷新界面
    private void startThreadSetRecycleViewData() {
        new Thread(() -> {
            mLoginUsername = (String) SharePreferenceUtil.get(Objects.requireNonNull(getAttachActivity()), SharePreferenceUtil.Current_Username, "");
            indexBean = new DownBindNameListBean();
            //绑定谁添加的设备--用户名
            indexBean.setDownBindName(mLoginUsername);
            LogUtils.e(TAG + "当前登入的用户名：mLoginUsername==" + mLoginUsername);
            //查询出设备表所有设备数据
            List<DeviceDBBean> AllDeviceDBBeanList = DeviceDBUtils.queryAll(getActivity());
            mDataList.clear();
            mDataList = DeviceDBUtils.getQueryBeanByNameBean(getActivity(), indexBean);
            if (null != AllDeviceDBBeanList) {
                LogUtils.e(TAG + "设备表总数==" + AllDeviceDBBeanList.size());
                LogUtils.e(TAG + "当前登入的用户下,绑定的设备总数==" + mDataList.size());
                for (int i = 0; i < AllDeviceDBBeanList.size(); i++) {
                    LogUtils.e("设备表总数,==第==" + i + "=条数据==" + AllDeviceDBBeanList.get(i).toString());
                }
                for (int i = 0; i < mDataList.size(); i++) {
                    LogUtils.e(TAG + "当前登入的用户下,绑定的设备==第==" + i + "=条数据==" + mDataList.get(i).toString());
                }
            } else {
                LogUtils.e(TAG + "设备表总数==0");
            }
            mHandler.sendEmptyMessage(Refresh_RecycleView);
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        //刷新列表数据
        startThreadSetRecycleViewData();
        //显示toast
        toast(event.getToastStr());
    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusView;
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}