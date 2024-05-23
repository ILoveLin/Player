package com.company.shenzhou.utlis;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/5/23 17:04
 * desc：
 */
public class SharePreferenceUtil {
    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "share_data";
    public static final String UID = "uid";

    public static final String Bugly_CanUse = "Bugly_CanUse";
    public static final String Tencent_CanWeb = "Tencent_Web";
    public static final String Permission_Shot = "Permission_Shot";
    public static final String Permission_Record = "Permission_Record";

    //这个是fragment.tab的保存信息
    public static final String DYNAMIC_SWITCH_TAB = "dynamic.switching.tab";

    //用户登入的时候存入
    public static final String Current_UserType = "2";  //0普通  1权限  2超级用户
    public static final String Current_Username = "admin";     // 虽然是不同字段表示，但是"admin" 如果同时存储username和password  后者会覆盖前者
    public static final String Current_Password = "123456000";    //
    public static final String Current_ToastShow = "ToastShow";    //解决第一次登入，用户列表显示adapter中toast，bug
    public static final String Current_ID = "1";
    public static final String Current_RememberPassword = "false";    //默认不记住密码

//    public static final String Type_HD3 = "HD3";                 //0代表,HD3
//    public static final String Type_HD3_4K = "HD3-4K";           //1代表,HD3
//    public static final String Type_Yitiji = "一代一体机";        //2代表,一体机
//    public static final String Type_ErBiHou = "耳鼻喉治疗台";     //3代表,耳鼻喉治疗台
//    public static final String Type_FuKe = "妇科治疗台";          //4代表,妇科治疗台
//    public static final String Type_MiNiao = "泌尿治疗台";        //5代表,泌尿治疗台
//    public static final String Type_WorkStation = "工作站";       //6代表,工作站
//    public static final String Type_Zhuanbo = "神州转播";         //7代表,神州转播

    //("HD3", "HD3-4K", "一代一体机","耳鼻喉治疗台","妇科治疗台","泌尿治疗台","工作站","神州转播")
    //  0         1        2               3           4           5           6       7   ,此处是设备类型选择的position

    public static final String Current_Admin_ChangePassword = "change";  //0普通  1权限  2超级用户

    /**
     * HD3   ==rtsp://username:password@ip/MediaInput/h264/stream_1 ------     --HD3，高清
     * HD3   ==rtsp://username:password@ip/MediaInput/h264/stream_2 ------     --HD3，标清
     * 一体机==rtsp://username:password@ip：port/session0.mpg ------            --一体机，标清
     * 一体机==rtsp://username:password@ip：port/session1.mpg ------            --一体机，高清
     * url链接地址=用户输入的url链接eg:http://www.cme8848.com/live/cme.m3u8
     * url链接地址=用户输入的url链接eg:http://www.cme8848.com/live/flv
     */

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context, String key, Object object) {

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

}
