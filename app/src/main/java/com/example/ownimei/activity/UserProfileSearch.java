package com.example.ownimei.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.StorageReference;
import com.kaopiz.kprogresshud.KProgressHUD;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ownimei.activity.SignUp.USER_ID;

public class UserProfileSearch extends AppCompatActivity implements View.OnClickListener {
    //    private ImageView backButton;
    private EditText userSearch;
    private Button userSearchButton;
    private CircleImageView circleImageView;
    private FirebaseAuth authOwner;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_search);
        authOwner = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        userSearch = findViewById(R.id.user_search_ID);
//        backButton = findViewById(R.id.sign_in_search_back_btn);
        userSearchButton = findViewById(R.id.user_search_button);
        circleImageView = findViewById(R.id.user_profile_search_image_ID);
//        backButton.setOnClickListener(this);
        userSearchButton.setOnClickListener(this);
        circleImageView.setOnClickListener(this);
        if (!StaticClass.isConnected(UserProfileSearch.this)) {
            StaticClass.buildDialog(UserProfileSearch.this);
        } else {
            loadImage();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }

    private void loadImage() {
        SharedPreferences sharedUId = getSharedPreferences(USER_ID, MODE_PRIVATE);
        String uId = sharedUId.getString("get_UID", "");
        StorageReference storageRef = firebaseStorage.getReference();
        storageRef.child("ProfilePictures/" + uId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                hideProgressBar();
                Glide.with(UserProfileSearch.this).load(uri).into(circleImageView);
            }
        });
    }

    //On Click Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.sign_in_search_back_btn:
//                backButtonSearch();
//                break;
            case R.id.user_profile_search_image_ID:
                startActivity(new Intent(UserProfileSearch.this, UserProfile.class));
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
                        TextView model = (TextView) searchDialog.findViewById(R.id.show_search_device_name);
                        model.setText("Model: " + addDeviceModel.getDeviceName());
                        TextView imei = (TextView) searchDialog.findViewById(R.id.show_search_imei);
                        imei.setText("IMEI: " + addDeviceModel.getPhoneImeiOne());
                        TextView status = (TextView) searchDialog.findViewById(R.id.show_search_status);
                        status.setText("Status: " + addDeviceModel.getStatus());
                        if (addDeviceModel.getStatus().equals("Stolen mode")) {
                            ((TextView) searchDialog.findViewById(R.id.show_search_status)).setTextColor(Color.parseColor("#FF5252"));
                        } else if (addDeviceModel.getStatus().equals("Safe mode")) {
                            ((TextView) searchDialog.findViewById(R.id.show_search_status)).setTextColor(Color.parseColor("#2DC92D"));
                        }
                        final ImageView image = searchDialog.findViewById(R.id.show_search_image);
                        StorageReference storageRef = firebaseStorage.getReference();
                        storageRef.child("ProfilePictures/" + addDeviceModel.getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(UserProfileSearch.this).load(uri).into(image);
                            }
                        });

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
//                        addDeviceModel.setPhoneImeiOne(document.getString("phoneImeiTwo"));
                        addDeviceModel.setPhoneImeiTwo(document.getString("phoneImeiTwo"));
                        addDeviceModel.setDeviceName(document.getString("deviceName"));
                        addDeviceModel.setStatus(document.getString("status"));
                        addDeviceModel.setUid(document.getString("uid"));


                        final Dialog searchDialog = new Dialog(UserProfileSearch.this);
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
                        if (addDeviceModel.getStatus().equals("Stolen mode")) {
                            ((TextView) searchDialog.findViewById(R.id.show_search_status)).setTextColor(Color.parseColor("#FF5252"));
                        } else if (addDeviceModel.getStatus().equals("Safe mode")) {
                            ((TextView) searchDialog.findViewById(R.id.show_search_status)).setTextColor(Color.parseColor("#2DC92D"));
                        }
                        final ImageView image = searchDialog.findViewById(R.id.show_search_image);
                        StorageReference storageRef = firebaseStorage.getReference();
                        storageRef.child("ProfilePictures/" + addDeviceModel.getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(UserProfileSearch.this).load(uri).into(image);
                            }
                        });
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
                        if (addDeviceModel.getStatus().equals("Stolen mode")) {
                            ((TextView) searchDialog.findViewById(R.id.show_search_status)).setTextColor(Color.parseColor("#FF5252"));
                        } else if (addDeviceModel.getStatus().equals("Safe mode")) {
                            ((TextView) searchDialog.findViewById(R.id.show_search_status)).setTextColor(Color.parseColor("#2DC92D"));
                        }
                        final ImageView image = searchDialog.findViewById(R.id.show_search_image);
                        StorageReference storageRef = firebaseStorage.getReference();
                        storageRef.child("ProfilePictures/" + addDeviceModel.getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(UserProfileSearch.this).load(uri).into(image);
                            }
                        });
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

    @Override
    public void onBackPressed() {
        finishAffinity();
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
