package com.moels.farmconnect.utils.models;

public class Product {

    public Product(){}

    private String productID,
            productName,
            quantity,
            unitPrice,
            price,
            imageUrl,
            owner,
            createDate,
            createTime,
            status,
            uploadStatus,
            updatedStatus;

    public Product(String productID,
                   String productName,
                   String quantity,
                   String unitPrice,
                   String price,
                   String imageUrl,
                   String owner,
                   String date,
                   String time,
                   String status
    ) {
        this.productID = productID;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.price = price;
        this.imageUrl = imageUrl;
        this.owner = owner;
        this.createDate = date;
        this.createTime = time;
        this.status = status;
    }

    public String getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOwner() {
        return owner;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getStatus() {
        return status;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public String getUpdateStatus() {
        return updatedStatus;
    }

    public Product setProductID(String productID) {
        this.productID = productID;
        return this;
    }

    public Product setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public Product setQuantity(String quantity) {
        this.quantity = quantity;
        return this;
    }

    public Product setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public Product setPrice(String price) {
        this.price = price;
        return this;
    }

    public Product setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Product setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public Product setCreateDate(String createDate) {
        this.createDate = createDate;
        return this;
    }

    public Product setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public Product setStatus(String status) {
        this.status = status;
        return this;
    }

    public Product setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
        return this;
    }

    public Product setUpdatedStatus(String updatedStatus) {
        this.updatedStatus = updatedStatus;
        return this;
    }
}
