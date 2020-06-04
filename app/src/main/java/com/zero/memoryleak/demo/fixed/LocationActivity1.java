package com.zero.memoryleak.demo.fixed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.zero.memoryleak.demo.R;
import com.zero.memoryleak.demo.leak.LocationChangedListener;
import com.zero.memoryleak.demo.leak.LocationManager;

/**
 * 注册监听导致的泄漏解决方式
 *
 * @author cqf
 */
public class LocationActivity1 extends Activity implements LocationChangedListener {
    public static void start(Context context) {
        Intent starter = new Intent(context, LocationActivity1.class);
        context.startActivity(starter);
    }

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        textView = findViewById(R.id.text_view);
        LocationManager1.getInstance().register(this);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        LocationManager1.getInstance().unregister();
    }

    @Override public void onLocationUpdate(double lat, double lng) {
        textView.setText("lat:" + lat + "lng:" + lng);
    }
}