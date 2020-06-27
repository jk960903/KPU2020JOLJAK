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

import java.util.Calendar;

public class SetDrug extends AppCompatActivity {
    EditText drugname,timeHour;
    Button timeOK;
    Intent intent;
    TimePicker timePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_drug);
        intent=getIntent();
        drugname=(EditText) findViewById(R.id.drugname);
        timeHour=(EditText)findViewById(R.id.drugtime);
        timeOK=(Button)findViewById(R.id.OK);
        timeOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=timeHour.getText().toString();
                int time=Integer.parseInt(s);
                intent.putExtra("time",time);
                s=drugname.getText().toString();
                intent.putExtra("name",s);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });



    }

}
