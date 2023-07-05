package com.moels.farmconnect.utility_classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

public final class PaymentsDatabaseHelper extends FarmConnectDatabase implements PaymentsDatabase{

    private static PaymentsDatabaseHelper uniqueInstance;

    private PaymentsDatabaseHelper(Context context){
        super(context);
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
        contentValues.put("productID", paymentDetails.get(1));
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
            Log.d("FarmConnect", "addPaymentRecord: payment details saved success fully");
        }
        return rowCreated;
    }

    @Override
    public List<String> getPayments(String zoneID) {
        return null;
    }

    @Override
    public List<String> getPaymentDetails(String paymentID) {
        return null;
    }
}
