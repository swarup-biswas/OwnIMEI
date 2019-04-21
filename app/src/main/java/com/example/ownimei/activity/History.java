package com.example.ownimei.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.ownimei.R;

public class History extends AppCompatActivity implements View.OnClickListener {
    private ImageView historyBackButoon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyBackButoon = findViewById(R.id.history_back_btn);

        historyBackButoon.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_back_btn:
                historyBackMethod();
                break;
        }
    }

    private void historyBackMethod() {
        onBackPressed();

    }
}
