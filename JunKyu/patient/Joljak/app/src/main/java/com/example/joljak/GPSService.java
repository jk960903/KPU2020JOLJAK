package com.example.joljak;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

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

import static java.util.Calendar.MINUTE;

public class GPSService extends Service implements LocationListener ,SensorEventListener {
    public static Intent serviceintent = null;
    GPSTracker gpsTracker;
    private String WalkView;

    Location mCurrentLocation;
    LatLng currentPositon;
    String Address;
    double latitude;
    double longitude;

    String CHANNEL_ID = "channel_01";//Notification을 위한 Channel ID
    String Name = "check";//Notificaton 이름
    static NotificationCompat.Builder notification;

    boolean check;
    public static int temp = 0;
    public static int walkcount;
    public static String p_id;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceintent = intent;
        serviceintent.getExtras();
        walkcount=serviceintent.getIntExtra("WalkCount",0);
        Log.e("Walktemp",Integer.toString(walkcount));
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, Name, NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        gpsTracker = new GPSTracker(this);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        Address = getCurrentAddress(latitude, longitude);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID);
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setContentTitle(Name);
        Address = getCurrentAddress(latitude, longitude);
        WalkView = Integer.toString(walkcount);
        notification.setContentText("위치 :" + Address + "\n" + "걸은 수:" + WalkView);
        //여기에 이제 빅스타일 텍스트 들어가야 할듯
        notification.setContentIntent(pendingIntent);
        startForeground(100, notification.build());
        if (!check) {
            Background background = new Background();
            Thread thread = new Thread(background);
            thread.start();
        }
        return START_REDELIVER_INTENT;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        Calendar cal = Calendar.getInstance();
        int Hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(MINUTE);
        int second = cal.get(Calendar.SECOND);
        Log.v("시간", Integer.toString(minute));
        Log.v("시간", Integer.toString(Hour));
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            walkcount++;
            if (Hour == 0 && minute == 0 && second <= 10) {
                walkcount = 0;
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int arrucary) {

    }
    public String getCurrentAddress(double latitude, double longitude) {// 현재 주소 받아오는 메서드
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 100000);

        } catch (IOException e) {


            return "지오코더 불가";
        } catch (IllegalArgumentException e) {
            return "GPS 잘못됨";
        }
        if (addresses == null || addresses.size() == 0) {
            return "주소 없음";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";
    }

    @Override
    public void onLocationChanged(Location location) {//GPS 위치가 변경되었을때 오버라이딩
        currentPositon = new LatLng(location.getLatitude(), location.getLongitude());//위도와 경도 얻어옴
        Address = getCurrentAddress(currentPositon.latitude, currentPositon.longitude);
        mCurrentLocation = location;//최근위치 변경

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public class Background implements Runnable {
        Calendar calendar;

        public void run() {
            //여기서 해야할것이 서버로 보내는 역할임
            boolean minutecheck = false;
            calendar = Calendar.getInstance();
            int predate = calendar.get(Calendar.DATE);
            while (true) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DATE);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                WalkView = Integer.toString(walkcount);
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                Address = getCurrentAddress(longitude, latitude);
                Log.e("wackcount",Integer.toString(walkcount));
                notification.setContentText("위치 :" + Address + "\n" + "걸은 수:" + WalkView);
                if (minute % 5 == 0) {//여기 if문은 sleep으로 제어하는게 좋다고 생각됨
                    //서버로 5분마다 데이터 보내는거 여기 하면됨

                    walkSend();//실험해보아야함
                    LocationSend();
                    //여기서 서버랑 5분마다 통신
                    //날짜 바뀌면 실행하고 초기화하는 작업 해야할듯 싶음
                    //시간 5분 마다 예를들어 if day% 5 or 10으로 설정
                }
                if (hour == 0 && predate != day) {//시간이 0이고 날짜가 바뀌면
                    walkcount = 0;
                    predate = day;
                }
                try {
                    Thread.sleep(10000);//이제 슬립을 좀 늘리고

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }

        public void walkSend() {
            Calendar calendar = Calendar.getInstance();
            String day = Integer.toString(calendar.get(Calendar.YEAR))  + Integer.toString(calendar.get(Calendar.MONTH))
                    + Integer.toString(calendar.get(Calendar.DATE)) +  Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
            if(calendar.get(Calendar.MONTH)<=10){
                 day = Integer.toString(calendar.get(Calendar.YEAR))  +"0"+ Integer.toString(calendar.get(Calendar.MONTH))
                        + Integer.toString(calendar.get(Calendar.DATE)) +  Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
                 Log.e("dayofmonth",day);
            }
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        System.out.println(response);
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {//제이슨 받아올수 있으면
                            //String WalkView = jsonResponse.getString("walkView");
                            //day = jsonResponse.getString("day");
                        } else {
                            //받아올수 없으면
                            Log.i("fail", "failjson");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            WalkRequest walkRequest = new WalkRequest("이준규", walkcount, day, responseListener);
            Log.e("걸음 보내짐","걸음보내짐");
            RequestQueue queue = Volley.newRequestQueue(GPSService.this);
            queue.add(walkRequest);
        }



        public void LocationSend() {
           final Response.Listener<String> responseListner=new Response.Listener<String>() {
               @Override
               public void onResponse(String response) {
                   try{
                       JSONObject jsonResponse=new JSONObject(response);
                       boolean success=jsonResponse.getBoolean("success");
                       if(success){

                       }
                       else{

                       }
                   }catch(JSONException e){
                       e.printStackTrace();
                   }
               }

           };
            LocationRequest locationRequest=new LocationRequest(p_id,longitude,latitude,responseListner);
            RequestQueue queue=Volley.newRequestQueue(GPSService.this);
            queue.add(locationRequest);
        }

    }
}

