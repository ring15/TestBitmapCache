package com.founq.testbitmapcache.rxjava;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ring on 2021/3/15.
 * 具体的被观察者
 */
public class WechatServer implements Observable {

    //观察者清单
    private List<Observer> mList;
    //发送给用户的消息
    private String message;

    public WechatServer() {
        mList = new ArrayList<>();
    }

    public void pushMessage(String msg) {
        this.message = msg;
        Log.i("test-微信服务号更新了消息", msg);
        //通知所有关注了本服务号的服务
        notifyObservers();
    }

    @Override
    public void add(Observer observer) {
        mList.add(observer);
    }

    @Override
    public void remove(Observer observer) {
        mList.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer: mList) {
            observer.update(message);
        }
    }
}
