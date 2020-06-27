package com.example.joljak;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//회원가입 아직 미완성
public class getAccount extends AppCompatActivity {
    Button btn;
    EditText edit1,edit2,edit3,edit4,edit5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_account);
        btn=(Button)findViewById(R.id.AccountButton);
        edit1=(EditText)findViewById(R.id.AccountID);
        edit2=(EditText)findViewById(R.id.AccountPWD);
        edit3=(EditText)findViewById(R.id.p_name);
        edit4=(EditText)findViewById(R.id.birth);
        edit5=(EditText)findViewById(R.id.Phonenum);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=edit1.getText().toString();
                String pwd=edit2.getText().toString();
                String name=edit3.getText().toString();
                String birth=edit4.getText().toString();
                String Phone=edit5.getText().toString();
                RegisterRequst send=new RegisterRequst();
                send.execute(id,pwd,name,birth,Phone);
                send.doInBackground(id,pwd,name,birth,Phone);
                finish();
            }
        });
    }

}
    class RegisterRequst extends AsyncTask<String, Void, String> {
      @Override
        protected String doInBackground(String... strings) {
            String sendmessage;
            String receivemessage=null;
            try{
                String str;
                URL url=new URL("http://15.164.94.165/join.jsp");
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStreamWriter osw=new OutputStreamWriter(conn.getOutputStream());
                Log.i("servercheck","check");
                sendmessage="p_id"+strings[0]+"&p_pwd"+strings[1]+"&p_name"+strings[2]
                        +"&p_birth"+strings[3]+"&p_phone"+strings[4];
                osw.write(sendmessage);
                osw.flush();
               if(conn.getResponseCode()==conn.HTTP_OK){
                    InputStreamReader tmp=new InputStreamReader(conn.getInputStream(),"UTF-8");

                    BufferedReader reader= new BufferedReader(tmp);
                    StringBuffer buffer=new StringBuffer();
                    while((str=reader.readLine())!=null){
                        buffer.append(str);
                    }
                    receivemessage=buffer.toString();
                }
                else{
                    Log.i("통신 결과",conn.getResponseCode()+"에러");

                }
            }
           catch(MalformedURLException e){
                e.printStackTrace();
                Log.i("error","malformederror");
                  }
            catch(IOException e){
                e.printStackTrace();
                Log.i("ioerror","IOError");
            }
            return receivemessage;

        }
}


