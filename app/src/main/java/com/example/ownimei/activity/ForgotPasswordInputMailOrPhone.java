package com.example.ownimei.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ownimei.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;

public class ForgotPasswordInputMailOrPhone extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth authForgotPass;
    private ImageView backButtonID;
    private EditText setEmailId;
    private Button nextButtonID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_input_mail_or_phone);
        authForgotPass = FirebaseAuth.getInstance();
        backButtonID = findViewById(R.id.forgot_password_back_btn);
        setEmailId = findViewById(R.id.forgot_password_input_mail_ID);
        nextButtonID = findViewById(R.id.forgot_password_input_mail_button_ID);
        backButtonID.setOnClickListener(this);
        setEmailId.setOnClickListener(this);
        nextButtonID.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgot_password_back_btn:
                onBackPressed();
                break;
                case R.id.forgot_password_input_mail_button_ID:
                    forgotPassImplementMethod();
                break;
        }
    }

    private void forgotPassImplementMethod() {
        final String forgotEmail = setEmailId.getText().toString().trim();
        if (forgotEmail.isEmpty()){
            AlertDialog.Builder builder  = new AlertDialog.Builder(ForgotPasswordInputMailOrPhone.this);
            builder.setMessage("Please enter your password");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorRed));
            return;
        }
        showProgressBar();
        authForgotPass.sendPasswordResetEmail(forgotEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    hideProgressBar();
                    Toast.makeText(ForgotPasswordInputMailOrPhone.this,"Email sent, Please check your email",Toast.LENGTH_SHORT).show();
                    Intent forgotInt = new Intent(ForgotPasswordInputMailOrPhone.this,SignIn.class);
                    startActivity(forgotInt);
                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Toast.makeText(ForgotPasswordInputMailOrPhone.this,"Email not sent",Toast.LENGTH_SHORT).show();
            }
        });

    }
    //Progressbar method
    static KProgressHUD kProgressHUD;

    public void showProgressBar() {
        kProgressHUD = KProgressHUD.create(ForgotPasswordInputMailOrPhone.this)
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
