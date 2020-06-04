package com.zero.memoryleak.demo.fixed.asynctask;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.zero.memoryleak.demo.fixed.DownloadListener;
import java.lang.ref.WeakReference;

/**
 * @author cqf
 */
public class DownloadTask extends AsyncTask<Void, Void, Void> {

    /**
     * 可以用WeakReference 持有listener引用
     * 垃圾回收机制里是这样描述的，一旦触发GC弱引用的对象则会被回收
     **/
    private WeakReference<DownloadListener> listener;

    public DownloadTask(DownloadListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    @Override
    protected Void doInBackground(Void... params) {
        /**
         * 检查是否取消
         * **/
        while (!isCancelled()) {
            SystemClock.sleep(2000 * 10);
        }
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