package com.example.myroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    private Animation topAni, botAni;
    private ImageView logo;
    private TextView appName;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }
    private void init(){
        logo = findViewById(R.id.splash_logo);
        appName = findViewById(R.id.splash_appName);

        topAni = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        topAni.setDuration(2000);
        botAni = AnimationUtils.loadAnimation(this, R.anim.bot_animation);
        botAni.setDuration(2000);
        logo.setAnimation(topAni);
        appName.setAnimation(botAni);
        auth = FirebaseAuth.getInstance();

        nextActivity();
    }

    private void nextActivity(){

        int SPLASH_TIME = 4000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(auth.getCurrentUser()!=null){
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();

            }
        }, SPLASH_TIME);
    }

}