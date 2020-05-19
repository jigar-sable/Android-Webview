package com.sam.webapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    Animation topanim,btmanim;
    ImageView img;
    TextView txt1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        img=findViewById(R.id.imgView2);
        txt1=findViewById(R.id.txt);
        //load animation from drawable
        topanim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        btmanim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        //set animation to image and text
        img.setAnimation(topanim);
        txt1.setAnimation(btmanim);

        int splash_Screen = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, splash_Screen);
    }
}
