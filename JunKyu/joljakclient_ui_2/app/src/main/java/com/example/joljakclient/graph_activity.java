package com.example.joljakclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class graph_activity extends AppCompatActivity {

    private BarChart graph_barChart;
    private static ArrayList<WalkData> List_Walk;
    private static int[][][] Walkstaistic;
    private Calendar calendar;
    private int hour;
    private int day;
    private int month;
    public static String[] mJsonString;
    Button[] buttons;
    public static String ls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_activity);
        initData();
        network();
        initBar(graph_barChart);
        buttons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeBartoMonth(graph_barChart);
            }
        });
        buttons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeBartoDay(graph_barChart);
            }
        });
        buttons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBar(graph_barChart);
            }
        });
    }
    private void network(){
        getWalk networkTask=new getWalk();
        networkTask.onPostExecute(ls);
    }
    private void initData(){
        List_Walk=new ArrayList<>();
        calendar=Calendar.getInstance();
        hour=calendar.get(Calendar.HOUR_OF_DAY);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);
        mJsonString=new String[5];
        buttons=new Button[3];
        buttons[0]=(Button)findViewById(R.id.order_month);
        buttons[1]=(Button)findViewById(R.id.order_day);
        buttons[2]=(Button)findViewById(R.id.order_hour);
        Intent intent=getIntent();
        ls=intent.getStringExtra("json");
    }
    private void initBar(BarChart barChart) {
        barChart = (BarChart) findViewById(R.id.graph);
        barChart.clearChart();
        String[] hour;
        hour = new String[24];
        //이제 여기서 execute 해주고
        for(int i=0; i<24; i++){
            Walkstaistic[5][26][i]+=i*250;
        }
        for (int i = 0; i < 24; i++) {
            hour[i] = Integer.toString(i);
            barChart.addBar(new BarModel(hour[i], Walkstaistic[month][day-1][i], 0xFF56B7F1));
        }
        barChart.startAnimation();
    }
    private void makeBartoDay(BarChart barChart){
        barChart=(BarChart)findViewById(R.id.graph);
        barChart.clearChart();
        String[] orderday;
        orderday=new String[31];
        int day_count=0;
        for(int i=0; i<31; i++){
            orderday[i]=Integer.toString(i+1);
            day_count=0;
            for(int j=0; j<24; j++){
                day_count+=Walkstaistic[month-1][i][j];
            }
            barChart.addBar(new BarModel(orderday[i],day_count,0xFF56B7F1));
        }
        barChart.startAnimation();
    }
    private void makeBartoMonth(BarChart barChart){
        barChart=(BarChart)findViewById(R.id.graph);
        barChart.clearChart();
        String[] orderMonth;
        orderMonth=new String[12];
        int month_count=0;
        for(int i=0; i<12; i++){
            orderMonth[i]=Integer.toString(i+1);
            for(int j=0; j<31; j++){
                for(int k=0; k<24; k++){
                    month_count+=Walkstaistic[i][j][k];
                }
            }
            barChart.addBar(new BarModel(orderMonth[i],month_count,0xFF56B7F2));
            month_count=0;
        }
        barChart.startAnimation();
    }
    private int getAverage(int id){
        calendar=Calendar.getInstance();
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int sum=0;
        switch(id){
            case 0://일별로 선택되었을때
                if(day>=3) {
                    for (int i = 0; i < day - 2; i++) {
                        sum+=Walkstaistic[month-1][i][23];
                    }
                    int average=sum/(day-1);
                    int gap=Walkstaistic[month-1][day-1][hour-1]-average;
                    return gap;
                }
                else if(day==2){
                    return Walkstaistic[month-1][day-1][23]-Walkstaistic[month-1][day-1][hour-1];
                }
                else{
                    return Walkstaistic[month-1][day-1][hour-1];
                }
            case 1:
                if(month>=3){
                    for(int i=0; i<=month-2; i++){
                        sum+=Walkstaistic[i][30][23];
                    }
                    int average=sum/(month-1);
                    int gap=Walkstaistic[month-1][day-1][hour-1]-average;
                }
        }
        return 0;
    }
    protected class getWalk extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = "http://192.168.62.94/query.php";//이부분을 쿼리문으로 바꿔주고 제이슨으로 받아오되 다르게 받아와야함
            String postParameters = "walk=" + params[0];
            try {

                URL url = new URL(serverURL);
                Log.e(serverURL, "url");
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
            mJsonString[0] = ls;
            Log.e("tag",s);
            showResult();
        }

        private void showResult() {
            String TAG_JSON = "root";
            String TAG_ID = "p_id";
            String TAG_WALK = "walk";
            String TAG_DAY = "datetime";
            try {
                JSONObject jsonObject = new JSONObject(ls.substring(ls.indexOf("{"), ls.lastIndexOf("}") + 1));
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String id = item.getString(TAG_ID);
                    String walk = item.getString(TAG_WALK);
                    String day = item.getString(TAG_DAY);

                    WalkData personalData = new WalkData();

                    personalData.setID(id);
                    personalData.setDatetime(day);
                    personalData.setWalk(walk);

                    List_Walk.add(personalData);

                    Log.e("CHECKDATA", walk);
                    Log.e("CHECKDATA", walk);
                }


            } catch (JSONException e) {

                Log.d("Error", "showResult : ", e);
                e.printStackTrace();
            }
            statisticWalk(List_Walk);
        }
    }
    private void statisticWalk(ArrayList<WalkData> list){
        Walkstaistic=new int[12][31][24];
        for(int i=0; i<12; i++){
            for(int j=0; j<31; j++){
                for(int k=0; k<24; k++){
                    Walkstaistic[i][j][k]=k;
                }
            }
        }
        for(int i=0; i<list.size()-1; i++){
            String month=list.get(i).getDatetime().substring(5,6);
            String day=list.get(i).getDatetime().substring(6,8);
            String hour=list.get(i).getDatetime().substring(8);
            Walkstaistic[Integer.parseInt(month)-1][Integer.parseInt(day)-1][Integer.parseInt(hour)-1]=Integer.parseInt(list.get(i).getWalk());

        }
    }
}
