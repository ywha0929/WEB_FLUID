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

import dalvik.system.DexClassLoader;

import java.lang.reflect.*;

public class MainActivity extends AppCompatActivity {

    private EditText edit1;
    private EditText edit2;

    private Button btn1;
    private Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//test
        edit1 = (EditText) findViewById(R.id.edit1);
        edit2 = (EditText) findViewById(R.id.edit2);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);


        edit1.setOnLongClickListener(new View.OnLongClickListener() {//distribute trigger
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        edit2.setOnLongClickListener(new View.OnLongClickListener() {//distribute trigger
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        btn1.setOnLongClickListener(new View.OnLongClickListener() {//distribute trigger
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        btn1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "btn1 short invoked");
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
                return true;
            }
        });
        btn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "btn2 short invoked");
                edit2.setTextSize(90);
            }
        });

    }
}