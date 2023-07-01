package com.moels.farmconnect.models;

public class PurchasesCard {
    private String productName;
    private String quantity;
    private String date;
    private String time;
    private String seller;
    private String imageUrl;

    public PurchasesCard(String productName, String quantity, String data, String time, String seller, String imageUrl) {
        this.productName = productName;
        this.quantity = quantity;
        this.date = data;
        this.time = time;
        this.seller = seller;
        this.imageUrl = imageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getSeller() {
        return seller;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
