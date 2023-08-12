package com.moels.farmconnect.model.command;

import android.content.Context;

import com.moels.farmconnect.model.database.ProductsTable;
import com.moels.farmconnect.model.observers.Observer;

public class ChangeProductStatusCommand implements Command{

    private String productID, status;
    private Observer observer;
    private ProductsTable productsTable;

    public ChangeProductStatusCommand(Context context , String productID, String status, Observer observer){
        this.productID = productID;
        this.status = status;
        this.observer = observer;
        productsTable = ProductsTable.getInstance(context);
    }
    @Override
    public void execute() {
        productsTable.registerObserver(observer);
        productsTable.updateProductStockStatus(productID, status);
        productsTable.removeObserver(observer);
    }
}
