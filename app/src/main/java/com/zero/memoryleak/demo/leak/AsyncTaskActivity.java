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
 * 内部类导致的内存泄漏
 *
 * @author cqf
 */
public class AsyncTaskActivity extends Activity {

    /**
     * 当我们在活动创建后的20秒内旋转/关闭该活动时，将会出现内存泄漏。
     * 由于AsyncTask被声明为非静态类，它持有容器类的引用
     * 导致Activity垃圾收集器无法回收
     *
     * 注意:如果任务是在旋转/关闭活动之前完成的，那么所有的事情都不会泄漏。
     **/

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        textView = findViewById(R.id.text_view);

        new DownloadTask().execute();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, AsyncTaskActivity.class);
        context.startActivity(starter);
    }

    public void updateText() {
        textView.setText(R.string.hello);
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(2000 * 10);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                updateText();
            } catch (Exception e) {
                //doNothing
            }
        }
    }
}