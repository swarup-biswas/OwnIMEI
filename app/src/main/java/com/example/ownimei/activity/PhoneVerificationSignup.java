package com.example.ownimei.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ownimei.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kaopiz.kprogresshud.KProgressHUD;

public class PhoneVerificationSignup extends AppCompatActivity implements View.OnClickListener {
    ImageView phoneVerificationBackBtnID;
    private EditText phoneVerificationCodeID;
    private Button phoneVerificationBtnID;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification_signup);

        auth = FirebaseAuth.getInstance();


        phoneVerificationCodeID = findViewById(R.id.phone_verification_code_ID);
        phoneVerificationBackBtnID = findViewById(R.id.phone_verification_back_btn);
        phoneVerificationBtnID = findViewById(R.id.phone_verification_btn_ID);

        phoneVerificationBtnID.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String vCode = SignUp.mVerificationId.getBytes().toString();
        String userInputCode = phoneVerificationCodeID.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(vCode,userInputCode );
        showProgressBar();
        verificationBack();
        signInWithPhoneAuthCredential(credential);
    }

    private void verificationBack() {
        Intent intentVerificationBack = new Intent(PhoneVerificationSignup.this,HomePage.class);
        startActivity(intentVerificationBack);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            hideProgressBar();
                            Log.d("onCodeSent:", "Successful");
                            Intent signUpIntent = new Intent(PhoneVerificationSignup.this, UserProfile.class);
                            startActivity(signUpIntent);
                            finish();
                            Toast.makeText(getApplicationContext(),"Registration Success",Toast.LENGTH_SHORT).show();

                        } else {
                            hideProgressBar();
                            Log.d("onCodeSent:", "UnSuccessful");
                            Toast.makeText(PhoneVerificationSignup.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                            Intent registrationFailedIntent = new Intent(PhoneVerificationSignup.this,SignUp.class);
                            startActivity(registrationFailedIntent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressBar();
                        Log.d("onCodeSent:", "onFailure" + e);
                        Toast.makeText(PhoneVerificationSignup.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                        Intent registrationFailedIntent = new Intent(PhoneVerificationSignup.this,SignUp.class);
                        startActivity(registrationFailedIntent);
                        finish();
                    }
                });
    }
    //Progressbar method
    static KProgressHUD kProgressHUD;
    public void showProgressBar() {
        kProgressHUD = KProgressHUD.create(PhoneVerificationSignup.this)
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
