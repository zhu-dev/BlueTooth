package com.example.mybluetooth.activities;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybluetooth.R;
import com.example.mybluetooth.db.DataBaseManager;
import com.example.mybluetooth.manager.UserBean;
import com.example.mybluetooth.bluetooth.BluetoothManager;
import com.example.mybluetooth.bluetooth.ConnectedThread;
import com.example.mybluetooth.utils.DateSimpleFormatUtil;
import com.example.mybluetooth.utils.EncodeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MeasureActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MeasureActivity";
    private static final int REQUEST_ENABLE_BT = 1;

    private TextView tv_state;
    private TextView tv_dia_pressure;
    private TextView tv_sys_pressure;
    private TextView tv_receive;
    private TextView tv_name;
    private Button btn_devices;
    private Button btn_input;
    private Button btn_clear_receive;
    private Button btn_save;
    private EditText ev_username;
    private Switch sw_hex_ascii;

    private BluetoothAdapter mBluetoothAdapter;
    private ConnectedThread mConnectedThread;

    private boolean sw_receive_hex_enable;
    private Date current;//测量日期
    private List<Integer> dataArray = new ArrayList();
    private int result;
    private int sys_pressure;
    private int dia_pressure;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_activty);

        openBlueTooth();//开启蓝牙

        sw_hex_ascii = findViewById(R.id.sw_hex_ascii);
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
        sw_hex_ascii.setChecked(true);
        sw_receive_hex_enable = true;
        tv_state = findViewById(R.id.tv_state);
        tv_dia_pressure = findViewById(R.id.tv_dia_pressure);
        tv_sys_pressure = findViewById(R.id.tv_sys_pressure);
        tv_receive = findViewById(R.id.tv_receive);
        tv_name = findViewById(R.id.tv_name);
        btn_devices = findViewById(R.id.btn_devices);
        btn_clear_receive = findViewById(R.id.btn_clear_receive);
        btn_input = findViewById(R.id.btn_input_name);
        ev_username = findViewById(R.id.ev_username);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        btn_input.setOnClickListener(this);
        btn_clear_receive.setOnClickListener(this);
        btn_devices.setOnClickListener(this);


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
                        String temp;
                        String content;
                        if (sw_receive_hex_enable) {
                            temp = EncodeUtil.bytesToHexString(buffer, length);
                            if (!temp.equals("20") && !temp.equals("0A")) {
                                result = Integer.parseInt(temp, 16);
                                dataArray.add(result);
                            } else if (temp.equals("0A")) {
                                dia_pressure = dataArray.get(1);
                                sys_pressure = dataArray.get(0);
                                content = sys_pressure + "/mmHg";
                                tv_sys_pressure.setText(content);
                                content = dia_pressure + "/mmHg";
                                tv_dia_pressure.setText(content);
                                content = "[" + sys_pressure + "," + dia_pressure + "]";
                                tv_receive.setText(content);
                                dataArray.clear();
                            }
                            Log.e(TAG, "handleMessage: ----1---result----" + result);
                        } else {
                            temp = EncodeUtil.bytesToCharStr(buffer, length);
                            tv_receive.setText(temp);
                            Log.e(TAG, "handleMessage: ---2------temp-" + temp);
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
    protected void onDestroy() {
        super.onDestroy();
        mConnectedThread.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_devices:
                //进入蓝牙设备连接界面
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), DevicesActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_clear_receive:
                tv_receive.setText(null);
                break;
            case R.id.btn_input_name:
                username = ev_username.getText().toString();
                tv_name.setText(username);
                break;
            case R.id.btn_save://将数据存到数据库
                if (!tv_name.getText().equals("") && !tv_dia_pressure.getText().equals("0/mmHg") && !tv_sys_pressure.getText().equals("0/mmHg")) {
                    current = new Date();
                    String hmsStr = DateSimpleFormatUtil.date2HmsStr(current);//将日期对象转化成时分秒字符串
                    String ymdStr = DateSimpleFormatUtil.date2YmdStr(current);//将日期对象转化成年月日字符串

                    UserBean user = new UserBean();
                    user.setSys_pressure(sys_pressure);
                    user.setDia_pressure(dia_pressure);
                    user.setTimeStr(hmsStr);
                    user.setDateStr(ymdStr);
                    user.setName(tv_name.getText().toString());

                    List<UserBean> users = new ArrayList<>();
                    users.add(user);

                    DataBaseManager.getInstance(MeasureActivity.this)
                            .insertUser(users)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean aBoolean) throws Exception {
                                    Toast.makeText(MeasureActivity.this, "存储成功", Toast.LENGTH_SHORT).show();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(MeasureActivity.this, "存储失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Toast.makeText(MeasureActivity.this, "用户名为空/血压数据为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void openBlueTooth() {
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
