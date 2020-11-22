package com.example.joljakclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.lang.String;

public class LoginActivity extends AppCompatActivity {//로그인 클래스 데모 버전에서는 사용하지 않음
    //로그인 페이지
    //DB연동 안하면 작동 불가 INSTANCE 연결 필요
    EditText LoginID,LoginPassword;
    Button loginButton,FindAcount,GetAccount;
    final static int LOGIN_OK=0;
    final static int GETACCOUNT_OK=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginID=(EditText) findViewById(R.id.loginID);
        LoginPassword=(EditText)findViewById(R.id.loginPassword);
        loginButton=(Button)findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                //로그인 완료
                //메인으로 이동하도록 설정
                //JSP파일 접근하여 데이터 읽어들이고
                Intent intent=new Intent();
                String ID=LoginID.getText().toString();
                String PassWord=LoginPassword.getText().toString();
                try {
                    makeTask task = new makeTask();
                    String result = task.execute(ID, PassWord).get();//데이터 전달
                    if(result.equals("true")){
                        Log.i("성공여부","성공");
                        Toast.makeText(LoginActivity.this,"로그인",Toast.LENGTH_SHORT).show();
                        setResult(LOGIN_OK);
                        finish();
                    } else if(result.equals("false")){
                        Log.i("성공여부","실패 아이디 또는 비밀번호 오류");
                        Toast.makeText(LoginActivity.this,"아이디 또는 비밀번호 오류",Toast.LENGTH_SHORT).show();
                        LoginID.setText("");
                        LoginPassword.setText("");
                    } else if(result.equals("noId")){
                        Log.i("성공여부","실패 아이디 없음");
                        Toast.makeText(LoginActivity.this, "아이디 존재하지 않음", Toast.LENGTH_SHORT).show();
                        LoginID.setText("");
                        LoginPassword.setText("");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            class makeTask extends AsyncTask<String,Void,String>{
                String sendMessage,receivemessage;
                @Override
                protected String doInBackground(String... strings){
                    try{
                        String str;
                        URL url=new URL("jsp주소");
                        HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.setRequestMethod("POST");
                        OutputStreamWriter outputStreamWriter=new OutputStreamWriter(conn.getOutputStream());
                        sendMessage="ID"+strings[0]+"PW"+strings[1];
                        outputStreamWriter.write(sendMessage);
                        outputStreamWriter.flush();
                        if(conn.getResponseCode()==conn.HTTP_OK){
                            InputStreamReader temp=new InputStreamReader(conn.getInputStream(),"UTF-8");
                            BufferedReader reader=new BufferedReader(temp);
                            StringBuffer buffer=new StringBuffer();
                            while((str=reader.readLine())!=null){
                                buffer.append(str);
                            }
                            receivemessage=buffer.toString();
                        } else{
                            Log.i("결과",conn.getResponseCode()+"실패");
                        }
                    }catch (MalformedURLException e){
                        e.printStackTrace();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                    return receivemessage;
                }
            }
        });
        FindAcount.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                //찾는 페이지로 이동
            }
        });

    }
    @Override
    protected void onActivityResult(int requestcode,int resultcode, Intent data){
        super.onActivityResult(requestcode,resultcode,data);
        switch (requestcode){
            case GETACCOUNT_OK:

        }
    }
}
