package com.example.mybluetooth.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mybluetooth.R;

public class startActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_query;
    private TextView tv_measure;
    private TextView tv_helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        setStatusBarColor(true);
        tv_query = findViewById(R.id.tv_query);
        tv_measure = findViewById(R.id.tv_measure);
        tv_helper = findViewById(R.id.tv_helper);

        tv_helper.setOnClickListener(this);
        tv_measure.setOnClickListener(this);
        tv_query.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_query:
                intent = new Intent();
                intent.setClass(getApplicationContext(), QueryActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_measure:
                //进入蓝牙设备连接界面
                intent = new Intent();
                intent.setClass(getApplicationContext(), MeasureActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_helper:
                intent = new Intent();
                intent.setClass(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void setStatusBarColor(boolean useStatusBarColor) {

        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);//透明
        if (useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }
}
