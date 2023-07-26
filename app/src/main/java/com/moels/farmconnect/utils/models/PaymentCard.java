package com.moels.farmconnect.utils.models;

public class PaymentCard {
    String methodOfPayment;
    String amountPayed;
    String date;
    String time;
    String recipientName;
    String imageUrl;

    public PaymentCard(String methodOfPayment, String amountPayed, String date, String time, String recipientNameName, String imageUrl) {
        this.methodOfPayment = methodOfPayment;
        this.amountPayed = amountPayed;
        this.date = date;
        this.time = time;
        this.recipientName = recipientNameName;
        this.imageUrl = imageUrl;
    }

    public String getMethodOfPayment() {
        return methodOfPayment;
    }

    public String getAmountPayed() {
        return amountPayed;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
