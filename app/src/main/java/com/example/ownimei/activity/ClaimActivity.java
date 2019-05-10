package com.example.ownimei.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ownimei.R;

public class ClaimActivity extends AppCompatActivity {
    private String name;
    private String model;
    private String email;
    private String imei;
    private String mac;
    private EditText claimMessage;
    private ImageView claimImage;
    private RelativeLayout uploadReceipt;
    private Button claimButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            name = bundle.getString("name");
            model = bundle.getString("model");
            email = bundle.getString("email");
            imei = bundle.getString("mac");
            mac = bundle.getString("imei");
        }
        claimMessage = findViewById(R.id.claim_message_ID);
        claimImage = findViewById(R.id.claim_image_ID);
        uploadReceipt = findViewById(R.id.upload_receipt_ID);
        claimButton = findViewById(R.id.claim_button);
        //
        claimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                        new String[]{  "ownimei19@gmail.com"});
                if(imei!=null){
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Claim ID: "+imei);
                }else {
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Claim ID: "+mac);
                }
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Add Message here...");

                emailIntent.setType("message/rfc822");

                try {
                    startActivity(Intent.createChooser(emailIntent,
                            "Send email using..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ClaimActivity.this,
                            "No email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        //


    }
}
