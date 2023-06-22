package com.moels.farmconnect.models;

public class Message {
    private String sender;
    private String content;
    private String time;

    public Message(){}

    public Message(String sender, String content, String times) {
        this.sender = sender;
        this.content = content;
        this.time = times;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }
}
