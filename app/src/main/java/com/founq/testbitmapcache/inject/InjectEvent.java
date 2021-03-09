package com.founq.testbitmapcache.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ring on 2021/3/9.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectEvent {

    //事件三要素
    //1. setOnClickListener
    //2. View.OnClickListener()
    //3. onClick(View v)
    String listenerSetter();
    Class listenerType();
    String callbackMethod();
}
