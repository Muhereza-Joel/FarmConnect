package com.moels.farmconnect.model.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.moels.farmconnect.model.observers.Observable;
import com.moels.farmconnect.model.observers.Observer;
import com.moels.farmconnect.utils.models.Card;
import com.moels.farmconnect.utils.models.Product;
import com.moels.farmconnect.utils.models.ProductCard;

import java.util.ArrayList;
import java.util.List;

public final class ProductsTableUtil extends FarmConnectDatabaseHelper implements ProductsTable, Observable {
    private static ProductsTableUtil uniqueInstance;

    private ArrayList<Observer> observers;
    private ProductsTableUtil(Context context){
        super(context);
        observers = new ArrayList<>();
    }

    public static ProductsTableUtil getInstance(Context context){
        if (uniqueInstance == null){
            uniqueInstance = new ProductsTableUtil(context);
        }
        return uniqueInstance;
    }


    @Override
    public List<String> getProductRemoteIds(){
        List<String> productsRemoteIds = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT productRemoteId FROM products", null);
        if (cursor.moveToNext()){
            do {
                @SuppressLint("Range") String productRemoteId = cursor.getString(cursor.getColumnIndex("productRemoteId"));
                productsRemoteIds.add(productRemoteId);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return productsRemoteIds;
    }

    @Override
    public boolean addProduct(List<String> productDetails){
        boolean rowCreated = false;

        ContentValues contentValues = new ContentValues();
        contentValues.put("productRemoteId", productDetails.get(0));
        contentValues.put("productName", productDetails.get(1));
        contentValues.put("quantity", productDetails.get(2));
        contentValues.put("unitPrice", productDetails.get(3));
        contentValues.put("price", productDetails.get(4));
        contentValues.put("imageUrl", productDetails.get(5));
        contentValues.put("uploaded", productDetails.get(6));
        contentValues.put("updated", productDetails.get(7));
        contentValues.put("owner", productDetails.get(8));
        contentValues.put("date", productDetails.get(9));
        contentValues.put("time", productDetails.get(10));
        contentValues.put("status", productDetails.get(11));
        contentValues.put("zoneID", productDetails.get(12));

        long rowsInserted = sqLiteDatabase.insertWithOnConflict("products", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (rowsInserted != -1) {
            rowCreated = true;
        }
        return rowCreated;
    }

    @Override
    public List<String> getUpdatedProduct(String updatedProductID){
        List<String> product = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM products WHERE updated = 'true' AND productRemoteId = '" + updatedProductID + "'", null);
        if (cursor.moveToNext()){
            do {
                @SuppressLint("Range") String zoneID = cursor.getString(cursor.getColumnIndex("zoneID"));
                @SuppressLint("Range") String productName = cursor.getString(cursor.getColumnIndex("productName"));
                @SuppressLint("Range") String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                @SuppressLint("Range") String unitPrice = cursor.getString(cursor.getColumnIndex("unitPrice"));
                @SuppressLint("Range") String price = cursor.getString(cursor.getColumnIndex("price"));
                @SuppressLint("Range") String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));

                product.add(zoneID);
                product.add(productName);
                product.add(quantity);
                product.add(unitPrice);
                product.add(price);
                product.add(imageUrl);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return product;
    }

    @Override
    public List<Product> getAllProductsToUpload(String currentZoneId){
        List<Product> products = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM products WHERE uploaded = 'false' AND zoneID = '" + currentZoneId + "'", null);
        if (cursor.moveToNext()){
            do{
                @SuppressLint("Range") String productRemoteId = cursor.getString(cursor.getColumnIndex("productRemoteId"));
                @SuppressLint("Range") String productName = cursor.getString(cursor.getColumnIndex("productName"));
                @SuppressLint("Range") String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                @SuppressLint("Range") String unitPrice = cursor.getString(cursor.getColumnIndex("unitPrice"));
                @SuppressLint("Range") String price = cursor.getString(cursor.getColumnIndex("price"));
                @SuppressLint("Range") String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                @SuppressLint("Range") String owner = cursor.getString(cursor.getColumnIndex("owner"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex("time"));
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));
                @SuppressLint("Range") String zoneID = cursor.getString(cursor.getColumnIndex("zoneID"));

                Product product = new Product(productRemoteId, productName, quantity, unitPrice, price, imageUrl, owner, date, time, status, zoneID);
                products.add(product);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    @Override
    public List<Card> getAllProducts(String zoneID, String owner){
        List<Card> items = new ArrayList<>();
        Cursor cursor;

        if (owner.equals("")){
            cursor = sqLiteDatabase.rawQuery("SELECT products.*" +
                    " FROM products" +
                    " LEFT JOIN purchases ON products.productRemoteId = purchases.productRemoteId " +
                    " WHERE purchases.productRemoteId IS NULL AND products.zoneID = " + zoneID, null);

        }else {
            cursor = sqLiteDatabase.rawQuery("SELECT products.*" +
                    " FROM products" +
                    " LEFT JOIN purchases ON products.productRemoteId = purchases.productRemoteId " +
                    " WHERE purchases.productRemoteId IS NULL AND products.zoneID = " + zoneID + " AND owner = " + "'" + owner + "'", null);
        }


        if (cursor.moveToNext()) {
            do {
                @SuppressLint("Range") String productRemoteID = cursor.getString(cursor.getColumnIndex("productRemoteId"));
                @SuppressLint("Range") String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                @SuppressLint("Range") String productName = cursor.getString(cursor.getColumnIndex("productName"));
                @SuppressLint("Range") String productQuantity = cursor.getString(cursor.getColumnIndex("quantity"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("time"));
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));
                @SuppressLint("Range") String productOwner = cursor.getString(cursor.getColumnIndex("owner"));

                if (!TextUtils.isEmpty(imageUrl) || !TextUtils.isEmpty(productName) || !TextUtils.isEmpty(productQuantity)) {
                    Card card = ProductCard.createProductCard(productRemoteID, productName, productQuantity, imageUrl, createTime, status, productOwner);
                    items.add(card);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;

    }

    @SuppressLint("Range")
    @Override
    public Product getProductDetails(String productID) {
        Product product = new Product();

        if (productID == null || productID.isEmpty()) {
            // Handle the case when productID is null or empty
            return null;
        }

        String[] columnsToPick = {"zoneID", "productRemoteId", "imageUrl", "productName", "quantity", "unitPrice", "price"};
        Cursor cursor = sqLiteDatabase.query("products",
                columnsToPick,
                "productRemoteId = ?", new String[]{productID}, null, null, null);

        if (cursor.moveToNext()) {
            product.setProductID(cursor.getString(cursor.getColumnIndex("productRemoteId")));
            product.setZoneID(cursor.getString(cursor.getColumnIndex("zoneID")));
            product.setImageUrl(cursor.getString(cursor.getColumnIndex("imageUrl")));
            product.setProductName(cursor.getString(cursor.getColumnIndex("productName")));
            product.setQuantity(cursor.getString(cursor.getColumnIndex("quantity")));
            product.setUnitPrice(cursor.getString(cursor.getColumnIndex("unitPrice")));
            product.setPrice(cursor.getString(cursor.getColumnIndex("price")));

        }
        cursor.close();
        return product;
    }


    @Override
    @SuppressLint("Range")
    public String getProductImageUrl(String productID){
        String productImageUrl = "";
        Cursor cursor = sqLiteDatabase.query("products", new String[]{"imageUrl"},
                "productRemoteId = ?", new String[]{productID}, null, null, null);
        if (cursor.moveToNext()){
           productImageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
        }
        cursor.close();
        return productImageUrl;
    }

    @SuppressLint("Range")
    @Override
    public String getProductOwner(String productID) {
        String owner = "";
        Cursor cursor = sqLiteDatabase.query("products", new String[]{"owner"},
                "productRemoteId = ?", new String[]{productID}, null, null, null);
        if (cursor.moveToNext()){
            owner = cursor.getString(cursor.getColumnIndex("owner"));
        }
        cursor.close();
        return owner;
    }

    @SuppressLint("Range")
    @Override
    public String getProductZoneID(String productID) {
        String zoneID = "";
        Cursor cursor = sqLiteDatabase.query("products", new String[]{"zoneID"},
                "productRemoteId = ?", new String[]{productID}, null, null, null);
        if (cursor.moveToNext()){
            zoneID = cursor.getString(cursor.getColumnIndex("zoneID"));
        }
        cursor.close();
        return zoneID;
    }

    @Override
    public boolean moveProductToZone(String targetZoneID, String productIdToMove) {
        boolean productMoved = false;

        ContentValues contentValues = new ContentValues();
        contentValues.put("zoneID", targetZoneID);

        int rowsUpdated = sqLiteDatabase.update("products", contentValues, "productRemoteId = ?", new String[]{productIdToMove});

        if (rowsUpdated > 0){
            productMoved = true;
        }

        return productMoved;
    }

    @Override
    public boolean updateProduct(String productID, ContentValues contentValues){
        boolean productUpdated = false;
        int rowsUpdated = sqLiteDatabase.update("products", contentValues, "productRemoteId = ?", new String[]{productID});

        if (rowsUpdated > 0){
            productUpdated = true;
        }
        return productUpdated;
    }

    @Override
    public void updateProductUploadStatus(String productID, boolean uploaded) {
        ContentValues values = new ContentValues();
        values.put("uploaded", uploaded ? "true" : "false");
        sqLiteDatabase.update("products", values, "productRemoteId = ?", new String[]{productID});
    }

    @Override
    public void updateProductUpdateStatus(String productID, boolean uploaded) {
        ContentValues values = new ContentValues();
        values.put("uploaded", uploaded ? "true" : "false");
        sqLiteDatabase.update("products", values, "productRemoteId = ?", new String[]{productID});
    }

    @Override
    public void updateProductStockStatus(String productId, String status) {
        ContentValues values = new ContentValues();
        values.put("status", status);
        sqLiteDatabase.update("products", values, "productRemoteId = ?", new String[]{productId});
        notifyObservers();
    }

    @Override
    public boolean deleteProductFromDatabase(String _id){
        boolean productDeleted = false;
        int rowsDeleted = sqLiteDatabase.delete("products", "productRemoteId = ?", new String[] {_id});
        if (rowsDeleted > 0) {
            productDeleted = true;
        }
        return productDeleted;
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        int index = observers.indexOf(observer);
        if (index >= 0) observers.remove(index);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers){
            observer.update();
        }
    }
}
