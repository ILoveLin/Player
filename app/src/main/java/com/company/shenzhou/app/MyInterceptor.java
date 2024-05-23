package com.company.shenzhou.app;

import android.content.Context;

import com.company.shenzhou.global.Constants;
import com.company.shenzhou.utlis.LogUtils;
import com.tencent.mmkv.MMKV;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lovelin on 2019/5/10
 * <p>
 * Describe:拦截器  添加header
 */
public class MyInterceptor implements Interceptor {
    private Context mContext;

    public MyInterceptor(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
//        String userid = (String) SharePreferenceUtil.get(mContext, "USERID", "");
//        Log.e("跳转播放界面", "login==GetUserID====调试头====" + userid + "");
        Request request = chain.request().newBuilder()
//                .addHeader("Content-Type","application/json")
//                .addHeader("Authorization", "Basic YWRtaW46ZTEwYWRjMzk0OWJhNTlhYmJlNTZlMDU3ZjIwZjg4M2U=")
//                .addHeader("token", token)
//                .addHeader("userid", userid)

                .build();

        Response response = chain.proceed(request);
        Headers headers = response.headers();
        for (int i = 0; i < headers.size(); i++) {
//            LogUtils.e("跳转播放界面" + "拦截器=:" + headers.name(i));
//            LogUtils.e("跳转播放界面" + "拦截器=:" + headers.get(headers.name(i)));

            if ("X-session".equals(headers.name(i))) {
                MMKV mmkv = MMKV.defaultMMKV();
                LogUtils.e("跳转播放界面" + "拦截器=:" + headers.name(i));
                LogUtils.e("跳转播放界面" + "拦截器=:" + headers.get(headers.name(i)));
                mmkv.encode(Constants.KEY_RC200_Session, headers.get(headers.name(i)) + "");
                break;
            }


        }
        return response;
//        return chain.proceed(request);

    }
}
























