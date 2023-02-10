package com.example.staticanalysistestapp;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class UIUpdate extends androidx.appcompat.widget.AppCompatButton {

    public UIUpdate(Context context) {
        super(context);
    }
    static void uiUpdate(TextView textView) {
        textView.setText("hi");
    }
    static void nonUIUpdate(){
        Log.d("TAG","this is non UI update");
    }
}
