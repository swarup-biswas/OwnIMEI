package com.example.ownimei.activity;

import android.app.Activity;
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
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ownimei.R;
import com.example.ownimei.StaticClass.StaticClass;
import com.example.ownimei.pojo.SignUpModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.kaopiz.kprogresshud.KProgressHUD;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    private ImageView signInBackBtn;
    private EditText loginEmailID;
    private EditText logInPassID;
    private TextView forgotPassID;
    private TextView createAccountID;
    private Button signInLogInBtn;

    private FirebaseAuth authSignIn;
    private FirebaseFirestore db;

    public static final String USER_ID = "User_Id";

    public static final String USER_INFO = "User_Info";

    public static final String SIGN_UP_TOKEN = "SIGN_UP_Success_Token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        authSignIn = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signInBackBtn = findViewById(R.id.sign_in_back_btn);
        loginEmailID = findViewById(R.id.sign_IN_email_ID);
        logInPassID = findViewById(R.id.sign_In_pass_ID);
        forgotPassID = findViewById(R.id.forgot_password_ID);
        createAccountID = findViewById(R.id.create_account_ID);
        signInLogInBtn = findViewById(R.id.sign_in_LOGIN_btn_ID);

        signInBackBtn.setOnClickListener(this);
        forgotPassID.setOnClickListener(this);
        createAccountID.setOnClickListener(this);
        signInLogInBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_back_btn:
                onBackPressed();
                finish();
                StaticClass.hideKeyboard(SignIn.this);
                break;
            case R.id.forgot_password_ID:
                Intent forgotPass = new Intent(SignIn.this, ForgotPasswordInputMailOrPhone.class);
                startActivity(forgotPass);
                break;
            case R.id.create_account_ID:
                Intent createAccountIntent = new Intent(SignIn.this, SignUp.class);
                startActivity(createAccountIntent);
                finish();
                break;
            case R.id.sign_in_LOGIN_btn_ID:
                userSignIn();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        hideProgressBar();
        super.onBackPressed();
    }

    private void userSignIn() {
        final String loginEmail = loginEmailID.getText().toString();
        final String loginPassword = logInPassID.getText().toString();
        if (loginEmail.isEmpty()) {
            loginEmailID.setError("Email required ");
            loginEmailID.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()) {
            loginEmailID.setError("Please enter a valid email");
            loginEmailID.requestFocus();
            return;
        }
        if (loginPassword.isEmpty()) {
            logInPassID.setError("Password required");
            logInPassID.requestFocus();
        }
        showProgressBar();
        if (!StaticClass.isConnected(SignIn.this)) {
            StaticClass.buildDialog(SignIn.this).show();
            hideProgressBar();
        } else {
            authSignIn.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        hideProgressBar();
                        if (authSignIn.getCurrentUser().isEmailVerified()) {
                            hideProgressBar();
                            String userID = authSignIn.getUid();
                            SharedPreferences.Editor editor = getSharedPreferences(USER_ID, MODE_PRIVATE).edit();
                            editor.putString("get_UID", "" + userID);
                            editor.apply();

                            //Load data start
                            DocumentReference docRef = db.collection("UserInformations").document("" + userID);
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    SignUpModel userInfo = documentSnapshot.toObject(SignUpModel.class);
                                    if (userInfo != null) {
                                        Log.d("nnnnnnnn#", "nnnnnn");
                                        String firstName = userInfo.getUserFirstName();
                                        String lastName = userInfo.getUserLastName();
                                        String emailUser = userInfo.getUserEmail();
                                        String nameUser = firstName + " " + lastName;
                                        //SharedPreferences name && email
                                        SharedPreferences sharedPreferences = getSharedPreferences(USER_INFO, MODE_PRIVATE);
                                        SharedPreferences.Editor editorUserInfo = sharedPreferences.edit();
                                        editorUserInfo.putString("Name", nameUser);
                                        editorUserInfo.putString("Email", emailUser);
                                        Log.d("SignInName", nameUser);
                                        editorUserInfo.apply();

//                                        //SignUp success token
//                                        SharedPreferences.Editor successEditor = getSharedPreferences(SIGN_UP_TOKEN, MODE_PRIVATE).edit();
//                                        String token = "" + 200;
//                                        successEditor.putString("get_successToken", token).apply();

                                        Intent signInIntent = new Intent(SignIn.this, UserProfile.class);
                                        signInIntent.putExtra("User_Name", nameUser);
                                        signInIntent.putExtra("User_Email", emailUser);
                                        signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(signInIntent);
                                        finish();
                                    } else {
                                        Log.d("nnnnnnnn#", "else");
                                    }
                                }
                            });
                        }else {
                            hideProgressBar();
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                            builder.setMessage("Please verify your email address.");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                        }
                    } else {
                        hideProgressBar();
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                        builder.setMessage("" + task.getException().getMessage());
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }
                }

            });

        }

    }

    //Progressbar method
    static KProgressHUD kProgressHUD;

    public void showProgressBar() {
        kProgressHUD = KProgressHUD.create(SignIn.this)
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
