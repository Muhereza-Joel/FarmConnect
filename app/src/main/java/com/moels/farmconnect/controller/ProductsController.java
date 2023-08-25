package com.moels.farmconnect.controller;

import android.text.TextUtils;

import com.moels.farmconnect.model.command.ChangeProductStatusCommand;
import com.moels.farmconnect.model.command.Command;
import com.moels.farmconnect.model.database.ProductsTable;
import com.moels.farmconnect.model.database.ProductsTableUtil;
import com.moels.farmconnect.model.observers.Observer;
import com.moels.farmconnect.utils.UI;
import com.moels.farmconnect.utils.models.Product;
import com.moels.farmconnect.utils.preferences.Globals;

public final class ProductsController extends Controller implements Observer {
    private final ProductsTable productsTable = ProductsTable.getInstance(context);
    private ProductsController(){};

    private static ProductsController uniqueInstance;
    public static ProductsController getInstance(){
        if (uniqueInstance == null){
            uniqueInstance = new ProductsController();
        }
        return uniqueInstance;
    }

    public Product getProductDetails(String productID){
        return productsTable.getProductDetails(productID);
    }

    public void changeProductStatus(String productId, String productStatus){
        Command command = new ChangeProductStatusCommand(context, productId, productStatus, this);
        command.execute();

    }

    public void moveProduct(String currentZineID, String targetZoneID, String productIdToMove){
            productsTable.moveProductToZone(currentZineID,targetZoneID, productIdToMove);
            UI.displayToast(context, "Product Moved");

    }

    @Override
    public void update() {
        listener.onSuccess();
    }
}
