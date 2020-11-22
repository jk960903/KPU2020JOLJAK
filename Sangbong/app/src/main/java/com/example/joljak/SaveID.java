package com.example.joljak;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
//사용자 정보 저장 액티비티
public class SaveID extends AppCompatActivity {

    private EditText UserId, UserAddress, CareId, CarePhone;
    private Button Send, Cancel;
    private String UAddress, UI, CP, CI;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_id);
        intent = getIntent();
        IdFind();
        Send.setOnClickListener(new Button.OnClickListener() {//완료 버튼
            @Override
            public void onClick(View v) {
                UAddress = UserAddress.getText().toString();
                UI = UserId.getText().toString();
                CP = CarePhone.getText().toString();
                CI = CareId.getText().toString();
                intent.putExtra("UserID", UI);
                intent.putExtra("UserAddress", UAddress);
                intent.putExtra("CareName", CI);
                intent.putExtra("CarePhone", CP);
                SendID();
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });
        Cancel.setOnClickListener(new Button.OnClickListener() {//종료 버튼
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void IdFind() {
        UserId = (EditText) findViewById(R.id.ClientName);
        UserAddress = (EditText) findViewById(R.id.Address);
        CareId = (EditText) findViewById(R.id.CareID);
        CarePhone = (EditText) findViewById(R.id.carePhone);
        Send = (Button) findViewById(R.id.complete);
        Cancel = (Button) findViewById(R.id.cancel);
    }

    private void SendID() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println(response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        SetRequest setRequest = new SetRequest(UI, UAddress, CI, CP, responseListener);
        RequestQueue queue = Volley.newRequestQueue(SaveID.this);
        queue.add(setRequest);
    }
}
