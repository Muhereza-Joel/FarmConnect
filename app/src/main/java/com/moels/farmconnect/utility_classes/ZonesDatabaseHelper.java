package com.moels.farmconnect.utility_classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ZonesDatabaseHelper extends SQLiteOpenHelper {

    private static ZonesDatabaseHelper uniqueInstance;
    private static final String DATABASE_NAME = "FarmConnectZonesDatabase";
    private static final int DATABASE_VERSION = 4; //upgraded from version 3
    private SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

    private ZonesDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static ZonesDatabaseHelper getInstance(Context context){
        if (uniqueInstance == null){
            uniqueInstance = new ZonesDatabaseHelper(context);
        }
        return uniqueInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE zones(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "remote_id TEXT UNIQUE, " +
                "zoneName TEXT, " +
                "location TEXT, " +
                "products TEXT, " +
                "description TEXT, " +
                "uploaded TEXT, " +
                "owner TEXT, " +
                "createDate TEXT, " +
                "createTime TEXT, " +
                "status TEXT, " +
                "updated TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {

            db.execSQL("CREATE TABLE temp_zones AS SELECT * FROM zones");

            db.execSQL("DROP TABLE IF EXISTS zones");

            db.execSQL("CREATE TABLE zones(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "remote_id TEXT UNIQUE, " +
                    "zoneName TEXT, " +
                    "location TEXT, " +
                    "products TEXT, " +
                    "description TEXT, " +
                    "uploaded TEXT, " +
                    "owner TEXT, " +
                    "createDate TEXT, " +
                    "createTime TEXT, " +
                    "status TEXT, " +
                    "updated TEXT" +
                    ")");

            db.execSQL("INSERT INTO zones (" +
                    "_id, remote_id, zoneName, location, products, description, " +
                    "uploaded, owner, createDate, createTime, status, updated) " +
                    "SELECT _id, remote_id, zoneName, location, products, description, " +
                    "uploaded, owner, createDate, createTime, status, NULL " +
                    "FROM temp_zones");


            // Step 5: Drop the temporary table
            db.execSQL("DROP TABLE IF EXISTS temp_zones");
        }
    }

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

    public List<String> getZoneIds(){
        List<String> zonesRemoteIds = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT remote_id FROM zones", null);
        if (cursor.moveToNext()){
            do {
                @SuppressLint("Range") String remote_id = cursor.getString(cursor.getColumnIndex("remote_id"));
                zonesRemoteIds.add(remote_id);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return zonesRemoteIds;
    }

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

    public boolean updateZone(String zoneID, ContentValues contentValues){
        boolean zoneUpdated = false;
        int rowUpdated = sqLiteDatabase.update("zones", contentValues, "remote_id = ?", new String[] {zoneID});
        if (rowUpdated > 0){
            zoneUpdated = true;
        }
        return zoneUpdated;

    }


    public void deleteZoneFromDatabase(String _id){
        sqLiteDatabase.delete("zones", "remote_id = ?", new String[] {_id});
    }
}
