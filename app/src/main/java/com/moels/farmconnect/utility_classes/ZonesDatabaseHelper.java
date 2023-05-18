package com.moels.farmconnect.utility_classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ZonesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FarmConnectZonesDatabase";
    private static final int DATABASE_VERSION = 1;

    public ZonesDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE zones(_id INTEGER PRIMARY KEY AUTOINCREMENT, zoneName TEXT, location TEXT, products TEXT, description TEXT, uploaded TEXT)";
        db.execSQL(query);
        Log.d("FarmConnect","Zones Database Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
