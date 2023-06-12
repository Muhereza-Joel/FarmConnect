package com.moels.farmconnect.models;

public class Product {

    public Product(){}

    private String productID, productName, quantity,unitPrice, price, imageUrl, owner, createDate, createTime, status, zoneID;

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


}
