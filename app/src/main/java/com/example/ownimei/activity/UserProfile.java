package com.example.ownimei.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ownimei.R;
import com.example.ownimei.StaticClass.StaticClass;
import com.example.ownimei.pojo.AddDeviceModel;
import com.example.ownimei.pojo.AddLaptopModel;
import com.example.ownimei.pojo.SignUpModel;
import com.example.ownimei.pojo.UserModeModel;
import com.example.ownimei.recycleview.AddDeviceAdapter;
import com.example.ownimei.recycleview.ViewHolder;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ownimei.activity.SignIn.SIGN_UP_TOKEN;
import static com.example.ownimei.activity.SignIn.USER_INFO;
import static com.example.ownimei.activity.SignUp.USER_ID;


public class UserProfile extends AppCompatActivity implements View.OnClickListener, ViewHolder.LongPressInterface {
    private TextView signOutId;
    private ImageView searchBtn;
    private ImageView menuButton;
    //Drawer user information
    private ImageView drawerImage;
    private TextView drawerName;

    private TextView ownerNameId;
    private TextView emailId;

    //    private EditText phoneNumberId;
//    private CountryCodePicker countryCodePicker;
//    private Button profileSaveButton;
//    private String codeSent;
    //User profile image
    private CircleImageView profileImage;
    private ImageView editImage;
    private static final int CODE_IMAGE_GALLERY = 1;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "IMEICropImage";
    private String destinationFileName;
    private Uri proImage;
    private String proImageUri;
    //Menu
    private DrawerLayout drawerLayout;
    private LinearLayout menuEditProfile;
    private LinearLayout menuAddDevice;
    private LinearLayout menuSearch;
    private LinearLayout menuPosition;
    //    private LinearLayout menuHistory;
    private LinearLayout menuHelp;
    private LinearLayout menuAbout;
    private LinearLayout menuSignOut;

    //Firebase
    private FirebaseAuth authOwner;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;
    //Mode status
    private String[] modeName;

    private String selectDeviceForFirstSignin;

    //Recycle view
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<AddDeviceModel> adddeviceModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        authOwner = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        drawerImage = findViewById(R.id.drawer_pro_image_ID);
        drawerName = findViewById(R.id.drawer_user_name_ID);

        signOutId = findViewById(R.id.user_profile_sign_out_ID);
        searchBtn = findViewById(R.id.user_profile_search_ID);
        ownerNameId = findViewById(R.id.ownerFirstNameID);
        emailId = findViewById(R.id.ownerEmailID);
//        phoneNumberId = findViewById(R.id.owner_phone_ID);
//        countryCodePicker = findViewById(R.id.country_code_picker_ID);
//        countryCodePicker.registerCarrierNumberEditText(phoneNumberId);
//        profileSaveButton = findViewById(R.id.profile_save_button_ID);

        // Menu button find and set click
        menuButton = findViewById(R.id.user_profile_menu_ID);
        drawerLayout = findViewById(R.id.drawer_layout_main);

        menuEditProfile = findViewById(R.id.menu_edit_profile);
        menuAddDevice = findViewById(R.id.menu_add_device);
        menuSearch = findViewById(R.id.menu_search);
        menuPosition = findViewById(R.id.menu_position);
//        menuHistory = findViewById(R.id.menu_history);
        menuHelp = findViewById(R.id.menu_help);
        menuAbout = findViewById(R.id.menu_about);
        menuSignOut = findViewById(R.id.menu_sign_out);

        //User profile image
        editImage = findViewById(R.id.user_profile_edit_image_ic_ID);
        profileImage = findViewById(R.id.user_profile_image_ID);

        signOutId.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
//        statusChangeId.setOnClickListener(this);
        menuButton.setOnClickListener(this);
        menuEditProfile.setOnClickListener(this);
        menuAddDevice.setOnClickListener(this);
        menuSearch.setOnClickListener(this);
        menuPosition.setOnClickListener(this);
//        menuHistory.setOnClickListener(this);
        menuHelp.setOnClickListener(this);
        menuAbout.setOnClickListener(this);
        menuSignOut.setOnClickListener(this);
        //User profile image
        editImage.setOnClickListener(this);
        profileImage.setOnClickListener(this);
        //
//        profileSaveButton.setOnClickListener(this);

        String uName = getIntent().getStringExtra("User_Name");
        String uEmail = getIntent().getStringExtra("User_Email");
        ownerNameId.setText(uName);
        emailId.setText(uEmail);
        drawerName.setText(uName);


        loadUserInformation();

    }

    //On Click Listener start
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_profile_image_ID:
                largeViewProPic();
                break;
            case R.id.user_profile_sign_out_ID:
                signOutUser();
                break;
            case R.id.user_profile_search_ID:
                searchUser();
                break;
            case R.id.profile_save_button_ID:
//                phoneAuth();
                break;
            case R.id.menu_position:
                devicePosition();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.user_profile_menu_ID:
                openDrawerMenu();
                break;
            case R.id.menu_edit_profile:
                editProfile();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.menu_add_device:
                addDeviceMenu();
                break;
            case R.id.menu_search:
                searchMenu();
                break;
//            case R.id.menu_history:
//                historyMenu();
//                break;
            case R.id.menu_help:
                Intent helpIntent = new Intent(UserProfile.this, Help.class);
                startActivity(helpIntent);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.menu_about:
                Intent aboutIntent = new Intent(UserProfile.this, AboutUs.class);
                startActivity(aboutIntent);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.menu_sign_out:
                signOutUser();
                break;
            //User profile image
            case R.id.user_profile_edit_image_ic_ID:
                editUserProImage();
                break;

        }
    }

//    private void phoneAuth() {
//        String phoneNumber = countryCodePicker.getFullNumberWithPlus();
//        if (phoneNumber.isEmpty() && phoneNumber.length() < 10) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
//            builder.setMessage("Please input your number").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            }).show();
//        }
//
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks);
//        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
//        View view = getLayoutInflater().inflate(R.layout.phone_code_verify, null);
//        final EditText mCode = view.findViewById(R.id.phone_code_ID);
//        Button mCodeButton = view.findViewById(R.id.phone_verification_button_ID);
//        mCodeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String finalMCode = mCode.getText().toString();
//                Log.i("Phone#####", "" + finalMCode);
//                Log.i("Phone00000", "" + codeSent);
//                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, finalMCode);
//                signInWithPhoneAuthCredential(credential);
//
//                    Toast.makeText(UserProfile.this, "Phone verification success", Toast.LENGTH_SHORT).show();
//                    Log.i("Phone00000", "" + codeSent);
//            }
//        });
//        builder.setView(view);
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }
//
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        authOwner.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//
//                        } else {
//
//                        }
//                    }
//                });
//    }
//
//
//    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//        @Override
//        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//            Toast.makeText(UserProfile.this, "Verification code send to your phone.", Toast.LENGTH_SHORT).show();
//
//        }
//
//        @Override
//        public void onVerificationFailed(FirebaseException e) {
//        }
//
//        @Override
//        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//            codeSent = s;
//            Log.i("Phone5555", "" + codeSent);
//
//        }
//
//    };

    //Device current position
    private void devicePosition() {
        if (!StaticClass.isConnected(this)) {
            StaticClass.buildDialog(this).show();
        } else {
            Intent positionIntent = new Intent(UserProfile.this, DevicePosition.class);
            startActivity(positionIntent);
        }

    }


    //Pro pic large view
    private void largeViewProPic() {

    }
    //On Click Listener end


    //User profile image start
    private void editUserProImage() {
        startActivityForResult(new Intent()
                .setAction(Intent.ACTION_GET_CONTENT)
                .setType("image/*"), CODE_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_IMAGE_GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                startCrop(imageUri);
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri imageCropResultUri = UCrop.getOutput(data);
            if (imageCropResultUri != null) {
                proImage = data.getData();
                profileImage.setImageURI(imageCropResultUri);
                uploadImageToFireBaseStore();
            }

        }

    }

    private void uploadImageToFireBaseStore() {
        showProgressBar();
        String uID1 = authOwner.getCurrentUser().getUid();

        final StorageReference profilePicRef = FirebaseStorage.getInstance().getReference("ProfilePictures/" + uID1 + ".jpg");

        profileImage.setDrawingCacheEnabled(true);
        profileImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        if (profileImage != null) {
            profilePicRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hideProgressBar();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressBar();
                    Toast.makeText(UserProfile.this, "Profile image not upload", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void startCrop(@NonNull Uri uri) {
        destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(10, 8);
        uCrop.withMaxResultSize(450, 450);
        uCrop.withOptions(getCropOption());
        uCrop.start(UserProfile.this);
    }

    private UCrop.Options getCropOption() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);

        options.setStatusBarColor(getResources().getColor(R.color.colorButton));
        options.setToolbarColor(getResources().getColor(R.color.colorActionBar));
        options.setToolbarTitle("OwnIMEI");

        return options;
    }
    //User profile image end

    //User information load from firebase start
    private void loadUserInformation() {

        // Retrieve data from preference
        String uId = authOwner.getCurrentUser().getUid();
        StorageReference storageRef = firebaseStorage.getReference();
        showProgressBar();
        storageRef.child("ProfilePictures/" + uId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                hideProgressBar();
                Glide.with(UserProfile.this).load(uri).into(profileImage);
                Glide.with(UserProfile.this).load(uri).into(drawerImage);
            }
        });
        SharedPreferences sGetUserInfo = getSharedPreferences(USER_INFO, MODE_PRIVATE);
        String userName = sGetUserInfo.getString("Name", "");
        String userEmail = sGetUserInfo.getString("Email", "");
        ownerNameId.setText(userName);
        emailId.setText(userEmail);
        drawerName.setText(userName);

        //Recycler view start
        recyclerView = findViewById(R.id.recycle_view_user_profile);
        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query docRef = db.collection("DeviceInfo").whereEqualTo("uid", uId);

        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                adddeviceModelList.clear();

                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    AddDeviceModel addDeviceModel = document.toObject(AddDeviceModel.class);
//                        addDeviceModel.setSelectDevice(document.getString("selectDevice"));
                    addDeviceModel.setDocumentId(document.getId());
//                        addDeviceModel.setDeviceName(document.getString("deviceName"));
//                        addDeviceModel.setPhoneImeiOne(document.getString("phoneImeiOne"));
//                        addDeviceModel.setPhoneImeiTwo(document.getString("phoneImeiTwo"));
//                        addDeviceModel.setMac(document.getString("mac"));
//                        addDeviceModel.setPurchaseDate(document.getString("purchaseDate"));
//                        addDeviceModel.setStatus(document.getString("status"));
                    adddeviceModelList.add(addDeviceModel);
                }


                //After sign up add device information
                if (adddeviceModelList.size() == 0) {
                    hideProgressBar();
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                    builder.setMessage("Please add your device information.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(UserProfile.this, AddDevice.class);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }


                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfile.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                AddDeviceAdapter adapter = new AddDeviceAdapter(adddeviceModelList, UserProfile.this);
                recyclerView.setAdapter(adapter);


            }
        });
    }
    //User information load from firebase end

    private void editProfile() {
//        phoneNumberId.setVisibility(View.VISIBLE);
//        countryCodePicker.setVisibility(View.VISIBLE);
//        profileSaveButton.setVisibility(View.VISIBLE);
        editImage.setVisibility(View.VISIBLE);
    }

//    private void historyMenu() {
//        Intent historyIntent = new Intent(UserProfile.this, History.class);
//        startActivity(historyIntent);
//        drawerLayout.closeDrawer(GravityCompat.START);
//    }

    private void searchMenu() {
        Intent searchMenuIntent = new Intent(UserProfile.this, UserProfileSearch.class);
        startActivity(searchMenuIntent);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void addDeviceMenu() {
        Intent addDeviceIntent = new Intent(UserProfile.this, AddDevice.class);
        startActivity(addDeviceIntent);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawerMenu() {
        drawerLayout.openDrawer(GravityCompat.START);

    }

    @Override
    public void onBackPressed() {
        hideProgressBar();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }

    private void searchUser() {
        startActivity(new Intent(UserProfile.this, UserProfileSearch.class));
    }

    private void signOutUser() {
        authOwner.signOut();
        SharedPreferences sharedPreferencesToken = getSharedPreferences(SIGN_UP_TOKEN, MODE_PRIVATE);
        SharedPreferences sharedPreferencesDelete = getSharedPreferences(USER_ID, MODE_PRIVATE);
        SharedPreferences sGetUserInfo = getSharedPreferences(USER_INFO, MODE_PRIVATE);
        sharedPreferencesDelete.edit().clear().apply();
        sharedPreferencesToken.edit().clear().apply();
        sGetUserInfo.edit().clear().apply();
        finish();
        Intent signOutIntent = new Intent(UserProfile.this, HomePage.class);
        startActivity(signOutIntent);
    }


    @Override
    public void longPressInterface(int position) {
//        final AddDeviceModel addDeviceModel = adddeviceModelList.get(position);
//        Log.d("longPressInterface", "" + adddeviceModelList.size());
//        Toast.makeText(UserProfile.this, "Long" + adddeviceModelList.get(position), Toast.LENGTH_LONG).show();
//        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
//        builder.setMessage("Are you sure?")
//                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        db.collection("DeviceInfo").document("" + addDeviceModel.getDocumentId())
//                                .delete()
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Toast.makeText(UserProfile.this, "Successfully delete the device info!", Toast.LENGTH_SHORT).show();
//                                        // Log.d("6897988", "" + adddeviceModelList.get(position).getUid());
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//
//                                    }
//                                });
//
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        dialog.show();

    }

    @Override
    public void onPressInterface(int position) {
        final AddDeviceModel addDeviceModel = adddeviceModelList.get(position);
        final Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.edit_delete);
        Button dialogDeleteButton = dialog1.findViewById(R.id.delete_ID);
        Button dialogEditButton = dialog1.findViewById(R.id.edit_ID);
        dialogDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                final Dialog dialog = new Dialog(UserProfile.this,R.style.Theme_Dialog);
                dialog.setContentView(R.layout.delete_dialog);
                getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

                dialog.setCancelable(false);
                Button dDeleteButton = dialog.findViewById(R.id.c_delete_ID);
                Button dcancelButton = dialog.findViewById(R.id.c_cancel_ID);
                dcancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideProgressBar();
                        dialog.dismiss();
                    }
                });
                showProgressBar();
                dDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideProgressBar();
                        dialog.dismiss();
                        db.collection("DeviceInfo").document("" + addDeviceModel.getDocumentId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hideProgressBar();
                                        Toast.makeText(UserProfile.this, "Successfully delete this device info!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hideProgressBar();
                                    }
                                });
                    }
                });
                dialog.show();
//                AlertDialog.Builder deletBuilder = new AlertDialog.Builder(UserProfile.this);
//                deletBuilder.setMessage("Are you sure?")
//                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//
//                            }
//                        });
//                AlertDialog deleteDialog = deletBuilder.create();
//                deleteDialog.show();
            }
        });
        dialogEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String documentID = addDeviceModel.getDocumentId();
                String deviceCatargory = addDeviceModel.getSelectDevice();
                String deviceName = addDeviceModel.getDeviceName();
                String imei1 = addDeviceModel.getPhoneImeiOne();
                String imei2 = addDeviceModel.getPhoneImeiTwo();
                String mac = addDeviceModel.getMac();
                String purchaseDate = addDeviceModel.getPurchaseDate();
                String status = addDeviceModel.getStatus();

                Intent editInten = new Intent(UserProfile.this, EditActivity.class);
                editInten.putExtra("deviceCatargory", deviceCatargory);
                editInten.putExtra("deviceName", deviceName);
                editInten.putExtra("imei1", imei1);
                editInten.putExtra("imei2", imei2);
                editInten.putExtra("mac", mac);
                editInten.putExtra("purchaseDate", purchaseDate);
                editInten.putExtra("status", status);
                editInten.putExtra("documentID", documentID);
                startActivity(editInten);
                dialog1.dismiss();
            }
        });
        dialog1.show();
    }

    //For device click


//    @Override
//    public void longPressInterface(int position) {
//        Log.d("rrrrrrrrrrrrrrr", "" + adddeviceModelList.get(position).getDocumentId());
//
//    }
//
//    @Override
//    public void onPressInterface(int position) {
//        Toast.makeText(UserProfile.this, "Short" + adddeviceModelList.get(position), Toast.LENGTH_LONG).show();
//    }

    //Progressbar method
    static KProgressHUD kProgressHUD;

    public void showProgressBar() {
        kProgressHUD = KProgressHUD.create(UserProfile.this)
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
