package com.example.mybluetooth.manager;

import android.util.Log;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

public class MyXValueFormatter extends ValueFormatter {
    private static final String TAG = "MyXValueFormatter";

    private List<String> labels;

    public MyXValueFormatter(List<String> labels) {
        Log.e(TAG, "MyXValueFormatter: --MyXValueFormatter----");
        this.labels = labels;
    }

    /**
     * 重写父类的getFormattedValue() 方法实现X轴的自定义
     *
     * @param value 横坐标值
     * @return 要显示的label值
     */
    @Override
    public String getFormattedValue(float value) {
        Log.e(TAG, "getFormattedValue:---labels---- " + labels.get((int) value / labels.size()));
        Log.e(TAG, "getFormattedValue:---value---- " + value);
        return labels.get((int) value % labels.size());
    }
}
