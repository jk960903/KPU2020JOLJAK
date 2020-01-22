package com.example.joljakclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    //보호자 어플 마찬가지로 사용자와 똑같이 GPS + 사용자 정보+현재 상태(보행수,심박수) 필요->여기에 대한 뒷받침이 논문필요
    //서버 연결필요
    //유저와 마찮가지로 클라이언트도 유저와 같은 상황이 필요하지만 중간에 서버가 들어감
    //유저는 유저 어플->서버->클라이언트 어플 이지만
    //클라이언트 어플은 상호작용이 필요가 없음 오직 수신만 필요하다라고 생각
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.mainmenu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent=null;
        switch (item.getItemId()){
            case R.id.GPS:
                intent=new Intent(MainActivity.this,GPSActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}