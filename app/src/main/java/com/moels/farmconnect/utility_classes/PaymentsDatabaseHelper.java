package com.moels.farmconnect.utility_classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.moels.farmconnect.models.PaymentCard;

import java.util.ArrayList;
import java.util.List;

public final class PaymentsDatabaseHelper extends FarmConnectDatabase implements PaymentsDatabase{

    private static PaymentsDatabaseHelper uniqueInstance;
    private PurchasesDatabase purchasesDatabase;
    private ContactsDatabase contactsDatabase;

    private PaymentsDatabaseHelper(Context context){
        super(context);
        contactsDatabase = ContactsDatabaseHelper.getInstance(context);
        purchasesDatabase = PurchasesDatabaseHelper.getInstance(context);
    }

    public static PaymentsDatabaseHelper getInstance(Context context){
        if (uniqueInstance == null){
            uniqueInstance = new PaymentsDatabaseHelper(context);
        }
        return uniqueInstance;
    }

    @Override
    public boolean addPaymentRecord(List<String> paymentDetails) {
        boolean rowCreated = false;

        ContentValues contentValues = new ContentValues();
        contentValues.put("payment_remote_id", paymentDetails.get(0));
        contentValues.put("productRemoteId", paymentDetails.get(1));
        contentValues.put("paymentMethod", paymentDetails.get(2));
        contentValues.put("totalAmount", paymentDetails.get(3));
        contentValues.put("amountPayed", paymentDetails.get(4));
        contentValues.put("balance", paymentDetails.get(5));
        contentValues.put("reason", paymentDetails.get(6));
        contentValues.put("referenceNumber", paymentDetails.get(7));
        contentValues.put("productOwner", paymentDetails.get(8));
        contentValues.put("createDate", paymentDetails.get(9));
        contentValues.put("createTime", paymentDetails.get(10));
        contentValues.put("uploaded", paymentDetails.get(11));
        contentValues.put("updated", paymentDetails.get(12));
        contentValues.put("zoneID", paymentDetails.get(13));

        long rowsInserted = sqLiteDatabase.insertWithOnConflict("payments", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (rowsInserted != -1){
            rowCreated = true;
            savePurchaseRecord(paymentDetails.get(1), paymentDetails.get(13));
            Log.d("FarmConnect", "addPaymentRecord: payment details saved success fully");
        }
        return rowCreated;
    }

    private void savePurchaseRecord(String productID, String zoneID){
        List<String> purchaseDetails = new ArrayList<String>();
        purchaseDetails.add(UI.generateUniqueID());
        purchaseDetails.add(productID);
        purchaseDetails.add("false"); //Flag for uploaded status
        purchaseDetails.add("false"); //Flag for update status
        purchaseDetails.add("Picked"); //Flag for delivery status
        purchaseDetails.add(zoneID);

        purchasesDatabase.addPurchaseRecord(purchaseDetails);
    }


    @SuppressLint("Range")
    @Override
    public List<PaymentCard> getPayments(String zoneID) {
        List<PaymentCard> paymentCards = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM payments WHERE zoneID = " + zoneID, null);
        if (cursor.moveToNext()){
            do {
                String methodOfPayment = cursor.getString(cursor.getColumnIndex("paymentMethod"));
                String amountPayed = cursor.getString(cursor.getColumnIndex("amountPayed"));
                String date = cursor.getString(cursor.getColumnIndex("createDate"));
                String time = cursor.getString(cursor.getColumnIndex("createTime"));
                String owner = cursor.getString(cursor.getColumnIndex("productOwner"));
                String recipientName = contactsDatabase.getOwnerUsername(owner);
                String imageUrl = contactsDatabase.getOwnerImageUrl(owner);
                PaymentCard paymentCard = new PaymentCard(methodOfPayment, amountPayed, date, time, recipientName, imageUrl);
                paymentCards.add(paymentCard);

            }while (cursor.moveToNext());
        }
        cursor.close();
        return paymentCards;
    }

    @Override
    public List<String> getPaymentDetails(String paymentID) {
        return null;
    }
}
