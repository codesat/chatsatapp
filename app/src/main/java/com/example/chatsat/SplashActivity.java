package com.example.chatsat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    private static int splashtimer=3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        // to remove the status bar above of tower ,battery
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ////to delay the activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // after running this splash i have to go to main activity
                Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent); // intent started
                finish(); //when someone press back then it wont allow to
                // go back on splash activity/previous activity in general


            }
        },splashtimer); // means this actvity will be shown for 3 seconds as defined above



    }
}