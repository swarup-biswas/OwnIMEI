package com.example.ownimei.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.kaopiz.kprogresshud.KProgressHUD;

public class HomePage extends AppCompatActivity implements View.OnClickListener {
    private TextView signInId;
    private TextView signUpId;
    private EditText searchId;
    private LinearLayout webID;
    private Button homeSearchButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(HomePage.this, UserProfile.class));
        }

//        //Search Box start
//
//        searchId.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                try {
//                    if (s.toString().length() == 5) {
//                        String mac = searchId.getText().toString();
//                        if (!mac.isEmpty()) {
//                            searchMethod();
//                        }
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });//Search Box end

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
            builder1.setMessage("Please input IMEI!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                        TextView imei = (TextView) searchDialog.findViewById(R.id.show_search_imei);
                        imei.setText("IMEI: " + addDeviceModel.getPhoneImeiOne());
                        TextView status = (TextView) searchDialog.findViewById(R.id.show_search_status);
                        status.setText("Status: " + addDeviceModel.getStatus());
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
                        addDeviceModel.setPhoneImeiOne(document.getString("phoneImeiTwo"));
                        addDeviceModel.setDeviceName(document.getString("deviceName"));
                        addDeviceModel.setStatus(document.getString("status"));
                        addDeviceModel.setUid(document.getString("uid"));


                        final Dialog searchDialog = new Dialog(HomePage.this);
                        searchDialog.setContentView(R.layout.imei_search_result);
                        TextView name = (TextView) searchDialog.findViewById(R.id.show_search_name);
                        name.setText("Owner name: " + addDeviceModel.getUserName());
                        TextView email = (TextView) searchDialog.findViewById(R.id.show_search_email);
                        email.setText("Email: " + addDeviceModel.getUserEmail());
                        TextView imei = (TextView) searchDialog.findViewById(R.id.show_search_imei);
                        imei.setText("IMEI: " + addDeviceModel.getPhoneImeiTwo());
                        TextView status = (TextView) searchDialog.findViewById(R.id.show_search_status);
                        status.setText("Status: " + addDeviceModel.getStatus());
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
                        searchDialog.setContentView(R.layout.imei_search_result);
                        TextView name = (TextView) searchDialog.findViewById(R.id.show_search_name);
                        name.setText("Owner name: " + addDeviceModel.getUserName());
                        TextView email = (TextView) searchDialog.findViewById(R.id.show_search_email);
                        email.setText("Email: " + addDeviceModel.getUserEmail());
                        TextView imei = (TextView) searchDialog.findViewById(R.id.show_search_imei);
                        imei.setText("MAC: " + addDeviceModel.getMac());
                        TextView status = (TextView) searchDialog.findViewById(R.id.show_search_status);
                        status.setText("Status: " + addDeviceModel.getStatus());
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
