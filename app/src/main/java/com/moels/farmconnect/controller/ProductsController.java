package com.moels.farmconnect.controller;

import com.moels.farmconnect.model.database.ProductsTable;
import com.moels.farmconnect.model.database.ProductsTableUtil;
import com.moels.farmconnect.model.observers.Observer;
import com.moels.farmconnect.utils.models.Product;

public final class ProductsController extends Controller implements Observer {

    private ProductsController(){};

    private static ProductsController uniqueInstance;
    public static ProductsController getInstance(){
        if (uniqueInstance == null){
            uniqueInstance = new ProductsController();
        }
        return uniqueInstance;
    }

    public Product getProductDetails(String productID){
        ProductsTable productsTable = ProductsTable.getInstance(context);
        return productsTable.getProductDetails(productID);
    }

    @Override
    public void update(Object object) {

    }
}
