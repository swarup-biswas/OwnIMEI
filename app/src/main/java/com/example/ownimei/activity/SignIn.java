package com.example.ownimei.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ownimei.R;
import com.example.ownimei.StaticClass.StaticClass;
import com.example.ownimei.pojo.SignUpModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
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
import com.kaopiz.kprogresshud.KProgressHUD;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    private ImageView signInBackBtn;
    private EditText loginEmailID;
    private EditText logInPassID;
    private TextView forgotPassID;
    private LinearLayout createAccountID;
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

        loginEmailID.setOnEditorActionListener(editorActionListener);
        logInPassID.setOnEditorActionListener(editorActionListener);
        signInBackBtn.setOnClickListener(this);
        forgotPassID.setOnClickListener(this);
        createAccountID.setOnClickListener(this);
        signInLogInBtn.setOnClickListener(this);

    }


    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            switch (actionId) {
                case EditorInfo.IME_ACTION_NEXT:

                    break;
                case EditorInfo.IME_ACTION_DONE:
                    userSignIn();
                    break;
            }

            return false;
        }
    };


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
        final String loginEmail = loginEmailID.getText().toString().trim();
        final String loginPassword = logInPassID.getText().toString().trim();
        if (loginEmail.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
            builder.setMessage("Please enter your email address.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SignIn.this);
            builder1.setMessage("Please enter your valid email address.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder1.show();
            return;
        }
        if (loginPassword.isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SignIn.this);
            builder1.setMessage("Please enter your password.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder1.show();
            return;
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
                        if (authSignIn.getCurrentUser().isEmailVerified()) {

                            String userID = authSignIn.getUid();
                            Log.d("Uid123", userID);
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
                                        String firstName = userInfo.getUserFirstName();
                                        String emailUser = userInfo.getUserEmail();
                                        String phoneUser = userInfo.getUserPhone();
                                        //SharedPreferences name && email
                                        SharedPreferences sharedPreferences = getSharedPreferences(USER_INFO, MODE_PRIVATE);
                                        SharedPreferences.Editor editorUserInfo = sharedPreferences.edit();
                                        editorUserInfo.putString("Name", firstName);
                                        editorUserInfo.putString("Email", emailUser);
                                        editorUserInfo.putString("Phone", phoneUser);
                                        editorUserInfo.putString("Password", loginPassword);
                                        editorUserInfo.apply();
                                        hideProgressBar();
                                        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
                                        if (isFirstRun) {
                                            hideProgressBar();
                                            Intent signInIntent = new Intent(SignIn.this, UserProfile.class);
                                            signInIntent.putExtra("User_Name", firstName);
                                            signInIntent.putExtra("User_Email", emailUser);
                                            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                                    .edit()
                                                    .putBoolean("isFirstRun", false)
                                                    .apply();
                                            startActivity(signInIntent);
                                            finish();
                                        } else {
                                            hideProgressBar();
                                            Intent signInIntent = new Intent(SignIn.this, UserProfileSearch.class);
                                            signInIntent.putExtra("User_Name", firstName);
                                            signInIntent.putExtra("User_Email", emailUser);
//                                        signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(signInIntent);
                                            finish();
                                        }

                                    } else {
                                        hideProgressBar();
                                    }
                                }
                            });
                        } else {
                            hideProgressBar();
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                            builder.setMessage("Please verify your email address.\nIf you don't get verification link, Please resend.");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    authSignIn.signOut();
                                }
                            }).setNegativeButton("Resend", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                    FirebaseUser user = auth.getCurrentUser();
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SignIn.this, "Verification link send", Toast.LENGTH_SHORT).show();
                                                        authSignIn.signOut();
                                                    }
                                                }
                                            });
                                }
                            });
                            builder.show();
                        }
                    } else {
                        hideProgressBar();
                        final Dialog dialog = new Dialog(SignIn.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.signin_dialog_for_not_signup_user);
                        TextView signUpDialog = dialog.findViewById(R.id.dialog_sign_up);
                        Button buttonDialog = dialog.findViewById(R.id.dialog_ok);
                        signUpDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(SignIn.this, SignUp.class));
                                dialog.dismiss();
                            }
                        });
                        buttonDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressBar();
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
