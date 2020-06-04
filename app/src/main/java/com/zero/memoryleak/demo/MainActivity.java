package com.zero.memoryleak.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import com.zero.memoryleak.demo.fixed.AsyncTaskActivity1;
import com.zero.memoryleak.demo.fixed.HandlerActivity1;
import com.zero.memoryleak.demo.fixed.LoginActivity1;
import com.zero.memoryleak.demo.fixed.StaticAsyncTaskActivity1;
import com.zero.memoryleak.demo.fixed.ThreadActivity1;
import com.zero.memoryleak.demo.leak.AsyncTaskActivity;
import com.zero.memoryleak.demo.leak.HandlerActivity;
import com.zero.memoryleak.demo.leak.LoginActivity;
import com.zero.memoryleak.demo.leak.StaticAsyncTaskActivity;
import com.zero.memoryleak.demo.leak.ThreadActivity;


public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.asyncTask).setOnClickListener(this);
        findViewById(R.id.staticAsyncTask).setOnClickListener(this);
        findViewById(R.id.thread).setOnClickListener(this);
        findViewById(R.id.handler).setOnClickListener(this);
        findViewById(R.id.singleton).setOnClickListener(this);

        findViewById(R.id.asyncTaskFixed).setOnClickListener(this);
        findViewById(R.id.staticAsyncTaskFixed).setOnClickListener(this);
        findViewById(R.id.threadFixed).setOnClickListener(this);
        findViewById(R.id.handlerFixed).setOnClickListener(this);
        findViewById(R.id.singletonFixed).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.asyncTask:
                AsyncTaskActivity.start(this);
                break;
            case R.id.staticAsyncTask:
                StaticAsyncTaskActivity.start(this);
                break;
            case R.id.thread:
                ThreadActivity.start(this);
                break;
            case R.id.handler:
                HandlerActivity.start(this);
                break;
            case R.id.singleton:
                LoginActivity.start(this);
                break;

            case R.id.asyncTaskFixed:
                AsyncTaskActivity1.start(this);
                break;
            case R.id.staticAsyncTaskFixed:
                StaticAsyncTaskActivity1.start(this);
                break;
            case R.id.threadFixed:
                ThreadActivity1.start(this);
                break;
            case R.id.handlerFixed:
                HandlerActivity1.start(this);
                break;
            case R.id.singletonFixed:
                LoginActivity1.start(this);
                break;
            default:
                break;
        }
    }
}
