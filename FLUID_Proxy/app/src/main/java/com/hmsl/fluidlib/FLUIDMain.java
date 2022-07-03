package com.hmsl.fluidlib;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

public class FLUIDMain {
    private static final String TAG = "FLUID(FLUIDLib)";
    public static com.hmsl.fluidmanager.IFLUIDService mRemoteService = null;
    private static final int MAX_BUFFER = 1024;

    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteService = com.hmsl.fluidmanager.IFLUIDService.Stub.asInterface(service);
            Log.d(TAG, "FLUIDManagerService connected = " + mRemoteService);

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "FLUIDManagerService disconnected = " + mRemoteService);
            mRemoteService = null;
        }
    };
    public static void runtest(String widgetType,  View view)
    {
        Bundle bundle = new Bundle();

        try {
            //Log.d("TAG","runtest");
            byte[] toSend = generate_byteArray(widgetType,view);
            //Log.d("TAG","runtest");
            bundle.putByteArray("key",toSend);
            Log.d(TAG,"runtest send : "+ getTS());
            mRemoteService.test(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void runUpdate(String unit, View view)
    {
        StringTokenizer st = new StringTokenizer(unit, "<>");

        st.nextToken();                     // 앞에 virtualinvoke 부분 -> 필요없으므로 삭제
        String first = st.nextToken();      // <> 내부 -> method가 있는 부분
        String second = st.nextToken();     // parameter 부분

        // todo : <> 내부에 method만 짤라내야한다.
        StringTokenizer st1 = new StringTokenizer(first, " ");
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while(st1.hasMoreTokens()){
            String str = st1.nextToken();
            if(str.contains("(")){
                sb.append(str);
                i++;
            }
            else if(str.contains(",") || str.contains(")")){
                sb.append(str);
                i++;
            }
        }

        // method 변수 : method 이름만 들어감(ex.setTextSize)
        // paramter[] : method 다음 괄호 안에 parameter type이 차례로 배열에 들어감.
        StringTokenizer st2 = new StringTokenizer(sb.toString(), "(,)");
        String method = st2.nextToken();
        String[] parameterType = new String[i];

        for(int j = 0; j< i; j++){
            parameterType[j] = st2.nextToken();
        }

        // <???> 다음 괄호 안 파라미터 인수들 처리
        StringTokenizer st3 = new StringTokenizer(second, " (,)");
        String[] parameter = new String[i];
        Object[] params = new Object[i];

        //params[j].getClass() -> Wrapper class 형식
        //params[j] parameter 그대로 잘 나옴.
        for(int j = 0; j<i; j++){
            parameter[j] = st3.nextToken();
            params[j] = setType(parameterType[j], parameter[j]);
        }

        Bundle bundle = new Bundle();
        try {
            bundle.putByteArray("key",generate_ubyteArray(method,view,params));
            Log.d(TAG,"runUpdate send : "+ getTS());
            mRemoteService.update(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object setType(String parameterType, String parameter){
        Object params = null;
        switch(parameterType){
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

    public static int setTypeFlag(Object param)
    {
        String type = param.getClass().toString();
        //Log.d("TAG",param.getClass().toString());
        if(type.contains("Float"))
        {
            return 1;
        }
        else if(type.contains("Integer"))
        {
            return 2;
        }
        else if(type.contains("String"))
        {
            return 3;
        }
        else if(type.contains("Boolean"))
        {
            return 4;
        }
        else if(type.contains("Double"))
        {
            return 5;
        }
        else if(type.contains("Long"))
        {
            return 6;
        }
        else if(type.contains("Character"))
        {
            return 7;
        }
        else
            return 0;
    }
    public static byte[] generate_ubyteArray(String method, View view, Object...params) throws IOException {
        byte[] utoByteArray = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeInt(view.getId());
        dataOutputStream.writeBoolean(true);
        //dataOutputStream.writeInt(view.getId());
        dataOutputStream.writeUTF(method);

        for(int i = 0; i< params.length; i++){
            Object param = params[i];
            int flag = setTypeFlag(param);
            dataOutputStream.writeInt(flag);
            switch(flag)
            {
                case 1:
                    dataOutputStream.writeFloat((Float)param);
                    break;
                case 2:
                    dataOutputStream.writeInt((int)param);
                    break;
                case 3:
                    dataOutputStream.writeUTF((String)param);
                    break;
                case 4:
                    dataOutputStream.writeBoolean((Boolean) param);
                    break;
                case 5:
                    dataOutputStream.writeDouble((Double)param);
                    break;
                case 6:
                    dataOutputStream.writeLong((Long)param);
                    break;
                case 7:
                    dataOutputStream.writeChar((Character)param);
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
        byte[] dtoByteArray=null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(view.getId());
        //Log.d("TAG",""+view.getId());
        dataOutputStream.writeBoolean(false);


        if(widgetType.contains("EditText")){

            EditText edit = (EditText) view;
            dataOutputStream.writeUTF(widgetType);
            //dataOutputStream.writeInt(edit.getId());
            dataOutputStream.writeUTF(edit.getText().toString());

            dataOutputStream.writeFloat(edit.getTextSize());
            dataOutputStream.flush();
            dtoByteArray = byteArrayOutputStream.toByteArray();

        }else if(widgetType.contains("Button")){
            Button btn = (Button) view;
            dataOutputStream.writeUTF(widgetType);
            //dataOutputStream.writeInt(btn.getId());
            dataOutputStream.writeUTF(btn.getText().toString());
            dataOutputStream.writeInt(btn.getHeight());
            dataOutputStream.writeInt(btn.getWidth());
            dataOutputStream.flush();
            dtoByteArray = byteArrayOutputStream.toByteArray();
        }
        if(widgetType.contains("TextView")){

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
    public static String getTS()
    {
        Long tsLong = System.nanoTime();
        String ts = tsLong.toString();
        return ts;
    }
}