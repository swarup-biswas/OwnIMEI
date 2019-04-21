package com.example.ownimei.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ownimei.R;
import com.example.ownimei.StaticClass.StaticClass;
import com.example.ownimei.pojo.AddDeviceModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.kaopiz.kprogresshud.KProgressHUD;

public class UserProfileSearch extends AppCompatActivity implements View.OnClickListener {
    private FirebaseFirestore db;
    private ImageView backButton;
    private EditText userSearch;
    private Button userSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_search);
        db = FirebaseFirestore.getInstance();
        userSearch = findViewById(R.id.user_search_ID);
        backButton = findViewById(R.id.sign_in_search_back_btn);
        userSearchButton = findViewById(R.id.user_search_button);
        backButton.setOnClickListener(this);
        userSearchButton.setOnClickListener(this);
    }

    //On Click Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_search_back_btn:
                backButtonSearch();
                break;
            case R.id.user_search_button:
                searchIMEI();
                break;
        }
    }

    private void searchIMEI() {
        String inputImei = userSearch.getText().toString();
        if (inputImei.isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(UserProfileSearch.this);
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

                        final Dialog searchDialog = new Dialog(UserProfileSearch.this);
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
                                userSearch.setText("");
                                searchDialog.dismiss();
                            }
                        });
                        searchDialog.show();
                    }
                } else {
                    hideProgressBar();
                    Toast.makeText(UserProfileSearch.this, "else", Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Toast.makeText(UserProfileSearch.this, "Opps!! IMEI Not found", Toast.LENGTH_SHORT).show();
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


                        final Dialog searchDialog = new Dialog(UserProfileSearch.this);
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
                                userSearch.setText("");
                                searchDialog.dismiss();
                            }
                        });
                        searchDialog.show();
                    }
                } else {
                    hideProgressBar();
                    Toast.makeText(UserProfileSearch.this, "else", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Toast.makeText(UserProfileSearch.this, "Opps!! IMEI Not found", Toast.LENGTH_SHORT).show();
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

                        final Dialog searchDialog = new Dialog(UserProfileSearch.this);
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
                                userSearch.setText("");
                                searchDialog.dismiss();
                            }
                        });
                        searchDialog.show();
                    }
                } else {
                    hideProgressBar();
                    Toast.makeText(UserProfileSearch.this, "else", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Toast.makeText(UserProfileSearch.this, "Opps!! MAC address Not found", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void backButtonSearch() {
        onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        StaticClass.hideKeyboard(this);
        return super.dispatchTouchEvent(ev);
    }

    //Progressbar method
    static KProgressHUD kProgressHUD;

    public void showProgressBar() {
        kProgressHUD = KProgressHUD.create(UserProfileSearch.this)
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
