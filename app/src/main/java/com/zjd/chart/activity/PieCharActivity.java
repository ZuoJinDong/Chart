package com.zjd.chart.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zjd.chart.R;
import com.zjd.chart.view.PieChartView;

public class PieCharActivity extends AppCompatActivity implements SensorEventListener {
    private PieChartView pieChartView;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_char);

        pieChartView= (PieChartView) findViewById(R.id.pieChartView);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        // 程序退出时取消注册传感器监听器
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        // 程序暂停时取消注册传感器监听器
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY){
            float gravityX=sensorEvent.values[0];
            float gravityY=sensorEvent.values[1];
            float gravityZ=sensorEvent.values[2];
            pieChartView.setGravity(gravityX,gravityY,gravityZ);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
