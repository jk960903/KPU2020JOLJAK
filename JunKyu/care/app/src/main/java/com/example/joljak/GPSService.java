package com.example.joljak;

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

public class GPSService extends Service implements LocationListener {
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
    public static int temp=0;
    public static int walkcount;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceintent = intent;
        serviceintent.getExtras();
        walkcount = serviceintent.getIntExtra("WalkCount", 0);
        check = serviceintent.getBooleanExtra("Thread", false);
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
        if(!check) {
            Background background = new Background();
            Thread thread = new Thread(background);
            thread.start();
        }
        return START_REDELIVER_INTENT;
    }

    public String getCurrentAddress(double latitude, double longitude) {// 현재 주소 받아오는 메서드
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 100000);

        } catch (IOException e) {

            Toast.makeText(this, "지오코더 불가", Toast.LENGTH_SHORT).show();
            return "지오코더 불가";
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "잘못된 gps좌표", Toast.LENGTH_SHORT).show();
            return "GPS 잘못됨";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 없음", Toast.LENGTH_SHORT).show();
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
                temp++;
                if (minute % 5 == 0) {//여기 if문은 sleep으로 제어하는게 좋다고 생각됨
                    //서버로 5분마다 데이터 보내는거 여기 하면됨
                    Log.i("minuteag","tagminute");
                    Log.i("tempcheck",Integer.toString(temp));
                    Log.i("String",Address);
                    //notification.setContentText(Integer.toString(temp));
                    //startForeground(100,notification.build());
                    //여기서 서버랑 5분마다 통신
                    //날짜 바뀌면 실행하고 초기화하는 작업 해야할듯 싶음
                    //시간 5분 마다 예를들어 if day% 5 or 10으로 설정
                }
                if (hour == 0 && predate != day) {//시간이 0이고 날짜가 바뀌면
                    walkcount=0;
                    predate=day;
                }
                try{
                    Thread.sleep(1000);//이제 슬립을 좀 늘리고

                }catch(InterruptedException e){
                    e.printStackTrace();
                }


            }
        }

    }

}
class GPSSend extends AsyncTask<String,Void,String> {
    String sendMessage, receiveMessage;
    @Override
    protected String doInBackground(String[] sId) {
        try {
            String str;
            URL url = new URL("경로");//이부분 ec2에서 elastic ip 설정 받고 해야함 안그러면 껏다 킬때마다 계속 ip바껴서 안됨
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            //여기 보낼 정보 id,passaword 등등 입력// 회원 가입의 경우 데이터 ㄱ
            //로그인일시 읽어야하고 회원가입일시 데이터 써서 전송
            // sendMessage=""
            //outputStreamWriter.write(sendMessage);
            outputStreamWriter.flush();//stream 삭제
            if (connection.getResponseCode() == connection.HTTP_OK) {
                InputStreamReader temp = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(temp);
                StringBuffer buffer = new StringBuffer();
                //jsp에서 보낸 값 수신
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMessage = buffer.toString();
            } else {
                Log.i("결과", connection.getResponseCode() + "에러");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveMessage;
    }
}

