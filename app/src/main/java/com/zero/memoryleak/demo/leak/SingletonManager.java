package com.zero.memoryleak.demo.leak;

import android.content.Context;


public class SingletonManager {

    private static SingletonManager singleton;
    private Context context;

    private SingletonManager(Context context) {
        this.context = context;
    }

    public synchronized static SingletonManager getInstance(Context context) {
        if (singleton == null) {
            /**
             * 持有Activity context引用. Leak!!!
             * 该Activity对象存会保存在堆内存中，直到应用程序结束，内存泄漏的时间很长 要特别注意这样的泄漏
             * **/
            singleton = new SingletonManager(context);
        }
        return singleton;
    }



}
