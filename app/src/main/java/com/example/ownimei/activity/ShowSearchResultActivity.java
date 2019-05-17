package com.example.ownimei.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ownimei.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nullable;

public class ShowSearchResultActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backButton;
    private ImageView showSearchImage;
    private RelativeLayout ownerCall;
    private TextView showName;
    private TextView showEmail;
    private TextView showModel;
    private TextView showIMEI;
    private TextView showMAC;
    private TextView showStatus;
    private Button doneButton;
    private Button claimButton;

    private String uid;
    private String phoneNumber;
    private String newPhoneNumber;
    private String claimID;
    private String claimIDIMEI2;
    private String claimIDMAC;
    private String status;

    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_search_result);
        firebaseStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        showSearchImage = findViewById(R.id.show_search_image);
        ownerCall = findViewById(R.id.call_imei_relative);
        showName = findViewById(R.id.show_search_name);
        showEmail = findViewById(R.id.show_search_email);
        showModel = findViewById(R.id.show_search_device_name);
        showIMEI = findViewById(R.id.show_search_imei);
        showMAC = findViewById(R.id.show_search_mac);
        showStatus = findViewById(R.id.show_search_status);
        backButton = findViewById(R.id.search_result_back_btn);
        doneButton = findViewById(R.id.show_search_button);
        claimButton = findViewById(R.id.claim_imei_ID);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            showName.setText("Name: " + bundle.getString("userName"));
            showEmail.setText("Email: " + bundle.getString("userEmail"));
            showModel.setText("Model: " + bundle.getString("deviceName"));
            claimID = bundle.getString("phoneImeiOne");
            claimIDIMEI2 = bundle.getString("phoneImeiTwo");
            claimIDMAC = bundle.getString("mac");
            status = bundle.getString("status");
            if (claimID != null) {
                showIMEI.setVisibility(View.VISIBLE);
                showIMEI.setText("IMEI: " + claimID);
            }

            if (claimIDIMEI2 != null) {
                showIMEI.setVisibility(View.VISIBLE);
                showIMEI.setText("IMEI: " + claimIDIMEI2);
            }

            if (claimIDMAC!=null){
                showMAC.setVisibility(View.VISIBLE);
                showMAC.setText("MAC: "+claimIDMAC);
            }
            if (status.equals("Safe mode")){
                showStatus.setText(status);
                showStatus.setTextColor(getResources().getColor(R.color.colorGreen));
            }
            if (status.equals("Lost mode")|| status.equals("Stolen mode")){
                showStatus.setText(status);
                showStatus.setTextColor(getResources().getColor(R.color.colorRed));
            }

            uid = bundle.getString("uid");
            phoneNumber = bundle.getString("userPhone");
        }

        backButton.setOnClickListener(this);
        doneButton.setOnClickListener(this);
        claimButton.setOnClickListener(this);
        ownerCall.setOnClickListener(this);

        findPhoneNumberMethod();
        imageLoadMethod();

    }

    private void imageLoadMethod() {
        StorageReference storageRef = firebaseStorage.getReference();
        storageRef.child("ProfilePictures/" + uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ShowSearchResultActivity.this).load(uri).into(showSearchImage);
            }
        });
    }

    private void findPhoneNumberMethod() {
        DocumentReference documentReference = db.collection("UserInformations").document(uid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                newPhoneNumber = documentSnapshot.getString("userPhone");
                if (newPhoneNumber != null) {
                    ownerCall.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_result_back_btn:
                onBackPressed();
                break;
            case R.id.show_search_button:
                onBackPressed();
                break;
            case R.id.claim_imei_ID:
                claimMethod();
                break;
            case R.id.call_imei_relative:
                if (phoneNumber != null) {
                    callMethod();
                }
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (firebaseAuth.getUid()!=null){
            Intent intent = new Intent(ShowSearchResultActivity.this, UserProfileSearch.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(ShowSearchResultActivity.this, HomePage.class);
            startActivity(intent);
            finish();
        }

    }

    private void claimMethod() {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{"ownimei19@gmail.com"});
        if (claimID!=null){
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Claim ID: " + claimID);
        }else if (claimIDIMEI2!=null){
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Claim ID: " + claimIDIMEI2);
        }
        else {
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Claim ID: " + claimIDMAC);
        }
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Add Message here...");

        emailIntent.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(emailIntent,
                    "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ShowSearchResultActivity.this,
                    "No email clients installed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void callMethod() {
        try {
            Intent my_callIntent = new Intent(Intent.ACTION_DIAL);
            my_callIntent.setData(Uri.parse("tel:" + newPhoneNumber));
            //here the word 'tel' is important for making a call...
            startActivity(my_callIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
