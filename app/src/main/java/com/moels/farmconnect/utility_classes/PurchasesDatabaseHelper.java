package com.moels.farmconnect.utility_classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

public class PurchasesDatabaseHelper extends SQLiteOpenHelper implements PurchasesDatabase{

    private static PurchasesDatabaseHelper uniqueInstance;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FarmConnectPurchasesDatabase";
    private SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

    private PurchasesDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static PurchasesDatabaseHelper getInstance(Context context){
        if (uniqueInstance == null){
            uniqueInstance = new PurchasesDatabaseHelper(context);
        }
        return uniqueInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE purchases(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "purchaseRemoteId TEXT UNIQUE, " +
                "productRemoteId TEXT, " +
                "uploaded TEXT, " +
                "updated TEXT, " +
                "status TEXT, " +
                "zoneID TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public boolean addPurchaseRecord(List<String> purchaseDetails) {
        boolean rowCreated = false;

        ContentValues contentValues = new ContentValues();
        contentValues.put("purchaseRemoteId", purchaseDetails.get(0));
        contentValues.put("productRemoteId", purchaseDetails.get(1));
        contentValues.put("uploaded", purchaseDetails.get(2));
        contentValues.put("updated", purchaseDetails.get(3));
        contentValues.put("status", purchaseDetails.get(4));
        contentValues.put("zoneID", purchaseDetails.get(5));

        long rowsInserted = sqLiteDatabase.insertWithOnConflict("purchases", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (rowsInserted != -1){
            rowCreated = true;
            Log.d("FarmConnect", "addPurchaseRecord: purchases details saved success fully");
        }

        return rowCreated;
    }

    @Override
    public List<String> getPurchases(String zoneID) {
        return null;
    }

    @Override
    public List<String> getPurchaseDetails(String purchaseID) {
        return null;
    }
}
