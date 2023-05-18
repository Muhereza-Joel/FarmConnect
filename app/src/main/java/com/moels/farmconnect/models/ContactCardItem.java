package com.moels.farmconnect.models;

public class ContactCardItem {
    private String username;
    private String phoneNumber;
    private String profilePicUrl;

    public void setCardItem(String username, String phoneNumber) {
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public void setCardItem(String username, String profilePicUrl, String phoneNumber) {
        this.username = username;
        this.profilePicUrl = profilePicUrl;
        this.phoneNumber = phoneNumber;
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
}
