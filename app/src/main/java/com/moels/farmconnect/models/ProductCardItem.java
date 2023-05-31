package com.moels.farmconnect.models;

public class ProductCardItem {
    private String _id, productName, quantity, photoUrl, status;

    public ProductCardItem(String _id, String productName, String quantity, String photoUrl, String status) {
        this._id = _id;
        this.productName = productName;
        this.quantity = quantity;
        this.photoUrl = photoUrl;
        this.status = status;
    }

    public String get_id() {
        return _id;
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
}
