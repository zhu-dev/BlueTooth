package com.example.mybluetooth.manager;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.ArrayList;
import java.util.List;

public class LineChartManager implements OnChartGestureListener {

    private static final String TAG = "LineChartManager";

    private List<UserBean> dataList;

    private LineChart lineChart;
    private XAxis xAxis;                //X轴
    private YAxis leftYAxis;            //左侧Y轴
    private YAxis rightYaxis;           //右侧Y轴
    private Legend legend;              //图例
    private LimitLine limitLine;        //限制线
//  private MyMarkerView markerView;    //标记视图 即点击xy轴交点时弹出展示信息的View 需自定义

    private List<Entry> entries_sys_list = new ArrayList<>();
    private List<Entry> entries_dia_list = new ArrayList<>();


    public LineChartManager(LineChart lineChart, List<UserBean> dataList) {
        this.lineChart = lineChart;
        initChart();
        showLine(dataList);
    }

    //初始化Chart
    private void initChart() {
        /* **图表设置***/
        //是否展示网格线
        lineChart.setDrawGridBackground(true);
        //是否显示边界
        lineChart.setDrawBorders(true);
        //是否可以拖动
        lineChart.setDragEnabled(true);
        //是否有触摸事件
        lineChart.setTouchEnabled(true);
        //是否可以缩放，默认是true
        lineChart.setScaleEnabled(true);
        //是否可以双击放大视图，默认是true
        lineChart.setDoubleTapToZoomEnabled(false);
        //拖拽滚动时，手放开是否会持续滚动，默认是true
        lineChart.setDragDecelerationEnabled(false);
        //设置XY轴动画效果
        lineChart.animateY(2500);
        lineChart.animateX(1500);
        //设置背景
        lineChart.setBackgroundColor(Color.WHITE);
        //是否显示边界
        lineChart.setDrawBorders(true);
        //设置手势监听器
        lineChart.setOnChartGestureListener(this);

        /* **XY轴的设置***/
        xAxis = lineChart.getXAxis();
        leftYAxis = lineChart.getAxisLeft();
        rightYaxis = lineChart.getAxisRight();
        rightYaxis.setEnabled(false);//不显示右边的Y轴，难看
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        lineChart.setVisibleXRange(1,3);
        //保证Y轴从0开始，不然会上移一点
        leftYAxis.setAxisMinimum(0f);
        rightYaxis.setAxisMinimum(0f);

      //  lineChart.setVisibleXRange(0,5);
        //x轴描述
        Description description = new Description();
        description.setEnabled(true);
        description.setText("单位/mmHg");
        lineChart.setDescription(description);

        /* **折线图例 标签 设置***/
        legend = lineChart.getLegend();
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        //显示位置 左下方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);
    }

    //可以在这里传入X坐标
    //这里需要处理x轴显示的问题
    private void showLine(List<UserBean> dataList) {
        if (dataList.size() != 0) {


            for (int i = 0; i < dataList.size(); i++) {
                Log.e(TAG, "showLine: ------" + dataList.get(i).getTimeStr());
            }


            List<String> xAxisList = new ArrayList<>();

            for (int i = 0; i < dataList.size(); i++) {
                UserBean data = dataList.get(i);

                Entry entry_sys = new Entry(i, (float) data.getSys_pressure());
                Entry entry_dia = new Entry(i, (float) data.getDia_pressure());

                entries_dia_list.add(entry_dia);
                entries_sys_list.add(entry_sys);

                xAxisList.add(data.getTimeStr());
            }

            for (int i = 0; i < xAxisList.size(); i++) {
                Log.e(TAG, "xAxisList: ------" + xAxisList.get(i));
            }

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new MyXValueFormatter(xAxisList));

            LineDataSet lineDataSet_sys = new LineDataSet(entries_sys_list, "收缩压");
            LineDataSet lineDataSet_dia = new LineDataSet(entries_dia_list, "舒张压");

            initLineDataSet(lineDataSet_sys, "#ff9966");
            initLineDataSet(lineDataSet_dia, "#66ff66");

            LineData lineData = new LineData(lineDataSet_sys, lineDataSet_dia);
            lineChart.setData(lineData);
            lineChart.invalidate(); // refresh
        }

    }

    private void initLineDataSet(LineDataSet lineDataSet, String color) {
        lineDataSet.setColor(Color.parseColor(color));//Color.parseColor("#ff9966")
        lineDataSet.setCircleColor(Color.parseColor(color));
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setValueTextSize(10f);
        //设置折线图填充
        lineDataSet.setDrawFilled(false);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
    }

    private void updateChart(List<UserBean> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            UserBean data = dataList.get(i);

            Entry entry_sys = new Entry(i, (float) data.getSys_pressure());
            Entry entry_dia = new Entry(i, (float) data.getDia_pressure());

            entries_dia_list.add(entry_dia);
            entries_sys_list.add(entry_sys);
        }

        List<ILineDataSet> dataSets = lineChart.getLineData().getDataSets();
        if (dataSets != null && dataSets.size() != 0) {
            for (ILineDataSet iLineDataSet : dataSets) {
                LineDataSet dataSet = (LineDataSet) iLineDataSet;
                if (dataSet.getLabel().equals("收缩压")) {
                    dataSet.setValues(entries_sys_list);
                } else if (dataSet.getLabel().equals("舒张压")) {
                    dataSet.setValues(entries_dia_list);
                }
            }
        } else {
            showLine(dataList);
        }
    }

    public void setDataList(List<UserBean> dataList) {
        if (dataList.size() != 0) {
            this.dataList = dataList;
            // lineChart.invalidate();
            showLine(dataList);
            for (int i = 0; i < dataList.size(); i++) {
                Log.e(TAG, "setDataList: ------" + dataList.get(i).getTimeStr());
            }
        }
    }


    /******************************下面是手势回调接口的重写方法***********************/

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        if (dX > 0) {

        }
    }
}
