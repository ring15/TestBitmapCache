package com.founq.testbitmapcache.inject;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ring on 2021/3/9.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@InjectEvent(listenerSetter = "setOnClickListener", listenerType = View.OnClickListener.class,
        callbackMethod = "onClick")
public @interface OnClick {
    int[] value();
}
