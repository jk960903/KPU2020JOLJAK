package com.example.joljak;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.Object;
import java.util.GregorianCalendar;

import static com.example.joljak.MainActivity.ACT_COMPLETE;
import static com.example.joljak.MainActivity.ACT_SUB;
//이 액티비티는 없어도 될거같기도 하고..... Becuase 이미 전에서 하고 있음 or 여기에 환자의 추이를 보여주는 것도 나쁘진 않다고 생각
public class ConditionActivity extends AppCompatActivity implements SensorEventListener {
    //걸음수 측정 아직 세이브 인스턴스나 이런것들이 안되서 그런거 같기도 함
    private SensorManager sensorManager;
    private Sensor gyroSensor;
    private Sensor tempSensor;
    TextView walkecount;
    Calendar cal = Calendar.getInstance();
    private SensorEvent sensorEvent;
    private int walkCount=0;
    String sfName="saveWalk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);
        sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        gyroSensor=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        walkecount=(TextView)findViewById(R.id.Walk);
        tempSensor=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        SharedPreferences sf=getSharedPreferences(sfName,0);
        walkCount=sf.getInt("Walk",0);
        walkecount.setText("걸음수 :"+walkCount);
    }
    @Override
    protected void onStart(){
        sensorManager.registerListener(this,gyroSensor,SensorManager.SENSOR_DELAY_GAME);
        if(cal.get(Calendar.HOUR_OF_DAY)==1){
            if(sensorEvent!=null){
                walkCount=0;
            }

        }
        super.onStart();
    }
    @Override
    protected void onResume(){//다시 시작되도 사용
        super.onResume();
        sensorManager.registerListener(this,gyroSensor,SensorManager.SENSOR_DELAY_GAME);
        if(cal.get(Calendar.HOUR_OF_DAY)==0&&cal.get(Calendar.MINUTE)==0){
            walkCount=0;
        }}
    @Override
    protected void onPause(){//화면 종료 후에도 사용
        super.onPause();
        sensorManager.registerListener(this,gyroSensor,SensorManager.SENSOR_DELAY_GAME);
        if(cal.get(Calendar.HOUR_OF_DAY)==16&&cal.get(Calendar.MINUTE)==0){
            walkCount=0;
        }
    }
    @Override
    protected void onStop(){//액티비티 끌때 한번 호출
        super.onStop();
        sensorManager.registerListener(this,gyroSensor,SensorManager.SENSOR_DELAY_GAME);
        if(cal.get(Calendar.HOUR_OF_DAY)==16&&cal.get(Calendar.MINUTE)==0){
            sensorEvent.values[0]=0;
        }
        SharedPreferences sf=getSharedPreferences(sfName,0);
        SharedPreferences.Editor editor=sf.edit();
        editor.putInt("Walk",walkCount);

    }
    @Override
    public void onSensorChanged(SensorEvent event){//센서작동과 관련 센서는 동작감지후 사용(한걸음씩 움직일대마다 동작)
        if(event.sensor.getType()==Sensor.TYPE_STEP_COUNTER){
            if(cal.get(Calendar.MINUTE)==6){
                walkCount=0;
            }
            else{
                walkCount++;
            }
            walkecount.setText("걸음수 :" +Integer.toString(walkCount));
            sensorEvent=event;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){//정확도 체크하는곳인데 잘모르겠음

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data){
        super.onActivityResult(requestcode,resultcode,data);
        switch(requestcode){
            case ACT_SUB:
                if(resultcode== Activity.RESULT_OK){
                    walkCount=data.getIntExtra("Walk",0);
                }
            case ACT_COMPLETE:
                walkCount=data.getIntExtra("Walk",0);
        }
    }
}
