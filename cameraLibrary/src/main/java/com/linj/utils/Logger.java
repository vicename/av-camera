package com.linj.utils;

import android.util.Log;

/**
 * Created by Li Dachang on 16/2/24.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class Logger {
    private static boolean IS_DEBUG=true;
    private static final String DEFAULT_TAG = "ssss---";
    private static final String DEFAULT_TAG_2 = "xxxx---";
    private static final String DC_TAG_1 = "---";
    private static final String DC_TAG_2 = "-+-+- ";

    public static void i(String msg) {
        i(DC_TAG_1, msg);
    }

    public static void i(Object msg) {
        i(DC_TAG_1, String.valueOf(msg));
    }

    public static void i(String flag, String msg) {
        if (IS_DEBUG) {
            Log.i(flag, msg);
        }
    }

    public static void i(String flag, String msgKey, Object msgValue) {
        i(flag, flag + " " + msgKey + " : " + String.valueOf(msgValue) + " --;");
    }

    public static void i(String flag, Object msg) {
        i(flag, String.valueOf(msg));
    }

    public static void i(int flag, Object msg) {
        if (flag == 1) {
            i(DC_TAG_1,String.valueOf(msg));
        }
    }

    public static void i(int tag, String msgKey, Object msgValue) {
        if (tag == 1) {
            i(DC_TAG_1, DC_TAG_1+ " " + msgKey + " : " + String.valueOf(msgValue) + " --");
        }
    }

    public static void e(int tag, Object msg) {
        if (IS_DEBUG) {
            Log.e("---", String.valueOf(msg));
        }
    }
}
