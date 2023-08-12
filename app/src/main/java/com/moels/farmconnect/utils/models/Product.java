package com.moels.farmconnect.utils.models;

public class Product {

    public Product(){}

    private String productID, productName, quantity,unitPrice, price, imageUrl, owner, createDate, createTime, status, zoneID, uploadStatus, updatedStatus;

    public Product(String productID,
                   String productName,
                   String quantity,
                   String unitPrice,
                   String price,
                   String imageUrl,
                   String owner,
                   String date,
                   String time,
                   String status,
                   String zoneID
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
        this.zoneID = zoneID;
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
    public String getZoneID() {
        return zoneID;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public String getUpdatedStatus() {
        return updatedStatus;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setZoneID(String zoneID) {
        this.zoneID = zoneID;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public void setUpdatedStatus(String updatedStatus) {
        this.updatedStatus = updatedStatus;
    }
}
