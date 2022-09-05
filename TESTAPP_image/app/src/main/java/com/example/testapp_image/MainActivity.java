package com.example.testapp_image;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button back_btn;
    Button next_btn;
    ImageView imageView;
    int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "[FLUID]TESTAPP_image";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        back_btn = findViewById(R.id.button1);
        next_btn = findViewById(R.id.button2);
        imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.pocketmonball);

        back_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG,"onLongClick invoked");
                return true;
            }
        });

        next_btn.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG,"onLongClick invoked");
                return true;
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG,"onLongClick invoked");
                return true;
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick invoked");
                state++;
                if (state == 0) {
                    imageView.setImageResource(R.drawable.pocketmonball);
                }
                else if (state == 1) {
                    imageView.setImageResource(R.drawable.stepone);
                }
                else if (state == 2) {
                    imageView.setImageResource(R.drawable.steptwo);
                }
                else if (state == 3) {
                    imageView.setImageResource(R.drawable.stepthree);
                }
                else {
                    state=3;
                    Toast.makeText(getApplicationContext(), "This is the last image.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state--;
                if (state == 0) {
                    imageView.setImageResource(R.drawable.pocketmonball);
                }
                else if (state == 1) {
                    imageView.setImageResource(R.drawable.stepone);
                }
                else if (state == 2) {
                    imageView.setImageResource(R.drawable.steptwo);
                }
                else if (state == 3) {
                    imageView.setImageResource(R.drawable.stepthree);
                }
                else {//if state < 0
                    state=0;
                    Toast.makeText(getApplicationContext(), "This is the first image.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}