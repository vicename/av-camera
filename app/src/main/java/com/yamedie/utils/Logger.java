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
    private static final String DEFAULT_FLAG = "ssss---";
    private static final String DEFAULT_FLAG_2 = "xxxx---";
    private static final String DC_TAG_1 = "---";
    private static final String DC_TAG_2 = "-+-+- ";

    public static void i(String msg) {
        i(DC_TAG_1, msg);
    }

    public static void i(Object msg) {
        i(DC_TAG_1, String.valueOf(msg));
    }

    public static void i(String flag, String msg) {
        if (CommonDefine.IS_DEBUG) {
            Log.i(flag, msg);
        }
    }

    public static void i(int i) {
        i("---", "-----------" + i);
    }

    public static void i(String flag, String msgKey, Object msgValue) {
        i(flag, flag + " " + msgKey + " : " + String.valueOf(msgValue) + " --;");
    }

    public static void i(String flag, Object msg) {
        i(flag, String.valueOf(msg));
    }

    public static void i(int flag, Object msg) {
        if (flag == 1) {
            i(DC_TAG_1, String.valueOf(msg));
        }
    }

    public static void i(int tag, String msgKey, Object msgValue) {
        if (tag == 1) {
            i(DC_TAG_1, DC_TAG_1 + " " + msgKey + " : " + String.valueOf(msgValue) + " --");
        }
    }

    public static void i(int tag, String key1, Object value1, String key2, Object value2) {
        if (tag == 1) {
            i(DC_TAG_1, key1 + ":" + String.valueOf(value1) + " -- " + key2 +":"+ String.valueOf(value2));
        }
    }

    public static void i(int tag, String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        if (tag == 1) {
            i(DC_TAG_1, key1 + ":" + String.valueOf(value1) + " -- " + key2 + ":" + String.valueOf(value2) + " -- " + key3 + ":" + String.valueOf(value3));
        }
    }


    public static void w(Object msg) {
        if (CommonDefine.IS_DEBUG) {
            Log.w("---", String.valueOf(msg));
        }
    }

    public static void e(Object msg) {
        if (CommonDefine.IS_DEBUG) {
            Log.e("---", String.valueOf(msg));
        }
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
