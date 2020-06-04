package com.zero.memoryleak.demo.fixed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import com.zero.memoryleak.demo.R;

/**
 * 解决不正当使用Handler 导致的内存泄漏
 *
 * @author cqf
 */
public class HandlerActivity1 extends Activity {

    private final DownloadTask downloadTask = new DownloadTask();

    private final Handler handler = new TaskHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        /**
         * 发布一个延时10分钟的任务
         *
         * **/
        handler.postDelayed(downloadTask, 1000 * 60 * 10);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * remove所有回调
         * **/
        handler.removeCallbacks(downloadTask);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, HandlerActivity1.class);
        context.startActivity(starter);
    }

    /**
     * 使用的是静态内部类，这样内部类无法持有外部类的引用
     **/
    private static class DownloadTask implements Runnable {
        @Override
        public void run() {
            Log.e("HandlerActivity", "in run()");
        }
    }

    /**
     * 使用的是静态内部类，解决内部类持有外部类的引用导致的泄漏问题
     **/
    private static class TaskHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.e("HandlerActivity", "handle message");
        }
    }
}