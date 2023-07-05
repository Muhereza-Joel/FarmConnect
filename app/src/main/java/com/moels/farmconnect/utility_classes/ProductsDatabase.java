package com.moels.farmconnect.utility_classes;

import android.content.ContentValues;

import com.moels.farmconnect.models.Card;
import com.moels.farmconnect.models.Product;

import java.util.List;

public interface ProductsDatabase {
    List<String> getProductRemoteIds();
    boolean addProduct(List<String> productDetails);
    List<String> getUpdatedProduct(String updatedProductID);
    List<Product> getAllProductsToUpload(String zoneID);
    List<Card> getAllProducts(String zoneID, String owner);
    List<String> getProductDetails(String productID);
    String getProductImageUrl(String productID);
    String getProductOwner(String productID);
    String getProductZoneID(String productID);
    boolean updateProduct(String productID, ContentValues contentValues);
    void updateProductUploadStatus(String productID, boolean uploaded);
    void updateProductUpdateStatus(String productID, boolean uploaded);
    boolean deleteProductFromDatabase(String _id);
}
