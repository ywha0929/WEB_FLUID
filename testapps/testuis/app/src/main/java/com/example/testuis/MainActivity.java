package com.example.testuis;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button button;
    TextView textView;
    ImageView imageView;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    SeekBar seekBar;
    Switch switchUI;
    EditText editText;
    static final String TAG = "tsetuis";
    Color textColor;
    Boolean switchB;

    Button btnTextView;
    Button btnImageView;
    Button btnRadioGroup;
    Button btnSeekBar;
    Button btnEditText;
    Button btnSwitch;
    Button btnButton;
    Boolean imageViewSet = new Boolean(false);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        textColor = new Color();
        textColor = Color.valueOf(Color.BLACK);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        radioGroup = findViewById(R.id.radioGroup);
        editText = findViewById(R.id.editText);
        switchUI = findViewById(R.id.switch1);
        seekBar = findViewById(R.id.seekBar);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);

        btnButton = findViewById(R.id.buttonButton);
        btnImageView = findViewById(R.id.buttonImageView);
        btnEditText = findViewById(R.id.buttonEditText);
        btnRadioGroup = findViewById(R.id.buttonRadioGroup);
        btnSeekBar = findViewById(R.id.buttonSeekBar);
        btnSwitch = findViewById(R.id.buttonSwitch);
        btnTextView = findViewById(R.id.buttonTextView);
        imageView.setImageResource(R.drawable.earth);
        switchB = switchUI.isChecked();

        btnEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().equals("hello"))
                    editText.setText("bye");
                else
                    editText.setText("hello");
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(imageViewSet.equals(false))
                {
                    imageView.setImageResource(R.drawable.ic_launcher_foreground);
                    imageViewSet = true;
                }
                else
                {
                    imageView.setImageResource(R.drawable.earth);
                    imageViewSet = false;
                }
                return true;
            }
        });

        btnImageView.setOnClickListener(new View.OnClickListener() {
            Boolean Stroke = false;
            @Override
            public void onClick(View view) {
                if(Stroke.equals(false))
                {
                    GradientDrawable newBorder = new GradientDrawable();
                    newBorder.setStroke(2,Color.RED);
                    newBorder.setColor(0);
                    imageView.setForeground(newBorder);
                    Stroke = true;
                }
                else
                {
                    GradientDrawable newBorder = new GradientDrawable();
                    newBorder.setStroke(52,Color.RED);
                    newBorder.setColor(0);
                    imageView.setForeground(newBorder);
                    Stroke = false;
                }
            }
        });

        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(button.getText().toString().equals("hello"))
                    button.setText("bye");
                else
                    button.setText("hello");
            }
        });

        btnRadioGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioGroup.getCheckedRadioButtonId() == radioButton1.getId())
                {
                    radioButton2.setChecked(true);
                }
                else
                {
                    radioButton1.setChecked(true);
                }
            }
        });

        btnSeekBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(seekBar.getProgress() == seekBar.getMax())
                    seekBar.setProgress(0);
                else
                    seekBar.setProgress(seekBar.getMax());
            }
        });

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchUI.isChecked() == true)
                    switchUI.setChecked(false);
                else
                    switchUI.setChecked(true);
            }
        });

        btnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView.getText().toString().equals("hello"))
                    textView.setText("bye");
                else
                    textView.setText("hello");
            }
        });
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                editText.setText("");
//                textView.setText("");
//            }
//        });
//
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                textView.setText(charSequence);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                Log.d(TAG, "onCheckedChanged: "+i);
//                if(switchB == false) {
//                    if (i == R.id.radioButton1) {
//                        textColor = Color.valueOf(Color.BLUE);
//                        textView.setTextColor(Color.BLUE);
//                    } else {
//                        textColor = Color.valueOf(Color.RED);
//                        textView.setTextColor(Color.RED);
//                    }
//                }
//            }
//        });
//
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                GradientDrawable newBorder = new GradientDrawable();
//                newBorder.setStroke(i,Color.RED);
//                newBorder.setColor(0);
//                imageView.setForeground(newBorder);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        switchUI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                switchB = b;
//                if(!b)
//                {
//                    textView.setTextColor(textColor.toArgb());
//                }
//                else
//                    textView.setTextColor(Color.WHITE);
//            }
//        });
    }
}