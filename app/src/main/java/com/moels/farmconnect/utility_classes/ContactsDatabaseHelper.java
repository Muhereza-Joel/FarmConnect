package com.moels.farmconnect.utility_classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContactsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FarmConnectContactsDatabase";
    private static final int DATABASE_VERSION = 2;

    public ContactsDatabaseHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION );
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE contacts (_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, phoneNumber TEXT)";
        db.execSQL(query);
        Log.d("FarmConnect","Contacts Database Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            // Drop the old table
            db.execSQL("DROP TABLE IF EXISTS contacts");

            // Create a new table with the updated schema
            db.execSQL("CREATE TABLE contacts (" +
                    "_id INTEGER PRIMARY KEY," +
                    "username TEXT," +
                    "phoneNumber TEXT)");
        }

    }
}
