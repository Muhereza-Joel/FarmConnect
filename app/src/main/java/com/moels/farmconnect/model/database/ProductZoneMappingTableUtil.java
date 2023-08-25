package com.moels.farmconnect.model.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public final class ProductZoneMappingTableUtil extends FarmConnectDatabaseHelper implements ProductZoneMappingTable{

    private static ProductZoneMappingTableUtil uniqueInstance;

    private ProductZoneMappingTableUtil(Context context) {
        super(context);
    }

    public static ProductZoneMappingTableUtil getInstance(Context context){
        if (uniqueInstance == null){
            uniqueInstance = new ProductZoneMappingTableUtil(context);
        }
        return uniqueInstance;
    }

    @Override
    public void addNewMapping(String productID, String zoneID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("product_id", productID);
        contentValues.put("zone_id", zoneID);

        sqLiteDatabase.insert("product_zone_mapping", null, contentValues);

    }

    @Override
    public List<String> getProductMappings(String productID) {

        List<String> zoneIdList = new ArrayList<>();
        String [] selectionArgs = {productID};
        String query = "SELECT zone_id FROM product_zone_mapping WHERE product_id = ? ";

        Cursor cursor = sqLiteDatabase.rawQuery(query, selectionArgs);
        if(cursor.moveToNext()){
            do {
                @SuppressLint("Range") String zoneID = cursor.getString(cursor.getColumnIndex("zone_id"));
                zoneIdList.add(zoneID);
            } while (cursor.moveToNext());
        }

        return zoneIdList;
    }

    @Override
    public List<String> getZoneMappings(String zoneID) {
        List<String> productIdList = new ArrayList<>();
        String [] selectionArgs = {zoneID};
        String query = "SELECT product_id FROM product_zone_mapping WHERE zone_id = ? ";

        Cursor cursor = sqLiteDatabase.rawQuery(query, selectionArgs);
        if(cursor.moveToNext()){
            do {
                @SuppressLint("Range") String productID = cursor.getString(cursor.getColumnIndex("product_id"));
                productIdList.add(productID);
            } while (cursor.moveToNext());
        }

        return productIdList;
    }

    @Override
    public boolean updateProductMapping(String currentZoneId, String targetZoneId, String productId) {
        boolean productMoved = false;

        ContentValues contentValues = new ContentValues();
        contentValues.put("zone_id", targetZoneId);

        int rowsUpdated = sqLiteDatabase
                .update("product_zone_mapping", contentValues, "zone_id = ? AND product_id = ?",
                        new String[]{currentZoneId, productId});

        if (rowsUpdated > 0){
            productMoved = true;
        }

        return productMoved;
    }

    @Override
    public boolean deleteProductMapping(String productId, String zoneId) {
        boolean productDeleted = false;
        int rowsDeleted = sqLiteDatabase.delete("product_zone_mapping", "product_id = ? AND zone_id = ? ", new String[] {productId, zoneId});
        if (rowsDeleted > 0) {
            productDeleted = true;
        }
        return productDeleted;
    }

}
