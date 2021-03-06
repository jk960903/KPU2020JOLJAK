package com.example.joljak;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DrugMenu extends AppCompatActivity {
    TextView textView;
    ArrayList<MedicineData> mlist;
    ListView listView;
    String mJsonString=null;
    String json=mJsonString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_menu);
        Intent intent=getIntent();
        mJsonString=intent.getStringExtra("json");
        listView=(ListView) findViewById(R.id.druglist);
        mlist=new ArrayList<>();
        getDrug getdrug=new getDrug();
        getdrug.execute("80");
        textView=(TextView)findViewById(R.id.list);
    }

    private class getDrug extends AsyncTask<String,Void,String> {
        String serverURL = "http://192.168.61.154/medicine_query1.php";
        @Override
        protected String doInBackground(String... strings) {
            String postParameters = "date3=" + strings[0];
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
            //mJsonString = s;
            Log.e("json",mJsonString);
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

                    mlist.add(medicineData);

                }


            } catch (JSONException e) {

                e.printStackTrace();
            }
            setView();
        }
    }
    protected void setView(){
        String[] menu=new String[mlist.size()];
        for(int i=0; i<menu.length; i++){
            menu[i]="약 이름 : "+mlist.get(i).getm_name()+"\t 복용 시간 : "+mlist.get(i).getm_time();
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,menu);
        listView=(ListView)findViewById(R.id.druglist);
        listView.setAdapter(adapter);

    }
}
