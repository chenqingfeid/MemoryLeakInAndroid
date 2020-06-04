package com.zero.memoryleak.demo.leak;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;
import com.zero.memoryleak.demo.R;

/**
 * 注册监听导致的泄漏
 *
 * @author cqf
 */
public class StaticAsyncTaskActivity extends Activity implements DownloadListener {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        textView = findViewById(R.id.text_view);

        new DownloadTask(this).execute();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, StaticAsyncTaskActivity.class);
        context.startActivity(starter);
    }

    public void updateText() {
        textView.setText(R.string.hello);
    }

    @Override
    public void onDownloadTaskDone() {
        updateText();
    }

    private static class DownloadTask extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        private DownloadListener listener;

        public DownloadTask(DownloadListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(2000 * 10);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                listener.onDownloadTaskDone();
            } catch (Exception e) {
                //doNothing
            }
        }
    }
}