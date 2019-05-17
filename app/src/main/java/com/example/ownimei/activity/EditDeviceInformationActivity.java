package com.example.ownimei.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ownimei.R;
import com.example.ownimei.StaticClass.StaticClass;
import com.example.ownimei.pojo.AddDeviceModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Calendar;

import static com.example.ownimei.activity.SignIn.USER_INFO;

public class EditDeviceInformationActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView deviceSelect;
    private EditText editDeviceName;
    private EditText editDeviceIMEIOne;
    private EditText editDeviceIMEITwo;
    private EditText editDeviceMAC;
    private TextView editDevicePurchaseDate;
    private TextView editDeviceMode;
    private Button updateDeviceButton;

    private String nameCatagory;
    private String nameDevice;
    private String nameIMEI1;
    private String nameIMEI2;
    private String nameMAC;
    private String namePurchaseDate;
    private String nameStatus;
    private String documentID;

    private ImageView updateBackButton;
    private DatePickerDialog datePickerDialog;
    private String selectionAddStatus;
    private String finalAddMode;
    //Query
    private String matchIMEiOne;
    private String matchIMEiOneForTwo;
    private String matchIMEiTwo;
    private String matchIMEiTwoForOne;
    private String matchMac;

   private String dName;
   private String eOne;
   private String eTwo;
   private String eMac;
   private String eDate;
   private String eStatus;


    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        db = FirebaseFirestore.getInstance();
        deviceSelect = findViewById(R.id.update_device_category_ID);
        editDeviceName = findViewById(R.id.edit_device_name_ID);
        editDeviceIMEIOne = findViewById(R.id.edit_device_IMEI_one_ID);
        editDeviceIMEITwo = findViewById(R.id.edit_device_IMEI_two_ID);
        editDeviceMAC = findViewById(R.id.edit_device_MAC_ID);
        editDevicePurchaseDate = findViewById(R.id.edit_device_purchase_date_ID);
        editDeviceMode = findViewById(R.id.edit_device_mode_ID);
        updateDeviceButton = findViewById(R.id.edit_device_update_button);
        updateBackButton = findViewById(R.id.edit_activity_back_button);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            nameCatagory = bundle.getString("deviceCatargory");
            nameDevice = bundle.getString("deviceName");
            nameIMEI1 = bundle.getString("imei1");
            nameIMEI2 = bundle.getString("imei2");
            nameMAC = bundle.getString("mac");
            namePurchaseDate = bundle.getString("purchaseDate");
            nameStatus = bundle.getString("status");
            documentID = bundle.getString("documentID");

            if (nameIMEI1.isEmpty()) {
                editDeviceIMEIOne.setVisibility(View.GONE);
            }
            if (nameIMEI2.isEmpty()) {
                editDeviceIMEITwo.setVisibility(View.GONE);
            }
            if (nameMAC.isEmpty()) {
                editDeviceMAC.setVisibility(View.GONE);
            }

            deviceSelect.setText(nameCatagory);
            EditText name = (EditText) findViewById(R.id.edit_device_name_ID);
            name.setText("" + nameDevice, TextView.BufferType.EDITABLE);
            EditText imei1 = (EditText) findViewById(R.id.edit_device_IMEI_one_ID);
            imei1.setText("" + nameIMEI1, TextView.BufferType.EDITABLE);
            EditText imei2 = (EditText) findViewById(R.id.edit_device_IMEI_two_ID);
            imei2.setText("" + nameIMEI2, TextView.BufferType.EDITABLE);
            EditText mac = (EditText) findViewById(R.id.edit_device_MAC_ID);
            mac.setText("" + nameMAC, TextView.BufferType.EDITABLE);
            editDevicePurchaseDate.setText(namePurchaseDate);
            editDeviceMode.setText(nameStatus);
        }
        updateDeviceButton.setOnClickListener(this);
        updateBackButton.setOnClickListener(this);
        editDevicePurchaseDate.setOnClickListener(this);
        editDeviceMode.setOnClickListener(this);

        editDeviceIMEIOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    if (s.toString().length() == 15) {
                        String one = editDeviceIMEIOne.getText().toString();
                        if (!one.isEmpty() && !one.equals(nameIMEI1)) {
                            queryOneImeiMethod();
                        }
                    }
                } catch (Exception e) {
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editDeviceIMEITwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.toString().length() == 15) {
                        String two = editDeviceIMEITwo.getText().toString();
                        if (!two.isEmpty() && !two.equals(nameIMEI2)) {
                            queryTwoImeiMethod();
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editDeviceMAC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    //TODO handle the mac search
                    if (s.toString().length() == 10) {
                        String mac = editDeviceMAC.getText().toString();
                        if (!mac.isEmpty() && !mac.equals(nameMAC)) {
                            queryMacMethod();
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_activity_back_button:
                onBackPressed();
                break;
            case R.id.edit_device_update_button:
                updateMethod();
                break;
            case R.id.edit_device_IMEI_one_ID:
                onBackPressed();
                break;
            case R.id.edit_device_IMEI_two_ID:
                onBackPressed();
                break;
            case R.id.edit_device_MAC_ID:
                onBackPressed();
                break;
            case R.id.edit_device_purchase_date_ID:
                datePickerMethod();
                break;
            case R.id.edit_device_mode_ID:
                updateDeviceMode();
                break;
        }
    }

    private void updateDeviceMode() {
        final String[] selectMode = getApplication().getResources().getStringArray(R.array.statusList);
        final AlertDialog.Builder addModeBuilder = new AlertDialog.Builder(EditDeviceInformationActivity.this);
        addModeBuilder.setTitle("Select mode");
        addModeBuilder.setSingleChoiceItems(R.array.statusList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        selectionAddStatus = selectMode[which];

                        break;
                    case 1:
                        selectionAddStatus = selectMode[which];

                        break;
                    case 2:
                        selectionAddStatus = selectMode[which];

                        break;
                }
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectionAddStatus != null) {
                    editDeviceMode.setText(selectionAddStatus);
                    finalAddMode = selectionAddStatus;
                }
            }
        });

        AlertDialog showAlartDialog = addModeBuilder.create();
        showAlartDialog.show();
    }

    private void datePickerMethod() {
        //Date picker
        Calendar calendarToday = Calendar.getInstance();
        int year = calendarToday.get(Calendar.YEAR);
        int month = calendarToday.get(Calendar.MONTH);
        int day = calendarToday.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(EditDeviceInformationActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editDevicePurchaseDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(calendarToday.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateMethod() {

         dName = editDeviceName.getText().toString();
         eOne = editDeviceIMEIOne.getText().toString();
         eTwo = editDeviceIMEITwo.getText().toString();
         eMac = editDeviceMAC.getText().toString();
         eDate = editDevicePurchaseDate.getText().toString();
         eStatus = editDeviceMode.getText().toString();

        //Update phone condition start
        if (nameCatagory.equals("Phone")) {
            showProgressBar();
            if (dName.isEmpty()) {
                hideProgressBar();
                AlertDialog.Builder builder = new AlertDialog.Builder(EditDeviceInformationActivity.this);
                builder.setMessage("Device model is empty.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return;
            }
            if (eOne.isEmpty()) {
                hideProgressBar();
                AlertDialog.Builder builder = new AlertDialog.Builder(EditDeviceInformationActivity.this);
                builder.setMessage("Please enter IMEI.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return;
            }
            if (eOne.length() != 15) {
                hideProgressBar();
                AlertDialog.Builder builder = new AlertDialog.Builder(EditDeviceInformationActivity.this);
                builder.setMessage("Please enter valid IMEI1.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return;
            }
            if (!eTwo.isEmpty()) {
                hideProgressBar();
                if (eTwo.length() != 15) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditDeviceInformationActivity.this);
                    builder.setMessage("Please enter valid IMEI2.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                    return;
                }
            }
            if (!nameIMEI2.isEmpty()) {
                if (eTwo.isEmpty()) {
                    hideProgressBar();
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditDeviceInformationActivity.this);
                    builder.setMessage("Please enter valid IMEI2.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                    return;
                }
            }

            if (eOne.equals(eTwo)) {
                hideProgressBar();
                AlertDialog.Builder builder = new AlertDialog.Builder(EditDeviceInformationActivity.this);
                builder.setMessage("IMEI1 && IMEI2 are same!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return;
            }
        }//End
        //Update Laptop condition start
        if (nameCatagory.equals("Laptop")) {
            showProgressBar();
            if (dName.isEmpty()) {
                hideProgressBar();
                AlertDialog.Builder builder = new AlertDialog.Builder(EditDeviceInformationActivity.this);
                builder.setMessage("Laptop model is empty.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return;
            }
            if (eMac.isEmpty()) {
                hideProgressBar();
                AlertDialog.Builder builder = new AlertDialog.Builder(EditDeviceInformationActivity.this);
                builder.setMessage("Laptop MAC address is empty.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return;
            }
        }//End

        //Test
        //Update dialog start
        final Dialog cUpdate = new Dialog(EditDeviceInformationActivity.this);
        cUpdate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cUpdate.setContentView(R.layout.update_dialog);
        Button cobButton = (Button) cUpdate.findViewById(R.id.update_button_ID);
        SharedPreferences sGetUserInfo = getSharedPreferences(USER_INFO, MODE_PRIVATE);
        final String userEmail = sGetUserInfo.getString("Email", "");
        final String userPass = sGetUserInfo.getString("Password", "");
        cobButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cUpdate.dismiss();
                showProgressBar();
                String pass = ((EditText) cUpdate.findViewById(R.id.update_con_pass_ID)).getText().toString();
                if (pass.isEmpty()) {
                    hideProgressBar();
                    android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(EditDeviceInformationActivity.this);
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
                    android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(EditDeviceInformationActivity.this);
                    builder1.setMessage("Please enter correct password.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder1.show();
                    return;
                }
                showProgressBar();
                if (!StaticClass.isConnected(EditDeviceInformationActivity.this)) {
                    StaticClass.buildDialog(EditDeviceInformationActivity.this);
                    hideProgressBar();

                } else {
                    showProgressBar();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential("" + userEmail, "" + userPass);
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    db.collection("DeviceInfo").document(documentID)
                                            .update("deviceName", dName, "mac", eMac, "phoneImeiOne", eOne, "phoneImeiTwo", eTwo, "purchaseDate", eDate, "status", eStatus)
                                            .addOnCompleteListener(new OnCompleteListener<Void>(

                                            ) {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        hideProgressBar();
                                                        startActivity(new Intent(EditDeviceInformationActivity.this, UserProfile.class));
                                                        Toast.makeText(EditDeviceInformationActivity.this, "Update success", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        hideProgressBar();
                                                        Toast.makeText(EditDeviceInformationActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            hideProgressBar();
                                            Toast.makeText(EditDeviceInformationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressBar();
                            Toast.makeText(EditDeviceInformationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        cUpdate.show();
        //Test end

    }

    //Query IMEI 1 start
    private void queryOneImeiMethod() {
        String im1 = editDeviceIMEIOne.getText().toString();
        showProgressBar();
        //IMEI one query
        if (!im1.isEmpty()) {
            Query queryImeiOne = db.collection("DeviceInfo").whereEqualTo("phoneImeiOne", im1);
            queryImeiOne.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        AddDeviceModel addDeviceModel = new AddDeviceModel();
                        if (document.getString("phoneImeiOne") != null) {
                            hideProgressBar();
                            addDeviceModel.setPhoneImeiOne(document.getString("phoneImeiOne"));
                            matchIMEiOneForTwo = addDeviceModel.getPhoneImeiOne();
                            Log.d("matchIMEiOneForTwo", "" + matchIMEiOneForTwo);
                            AlertDialog.Builder builderOne = new AlertDialog.Builder(EditDeviceInformationActivity.this);
                            builderOne.setMessage("Opps! this IMEI already exist. Please input your correct IMEI.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AddDeviceModel addDeviceModel = new AddDeviceModel();
                                    editDeviceIMEIOne.getText().clear();
                                    matchIMEiOne = addDeviceModel.getPhoneImeiOne();
                                    Log.d("Clear@@@@@@", "" + matchIMEiOne);
                                }
                            });
                            AlertDialog dialogOne = builderOne.create();
                            dialogOne.setCancelable(false);
                            dialogOne.show();
                        } else {
                            hideProgressBar();
                        }
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressBar();
                    Toast.makeText(EditDeviceInformationActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    hideProgressBar();
                }
            });

            //IMEI two
            if (matchIMEiOneForTwo == null) {
                Query queryImeiTwo = db.collection("DeviceInfo").whereEqualTo("phoneImeiTwo", im1);
                queryImeiTwo.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            AddDeviceModel addDeviceModel = new AddDeviceModel();
                            if (document.getString("phoneImeiTwo") != null) {
                                hideProgressBar();
                                addDeviceModel.setPhoneImeiOne(document.getString("phoneImeiTwo"));
                                AlertDialog.Builder builderOne = new AlertDialog.Builder(EditDeviceInformationActivity.this);
                                builderOne.setMessage("Opps! this IMEI already exist. Please enter your correct IMEI.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AddDeviceModel addDeviceModel = new AddDeviceModel();
                                        editDeviceIMEIOne.getText().clear();
                                        matchIMEiOne = addDeviceModel.getPhoneImeiOne();
                                    }
                                });
                                AlertDialog dialogOne = builderOne.create();
                                dialogOne.setCancelable(false);
                                dialogOne.show();
                            } else {
                                hideProgressBar();
                            }
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressBar();
                        Toast.makeText(EditDeviceInformationActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        hideProgressBar();
                    }
                });
            }
        }
    }

    //Query IMEI 1 end
    //Query IMEI 2 start
    private void queryTwoImeiMethod() {
        showProgressBar();
        String im2 = editDeviceIMEITwo.getText().toString();
        //IMEI one query
        if (!im2.isEmpty()) {
            Query queryImeiOne = db.collection("DeviceInfo").whereEqualTo("phoneImeiOne", im2);
            queryImeiOne.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        AddDeviceModel addDeviceModel = new AddDeviceModel();
                        if (document.getString("phoneImeiOne") != null) {
                            hideProgressBar();
                            addDeviceModel.setPhoneImeiTwo(document.getString("phoneImeiOne"));
                            matchIMEiTwoForOne = addDeviceModel.getPhoneImeiTwo();
                            Log.d("matchIMEiOneForTwo", "" + matchIMEiTwoForOne);
                            AlertDialog.Builder builderOne = new AlertDialog.Builder(EditDeviceInformationActivity.this);
                            builderOne.setMessage("Opps! this IMEI already exist. Please enter your correct IMEI.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AddDeviceModel addDeviceModel = new AddDeviceModel();
                                    editDeviceIMEITwo.getText().clear();
                                    matchIMEiTwo = addDeviceModel.getPhoneImeiTwo();
                                }
                            });
                            AlertDialog dialogOne = builderOne.create();
                            dialogOne.setCancelable(false);
                            dialogOne.show();
                        } else {
                            hideProgressBar();
                        }
                    }
                }

            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    hideProgressBar();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressBar();
                    Toast.makeText(EditDeviceInformationActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            //IMEI two
            if (matchIMEiTwoForOne == null) {
                Query queryImeiTwo = db.collection("DeviceInfo").whereEqualTo("phoneImeiTwo", im2);
                queryImeiTwo.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            AddDeviceModel addDeviceModel = new AddDeviceModel();
                            if (document.getString("phoneImeiTwo") != null) {
                                hideProgressBar();
                                addDeviceModel.setPhoneImeiOne(document.getString("phoneImeiTwo"));
                                AlertDialog.Builder builderOne = new AlertDialog.Builder(EditDeviceInformationActivity.this);
                                builderOne.setMessage("Opps! this IMEI already exist. Please enter your correct IMEI.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AddDeviceModel addDeviceModel = new AddDeviceModel();
                                        editDeviceIMEITwo.getText().clear();
                                        matchIMEiTwo = addDeviceModel.getPhoneImeiTwo();
                                        Log.d("Clear2@@@@@@", "" + matchIMEiTwo);
                                    }
                                });
                                AlertDialog dialogOne = builderOne.create();
                                dialogOne.setCancelable(false);
                                dialogOne.show();
                            } else {
                                hideProgressBar();
                            }
                        }
                    }

                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        hideProgressBar();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressBar();
                        Toast.makeText(EditDeviceInformationActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    //Query IMEI 2 end
    //Query MAC start
    private void queryMacMethod() {
        showProgressBar();
        String mac1 = editDeviceMAC.getText().toString();
        if (!mac1.isEmpty()) {
            Query queryMac = db.collection("DeviceInfo").whereEqualTo("mac", mac1);
            queryMac.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        AddDeviceModel addDeviceModel = new AddDeviceModel();
                        if (document.getString("mac") != null) {
                            hideProgressBar();
                            addDeviceModel.setMac(document.getString("mac"));
                            Log.d("matchIMEiOneForTwo", "" + matchIMEiOneForTwo);
                            AlertDialog.Builder builderOne = new AlertDialog.Builder(EditDeviceInformationActivity.this);
                            builderOne.setMessage("Opps! this IMEI already exist. Please input your correct IMEI.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AddDeviceModel addDeviceModel = new AddDeviceModel();
                                    editDeviceMAC.getText().clear();
                                    matchMac = addDeviceModel.getMac();
                                    Log.d("addDeviceMAC@@@@@@", "" + editDeviceMAC);
                                }
                            });
                            AlertDialog dialogOne = builderOne.create();
//                            dialogOne.setCanceledOnTouchOutside(false);
                            dialogOne.setCancelable(false);
                            dialogOne.show();
                        } else {
                            hideProgressBar();
                        }
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressBar();
                    Toast.makeText(EditDeviceInformationActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    hideProgressBar();
                }
            });
        }
    }
    //Query MAC end

    //Progressbar method
    static KProgressHUD kProgressHUD;

    public void showProgressBar() {
        kProgressHUD = KProgressHUD.create(EditDeviceInformationActivity.this)
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
