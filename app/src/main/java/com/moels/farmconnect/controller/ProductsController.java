package com.moels.farmconnect.controller;

import android.text.TextUtils;

import com.moels.farmconnect.model.command.ChangeProductStatusCommand;
import com.moels.farmconnect.model.command.Command;
import com.moels.farmconnect.model.database.ProductZoneMappingTable;
import com.moels.farmconnect.model.database.ProductsTable;
import com.moels.farmconnect.model.database.ProductsTableUtil;
import com.moels.farmconnect.model.observers.Observer;
import com.moels.farmconnect.utils.UI;
import com.moels.farmconnect.utils.models.Product;
import com.moels.farmconnect.utils.preferences.Globals;

import java.util.List;

public final class ProductsController extends Controller implements Observer {
    private final ProductsTable productsTable = ProductsTable.getInstance(context);
    private final ProductZoneMappingTable productZoneMappingTable = ProductZoneMappingTable.getInstance(context);
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

    public void copyProduct(String productId, List<String> zoneIds){
        int count = 0;
        for (String id : zoneIds){
            productZoneMappingTable.addNewMapping(productId, id);
            count ++;
        }

        if (count == 1) UI.displayToast(context, "Product Copied");
        else UI.displayToast(context, "Products Copied to " + count + " zones");
    }

    public void copyProduct(String productID, String zoneID){
        productZoneMappingTable.addNewMapping(productID, zoneID);
    }

    public List<String> getProductMappings(String productID){
        return productZoneMappingTable.getProductMappings(productID);
    }

    public List<String> getZoneMappings(String zoneID){
        return productZoneMappingTable.getZoneMappings(zoneID);
    }

    public boolean deleteProductMapping(String productID, String zoneID){
        boolean productMappedRemoved = productZoneMappingTable.deleteProductMapping(productID, zoneID);

        if (productMappedRemoved){
            UI.displayToast(context, "Product Removed");
        }

        return productMappedRemoved;
    }

    @Override
    public void update() {
        listener.onSuccess();
    }
}
