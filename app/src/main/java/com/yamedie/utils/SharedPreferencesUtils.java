package com.yamedie.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Li Dachang on 16/1/27.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class SharedPreferencesUtils {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String file = "itCast";

    public SharedPreferencesUtils(Context context) {
        super();
        pref = context.getSharedPreferences(file, context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public SharedPreferencesUtils(Context context, String file) {
        super();
        this.file = file;
        pref = context.getSharedPreferences(file, context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * 保存参数,自动识别存储类型
     *
     * @param key
     * @param value
     */
    public void saveIn(String key, Object value) {
        String type = value.getClass().getSimpleName();
        if ("String".equals(type)) {
            editor.putString(key, (String) value);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) value);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) value);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) value);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) value);
        }
        editor.apply();
    }

    /**
     * 获取各项配置参数
     * 需要传入默认值,
     *
     * @return pref.getxxxx(key, defValue)
     */
    public String getPrefString(String key, String defValue) {
        return pref.getString(key, defValue);
    }

    public Boolean getPrefBoolean(String key, Boolean defValue) {
        return pref.getBoolean(key, defValue);
    }

    public Integer getPrefInt(String key, Integer defValue) {
        return pref.getInt(key, defValue);
    }

    public Float getPrefFloat(String key, Float defValue) {
        return pref.getFloat(key, defValue);
    }

    public Long getPrefLong(String key, Long defValue) {
        return pref.getLong(key, defValue);
    }

}