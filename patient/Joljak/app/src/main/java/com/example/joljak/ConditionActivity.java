package com.example.joljak;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class ConditionActivity extends AppCompatActivity implements SensorEventListener {
    //걸음수 측정
    SensorManager sensorManager;
    Sensor gyroSensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        gyroSensor=sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }
    @Override
    protected void onResume(){//다시 시작되도 사용
        super.onResume();
    }
    @Override
    protected void onPause(){//화면 종료 후에도 사용
        super.onPause();
    }
    @Override
    public void onSensorChanged(SensorEvent event){//센서작동과 관련 센서는 동작감지후 사용(한걸음씩 움직일대마다 동작)

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){//정확도 체크하는곳인데 잘모르겠음

    }
}
