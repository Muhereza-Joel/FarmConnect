package com.moels.farmconnect.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String name;
    private String phoneNumber;
    private String dateOfBirth;
    private String accountType;
    private String profilePicUrl;
    private String gender;

    public User(){}

    public User(String name, String gender, String phoneNumber, String dateOfBirth, String accountType, String profilePicUrl) {
        this.name = name;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.accountType = accountType;
        this.profilePicUrl = profilePicUrl;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getGender() {
        return gender;
    }
}
