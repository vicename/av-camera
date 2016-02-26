package com.linj.imageloader;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * Created by Li Dachang on 16/2/24.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class CommonUtil {

    /**
     * Convert Dp to Pixel
     */
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }


    /**
     * 获取屏幕宽度
     *
     * @param context 上下文
     * @param isWidth 是否宽度,默认高度
     * @return 尺寸
     */
    public static int getScreenSize(Context context, boolean isWidth) {
        WindowManager windowManager = ((Activity) context).getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int length;
        if (isWidth) {
            length = displayMetrics.widthPixels;
        } else {
            length = displayMetrics.heightPixels;
        }
        return length;
    }


}
