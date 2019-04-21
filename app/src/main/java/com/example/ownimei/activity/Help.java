package com.example.ownimei.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.ownimei.R;
import com.example.ownimei.StaticClass.StaticClass;
import com.example.ownimei.pojo.AddLaptopModel;
import com.example.ownimei.pojo.HelpModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.kaopiz.kprogresshud.KProgressHUD;

import static com.example.ownimei.activity.SignUp.USER_ID;

public class Help extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageView;
    private EditText helpMessage;
    private Button sendMessage;
    private FirebaseFirestore helpDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        helpDb = FirebaseFirestore.getInstance();

        imageView = findViewById(R.id.help_back_button);
        helpMessage = findViewById(R.id.help_message_ID);
        sendMessage = findViewById(R.id.help_button);

        imageView.setOnClickListener(this);
        sendMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.help_back_button:
                onBackPressed();
                break;
            case R.id.help_button:
                helpButtonMethod();
                break;
        }
    }

    private void helpButtonMethod() {
        String message = helpMessage.getText().toString();
        if (!StaticClass.isConnected(Help.this)) {
            StaticClass.buildDialog(Help.this).show();
        } else {
            if (message.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Help.this);
                builder.setMessage("Please type your message");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                return;
            } else {

                SharedPreferences prefs = getSharedPreferences(USER_ID, MODE_PRIVATE);
                String restoredUID = prefs.getString("get_UID", "No id found");
                showProgressBar();
                HelpModel helpMessageFinal = new HelpModel(restoredUID, message);
                DocumentReference helpDocumentReference = helpDb.collection("HelpMessage").document(restoredUID);
                helpDocumentReference.set(helpMessageFinal).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressBar();
                        AlertDialog.Builder builder =new AlertDialog.Builder(Help.this);
                        builder.setMessage("Thanks for your message.We will notify you by email").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Help.this,UserProfile.class);
                                startActivity(intent);
                            }
                        }).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressBar();
                        AlertDialog.Builder builder = new AlertDialog.Builder( Help.this);
                        builder.setMessage("Opps, there is a technical error.Please resend your message.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                    }
                });
            }


        }

    }

    //Progressbar method
    static KProgressHUD kProgressHUD;

    public void showProgressBar() {
        kProgressHUD = KProgressHUD.create(Help.this)
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
