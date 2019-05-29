package com.example.ownimei.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ownimei.R;
import com.example.ownimei.StaticClass.StaticClass;
import com.example.ownimei.pojo.AddDeviceModel;
import com.google.android.gms.tasks.OnCanceledListener;
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

    private EditText userSearch;
    private ImageView userSearchButton;
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

        userSearchButton = findViewById(R.id.user_search_button);
        circleImageView = findViewById(R.id.user_profile_search_image_ID);

        userSearch.setOnEditorActionListener(editorActionListener);
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

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            searchIMEI();
            return false;
        }
    };

    private void loadImage() {
        SharedPreferences sharedUId = getSharedPreferences(USER_ID, MODE_PRIVATE);
        String uId = sharedUId.getString("get_UID", "");
        StorageReference storageRef = firebaseStorage.getReference();
        storageRef.child("ProfilePictures/" + uId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(circleImageView);
            }
        });
    }

    //On Click Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_profile_search_image_ID:
                startActivity(new Intent(UserProfileSearch.this, UserProfile.class));
                break;
            case R.id.user_search_button:
                searchIMEI();
                break;
        }
    }

    private void searchIMEI() {
        if (!StaticClass.isConnected(UserProfileSearch.this)) {
            StaticClass.buildDialog(UserProfileSearch.this);
            return;
        }
        final String inputImei = userSearch.getText().toString();
        if (inputImei.isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(UserProfileSearch.this);
            builder1.setMessage("Please enter IMEI!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder1.show();
            return;
        }
        if (inputImei.length() < 10) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(UserProfileSearch.this);
            builder1.setMessage("Please enter valid IMEI/MAC!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder1.show();
            return;
        }
        showProgressBar();
        final CollectionReference deviceInfoCollection = db.collection("DeviceInfo");
        Query query = deviceInfoCollection.whereEqualTo("phoneImeiOne", inputImei);
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
                        addDeviceModel.setUserPhone(document.getString("userPhone"));

                        Intent intentHomeIMEI1 = new Intent(UserProfileSearch.this, ShowSearchResultActivity.class);
                        intentHomeIMEI1.putExtra("userName", addDeviceModel.getUserName());
                        intentHomeIMEI1.putExtra("userEmail", addDeviceModel.getUserEmail());
                        intentHomeIMEI1.putExtra("phoneImeiOne", addDeviceModel.getPhoneImeiOne());
                        intentHomeIMEI1.putExtra("deviceName", addDeviceModel.getDeviceName());
                        intentHomeIMEI1.putExtra("status", addDeviceModel.getStatus());
                        intentHomeIMEI1.putExtra("uid", addDeviceModel.getUid());
                        intentHomeIMEI1.putExtra("userPhone", addDeviceModel.getUserPhone());
                        startActivity(intentHomeIMEI1);
                        finish();
                    }
                } else {
                    CollectionReference deviceInfoCollection = db.collection("DeviceInfo");
                    Query queryTwoIMEI = deviceInfoCollection.whereEqualTo("phoneImeiTwo", inputImei);
                    queryTwoIMEI.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    hideProgressBar();
                                    addDeviceModel.setUserName(document.getString("userName"));
                                    addDeviceModel.setUserEmail(document.getString("userEmail"));
                                    addDeviceModel.setPhoneImeiTwo(document.getString("phoneImeiTwo"));
                                    addDeviceModel.setDeviceName(document.getString("deviceName"));
                                    addDeviceModel.setStatus(document.getString("status"));
                                    addDeviceModel.setUid(document.getString("uid"));
                                    addDeviceModel.setUserPhone(document.getString("userPhone"));

                                    Intent intentHomeIMEI2 = new Intent(UserProfileSearch.this, ShowSearchResultActivity.class);
                                    intentHomeIMEI2.putExtra("userName", addDeviceModel.getUserName());
                                    intentHomeIMEI2.putExtra("userEmail", addDeviceModel.getUserEmail());
                                    intentHomeIMEI2.putExtra("phoneImeiTwo", addDeviceModel.getPhoneImeiTwo());
                                    intentHomeIMEI2.putExtra("deviceName", addDeviceModel.getDeviceName());
                                    intentHomeIMEI2.putExtra("status", addDeviceModel.getStatus());
                                    intentHomeIMEI2.putExtra("uid", addDeviceModel.getUid());
                                    intentHomeIMEI2.putExtra("userPhone", addDeviceModel.getUserPhone());
                                    startActivity(intentHomeIMEI2);
                                    finish();
                                }
                            } else {
                                CollectionReference deviceInfoCollection = db.collection("DeviceInfo");
                                Query queryMac = deviceInfoCollection.whereEqualTo("mac", inputImei);
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

                                                Intent intentHomeMAC = new Intent(UserProfileSearch.this, ShowSearchResultActivity.class);
                                                intentHomeMAC.putExtra("userName", addDeviceModel.getUserName());
                                                intentHomeMAC.putExtra("userEmail", addDeviceModel.getUserEmail());
                                                intentHomeMAC.putExtra("mac", addDeviceModel.getMac());
                                                intentHomeMAC.putExtra("deviceName", addDeviceModel.getDeviceName());
                                                intentHomeMAC.putExtra("status", addDeviceModel.getStatus());
                                                intentHomeMAC.putExtra("uid", addDeviceModel.getUid());
                                                intentHomeMAC.putExtra("userPhone", addDeviceModel.getUserPhone());
                                                startActivity(intentHomeMAC);
                                                finish();
                                            }
                                        } else {
                                            hideProgressBar();
                                            Toast.makeText(UserProfileSearch.this, "Opps!! Entered address Not found", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hideProgressBar();
                                        Toast.makeText(UserProfileSearch.this, "Request Failed, Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressBar();
                            Toast.makeText(UserProfileSearch.this, "Request Failed, Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Toast.makeText(UserProfileSearch.this, "Request Failed, Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
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
