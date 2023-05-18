package com.moels.farmconnect.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String name;
    private String phoneNumber;
    private String dateOfBirth;
    private String accountType;
    private List<Byte> profilePicUrl;
    private String gender;

    public User(){}

    public User(String name, String gender, String phoneNumber, String dateOfBirth, String accountType, List profilePicUrl) {
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

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public List<Byte> getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(List<Byte> profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


}
