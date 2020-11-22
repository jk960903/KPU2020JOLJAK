package com.example.joljakclient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.String;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private boolean CheckLogin;
    String sfName="ClientLogin";
    final static int ACT_SUB=1;
    final static int LOGIN_OK=0;
    private static int[][][] Walkstaistic;
    private static int[] intentwalk;
    static String url="http://192.168.62.120/getjson.php";
    public static TextView getText;
    public static TextView AddressText;
    public static String[] mJsonString;
    private static ArrayList<WalkData> List_Walk;
    private static ArrayList<LocationData> List_Locate;
    public static TextView temp;
    private ImageButton menu;
    private static ArrayList<HeartData> List_Heart;
    private static TextView HeartText;
    private static TextView WalkText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View actionbarView;
        ImageButton menu_btn;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(false);

        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        actionbarView = getLayoutInflater().inflate(R.layout.toolbar, null);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionbarView, layout);


        SharedPreferences sf=getSharedPreferences(sfName,0);
        mJsonString=new String[5];
        getText=(TextView)findViewById(R.id.get);
        AddressText=(TextView)findViewById(R.id.address);
        HeartText=(TextView)findViewById(R.id.heartrate);
        List_Walk=new ArrayList<>();
        List_Locate=new ArrayList<>();
        List_Heart=new ArrayList<>();
        Walkstaistic=new int[12][31][24];
        intentwalk=new int[12*31*24];
        for(int i=0; i<12; i++){//배열 init
            for(int j=0; j<31; j++){
                for (int k=0; k<24; k++){
                    Walkstaistic[i][j][k]=1000;
                }
            }
        }
        WalkText=(TextView)findViewById(R.id.walk);
        //CheckLogin=sf.getBoolean("login",false);
        /*if(!CheckLogin){
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivityForResult(intent,LOGIN_OK);
        }*/
        NetworkTask networkTask=new NetworkTask();
        networkTask.execute(url,"80");
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println(response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    Toast.makeText(getApplicationContext(), "success" + success, Toast.LENGTH_SHORT).show();

                    String p_id=jsonResponse.getString("p_id");
                    String p_walk=jsonResponse.getString("walk");

                    //서버에서보내준 값이 true이면?
                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                    String p_id1 = jsonResponse.getString("p_id");
                    String p_pw = jsonResponse.getString("p_pw");

                    getText.setText(p_walk);
                    //로그인에 성공했으므로 MainActivity로 넘어감
                    setResult(0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        LocateNet Locatetask=new LocateNet();
        Locatetask.execute(url,"2020년 5월 14일 18시");
        getData getdata=new getData();
        getdata.execute("80");
        HeartLate heart=new HeartLate();
        heart.execute("80");
    }
    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onStop(){
        SharedPreferences sf=getSharedPreferences(sfName,0);
        SharedPreferences.Editor editor=sf.edit();
        editor.putBoolean("login",false);
        super.onStop();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){//메뉴 옵션 리플레이터
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.mainmenu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){//메뉴이동
        Intent intent=null;
        switch (item.getItemId()){
            case R.id.GPS:
                intent=new Intent(MainActivity.this,GPSActivity.class);
                intent.putExtra("Longitude",Double.parseDouble(List_Locate.get(0).getLongitude()));
                intent.putExtra("Latitude",Double.parseDouble(List_Locate.get(0).getLatitude()));
                startActivity(intent);
                break;
            case R.id.Graph:
                intent=new Intent(MainActivity.this,graph_activity.class);
                intent.putExtra("json",mJsonString[0]);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestcode,int resultcode,Intent data){//액티비티 리설트로 데모버전에서는 로그인 없으므로 사용하지 않음
        super.onActivityResult(requestcode,resultcode,data);
        switch (requestcode){
            case LOGIN_OK://로그인이 되어있다면
                CheckLogin=true;
        }
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
    public class NetworkTask extends AsyncTask<String,Void,String>{
        private String url;
        private ContentValues values;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();


        }
        @Override
        protected String doInBackground(String... params){
            String serverURL="http://192.168.62.120/query.php";
            String postParameters="walk="+params[1];
            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Accept-charset","UTF-8");
                httpURLConnection.setRequestProperty("Context_type","application/x-www-form-urlencoded;charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("tag", "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){

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
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            mJsonString[0]=s;
            showResult();
        }
        private void showResult(){
            String TAG_JSON="root";
            String TAG_ID="p_id";
            String TAG_WALK="walk";
            String TAG_DAY="datetime";
            try{
                JSONObject jsonObject = new JSONObject(mJsonString[0].substring(mJsonString[0].indexOf("{"), mJsonString[0].lastIndexOf("}") + 1));
                Log.e("json",mJsonString[0]);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for(int i=0;i<jsonArray.length();i++){

                    JSONObject item = jsonArray.getJSONObject(i);

                    String id = item.getString(TAG_ID);
                    String walk = item.getString(TAG_WALK);
                    String day = item.getString(TAG_DAY);

                    WalkData personalData=new WalkData();

                    personalData.setID(id);
                    personalData.setDatetime(day);
                    personalData.setWalk(walk);

                    List_Walk.add(personalData);

                }
                WalkText.setText((List_Walk.get(List_Walk.size()-1).getWalk()));

            } catch (JSONException e) {

                e.printStackTrace();
            }
            statisticWalk(List_Walk);
        }
        private void statisticWalk(ArrayList<WalkData> list){
            for(int i=0; i<list.size()-1; i++){
                String month=list.get(i).getDatetime().substring(4,5);
                String day=list.get(i).getDatetime().substring(6,7);
                String hour=list.get(i).getDatetime().substring(8);
                Walkstaistic[Integer.parseInt(month)][Integer.parseInt(day)][Integer.parseInt(hour)]=Integer.parseInt(List_Walk.get(i).getWalk());
            }
        }
    }
    private class LocateNet extends AsyncTask<String,Void,String> {//위치정보 받아오기
        String serverURL = "http://192.168.62.120/query_locate.php";


        @Override
        protected String doInBackground(String... strings) {
            String postParameters = "locate=" + strings[0];
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
                Log.d("tag", "response code - " + responseStatusCode);

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
            mJsonString[1] = s;
            showResult();
        }

        private void showResult() {
            String TAG_JSON = "root";
            String TAG_NAME = "p_name";
            String TAG_LONGITUDE = "p_longitude";
            String TAG_LATITUDE="p_latitude";
            try {
                JSONObject jsonObject = new JSONObject(mJsonString[1].substring(mJsonString[1].indexOf("{"), mJsonString[1].lastIndexOf("}") + 1));
                Log.e("json",mJsonString[0]);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String id = item.getString(TAG_NAME);
                    String longiude = item.getString(TAG_LONGITUDE);
                    String latitude = item.getString(TAG_LATITUDE);

                    LocationData locationData = new LocationData();

                    locationData.setID(id);
                    locationData.setLatitude(latitude);
                    locationData.setLongitude(longiude);

                    List_Locate.add(locationData);
                    String address=getCurrentAddress(Double.parseDouble(List_Locate.get(List_Locate.size()-1).getLatitude()),Double.parseDouble(List_Locate.get(List_Locate.size()-1).getLongitude()));
                    AddressText.setText(address);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
    private class HeartLate extends AsyncTask<String,Void,String>{//심박 받아오기
        String serverURL = "http://192.168.62.120/query_heart.php";


        @Override
        protected String doInBackground(String... strings) {
            String postParameters = "data2=" + strings[0];
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
            mJsonString[2] = s;
            showResult();
        }

        private void showResult() {
            String TAG_JSON = "root";
            String TAG_ID = "p_name";
            String TAG_HEART="p_heart";
            try {
                JSONObject jsonObject = new JSONObject(mJsonString[2].substring(mJsonString[2].indexOf("{"), mJsonString[2].lastIndexOf("}") + 1));
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String id = item.getString(TAG_ID);
                    String heart=item.getString(TAG_HEART);

                    HeartData heartData=new HeartData();

                    heartData.setName(id);
                    heartData.setHeart(Integer.parseInt(heart));

                    List_Heart.add(heartData);
                }
                HeartText.setText(Integer.toString(List_Heart.get(List_Heart.size()-1).p_heart));


            } catch (JSONException e) {

                Log.d("Error", "showResult : ", e);
                e.printStackTrace();
            }


        }
    }

    private class getData extends AsyncTask<String,Void,String>{//걸음수 받아오기
        String serverURL = "http://192.168.62.120/query_demo.php";


        @Override
        protected String doInBackground(String... strings) {
            String postParameters = "data1=" + strings[0];
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
                Log.d("tag", "response code - " + responseStatusCode);

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
            mJsonString[3] = s;
            showResult();
        }

        private void showResult() {
            String TAG_JSON = "root";
            String TAG_ID = "p_name";
            String TAG_LOCATE = "p_address";
            String TAG_DAY = "c_name";
            String TAG_PHONE="c_phone";
            try {
                JSONObject jsonObject = new JSONObject(mJsonString[3].substring(mJsonString[3].indexOf("{"), mJsonString[3].lastIndexOf("}") + 1));
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String name = item.getString(TAG_ID);
                    String address = item.getString(TAG_LOCATE);
                    String c_name = item.getString(TAG_DAY);
                    String c_phone=item.getString(TAG_PHONE);

                    Patientdata patientdata=new Patientdata();
                    patientdata.setp_name(name);
                    patientdata.setp_address(address);
                    patientdata.setc_name(c_name);
                    patientdata.setC_phone(c_phone);

                    TextView ptext=(TextView)findViewById(R.id.p_name);
                    TextView ctext=(TextView)findViewById(R.id.c_name);

                    ptext.setText(name);
                    ctext.setText(c_name);
                }


            } catch (JSONException e) {

                Log.d("Error", "showResult : ", e);
                e.printStackTrace();
            }


        }
    }



}