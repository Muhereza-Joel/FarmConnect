package com.moels.farmconnect.models;

public class ProductCardItem {
    private String _id, productName, quantity, photoUrl, createTime, status;

    public ProductCardItem(String _id, String productName, String quantity, String photoUrl, String createTime, String status) {
        this._id = _id;
        this.productName = productName;
        this.quantity = quantity;
        this.photoUrl = photoUrl;
        this.createTime = createTime;
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

    public String getCreateTime() {
        return createTime;
    }

    public String getStatus() {
        return status;
    }
}
