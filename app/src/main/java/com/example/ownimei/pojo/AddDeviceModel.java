package com.example.ownimei.pojo;

import android.widget.TextView;

import java.util.ArrayList;

public class AddDeviceModel {

    private String userName;
    private String userEmail;
    private String documentId;
    private String uid;
    private String selectDevice;
    private String deviceName;
    private String phoneImeiOne;
    private String phoneImeiTwo;
    private String mac;
    private String purchaseDate;
    private String status;

    public AddDeviceModel() {
    }

    public AddDeviceModel(String userName, String userEmail, String uid, String selectDevice, String deviceName, String phoneImeiOne, String phoneImeiTwo, String mac, String purchaseDate, String status) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.uid = uid;
        this.selectDevice = selectDevice;
        this.deviceName = deviceName;
        this.phoneImeiOne = phoneImeiOne;
        this.phoneImeiTwo = phoneImeiTwo;
        this.mac = mac;
        this.purchaseDate = purchaseDate;
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSelectDevice() {
        return selectDevice;
    }

    public void setSelectDevice(String selectDevice) {
        this.selectDevice = selectDevice;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPhoneImeiOne() {
        return phoneImeiOne;
    }

    public void setPhoneImeiOne(String phoneImeiOne) {
        this.phoneImeiOne = phoneImeiOne;
    }

    public String getPhoneImeiTwo() {
        return phoneImeiTwo;
    }

    public void setPhoneImeiTwo(String phoneImeiTwo) {
        this.phoneImeiTwo = phoneImeiTwo;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
