package com.example.joljak;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class DrugMenu extends AppCompatActivity {
    TextView[] textViews;
    ArrayList<Integer> drugtime;
    ArrayList<String> drugname;
    ListView linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_menu);
        Intent intent=getIntent();
        linearLayout=(ListView) findViewById(R.id.druglist);
        drugname=intent.getStringArrayListExtra("drugnamelist");
        drugtime=intent.getIntegerArrayListExtra("druglist");
        textViews=new TextView[drugname.size()];
        for(int i=0; i<textViews.length; i++){
            textViews[i]=new TextView(this);
            textViews[i].setText("약 이름"+drugname.get(i)+"\n"+"복용 시간"+drugtime.get(i));
            textViews[i].setTextSize(20);
            linearLayout.addView(textViews[i],10,10);

            setContentView(linearLayout);
        }
    }
}
