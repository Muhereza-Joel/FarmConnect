package com.moels.farmconnect.model.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.moels.farmconnect.utils.models.PurchasesCard;
import com.moels.farmconnect.utils.UI;

import java.util.ArrayList;
import java.util.List;

public final class PurchasesTableUtil extends FarmConnectDatabaseHelper implements PurchasesTable {

    private static PurchasesTableUtil uniqueInstance;
    private ContactsTable contactsDatabase;

    private PurchasesTableUtil(Context context) {
        super(context);
        contactsDatabase = ContactsTableUtil.getInstance(context);
    }

    public static PurchasesTableUtil getInstance(Context context){
        if (uniqueInstance == null){
            uniqueInstance = new PurchasesTableUtil(context);
        }
        return uniqueInstance;
    }

    @Override
    public boolean addPurchaseRecord(List<String> purchaseDetails) {
        boolean rowCreated = false;

        ContentValues contentValues = new ContentValues();
        contentValues.put("purchaseRemoteId", purchaseDetails.get(0));
        contentValues.put("productRemoteId", purchaseDetails.get(1));
        contentValues.put("date", UI.getCurrentDate());
        contentValues.put("time", UI.getCurrentTime());
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

    @SuppressLint("Range")
    @Override
    public List<PurchasesCard> getPurchases(String zoneID) {
        List<PurchasesCard> purchasesCards = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT productName, quantity, purchases.date, purchases.time, owner, imageUrl,purchaseRemoteId " +
                "FROM products JOIN purchases" +
                " ON products.productRemoteId = purchases.productRemoteId " +
                "WHERE purchases.zoneID = " + zoneID, null);

        if (cursor.moveToNext()){
            do {
                String productName = cursor.getString(cursor.getColumnIndex("productName"));
                String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String owner = contactsDatabase.getOwnerUsername(cursor.getString(cursor.getColumnIndex("owner")));
                String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));

                PurchasesCard purchasesCard = new PurchasesCard(productName, quantity, date, time, owner, imageUrl);
                purchasesCards.add(purchasesCard);

            }while (cursor.moveToNext());
        }
        cursor.close();
        return purchasesCards;
    }

    @Override
    public List<String> getPurchaseDetails(String purchaseID) {
        return null;
    }
}
