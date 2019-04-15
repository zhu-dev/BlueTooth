package com.example.mybluetooth.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mybluetooth.R;
import com.example.mybluetooth.adapter.DevicesRecycleAdapter;
import com.example.mybluetooth.bluetooth.BluetoothManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DevicesActivity extends AppCompatActivity implements DevicesRecycleAdapter.OnClickListener {

    private static final String TAG = "DevicesActivity";

    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private List<String> mDevicesNameList = new ArrayList<>();

    private DevicesRecycleAdapter adapter;

    private ProgressBar pb_devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//获得蓝牙适配器

        pb_devices = findViewById(R.id.pb_devices);
        pb_devices.setVisibility(View.GONE);

        pairedDevicesInit();//将已配对的设备添加到列表中
        registerBroadcast();//注册广播接收器并开启蓝牙搜索，记得在生命周期中注销广播

        RecyclerView recyclerView = findViewById(R.id.recycle_devices);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DevicesRecycleAdapter(mDevicesNameList);
        adapter.setOnClickListener(this);//设置点击回调接口
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);//注销广播
    }

    private void pairedDevicesInit() {
        // 将已配对的设备添加到列表中
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mDevicesNameList.add(device.getName() + "\n" + device.getAddress());
                deviceList.add(device);
            }
        }
    }


    private void registerBroadcast() {
        // 注册广播接收器，以获取蓝牙设备搜索结果
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        // 搜索蓝牙设备
        mBluetoothAdapter.startDiscovery();
        pb_devices.setVisibility(View.VISIBLE);
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device);
                // Add the name and address to an array adapter to show in a ListView
                mDevicesNameList.add(device.getName() + "\n" + device.getAddress());
                Toast.makeText(getApplicationContext(), device.getName() + "\n" + device.getAddress(), Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onClick(int position) {
        BluetoothDevice device = deviceList.get(position);
        final BluetoothSocket socket;
        try {
            // 蓝牙串口服务对应的UUID。如使用的是其它蓝牙服务，需更改下面的字符串
            UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (Exception e) {
            Log.e(TAG, "onClick:---获取Socket失败---- " + e.getMessage());
            Toast.makeText(this, "获取Socket失败", Toast.LENGTH_SHORT).show();
            return;
        }
        mBluetoothAdapter.cancelDiscovery();

        //因为connect方法放在主线程会阻塞主线程造成卡顿，所以这里我放到子线程中
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.connect();//this is a block method
                    BluetoothManager.setBluetoothSocket(socket);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DevicesActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                            pb_devices.setVisibility(View.GONE);
                            // 连接成功，返回主界面
                            finish();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onClick:---连接失败---- " + e.getMessage());
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        Log.e(TAG, "onClick:---关闭socket失败---- " + e1.getMessage());
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DevicesActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                            pb_devices.setVisibility(View.GONE);
                        }
                    });
                }

            }
        }).start();

    }
}
