package com.founq.testbitmapcache.rxjava;

/**
 * Created by ring on 2021/3/15.
 * 抽象观察者
 */
public interface Observer {
    //当被观察者发出消息的时候，这个方法接收消息
    void update(Object msg);
}
