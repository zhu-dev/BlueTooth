package com.example.mybluetooth.activities;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybluetooth.R;
import com.example.mybluetooth.db.DataBaseManager;
import com.example.mybluetooth.manager.LineChartManager;
import com.example.mybluetooth.manager.UserBean;
import com.example.mybluetooth.utils.DateSimpleFormatUtil;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "QueryActivity";
    private LineChartManager lineChartManager;

    private LineChart lineChart;

    private EditText ev_username;
    private Button btn_input;
    private Button btn_search;
    private TextView tv_name;
    private TextView tv_date;
    private TextView tv_dia_pressure;
    private TextView tv_sys_pressure;

    private List<UserBean> users = new ArrayList<>();
    private List<UserBean> dataList = new ArrayList<>();
    private String username;
    private String measureTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置横屏
        initData();
        initView();
        lineChart = findViewById(R.id.chart);
        lineChartManager = new LineChartManager(lineChart, dataList);
    }

    private void initView() {
        ev_username = findViewById(R.id.ev_username);
        btn_input = findViewById(R.id.btn_input);
        btn_search = findViewById(R.id.btn_search);
        tv_name = findViewById(R.id.tv_name);
        tv_date = findViewById(R.id.tv_date);
        tv_dia_pressure = findViewById(R.id.tv_dia_pressure);
        tv_sys_pressure = findViewById(R.id.tv_sys_pressure);

        btn_input.setOnClickListener(this);
        btn_search.setOnClickListener(this);
    }

    private void initData() {
//        for (int i = 0; i < 12; i++) {
//            UserBean person = new UserBean();
//            person.setDia_pressure(10 + i);
//            person.setSys_pressure(15 + i);
//            dataList.add(person);
//        }
//        Date date = new Date();
//        String dateStr = DateSimpleFormatUtil.date2HmsStr(date);
//        String dateStr2 = DateSimpleFormatUtil.date2YmdStr(date);
//        Log.e(TAG, "initData: ---date--" + dateStr);
//        Log.e(TAG, "initData: ---date2--" + dateStr2);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_input:
                username = ev_username.getText().toString();
                tv_name.setText(username);
                break;
            case R.id.btn_search:
                DataBaseManager.getInstance(QueryActivity.this)
                        .getUser(username)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<UserBean>>() {
                            @Override
                            public void accept(List<UserBean> userBeans) throws Exception {
                                dataList = userBeans;
                                lineChartManager.setDataList(dataList);
                                measureTime = dataList.get(0).getDateStr() + " " + dataList.get(0).getTimeStr();
                                tv_date.setText(measureTime);
                                Log.e(TAG, "accept: ------" + dataList.get(0).getName());
                                Log.e(TAG, "accept: ---dataList.size()---" + dataList.size());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: " + throwable.getMessage());
                                Toast.makeText(QueryActivity.this, "读取出错", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
        }
    }
}
