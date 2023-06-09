package com.moels.farmconnect.utility_classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ContactsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FarmConnectContactsDatabase";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

    public ContactsDatabaseHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION );
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contacts (" +
                "_id INTEGER PRIMARY KEY," +
                "username TEXT," +
                "phoneNumber TEXT)");

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

    public List<String> getAllRegisteredContacts(){
        List<String> phoneNumbers = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT phoneNumber FROM contacts", null);
        if (cursor.moveToNext()){
            do {
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
                phoneNumbers.add(phoneNumber);
            }while (cursor.moveToNext());
        }
        return phoneNumbers;
    }
}
