package com.zero.memoryleak.demo.fixed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;

import android.widget.TextView;

import com.zero.memoryleak.demo.R;
import java.lang.ref.WeakReference;

/**
 * 用弱引用机制解决注册 回调的导致的泄漏
 *
 * @author cqf
 **/

public class StaticAsyncTaskActivity1 extends Activity implements DownloadListener {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        textView = findViewById(R.id.text_view);

        new DownloadTask(this).execute();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, StaticAsyncTaskActivity1.class);
        context.startActivity(starter);
    }

    @Override
    public void onDownloadTaskDone() {
        updateText();
    }

    public void updateText() {
        textView.setText(R.string.hello);
    }

    private static class DownloadTask extends AsyncTask<Void, Void, Void> {

        /**
         * The WeakReference allows the Activity to be garbage collected.
         * garbage collected does not protect the weak reference from begin reclaimed.
         **/
        private WeakReference<DownloadListener> listener;

        private DownloadTask(DownloadListener activity) {
            this.listener = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(2000 * 10);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (listener.get() != null) {
                listener.get().onDownloadTaskDone();
            }
        }
    }
}
