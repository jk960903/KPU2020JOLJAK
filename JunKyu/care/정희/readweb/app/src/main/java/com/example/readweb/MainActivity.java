package com.example.readweb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    Button btnDownload = (Button) findViewById(R.id.download);
        View.OnClickListener downloadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    EditText url = (EditText) findViewById(R.id.url);
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url.getText().toString());
                } else {
                    Toast.makeText(getBaseContext(), "Network is not Available",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        btnDownload.setOnClickListener(downloadListener);
    }
        private boolean isNetworkAvailable() {
            boolean available = false;
            ConnectivityManager connMagr =
                    (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMagr.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable())
                available = true;
            return  available;
        }

        private  String downloadUrl(String strUrl) throws IOException{
            String s = null;
            byte[] buffer = new byte[1000];
            InputStream iStream = null;
            try{
                Log.d("Task1"+strUrl+"AA","url");
                URL url = new URL(strUrl);
                Log.d("Task2","url");
                HttpURLConnection urlConnection = (HttpURLConnection)url
                        .openConnection();
                Log.d("Task3","url");
                urlConnection.connect();
                Log.d("Task4","url");
                iStream = urlConnection.getInputStream();
                Log.d("Task5","url");
                iStream.read(buffer);
                Log.d("Task6","url");
                s = new String(buffer);
                Log.d("Task7","url");
            } catch (Exception e) {
                Log.d("Exception while downloading url",e.toString());
                Log.d("Taskcatch","url");
            } finally {
                iStream.close();
            }
            return s;
        }
        private class DownloadTask extends AsyncTask<String, Integer,String>{
            String s = null;

            protected String doInBackground(String...url){
                try{
                    s = downloadUrl(url[0]);
                }catch (Exception e){
                    Log.d("Background Task ", e.toString());
                }
                return s;
            }
            protected  void onPostExecute(String result){
                TextView tView = (TextView)findViewById(R.id.text);
                tView.setText(result);
                Toast.makeText(getBaseContext(),
                        "Web page downloaded successfully",Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }