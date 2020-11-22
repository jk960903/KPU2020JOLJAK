package com.example.joljak;
//로그인 정보로 데모버전에서는 사용하지 않음
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_login);
                //강의에서 final을 추가시켜줌
                final EditText idText = (EditText)findViewById(R.id.idText);
                final EditText passwordText = (EditText)findViewById(R.id.passwordText);
                final Button loginbtn = (Button)findViewById(R.id.loginbtn);
                final TextView registerbtn = (TextView)findViewById(R.id.registerbtn);

                registerbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                Intent registerIntent = new Intent(Login.this, getAccount.class);
                Login.this.startActivity(registerIntent);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String p_id = idText.getText().toString();
                final String p_pw = passwordText.getText().toString();
                //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            System.out.println(response);
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            Toast.makeText(getApplicationContext(), "success"+success, Toast.LENGTH_SHORT).show();
                            //서버에서보내준 값이 true이면?
                            if(success){
                                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                                String p_id = jsonResponse.getString("p_id");
                                String p_pw = jsonResponse.getString("p_pw");
                                //로그인에 성공했으므로 MainActivity로 넘어감
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("p_id", p_id);
                                intent.putExtra("p_pw", p_pw);
                                intent.putExtra("login",success);
                                setResult(Activity.RESULT_OK,intent);
                                Login.this.startActivity(intent);
                            }else{//로그인 실패시
                                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                builder.setMessage("Login failed")
                                        .setNegativeButton("retry", null)
                                        .create()
                                        .show();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(p_id, p_pw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Login.this);
                queue.add(loginRequest);
            }
        });
    }
}