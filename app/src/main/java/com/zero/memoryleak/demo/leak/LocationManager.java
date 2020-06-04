package com.zero.memoryleak.demo.leak;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.SystemClock;

/**
 * @author cqf
 */
public class LocationManager {
    public static LocationManager instance;
    private LocationChangedListener mListener;

    public static LocationManager getInstance() {
        if (instance == null) {
            synchronized (LocationManager.class) {
                if (instance == null) {
                    instance = new LocationManager();
                }
            }
        }
        return instance;
    }

    public void register(LocationChangedListener listener) {
        this.mListener = listener;
        new LocationTask(mListener).execute();
    }

    private static class LocationTask extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        private LocationChangedListener listener;

        public LocationTask(LocationChangedListener listener) {
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
                listener.onLocationUpdate(114, 114);
            } catch (Exception e) {
                //doNothing
            }
        }
    }
}


