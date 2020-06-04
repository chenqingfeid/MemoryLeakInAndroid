package com.zero.memoryleak.demo.fixed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import com.zero.memoryleak.demo.R;

/**
 * @author cqf
 */
public class ThreadActivity1 extends Activity {

    /**
     * if the task done before to move to another activity
     * or rotate the device every thing will works fine with out leak.
     **/

    private DownloadTask thread = new DownloadTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * Interrupts/stops this thread.
         * **/
        thread.interrupt();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, ThreadActivity1.class);
        context.startActivity(starter);
    }

    /**
     * make it static so it does not have referenced to the containing activity class
     **/
    private static class DownloadTask extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                SystemClock.sleep(2000 * 10);
            }
        }
    }
}