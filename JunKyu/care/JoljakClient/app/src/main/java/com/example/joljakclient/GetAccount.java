package com.example.joljakclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.util.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetAccount extends AppCompatActivity {
    EditText ID,PW,Name,PName,Phone;//보호자의 DB 입력 및 환자의 DB입력
    Button Check;
    final static int GETACCOUNT_OK=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_account);
        ID=(EditText)findViewById(R.id.InputID);
        PW=(EditText)findViewById(R.id.inputPW);
        Name=(EditText)findViewById(R.id.name);
        Phone=(EditText)findViewById(R.id.PhoneNumber);
        PName=(EditText)findViewById(R.id.patientName);
        Check=(Button)findViewById(R.id.check);
        Check.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent();
                setResult(GETACCOUNT_OK);
                finish();
            }
            class CheckID extends AsyncTask<String,String,String>{
                String SendMessage,ReceiveMessage;//jsp 송수신 메시지
                @Override
                protected String doInBackground(String... strings){
                    try{
                    String str;
                    URL url=new URL("jsp주소");//JSPDB 연결 링크
                    HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestMethod("POST");
                    OutputStreamWriter outputStreamWriter=new OutputStreamWriter(conn.getOutputStream());
                    SendMessage="ID"+strings[0]+"PW"+strings[1];
                    outputStreamWriter.write(SendMessage);
                    outputStreamWriter.flush();
                    if(conn.getResponseCode()==conn.HTTP_OK){
                        InputStreamReader temp=new InputStreamReader(conn.getInputStream(),"UTF-8");
                        BufferedReader reader=new BufferedReader(temp);
                        StringBuffer buffer=new StringBuffer();
                        while((str=reader.readLine())!=null){
                            buffer.append(str);
                        }
                        ReceiveMessage=buffer.toString();
                    } else{
                        Log.i("결과",conn.getResponseCode()+"실패");
                    }
                }catch (
                MalformedURLException e){
                    e.printStackTrace();
                }
                    catch (
                IOException e){
                    e.printStackTrace();
                }
                    return ReceiveMessage;
                }
            }
        });
    }

}
