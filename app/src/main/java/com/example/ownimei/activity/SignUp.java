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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ownimei.R;
import com.example.ownimei.StaticClass.StaticClass;
import com.example.ownimei.pojo.AddDeviceModel;
import com.example.ownimei.pojo.SignUpModel;
import com.example.ownimei.popUp.SignUpPopUpActivity;
import com.example.ownimei.recycleview.AddDeviceAdapter;
import com.google.android.gms.flags.Flag;
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
    private TextView forgotPass;
    private LinearLayout signIN;

    private String phone;
    private String gender;


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
        forgotPass = findViewById(R.id.sign_up_forgot_password_ID);
        signIN = findViewById(R.id.signUp_signIn_ID);
        firstNameID.setOnEditorActionListener(editorActionListener);
        emailID.setOnEditorActionListener(editorActionListener);
        passID.setOnEditorActionListener(editorActionListener);
        retypePassID.setOnEditorActionListener(editorActionListener);
        forgotPass.setOnClickListener(this);
        signIN.setOnClickListener(this);
        signUpBackBtn.setOnClickListener(this);
        signUpBtnID.setOnClickListener(this);

    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            switch (actionId) {
                case EditorInfo.IME_ACTION_NEXT:

                    break;
                case EditorInfo.IME_ACTION_DONE:
                    signUPMethod();
                    break;
            }

            return false;
        }
    };

    //On Click Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_back_btn:
                onBackPressed();
                break;

            case R.id.sign_UP_btn_ID:
                signUPMethod();
                break;
            case R.id.sign_up_forgot_password_ID:
                startActivity(new Intent(SignUp.this, ForgotPasswordInputMailOrPhone.class));
                break;
            case R.id.signUp_signIn_ID:
                startActivity(new Intent(SignUp.this, SignIn.class));
                break;
        }

    }

    //    //Back button

    @Override
    public void onBackPressed() {
        hideProgressBar();
        super.onBackPressed();
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

                        SignUpModel userInformation = new SignUpModel(firstName, lastName, email, phone, gender);
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
                                    Intent intent = new Intent(SignUp.this, SignUpPopUpActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    auth.signOut();
                                    finish();

                                } else {
                                    hideProgressBar();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                    builder.setMessage("" + task.getException());
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder.show();
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
