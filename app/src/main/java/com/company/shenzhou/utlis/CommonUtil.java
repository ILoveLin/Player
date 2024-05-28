package com.company.shenzhou.utlis;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.company.shenzhou.R;
import com.company.shenzhou.bean.dbbean.DeviceDBBean;
import com.company.shenzhou.bean.dbbean.UserDBBean;
import com.company.shenzhou.bean.dbbean.UserDBRememberBean;
import com.company.shenzhou.bean.socket.MicSocketBean;
import com.company.shenzhou.global.Constants;
import com.company.shenzhou.playerdb.manager.UserDBBeanUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/5/24 14:49
 * desc：
 */
public class CommonUtil {

    public static String getMicUrl(String socketUrl, String currentIP) {

        if ("".equals(currentIP)) {
            currentIP = "192.123.66.64";
            LogUtils.e("ANetty:ddns模式 麦克风推流地址:ddnsIP的ip为空 地址转换错误~");

        }
        int i1 = socketUrl.lastIndexOf(":");
        String substring = socketUrl.substring(i1);
        LogUtils.e("ANetty:ddns模式 getMicUrl:" + substring);

        return "rtmp://" + currentIP + substring;

    }

    public static String getApiVersion(DeviceDBBean bean) {
        String apiVersion = "";
        if (null == bean.getApiVersion() || "".equals(bean.getApiVersion())) {
            apiVersion = Constants.ApiVersion.V1;
            return apiVersion;
        } else {
            apiVersion = bean.getApiVersion();
            return apiVersion;

        }
    }

    public static MicSocketBean getSocketBean(String errCode, String operation, String mMicName) {
        MicSocketBean startBean = new MicSocketBean();
        startBean.setErrCode(errCode);
        startBean.setOperation(operation);
        startBean.setVoiceID("");
        startBean.setStringParam(mMicName + "_" + SystemUtil.getDeviceBrand());
        startBean.setUrl("");
        return startBean;
    }

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    public static String getSaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return null;
        }
    }

    public static void setViewHeight(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (null == layoutParams)
            return;
        layoutParams.width = width;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    public static void saveBitmap(Bitmap bitmap) throws FileNotFoundException {
        if (bitmap != null) {
            File file = new File(getPath(), "GSY-" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream;
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            bitmap.recycle();
        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        //显示软键盘
//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //如果上面的代码没有弹出软键盘 可以使用下面另一种方式
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    public static String getPath() {
        String path = getAppPath(NAME);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    private static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String NAME = "GSYVideo";

    public static String getAppPath(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(SD_PATH);
        sb.append(File.separator);
        sb.append(name);
        sb.append(File.separator);
        return sb.toString();
    }


    public static ArrayList<String> getNameList(List<UserDBBean> list) {
        if (null != list) {
            ArrayList<String> nameList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                String username = list.get(i).getUsername();
                nameList.add(username);
            }
            return nameList;
        }
        return new ArrayList<>();
    }

    /**
     * 打卡软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void openKeybord(final EditText mEditText, final Context mContext) {

        //必须要等UI绘制完成之后，打开软键盘的代码才能生效，所以要设置一个延时
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext
                        .getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 500);
    }

    /**
     * 获取16进制的
     * * Send_Type(Received_Type)	发送(接收方)方设备类型
     * * 设备类型：对应关系
     * * 00-工作站， 01-HD3摄像机，02-冷光源，03-气腹机，04-冲洗机，
     * * 05-4K摄像机，06-耳鼻喉控制板，07-(智能一体机)一代一体机，8-耳鼻喉治疗台，
     * * 9-妇科治疗台，10-泌尿治疗台，0B-手术一体机， A2-RC200，A0-iOS，A1-Android，
     * * FF-所有设备(神州转播)
     * * 更多设备类型依次类推，平台最大可连接255种受控设备
     * <p>
     * 我们手动吧 8  9  改成了 08 09 所以需要在这里替换下
     * <p>
     * string从随机之开始到校验和处结束的String
     *
     * @return 返回的是16进制的设备类型
     */
    public static String getDeviceTypeHexNum(Context context, String typeDesc) {
        //字符串--48位--50位表示的是设备类型
        String result = null;
//            String str = string.substring(48, 50);

        if (context.getResources().getString(R.string.device_Work_Station).equals(typeDesc)) {
            result = Constants.Type_HexString_00;//00  双重判断
        } else if (context.getResources().getString(R.string.device_type_HD3).equals(typeDesc)) {//HD3摄像机
            result = Constants.Type_HexString_01;//1 双重判断
        } else if (context.getResources().getString(R.string.device_type_HD3_4K).equals(typeDesc)) {
            result = Constants.Type_HexString_05;
        } else if (context.getResources().getString(R.string.device_V1_YiTiJi).equals(typeDesc)) {
            result = Constants.Type_HexString_07;
        } else if (context.getResources().getString(R.string.device_EarNoseTable).equals(typeDesc)) {
            result = Constants.Type_HexString_08;//08  双重判断
        } else if (context.getResources().getString(R.string.device_FuKeTable).equals(typeDesc)) {
            result = Constants.Type_HexString_09;//09  双重判断
        } else if (context.getResources().getString(R.string.device_MiNiaoTable).equals(typeDesc)) {
            result = Constants.Type_HexString_0A;
        } else if (context.getResources().getString(R.string.device_Custom_Url).equals(typeDesc)) {
            result = Constants.Type_HexString_FF;
        } else if (context.getResources().getString(R.string.device_type_RC200).equals(typeDesc)) {
            result = Constants.Type_HexString_A2;
        }

        return result;


    }


    /**
     * 获取16进制的
     * * Send_Type(Received_Type)	发送(接收方)方设备类型
     * * 设备类型：对应关系
     * * 00-工作站， 01-HD3摄像机，02-冷光源，03-气腹机，04-冲洗机，
     * * 05-4K摄像机，06-耳鼻喉控制板，07-(智能一体机)一代一体机，8-耳鼻喉治疗台，
     * * 9-妇科治疗台，10-泌尿治疗台，0B-手术一体机， A2-RC200，A0-iOS，A1-Android，
     * * FF-所有设备(神州转播)
     * * 更多设备类型依次类推，平台最大可连接255种受控设备
     * <p>
     * 我们手动吧 8  9  改成了 08 09 所以需要在这里替换下
     * <p>
     * string从随机之开始到校验和处结束的String
     *
     * @return 返回的是 10 进制的设备类型
     */
    public static String getDeviceTypeDecNum(Context context, String typeDesc) {
        switch (typeDesc) {
            case Constants.Type_Work_Station: //工作站
                return Constants.Type_DecString_00;
            case Constants.Type_HD3: //HD3摄像机
                return Constants.Type_DecString_01;
            case Constants.Type_HD3_4K: //4K摄像机(HD3-4K)
                return Constants.Type_DecString_05;
            case Constants.Type_V1_YiTiJi: //(智能一体机)一代一体机
                return Constants.Type_DecString_07;
            case Constants.Type_EarNoseTable: //耳鼻喉治疗台
                return Constants.Type_DecString_08;
            case Constants.Type_FuKeTable: //妇科治疗台
                return Constants.Type_DecString_09;
            case Constants.Type_MiNiaoTable: //泌尿治疗台
                return Constants.Type_DecString_0A;
            case Constants.Type_Custom_Url: //神州转播
                return Constants.Type_DecString_FF;
            case Constants.Type_Operation_YiTiJi: //手术一体机
                return Constants.Type_DecString_0B;
            case Constants.Type_RC200: //RC200
                return Constants.Type_DecString_A2;

        }
        return "";


    }


    /**
     * 根据deviceTypeDesc 获取deviceTypeNum
     */
    public static String getDeviceTypeNum(String deviceTypeDesc) {
        switch (deviceTypeDesc) {
            case Constants.Type_Work_Station: //工作站
                return "00";
            case Constants.Type_HD3: //HD3摄像机
                return "01";
            case Constants.Type_HD3_4K: //4K摄像机(HD3-4K)
                return "05";
            case Constants.Type_V1_YiTiJi: //一代一体机
                return "07";
            case Constants.Type_EarNoseTable: //耳鼻喉治疗台
                return "08";
            case Constants.Type_FuKeTable: //妇科治疗台
                return "09";
            case Constants.Type_MiNiaoTable: //泌尿治疗台
                return "10";
            case Constants.Type_Custom_Url: //神州转播
                return "FF";
            case Constants.Type_Operation_YiTiJi: //手术一体机
                return "0B";
            case Constants.Type_RC200: //RC200
                return "A2";

        }
        return "";

    }

    /**
     * 根据deviceTypeNum 获取deviceTypeDesc
     */
    public static String getDeviceTypeDesc(String deviceTypeNum) {
        switch (deviceTypeNum) {
            case "00": //工作站
            case "0": //工作站
                return Constants.Type_Work_Station;
            case "01": //HD3摄像机
                return Constants.Type_HD3;
            case "05": //4K摄像机(HD3-4K)
                return Constants.Type_HD3_4K;
            case "07": //(智能一体机)一代一体机
                return Constants.Type_V1_YiTiJi;
            case "08": //耳鼻喉治疗台
            case "8": //耳鼻喉治疗台
                return Constants.Type_EarNoseTable;
            case "9": //妇科治疗台
            case "09": //妇科治疗台
                return Constants.Type_FuKeTable;
            case "10": //泌尿治疗台
                return Constants.Type_MiNiaoTable;
            case "FF": //神州转播
                return Constants.Type_Custom_Url;
            case "0B": //手术一体机
                return Constants.Type_Operation_YiTiJi;
            case "A2": //RC200
                return Constants.Type_RC200;

        }
        return "";

    }
//    public static String getDeviceTypeNum(String typeDesc) {
//        //字符串--48位--50位表示的是设备类型
//        String result = null;
////            String str = string.substring(48, 50);
//
//        if (Constants.Type_Work_Station.equals(typeDesc)) {
//            result = "0";//00  双重判断
//        } else if (Constants.Type_HD3.equals(typeDesc)) {//HD3摄像机
//            result = "01";//1 双重判断
//        } else if ("HD3-4K".equals(typeDesc)) {
//            result = "05";
//        } else if (Constants.Type_V1_YiTiJi.equals(typeDesc)) {
//            result = "07";
//        } else if (Constants.Type_EarNoseTable.equals(typeDesc)) {
//            result = "8";//08  双重判断
//        } else if (Constants.Type_FuKeTable.equals(typeDesc)) {
//            result = "9";//09  双重判断
//        } else if (Constants.Type_MiNiaoTable.equals(typeDesc)) {
//            result = "10";
//        } else if (Constants.Type_Custom_Url.equals(typeDesc)) {
//            result = "FF";
//        }
//
//        return result;
//
//
//    }

    public static boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }


    /**
     * 判断字符串是否为URL
     *
     * @param urls 需要判断的String类型url
     * @return true:是URL；false:不是URL
     */
    public static boolean isHttpUrl(String urls) {
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式

        Pattern pat = Pattern.compile(regex.trim());//对比
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();//判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }


    /**
     * 利用正则表达式判断字符是否为IP
     *
     * @param ipString
     * @return true表示正确
     */
    public static boolean isCorrectIp2(String ipString) {
        String ipRegex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";    //IP地址的正则表达式
        //如果前三项判断都满足，就判断每段数字是否都位于0-255之间
        if (ipString.matches(ipRegex)) {
            String[] ipArray = ipString.split("\\.");
            for (int i = 0; i < ipArray.length; i++) {
                int number = Integer.parseInt(ipArray[i]);
                //4.判断每段数字是否都在0-255之间
                if (number < 0 || number > 255) {
                    return false;
                }
            }
            return true;
        } else {
            return false;    //如果与正则表达式不匹配，则返回false
        }
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(android.content.Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }






}

