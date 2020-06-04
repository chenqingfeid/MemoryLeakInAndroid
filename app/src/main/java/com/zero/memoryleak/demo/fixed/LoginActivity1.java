package com.zero.memoryleak.demo.fixed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.zero.memoryleak.demo.R;

/**
 * 单例导致的内存泄漏解决方法
 *
 * @author cqf
 */
public class LoginActivity1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        /**
         * 使用application 上下文 非Activity Context
         * **/
        SingletonManager.getInstance(this);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity1.class);
        context.startActivity(starter);
    }
}