package com.moels.farmconnect.utils.models;

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

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public User setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public User setAccountType(String accountType) {
        this.accountType = accountType;
        return this;
    }

    public User setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
        return this;
    }

    public User setGender(String gender) {
        this.gender = gender;
        return this;
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
