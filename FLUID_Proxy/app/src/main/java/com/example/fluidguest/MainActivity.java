package com.example.fluidguest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
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
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FLUID(FLUIDGuest)";

    private int port = 5673;
    private String ip = "192.168.0.11";
    private int maxBufferSize = 1024;

    private Handler mHandler = new Handler();
    //private Handler updateHandler = new Handler();
    private Handler workerHandler;
    public Socket socket;
    private LinearLayout container;
    private ArrayList<TextView> UI_List = new ArrayList<TextView>();

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
                        Log.d(TAG,"received message : "+ getTS());
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
                            }else if (WidgetType.contains("TextView")) {
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
                                                //Log.d("TAG", "before find method");
                                                Method m=null;
                                                switch(flag)
                                                {

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
                                                        }
                                                        catch(Exception e)
                                                        {
                                                            m = cls.getMethod(method,CharSequence.class);
                                                        }
                                                        finally {
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
    public static String getTS()
    {
        Long tsLong = System.nanoTime();
        String ts = tsLong.toString();
        return ts;
    }
}