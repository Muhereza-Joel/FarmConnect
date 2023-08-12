package com.moels.farmconnect.model.database;

import android.content.ContentValues;
import android.content.Context;

import com.moels.farmconnect.utils.models.Card;
import com.moels.farmconnect.utils.models.Product;

import java.util.List;

public interface ProductsTable {
    static ProductsTable getInstance(Context context){
        return ProductsTableUtil.getInstance(context);
    }
    List<String> getProductRemoteIds();
    boolean addProduct(List<String> productDetails);
    List<String> getUpdatedProduct(String updatedProductID);
    List<Product> getAllProductsToUpload(String zoneID);
    List<Card> getAllProducts(String zoneID, String owner);
    Product getProductDetails(String productID);
    String getProductImageUrl(String productID);
    String getProductOwner(String productID);
    String getProductZoneID(String productID);
    boolean updateProduct(String productID, ContentValues contentValues);
    void updateProductUploadStatus(String productID, boolean uploaded);
    void updateProductUpdateStatus(String productID, boolean uploaded);
    boolean deleteProductFromDatabase(String _id);
}
