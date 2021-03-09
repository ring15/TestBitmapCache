package com.founq.testbitmapcache.inject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by ring on 2021/3/9.
 */
public class ListenerHandler implements InvocationHandler {

    private Object obj;//MainActivity
    private Method mMethod;//onClick真正需要执行的方法

    public ListenerHandler(Object obj, Method method) {
        this.obj = obj;
        mMethod = method;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return mMethod.invoke(obj, args);
    }
}
