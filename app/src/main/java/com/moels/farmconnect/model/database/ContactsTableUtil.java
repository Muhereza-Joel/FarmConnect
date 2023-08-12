package com.moels.farmconnect.model.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public final class ContactsTableUtil extends FarmConnectDatabaseHelper implements ContactsTable {
    private static ContactsTableUtil uniqueInstance;

    private ContactsTableUtil(Context context){
        super(context);
    }

    public static ContactsTableUtil getInstance(Context context){
        if (uniqueInstance == null){
            uniqueInstance = new ContactsTableUtil(context);
        }
        return uniqueInstance;
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
