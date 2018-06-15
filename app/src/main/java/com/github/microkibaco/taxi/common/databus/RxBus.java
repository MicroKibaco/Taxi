package com.github.microkibaco.taxi.common.databus;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxBus {

    private static final String TAG = RxBus.class.getSimpleName();
    private static volatile RxBus instance;

    // 订阅者集合
    private Set<Object> subscribers;

    /**
     * 注册 DataBusSubscriber
     */
    public synchronized void register(Object subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * 注销 DataBusSubscriber
     */
    public synchronized void unRegister(Object subscriber) {
        subscribers.remove(subscriber);
    }


    /**
     * 单例模式
     */
    private RxBus() {
        subscribers = new CopyOnWriteArraySet<>();
    }

    public static synchronized RxBus getInstance() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {

                    instance = new RxBus();

                }
            }
        }
        return instance;
    }


    /**
     * 处理包装过程
     */

    public void chainProcess(Func1 func) {

        Observable.just("")
                .subscribeOn(Schedulers.io()) // 指定处理过程在 IO 线程
                .map(func)   // 包装处理过程
                .observeOn(AndroidSchedulers.mainThread())  // 指定事件消费在 Main 线程
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object data) {
                        if (data == null) {
                            return;
                        }
                        send(data);
                    }
                });
    }


    /**
     * 发送数据
     */
    private void send(Object data) {

        for (Object subscriber : subscribers) {
            // 扫描注解，将数据发送到注册的对象的标记方法
            callMethodByAnnotiation(subscriber, data);
        }

    }

    @SuppressWarnings("Since15")
    private void callMethodByAnnotiation(Object target, Object data) {
        Method[] methodArray = target.getClass().getDeclaredMethods();

        for (Method aMethodArray : methodArray) {
            try {
                if (aMethodArray.isAnnotationPresent(RegisterBus.class)) {
                    // 被 @RegisterBus 修饰的方法
                    Class<?> paramType = aMethodArray.getParameterTypes()[0];
                    if (data.getClass().getName().equals(paramType.getName())) {
                        // 参数类型和 data 一样，调用此方法
                        aMethodArray.invoke(target, data);
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }
}
