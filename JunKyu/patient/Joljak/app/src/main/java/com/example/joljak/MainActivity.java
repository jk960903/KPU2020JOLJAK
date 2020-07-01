package com.example.joljak;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import static android.content.Context.LOCATION_SERVICE;
import static java.util.Calendar.MINUTE;

//서비스 처리 도중 데이터를 전송하는 페이지를 역할을 만들어야함

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView ID;//신원 정보
    private TextView UserAddress;//주소 정보
    private TextView CareId;//보호자 신원 정보
    private TextView CarePhone;//보호자 전화번호
    private TextView address, longitude, latitude;
    private List<CareId> care;
    private UserId user;
    private PowerManager powerManager;
    CareId care1;

    private static BluetoothSPP bt;
    final static int ACT_SUB = 0;
    final static int ACT_COMPLETE = 1;
    final static int ACT_LOGIN = 2;
    final static int ACT_DRUG = 3;
    final static int ACT_DRUGMENU = 4;
    String sfName = "myFile";
    private String mJsonString;
    //할일을 하지 않았을때 백그라운드에 표시
    //클라이언트한테 전송

    //자이로 센서 부분
    private static int walkCount;
    private SensorManager sensorManager;
    private Sensor gyroSensor;
    private TextView WalkView;
    private Calendar cal = Calendar.getInstance();
    private boolean checkLogin;
    public String p_id;
    //블루투스
    //GPS 부분 맵은 구현 안하고 지오코더 및 위도 경도만 표현할것
    Geocoder geocoder;
    LatLng latlng;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;

    private static ArrayList<MedicineData> mlist;

    // URL 설정.
    static String url = "http://192.168.63.221/PHP_connection.php";
    static boolean logincheck;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double longitude = intent.getDoubleExtra("Longitude", 0);
            double latitude = intent.getDoubleExtra("Latitude", 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkLocationServicesStatus()) {//위치 정보 승인 확인
            showDialogForLocationServiceSetting();//아니라면 승인 확인 다이얼로그 호출
        }
        /*bt = new BluetoothSPP(this);//초기화
        if (!bt.isBluetoothAvailable()) {
            Log.i("bluetooth error", "에러");
            finish();
        }
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                //텍스트 설정
            }
        });
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
                BluetoothThread bluetoothThread = new BluetoothThread();
                Thread thread = new Thread(bluetoothThread);
                thread.start();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });*/
        Button btnConnect = (Button) findViewById(R.id.connect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
        mlist=new ArrayList<>();
        ID = (TextView) findViewById(R.id.mainUser);
        UserAddress = (TextView) findViewById(R.id.mainAddress);
        CareId = findViewById(R.id.MainCare);
        CarePhone = findViewById(R.id.MainCarePhone);
        longitude = (TextView) findViewById(R.id.longitude);
        latitude = (TextView) findViewById(R.id.latitude);
        address = (TextView) findViewById(R.id.address);
        GPSTracker gpsTracker = new GPSTracker(MainActivity.this);
        double Longitude = gpsTracker.getLongitude();
        double Latitude = gpsTracker.getLatitude();
        String addressnow = getCurrentAddress(Latitude, Longitude);
        longitude.setText("경도" + Double.toString(Longitude));
        latitude.setText("위도" + Double.toString(Latitude));
        address.setText(addressnow);
        care1 = new CareId();
        care = new ArrayList<>();
        WalkView = (TextView) findViewById(R.id.walkview);
        SharedPreferences sf = getSharedPreferences(sfName, 0);
        String str = sf.getString("ID", "");
        ID.setText(str);
        str = sf.getString("USERADDRESS", "");
        UserAddress.setText(str);
        str = sf.getString("CAREID", "");
        CareId.setText(str);
        str = sf.getString("CAREPHONE", "");
        CarePhone.setText(str);
        //처음시작이라면 사용자 정보 보호자정보 입력(데이터 입력이없어도 처음시작)
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        WalkView.setText("걸을수:+" + Integer.toString(walkCount));
        /* 아직 db 생성이 안되었고 미완성이라 주석처리
        if(!checkLogin){
            Intent intent=new Intent(MainActivity.this,Login.class);
            startActivityForResult(intent,ACT_LOGIN );
        }*/
        if (GPSService.serviceintent == null) {

            Intent foreGroundService = new Intent(this, GPSService.class);
            foreGroundService.putExtra("p_id", "영주");
            startForegroundService(foreGroundService);
        } else {
            Intent foreGroundService = GPSService.serviceintent;

        }
        getDrug getDrug=new getDrug();
        getDrug.execute("80","80");
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data) {//사용자 정보 및 보호자 정보 설정
        super.onActivityResult(requestcode, resultcode, data);
        switch (requestcode) {
            case ACT_SUB:
                if (resultcode == Activity.RESULT_OK) {
                    user.name = data.getStringExtra("UserID");
                    user.Address = data.getStringExtra("UserAddress");
                    care1 = new CareId(data.getStringExtra("CareName"), data.getStringExtra("CarePhone"));
                    care.add(care1);
                    ID.setText(user.name);
                    UserAddress.setText(user.Address);
                    CareId.setText(care1.CareId);
                    CarePhone.setText(care1.Phone);
                }
                break;
            case ACT_COMPLETE:
                //여기에 이제 설정된것들을 넣으면됨
                user.name = data.getStringExtra("UserID");
                user.Address = data.getStringExtra("UserAddress");
                care1 = new CareId(data.getStringExtra("CareName"), data.getStringExtra("CarePhone"));
                care.add(care1);
                ID.setText(user.name);
                UserAddress.setText(user.Address);
                CareId.setText(care1.CareId);
                CarePhone.setText(care1.Phone);
                break;
            case BluetoothState.REQUEST_CONNECT_DEVICE:
                if (resultcode == Activity.RESULT_OK) {
                    bt.connect(data);
                }
                break;
            case BluetoothState.REQUEST_ENABLE_BT:
                if (resultcode == Activity.RESULT_OK) {
                    bt.setupService();
                    bt.startService(BluetoothState.DEVICE_OTHER);
                    //setup();
                }
                break;
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        checkRunTimePermission();
                        return;
                    }
                }
            case ACT_DRUG:
                break;
            case ACT_LOGIN:
                p_id = data.getStringExtra("p_id");
                Log.i("p_id", p_id);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        /*if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리

            }
        }*/
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME);

        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate) {//일시정지될때 저장
        super.onSaveInstanceState(outstate);
    }

    @Override
    protected void onStop() {//어플이 종료되기전 데이터 저장(사용자 보호자 인적사항등을 저장하기 위해 사용)
        super.onStop();
        SharedPreferences sf = getSharedPreferences(sfName, 0);
        SharedPreferences.Editor editor = sf.edit();
        editor.putInt("Walk", walkCount);
        if (user != null) {
            editor.putString("ID", user.name);
            editor.putString("USERADDRESS", user.Address);
            editor.putString("CAREID", care1.CareId);
            editor.putString("CAREPHONE", care1.Phone);

            editor.commit();
        }
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.GPS:
                intent = new Intent(MainActivity.this, GPSActivity.class);
                startActivity(intent);
                break;
            case R.id.write://디비부분에 저장 희망 이게 제일 중요함 만약 어그러지면 다 망함
                intent = new Intent(MainActivity.this, SaveID.class);
                startActivityForResult(intent, ACT_SUB);
                intent = getIntent();
                user = new UserId();
                user.name = intent.getStringExtra("UserID");
                user.Address = intent.getStringExtra("UserAddress");
                care1 = new CareId(intent.getStringExtra("CareName"), intent.getStringExtra("CarePhone"));
                care.add(care1);
                ID.setText(user.name);
                UserAddress.setText(user.Address);
                CareId.setText(care1.CareId);
                CarePhone.setText(care1.Phone);
                break;
            /*case R.id.goAccount:
                intent = new Intent(MainActivity.this, getAccount.class);
                startActivity(intent);
                break;*/
            case R.id.drugpage:
                intent = new Intent(MainActivity.this, SetDrug.class);
                startActivityForResult(intent, ACT_DRUG);
                intent = getIntent();
                break;
            /*case R.id.loginActivity:
                intent = new Intent(MainActivity.this, Login.class);
                startActivityForResult(intent, ACT_LOGIN);

                break;*/
            case R.id.drugmenu:
                intent = new Intent(MainActivity.this, DrugMenu.class);
                intent.putExtra("json",mJsonString);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
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
            walkCount++;
            if (Hour == 0 && minute == 0 && second <= 10) {
                walkCount = 0;
            }
            boolean check = true;
            Intent foreGroundService = new Intent(this, GPSService.class);
            foreGroundService.putExtra("WalkCount", walkCount);
            foreGroundService.putExtra("Thread", check);
            startForegroundService(foreGroundService);
            WalkView.setText("" + Integer.toString(walkCount));


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int arrucary) {

    }
    //GPS 활성화 메소드
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n 위치 정보를 받아오시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettionIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettionIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                } else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    private void checkRunTimePermission() {
        //런타임 퍼미션 처리
        //1. 위치 퍼미션 가지고 있는지 체크
        int hasFineLacationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLacationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            //퍼미션 가지고 있는 경우
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0]))//사용자가 퍼미션 거부한경우
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            else {
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //위도 및 지오코더
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

    public class BluetoothThread implements Runnable {
        int time=0;
        @Override
        public void run() {
            getDrug getdrug=new getDrug();
            getdrug.execute("80");
            while (true) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);
                for(int i=0; i<mlist.size(); i++){
                    if(mlist.get(i).gethour()==hour){
                        bt.send("a",true);
                        if(time<0);

                    }
                }
                //HeartSend();센서kk 고장으로 오류
                bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
                    @Override
                    public void onDataReceived(byte[] data, String message) {
                        HeartSend(message);
                        TextView textView =(TextView)findViewById(R.id.pulse);
                        textView.setText(message);
                    }
                });
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void HeartSend(String Message) {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        System.out.println(response);
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            HeartRequest heartRequest = new HeartRequest(p_id,Message ,responseListener);//heratrate 들어가야함
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(heartRequest);
        }

    }




    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            //tv_outPut.setText(s);
        }
    }
    private class getDrug extends AsyncTask<String,Void,String> {
        String serverURL = "http://192.168.62.36/query_medicine.php";
        @Override
        protected String doInBackground(String... strings) {
            String postParameters = "data3=" + strings[0];
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Accept-charset", "UTF-8");
                httpURLConnection.setRequestProperty("Context_type", "application/x-www-form-urlencoded;charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mJsonString = s;
            showResult();
        }

        private void showResult() {
            String TAG_JSON = "root";
            String TAG_NAME = "p_name";
            String TAG_MEDICINE = "m_name";
            String TAG_TIME="m_time";
            try {
                JSONObject jsonObject = new JSONObject(mJsonString.substring(mJsonString.indexOf("{"), mJsonString.lastIndexOf("}") + 1));
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String id = item.getString(TAG_NAME);
                    String m_name = item.getString(TAG_MEDICINE);
                    String m_time = item.getString(TAG_TIME);

                    MedicineData medicineData = new MedicineData();

                    medicineData.setID(id);
                    medicineData.setm_name(m_name);
                    medicineData.setm_time(m_time);
                    if(m_time.length()<=4){
                     medicineData.settime(Integer.parseInt(m_time.substring(0,1)));
                    }
                    else {
                        medicineData.settime(Integer.parseInt(m_time.substring(0, 2)));
                    }
                    mlist.add(medicineData);

                }


            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }
}







