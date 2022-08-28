package com.example.testapp_test;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText edit1;
    private TextView text1;

    private Button btn1;



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("FLUID", "onTouchEvent");
        return super.onTouchEvent(event);
    }

    private Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//test
        edit1 = (EditText) findViewById(R.id.edit1);
        text1 = (TextView) findViewById(R.id.text1);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        View view = findViewById(R.id.rootlayout);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("TAG", "onTouch: onTouch on whole layout");

                return false;
            }
        });


        edit1.setOnLongClickListener(new View.OnLongClickListener() {//distribute trigger
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        text1.setOnLongClickListener(new View.OnLongClickListener() {//distribute trigger
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
                text1.setTextSize(90);
            }
        });

    }
}