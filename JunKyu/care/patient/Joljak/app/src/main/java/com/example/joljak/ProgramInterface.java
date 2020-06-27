package com.example.joljak;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ProgramInterface extends AppCompatActivity {//제품 인터페이스에 대한 모든 정보 저장
    //Bluetooth 정보 가져오고 다른 액티비티 정보 다 가져와야함
    //프로그램 정보에 관한것들 다 가져와야함
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_interface);
    }
}
