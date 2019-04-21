package com.example.ownimei.pojo;

public class SignUpModel {

    private String userFirstName;
    private String userLastName;
    private String userEmail;

    public SignUpModel() {
    }

    public SignUpModel(String firstName, String lastName, String email) {
        this.userFirstName = firstName;
        this.userLastName = lastName;
        this.userEmail = email;
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



}
