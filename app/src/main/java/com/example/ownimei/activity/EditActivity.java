package com.example.ownimei.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ownimei.R;
import com.example.ownimei.StaticClass.StaticClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Calendar;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

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
        final AlertDialog.Builder addModeBuilder = new AlertDialog.Builder(EditActivity.this);
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
        datePickerDialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editDevicePurchaseDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(calendarToday.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateMethod() {

        String dName = editDeviceName.getText().toString();
        String eOne = editDeviceIMEIOne.getText().toString();
        String eTwo = editDeviceIMEITwo.getText().toString();
        String eMac = editDeviceMAC.getText().toString();
        String eDate = editDevicePurchaseDate.getText().toString();
        String eStatus = editDeviceMode.getText().toString();

        //Update condition start
        if (nameCatagory.equals("Phone")) {

            if (dName.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setMessage("Device model is empty.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return;
            }
            if (eOne.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setMessage("Please enter IMEI.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return;
            }
            if (eOne.length() != 15) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setMessage("Please enter valid IMEI1.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return;
            }
            if (!eTwo.isEmpty()) {
                if (eTwo.length() != 15) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setMessage("IMEI1 && IMEI2 are same!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return;
            }
        }
        if (nameCatagory.equals("Laptop")) {
            if (dName.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setMessage("Laptop model is empty.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return;
            }
            if (eMac.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setMessage("Laptop MAC address is empty.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return;
            }
        }
        //Update condition end
        showProgressBar();
if(!StaticClass.isConnected(EditActivity.this)){
    StaticClass.buildDialog(EditActivity.this).show();
    hideProgressBar();
}
        db.collection("DeviceInfo").document(documentID)
                .update("deviceName",dName,"mac",eMac,"phoneImeiOne",eOne,"phoneImeiTwo",eTwo,"purchaseDate",eDate,"status",eStatus)
                .addOnCompleteListener(new OnCompleteListener<Void>(

                ) {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            hideProgressBar();
                            startActivity(new Intent(EditActivity.this,UserProfile.class));
                            Toast.makeText(EditActivity.this,"Update success",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(EditActivity.this,"Update failed",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    //Progressbar method
    static KProgressHUD kProgressHUD;

    public void showProgressBar() {
        kProgressHUD = KProgressHUD.create(EditActivity.this)
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
