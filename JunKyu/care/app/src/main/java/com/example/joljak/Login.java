package com.example.joljak;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.lang.String;
public class Login extends AppCompatActivity {
    //로그인 페이지로 EC2 로 Elastic Ips 설정후 사용해야함
    //url은 그래서 빈칸 혹은 주석처리로 할 예정
    //DB연동해야함 Client Login Activity 참조
    EditText Id, passWord;
    Button Login, Account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Id = (EditText) findViewById(R.id.Id);
        passWord = (EditText) findViewById(R.id.password);
        Login = (Button) findViewById(R.id.login);
        Account = (Button) findViewById(R.id.account);
    }

    class RegisterRequst extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String sendmessage, receivemessage = null;
            try {
                String str;
                URL url = new URL("http://15.164.226.117/join.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendmessage = "p_id" + strings[0] + "&p_pwd" + strings[1] + "&p_name" + strings[2]
                        + "&p_birth" + strings[3] + "&p_phone" + strings[4];
                osw.write(sendmessage);
                osw.flush();
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receivemessage = buffer.toString();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.i("error", "malformederror");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("ioerror", "IOError");
            }
            return receivemessage;
        }
    }
}