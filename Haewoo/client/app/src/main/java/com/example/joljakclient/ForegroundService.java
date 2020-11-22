package com.example.joljakclient;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import static java.lang.Thread.sleep;

public class ForegroundService extends Service {
    public static Intent serviceintent=null;
    static String CHANNEL_ID="Client";
    String ServiceName="CHECK1212";//이건 나중에 수정필요
    static NotificationCompat.Builder notification;
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
        serviceintent = intent;
        serviceintent.getExtras();
        int walkcount = serviceintent.getIntExtra("WalkCount", 0);
        //check = serviceintent.getBooleanExtra("Thread", false);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, ServiceName, NotificationManager.IMPORTANCE_HIGH);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID);
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setContentTitle(ServiceName);
        notification.setContentText("위치 :" );
        Log.i("hello","check");
        notification.setContentIntent(pendingIntent);
        startForeground(8080, notification.build());
        Background background=new Background();
        Thread thread= new Thread(background);
        thread.run();
        return START_REDELIVER_INTENT;
    }

    public class Background implements Runnable{
        Calendar calendar;

        @Override
        public void run(){
            int predate=Calendar.getInstance().get(Calendar.DATE);
            while(true){//시간설정 부 꺼지지 않게 하기 위해 while True 사용
                calendar=Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DATE);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                if(minute%5==0){
                    //서버로 5분마다 데이터 수신 여기 하면됨
                }
                if(hour==0&&predate!=day){//시간이 0이고 날짜가 바뀌면
                    predate=day;
                    //초기화
                }
                try {
                    sleep(5000000);
                }
                catch (InterruptedException e){
                    Log.i("one","인터럽트 익셉션");
                }
            }
        }
    }
}
