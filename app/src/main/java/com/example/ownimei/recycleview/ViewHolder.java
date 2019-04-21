package com.example.ownimei.recycleview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ownimei.R;

public class ViewHolder extends RecyclerView.ViewHolder  {


    private TextView vhDevice;
    private TextView vhModel;
    private TextView vhIMEIone;
    private TextView vhIMEItwo;
    private TextView vhPurchase;
    private TextView vhMac;
    private TextView vhStatus;

    private TextView vhDeviceName;
    private TextView vhModelName;
    private TextView vhIMEIoneName;
    private TextView vhIMEItwoName;
    private TextView vhPurchaseDate;
    private TextView vhMacName;
    private TextView vhStatusName;

    private LinearLayout iOne;
    private LinearLayout iTwo;
    private LinearLayout mac;

    private View totalLinearView;

    private LongPressInterface lPressInterface;

    public ViewHolder(@NonNull View itemView, LongPressInterface lPressInterface) {
        super(itemView);
        this.lPressInterface = lPressInterface;

        totalLinearView = itemView;

        vhDevice = itemView.findViewById(R.id.vh_device);
        vhModel = itemView.findViewById(R.id.vh_model);
        vhIMEIone = itemView.findViewById(R.id.vh_imei_one);
        vhIMEItwo = itemView.findViewById(R.id.vh_imei_two);
        vhMac = itemView.findViewById(R.id.vh_mac);
        vhPurchase = itemView.findViewById(R.id.vh_purchase);
        vhStatus = itemView.findViewById(R.id.vh_status);

        vhDeviceName = itemView.findViewById(R.id.vh_device_type);
        vhModelName = itemView.findViewById(R.id.vh_model_name);
        vhIMEIoneName = itemView.findViewById(R.id.vh_imei_one_name);
        vhIMEItwoName = itemView.findViewById(R.id.vh_imei_two_name);
        vhMacName = itemView.findViewById(R.id.vh_mac_ID);
        vhPurchaseDate = itemView.findViewById(R.id.vh_purchase_date);
        vhStatusName = itemView.findViewById(R.id.vh_status_name);

        iOne = itemView.findViewById(R.id.lIMEIone);
        iTwo = itemView.findViewById(R.id.lIMEItwo);
        mac = itemView.findViewById(R.id.lMAc);

    }

    //Interface class for item click
    public interface LongPressInterface {
        void longPressInterface(int position);

        void onPressInterface(int position);
    }

    public TextView getVhDevice() {
        return vhDevice;
    }

    public void setVhDevice(TextView vhDevice) {
        this.vhDevice = vhDevice;
    }

    public TextView getVhModel() {
        return vhModel;
    }

    public void setVhModel(TextView vhModel) {
        this.vhModel = vhModel;
    }

    public TextView getVhIMEIone() {
        return vhIMEIone;
    }

    public void setVhIMEIone(TextView vhIMEIone) {
        this.vhIMEIone = vhIMEIone;
    }

    public TextView getVhIMEItwo() {
        return vhIMEItwo;
    }

    public void setVhIMEItwo(TextView vhIMEItwo) {
        this.vhIMEItwo = vhIMEItwo;
    }

    public TextView getVhPurchase() {
        return vhPurchase;
    }

    public void setVhPurchase(TextView vhPurchase) {
        this.vhPurchase = vhPurchase;
    }

    public TextView getVhMac() {
        return vhMac;
    }

    public void setVhMac(TextView vhMac) {
        this.vhMac = vhMac;
    }

    public TextView getVhStatus() {
        return vhStatus;
    }

    public void setVhStatus(TextView vhStatus) {
        this.vhStatus = vhStatus;
    }

    public TextView getVhDeviceName() {
        return vhDeviceName;
    }

    public void setVhDeviceName(TextView vhDeviceName) {
        this.vhDeviceName = vhDeviceName;
    }

    public TextView getVhModelName() {
        return vhModelName;
    }

    public void setVhModelName(TextView vhModelName) {
        this.vhModelName = vhModelName;
    }

    public TextView getVhIMEIoneName() {
        return vhIMEIoneName;
    }

    public void setVhIMEIoneName(TextView vhIMEIoneName) {
        this.vhIMEIoneName = vhIMEIoneName;
    }

    public TextView getVhIMEItwoName() {
        return vhIMEItwoName;
    }

    public void setVhIMEItwoName(TextView vhIMEItwoName) {
        this.vhIMEItwoName = vhIMEItwoName;
    }

    public TextView getVhPurchaseDate() {
        return vhPurchaseDate;
    }

    public void setVhPurchaseDate(TextView vhPurchaseDate) {
        this.vhPurchaseDate = vhPurchaseDate;
    }

    public TextView getVhMacName() {
        return vhMacName;
    }

    public void setVhMacName(TextView vhMacName) {
        this.vhMacName = vhMacName;
    }

    public TextView getVhStatusName() {
        return vhStatusName;
    }

    public void setVhStatusName(TextView vhStatusName) {
        this.vhStatusName = vhStatusName;
    }

    public LinearLayout getiOne() {
        return iOne;
    }

    public void setiOne(LinearLayout iOne) {
        this.iOne = iOne;
    }

    public LinearLayout getiTwo() {
        return iTwo;
    }

    public void setiTwo(LinearLayout iTwo) {
        this.iTwo = iTwo;
    }

    public LinearLayout getMac() {
        return mac;
    }

    public void setMac(LinearLayout mac) {
        this.mac = mac;
    }

    public View getTotalLinearView() {
        return totalLinearView;
    }

    public void setTotalLinearView(View totalLinearView) {
        this.totalLinearView = totalLinearView;
    }

    public LongPressInterface getlPressInterface() {
        return lPressInterface;
    }

    public void setlPressInterface(LongPressInterface lPressInterface) {
        this.lPressInterface = lPressInterface;
    }
}
