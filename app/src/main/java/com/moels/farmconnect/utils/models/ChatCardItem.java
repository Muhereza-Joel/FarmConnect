package com.moels.farmconnect.utils.models;

public class ChatCardItem {

    private String username;
    private String photoUrl;

    public ChatCardItem(String username, String photoUrl) {
        this.username = username;
        this.photoUrl = photoUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

