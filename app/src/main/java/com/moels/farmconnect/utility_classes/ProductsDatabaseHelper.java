package com.moels.farmconnect.utility_classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ProductsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FarmConnectProductsDatabase";
    private static final int DATABASE_VERSION = 2;
    private final SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

    public ProductsDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE products(_pid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productRemoteId TEXT, " +
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
            db.execSQL("DROP TABLE IF EXISTS products");
            db.execSQL("CREATE TABLE products(_pid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "productRemoteId TEXT, " +
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

        long rowsInserted = sqLiteDatabase.insert("products", null, contentValues);
        if (rowsInserted > 0) rowCreated = true;
        return rowCreated;
    }

    public List<String> getProductDetails(String productID){
        List<String> resultSet = new ArrayList<>();
        String [] columnsToPick = {"imageUrl","productName","quantity", "unitPrice", "price"};
        Cursor cursor = sqLiteDatabase.query("products",
                columnsToPick,
                "productRemoteId = ?", new String[]{productID}, null, null, null);

        if (cursor.moveToNext()){
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

    public boolean deleteProductFromDatabase(String _id){
        boolean productDeleted = false;
        int rowsDeleted = sqLiteDatabase.delete("products", "productRemoteId = ?", new String[] {_id});
        if (rowsDeleted > 0) {
            productDeleted = true;
        }
        return productDeleted;
    }

}
