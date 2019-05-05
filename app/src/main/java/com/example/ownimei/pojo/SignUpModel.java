package com.example.ownimei.pojo;

public class SignUpModel {

    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userPhone;
    private String userGender;

    public SignUpModel() {
    }

    public SignUpModel(String firstName, String lastName, String email,String userPhone,String userGender) {
        this.userFirstName = firstName;
        this.userLastName = lastName;
        this.userEmail = email;
        this.userPhone = userPhone;
        this.userGender = userGender;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }
}
