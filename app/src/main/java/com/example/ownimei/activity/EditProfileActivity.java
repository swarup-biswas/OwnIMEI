package com.example.ownimei.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ownimei.R;
import com.example.ownimei.StaticClass.StaticClass;
import com.example.ownimei.pojo.SignUpModel;
import com.example.ownimei.pojo.UserModeModel;
import com.example.ownimei.popUp.SignUpPopUpActivity;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.BaseUIManager;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.i18n.phonenumbers.Phonenumber;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ownimei.activity.SignIn.SIGN_UP_TOKEN;
import static com.example.ownimei.activity.SignIn.USER_INFO;
import static com.example.ownimei.activity.SignUp.USER_ID;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView changeImage;
    private ImageView editBackButton;
    private ImageView editSaveButton;
//    private EditText editEmail;
    private EditText editName;
    private CardView changePassword;

    //Test Account kit
    private EditText etPhone;
    private ImageView ivPhoneOk;
    public static int APP_REQUEST_CODE = 99;
    private String phoneNumber1;
    private String phone;
    private ImageView nameButton;
//    private ImageView emailButton;

    //Firebase
    private FirebaseAuth authOwner;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;
    private FirebaseUser user;

    //User profile image
    private CircleImageView profileImage;
    private ImageView editImage;
    private static final int CODE_IMAGE_GALLERY = 1;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "IMEICropImage";
    private String destinationFileName;
    private Uri proImage;
    private String proImageUri;

    private String userEmail;
    private String userName;
    private String userPhone;
    private String pass;
    private String newPass;
    private String email;
    private FirebaseUser emailUser;

    private CardView deleteAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        authOwner = FirebaseAuth.getInstance();
        user = authOwner.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        //Account kit test
        nameButton = findViewById(R.id.iv_name_ok_button);
        nameButton.setOnClickListener(this);
//        emailButton = findViewById(R.id.iv_email_ok_button);
//        emailButton.setOnClickListener(this);
        etPhone = findViewById(R.id.edit_phone_ID);
        ivPhoneOk = findViewById(R.id.iv_phone_ok_button);
        ivPhoneOk.setOnClickListener(this);


        changeImage = findViewById(R.id.edit_image_ID);
        editBackButton = findViewById(R.id.edit_back_btn);
        editSaveButton = findViewById(R.id.edit_save_ID);
//        editEmail = findViewById(R.id.edit_email_ID);
        editName = findViewById(R.id.edit_name_ID);
        changePassword = findViewById(R.id.change_password_ID);
        changeImage.setOnClickListener(this);
        editBackButton.setOnClickListener(this);
        editSaveButton.setOnClickListener(this);
        changePassword.setOnClickListener(this);
//        editEmail.setOnClickListener(this);

        //Delete Account
        deleteAccount = findViewById(R.id.delete_account_ID);
        deleteAccount.setOnClickListener(this);

        SharedPreferences sGetUserInfo = getSharedPreferences(USER_INFO, MODE_PRIVATE);
        userName = sGetUserInfo.getString("Name", "");
        userEmail = sGetUserInfo.getString("Email", "");
        userPhone = sGetUserInfo.getString("Phone", "");
        pass = sGetUserInfo.getString("Password", "");
//        editEmail.setText(userEmail);
        editName.setText(userName);
        etPhone.setText(userPhone);

        //Retrieve image
        SharedPreferences sharedUId = getSharedPreferences(USER_ID, MODE_PRIVATE);
        String uId = sharedUId.getString("get_UID", "");
        StorageReference storageRef = firebaseStorage.getReference();
        showProgressBar();
        storageRef.child("ProfilePictures/" + uId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                hideProgressBar();
                Glide.with(getApplicationContext()).load(uri).into(changeImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
               // Toast.makeText(EditProfileActivity.this, "Image not found", Toast.LENGTH_SHORT).show();
            }
        });
        hideProgressBar();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_back_btn:
                startActivity(new Intent(EditProfileActivity.this, UserProfile.class));
                finish();
                break;
            case R.id.edit_save_ID:
                startActivity(new Intent(EditProfileActivity.this, HomePage.class));
                finish();
                break;
            case R.id.iv_name_ok_button:
                editNameSaveMethod();
                break;
//            case R.id.iv_email_ok_button:
//                editEmailSaveMethod();
//                break;
            case R.id.edit_image_ID:
                changeImageMethod();
                break;
            case R.id.change_password_ID:
                passwordChaneMethod();
                break;
            //Account kit
            case R.id.iv_phone_ok_button:
                AccessToken accessToken = AccountKit.getCurrentAccessToken();

                if (accessToken != null) {
                    //Handle Returning User
                    Toast.makeText(EditProfileActivity.this, "User already loged in", Toast.LENGTH_LONG).show();
                } else {
                    onLogin(LoginType.PHONE);

                }
                break;
            case R.id.delete_account_ID:
                deleteAccountMethod();
                break;
        }
    }


//    private void editEmailSaveMethod() {
//        if (!StaticClass.isConnected(this)) {
//            StaticClass.buildDialog(this);
//        } else {
//            changeEmailMethod();
//        }
//    }

    private void editNameSaveMethod() {
        if (!StaticClass.isConnected(this)) {
            StaticClass.buildDialog(this);
        } else {
            changeNameMethod();
        }
    }

    private void onLogin(LoginType phone) {
        phoneNumber1 = etPhone.getText().toString();
        if (phoneNumber1.isEmpty()){
            AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfileActivity.this);

            dialog.setMessage("Please enter your valid phone number");
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return;
        }
        PhoneNumber phoneNumber = new PhoneNumber("+88", phoneNumber1, "BD");

        final Intent intent = new Intent(EditProfileActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.CODE).setInitialPhoneNumber(phoneNumber);// or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);

    }

    //TODO check update password
    private void passwordChaneMethod() {
        final Dialog dialog = new Dialog(EditProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_password);
        Button cobButton = (Button) dialog.findViewById(R.id.change_password_button_ID);

        cobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText oPass = dialog.findViewById(R.id.old_pass_ID);
                EditText nPass = dialog.findViewById(R.id.new_pass_ID);
                String oldPass = oPass.getText().toString().trim();
                newPass = nPass.getText().toString().trim();


                if (!pass.equals(oldPass)) {
                    AlertDialog.Builder oBuilder = new AlertDialog.Builder(EditProfileActivity.this);
                    oBuilder.setMessage("Your old password is not correct.");
                    oBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog oDialog1 = oBuilder.create();
                    oDialog1.show();
                    return;
                }
                if (newPass.isEmpty()) {
                    AlertDialog.Builder nBuilder = new AlertDialog.Builder(EditProfileActivity.this);
                    nBuilder.setMessage("Please enter new password.");
                    nBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog nDialog = nBuilder.create();
                    nDialog.show();
                    return;
                }
                if (newPass.length() < 6) {
                    AlertDialog.Builder sBuilder = new AlertDialog.Builder(EditProfileActivity.this);
                    sBuilder.setMessage("Minimum length of password should be 6.");
                    sBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog sDialog = sBuilder.create();
                    sDialog.show();
                    return;
                }
                if (oldPass.equals(newPass)) {
                    AlertDialog.Builder pBuilder = new AlertDialog.Builder(EditProfileActivity.this);
                    pBuilder.setMessage("Your new password and old password are same.");
                    pBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog pDialog = pBuilder.create();
                    pDialog.show();
                    return;
                }
                showProgressBar();
                user.updatePassword(newPass)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    hideProgressBar();
                                    SharedPreferences sharedPreferences = getSharedPreferences(USER_INFO, MODE_PRIVATE);
                                    SharedPreferences.Editor editorUserInfo = sharedPreferences.edit();
                                    editorUserInfo.putString("Password", newPass);
                                    editorUserInfo.apply();
                                    Toast.makeText(EditProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressBar();
                        Toast.makeText(EditProfileActivity.this, "Sign Out and try again!", Toast.LENGTH_SHORT).show();


                    }
                });


            }
        });


        dialog.show();

    }

    private void changeNameMethod() {

        showProgressBar();
        final String name = editName.getText().toString();
        if (name.isEmpty()) {
            hideProgressBar();
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            builder.setMessage("Please enter your name").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        if (!userName.equals(name)) {
            db.collection("UserInformations").document(authOwner.getCurrentUser().getUid())
                    .update("userFirstName", name).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        hideProgressBar();
                        SharedPreferences sharedPreferences = getSharedPreferences(USER_INFO, MODE_PRIVATE);
                        SharedPreferences.Editor editorUserInfo = sharedPreferences.edit();
                        editorUserInfo.putString("Name", name);
                        editorUserInfo.apply();
                        Toast.makeText(EditProfileActivity.this, "Update Success", Toast.LENGTH_SHORT).show();
                        //  startActivity(new Intent(EditProfileActivity.this, UserProfile.class));

                    } else {
                        hideProgressBar();
                        Toast.makeText(EditProfileActivity.this, "Name Update failed", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        } else {
            Toast.makeText(EditProfileActivity.this, "Name same!", Toast.LENGTH_SHORT).show();
            hideProgressBar();
        }
    }

//    private void changeEmailMethod() {
//
//        //TODO recheck the hole email update system including handel share preference
//
//        email = editEmail.getText().toString().trim();
//        showProgressBar();
//        if (email.isEmpty()) {
//            hideProgressBar();
//            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
//            builder.setMessage("Please enter your email");
//            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            });
//            builder.show();
//            return;
//        }
//
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            hideProgressBar();
//            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
//            builder.setMessage("Please enter your valid email");
//            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            });
//            builder.show();
//            return;
//        }
//        Log.d("SharedPreEmail", "" + userEmail);
//        if (!userEmail.equals(email)) {
//            emailUser = FirebaseAuth.getInstance().getCurrentUser();
//            showProgressBar();
//            CollectionReference deviceInfoCollection = db.collection("UserInformations");
//            Query query = deviceInfoCollection.whereEqualTo("userEmail", email);
//            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                @Override
//                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                    if (!queryDocumentSnapshots.isEmpty()) {
//                        SignUpModel userInformation = new SignUpModel();
//                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                            userInformation.setUserEmail(document.getString("userEmail"));
//                        }
//                        String searchEmail = userInformation.getUserEmail();
//                        Log.d("SearchEmail12", "" + searchEmail);
//                        if (searchEmail.isEmpty()) {
//                            showProgressBar();
//                            emailUser.updateEmail(email)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                hideProgressBar();
//                                                SharedPreferences sharedPreferences = getSharedPreferences(USER_INFO, MODE_PRIVATE);
//                                                SharedPreferences.Editor editorUserInfo = sharedPreferences.edit();
//                                                editorUserInfo.putString("Email", email);
//                                                editorUserInfo.apply();
//                                                Toast.makeText(EditProfileActivity.this, "Update Success", Toast.LENGTH_SHORT).show();
//                                                //  startActivity(new Intent(EditProfileActivity.this, UserProfile.class));
//
//                                            }
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    hideProgressBar();
//                                    Toast.makeText(EditProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                                }
//                            });
//                        } else {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
//                            builder.setMessage("This email '" + searchEmail + "' already exist.");
//                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    hideProgressBar();
//                                }
//                            });
//                            AlertDialog alertDialog = builder.create();
//                            alertDialog.show();
//
//                        }
//                    } else {
//                        showProgressBar();
//                        emailUser.updateEmail(email)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            hideProgressBar();
//                                            SharedPreferences sharedPreferences = getSharedPreferences(USER_INFO, MODE_PRIVATE);
//                                            SharedPreferences.Editor editorUserInfo = sharedPreferences.edit();
//                                            editorUserInfo.putString("Email", email);
//                                            editorUserInfo.apply();
//                                            user.sendEmailVerification()
//                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if (task.isSuccessful()) {
//                                                                hideProgressBar();
//                                                                authOwner.signOut();
//                                                                SharedPreferences sharedPreferencesToken = getSharedPreferences(SIGN_UP_TOKEN, MODE_PRIVATE);
//                                                                SharedPreferences sharedPreferencesDelete = getSharedPreferences(USER_ID, MODE_PRIVATE);
//                                                                SharedPreferences sGetUserInfo = getSharedPreferences(USER_INFO, MODE_PRIVATE);
//                                                                sharedPreferencesDelete.edit().clear().apply();
//                                                                sharedPreferencesToken.edit().clear().apply();
//                                                                sGetUserInfo.edit().clear().apply();
//                                                                Intent intent = new Intent(EditProfileActivity.this, SignUpPopUpActivity.class);
//                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                                startActivity(intent);
//                                                                finish();
//                                                            }
//                                                        }
//                                                    }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    hideProgressBar();
//                                                    Toast.makeText(EditProfileActivity.this,""+e.getMessage(),
//                                                            Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
////                                            Toast.makeText(EditProfileActivity.this, "Update Success", Toast.LENGTH_SHORT).show();
//                                            //           startActivity(new Intent(EditProfileActivity.this, UserProfile.class));
//
//                                        }
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                hideProgressBar();
//                                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
//                                builder.setMessage("At first sign out \nThen sign in again for email change.");
//                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                });
//                                builder.show();
//
//
//                            }
//                        });
//                    }
//
//                }
//            });
//        } else {
//            hideProgressBar();
//            Toast.makeText(EditProfileActivity.this, "Mail same!", Toast.LENGTH_LONG).show();
//        }
//    }

    //Edit photo start
    private void changeImageMethod() {
        startActivityForResult(new Intent()
                .setAction(Intent.ACTION_GET_CONTENT)
                .setType("image/*"), CODE_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

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
                changeImage.setImageURI(imageCropResultUri);
                uploadImageToFireBaseStore();
            }

        }
        //Phone verification final method
        phoneVerificationFinalMethod(requestCode, resultCode, data);

    }

    //Phone verification final method
    private void phoneVerificationFinalMethod(int requestCode, int resultCode, Intent data) {
        //Account kit start
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null) {
                Toast.makeText(EditProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            } else if (loginResult.wasCancelled()) {
                Toast.makeText(EditProfileActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                if (loginResult.getAccessToken() != null) {
                    Toast.makeText(EditProfileActivity.this, "Success", Toast.LENGTH_LONG).show();
                } else {
                  //  Toast.makeText(EditProfileActivity.this, "Success:%s..." + loginResult.getAuthorizationCode().substring(0, 10), Toast.LENGTH_LONG).show();
                }
                //Upload number
                phone = etPhone.getText().toString();
                if (phone != null) {
                    db.collection("UserInformations").document(authOwner.getCurrentUser().getUid())
                            .update("userPhone", phone).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SharedPreferences sharedPreferences = getSharedPreferences(USER_INFO, MODE_PRIVATE);
                                SharedPreferences.Editor editorUserInfo = sharedPreferences.edit();
                                editorUserInfo.putString("Phone", phone);
                                editorUserInfo.apply();
                                Toast.makeText(EditProfileActivity.this, "Update Success", Toast.LENGTH_SHORT).show();
                            } else {
                                hideProgressBar();
                            }
                        }
                    });
                }else {
                    Toast.makeText(EditProfileActivity.this,"Please try again.",Toast.LENGTH_LONG).show();
                }
                //startActivity(new Intent(EditProfileActivity.this, EditProfileActivity.class));
            }

            // Surface the result to your user in an appropriate way.
          //  Toast.makeText(EditProfileActivity.this, "Last else", Toast.LENGTH_LONG).show();

        }
        //Account kit end

    }

    private void uploadImageToFireBaseStore() {
        String uID1 = authOwner.getCurrentUser().getUid();
        showProgressBar();
        final StorageReference profilePicRef = FirebaseStorage.getInstance().getReference("ProfilePictures/" + uID1 + ".jpg");

        changeImage.setDrawingCacheEnabled(true);
        changeImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) changeImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        if (changeImage != null) {
            profilePicRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hideProgressBar();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressBar();
                    Toast.makeText(EditProfileActivity.this, "Profile image not upload", Toast.LENGTH_SHORT).show();
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
        uCrop.start(EditProfileActivity.this);
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

    ////Edit photo end
    //Progressbar method
    static KProgressHUD kProgressHUD;

    public void showProgressBar() {
        kProgressHUD = KProgressHUD.create(EditProfileActivity.this)
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

    //Account delete method
    private void deleteAccountMethod() {
        final Dialog cDelete = new Dialog(EditProfileActivity.this);
        cDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cDelete.setContentView(R.layout.delete_dialog);
        Button cobButton = (Button) cDelete.findViewById(R.id.c_delete_ID);
        SharedPreferences sGetUserInfo = getSharedPreferences(USER_INFO, MODE_PRIVATE);
        final String userPass = sGetUserInfo.getString("Password", "");
        cobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = ((EditText) cDelete.findViewById(R.id.delete_con_pass_ID)).getText().toString();
                showProgressBar();
                if (pass.isEmpty()) {
                    hideProgressBar();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(EditProfileActivity.this);
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
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(EditProfileActivity.this);
                    builder1.setMessage("Please enter correct password.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder1.show();
                    return;
                }

                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        hideProgressBar();
                                        Toast.makeText(EditProfileActivity.this, "User account deleted.", Toast.LENGTH_LONG).show();
                                        SharedPreferences sharedPreferencesToken = getSharedPreferences(SIGN_UP_TOKEN, MODE_PRIVATE);
                                        SharedPreferences sharedPreferencesDelete = getSharedPreferences(USER_ID, MODE_PRIVATE);
                                        SharedPreferences sGetUserInfo = getSharedPreferences(USER_INFO, MODE_PRIVATE);
                                        sharedPreferencesDelete.edit().clear().apply();
                                        sharedPreferencesToken.edit().clear().apply();
                                        sGetUserInfo.edit().clear().apply();
                                        authOwner.signOut();
                                        startActivity(new Intent(EditProfileActivity.this, HomePage.class));
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressBar();
                            Toast.makeText(EditProfileActivity.this, "Sign Out and try again!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        cDelete.show();
    }
}

