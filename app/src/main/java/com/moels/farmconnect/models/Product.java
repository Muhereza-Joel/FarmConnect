package com.moels.farmconnect.models;

public class Product {

    private Product(){}

    private String productID, productName, quantity, price, owner, date, time;

    public Product(String productID, String productName, String quantity, String price, String owner, String date, String time) {
        this.productID = productID;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.owner = owner;
        this.date = date;
        this.time = time;
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

    public String getPrice() {
        return price;
    }

    public String getOwner() {
        return owner;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
