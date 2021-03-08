package com.founq.testbitmapcache;

import android.app.Application;

/**
 * Created by ring on 2021/3/8.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ImageCache.getInstance().init(this, getCacheDir().getAbsolutePath());
    }
}
