package com.founq.testbitmapcache.rxjava;

/**
 * Created by ring on 2021/3/15.
 * 抽象被观察者
 */
public interface Observable {
    void add(Observer observer);
    void remove(Observer observer);

    //通知观察者，消息已经发出了
    void notifyObservers();
}
