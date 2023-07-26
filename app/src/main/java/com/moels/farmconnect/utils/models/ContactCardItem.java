package com.moels.farmconnect.utils.models;

public class ContactCardItem {
    private String username;
    private String phoneNumber;
    private String profilePicUrl;
    private String accountType;

    public void setCardItem(String username, String phoneNumber) {
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public void setCardItem(String username, String profilePicUrl, String phoneNumber, String accountType) {
        this.username = username;
        this.profilePicUrl = profilePicUrl;
        this.phoneNumber = phoneNumber;
        this.accountType = accountType;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getAccountType() {
        return accountType;
    }
}
