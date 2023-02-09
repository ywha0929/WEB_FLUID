package com.example.falsenegativetestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button button1;
    Button button2;
    Button button0;
    Button button3;
    Button button4;
    Button button5;
    final String TAG = "FLUID(FalseNegativeTestApp)";
    int mode = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        textView.setTextSize(50);
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+getTS());

                if(mode == 0)
                {
                    textView.setText("this is test");
                    mode = 1;
                }

                else
                {
                    textView.setText("running test");
                    mode = 0;
                }

            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+getTS());
                if(mode == 0)
                {
                    textView.setTextSize(40);
                    textView.setText("this is test");
                    mode = 1;
                }

                else
                {
                    textView.setTextSize(50);
                    textView.setText("running test");
                    mode = 0;
                }

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+getTS());

                if(mode == 0)
                {
                    textView.setTextSize(60);
                    textView.setTextSize(40);
                    textView.setText("this is test");
                    mode = 1;
                }

                else
                {
                    textView.setTextSize(60);
                    textView.setTextSize(50);
                    textView.setText("running test");
                    mode = 0;
                }

            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+getTS());
                if(mode == 0)
                {
                    textView.setTextSize(60);
                    textView.setTextSize(30);
                    textView.setTextSize(40);
                    textView.setText("this is test");
                    mode = 1;
                }

                else
                {
                    textView.setTextSize(60);
                    textView.setTextSize(30);
                    textView.setTextSize(50);
                    textView.setText("running test");
                    mode = 0;
                }

            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+getTS());
                if(mode == 0)
                {
                    textView.setTextSize(60);
                    textView.setTextSize(30);
                    textView.setTextSize(20);
                    textView.setTextSize(40);
                    textView.setText("this is test");
                    mode = 1;
                }

                else
                {
                    textView.setTextSize(60);
                    textView.setTextSize(30);
                    textView.setTextSize(20);
                    textView.setTextSize(50);
                    textView.setText("running test");
                    mode = 0;
                }

            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+getTS());
                if(mode == 0)
                {
                    textView.setTextSize(60);
                    textView.setTextSize(30);
                    textView.setTextSize(90);
                    textView.setTextSize(20);
                    textView.setTextSize(40);
                    textView.setText("this is test");
                    mode = 1;
                }

                else
                {
                    textView.setTextSize(60);
                    textView.setTextSize(30);
                    textView.setTextSize(90);
                    textView.setTextSize(20);
                    textView.setTextSize(50);
                    textView.setText("running test");
                    mode = 0;
                }

            }
        });
    }
    public static String getTS() {
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        return ts;
    }
}