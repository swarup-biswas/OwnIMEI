package com.example.ownimei.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ownimei.R;
import com.example.ownimei.StaticClass.StaticClass;
import com.example.ownimei.pojo.AddDeviceModel;
import com.example.ownimei.pojo.SignUpModel;
import com.example.ownimei.recycleview.AddDeviceAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaopiz.kprogresshud.KProgressHUD;


public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private ImageView signUpBackBtn;
    private EditText firstNameID;
    private EditText lastNameID;
    private EditText emailID;
    private EditText passID;
    private EditText retypePassID;
    private Button signUpBtnID;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    static String mVerificationId;
    public static final String USER_ID = "User_Id";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        signUpBackBtn = findViewById(R.id.sign_up_back_btn);
        firstNameID = findViewById(R.id.sign_UP_first_name_ID);
        lastNameID = findViewById(R.id.sign_UP_last_name_ID);
        emailID = findViewById(R.id.sign_UP_email_ID);
        passID = findViewById(R.id.sign_UP_pass_ID);
        retypePassID = findViewById(R.id.sign_UP_retype_pass_ID);
        signUpBtnID = findViewById(R.id.sign_UP_btn_ID);
        signUpBackBtn.setOnClickListener(this);
        signUpBtnID.setOnClickListener(this);

    }

    //On Click Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_back_btn:
                onBackPressedSignUp();
                break;

            case R.id.sign_UP_btn_ID:
                signUPMethod();
                break;
        }

    }

    //    //Back button
    private void onBackPressedSignUp() {
        hideProgressBar();
        onBackPressed();
    }

    //check to see if the user is currently signed in

    //Sign Up method
    private void signUPMethod() {
        final String firstName = firstNameID.getText().toString().trim();
        final String lastName = lastNameID.getText().toString().trim();
        final String email = emailID.getText().toString().trim();
        final String password = passID.getText().toString().trim();
        final String rePassword = retypePassID.getText().toString().trim();
        //Name validation check
        if (firstName.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setMessage("Please enter your first name");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return;
        }
        if (lastName.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setMessage("Please enter your last name");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return;
        }
        //Email validation check
        if (email.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setMessage("Please enter your email");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setMessage("Please enter your valid email");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return;
        }
        //Password validation check
        if (password.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setMessage("Please enter password");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return;
        }
        if (password.length() < 6) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setMessage("Minimum length of password should be 6");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return;
        }
        if (rePassword.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setMessage("Please retype password");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return;
        }
        if (!password.equals(rePassword)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setMessage("Password not match");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return;
        }

        //Progressbar start
        showProgressBar();
        if (!StaticClass.isConnected(SignUp.this)) {
            StaticClass.buildDialog(SignUp.this).show();
            hideProgressBar();
        } else {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        SignUpModel userInformation = new SignUpModel(firstName, lastName, email);
                        DocumentReference userData = firebaseFirestore.collection("UserInformations").document(task.getResult().getUser().getUid());
                        userData.set(userInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                }

                            }
                        });

                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    hideProgressBar();

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                    builder.setMessage("Verification link send.Please check your email.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent signUpIntent = new Intent(SignUp.this, SignIn.class);
                                            startActivity(signUpIntent);
                                            auth.signOut();
                                            finish();
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.setCancelable(false);
                                    dialog.show();

                                } else {
                                    hideProgressBar();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                    builder.setMessage("" + task.getException().getMessage());
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder.show();
//                                    Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    auth.signOut();
                                    finish();
                                }
                            }
                        });
                    } else {
                        hideProgressBar();
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                        builder.setMessage("" + task.getException().getMessage());
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
//                        Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
//
//            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull final Task<AuthResult> task) {
//                    try {
//
//                    }catch (Exception e){
//
//                    }
//                     SignUpModel userInformation = new SignUpModel(firstName, lastName, email);
//                     DocumentReference userData = firebaseFirestore.collection("UserInformations").document(task.getResult().getUser().getUid());
//                    userData.set(userInformation)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        hideProgressBar();
//
//                                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//                                                    SharedPreferences.Editor editor = getSharedPreferences(USER_ID, MODE_PRIVATE).edit();
//                                                    String userID = auth.getUid();
//                                                    editor.putString("get_UID", "" + userID);
//                                                    editor.apply();
//                                                    Toast.makeText(SignUp.this, "Signed up successfully.Please check your email for verification.", Toast.LENGTH_SHORT).show();
//                                                    //SignUp success token
//                                                    SharedPreferences.Editor successEditor = getSharedPreferences(SIGN_UP_TOKEN, MODE_PRIVATE).edit();
//                                                    String token = "" + 200;
//                                                    successEditor.putString("get_successToken", token).apply();
//
//                                                    Intent signUpIntent = new Intent(SignUp.this, SignIn.class);
//                                                    startActivity(signUpIntent);
//                                                    finish();
//                                                } else {
//                                                    Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//
//                                            }
//                                        });
//                                    } else {
//                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
//                                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
//                                            builder.setMessage("You are already registered");
//                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//
//                                                }
//                                            });
//                                            builder.show();
//                                        } else {
//                                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
//                                            builder.setMessage("" + task.getException().getMessage());
//                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//
//                                                }
//                                            });
//                                            builder.show();
//                                        }
//                                    }
//                                }
//                            });
//                }
//            });
        }
    }

    //Progressbar method
    static KProgressHUD kProgressHUD;

    public void showProgressBar() {
        kProgressHUD = KProgressHUD.create(SignUp.this)
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
        StaticClass.hideKeyboard(this);
        return super.dispatchTouchEvent(ev);
    }
}
