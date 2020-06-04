package com.zero.memoryleak.demo.fixed.asynctask;

import android.app.Activity;
import android.os.Bundle;

import android.widget.TextView;
import com.zero.memoryleak.demo.R;
import com.zero.memoryleak.demo.fixed.DownloadListener;

/**
 * 匿名内部类导致的内存泄漏的解决方法
 *
 * @author cqf
 */
public class BestAsyncTaskActivity extends Activity implements DownloadListener {

    private TextView textView;
    private DownloadTask downloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        textView = findViewById(R.id.text_view);
        downloadTask = new DownloadTask(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 取消任务，不会再调用onPostExecute
         * **/
        downloadTask.cancel(true);
    }

    @Override
    public void onDownloadTaskDone() {
        updateText();
    }

    public void updateText() {
        textView.setText(R.string.hello);
    }
}
