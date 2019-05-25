package com.example.ownimei.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ownimei.R;

public class AboutUs extends AppCompatActivity {
    private ImageView imageView;
    private TextView wvOwnIMEI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        imageView = findViewById(R.id.about_us_back_button);
        wvOwnIMEI = findViewById(R.id.wv_university);
        wvOwnIMEI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutUs.this,"Under developing.",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(AboutUs.this, ownIMEIWebActivity.class));
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
