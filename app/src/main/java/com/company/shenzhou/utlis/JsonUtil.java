package com.company.shenzhou.utlis;

import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/5/29 15:33
 * desc：
 */
public class JsonUtil {
    /**
     * ZXing扫码，确定是线路几的数据bean
     * 如果异常，默认使用线路1的bean
     *
     * @param response
     * @return
     */

    public static String parseJson2CheckLine(String response) {
        try {
            JSONObject object = new JSONObject(response);
            //判断线路几，然后使用不同的json数据bean解析数据
            String ln = object.getString("ln");
            //扫码出来的数据0 1 2 ，接口的也是0 1 2；对应本地数据库，线路1，线路2，线路3
            return ln;
        } catch (JSONException e) {
            e.printStackTrace();

        }
        //默认：线路3
        return "2";

    }


    /**
     * 常规的http请求判断code
     *
     * @param response
     * @return
     */
    public static boolean parseJson2CheckCode(String response) {
        if (isGoodJson(response)) {
            try {
                JSONObject object = new JSONObject(response);
                String code = object.getString("code");
                if ("0".equals(code)) {
                    return true;

                } else {
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;

        } else {
            return false;
        }


    }


    /**
     * 校验是否是正常的json数据
     *
     * @param json
     * @return
     */
    public static boolean isGoodJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return false;
        }
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        } catch (JsonParseException e) {
            return false;
        }
    }
}
