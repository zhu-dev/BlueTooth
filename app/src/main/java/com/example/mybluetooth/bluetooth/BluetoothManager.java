package com.example.mybluetooth.bluetooth;

import android.bluetooth.BluetoothSocket;

public class BluetoothManager {
    private static BluetoothSocket mSocket = null;

    public static void setBluetoothSocket(BluetoothSocket socket) {
        mSocket = socket;
    }

    public static BluetoothSocket getBluetoothSocket() {
        if(mSocket != null) {
            return mSocket;
        }
        return null;
    }
}
