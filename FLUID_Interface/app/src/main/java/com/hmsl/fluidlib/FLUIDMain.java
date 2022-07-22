package com.hmsl.fluidlib;

import static android.os.SystemClock.sleep;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.constraintlayout.widget.ConstraintSet;

import com.hmsl.fluidmanager.IFLUIDService;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class FLUIDMain {
    private static final String TAG = "FLUID(FLUIDLib)";
    static public com.hmsl.fluidmanager.IFLUIDService mRemoteService = null;
    public ServiceConnection mServiceConnection;
    private static final int MAX_BUFFER = 1024;
    public Context mContext = null;
    static FLUIDMain instance;
    long latestEventTime;
    private Map<Integer,Object> listTextListener = new HashMap<Integer,Object>();
    private final IBinder mBinder = new IReverseConnection.Stub() {
        @Override
        public void doCheck(int a) throws RemoteException {
            Log.d(TAG, "this is doCheck");
            Activity activity = (Activity) mContext;
            activity.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    Toast toast = Toast.makeText(mContext.getApplicationContext(), "got message from service" + a, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

        }
        public void reverseKeyboardEvent(Bundle bundle) throws RemoteException
        {
            Log.d(TAG,"this is reverseMotionEvent");
            Activity activity = (Activity) mContext;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int ID = bundle.getInt("ID");
                    CharSequence text= (CharSequence) bundle.getCharSequence("text");
                    TextView textView = (TextView) activity.findViewById(ID);
                    if(!textView.getText().toString().equals(text.toString())) {
                        textView.removeTextChangedListener((TextWatcher) listTextListener.get(ID));
                        textView.setText(text);
                        //textwatcher watcher = new textwatcher(textView);
                        textView.addTextChangedListener((TextWatcher) listTextListener.get(ID));
                        //listTextListener.put(ID,watcher);
                    }
                    Toast toast = Toast.makeText(mContext.getApplicationContext(), "got message from service \nID : " + bundle.getInt("ID") + "\nobject: "+bundle.getCharSequence
                                    ("text"),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
        public void reverseMotionEvent(Bundle bundle) throws RemoteException
        {
            Log.d(TAG,"this is reverseMotionEvent");
            Activity activity = (Activity) mContext;


            activity.runOnUiThread(new Runnable(){

                @Override
                public void run() {

                    int ID = bundle.getInt("ID");
                    MotionEvent motionEvent = bundle.getParcelable("motionevent");
                    View view = (View) activity.findViewById(ID);
                    float motionX = bundle.getFloat("x");
                    float motionY = bundle.getFloat("y");
                    float guestWidth = bundle.getFloat("width");
                    float guestHeight = bundle.getFloat("height");
                    float newX = (float)((motionX/guestWidth) * (float)view.getWidth());
                    float newY = (float)((motionY/guestHeight) * (float)view.getHeight());
                    Log.d(TAG,"motionX : "+motionX);
                    Log.d(TAG,"motionY : "+motionY);
                    Log.d(TAG,"guestWidth : "+guestWidth);
                    Log.d(TAG,"guestHeight : "+guestHeight);
                    //Log.d(TAG,"event getX : "+motionEvent.getX());
                    //Log.d(TAG,"event getY : "+motionEvent.getY());
                    motionEvent.setLocation(newX,newY);
                    Log.d(TAG,"after event getX : "+motionEvent.getX());
                    Log.d(TAG,"after event getY : "+motionEvent.getY());
//                    MotionEvent newmotionEvent = MotionEvent.obtain(curtime+timeInterval,curtime,motionEvent.getAction(),
//                            255.0f,90.0f,motionEvent.getMetaState());
                    if(motionEvent.getEventTime() != latestEventTime) {
                        latestEventTime = motionEvent.getEventTime();

                        view.dispatchTouchEvent(motionEvent);
                    }

//                    Toast toast = Toast.makeText(mContext.getApplicationContext(), "got message from service \nID : " + bundle.getInt("ID") + "\nobject: "+bundle.getParcelable("motionevent"),
//                            Toast.LENGTH_SHORT);
//                    toast.show();
                }
            });
        }
        // distribute


    };


    public static FLUIDMain getInstance(Context context) {
        if (instance == null) {
            instance = new FLUIDMain(context);
        }
        return instance;
    }

    public FLUIDMain(Context context) {
        Log.d(TAG, "FluidMain");
        mContext = context;
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mRemoteService = com.hmsl.fluidmanager.IFLUIDService.Stub.asInterface(service);
                Log.d(TAG, "FLUIDManagerService connected = " + mRemoteService);
                Bundle bundle = new Bundle();
                ComponentName componentName = new ComponentName(mContext,"com.example.testapp.MainActivity.class");
                bundle.putParcelable("ComponentName",componentName);
                bundle.putBinder("Binder", mBinder);
                try {
                    mRemoteService.reverseConnect(bundle);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "FLUIDManagerService disconnected = " + mRemoteService);
                mRemoteService = null;

            }
        };
    }

    public void runBind() {
        Intent intent = new Intent();
        intent.setPackage("com.hmsl.fluidmanager");
        intent.setClassName("com.hmsl.fluidmanager", "com.hmsl.fluidmanager.FLUIDManagerService");
        Log.d("TAG", "mContext info : " + mContext);

        Boolean isConnected = mContext.bindService(intent, (ServiceConnection) mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d("TAG", "is bind : " + isConnected + mRemoteService);


    }

    public void reverseBind() {

        Log.d(TAG, "this is reverseBind");
        Bundle bundle = new Bundle();
        bundle.putBinder("Binder", mBinder);
        Log.d("TAG", "mContext info : " + mContext);

        try {


            while (mRemoteService == null) {
                Log.d(TAG, "waiting1...");
            }
            Log.d(TAG, "mRemoteService : " + mRemoteService);
            mRemoteService.reverseConnect(bundle);
            while (mRemoteService == null) {
                Log.d(TAG, "waiting2...");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void runtest(String widgetType, View view) {
        Bundle bundle = new Bundle();

        try {
            //Log.d("TAG","run test");
            byte[] toSend = generate_byteArray(widgetType, view);
            Log.d(TAG,"runtest : "+widgetType);
            bundle.putByteArray("key", toSend);
            Log.d(TAG, "runtest send : " + getTS());
            if(widgetType.contains("TextView") || widgetType.contains("EditText"))
            {
                Log.d(TAG, "runtest add Listener : " + getTS());
                TextView textView = (TextView) view;
                textwatcher watcher = new textwatcher(textView);
                textView.addTextChangedListener(watcher);
                listTextListener.put(textView.getId(),watcher);
//                textView.addTextChangedListener(new TextWatcher() {
//                    String beforeText;
//                    @Override
//                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                        beforeText = textView.getText().toString();
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                        Bundle bundle = new Bundle();
//                        if(!textView.getText().toString().equals(beforeText))
//                        {
//                            try {
//                                bundle.putByteArray("key",generate_ubyteArray("setText",textView,charSequence.toString()));
//                                mRemoteService.update(bundle);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//
//                    }
//                });
            }
            mRemoteService.test(bundle);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void runUpdateTest(String signature, View view,Object param)
    {
        Bundle bundle = new Bundle();
        try{
            bundle.putByteArray("key",generate_ubyteArray(signature,view,param));
            mRemoteService.update(bundle);
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void runUpdate(String unit, View view) {
        StringTokenizer st = new StringTokenizer(unit, "<>");

        st.nextToken();                     // 앞에 virtualinvoke 부분 -> 필요없으므로 삭제
        String first = st.nextToken();      // <> 내부 -> method가 있는 부분
        String second = st.nextToken();     // parameter 부분

        // todo : <> 내부에 method만 짤라내야한다.
        StringTokenizer st1 = new StringTokenizer(first, " ");
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (st1.hasMoreTokens()) {
            String str = st1.nextToken();
            if (str.contains("(")) {
                sb.append(str);
                i++;
            } else if (str.contains(",") || str.contains(")")) {
                sb.append(str);
                i++;
            }
        }

        // method 변수 : method 이름만 들어감(ex.setTextSize)
        // paramter[] : method 다음 괄호 안에 parameter type이 차례로 배열에 들어감.
        StringTokenizer st2 = new StringTokenizer(sb.toString(), "(,)");
        String method = st2.nextToken();
        String[] parameterType = new String[i];

        for (int j = 0; j < i; j++) {
            parameterType[j] = st2.nextToken();
        }

        // <???> 다음 괄호 안 파라미터 인수들 처리
        StringTokenizer st3 = new StringTokenizer(second, " (,)");
        String[] parameter = new String[i];
        Object[] params = new Object[i];

        //params[j].getClass() -> Wrapper class 형식
        //params[j] parameter 그대로 잘 나옴.
        for (int j = 0; j < i; j++) {
            parameter[j] = st3.nextToken();
            params[j] = setType(parameterType[j], parameter[j]);
        }

        Bundle bundle = new Bundle();
        try {
            bundle.putByteArray("key", generate_ubyteArray(method, view, params));
            Log.d(TAG, "runUpdate send : " + getTS());
            mRemoteService.update(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object setType(String parameterType, String parameter) {
        Object params = null;
        switch (parameterType) {
            // 추후에 필요한 파라미터 타입은 추가하면 됌
            case "float":
                params = Float.parseFloat(parameter);
                break;
            case "int":
                params = Integer.parseInt(parameter);
                break;
            case "String":
                params = parameter;
                break;
        }
        return params;
    }

    public static int setTypeFlag(Object param) {
        String type = param.getClass().toString();
        //Log.d("TAG",param.getClass().toString());
        if (type.contains("Float")) {
            return 1;
        } else if (type.contains("Integer")) {
            return 2;
        } else if (type.contains("String")) {
            return 3;
        } else if (type.contains("Boolean")) {
            return 4;
        } else if (type.contains("Double")) {
            return 5;
        } else if (type.contains("Long")) {
            return 6;
        } else if (type.contains("Char")) {
            return 7;
        } else
            return 0;
    }

    public static byte[] generate_ubyteArray(String method, View view, Object... params) throws IOException {
        byte[] utoByteArray = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeInt(view.getId());
        dataOutputStream.writeBoolean(true);
        //dataOutputStream.writeInt(view.getId());
        dataOutputStream.writeUTF(method);

        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            int flag = setTypeFlag(param);
            dataOutputStream.writeInt(flag);
            switch (flag) {
                case 1:
                    dataOutputStream.writeFloat((Float) param);
                    break;
                case 2:
                    dataOutputStream.writeInt((int) param);
                    break;
                case 3:
                    dataOutputStream.writeUTF((String) param);
                    break;
                case 4:
                    dataOutputStream.writeBoolean((Boolean) param);
                    break;
                case 5:
                    dataOutputStream.writeDouble((Double) param);
                    break;
                case 6:
                    dataOutputStream.writeLong((Long) param);
                    break;
                case 7:
                    dataOutputStream.writeChar((Character) param);
                    break;
                default:
                    //Log.d("TAG","invalid param");
            }
        }

        dataOutputStream.flush();
        utoByteArray = byteArrayOutputStream.toByteArray();
        return utoByteArray;
    }

    public static byte[] generate_byteArray(String widgetType, View view) throws IOException {
        byte[] dtoByteArray = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(view.getId());
        //Log.d("TAG",""+view.getId());
        dataOutputStream.writeBoolean(false);


        if (widgetType.contains("EditText")) {

            EditText edit = (EditText) view;
            dataOutputStream.writeUTF(widgetType);
            //dataOutputStream.writeInt(edit.getId());
            dataOutputStream.writeUTF(edit.getText().toString());

            dataOutputStream.writeFloat(edit.getTextSize());
            dataOutputStream.flush();
            dtoByteArray = byteArrayOutputStream.toByteArray();

        } else if (widgetType.contains("Button")) {
            Button btn = (Button) view;
            dataOutputStream.writeUTF(widgetType);
            //dataOutputStream.writeInt(btn.getId());
            dataOutputStream.writeUTF(btn.getText().toString());
            dataOutputStream.writeInt(btn.getHeight());
            dataOutputStream.writeInt(btn.getWidth());
            dataOutputStream.flush();
            dtoByteArray = byteArrayOutputStream.toByteArray();
        }
        if (widgetType.contains("TextView")) {

            TextView edit = (TextView) view;
            dataOutputStream.writeUTF(widgetType);
            //dataOutputStream.writeInt(edit.getId());
            dataOutputStream.writeUTF(edit.getText().toString());

            dataOutputStream.writeFloat(edit.getTextSize());
            dataOutputStream.flush();
            dtoByteArray = byteArrayOutputStream.toByteArray();

        }
        return dtoByteArray;
    }

    public static String getTS() {
        Long tsLong = System.nanoTime();
        String ts = tsLong.toString();
        return ts;
    }
    class textwatcher implements TextWatcher
    {
        String beforeText;
        TextView textV;
        public textwatcher(TextView textV)
        {
            this.textV = textV;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.d(TAG, "before carSequence : " + charSequence);
            Log.d(TAG, "before getText : " + textV.getText().toString());
            beforeText = textV.getText().toString();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Bundle bundle = new Bundle();
            if(!textV.getText().toString().equals(beforeText))
            {
                try {
                    bundle.putByteArray("key",generate_ubyteArray("setText",textV,charSequence.toString()));
                    mRemoteService.update(bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}