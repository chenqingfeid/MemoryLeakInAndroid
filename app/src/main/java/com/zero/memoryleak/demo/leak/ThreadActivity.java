package com.zero.memoryleak.demo.leak;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import com.zero.memoryleak.demo.R;

/**
 * 使用线程导致的内存泄漏
 *
 * @author cqf
 */
public class ThreadActivity extends Activity {

    /**
     * 在创建活动后的20秒内旋转/关闭活动时，会发生内存泄漏。
     * 由于AsyncTask被声明为非静态类，因此它将保存*活动的引用，
     * 从而使该Activity不符合垃圾收集的条件。
     * 注意：如果在旋转/关闭活动之前完成任务，一切正常。
     **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        new DownloadTask().start();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, ThreadActivity.class);
        context.startActivity(starter);
    }

    private class DownloadTask extends Thread {
        @Override
        public void run() {
            SystemClock.sleep(2000 * 10);
        }
    }
}