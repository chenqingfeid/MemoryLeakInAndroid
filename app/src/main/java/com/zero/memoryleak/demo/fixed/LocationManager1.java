package com.zero.memoryleak.demo.fixed;

import android.os.AsyncTask;
import android.os.SystemClock;
import com.zero.memoryleak.demo.leak.LocationChangedListener;
import java.lang.ref.WeakReference;

/**
 * @author cqf
 */
public class LocationManager1 {
    private static LocationManager1 instance;
    private LocationTask mTask;

    public static LocationManager1 getInstance() {
        if (instance == null) {
            synchronized (LocationManager1.class) {
                if (instance == null) {
                    instance = new LocationManager1();
                }
            }
        }
        return instance;
    }

    public void register(LocationChangedListener listener) {
        mTask = new LocationTask(listener);
        mTask.execute();
    }

    public void unregister() {
        mTask.cancel(true);
    }

    private static class LocationTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<LocationChangedListener> listenerWeakReference =
            new WeakReference<>(null);

        public LocationTask(LocationChangedListener listener) {
            listenerWeakReference = new WeakReference<>(listener);
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
                if (isCancelled()) {
                    return;
                }
                final LocationChangedListener listener = listenerWeakReference.get();
                if (listener != null) {
                    listener.onLocationUpdate(114, 114);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


