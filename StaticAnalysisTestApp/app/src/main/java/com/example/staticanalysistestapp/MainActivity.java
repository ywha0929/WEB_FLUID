package com.example.staticanalysistestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button button;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methodInvocation1.method1(textView);
            }
        });
    }
}
class methodInvocation3{
    public static void method3(TextView textView) {
        Log.d("TAG","hello this is log3");
        UIUpdate.uiUpdate(textView);
        return;
    }
}
class methodInvocation2{
    public static void method2(TextView textView) {
        Log.d("TAG","hello this is log2");
		methodInvocation3.method3(textView);
        UIUpdate.nonUIUpdate();
        return;
    }
}

class methodInvocation1{
    public static void method1(TextView textView) {

        Log.d("TAG","hello this is log");
        methodInvocation2.method2(textView);

    }
}
