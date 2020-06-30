package com.example.joljak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class SetDrug extends AppCompatActivity {
    int Hout,Minute=0;
    Button timeOK;
    Intent intent;
    TimePicker timePicker;
    EditText drugname;
    String stringtime;
    String druggetname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_drug);
        intent=getIntent();
        timePicker=(TimePicker)findViewById(R.id.timePicker);
        drugname=(EditText)findViewById(R.id.drugname);
        timeOK=(Button)findViewById(R.id.OK);
        timeOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hout=timePicker.getHour();
                Minute=timePicker.getMinute();
                stringtime=Integer.toString(Hout)+":"+Integer.toString(Minute);
                druggetname=drugname.getText().toString();
                intent.putExtra("drugname",drugname.getText().toString());
                intent.putExtra("drugtime",Hout);
                intent.putExtra("drugmin",Minute);
                setResult(3,intent);
                finish();
            }
        });



    }
    private void DrugSend(){
        final Response.Listener<String> responseListner=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse=new JSONObject(response);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

        };
        DrugRequest drugRequest=new DrugRequest("setdrug",druggetname,stringtime,responseListner);
        RequestQueue queue= Volley.newRequestQueue(SetDrug.this);
        queue.add(drugRequest);
    }

}
