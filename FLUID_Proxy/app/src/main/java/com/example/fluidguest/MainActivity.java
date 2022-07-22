package com.example.fluidguest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fluidguest.R;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FLUID(FLUIDGuest)";

    private int port = 5673;
    private String ip = "192.168.0.18";
    private int maxBufferSize = 1024;

    private Handler mHandler = new Handler();
    //private Handler updateHandler = new Handler();
    private Handler workerHandler;
    public Socket socket;
    private LinearLayout container;
    private Handler sendHandler;
    private ArrayList<TextView> UI_List = new ArrayList<TextView>();
    private Map<Integer,Object> listTextListener = new HashMap<Integer,Object>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //hi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = (LinearLayout) findViewById(R.id.layout);

        ClientThread socketthread = new ClientThread();
        socketthread.start();

        WorkerThread worker = null;
        try {
            worker = new WorkerThread();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        worker.start();
        SendThread sendThread = new SendThread();
        sendThread.start();
    }

    class ClientThread extends Thread {
        private static final int MAX_BUFFER = 1024;

        @Override
        public void run() {
            try {
                //Log.d("TAG", "before make socket");
                socket = new Socket(ip, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (socket != null) {
                try {
                    InputStream is = socket.getInputStream();
                    byte[] input = new byte[MAX_BUFFER];
                    if (is.read(input) != 0) {
                        Log.d(TAG, "received message : " + getTS());
                        Message msg = Message.obtain();
                        msg.obj = input;

                        workerHandler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class SendThread extends Thread {
        ObjectOutputStream objectOutputStream;

        public SendThread() {

        }

        public void run() {
            while (socket == null) ;
            try {
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                Log.d(TAG, "output stream set");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Looper.prepare();
            sendHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    while (objectOutputStream == null) ;
                    Log.d(TAG, "looper running");
                    Bundle bundle = (Bundle) msg.obj;
                    try {
                        Log.d(TAG, "About to send Motion to Manager");
                        //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        Parcel parcel = Parcel.obtain();
                        bundle.writeToParcel(parcel, 0);
                        byte[] buffer = parcel.marshall();
                        parcel.recycle();
                        //OutputStream outputStream = socket.getOutputStream();

                        //objectOutputStream.writeInt(buffer.length);
                        objectOutputStream.writeInt(msg.arg1);
//                        if(msg.arg1 == 2)
//                            objectOutputStream.writeInt(msg.arg2);
                        objectOutputStream.writeObject(buffer);
                        Log.d(TAG, "Sent Motion to Manager");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            Looper.loop();

        }
    }

    class WorkerThread extends Thread {
        private Socket socket;
        private Method methods[];
        private Class cls;



        public WorkerThread() throws ClassNotFoundException {
            cls = Class.forName("android.widget.TextView");
            methods = cls.getDeclaredMethods();
        }

        public void run() {
            Looper.prepare();

            workerHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    byte[] input = (byte[]) msg.obj;
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input);
                    DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                    try {

                        int id = dataInputStream.readInt();
                        //Log.d("TAG", "" + id);
                        boolean isUpdate = dataInputStream.readBoolean();
                        //Log.d("TAG", "" + isUpdate);
                        if (!isUpdate) //distribute
                        {
                            //Log.d("TAG", "distribute mode");
                            String WidgetType = dataInputStream.readUTF();
                            if (WidgetType.contains("Button")) {
                                //Log.d("TAG", "this is button");
                                //int id = dataInputStream.readInt();
                                String text = dataInputStream.readUTF();
                                int height = dataInputStream.readInt();
                                int width = dataInputStream.readInt();


                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        createButton(id, text, height, width);

                                    }
                                });

                            } else if (WidgetType.contains("EditText")) {
                                //Log.d("TAG", "this is edittext");
                                //int id = dataInputStream.readInt();

                                String text = dataInputStream.readUTF();
                                float textsize = dataInputStream.readFloat();
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        createEditText(id, text, textsize);
                                    }
                                });
                            } else if (WidgetType.contains("TextView")) {
                                //Log.d("TAG", "this is edittext");
                                //int id = dataInputStream.readInt();

                                String text = dataInputStream.readUTF();
                                float textsize = dataInputStream.readFloat();
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        createTextView(id, text, textsize);
                                    }
                                });
                            }
                        } else //isupdate
                        {
                            //int id = dataInputStream.readInt();
                            //Log.d("TAG", "update mode");
                            String method = dataInputStream.readUTF();//set Text

                            int flag = dataInputStream.readInt();
                            Object param = getParam(flag, dataInputStream);

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //Log.d("TAG", "inside run");
                                        for (int i = 0; i < UI_List.size(); i++) {

                                            //Log.d("TAG", "inside loop id : " + id + "          get id : " + UI_List.get(i).getId());
                                            if (id == (long) UI_List.get(i).getId()) {
                                                if (method.equals("setText")) {
                                                    if (!UI_List.get(i).getText().toString().equals((String) param)) {
                                                        //UI_List.get(i).removeTextChangedListener((TextWatcher) listTextListener.get(id));
                                                        //listTextListener.remove(id);
                                                        UI_List.get(i).setText((String) param);
                                                        //textwatcher watcher = new textwatcher(UI_List.get(i));
                                                        //UI_List.get(i).addTextChangedListener((TextWatcher) listTextListener.get(id));
                                                        //listTextListener.put(id,watcher);


                                                    } else {
                                                        break;
                                                    }
                                                }
                                                //Log.d("TAG", "before find method");
                                                Method m = null;
                                                switch (flag) {

                                                    case 1:
                                                        m = cls.getMethod(method, float.class);
                                                        //Log.d("TAG", "found method : " + m.toString());
                                                        m.invoke(UI_List.get(i), param); // parameter
                                                        break;
                                                    case 2:
                                                        m = cls.getMethod(method, int.class);
                                                        //Log.d("TAG", "found method : " + m.toString());
                                                        m.invoke(UI_List.get(i), param); // parameter
                                                        break;
                                                    case 3:
                                                        try {
                                                            m = cls.getMethod(method, String.class);
                                                        } catch (Exception e) {
                                                            m = cls.getMethod(method, CharSequence.class);
                                                        } finally {
                                                            //Log.d("TAG", "found method : " + m.toString());
                                                            m.invoke(UI_List.get(i), param); // parameter
                                                        }

                                                        break;
                                                    case 4:
                                                        m = cls.getMethod(method, boolean.class);
                                                        //Log.d("TAG", "found method : " + m.toString());
                                                        m.invoke(UI_List.get(i), param); // parameter
                                                        break;
                                                    case 5:
                                                        //Double
                                                        m = cls.getMethod(method, float.class);
                                                        //Log.d("TAG", "found method : " + m.toString());
                                                        m.invoke(UI_List.get(i), Float.parseFloat(param.toString())); // parameter
                                                        break;
                                                    case 6:
                                                        m = cls.getMethod(method, long.class);
                                                        //Log.d("TAG", "found method : " + m.toString());
                                                        m.invoke(UI_List.get(i), param); // parameter
                                                        break;
                                                    case 7:
                                                        m = cls.getMethod(method, char.class);
                                                        //Log.d("TAG", "found method : " + m.toString());
                                                        m.invoke(UI_List.get(i), param); // parameter
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            } else {
                                                //Log.d("TAG", "id랑 getid가 다름");


                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                }
                            });

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            Looper.loop();
        }
    }

    public void createEditText(long id, String text, float textsize) {

        EditText edit = new EditText(this);
        UI_List.add(edit);
        edit.setId((int) id);
        edit.setText(text);
        edit.setTextSize(pxToDp(textsize));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        edit.setLayoutParams(lp);
        container.addView(edit);
        edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "EditText touch");
                //motionEvent.setLocation(motionEvent.getX()/view.getWidth(),motionEvent.getY()/view.getHeight());
                Bundle bundle = new Bundle();
                bundle.putInt("ID", view.getId());

                bundle.putParcelable("motionevent", motionEvent);
                bundle.putFloat("x", motionEvent.getX());
                bundle.putFloat("y", motionEvent.getY());
                bundle.putFloat("width", view.getWidth());
                bundle.putFloat("height", view.getHeight());
                Message message = Message.obtain();
                message.obj = bundle;
                message.arg1 = 1;
                sendHandler.sendMessage(message);
                return false;
            }
        });
        textwatcher watcher = new textwatcher(edit);
        edit.addTextChangedListener(watcher);
        listTextListener.put(edit.getId(),watcher);
//        edit.addTextChangedListener(new TextWatcher() {
//            String beforeText;
//
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.d(TAG, "before carSequence : " + charSequence);
//                Log.d(TAG, "before getText : " + edit.getText().toString());
//                beforeText = edit.getText().toString();
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                Log.d(TAG, "on carSequence : " + charSequence);
//                Log.d(TAG, "on getText : " + edit.getText().toString());
//                Log.d(TAG, "equal? : " + edit.getText().toString().equals(charSequence.toString()));
//                if (!edit.getText().toString().equals(beforeText)) {
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("ID", edit.getId());
//                    bundle.putCharSequence("text", charSequence);
//                    Message message = Message.obtain();
//                    message.obj = bundle;
//                    message.arg1 = 2;
//                    sendHandler.sendMessage(message);
//                }
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (beforeText.equals(edit.getText().toString())) {
//                    edit.setSelection(beforeText.length());
//                }
//            }
//        });
    }

    public void createTextView(long id, String text, float textsize) {

        TextView textV = new TextView(this);
        UI_List.add(textV);
        textV.setId((int) id);
        textV.setText((CharSequence) text);
        textV.setTextSize(pxToDp(textsize));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textV.setLayoutParams(lp);
        container.addView(textV);
        textV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "TextView touch");
                //motionEvent.setLocation(motionEvent.getX()/view.getWidth(),motionEvent.getY()/view.getHeight());
                Bundle bundle = new Bundle();
                bundle.putInt("ID", view.getId());
                bundle.putParcelable("motionevent", motionEvent);
                Message message = Message.obtain();
                bundle.putFloat("x", motionEvent.getX());
                bundle.putFloat("y", motionEvent.getY());
                bundle.putFloat("width", view.getWidth());
                bundle.putFloat("height", view.getHeight());
                message.obj = bundle;
                message.arg1 = 1;
                sendHandler.sendMessage(message);
                return true;
            }
        });
        textwatcher watcher = new textwatcher(textV);
        textV.addTextChangedListener(watcher);
        listTextListener.put(textV.getId(),watcher);
//        textV.addTextChangedListener(new TextWatcher() {
//            String beforeText;
//
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.d(TAG, "before carSequence : " + charSequence);
//                Log.d(TAG, "before getText : " + textV.getText().toString());
//                beforeText = textV.getText().toString();
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.d(TAG, "on carSequence : " + charSequence);
//                Log.d(TAG, "on getText : " + textV.getText().toString());
//                Log.d(TAG, "equal? : " + textV.getText().toString().equals(charSequence.toString()));
//                if (!textV.getText().toString().equals(beforeText)) {
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("ID", textV.getId());
//                    bundle.putCharSequence("text", charSequence);
//                    Message message = Message.obtain();
//                    message.obj = bundle;
//                    message.arg1 = 2;
//                    sendHandler.sendMessage(message);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

    }

    public void createButton(long id, String text, long height, long width) {

        Button btn = new Button(this);
        UI_List.add(btn);
        btn.setText(text);
        btn.setId((int) id);
        btn.setHeight((int) height);
        btn.setWidth((int) width);


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btn.setLayoutParams(lp);
        container.addView(btn);

        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "Button touch");
                Log.d(TAG, "button width : " + view.getWidth());
                Log.d(TAG, "button height : " + view.getHeight());
                Log.d(TAG, "button getX : " + view.getX());
                Log.d(TAG, "button getY : " + view.getY());
                Log.d(TAG, "event getX : " + motionEvent.getX());
                Log.d(TAG, "event getY : " + motionEvent.getY());
                //motionEvent.setLocation(motionEvent.getX()/view.getWidth(),motionEvent.getY()/view.getHeight());
                Log.d(TAG, "after event getX : " + motionEvent.getX());
                Log.d(TAG, "after event getY : " + motionEvent.getY());
                Bundle bundle = new Bundle();
                bundle.putInt("ID", view.getId());
                bundle.putParcelable("motionevent", motionEvent);
                Message message = Message.obtain();
                bundle.putFloat("x", motionEvent.getX());
                bundle.putFloat("y", motionEvent.getY());
                bundle.putFloat("width", view.getWidth());
                bundle.putFloat("height", view.getHeight());
                message.obj = bundle;
                message.arg1 = 1;

                sendHandler.sendMessage(message);
                return true;
            }
        });

    }

    public int pxToDp(float px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public Object getParam(int flag, DataInputStream dis) throws IOException {
        //Log.d("TAG",""+flag);
        switch (flag) {
            case 1:
                return dis.readFloat();
            case 2:
                return dis.readInt();
            case 3:
                return dis.readUTF();
            case 4:
                return dis.readBoolean();
            case 5:
                return dis.readDouble();
            case 6:
                return dis.readLong();
            case 7:
                return dis.readChar();
            default:

                //Log.d("TAG", "invalid param");
                return null;
        }
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
            Log.d(TAG, "on carSequence : " + charSequence);
            Log.d(TAG, "on getText : " + textV.getText().toString());
            Log.d(TAG, "equal? : " + textV.getText().toString().equals(charSequence.toString()));
            Log.wtf(TAG,"call stack",new Throwable("get stacks"));
            if (!textV.getText().toString().equals(beforeText)) {
                Bundle bundle = new Bundle();
                bundle.putInt("ID", textV.getId());
                bundle.putCharSequence("text", charSequence);
                Message message = Message.obtain();
                message.obj = bundle;
                message.arg1 = 2;
                sendHandler.sendMessage(message);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}