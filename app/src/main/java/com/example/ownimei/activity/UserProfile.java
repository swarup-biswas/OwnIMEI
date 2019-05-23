package com.example.ownimei.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ownimei.R;
import com.example.ownimei.StaticClass.StaticClass;
import com.example.ownimei.pojo.AddDeviceModel;
import com.example.ownimei.recycleview.AddDeviceAdapter;
import com.example.ownimei.recycleview.ViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ownimei.activity.SignIn.SIGN_UP_TOKEN;
import static com.example.ownimei.activity.SignIn.USER_INFO;
import static com.example.ownimei.activity.SignUp.USER_ID;


public class UserProfile extends AppCompatActivity implements View.OnClickListener, ViewHolder.LongPressInterface {
    private FloatingActionButton floatingActionButton;
    private TextView signOutId;
    private ImageView searchBtn;
    private ImageView menuButton;
    //Drawer user information
    private ImageView drawerImage;
    private TextView drawerName;

    private TextView ownerNameId;
    private TextView emailId;
    private TextView ownerPhone;

    //User profile image
    private CircleImageView profileImage;
    //    private ImageView editImage;
    private static final int CODE_IMAGE_GALLERY = 1;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "IMEICropImage";
    private String destinationFileName;
    private Uri proImage;
    //Menu
    private DrawerLayout drawerLayout;
    private LinearLayout menuEditProfile;
    private LinearLayout menuAddDevice;
    private LinearLayout menuSearch;
    private LinearLayout menuPosition;
    // private LinearLayout menuHistory;
    private LinearLayout menuHelp;
    private LinearLayout menuAbout;
    private LinearLayout menuSignOut;

    //Firebase
    private FirebaseAuth authOwner;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageRef;
    private String uId;


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
        storageRef = firebaseStorage.getReference();

        // Retrieve data from preference
        SharedPreferences sharedUId = getSharedPreferences(USER_ID, MODE_PRIVATE);
        uId = sharedUId.getString("get_UID", "");

        drawerImage = findViewById(R.id.drawer_pro_image_ID);
        drawerName = findViewById(R.id.drawer_user_name_ID);

        floatingActionButton = findViewById(R.id.add_floating_button);

        signOutId = findViewById(R.id.user_profile_sign_out_ID);
        searchBtn = findViewById(R.id.user_profile_search_ID);
        ownerNameId = findViewById(R.id.ownerFirstNameID);
        emailId = findViewById(R.id.ownerEmailID);
        ownerPhone = findViewById(R.id.owner_phone_ID);
        // Menu button find and set click
        menuButton = findViewById(R.id.user_profile_menu_ID);
        drawerLayout = findViewById(R.id.drawer_layout_main);

        menuEditProfile = findViewById(R.id.menu_edit_profile);
        menuAddDevice = findViewById(R.id.menu_add_device);
        menuSearch = findViewById(R.id.menu_search);
        menuPosition = findViewById(R.id.menu_position);
        menuHelp = findViewById(R.id.menu_help);
        menuAbout = findViewById(R.id.menu_about);
        menuSignOut = findViewById(R.id.menu_sign_out);

        //User profile image
        profileImage = findViewById(R.id.user_profile_image_ID);
        floatingActionButton.setOnClickListener(this);
        signOutId.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        menuButton.setOnClickListener(this);
        menuEditProfile.setOnClickListener(this);
        menuAddDevice.setOnClickListener(this);
        menuSearch.setOnClickListener(this);
        menuPosition.setOnClickListener(this);
        menuHelp.setOnClickListener(this);
        menuAbout.setOnClickListener(this);
        menuSignOut.setOnClickListener(this);
        //User profile image
        profileImage.setOnClickListener(this);

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
            case R.id.add_floating_button:
                startActivity(new Intent(UserProfile.this, AddDevice.class));
                break;

            case R.id.user_profile_image_ID:
                editUserProImage();
                break;
            case R.id.user_profile_sign_out_ID:
                signOutUser();
                break;
            case R.id.user_profile_search_ID:
                searchUser();
                break;
            case R.id.menu_position:
                devicePosition();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.user_profile_menu_ID:
                openDrawerMenu();
                break;
            case R.id.menu_edit_profile:
                startActivity(new Intent(UserProfile.this, EditProfileActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.menu_add_device:
                addDeviceMenu();
                break;
            case R.id.menu_search:
                searchMenu();
                break;
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

        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(UserProfile.this, UserProfileSearch.class));
        hideProgressBar();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        finish();
    }

    //Device current position
    private void devicePosition() {
        if (!StaticClass.isConnected(this)) {
            StaticClass.buildDialog(this).show();
        } else {
            //Location enable start
            LocationManager lm = (LocationManager) UserProfile.this.getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }

            if (!gps_enabled) {
                // notify user
                new AlertDialog.Builder(UserProfile.this)
                        .setMessage(R.string.gps_dialog)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                UserProfile.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false).show();
            } else {
                Intent positionIntent = new Intent(UserProfile.this, DevicePosition.class);
                startActivity(positionIntent);
            }
            //Location enable end

        }

    }

    //On Click Listener end

    //    //User profile image start
    private void editUserProImage() {
        showProgressBar();
        final Dialog imageViewDialog = new Dialog(UserProfile.this);
        imageViewDialog.setContentView(R.layout.full_screen_image);
        storageRef.child("ProfilePictures/" + uId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                hideProgressBar();
                Glide.with(getApplicationContext()).load(uri).into((ImageView) imageViewDialog.findViewById(R.id.full_image_ID));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Toast.makeText(UserProfile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        imageViewDialog.show();
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
        showProgressBar();
        storageRef.child("ProfilePictures/" + uId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                hideProgressBar();
                Glide.with(getApplicationContext()).load(uri).into(profileImage);
                Glide.with(getApplicationContext()).load(uri).into(drawerImage);

            }
        });
        SharedPreferences sGetUserInfo = getSharedPreferences(USER_INFO, MODE_PRIVATE);
        String userName = sGetUserInfo.getString("Name", "");
        String userEmail = sGetUserInfo.getString("Email", "");
        String userPhone = sGetUserInfo.getString("Phone", "");
        ownerNameId.setText(userName);
        drawerName.setText(userName);
        emailId.setText(userEmail);
        if (userPhone != null) {
            ownerPhone.setVisibility(View.VISIBLE);
            ownerPhone.setText(userPhone);
        }

        //Recycler view start
        recyclerView = findViewById(R.id.recycle_view_user_profile);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query docRef = db.collection("DeviceInfo").whereEqualTo("uid", uId);

        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                adddeviceModelList.clear();
                hideProgressBar();

                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    AddDeviceModel addDeviceModel = document.toObject(AddDeviceModel.class);
                    addDeviceModel.setDocumentId(document.getId());
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

    private void searchUser() {
        startActivity(new Intent(UserProfile.this, UserProfileSearch.class));
    }

    @Override
    public void longPressInterface(int position) {


    }

    @Override
    public void onPressInterface(int position) {
        final AddDeviceModel addDeviceModel = adddeviceModelList.get(position);
        final Dialog dialog1 = new Dialog(UserProfile.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.edit_delete);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog1.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        Button dialogDeleteButton = dialog1.findViewById(R.id.delete_ID);
        Button dialogEditButton = dialog1.findViewById(R.id.edit_ID);
        dialogDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                //Delete dialog start
                final Dialog cDelete = new Dialog(UserProfile.this);
                cDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
                cDelete.setContentView(R.layout.delete_dialog);
                Button cobButton = (Button) cDelete.findViewById(R.id.c_delete_ID);
                SharedPreferences sGetUserInfo = getSharedPreferences(USER_INFO, MODE_PRIVATE);
                final String userEmail = sGetUserInfo.getString("Email", "");
                final String userPass = sGetUserInfo.getString("Password", "");
                Log.d("userEmail", "" + userPass);

                cobButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        cDelete.dismiss();
                        showProgressBar();
                        String pass = ((EditText) cDelete.findViewById(R.id.delete_con_pass_ID)).getText().toString();
                        if (pass.isEmpty()) {
                            hideProgressBar();
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(UserProfile.this);
                            builder1.setMessage("Please enter your password.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder1.show();
                            return;
                        }
                        if (!pass.equals(userPass)) {
                            hideProgressBar();
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(UserProfile.this);
                            builder1.setMessage("Please enter correct password.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder1.show();
                            return;
                        }
                        if (StaticClass.isConnected(UserProfile.this)) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential("" + userEmail, "" + userPass);
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {


                                            db.collection("DeviceInfo").document("" + addDeviceModel.getDocumentId())
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            hideProgressBar();
                                                            Toast.makeText(UserProfile.this, "Successfully delete device info!", Toast.LENGTH_SHORT).show();
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
                        } else {
                            StaticClass.buildDialog(UserProfile.this);
                            hideProgressBar();
                        }

                    }
                });
                cDelete.show();
//Delete dialog end
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

                Intent editInten = new Intent(UserProfile.this, EditDeviceInformationActivity.class);
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

    private void signOutUser() {
        authOwner.signOut();
        SharedPreferences sharedPreferencesToken = getSharedPreferences(SIGN_UP_TOKEN, MODE_PRIVATE);
        SharedPreferences sharedPreferencesDelete = getSharedPreferences(USER_ID, MODE_PRIVATE);
        SharedPreferences sGetUserInfo = getSharedPreferences(USER_INFO, MODE_PRIVATE);
        sharedPreferencesDelete.edit().clear().apply();
        sharedPreferencesToken.edit().clear().apply();
        sGetUserInfo.edit().clear().apply();
        Intent signOutIntent = new Intent(UserProfile.this, HomePage.class);
        signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(signOutIntent);
        finish();
    }

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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
