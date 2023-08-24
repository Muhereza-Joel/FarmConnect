package com.moels.farmconnect.model.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.moels.farmconnect.model.observers.Observable;
import com.moels.farmconnect.model.observers.Observer;
import com.moels.farmconnect.utils.models.Zone;
import com.moels.farmconnect.utils.models.ZoneCardItem;
import com.moels.farmconnect.utils.preferences.Globals;

import java.util.ArrayList;
import java.util.List;

public final class ZonesTableUtil extends FarmConnectDatabaseHelper implements ZonesTable, Observable {
    static ZonesTable uniqueInstance = null;
    private ArrayList<Observer> observers;

    private ZonesTableUtil(Context context){
        super(context);
        observers = new ArrayList<>();
    }

    public static ZonesTable getInstance(Context context) {
        if (uniqueInstance == null) {
            uniqueInstance = new ZonesTableUtil(context);
        }
        return uniqueInstance;
    }
    @Override
    public List<ZoneCardItem> getAllZonesFormatedInCards() {
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
    public List<Zone> getAllZones() {
        List<Zone> zoneList = new ArrayList<>();
        String [] columnsToPick = {"remote_id","zoneName", "location", "createTime", "status", "owner"};
        Cursor cursor = sqLiteDatabase.query("zones", columnsToPick, null, null, null, null, null);

        if (cursor.moveToNext()) {
            do {
                Zone zone = new Zone();
                zone.setZoneID(cursor.getString(cursor.getColumnIndex("remote_id")));
                zone.setZoneName(cursor.getString(cursor.getColumnIndex("zoneName")));
                zone.setZoneLocation(cursor.getString(cursor.getColumnIndex("location")));
                zone.setTime(cursor.getString(cursor.getColumnIndex("createTime")));
                zone.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                zone.setOwner(cursor.getString(cursor.getColumnIndex("owner")));

                zoneList.add(zone);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return zoneList;
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
    @SuppressLint("Range")
    public Zone getZoneDetails(String zoneID){
        Zone zone = new Zone();
        String [] columnsToPick = {"remote_id","zoneName", "location", "products", "description"};
        Cursor cursor = sqLiteDatabase.query("zones", columnsToPick, "remote_id = ?", new String[]{zoneID}, null, null, null);

        if (cursor.moveToNext()){
            do {
                zone.setZoneName(cursor.getString(cursor.getColumnIndex("zoneName")));
                zone.setZoneLocation(cursor.getString(cursor.getColumnIndex("location")));
                zone.setProductsToCollect(cursor.getString(cursor.getColumnIndex("products")));
                zone.setDescription(cursor.getString(cursor.getColumnIndex("description")));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return zone;
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
    public boolean addZoneToDatabase(Zone zone){
        boolean rowCreated = false;

        ContentValues contentValues = new ContentValues();
        contentValues.put("remote_id", zone.getZoneID());
        contentValues.put("zoneName", zone.getZoneName());
        contentValues.put("location", zone.getZoneLocation());
        contentValues.put("products", zone.getProductsToCollect());
        contentValues.put("description", zone.getDescription());
        contentValues.put("uploaded", zone.getUploadStatus());
        contentValues.put("owner", zone.getOwner());
        contentValues.put("createDate", zone.getDate());
        contentValues.put("createTime", zone.getTime());
        contentValues.put("status", zone.getStatus());
        contentValues.put("updated", zone.getUpdatedStatus());

        long rowsInserted = sqLiteDatabase.insertWithOnConflict("zones", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

        if (rowsInserted != -1){
            Log.d("FarmConnect", "addZoneToDatabase: Collection zone added to database created");
            rowCreated = true;
            notifyObservers();
        }else {
            Log.d("FarmConnect", "addZoneToDatabase: Collection zone already exits in database");
        }
        return rowCreated;
    }

    @Override
    public boolean updateZone(String zoneID, Zone zone){
        ContentValues contentValues = new ContentValues();
        contentValues.put("zoneName", zone.getZoneName());
        contentValues.put("location", zone.getZoneLocation());
        contentValues.put("products", zone.getProductsToCollect());
        contentValues.put("description", zone.getDescription());
        contentValues.put("updated", "true");
        boolean zoneUpdated = false;
        int rowUpdated = sqLiteDatabase.update("zones", contentValues, "remote_id = ?", new String[] {zoneID});
        if (rowUpdated > 0){
            zoneUpdated = true;
            notifyObservers();
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

    @Override
    public void changeZoneStatus(String zoneID, String status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);

        int rowsUpdated = sqLiteDatabase.update("zones", contentValues, "remote_id = ?", new String[]{zoneID});
        if (rowsUpdated > 0) notifyObservers();
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
