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

    private static final String DATABASE_NAME = "FarmConnectZonesDatabase";
    private static final int DATABASE_VERSION = 3;

    private SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

    public ZonesDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE zones(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "remote_id TEXT, " +
                "zoneName TEXT, " +
                "location TEXT, " +
                "products TEXT, " +
                "description TEXT, " +
                "uploaded TEXT, " +
                "owner TEXT, " +
                "createDate TEXT, " +
                "createTime TEXT, " +
                "status TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion){
            db.execSQL("DROP TABLE IF EXISTS zones");
            db.execSQL("CREATE TABLE zones(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "remote_id TEXT, " +
                        "zoneName TEXT, " +
                        "location TEXT, " +
                        "products TEXT, " +
                        "description TEXT, " +
                        "uploaded TEXT, " +
                        "owner TEXT, " +
                        "createDate TEXT, " +
                        "createTime TEXT, " +
                        "status TEXT )");
        }

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

        long rowsInserted = sqLiteDatabase.insertWithOnConflict("zones", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (rowsInserted != -1){
            rowCreated = true;
        }
        return rowCreated;
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
        return zonesRemoteIds;
    }

    public void deleteZoneFromDatabase(String _id){
        sqLiteDatabase.delete("zones", "remote_id = ?", new String[] {_id});
    }
}
