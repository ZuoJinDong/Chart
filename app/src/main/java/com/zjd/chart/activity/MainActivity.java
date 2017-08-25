package com.zjd.chart.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zjd.chart.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_line:
                startActivity(new Intent(this,LineActivity.class));
                break;
            case R.id.btn_barChart:
                startActivity(new Intent(this,BarChartActivity.class));
                break;
            case R.id.btn_pieChart:
                startActivity(new Intent(this,PieCharActivity.class));
                break;
        }
    }
}
