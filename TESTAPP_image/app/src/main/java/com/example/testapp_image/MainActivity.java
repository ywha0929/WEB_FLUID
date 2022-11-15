package com.example.testapp_image;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    final String TAG = "[FLUID]TESTAPP_image";
    private Button back_btn;
    private Button next_btn;
    private ImageView imageView;
    private int state = 0;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig)
    {
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) //landscape screen
        {
            setContentView(R.layout.activity_main_landscape);
            Log.d(TAG,"This is Landscape screen!");
        }
        else //portrait screen
        {
            setContentView(R.layout.activity_main);
            Log.d(TAG,"This is Portrait screen!");
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"onSave executed");
        outState.putInt("state", state);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        state=savedInstanceState.getInt("state");
        Log.d(TAG,"onRestore executed");

        if (state == 0)
        {
            imageView.setImageResource(R.drawable.pocketmonball);
        }
        else if (state == 1)
        {
            imageView.setImageResource(R.drawable.stepone);
        }
        else if (state == 2)
        {
            imageView.setImageResource(R.drawable.steptwo);
        }
        else if (state == 3)
        {
            imageView.setImageResource(R.drawable.stepthree);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        Log.d(TAG,"onCreate executed");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Configuration newConfig = getResources().getConfiguration();

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) //landscape screen
        {
            setContentView(R.layout.activity_main_landscape);
            Log.d(TAG,"This is Landscape screen!");
            back_btn = findViewById(R.id.button3);
            next_btn = findViewById(R.id.button4);
            imageView = findViewById(R.id.imageView);
        }
        else //portrait screen
        {
            setContentView(R.layout.activity_main);
            Log.d(TAG,"This is Portrait screen!");
            back_btn = findViewById(R.id.button1);
            next_btn = findViewById(R.id.button2);
            imageView = findViewById(R.id.imageView);
        }


        back_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view)
            {
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
            public boolean onLongClick(View view)
            {
                Log.d(TAG,"onLongClick invoked");
                return true;
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG,"onClick invoked");
                state++;
                if (state == 0)
                {
                    imageView.setImageResource(R.drawable.pocketmonball);
                }
                else if (state == 1)
                {
                    imageView.setImageResource(R.drawable.stepone);
                }
                else if (state == 2)
                {
                    imageView.setImageResource(R.drawable.steptwo);
                }
                else if (state == 3)
                {
                    imageView.setImageResource(R.drawable.stepthree);
                }
                else
                {
                    state=3;
                    Toast.makeText(getApplicationContext(), "This is the last image.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                state--;
                if (state == 0)
                {
                    imageView.setImageResource(R.drawable.pocketmonball);
                }
                else if (state == 1)
                {
                    imageView.setImageResource(R.drawable.stepone);
                }
                else if (state == 2)
                {
                    imageView.setImageResource(R.drawable.steptwo);
                }
                else if (state == 3)
                {
                    imageView.setImageResource(R.drawable.stepthree);
                }
                else
                { //if state < 0
                    state=0;
                    Toast.makeText(getApplicationContext(), "This is the first image.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}