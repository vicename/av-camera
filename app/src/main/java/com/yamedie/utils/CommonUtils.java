package com.yamedie.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.View;

/**
 * Created by Li Dachang on 16/1/25.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class CommonUtils {
    /**
     * 检测相机是否存在
     * @param context 上下文
     * @return 是否存在
     */
    public static boolean checkCameraHardWare(Context context){
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }
        return false;
    }



    public static Bitmap pathToBitmap(Uri uri){
        Bitmap bm = BitmapFactory.decodeFile(uri.toString());
        return bm;
    }

    /**
     * 让按钮一段时间内不可点击
     * @param view view控件
     * @param time 不可点击的时间
     */
    public static void disableViewForSeconds(final View view,int time) {
        view.setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setClickable(true);
            }
        }, time);
    }
}
