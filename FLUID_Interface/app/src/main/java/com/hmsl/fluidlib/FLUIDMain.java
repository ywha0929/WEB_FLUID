package com.hmsl.fluidlib;

import static android.os.SystemClock.sleep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;

import com.hmsl.fluidmanager.IFLUIDService;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
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
    static int isChooseMode = 0;
    static int isChooseModeWait = 0;
    private ArrayList<View> distributeList = new ArrayList<>();
    private ArrayList<Layout_Tree> layout_trees = new ArrayList<>();
    private Map<Integer,Object> listTextListener = new HashMap<Integer,Object>();
    private Map<Integer,Object> listBorder = new HashMap<>();
    private final IBinder mBinder = new IReverseConnection.Stub() {
        @Override
        public void doCheck(String msg) throws RemoteException {
            Log.d(TAG, "this is doCheck");
            Activity activity = (Activity) mContext;
            activity.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    Toast toast = Toast.makeText(mContext.getApplicationContext(), msg, Toast.LENGTH_SHORT);
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
        distributeList.clear();
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

    private View findViewAt(ViewGroup viewGroup, float x, float y) {
        for(int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                View foundView = findViewAt((ViewGroup) child, x, y);
                if (foundView != null && foundView.isShown()) {
                    return foundView;
                }
            } else {
                int[] location = new int[2];
                child.getLocationOnScreen(location);
                Rect rect = new Rect(location[0], location[1], location[0] + child.getWidth(), location[1] + child.getHeight());
                if (rect.contains((int)x, (int)y)) {
                    return child;
                }
            }
        }

        return null;
    }
    private String getViewType(View view)
    {
        Class EditText = EditText.class;
        Class Button = Button.class;
        Class TextView = TextView.class;

        Class ImageView = ImageView.class;
        String classType="";

        if(view.getClass().toString().contains("android"))
        {
            String thisType = view.getClass().toString();
            if(ImageView.isInstance(view))
            {
                classType = ImageView.toString();
            }
            else if( !(thisType.equals(EditText.toString()) || thisType.equals(Button.toString()) || thisType.equals(TextView.toString())) )
            {
                //treat unsupported TextView child view as custom view
                classType = "OtherView";
            }
            else
            {
                classType = thisType;
            }
        }
        else
        {
            if(EditText.isInstance(view))
            {
                classType = EditText.toString();
            }
            else if(Button.isInstance(view))
            {
                classType = Button.toString();
            }
            else if(TextView.isInstance(view))
            {
                classType = TextView.toString();
            }
            else if(ImageView.isInstance(view))
            {
                classType = ImageView.toString();
            }
            else
            {
                classType = "OtherView";
            }
        }
//
        return classType;
    }
    public int runTouchCheck(MotionEvent e) {
        //if return true, mainActivity will pass the event to view
        //if return false, mainActivity will not pass the event to view
        Log.d(TAG,"This is runTouchCheck"+e.getDownTime());
        Log.d(TAG,"This is motionEvent.getPointerCount, Action : "+e.getPointerCount()+", "+e.getAction());
        long Action = e.getAction();
        long filtered_Action = Action & 111;
        if(isChooseMode == 0) { //current mode is normal mode

            if (e.getPointerCount() == 3 && filtered_Action == MotionEvent.ACTION_POINTER_DOWN ){ //switch to choose mode
                Log.d(TAG,"wait change to choose mode");
                isChooseModeWait = 1;
                return 1;
            }
            else if(isChooseModeWait == 1 &&Action == MotionEvent.ACTION_UP)
            {
                Log.d(TAG,"change to choose mode");
                isChooseModeWait = 0;
                isChooseMode = 1;
                Activity activity = (Activity) mContext;
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(mContext.getApplicationContext(), "Choose Mode", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                return 1;
            }
            else { //normal mode
                Log.d(TAG,"normal mode");
                return 1;
            }
        }
        else{ //current mode is chooseMode
            if(e.getPointerCount() == 3 && filtered_Action == MotionEvent.ACTION_POINTER_DOWN) //switch to normal mode
            {
                Log.d(TAG, "distribute and switch to normal mode");
                for(int i = 0; i<distributeList.size(); i++)
                {
                    View thisView = distributeList.get(i);
                    thisView.setForeground((Drawable) listBorder.get(thisView.getId()));
//                    ViewGroup thisBorder = listBackground.get(thisView.getId());
//                    WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//                    windowManager.removeView(thisBorder);
                    Log.d(TAG,"Distributing ["+i+"]th Widget");
                    //todo
//                    runDistribute(thisView.getClass().toString(),thisView);
                    runDistribute(getViewType(thisView),thisView);

                }
                isChooseMode = 0;
                distributeList.clear();
                Activity activity = (Activity) mContext;
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(mContext.getApplicationContext(), "Normal Mode", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                return 0;
            }
            else if(e.getAction() == MotionEvent.ACTION_UP) { //choose

                Log.d(TAG,"choose mode");
                Activity activity = (Activity) mContext;
                ViewGroup rootLayout = (ViewGroup) activity.getWindow().getDecorView().getRootView();
                View targetView = findViewAt(rootLayout,e.getX(),e.getY());

                if(targetView != null)
                {
                    if(!distributeList.contains(targetView))
                    {
                        distributeList.add(targetView);
                        Drawable origin = targetView.getBackground();
                        GradientDrawable newBorder = new GradientDrawable();
                        if(origin != null)
                        {
                            newBorder.setColorFilter(origin.getColorFilter());
                            newBorder.setAlpha(origin.getAlpha());
                        }

                        newBorder.setStroke(20,Color.RED);
                        newBorder.setColor(0);
                        listBorder.put(targetView.getId(),targetView.getForeground());
                        targetView.setForeground(newBorder);
//                        int[] location = new int[2];
//                        targetView.getLocationOnScreen(location);
//                        //create layout for box
//                        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                                targetView.getWidth(),
//                                targetView.getHeight(),
//                                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                                |WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                                |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
//                                PixelFormat.TRANSLUCENT
//                            );
//                        params.gravity = Gravity.LEFT | Gravity.TOP;
//                        params.x = location[0];
//                        params.y = location[1];
//                        LinearLayout overlayBoundary = new LinearLayout((Activity)instance.mContext);
//                        Log.d(TAG,"layout creation : "+overlayBoundary);
//                        GradientDrawable border = new GradientDrawable();
//                        border.setStroke(10,Color.RED);
//                        border.setColor(0); //transparent
//                        overlayBoundary.setBackground(border);
//
//                        WindowManager windowManager = (WindowManager) instance.mContext.getSystemService(Context.WINDOW_SERVICE);
//                        Log.d(TAG, "windowManager : "+windowManager);
//                        Log.d(TAG, "params : " + params);
//
//                        windowManager.addView(overlayBoundary,params);
//                        listBackground.put(targetView.getId(),overlayBoundary);


//                    Canvas canvas = new Canvas();
//                    Paint paint = new Paint();
//                    paint.setColor(Color.RED);
//                    int[] location = new int[2];
//                    targetView.getL3..[ocationOnScreen(location);
//                    Rect rect = new Rect(location[0], location[1], location[0] + targetView.getWidth(), location[1] + targetView.getHeight());
//                    canvas.drawRect(rect,paint);

                    }

                }
                return 0;

            }
            else
            {
                return 0;
            }

        }
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

    @SuppressLint("ResourceType")
    public void runDistribute(String widgetType, View view) {
        Bundle bundle = new Bundle();

        try {
            if(view.getId() == -1)
            {
                view.setId(View.generateViewId());
            }
            View parent = (View) view.getParent();
            if(parent.getId() == -1)
            {
                parent.setId(View.generateViewId());
            }
            Log.d(TAG, "runDistribute : ID, Type : " + view.getId()+", " +view.getClass().toString());
            Log.d(TAG, "runDistribute : widgetType : "+widgetType);
            byte[] layout = generate_lbyteArray(view);
            byte[] widget = generate_dbyteArray(widgetType, view);
            Log.d(TAG,"layout bytearray length : "+layout.length);
            Log.d(TAG,"widget bytearray length : "+widget.length);
//            Log.d(TAG,"runDistribute : "+widgetType);
            bundle.putByteArray("layout", layout);
            bundle.putByteArray("widget", widget);
            Log.d(TAG, "runDistribute send to service: " + getTS());
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
        if(method.contains("setImage")){
            ImageView imview = (ImageView) view;
            //typrflag 3으로
            //stringsize
            //bitmap
            //convert ImageView to bitmap
            BitmapDrawable drawable = (BitmapDrawable) imview.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            //bitmap to byte
            ByteArrayOutputStream bitmapOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bitmapOutputStream);
            byte[] byteArray = bitmapOutputStream.toByteArray();

            //length of byte array
            String encoded = Base64.getEncoder().encodeToString(byteArray);
            int byteLength = encoded.getBytes(StandardCharsets.UTF_8).length;

            dataOutputStream.writeInt(3);
            dataOutputStream.writeInt(byteLength);
            dataOutputStream.writeUTF(encoded);
        }
        else
        {
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
        }


        dataOutputStream.flush();
        utoByteArray = byteArrayOutputStream.toByteArray();
        return utoByteArray;
    }
    public static Bitmap loadBitmapFromView(View view)
    {
        Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        view.layout(0,0, view.getWidth(),view.getHeight());
        view.draw(c);
        return b;
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
                    if(child.getId() == -1)
                    {
                        child.setId(View.generateViewId());
                    }
                    layout_tree.Widget_List.add(new Widget_Location(child.getId(), child.getX(), child.getY()));
                }
                instance.layout_trees.add(layout_tree);
                dataOutputStream.writeInt(layout.getId());
                dataOutputStream.writeInt(2);
                dataOutputStream.writeInt(layout_type);
                dataOutputStream.writeInt(width);
                dataOutputStream.writeInt(height);
//                dataOutputStream.writeFloat(layout.getX());
//                dataOutputStream.writeFloat(layout.getY());
                dataOutputStream.writeFloat(convertPixelsToDpFloat(layout.getX(), instance.mContext));
                dataOutputStream.writeFloat(convertPixelsToDpFloat(layout.getY(), instance.mContext));
            }
        }

        dataOutputStream.flush();
        dtoByteArray = byteArrayOutputStream.toByteArray();
        return dtoByteArray;
    }
    public static byte[] generate_dbyteArray(String widgetType, View view) throws IOException, XmlPullParserException {
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
            dataOutputStream.writeInt(convertPixelsToDpInt(edit.getHeight(),instance.mContext));
            dataOutputStream.writeInt(convertPixelsToDpInt(edit.getWidth(), instance.mContext));
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
            dataOutputStream.writeInt(convertPixelsToDpInt(edit.getHeight(),instance.mContext));
            dataOutputStream.writeInt(convertPixelsToDpInt(edit.getWidth(), instance.mContext));
            dataOutputStream.flush();
            dtoByteArray = byteArrayOutputStream.toByteArray();

        }
        else if (widgetType.contains("ImageView")) {
//            Log.d(TAG, "generate_byteArray: this is image view///////");
            ////////////////////////////////////////////////////////////image///////////////////////////////////////////////////////
            ImageView image = (ImageView) view;
            size = widgetType.getBytes(StandardCharsets.UTF_8).length;
            dataOutputStream.writeInt(size);
            dataOutputStream.writeUTF(widgetType);


            //convert ImageView to bitmap
            Bitmap bitmap = loadBitmapFromView(image);
//            Drawable drawable = image.getDrawable();
//            BitmapDrawable Bitmapdrawable;
//            if(drawable.getClass() == VectorDrawable.class)
//            {
//                Drawable newDrawable = new Drawable() {
//                    @Override
//                    public void draw(@NonNull Canvas canvas) {
//                        Paint paint = new Paint();
//                        paint.setStyle(Paint.Style.FILL);
//                        canvas.drawCircle(image.getWidth()/2, image.getHeight()/2 , image.getWidth()/2, paint);
//                    }
//
//                    @Override
//                    public void setAlpha(int alpha) {
//
//                    }
//
//                    @Override
//                    public void setColorFilter(@Nullable ColorFilter colorFilter) {
//
//                    }
//
//                    @Override
//                    public int getOpacity() {
//                        return PixelFormat.UNKNOWN;
//                    }
//                };
//
//                Bitmapdrawable = (BitmapDrawable) newDrawable;
//            }
//            else
//            {
//                Bitmapdrawable = (BitmapDrawable) image.getDrawable();
//            }
//            Bitmap bitmap = Bitmapdrawable.getBitmap();

            //bitmap to byte
            ByteArrayOutputStream bitmapOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bitmapOutputStream);
            byte[] byteArray = bitmapOutputStream.toByteArray();

            //length of byte array
            String encoded = Base64.getEncoder().encodeToString(byteArray);
            int byteLength = encoded.getBytes(StandardCharsets.UTF_8).length;
            dataOutputStream.writeInt( convertPixelsToDpInt(image.getHeight(), instance.mContext));
            dataOutputStream.writeInt( convertPixelsToDpInt(image.getWidth(), instance.mContext));
            dataOutputStream.writeInt(byteLength);
            Log.d(TAG,"bitmap length : "+byteLength);
//            Log.d(TAG, "bitmap : \n"+encoded);
            dataOutputStream.writeUTF(encoded);




            dataOutputStream.flush();
            dtoByteArray = byteArrayOutputStream.toByteArray();
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
        else if(widgetType.contains("OtherView"))
        {
            View thisView = (View) view;
            size = widgetType.getBytes(StandardCharsets.UTF_8).length;
            dataOutputStream.writeInt(size);
            dataOutputStream.writeUTF(widgetType);
            //send size
            dataOutputStream.writeInt(convertPixelsToDpInt(view.getHeight(),instance.mContext));
            dataOutputStream.writeInt(convertPixelsToDpInt(view.getWidth(),instance.mContext));
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