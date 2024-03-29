package com.moels.farmconnect.utils.models;

public abstract class Card {
    private String id;
    private String createTime;

    public Card(String id, String createTime) {
        this.id = id;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

}
