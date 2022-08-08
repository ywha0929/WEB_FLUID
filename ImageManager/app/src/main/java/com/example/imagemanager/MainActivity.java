package com.example.imagemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button next_btn;
    Button back_btn;
    ImageView imageView;
    int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        next_btn = findViewById(R.id.button2);
        imageView = findViewById(R.id.imageView);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 0) {
                    imageView.setImageResource(R.drawable.stepone);
                    state++;
                }
                else if (state == 1) {
                    imageView.setImageResource(R.drawable.steptwo);
                    state++;
                }
                else if (state ==2) {
                    imageView.setImageResource(R.drawable.stepthree);
                    state++;
                }
                else {
                    Toast.makeText(getApplicationContext(), "This is the last image.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        back_btn = findViewById(R.id.button1);
        imageView = findViewById(R.id.imageView);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 0) {
                    Toast.makeText(getApplicationContext(), "This is the first image.", Toast.LENGTH_SHORT).show();
                }
                else if (state == 1) {
                    imageView.setImageResource(R.drawable.stepone);
                    state--;
                }
                else if (state == 2) {
                    imageView.setImageResource(R.drawable.steptwo);
                    state--;
                }
                else {
                    imageView.setImageResource(R.drawable.stepthree);
                    state--;
                }
            }
        });

    }

}