package com.example.joljak;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SaveID extends AppCompatActivity {// -> 인터페이스 조정 필요 액티비티상으로 건드릴지 아니면 서버에서 가져와서 컨트롤 할지에 대한 컨트롤 필요

    private EditText UserId,UserAddress,CareId,CarePhone;
    private Button Send,Cancel;
    private String UAddress,UI,CP,CI;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_id);
        intent=getIntent();
        IdFind();
        Send.setOnClickListener(new Button.OnClickListener() {//완료 버튼
            @Override
            public void onClick(View v) {
                 UAddress=UserAddress.getText().toString();
                 UI=UserId.getText().toString();
                 CP=CarePhone.getText().toString();
                 CI=CareId.getText().toString();
                intent.putExtra("UserID",UI);
                intent.putExtra("UserAddress",UAddress);
                intent.putExtra("CareName",CI);
                intent.putExtra("CarePhone",CP);
                setResult(Activity.RESULT_OK,intent);
                finish();

            }
        });
        Cancel.setOnClickListener(new Button.OnClickListener(){//종료 버튼
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }
    private void IdFind(){
        UserId=(EditText)findViewById(R.id.ClientName);
        UserAddress=(EditText)findViewById(R.id.Address);
        CareId=(EditText)findViewById(R.id.CareID);
        CarePhone=(EditText)findViewById(R.id.carePhone);
        Send=(Button)findViewById(R.id.complete);
        Cancel=(Button)findViewById(R.id.cancel);
    }
}
