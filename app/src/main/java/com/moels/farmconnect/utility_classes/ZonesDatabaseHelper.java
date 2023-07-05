package com.moels.farmconnect.utility_classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.moels.farmconnect.models.ZoneCardItem;

import java.util.ArrayList;
import java.util.List;

public final class ZonesDatabaseHelper extends FarmConnectDatabase implements ZonesDatabase{

    private static ZonesDatabaseHelper uniqueInstance;

    private ZonesDatabaseHelper(Context context){
        super(context);
    }

    public static ZonesDatabaseHelper getInstance(Context context){
        if (uniqueInstance == null){
            uniqueInstance = new ZonesDatabaseHelper(context);
        }
        return uniqueInstance;
    }

    @Override
    public List<ZoneCardItem> getZonesFromDatabase() {
        List<ZoneCardItem> listOfZoneCardItems = new ArrayList<>();
        String [] columnsToPick = {"remote_id","zoneName", "location", "createTime", "status", "owner"};
        Cursor cursor = sqLiteDatabase.query("zones", columnsToPick, null, null, null, null, null);

        if (cursor.moveToNext()) {
            do {
                @SuppressLint("Range") String remote_id = cursor.getString(cursor.getColumnIndex("remote_id"));
                @SuppressLint("Range") String zoneName = cursor.getString(cursor.getColumnIndex("zoneName"));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex("location"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));
                @SuppressLint("Range") String owner = cursor.getString(cursor.getColumnIndex("owner"));

                if (!TextUtils.isEmpty(zoneName) || !TextUtils.isEmpty(location)) {
                    ZoneCardItem zoneCardItem = new ZoneCardItem(remote_id, zoneName, location, createTime, status, owner);
                    listOfZoneCardItems.add(zoneCardItem);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listOfZoneCardItems;
    }

    @Override
    @SuppressLint("Range")
    public String getZoneOwner(String zoneID){
        String phoneNumber = "";
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT owner FROM zones WHERE remote_id = '"+ zoneID + "'", null);
        if (cursor.moveToNext()){
            do {
                 phoneNumber = cursor.getString(cursor.getColumnIndex("owner"));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return phoneNumber;
    }

    @Override
    public List<String> getZoneIds(String phoneNumber){
        List<String> zonesRemoteIds = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT remote_id FROM zones WHERE owner = '"+ phoneNumber + "'", null);
        if (cursor.moveToNext()){
            do {
                @SuppressLint("Range") String remote_id = cursor.getString(cursor.getColumnIndex("remote_id"));
                zonesRemoteIds.add(remote_id);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return zonesRemoteIds;
    }

    @Override
    public List<String> getZoneDetails(String zoneID){
        List<String> zoneDetails = new ArrayList<>();
        String [] columnsToPick = {"remote_id","zoneName", "location", "products", "description"};
        Cursor cursor = sqLiteDatabase.query("zones", columnsToPick, "remote_id = ?", new String[]{zoneID}, null, null, null);

        if (cursor.moveToNext()){
            do {
                @SuppressLint("Range") String zoneName = cursor.getString(cursor.getColumnIndex("zoneName"));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex("location"));
                @SuppressLint("Range") String products = cursor.getString(cursor.getColumnIndex("products"));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));

                zoneDetails.add(zoneName);
                zoneDetails.add(location);
                zoneDetails.add(products);
                zoneDetails.add(description);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return zoneDetails;
    }

    @SuppressLint("Range")
    @Override
    public List<String> getZonesToUpload() {
        List<String> zoneDetails = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM zones WHERE uploaded = 'false'", null);

        if (cursor.moveToFirst()) {
            do {
                zoneDetails.add(cursor.getString(cursor.getColumnIndex("remote_id")));
                zoneDetails.add(cursor.getString(cursor.getColumnIndex("zoneName")));
                zoneDetails.add(cursor.getString(cursor.getColumnIndex("location")));
                zoneDetails.add(cursor.getString(cursor.getColumnIndex("products")));
                zoneDetails.add(cursor.getString(cursor.getColumnIndex("description")));
                zoneDetails.add(cursor.getString(cursor.getColumnIndex("owner")));
                zoneDetails.add(cursor.getString(cursor.getColumnIndex("createDate")));
                zoneDetails.add(cursor.getString(cursor.getColumnIndex("createTime")));
                zoneDetails.add(cursor.getString(cursor.getColumnIndex("status")));


            } while (cursor.moveToNext());
        }
        cursor.close();
        return zoneDetails;
    }

    @Override
    public boolean addZoneToDatabase(List<String> zoneDetails){
        boolean rowCreated = false;

        ContentValues contentValues = new ContentValues();
        contentValues.put("remote_id", zoneDetails.get(0));
        contentValues.put("zoneName", zoneDetails.get(1));
        contentValues.put("location", zoneDetails.get(2));
        contentValues.put("products", zoneDetails.get(3));
        contentValues.put("description", zoneDetails.get(4));
        contentValues.put("uploaded", zoneDetails.get(5));
        contentValues.put("owner", zoneDetails.get(6));
        contentValues.put("createDate", zoneDetails.get(7));
        contentValues.put("createTime", zoneDetails.get(8));
        contentValues.put("status", zoneDetails.get(9));
        contentValues.put("updated", zoneDetails.get(10));

        long rowsInserted = sqLiteDatabase.insertWithOnConflict("zones", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

        if (rowsInserted != -1){
            Log.d("FarmConnect", "addZoneToDatabase: Collection zone added to database created");
            rowCreated = true;
        }else {
            Log.d("FarmConnect", "addZoneToDatabase: Collection zone already exits in database");
        }
        return rowCreated;
    }

    @Override
    public boolean updateZone(String zoneID, ContentValues contentValues){
        boolean zoneUpdated = false;
        int rowUpdated = sqLiteDatabase.update("zones", contentValues, "remote_id = ?", new String[] {zoneID});
        if (rowUpdated > 0){
            zoneUpdated = true;
        }
        return zoneUpdated;

    }

    @Override
    public boolean updateZoneUploadStatus(String zoneID, boolean uploadedStatus) {
        boolean rowUpdated = false;
        ContentValues values = new ContentValues();
        values.put("uploaded", uploadedStatus ? "true" : "false");
        int rowsUpdated = sqLiteDatabase.update("zones", values, "remote_id = ?", new String[]{zoneID});
        if (rowsUpdated > 0){
            rowUpdated = true;
        }
        return rowUpdated;
    }

    @Override
    public void deleteZoneFromDatabase(String _id){
        sqLiteDatabase.delete("zones", "remote_id = ?", new String[] {_id});
    }
}
