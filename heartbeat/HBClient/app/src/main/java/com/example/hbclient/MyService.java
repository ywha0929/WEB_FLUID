package com.example.hbclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MyService extends Service {

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Thread newThread = new networkThread();
        newThread.start();
    }
}

class networkThread extends Thread{
    private static final String TAG = "HBMessage";

    @Override
    public void run() {
        try{
            Socket socket = new Socket("192.168.50.236",30000);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeUTF("pong");
            dataOutputStream.flush();
            OutputStream os = socket.getOutputStream();
            byte[] buffer = new byte[100];
            byte[] pong = byteArrayOutputStream.toByteArray();
            while(true)
            {
                socket.getInputStream().read(buffer);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
                DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                Log.d(TAG, "HB : " + dataInputStream.readUTF() + " " + getTS());
                os.write(pong);
                os.flush();
            }
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public static String getTS() {
        Long tsLong = System.nanoTime();
        String ts = tsLong.toString();
        return ts;
    }
}