package com.example.joljakclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.joljakclient.MainActivity;
import com.example.joljakclient.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splashscreen);
        LogoLauncher logoLauncher =new LogoLauncher();
        logoLauncher.start();
    }
    private class LogoLauncher extends Thread{
        public  void run(){
            try{
                sleep(2000);
            }catch (InterruptedException e)
            {   e.printStackTrace();}
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(intent);
            SplashScreen.this.finish();
        }
    }
}
