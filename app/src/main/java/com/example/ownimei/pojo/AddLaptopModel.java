package com.example.ownimei.pojo;

public class AddLaptopModel {
    private String uid;
    private String selectDevice;
    private String deviceName;
    private String mac;
    private String purchaseDate;

    public AddLaptopModel() {
    }

    public AddLaptopModel(String uid, String selectDevice, String deviceName, String mac, String purchaseDate) {
        this.uid = uid;
        this.selectDevice = selectDevice;
        this.deviceName = deviceName;
        this.mac = mac;
        this.purchaseDate = purchaseDate;
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
}
