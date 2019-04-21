package com.example.ownimei.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.ownimei.R;

public class Splash extends AppCompatActivity {
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        linearLayout = findViewById(R.id.splash_ID);

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                startHomePage();
            }
        });
        thread.start();

    }

    private void doWork() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startHomePage() {
        Intent intent = new Intent(Splash.this, HomePage.class);
        startActivity(intent);
        finish();
    }
}
