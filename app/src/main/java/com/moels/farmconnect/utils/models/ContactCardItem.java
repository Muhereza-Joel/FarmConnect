package com.moels.farmconnect.utils.models;

public class ContactCardItem {
    private String username;
    private String phoneNumber;
    private String profilePicUrl;
    private String accountType;

    public ContactCardItem setUsername(String username) {
        this.username = username;
        return this;
    }

    public ContactCardItem setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public ContactCardItem setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
        return this;
    }

    public ContactCardItem setAccountType(String accountType) {
        this.accountType = accountType;
        return this;
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
