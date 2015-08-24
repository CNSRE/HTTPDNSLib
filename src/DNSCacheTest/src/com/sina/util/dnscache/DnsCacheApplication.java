package com.sina.util.dnscache;

import android.app.Application;
import android.content.Context;

import com.sina.util.dnscache.tasksetting.SpfConfig;

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
