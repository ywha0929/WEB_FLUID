package com.example.falsenegativetestapp;

import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class UIUpdate extends androidx.appcompat.widget.AppCompatButton {
    public UIUpdate(@NonNull Context context) {
        super(context);
    }

    public void falseNegativeSetText(TextView textView, String text)
    {
        textView.setTextSize(40);
        textView.setTextSize(30);
        textView.setTextSize(50);
        textView.setText(text);
    }
}
