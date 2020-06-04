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
public class LocationActivity extends Activity implements LocationChangedListener {
    public static void start(Context context) {
        Intent starter = new Intent(context, LocationActivity.class);
        context.startActivity(starter);
    }

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        textView = findViewById(R.id.text_view);
        LocationManager.getInstance().register(this);

    }

    @Override public void onLocationUpdate(double lat, double lng) {
        textView.setText("lat:" + lat + "lng:" + lng);
    }
}