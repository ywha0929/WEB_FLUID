package com.example.testapp_test;

import static android.os.SystemClock.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import dalvik.system.DexClassLoader;

import java.lang.reflect.*;

public class MainActivity extends AppCompatActivity {

    private EditText edit1;
    private TextView text1;

    private Button btn1;
    private Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String TAG = "FLUID TargetApp";
//test
        edit1 = (EditText) findViewById(R.id.edit1);
        text1 = (TextView) findViewById(R.id.text1);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);


        edit1.setOnLongClickListener(new View.OnLongClickListener() {//distribute trigger
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG,"onLongClick invocated : "+getTS());
                return true;
            }
        });

        text1.setOnLongClickListener(new View.OnLongClickListener() {//distribute trigger
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG,"onLongClick invocated : "+getTS());
                return true;
            }
        });

        btn1.setOnLongClickListener(new View.OnLongClickListener() {//distribute trigger
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG,"onLongClick invocated : "+getTS());
                return true;
            }
        });
        btn1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "btn1 short invoked");
                Log.d(TAG,"onLongClick invocated : "+getTS());
                if(edit1.getCurrentTextColor() == Color.BLUE)
                {
                    edit1.setTextColor(Color.RED);
                }
                else {
                    edit1.setTextColor(Color.BLUE);
                }

            }
        });
        btn2.setOnLongClickListener(new View.OnLongClickListener() { //distribute trigger
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG,"onLongClick invocated : "+getTS());
                return true;
            }
        });
        btn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "btn2 short invoked");
                Log.d(TAG,"onLongClick invocated : "+getTS());
                text1.setTextSize(90);
            }
        });

    }
    public static String getTS() {
        Long tsLong = System.nanoTime();
        String ts = tsLong.toString();
        return ts;
    }
}