package com.hewuzhao.cachecontrast;

import android.app.Application;

/**
 * @author hewuzhao
 * @date 2020/6/6
 */
public class MyApplication extends Application {

    public static MyApplication sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }
}
