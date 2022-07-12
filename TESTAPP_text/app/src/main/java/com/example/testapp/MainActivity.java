package com.example.testapp;

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

    Class<?> clazz = null;
    Method mtd;
    Object objFluidLib;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//test
        edit1 = (EditText) findViewById(R.id.edit1);
        edit2 = (EditText) findViewById(R.id.edit2);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        Activity a = this;
        DexClassLoader dex = new DexClassLoader("/data/local/tmp/fluidlib.apk", "/data/local/tmp/", null, getClass().getClassLoader());
        try {
            clazz = dex.loadClass("com.hmsl.fluidlib.FLUIDMain");

            Method mtdgetInstance = clazz.getDeclaredMethod("getInstance",Context.class);
            objFluidLib = mtdgetInstance.invoke(null,this);
            mtd = clazz.getDeclaredMethod("runBind");
            mtd.invoke(objFluidLib, null);



//            Log.d("TAG", "TRY started");
//            clazz = dex.loadClass("com.hmsl.fluidlib.FLUIDMain");
//            Log.d("TAG", "clazz : " + clazz.getClass().toString());
//            Object obj = clazz.newInstance();
//            Log.d("TAG", "obj = " + obj.getClass().toString());
//            obj = clazz.getDeclaredField("mServiceConnection").get(obj);
//            Intent intent = new Intent("com.hmsl.fluidmanager.MY_Service");
//            intent.setPackage("com.hmsl.fluidmanager");
//            //this();
//            intent.setClassName("com.hmsl.fluidmanager", "com.hmsl.fluidmanager.FLUIDManagerService");
//            Boolean bool = bindService(intent, (ServiceConnection) obj, Context.BIND_AUTO_CREATE);
//            Log.d("TAG", "is bind : " + bool);
        } catch(InvocationTargetException e)
        {
            e.getTargetException().printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
            return;
        }


        edit1.setOnLongClickListener(new View.OnLongClickListener() {//distribute trigger
            @Override
            public boolean onLongClick(View v) {
                try {
                    Log.d("TAG", "edit1 invoked");
                    mtd = clazz.getDeclaredMethod("runtest", String.class,View.class);
                    mtd.invoke(objFluidLib, "EditText", v);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            }
        });

        edit2.setOnLongClickListener(new View.OnLongClickListener() {//distribute trigger
            @Override
            public boolean onLongClick(View v) {
                try {
                    Log.d("TAG", "edit2 invoked");
                    mtd = clazz.getDeclaredMethod("runtest", String.class,View.class);
                    mtd.invoke(objFluidLib, "EditText", v);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            }
        });

        btn1.setOnLongClickListener(new View.OnLongClickListener() {//distribute trigger
            @Override
            public boolean onLongClick(View v) {
                try {
                    Log.d("TAG", "btn1 invoked");
                    mtd = clazz.getDeclaredMethod("runtest", String.class,View.class);
                    mtd.invoke(objFluidLib, "Button", v);

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

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
                    try {
                        mtd = clazz.getDeclaredMethod("runUpdateTest",String.class,View.class,Object.class);
                        mtd.invoke(objFluidLib,"setTextColor",edit1,Color.RED);
//                    mtd = clazz.getDeclaredMethod("runUpdate",String.class,View.class);
//                    mtd.invoke(objFluidLib,"setTextColor",edit1);
                    } catch(Exception e)
                    {
                        e.printStackTrace();
                        return;
                    }
                }
                else {
                    edit1.setTextColor(Color.BLUE);
                    try {
                        mtd = clazz.getDeclaredMethod("runUpdateTest",String.class,View.class,Object.class);
                        mtd.invoke(objFluidLib,"setTextColor",edit1,Color.BLUE);
//                    mtd = clazz.getDeclaredMethod("runUpdate",String.class,View.class);
//                    mtd.invoke(objFluidLib,"setTextColor",edit1);
                    } catch(Exception e)
                    {
                        e.printStackTrace();
                        return;
                    }
                }

            }
        });
        btn2.setOnLongClickListener(new View.OnLongClickListener() { //distribute trigger
            @Override
            public boolean onLongClick(View v) {
                try {
                    Log.d("TAG", "btn2 invoked");
                    mtd = clazz.getDeclaredMethod("runtest", String.class,View.class);
                    mtd.invoke(objFluidLib, "Button", v);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        });
        btn1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("TAG","motionEvent :\n"+motionEvent);
                return false;
            }
        });
        btn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "btn2 short invoked");
                edit2.setTextSize(90);
                try {
                    mtd = clazz.getDeclaredMethod("runUpdateTest",String.class,View.class,Object.class);
                    mtd.invoke(objFluidLib,"setTextSize",edit2,90.0);
//                    mtd = clazz.getDeclaredMethod("runUpdate",String.class,View.class);
//                    mtd.invoke(objFluidLib,"setTextSize",edit2);
                } catch(Exception e)
                {
                    e.printStackTrace();
                    return;
                }

            }
        });

    }
}