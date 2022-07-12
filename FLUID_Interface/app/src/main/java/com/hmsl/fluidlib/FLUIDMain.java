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
import android.util.Log;
import android.view.GestureDetector;
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
import java.util.List;
import java.util.StringTokenizer;

public class FLUIDMain {
    private static final String TAG = "FLUID(FLUIDLib)";
    static public com.hmsl.fluidmanager.IFLUIDService mRemoteService = null;
    public ServiceConnection mServiceConnection;
    private static final int MAX_BUFFER = 1024;
    public Context mContext = null;
    static FLUIDMain instance;
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

        public void reverseMotionEvent(Bundle bundle) throws RemoteException
        {
            Log.d(TAG,"this is reverseMotionEvent");
            Activity activity = (Activity) mContext;
            List<MotionEvent> motionEventList = new ArrayList<MotionEvent>();
            activity.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    int ID = bundle.getInt("ID");
                    MotionEvent motionEvent = bundle.getParcelable("motionevent");
                    View view = (View) activity.findViewById(ID);
                    long timeInterval = motionEvent.getDownTime() - motionEvent.getEventTime();
                    long curtime = System.nanoTime()/1000000;
                    MotionEvent newmotionEvent = MotionEvent.obtain(curtime+timeInterval,curtime,motionEvent.getAction(),
                            motionEvent.getX(),motionEvent.getY(),motionEvent.getMetaState());
                    if(newmotionEvent.getAction() !=MotionEvent.ACTION_UP)
                    {
                        motionEventList.add(newmotionEvent);
                    }
                    else
                    {
                        for(MotionEvent m : motionEventList)
                            view.dispatchTouchEvent(m);
                    }
                    view.dispatchTouchEvent(newmotionEvent);

                    Toast toast = Toast.makeText(mContext.getApplicationContext(), "got message from service \nID : " + bundle.getInt("ID") + "\nobject: "+bundle.getParcelable("motionevent"),
                            Toast.LENGTH_SHORT);
                    toast.show();
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
            //Log.d("TAG","runtest");
            bundle.putByteArray("key", toSend);
            Log.d(TAG, "runtest send : " + getTS());
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
        } else if (type.contains("Character")) {
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
}