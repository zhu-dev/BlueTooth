package com.example.mybluetooth.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread{
    private final BluetoothSocket mSocket;
    private final InputStream mInStream;
    private final OutputStream mOutStream;
    private Handler mHandler;

    public final static int MESSAGE_READ = 1;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mSocket = socket;
        mHandler = handler;
        InputStream is = null;
        OutputStream os = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException e) { }

        mInStream = is;
        mOutStream = os;
    }

    public void run() {

//        try {
//            Thread.sleep(600);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            byte[] buffer = new byte[1];
            try {
                // Read from the InputStream
                bytes = mInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) { }
    }
}
