package com.hmsl.fluidmanager;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;


//import com.hmsl.fluidlib.IFLUIDService;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hmsl.fluidlib.IReverseConnection;

public class FLUIDManagerService extends Service {
    private static final String TAG = "FLUID(FLUIDManagerService)";
    public static com.hmsl.fluidlib.IReverseConnection mRemoteService = null;
    public ServiceConnection mServiceConnection;
    private final int port = 5673;
    private Handler distributeHandler;
    private Handler updateHandler;
    ServerSocket server;
    Socket socket;

    private ArrayList<Integer> id_list = new ArrayList<>();

    private final IBinder mBinder = new IFLUIDService.Stub() {
        // distribute

        @Override
        public void endOfDistribute() throws RemoteException {
            Log.d(TAG,"endOfDistribute received : "+getTS());
            Message msg = Message.obtain();
            msg.obj = "endOfDistribute";
            distributeHandler.sendMessage(msg);
        }

        public void distribute(Bundle bundle) {
            Log.d(TAG, "test received : " + getTS());
            bundle.setClassLoader(getClass().getClassLoader());

            //Log.d("TAG",""+recvBuffer);
//            Object clazz = bundle.getParcelable("key");
//            PJson pJson = (PJson)clazz;
//            Log.e(TAG, "Trigger 호출 " + pJson.getString());

            // Distribution Trigger 발생 시 Message에 Json String 데이터를 보냄
            Message msg = Message.obtain();
            msg.obj = bundle;
            distributeHandler.sendMessage(msg);

            //Log.e(TAG, "Message 전송");
        }

        // update
        public void update(Bundle bundle) {
            Log.d(TAG, "update received : " + getTS());

            bundle.setClassLoader(getClass().getClassLoader());
            byte[] recvBuffer = bundle.getByteArray("key");
//            Object clazz = bundle.getParcelable("key");
//            PJson pJson = (PJson)clazz;
//            Log.e(TAG, "Update 호출 " + pJson.getString());

            // Update 발생 시 Message에 Json String 데이터를 보냄
            Message msg = Message.obtain();
            msg.obj = recvBuffer;
            updateHandler.sendMessage(msg);
            //Log.e(TAG, "Message 전송");
        }


        public void reverseConnect(Bundle bundle) {
            Log.d(TAG, "this is reverseConnect");
                    mServiceConnection = new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                            mRemoteService = com.hmsl.fluidlib.IReverseConnection.Stub.asInterface(service);
                            Log.d(TAG, "reverse connection connected = " + mRemoteService);

                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {
                            Log.d(TAG, "reverse connection disconnected = " + mRemoteService);
                    mRemoteService = null;

                }
            };
            mServiceConnection.onServiceConnected((ComponentName) bundle.getParcelable("ComponentName"), bundle.getBinder("Binder"));

            try {
//                mRemoteService.doCheck("FLUID Service reverse bind done");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    public FLUIDManagerService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");


        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            ServerThread server = new ServerThread(port);
            server.start();
            SocketInputThread socketInputThread = new SocketInputThread();
            socketInputThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static <T> T bytes2Parcelable(final byte[] bytes,
//                                         final Parcelable.Creator<T> creator) {
//        if (bytes == null) return null;
//        Parcel parcel = Parcel.obtain();
//        parcel.unmarshall(bytes, 0, bytes.length);
//        parcel.setDataPosition(0);
//        parcel.readInt();
//        T result = creator.createFromParcel(parcel);
//        parcel.recycle();
//        return result;
//    }

    class SocketInputThread extends Thread {
        InputStream inputStream;
        ByteArrayInputStream byteArrayInputStream;
        DataInputStream dataInputStream;
        private static final int MAX_BUFFER = 1024;
        int targetID = 0;
        List<Bundle> bundleList = new ArrayList<Bundle>();

        public SocketInputThread() {

        }

        @Override
        public void run() {

            while (socket == null) ;
            try {
                inputStream = socket.getInputStream();

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "input stream set");
            try {
                mRemoteService.doCheck("FLUID Service socket input connected");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            while (true) {
                try {

                    byte[] buffer = new byte[1000];
                    inputStream.read(buffer);
                    byteArrayInputStream = new ByteArrayInputStream(buffer);
                    dataInputStream = new DataInputStream(byteArrayInputStream);
                    int ID = dataInputStream.readInt();
                    int typeEvent = dataInputStream.readInt();
                    Log.d(TAG,"type Event : "+typeEvent+", ID : "+ID);
//                    long ID = Integer.toUnsignedLong(dataInputStream.readInt());
//                    long typeEvent = Integer.toUnsignedLong(dataInputStream.readInt());

                    //buffer = (byte[]) objectInputStream.readObject();
//                    Bundle bundle = bytes2Parcelable(buffer, Bundle.CREATOR);
                    //aMotionEvent motionEvent = bundle.getParcelable("motionevent");
                    //Log.d(TAG,"motion getX : "+motionEvent.getX());
                    //Log.d(TAG,"motion getY : "+motionEvent.getY());
                    //if (typeEvent == 1)
                        //mRemoteService.reverseMotionEvent(bundle);
                    long down_time=0;
                    if(typeEvent == 1)
                    {
                        int up_down = dataInputStream.readInt();
                        if(up_down == 0){
//                            down_time = System.
//                            Log.i(TAG,"down Time : "+down_time);
                            MotionEvent motiondown = MotionEvent.obtain(0,0,MotionEvent.ACTION_DOWN,dataInputStream.readFloat(),dataInputStream.readFloat(),0);
                            Bundle bundledown = new Bundle();
                            bundledown.putParcelable("motionevent",motiondown);
                            bundledown.putInt("ID",ID);
                            mRemoteService.reverseMotionEvent(bundledown);

                        }
                        else{
                            MotionEvent motionup = MotionEvent.obtain(0,0,MotionEvent.ACTION_UP,dataInputStream.readFloat(),dataInputStream.readFloat(),0);
                            Bundle bundleup = new Bundle();
                            bundleup.putInt("ID",ID);
                            bundleup.putParcelable("motionevent",motionup);
                            bundleup.putInt("ID",ID);
                            mRemoteService.reverseMotionEvent(bundleup);
                        }
                    }

                    else if(typeEvent == 2) {
                        int textlength = dataInputStream.readInt();
                        inputStream.read(buffer);
                        byte[] newBuffer = Arrays.copyOfRange(buffer,0,textlength);
                        String text = new String(newBuffer, StandardCharsets.UTF_8);
//                        Log.d(TAG,"input ID : "+ID);
//                        Log.d(TAG, "input typeEvent : "+typeEvent);
                        Log.d(TAG, "input text : "+text);
                        Bundle bundle = new Bundle();
                        bundle.putInt("ID", ID);
                        bundle.putCharSequence("text",text);
                        mRemoteService.reverseKeyboardEvent(bundle);
                    }
                    else
                        Log.e(TAG,"invalid typeEvent num");


                    Log.d(TAG, "read input from guest");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ServerThread : ServerSocket 열기
    class ServerThread extends Thread {

        public ServerThread(int port) throws IOException {
            server = new ServerSocket(port);
        }

        @Override
        public void run() {
            try {
                socket = server.accept();
                Log.e(TAG, "Socket connected");
//                mRemoteService.doCheck("Socket connected");
            } catch (Exception e) {
                e.printStackTrace();
            }

            Looper.prepare();

            // Distribute 담당 handler
            distributeHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    Object obj = msg.obj;
                    if(String.class.isInstance(obj))
                    {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                        try {
                            dataOutputStream.writeInt(0);
                            dataOutputStream.writeInt(3);
                            dataOutputStream.flush();
                            byte[] msgEndOfDistribute = byteArrayOutputStream.toByteArray();
                            ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();

                            byte[] length_byte = new byte[4];
                            int length_int = msgEndOfDistribute.length;
                            Log.d(TAG, "handleMessage: "+length_int);
                            for(int i = 0; i< 4; i++)
                            {
                                int quotient = ((int) Math.floor( length_int / (int)Math.pow(256,3-i) ) );
                                length_byte[i] = (byte) quotient;
                                length_int -= quotient * (int) Math.pow(256,3-i);
                                //Log.d(TAG,""+length_int+" "+(int)Math.pow(16,3-i) + " " + length_int/(int)Math.pow(16,3-i));
                            }
                            byteArrayOutputStream1.write(length_byte);
                            byteArrayOutputStream1.write(msgEndOfDistribute);

                            byte[] output = byteArrayOutputStream1.toByteArray();
                            OutputStream outputStream = socket.getOutputStream();
                            outputStream.write(output);
                            outputStream.flush();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Bundle bundle = (Bundle) msg.obj;

                        //Log.e(TAG, "Message 받음 " + input);
                        try {
                            byte[] layout = bundle.getByteArray("layout");
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(layout);
                            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                            int id = dataInputStream.readInt();
                            Log.d(TAG,"layout ID : "+id);
                            boolean is_distribute = false;
                            if (id_list.size() == 0) id_list.add(id);
                            else {
                                //ID 중복 검사
                                for (int i = 0; i < id_list.size(); i++) {
                                    if (id_list.get(i) == id) {
                                        is_distribute = true;
                                        break;
                                    }
                                }
                            }
                            //ID 중복 x => Distribute 해야 하므로 socket 통신으로 Json 객체를 보낸다.
                            if (!is_distribute) {
                                id_list.add(id);
                                //Log.e(TAG, "전송하려는 Json Object" + input);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                byte[] length_byte = new byte[4];
                                int length_int = layout.length;
                                for(int i = 0; i< 4; i++)
                                {
                                    int quotient = ((int) Math.floor( length_int / (int)Math.pow(256,3-i) ) );
                                    length_byte[i] = (byte) quotient;
                                    length_int -= quotient * (int) Math.pow(256,3-i);
                                    //Log.d(TAG,""+length_int+" "+(int)Math.pow(16,3-i) + " " + length_int/(int)Math.pow(16,3-i));
                                }

                                byteArrayOutputStream.write(length_byte);
                                byteArrayOutputStream.write(layout);
                                byte[] output = byteArrayOutputStream.toByteArray();
                                OutputStream outputStream = socket.getOutputStream();
                                outputStream.write(output);
                                outputStream.flush();
    //                            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
    ////                            OutputStream os = socket.getOutputStream();
    //                            dataOutputStream.writeInt(layout.length);
    //                            dataOutputStream.write(layout);
    //
    //                            dataOutputStream.flush();
                                //os.close();
                                Log.e(TAG, "UI distribute socket msg 전송 성공 : " + getTS());
//                                mRemoteService.doCheck("FLUID Service : distribute layout sent");
                            } else {
                                //Log.e(TAG, "이미 Distribute된 UI 입니다.");
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                        try {
                            byte[] widget = bundle.getByteArray("widget");
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(widget);
                            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                            int id = dataInputStream.readInt();
                            Log.d(TAG,"distribute ID : "+id);
                            //Log.e(TAG, "UI_ID = " + id);
                            //Log.d(TAG,""+dataInputStream.readBoolean());
    //                        JSONParser jsonParser = new JSONParser();
    //                        JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
    //                        long id = (long)jsonObject.get("Id");
    //                        Log.e(TAG, "UI_ID = "+id);

                            boolean is_distribute = false;
                            if (id_list.size() == 0) id_list.add(id);
                            else {
                                //ID 중복 검사
                                for (int i = 0; i < id_list.size(); i++) {
                                    if (id_list.get(i) == id) {
                                        is_distribute = true;
                                        break;
                                    }
                                }
                            }

                            //ID 중복 x => Distribute 해야 하므로 socket 통신으로 Json 객체를 보낸다.
                            if (!is_distribute) {
                                id_list.add(id);
                                //Log.e(TAG, "전송하려는 Json Object" + input);

    //                            BufferedOutputStream os = (BufferedOutputStream) socket.getOutputStream();
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                byte[] length_byte = new byte[4];
                                int length_int = widget.length;
                                for(int i = 0; i< 4; i++)
                                {
                                    int quotient = ((int) Math.floor( length_int / (int)Math.pow(256,3-i) ) );
                                    length_byte[i] = (byte) quotient;
                                    length_int -= quotient * (int) Math.pow(256,3-i);
    //                                Log.d(TAG, "handleMessage: convert" +i);
    //                                Log.d(TAG, "handleMessage: quotient : " + quotient);
    //                                Log.d(TAG, "handleMessage: power : " + (int) Math.pow(256,3-i));
    //                                Log.d(TAG, "handleMessage: length_int" + length_int);
                                }

                                byteArrayOutputStream.write(length_byte);
                                byteArrayOutputStream.write(widget);
                                byte[] output = byteArrayOutputStream.toByteArray();
                                OutputStream outputStream = socket.getOutputStream();
                                outputStream.write(output);
                                outputStream.flush();

                                Log.e(TAG, "UI distribute socket msg 전송 성공 : " + getTS());
//                                mRemoteService.doCheck("FLUID Service : distribute widget sent");
                            } else {
                                //Log.e(TAG, "이미 Distribute된 UI 입니다.");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            // Update 담당 Handler
            updateHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    byte[] input = (byte[]) msg.obj;
                    //Log.e(TAG, "Message 받음");
                    try {
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input);
                        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                        int id = dataInputStream.readInt();
                        //Log.d("TAG","UI_ID : " + id);
                        //Log.d(TAG,""+dataInputStream.readBoolean());
                        boolean is_distribute = false;
                        for (int i = 0; i < id_list.size(); i++) {
                            if (id_list.get(i) == id) {
                                is_distribute = true;
                                break;
                            }
                        }
                        if (is_distribute) {

                            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
//                            OutputStream os = socket.getOutputStream();
                            dataOutputStream.writeInt(input.length);
                            dataOutputStream.write(input);
                            Log.d(TAG, "update socket message sent : " + getTS());
//                            mRemoteService.doCheck("FLUID Service update msg sent");
                        } else {
                            Log.d("TAG", "undistributed UI's update");
                        }
//                        JSONParser jsonParser = new JSONParser();
//                        JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
//
//                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//                        objectOutputStream.writeObject(jsonObject);
//                        objectOutputStream.flush();
                        //Log.e(TAG, "JsonObject 전송 성공");
                    } catch (Exception e) {
                        e.printStackTrace();
                        //Log.e(TAG, "UpdateHandler에서 오류 발생");
                    }
                }
            };

            Looper.loop();
        }
    }

    public static String getTS() {
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        return ts;
    }
}