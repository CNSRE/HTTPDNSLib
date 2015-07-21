package com.sina.util.dnscache;

import com.sina.util.dnscache.tasksetting.SpfConfig;

import android.app.Application;
import android.content.Context;

public class DnsCacheApplication extends Application{
    public static Context mGlobalInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mGlobalInstance = this;
        SpfConfig.init(this.getApplicationContext());
        DNSCache.Init(this);
    }
}
