package com.moels.farmconnect.utility_classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ContactsDatabaseHelper extends SQLiteOpenHelper implements ContactsDatabase{
    private static ContactsDatabaseHelper uniqueInstance;
    private static final String DATABASE_NAME = "FarmConnectContactsDatabase";
    private static final int DATABASE_VERSION = 3; //Upgraded database from version 2
    private SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

    private ContactsDatabaseHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION );
    }

    public static ContactsDatabaseHelper getInstance(Context context){
        if (uniqueInstance == null){
            uniqueInstance = new ContactsDatabaseHelper(context);
        }
        return uniqueInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contacts (" +
                "_id INTEGER PRIMARY KEY," +
                "username TEXT," +
                "phoneNumber TEXT UNIQUE," +
                "imageUrl TEXT," +
                "accountType TEXT," +
                "uploaded TEXT," +
                "updated TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            // Create a temporary table to backup the existing data
            db.execSQL("CREATE TABLE contacts_temp AS SELECT * FROM contacts");

            // Drop the old table
            db.execSQL("DROP TABLE IF EXISTS contacts");

            // Create a new table with the updated schema
            db.execSQL("CREATE TABLE contacts (" +
                    "_id INTEGER PRIMARY KEY," +
                    "username TEXT," +
                    "phoneNumber TEXT UNIQUE," +
                    "imageUrl TEXT," +
                    "accountType TEXT," +
                    "uploaded TEXT," +
                    "updated TEXT)");

            // Copy the data from the temporary table to the new table
            db.execSQL("INSERT INTO contacts (_id, username, phoneNumber) SELECT _id, username, phoneNumber FROM contacts_temp");

            // Drop the temporary table
            db.execSQL("DROP TABLE IF EXISTS contacts_temp");
        }
    }

    @Override
    public String getOwnerUsername(String phoneNumber){
        String productOwnerUsername = "~self~";
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT username FROM contacts WHERE phoneNumber = '" + phoneNumber +"'", null);
        if (cursor.moveToNext()){
            do {
                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("username"));
                if (!(TextUtils.isEmpty(username))){
                    productOwnerUsername = username;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return productOwnerUsername;
    }

    @Override
    public String getOwnerImageUrl(String phoneNumber){
        String ownerImageUrl = "";
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT imageUrl FROM contacts WHERE phoneNumber = '" + phoneNumber +"'", null);
        if (cursor.moveToNext()){
            do {
                @SuppressLint("Range") String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                if (!(TextUtils.isEmpty(imageUrl))){
                    ownerImageUrl = imageUrl;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return ownerImageUrl;
    }

    @Override
    public List<String> getAllRegisteredContacts(){
        List<String> phoneNumbers = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT phoneNumber FROM contacts", null);
        if (cursor.moveToNext()){
            do {
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
                if (!(TextUtils.isEmpty(phoneNumber))){
                    phoneNumbers.add(phoneNumber);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return phoneNumbers;
    }

    @Override
    public boolean addContactToDatabase(List<String> contactDetails) {
        boolean rowCreated = false;
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", contactDetails.get(0));
        contentValues.put("phoneNumber", contactDetails.get(1));
        contentValues.put("imageUrl", contactDetails.get(2));
        contentValues.put("accountType", contactDetails.get(3));
        contentValues.put("uploaded", contactDetails.get(4));
        contentValues.put("updated", contactDetails.get(5));

        long rowsInserted = sqLiteDatabase.insertWithOnConflict("contacts", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (rowsInserted != -1) {
            rowCreated = true;
        }
        return rowCreated;
    }
}
