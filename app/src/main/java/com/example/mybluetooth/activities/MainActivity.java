package com.example.mybluetooth.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybluetooth.R;
import com.example.mybluetooth.bluetooth.BluetoothManager;
import com.example.mybluetooth.bluetooth.ConnectedThread;
import com.example.mybluetooth.utils.EncodeUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;

    private TextView tv_state;
    private TextView tv_receive;
    private EditText ed_send;
    private Button btn_send;
    private Button btn_devices;
    private Button btn_clear_receive;


    private Switch sw_hex_ascii;
    private Switch sw_hex_ascii_send;
    private boolean sw_receive_hex_enable;
    private boolean sw_send_hex_enable;

    private BluetoothAdapter mBluetoothAdapter;
    private ConnectedThread mConnectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openBlueTooth();//开启蓝牙

        sw_hex_ascii = findViewById(R.id.sw_hex_ascii);
        sw_hex_ascii_send = findViewById(R.id.sw_hex_ascii_send);
        sw_hex_ascii.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sw_receive_hex_enable = true;
                } else {
                    sw_receive_hex_enable = false;
                }
            }
        });

        tv_state = findViewById(R.id.tv_state);
        tv_receive = findViewById(R.id.tv_receive);
        btn_send = findViewById(R.id.btn_send);
        btn_devices = findViewById(R.id.btn_devices);
        ed_send = findViewById(R.id.ev_send);
        btn_clear_receive = findViewById(R.id.btn_clear_receive);
        btn_clear_receive.setOnClickListener(this);
        btn_devices.setOnClickListener(this);
        btn_send.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //回到主界面后检查是否已成功连接蓝牙设备
        if (BluetoothManager.getBluetoothSocket() == null || mConnectedThread != null) {
            tv_state.setText("未连接");
            return;
        }
        tv_state.setText("已连接");

        //已连接蓝牙设备，则接收数据，并显示到接收区文本框
        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // int isClear = tv_receive.getMaxLines() * tv_receive.getWidth();
                switch (msg.what) {
                    case ConnectedThread.MESSAGE_READ:
                        byte[] buffer = (byte[]) msg.obj;
                        int length = msg.arg1;
                        if (sw_receive_hex_enable) {
                            String temp = EncodeUtil.bytesToHexString(buffer, length);
                            String content = tv_receive.getText() + temp + "\r";
                            tv_receive.setText(content);
                            Log.e(TAG, "handleMessage: ---1--" + temp);
                        } else {
                            String temp = EncodeUtil.bytesToCharStr(buffer, length);
                            String content = tv_receive.getText() + temp;
                            tv_receive.setText(content);
                            Log.e(TAG, "handleMessage: ---2--" + temp);
                        }


                        break;
                }

            }
        };

        //启动蓝牙数据收发线程
        mConnectedThread = new ConnectedThread(BluetoothManager.getBluetoothSocket(), handler);
        mConnectedThread.start();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击【发送】按钮后，将文本框中的文本按照ASCII码发送到已连接的蓝牙设备
            // 对应的十六进制的数，实际是二进制  比如  a-----ascii码97----- 十六进制61----二进制 0110 0001
            case R.id.btn_send:
                if (ed_send.getText().toString().isEmpty()) {
                    return;
                }

                if (BluetoothManager.getBluetoothSocket() == null || mConnectedThread == null) {
                    Toast.makeText(MainActivity.this, "蓝牙未连接", Toast.LENGTH_SHORT).show();
                    return;
                }

                String sendStr = ed_send.getText().toString();
                Log.e(TAG, "onClick: --2----" + sendStr);
                char[] chars = sendStr.toCharArray();
                byte[] bytes = new byte[chars.length];
                for (int i = 0; i < chars.length; i++) {
                    bytes[i] = (byte) chars[i];
                }
                mConnectedThread.write(bytes);
                break;
            case R.id.btn_devices:

                //进入蓝牙设备连接界面
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), DevicesActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_clear_receive:
                tv_receive.setText(null);
                break;
        }
    }

    private void openBlueTooth(){
        // 获取蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }

        //请求开启蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}
