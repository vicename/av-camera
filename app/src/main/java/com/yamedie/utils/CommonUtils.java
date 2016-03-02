package com.yamedie.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.yamedie.common.CommonDefine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Li Dachang on 16/1/25.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class CommonUtils {
    /**
     * 检测相机是否存在
     *
     * @param context 上下文
     * @return 是否存在
     */
    public static boolean checkCameraHardWare(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }


    public static Bitmap pathToBitmap(Uri uri) {
        Bitmap bm = BitmapFactory.decodeFile(uri.toString());
        return bm;
    }

    /**
     * 判断是否为主线程
     *
     * @return 是否
     */
    public static boolean isInMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 友盟用户行为统计
     *
     * @param context 上下文
     * @param tag     标识
     */
    public static void mobClickAgentGo(Context context, String tag) {
//        if (!CommonDefine.IS_DEBUG)
        MobclickAgent.onEvent(context, tag);
    }

    /**
     * 友盟用户行为统计(包含Map)
     *
     * @param context 上下文
     * @param tag     标识
     * @param map     具体行为
     */
    public static void mobClickAgentGo(Context context, String tag, Map<String, String> map) {
//        if (!CommonDefine.IS_DEBUG)
        MobclickAgent.onEvent(context, tag, map);
    }

    /**
     * 获取当前时间
     *
     * @param hasHours 是否精确到小时 分 秒
     * @return 格式化好的时间
     */
    public static String getTime(boolean hasHours) {
        Date date = new Date();
        long time = date.getTime();
        if (hasHours) {
            return formatTime(time, true);
        } else {
            return formatTime(time, false);
        }
    }

    /**
     * 格式化时间
     *
     * @param time     时间戳
     * @param hasHours 是否精确到小时和秒
     * @return 时间
     */
    public static String formatTime(long time, boolean hasHours) {
        SimpleDateFormat dateFormat;
        if (hasHours) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        return dateFormat.format(new Date(time));
    }


    /**
     * 将带中文的URL转换成UTF-8编码
     *
     * @param url url
     * @return 编码后的url
     */
    public static String encodeUrl(String url) {
        return Uri.encode(url, "-![.:/,%?&=]");
    }

    /**
     * 让按钮一段时间内不可点击
     *
     * @param view view控件
     * @param time 不可点击的时间
     */
    public static void disableViewForSeconds(final View view, int time) {
        view.setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setClickable(true);
            }
        }, time);
    }

    /**
     * 检查耗时
     *
     * @param time 毫秒数
     * @return 时间差
     */
    public static int timeSpendCheck(long time) {
        if (time > 100000) {
            Date date = new Date();
            return (int) (date.getTime() - time);
        } else {
            return 0;
        }
    }

    public static long timeSpendCheck() {
        return new Date().getTime();
    }
}
