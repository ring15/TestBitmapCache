package com.founq.testbitmapcache.inject;

import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by ring on 2021/3/8.
 */
public class InjectManager {

    public static void inject(Object obj) {
        //布局注入
        injectLayout(obj);
        //控件注入
        injectViews(obj);
        //事件注入
        injectEvent(obj);
    }

    /**
     * 事件注入
     *
     * @param obj
     */
    private static void injectEvent(Object obj) {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            //事件注入，不只是点击事件，还有长按事件等
//            OnClick onClick = method.getAnnotation(OnClick.class);
            Annotation[] annotations = method.getAnnotations();//所以，使用元注解【注解的注解】
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();//去拿注解类的class对象
                InjectEvent injectEvent = annotationType.getAnnotation(InjectEvent.class);
                if (null != injectEvent) {
                    //表示，当前annotation是自定义注解

                    //拿到事件三要素
                    //1. setOnClickListener
                    String listenerSetter = injectEvent.listenerSetter();
                    //2. View.OnClickListener()
                    Class listenerType = injectEvent.listenerType();
                    //3. onClick(View v)
                    String callbackMethod = injectEvent.callbackMethod();

                    //new View.OnClickListener(),动态代理处理，代理的方法都是一个，所以，不用再循环中
                    ListenerHandler listenerHandler = new ListenerHandler(obj, method);//用method动态替换
                    Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, listenerHandler);

                    try {
                        Method valueMethod = annotationType.getDeclaredMethod("value");
                        int[] ids = (int[]) valueMethod.invoke(annotation);
                        for (int id : ids) {
                            //mButton = findViewById(R.id.btn);
                            Method findViewById = clazz.getMethod("findViewById", int.class);//需要通过findViewById获取按钮等，因为，不能确保其他地方有调用过findViewById
                            View view = (View) findViewById.invoke(obj, id);//静态方法，obj可以为null，非静态方法，obj是它的实例方法

                            //mButton.setOnClickListener
                            Method setterMethod = view.getClass().getMethod(listenerSetter, listenerType);

//                            setterMethod.invoke(view, listenerType.newInstance());//listenerType.newInstance()可以new一个OnClickListener对象，但是，没有处理onClick方法，所以，只能使用动态代理
                            setterMethod.invoke(view, listener);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 控件注入
     *
     * @param obj
     */
    private static void injectViews(Object obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            InjectView injectView = field.getAnnotation(InjectView.class);
            if (null != injectView) {
                try {
                    Method findViewById = clazz.getMethod("findViewById", int.class);
                    int value = injectView.value();
                    View view = (View) findViewById.invoke(obj, value);//静态方法，obj可以为null，非静态方法，obj是它的实例方法
                    field.setAccessible(true);
                    field.set(obj, view);//btn = view;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 布局注入
     *
     * @param obj
     */
    private static void injectLayout(Object obj) {
        Class<?> clazz = obj.getClass();
        InjectLayout injectLayout = clazz.getAnnotation(InjectLayout.class);

        if (null != injectLayout) {
            //setContentView(R.layout.activity_dependency_injection);
            try {
                Method setContentView = clazz.getMethod("setContentView", int.class);
                int value = injectLayout.value();
                setContentView.invoke(obj, value);//静态方法，obj可以为null，非静态方法，obj是它的实例方法
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
