package com.example.hbserver;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyService extends Service {
    private static final String TAG = "HBMessage";
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
        Log.d(TAG, "run: ");
        try{
            ServerSocket serverSocket = new ServerSocket(30000);
            Socket socket = serverSocket.accept();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeUTF("ping");
            dataOutputStream.flush();
            OutputStream os = socket.getOutputStream();


            byte[] buffer = new byte[100];
            byte[] ping = byteArrayOutputStream.toByteArray();

            while(true)
            {
                os.write(ping);
                os.flush();

                socket.getInputStream().read(buffer);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
                DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                Log.d(TAG, "HB : " + dataInputStream.readUTF() + " " + getTS());
                sleep(30);
            }
        } catch (Exception e)
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