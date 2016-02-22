package com.yamedie.utils;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import com.yamedie.common.CommonDefine;

/**
 * Created by Li Dachang on 16/1/25.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class Logger {
    private static final String DEFAULT_FLAG = "sssss";
    private static final String DEFAULT_FLAG_2 = "xxxx";
    private static final String DC_FLAG_1 = "---";
    private static final String DC_FALG_2 = "-+-+- ";

    public static void i(String msg) {
        i(DEFAULT_FLAG, msg);
    }

    public static void i(Object msg) {
        i(DEFAULT_FLAG, String.valueOf(msg));
    }

    public static void i(String flag, String msg) {
        if (CommonDefine.is_debug) {
            Log.i(flag, msg);
        }
    }

    public static void i(String flag, String msgKey, Object msgValue) {
        i(flag, flag + " " + msgKey + " : " + String.valueOf(msgValue) + " --;");
    }

    public static void i(String flag, Object msg) {
        i(flag, String.valueOf(msg));
    }


    /**
     * 打印URL的Log
     *
     * @param msg       相关信息
     * @param serverURL 请求地址
     * @param params    请求队列
     */
    public static void url(String msg, String serverURL, RequestParams params) {
        String url = AsyncHttpClient.getUrlWithQueryString(true, serverURL, params);
        i("--URL--", msg + "--" + url);//打印
    }

}
