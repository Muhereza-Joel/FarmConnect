package com.moels.farmconnect.models;

public class ProductCard extends Card{
    private String productName, quantity, photoUrl, status, owner;

    private ProductCard(String _id, String productName, String quantity, String photoUrl, String createTime, String status, String owner) {
        super(_id, createTime);
        this.productName = productName;
        this.quantity = quantity;
        this.photoUrl = photoUrl;
        this.status = status;
        this.owner = owner;
    }

    public String getProductName() {
        return productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getOwner() {
        return owner;
    }

    public static ProductCard createProductCard(String _id, String productName, String quantity, String photoUrl, String createTime, String status, String owner){
        return new ProductCard(_id, productName, quantity, photoUrl, createTime, status, owner);
    }
}
