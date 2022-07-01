package com.application.adimviandroid.utils;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedUtil.setInstance(this);
    }
}
