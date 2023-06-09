package com.moels.farmconnect.utility_classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.moels.farmconnect.models.Product;
import com.moels.farmconnect.models.ProductCardItem;

import java.util.ArrayList;
import java.util.List;

public class ProductsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FarmConnectProductsDatabase";

    //Earlier version number was 2
    private static final int DATABASE_VERSION = 3;
    private final SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

    public ProductsDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE products(" +
                "_pid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productRemoteId TEXT UNIQUE, " +
                "productName TEXT, " +
                "quantity TEXT, " +
                "unitPrice TEXT, " +
                "price TEXT, " +
                "imageUrl TEXT, " +
                "uploaded TEXT, " +
                "updated TEXT, " +
                "owner TEXT, " +
                "date TEXT, " +
                "time TEXT, " +
                "status TEXT, " +
                "zoneID TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion){
            //Copy original data
            db.execSQL("CREATE TABLE products_temp AS SELECT * FROM products");

            db.execSQL("DROP TABLE IF EXISTS products");

            db.execSQL("CREATE TABLE products(" +
                    "_pid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "productRemoteId TEXT UNIQUE, " +
                    "productName TEXT, " +
                    "quantity TEXT, " +
                    "unitPrice TEXT, " +
                    "price TEXT, " +
                    "imageUrl TEXT, " +
                    "uploaded TEXT, " +
                    "updated TEXT, " +
                    "owner TEXT, " +
                    "date TEXT, " +
                    "time TEXT, " +
                    "status TEXT, " +
                    "zoneID TEXT)");


            //Copy data back to the table
            db.execSQL("INSERT INTO products(" +
                    "productRemoteId, productName, quantity, unitPrice, " +
                    "price, imageUrl, uploaded, updated, owner, " +
                    "date, time, status, zoneID) " +
                    "SELECT productRemoteId, productName, quantity, unitPrice, " +
                    "price, imageUrl, uploaded, updated, owner, " +
                    "date, time, status, zoneID FROM products_temp");

            //Delete the temporary table
            db.execSQL("DROP TABLE IF EXISTS products_temp");
        }

    }

    public List<String> getProductRemoteIds(){
        List<String> productsRemoteIds = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT productRemoteId FROM products", null);
        if (cursor.moveToNext()){
            do {
                @SuppressLint("Range") String productRemoteId = cursor.getString(cursor.getColumnIndex("productRemoteId"));
                productsRemoteIds.add(productRemoteId);
            }while (cursor.moveToNext());
        }
        return productsRemoteIds;
    }

    public boolean addProductToDatabase(List<String> productDetails){
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

    public List<String> getUpdatedProduct(String currentProductId){
        List<String> product = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM products WHERE updated = 'true' AND productRemoteId = '" + currentProductId + "'", null);
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
        return product;
    }

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

    public List<ProductCardItem> getAllProducts(String zoneID, String owner){
        List<ProductCardItem> items = new ArrayList<>();
        Cursor cursor;

        if (owner.equals("")){
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM products WHERE zoneID = '" + zoneID + "'", null);

        }else {
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM products WHERE zoneID = '" + zoneID + "' AND owner = '" + owner + "'", null);
        }


        if (cursor.moveToNext()) {
            do {
                @SuppressLint("Range") String productRemoteID = cursor.getString(cursor.getColumnIndex("productRemoteId"));
                @SuppressLint("Range") String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                @SuppressLint("Range") String productName = cursor.getString(cursor.getColumnIndex("productName"));
                @SuppressLint("Range") String productQuantity = cursor.getString(cursor.getColumnIndex("quantity"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("time"));
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));

                if (!TextUtils.isEmpty(imageUrl) || !TextUtils.isEmpty(productName) || !TextUtils.isEmpty(productQuantity)) {
                    ProductCardItem productCardItem = new ProductCardItem(productRemoteID, productName, productQuantity, imageUrl, createTime, status);
                    items.add(productCardItem);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;

    }

    public List<String> getProductDetails(String productID) {
        List<String> resultSet = new ArrayList<>();

        if (productID == null || productID.isEmpty()) {
            // Handle the case when productID is null or empty
            return resultSet;
        }

        String[] columnsToPick = {"imageUrl", "productName", "quantity", "unitPrice", "price"};
        Cursor cursor = sqLiteDatabase.query("products",
                columnsToPick,
                "productRemoteId = ?", new String[]{productID}, null, null, null);

        if (cursor.moveToNext()) {
            @SuppressLint("Range") String productImageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
            @SuppressLint("Range") String productName = cursor.getString(cursor.getColumnIndex("productName"));
            @SuppressLint("Range") String productQuantity = cursor.getString(cursor.getColumnIndex("quantity"));
            @SuppressLint("Range") String unitPrice = cursor.getString(cursor.getColumnIndex("unitPrice"));
            @SuppressLint("Range") String productPrice = cursor.getString(cursor.getColumnIndex("price"));

            resultSet.add(productImageUrl);
            resultSet.add(productName);
            resultSet.add(productQuantity);
            resultSet.add(unitPrice);
            resultSet.add(productPrice);

            cursor.close();
        }

        return resultSet;
    }


    @SuppressLint("Range")
    public String getImageUrl(String productID){
        String productImageUrl = "";
        Cursor cursor = sqLiteDatabase.query("products", new String[]{"imageUrl"},
                "productRemoteId = ?", new String[]{productID}, null, null, null);
        if (cursor.moveToNext()){
           productImageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
        }
        cursor.close();
        return productImageUrl;
    }

    public boolean updateProduct(String productID, ContentValues contentValues){
        boolean productUpdated = false;
        int rowsUpdated = sqLiteDatabase.update("products", contentValues, "productRemoteId = ?", new String[]{productID});
        if (rowsUpdated > 0){
            productUpdated = true;
        }
        return productUpdated;
    }

    public void updateProductUploaded(String productID, boolean uploaded) {
        ContentValues values = new ContentValues();
        values.put("uploaded", uploaded ? "true" : "false");
        sqLiteDatabase.update("products", values, "productRemoteId = ?", new String[]{productID});
    }

    public void updateProductUpdateStatus(String productID, boolean uploaded) {
        ContentValues values = new ContentValues();
        values.put("uploaded", uploaded ? "true" : "false");
        sqLiteDatabase.update("products", values, "productRemoteId = ?", new String[]{productID});
    }


    public boolean deleteProductFromDatabase(String _id){
        boolean productDeleted = false;
        int rowsDeleted = sqLiteDatabase.delete("products", "productRemoteId = ?", new String[] {_id});
        if (rowsDeleted > 0) {
            productDeleted = true;
        }
        return productDeleted;
    }

}
