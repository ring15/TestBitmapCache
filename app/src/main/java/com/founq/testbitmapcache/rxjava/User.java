package com.founq.testbitmapcache.rxjava;

import android.util.Log;

/**
 * Created by ring on 2021/3/15.
 * 具体观察者
 */
public class User implements Observer {

    private String name;
    private String message;

    public User(String name) {
        this.name = name;
    }

    @Override
    public void update(Object msg) {
        this.message = (String) msg;
        read();
    }

    private void read() {
        Log.i("test", name + "收到了消息：" + message);
    }
}
