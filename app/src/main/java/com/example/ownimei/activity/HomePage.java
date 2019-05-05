package com.example.ownimei.activity;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        showProgressBar();
        CollectionReference deviceInfoCollection = db.collection("DeviceInfo");
        Query query = deviceInfoCollection.whereEqualTo("phoneImeiOne", inputImei);
        Query queryTwoIMEI = deviceInfoCollection.whereEqualTo("phoneImeiTwo", inputImei);
        Query queryMac = deviceInfoCollection.whereEqualTo("mac", inputImei);
        final AddDeviceModel addDeviceModel = new AddDeviceModel();
//IMEI 1
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        hideProgressBar();
                        addDeviceModel.setUserName(document.getString("userName"));
                        addDeviceModel.setUserEmail(document.getString("userEmail"));
                        addDeviceModel.setPhoneImeiOne(document.getString("phoneImeiOne"));
                        addDeviceModel.setDeviceName(document.getString("deviceName"));
                        addDeviceModel.setStatus(document.getString("status"));
                        addDeviceModel.setUid(document.getString("uid"));


                        final Dialog searchDialog = new Dialog(HomePage.this);
                        searchDialog.setContentView(R.layout.imei_search_result);
                        TextView name = (TextView) searchDialog.findViewById(R.id.show_search_name);
                        name.setText("Owner name: " + addDeviceModel.getUserName());
                        TextView email = (TextView) searchDialog.findViewById(R.id.show_search_email);
                        email.setText("Email: " + addDeviceModel.getUserEmail());
                        TextView model = (TextView) searchDialog.findViewById(R.id.show_search_device_name);
                        model.setText("Model: " + addDeviceModel.getDeviceName());
                        TextView imei = (TextView) searchDialog.findViewById(R.id.show_search_imei);
                        imei.setText("IMEI: " + addDeviceModel.getPhoneImeiOne());
                        TextView status = (TextView) searchDialog.findViewById(R.id.show_search_status);
                        status.setText("Status: " + addDeviceModel.getStatus());
                        if (addDeviceModel.getStatus().equals("Stolen mode")){
                            ((TextView) searchDialog.findViewById(R.id.show_search_status)).setTextColor(Color.parseColor("#FF5252"));
                        }else if (addDeviceModel.getStatus().equals("Safe mode")){
                            ((TextView) searchDialog.findViewById(R.id.show_search_status)).setTextColor(Color.parseColor("#2DC92D"));
                        }
                        final ImageView image = searchDialog.findViewById(R.id.show_search_image);
                        StorageReference storageRef = firebaseStorage.getReference();
                        storageRef.child("ProfilePictures/" +addDeviceModel.getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(HomePage.this).load(uri).into(image);
                            }
                        });
                        Button dialogButton = (Button) searchDialog.findViewById(R.id.show_search_button);
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                searchId.setText("");
                                searchDialog.dismiss();
                            }
                        });
                        searchDialog.show();
                    }
                } else {
                    hideProgressBar();
                    Toast.makeText(HomePage.this, "This IMEI or Mac not registered!", Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Toast.makeText(HomePage.this, "Opps!! IMEI Not found", Toast.LENGTH_SHORT).show();
            }
        });
//IMEI 2
        queryTwoIMEI.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        hideProgressBar();
                        addDeviceModel.setUserName(document.getString("userName"));
                        addDeviceModel.setUserEmail(document.getString("userEmail"));
//                        addDeviceModel.setPhoneImeiOne(document.getString("phoneImeiTwo"));
                        addDeviceModel.setPhoneImeiTwo(document.getString("phoneImeiTwo"));
                        addDeviceModel.setDeviceName(document.getString("deviceName"));
                        addDeviceModel.setStatus(document.getString("status"));
                        addDeviceModel.setUid(document.getString("uid"));



                        final Dialog searchDialog = new Dialog(HomePage.this);
                        searchDialog.setContentView(R.layout.imei_search_result);
                        TextView name = (TextView) searchDialog.findViewById(R.id.show_search_name);
                        name.setText("Owner name: " + addDeviceModel.getUserName());
                        TextView email = (TextView) searchDialog.findViewById(R.id.show_search_email);
                        email.setText("Email: " + addDeviceModel.getUserEmail());
                        TextView model = (TextView) searchDialog.findViewById(R.id.show_search_device_name);
                        model.setText("Model: " + addDeviceModel.getDeviceName());
                        TextView imei = (TextView) searchDialog.findViewById(R.id.show_search_imei);
                        imei.setText("IMEI: " + addDeviceModel.getPhoneImeiTwo());
                        TextView status = (TextView) searchDialog.findViewById(R.id.show_search_status);
                        status.setText("Status: " + addDeviceModel.getStatus());
                        if (addDeviceModel.getStatus().equals("Stolen mode")){
                            ((TextView) searchDialog.findViewById(R.id.show_search_status)).setTextColor(Color.parseColor("#FF5252"));
                        }else if (addDeviceModel.getStatus().equals("Safe mode")){
                            ((TextView) searchDialog.findViewById(R.id.show_search_status)).setTextColor(Color.parseColor("#2DC92D"));
                        }
                        final ImageView image = searchDialog.findViewById(R.id.show_search_image);
                        StorageReference storageRef = firebaseStorage.getReference();
                        storageRef.child("ProfilePictures/" +addDeviceModel.getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(HomePage.this).load(uri).into(image);
                            }
                        });
                        Button dialogButton = (Button) searchDialog.findViewById(R.id.show_search_button);
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                searchId.setText("");
                                searchDialog.dismiss();
                            }
                        });
                        searchDialog.show();
                    }
                } else {
                    hideProgressBar();
                    Toast.makeText(HomePage.this, "This IMEI or MAC not registered!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Toast.makeText(HomePage.this, "Opps!! IMEI Not found", Toast.LENGTH_SHORT).show();
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

                        final Dialog searchDialog = new Dialog(HomePage.this);
                        searchDialog.setContentView(R.layout.mac_search_result);
                        TextView name = (TextView) searchDialog.findViewById(R.id.show_search_name);
                        name.setText("Owner name: " + addDeviceModel.getUserName());
                        TextView email = (TextView) searchDialog.findViewById(R.id.show_search_email);
                        email.setText("Email: " + addDeviceModel.getUserEmail());
                        TextView model = (TextView) searchDialog.findViewById(R.id.show_search_device_name);
                        model.setText("Model: " + addDeviceModel.getDeviceName());
                        TextView mac = (TextView) searchDialog.findViewById(R.id.show_search_mac);
                        mac.setText("MAC: " + addDeviceModel.getMac());
                        TextView status = (TextView) searchDialog.findViewById(R.id.show_search_status);
                        status.setText("Status: " + addDeviceModel.getStatus());
                        if (addDeviceModel.getStatus().equals("Stolen mode")){
                            ((TextView) searchDialog.findViewById(R.id.show_search_status)).setTextColor(Color.parseColor("#FF5252"));
                        }else if (addDeviceModel.getStatus().equals("Safe mode")){
                            ((TextView) searchDialog.findViewById(R.id.show_search_status)).setTextColor(Color.parseColor("#2DC92D"));
                        }
                        final ImageView image = searchDialog.findViewById(R.id.show_search_image);
                        StorageReference storageRef = firebaseStorage.getReference();
                        storageRef.child("ProfilePictures/" +addDeviceModel.getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(HomePage.this).load(uri).into(image);
                            }
                        });
                        Button dialogButton = (Button) searchDialog.findViewById(R.id.show_search_button);
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                searchId.setText("");
                                searchDialog.dismiss();
                            }
                        });
                        searchDialog.show();
                    }
                } else {
                    hideProgressBar();
                    Toast.makeText(HomePage.this, "This IMEI or Mac not registered!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Toast.makeText(HomePage.this, "Opps!! MAC address Not found", Toast.LENGTH_SHORT).show();
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
