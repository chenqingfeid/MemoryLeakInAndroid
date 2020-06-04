package com.zero.memoryleak.demo.fixed;

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
             * 解决方法就是替换成 Application context
             * **/
            singleton = new SingletonManager(context.getApplicationContext());
        }
        return singleton;
    }
}
