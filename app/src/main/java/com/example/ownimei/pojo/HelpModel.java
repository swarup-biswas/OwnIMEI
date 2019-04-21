package com.example.ownimei.pojo;

public class HelpModel {
    private String uID;
    private String helpMessage;

    public HelpModel() {
    }

    public HelpModel(String uID, String helpMessage) {
        this.uID = uID;
        this.helpMessage = helpMessage;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }
}
