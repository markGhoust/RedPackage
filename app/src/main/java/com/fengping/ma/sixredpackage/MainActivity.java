package com.fengping.ma.sixredpackage;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button helloworld;
    //EditText mNotTextView;

    final static String TAG = "fengping.ma.test";

    static int notification_id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helloworld = findViewById(R.id.helloworld);
        //mNotTextView = findViewById(R.id.notification_text);
        helloworld.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        /*if (v.getId() == R.id.helloworld) {
            String content = String.valueOf(mNotTextView.getText());
            *//*long downTime = SystemClock.uptimeMillis();
            final MotionEvent downEvent = MotionEvent.obtain(downTime,downTime,MotionEvent.ACTION_DOWN,300,300,0);
            downTime =+ 1000;
            final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 300, 300, 0);
            v.onTouchEvent(downEvent);
            v.onTouchEvent(upEvent);
            downEvent.recycle();
            upEvent.recycle();*//*
        }*/
    }
}
