package com.hmsl.fluidlib;

import static android.os.SystemClock.sleep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.constraintlayout.widget.ConstraintSet;

import com.hmsl.fluidmanager.IFLUIDService;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
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
    private ArrayList<Layout_Tree> layout_trees = new ArrayList<>();
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

                    view.dispatchTouchEvent(motionEvent);

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

    public void runDistribute(String widgetType, View view) {
        Bundle bundle = new Bundle();

        try {
            //Log.d("TAG","run test");
            byte[] layout = generate_lbyteArray(view);
            byte[] widget = generate_dbyteArray(widgetType, view);

            Log.d(TAG,"runDistribute : "+widgetType);
            bundle.putByteArray("layout", layout);
            bundle.putByteArray("widget", widget);
            Log.d(TAG, "runDistribute send : " + getTS());
            if(widgetType.contains("TextView") || widgetType.contains("EditText"))
            {
                Log.d(TAG, "runDistribute add Listener : " + getTS());
                TextView textView = (TextView) view;
                textwatcher watcher = new textwatcher(textView);
                textView.addTextChangedListener(watcher);
                listTextListener.put(textView.getId(),watcher);
            }
            mRemoteService.distribute(bundle);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void runUpdateTest(String signature, View view,Object param)
    {
        Bundle bundle = new Bundle();
        if(signature.contains("setTextSize"))
        {
            param = convertPixelsToDpFloat((float)param, instance.mContext);
        }
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
            Log.d(TAG,"method : "+method);
            if (method.contains("setTextSize")){
                convertPixelsToDpFloat((float)params[0], instance.mContext);
            }
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
        dataOutputStream.writeInt(1); //1 for update
        //dataOutputStream.writeInt(view.getId());
        int size = method.getBytes(StandardCharsets.UTF_8).length;
        dataOutputStream.writeInt(size);
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
                    int length = ((String) param).getBytes(StandardCharsets.UTF_8).length;
                    dataOutputStream.writeInt(length);
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
    public static byte[] generate_lbyteArray(View view) throws IOException {
        byte[] dtoByteArray = null;
        int size;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        ViewGroup layout = (ViewGroup) view.getParent();
        if(layout.getClass().toString().contains("LinearLayout"))
        {
            LinearLayout linearLayout = (LinearLayout) layout;
            int layout_type = 0; //0 stands for linear layout
            int orientation = linearLayout.getOrientation();
            Log.d(TAG,"orientation : "+orientation);
            int width = convertPixelsToDpInt(linearLayout.getWidth(), instance.mContext);
            int height = convertPixelsToDpInt(linearLayout.getHeight(),instance.mContext);
            dataOutputStream.writeInt(layout.getId());
            dataOutputStream.writeInt(2); //2 for layout
            dataOutputStream.writeInt(layout_type);
            dataOutputStream.writeInt(orientation);
            dataOutputStream.writeInt(width);
            dataOutputStream.writeInt(height);
        }
        else
        {
            int numWidget = layout.getChildCount();
            int layout_type = 1;
            int width = convertPixelsToDpInt(layout.getWidth(), instance.mContext);
            int height = convertPixelsToDpInt(layout.getHeight(), instance.mContext);
            boolean isExist = false;
            for(int i = 0; i < instance.layout_trees.size(); i++)
            {
                if(instance.layout_trees.get(i).Layout_ID == layout.getId())
                {
                    isExist = true;
                }
            }
            if(isExist == false) {
                Layout_Tree layout_tree = new Layout_Tree();
                layout_tree.Layout_ID = layout.getId();
                for (int i = 0; i < numWidget; i++) {
                    View child = layout.getChildAt(i);
                    layout_tree.Widget_List.add(new Widget_Location(child.getId(), child.getX(), child.getY()));
                }
                instance.layout_trees.add(layout_tree);
                dataOutputStream.writeInt(layout.getId());
                dataOutputStream.writeInt(2);
                dataOutputStream.writeInt(layout_type);
                dataOutputStream.writeInt(width);
                dataOutputStream.writeInt(height);
            }
        }

        dataOutputStream.flush();
        dtoByteArray = byteArrayOutputStream.toByteArray();
        return dtoByteArray;
    }
    public static byte[] generate_dbyteArray(String widgetType, View view) throws IOException {
        byte[] dtoByteArray = null;
        int size;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(view.getId());
        Log.d(TAG,"view.getX : "+view.getX());
        Log.d(TAG,"view.getY : "+view.getY());
        //Log.d("TAG",""+view.getId());
        dataOutputStream.writeInt(0); //0 for distribute
        dataOutputStream.writeInt(((ViewGroup)view.getParent()).getId());
        //added
        float X=0;
        float Y=0;
        for (int i = 0; i< instance.layout_trees.size(); i++)
        {
            Layout_Tree layout_tree= instance.layout_trees.get(i);
            if(layout_tree.Layout_ID == ((ViewGroup) view.getParent()).getId())
            {
                for (int j = 0; j<layout_tree.Widget_List.size(); j++)
                {
                    Widget_Location location = layout_tree.Widget_List.get(j);
                    if(location.Widget_ID == view.getId())
                    {
                        X=location.Widget_X;
                        Y=location.Widget_Y;
                    }
                }
            }
        }
        dataOutputStream.writeFloat(convertPixelsToDpFloat(X, instance.mContext));
        dataOutputStream.writeFloat(convertPixelsToDpFloat(Y, instance.mContext));

//        Log.d(TAG,"layout.get"+layout.getClass());
        if (widgetType.contains("EditText")) {

            EditText edit = (EditText) view;
            size = widgetType.getBytes(StandardCharsets.UTF_8).length;
            dataOutputStream.writeInt(size);
            dataOutputStream.writeUTF(widgetType);
            //dataOutputStream.writeInt(edit.getId());

            size = edit.getText().toString().getBytes(StandardCharsets.UTF_8).length;
            dataOutputStream.writeInt(size);
            dataOutputStream.writeUTF(edit.getText().toString());
            dataOutputStream.writeFloat(convertPixelsToDpFloat(edit.getTextSize(), instance.mContext));
            dataOutputStream.flush();
            dtoByteArray = byteArrayOutputStream.toByteArray();

        } else if (widgetType.contains("Button")) {
            Button btn = (Button) view;
            size = widgetType.getBytes(StandardCharsets.UTF_8).length;
            dataOutputStream.writeInt(size);
            dataOutputStream.writeUTF(widgetType);
            //dataOutputStream.writeInt(btn.getId());
            size = btn.getText().toString().getBytes(StandardCharsets.UTF_8).length;
            dataOutputStream.writeInt(size);
            dataOutputStream.writeUTF(btn.getText().toString());
            dataOutputStream.writeInt( convertPixelsToDpInt(btn.getHeight(), instance.mContext));
            dataOutputStream.writeInt( convertPixelsToDpInt(btn.getWidth(), instance.mContext));

//            dataOutputStream.writeInt( btn.getHeight());
//            dataOutputStream.writeInt( btn.getWidth());
            dataOutputStream.flush();
            dtoByteArray = byteArrayOutputStream.toByteArray();
        }
        if (widgetType.contains("TextView")) {

            TextView edit = (TextView) view;
            size = widgetType.getBytes(StandardCharsets.UTF_8).length;
            dataOutputStream.writeInt(size);
            dataOutputStream.writeUTF(widgetType);
            //dataOutputStream.writeInt(edit.getId());
            size = edit.getText().toString().getBytes(StandardCharsets.UTF_8).length;
            dataOutputStream.writeInt(size);
            dataOutputStream.writeUTF(edit.getText().toString());

            dataOutputStream.writeFloat( convertPixelsToDpFloat(edit.getTextSize(), instance.mContext));
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

    public static int convertDpToPixel(int dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = dp * ((int)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static int convertPixelsToDpInt(int px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = px / ((int)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
    public static float convertPixelsToDpFloat(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
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
class Layout_Tree {
    int Layout_ID;
    ArrayList<Widget_Location> Widget_List = new ArrayList<Widget_Location>();

}
class Widget_Location {
    int Widget_ID;
    float Widget_X;
    float Widget_Y;
    public Widget_Location(int ID, float X, float Y) {
        this.Widget_ID = ID;
        this.Widget_X = X;
        this.Widget_Y = Y;
    }
}