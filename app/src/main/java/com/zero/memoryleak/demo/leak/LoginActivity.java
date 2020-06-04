package com.zero.memoryleak.demo.leak;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.zero.memoryleak.demo.R;

/**
 * 单例类持有Activity Context 引用导致的内存泄漏
 *
 * @author cqf
 */
public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        /**
         * 将Activity context 传递给单例类
         **/
        SingletonManager.getInstance(this);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }
}