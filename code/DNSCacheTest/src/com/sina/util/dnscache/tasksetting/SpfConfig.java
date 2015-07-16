package com.sina.util.dnscache.tasksetting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SpfConfig {

    private static SpfConfig mInstance;
    private static SharedPreferences sharedPreferences;
    private static Object lock = new Object();
    private SpfConfig() {
        if (null == sharedPreferences) {
            throw new RuntimeException("SpfConfig 初始化失败!!!");
        }
    }

    public static SpfConfig getInstance() {
        if (null == mInstance) {
            synchronized (lock) {
                if (null == mInstance) {
                    mInstance = new SpfConfig();
                }
            }
        }
        return mInstance;
    }

    public void putString(String key, String value) {
        Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public void putInt(String key, int value) {
        Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public void putLong(String key, long value) {
        Editor edit = sharedPreferences.edit();
        edit.putLong(key, value);
        edit.commit();
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public static void init(Context ctx) {
        sharedPreferences = ctx.getSharedPreferences("sp_dnscache", Context.MODE_PRIVATE);
    }
}
