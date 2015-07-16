package com.sina.util.dnscache;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppConfigUtil {

    private static Context mContext;

    public static void init(Context ctx) {
        mContext = ctx;
    }
    
    public static Context getApplicationContext(){
        return mContext.getApplicationContext();
    }

    /**
     * 获取缓存文件夹
     * @return
     */
    public static File getExternalCacheDir() {
        return mContext.getExternalCacheDir();
    }

    /**
     * 返回当前程序版本名 
     * @return
     */
    public static String getAppVersionName() {
        String versionName = "";
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
