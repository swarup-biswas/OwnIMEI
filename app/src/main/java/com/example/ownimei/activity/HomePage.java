package com.example.ownimei.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ownimei.R;
import com.example.ownimei.StaticClass.StaticClass;
import com.example.ownimei.pojo.AddDeviceModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaopiz.kprogresshud.KProgressHUD;

public class HomePage extends AppCompatActivity implements View.OnClickListener {
    private TextView signInId;
    private TextView signUpId;
    private EditText searchId;
    private LinearLayout webID;
    private Button homeSearchButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;
    private int x;
    private int y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        signInId = findViewById(R.id.home_sign_in_ID);
        signUpId = findViewById(R.id.home_sign_up_ID);
        searchId = findViewById(R.id.home_search_ID);
        searchId.setOnEditorActionListener(editorActionListener);
        webID = findViewById(R.id.home_website_ID);
        homeSearchButton = findViewById(R.id.home_search_button);
        homeSearchButton.setOnClickListener(this);
        signInId.setOnClickListener(this);
        signUpId.setOnClickListener(this);
//        searchId.setOnClickListener(this);
        webID.setOnClickListener(this);

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(HomePage.this, UserProfileSearch.class));
        }

    }

    //Keyboard edit start
    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            searchMethod();
            return false;
        }
    };
    //end

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.home_sign_in_ID:
                Intent signInIntent = new Intent(HomePage.this, SignIn.class);
                startActivity(signInIntent);
                break;
            case R.id.home_sign_up_ID:
                Intent signUpIntent = new Intent(HomePage.this, SignUp.class);
                startActivity(signUpIntent);
                break;
            case R.id.home_search_button:
                searchMethod();
                break;
            case R.id.home_search_ID:
                //TODO search action

                break;
            case R.id.home_website_ID:
                //TODO add website link
                break;
        }

    }

    private void searchMethod() {
        String inputImei = searchId.getText().toString();
        if (inputImei.isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(HomePage.this);
            builder1.setMessage("Please enter your IMEI/MAC id.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder1.show();
            return;
        }
        CollectionReference deviceInfoCollection = db.collection("DeviceInfo");
        Query query = deviceInfoCollection.whereEqualTo("phoneImeiOne", inputImei);
        Query queryTwoIMEI = deviceInfoCollection.whereEqualTo("phoneImeiTwo", inputImei);
        Query queryMac = deviceInfoCollection.whereEqualTo("mac", inputImei);
        final AddDeviceModel addDeviceModel = new AddDeviceModel();
//IMEI 1
        showProgressBar();
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    x = 1;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        hideProgressBar();
                        addDeviceModel.setUserName(document.getString("userName"));
                        addDeviceModel.setUserEmail(document.getString("userEmail"));
                        addDeviceModel.setPhoneImeiOne(document.getString("phoneImeiOne"));
                        addDeviceModel.setDeviceName(document.getString("deviceName"));
                        addDeviceModel.setStatus(document.getString("status"));
                        addDeviceModel.setUid(document.getString("uid"));
                        addDeviceModel.setUserPhone(document.getString("userPhone"));

                        Intent intentHomeIMEI1 = new Intent(HomePage.this,ShowSearchResultActivity.class);
                        intentHomeIMEI1.putExtra("userName",addDeviceModel.getUserName());
                        intentHomeIMEI1.putExtra("userEmail",addDeviceModel.getUserEmail());
                        intentHomeIMEI1.putExtra("phoneImeiOne",addDeviceModel.getPhoneImeiOne());
                        intentHomeIMEI1.putExtra("deviceName",addDeviceModel.getDeviceName());
                        intentHomeIMEI1.putExtra("status",addDeviceModel.getStatus());
                        intentHomeIMEI1.putExtra("uid",addDeviceModel.getUid());
                        intentHomeIMEI1.putExtra("userPhone",addDeviceModel.getUserPhone());
                        startActivity(intentHomeIMEI1);
                        finish();
                    }
                } else {
                    hideProgressBar();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
            }
        });
//IMEI 2
        queryTwoIMEI.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    y = 2;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        hideProgressBar();
                        addDeviceModel.setUserName(document.getString("userName"));
                        addDeviceModel.setUserEmail(document.getString("userEmail"));
                        addDeviceModel.setPhoneImeiTwo(document.getString("phoneImeiTwo"));
                        addDeviceModel.setDeviceName(document.getString("deviceName"));
                        addDeviceModel.setStatus(document.getString("status"));
                        addDeviceModel.setUid(document.getString("uid"));
                        addDeviceModel.setUserPhone(document.getString("userPhone"));

                        Intent intentHomeIMEI2 = new Intent(HomePage.this,ShowSearchResultActivity.class);
                        intentHomeIMEI2.putExtra("userName",addDeviceModel.getUserName());
                        intentHomeIMEI2.putExtra("userEmail",addDeviceModel.getUserEmail());
                        intentHomeIMEI2.putExtra("phoneImeiTwo",addDeviceModel.getPhoneImeiTwo());
                        intentHomeIMEI2.putExtra("deviceName",addDeviceModel.getDeviceName());
                        intentHomeIMEI2.putExtra("status",addDeviceModel.getStatus());
                        intentHomeIMEI2.putExtra("uid",addDeviceModel.getUid());
                        intentHomeIMEI2.putExtra("userPhone",addDeviceModel.getUserPhone());
                        startActivity(intentHomeIMEI2);
                        finish();
                    }
                } else {
                    hideProgressBar();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
            }
        });
//Mac
        queryMac.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        hideProgressBar();
                        addDeviceModel.setUserName(document.getString("userName"));
                        addDeviceModel.setUserEmail(document.getString("userEmail"));
                        addDeviceModel.setMac(document.getString("mac"));
                        addDeviceModel.setDeviceName(document.getString("deviceName"));
                        addDeviceModel.setStatus(document.getString("status"));
                        addDeviceModel.setUid(document.getString("uid"));
                        addDeviceModel.setUserPhone(document.getString("userPhone"));

                        Intent intentHomeMAC = new Intent(HomePage.this,ShowSearchResultActivity.class);
                        intentHomeMAC.putExtra("userName",addDeviceModel.getUserName());
                        intentHomeMAC.putExtra("userEmail",addDeviceModel.getUserEmail());
                        intentHomeMAC.putExtra("mac",addDeviceModel.getMac());
                        intentHomeMAC.putExtra("deviceName",addDeviceModel.getDeviceName());
                        intentHomeMAC.putExtra("status",addDeviceModel.getStatus());
                        intentHomeMAC.putExtra("uid",addDeviceModel.getUid());
                        intentHomeMAC.putExtra("userPhone",addDeviceModel.getUserPhone());
                        startActivity(intentHomeMAC);
                        finish();

                    }
                } else {
                    hideProgressBar();
                    if (x != 1 && y != 2) {
                        Toast.makeText(HomePage.this, "This IMEI or Mac not registered!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                if (x != 1 && y != 2) {
                    Toast.makeText(HomePage.this, "Opps!! Entered address Not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        StaticClass.hideKeyboard(this);
        return super.dispatchTouchEvent(ev);
    }

    //Progressbar method
    static KProgressHUD kProgressHUD;

    public void showProgressBar() {
        kProgressHUD = KProgressHUD.create(HomePage.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
        kProgressHUD.show();
    }

    public void hideProgressBar() {
        if (kProgressHUD != null)
            kProgressHUD.dismiss();
    }

}
