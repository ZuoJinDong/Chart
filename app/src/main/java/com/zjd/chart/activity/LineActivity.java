package com.zjd.chart.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zjd.chart.R;
import com.zjd.chart.view.LineView;

public class LineActivity extends AppCompatActivity implements SensorEventListener {
    private LineView lineView;
    private Button btn_start;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);

        lineView= (LineView) findViewById(R.id.lineView);
        btn_start= (Button) findViewById(R.id.btn_start);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        lineView.setOnPointClickListener(new LineView.OnPointClickListener() {
            @Override
            public void onPointClick(int position, int number) {
                btn_start.setText(position+1+"+"+number);
            }
        });
    }

    public void onClick(View view) {
        lineView.startAmin();
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
            lineView.setGravity(gravityX,gravityY,gravityZ);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
