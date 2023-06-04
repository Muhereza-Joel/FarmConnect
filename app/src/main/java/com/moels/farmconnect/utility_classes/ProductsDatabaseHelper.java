package com.moels.farmconnect.utility_classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FarmConnectProductsDatabase";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

    public ProductsDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE products(_pid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productRemoteId TEXT, " +
                "productName TEXT, " +
                "quantity TEXT, " +
                "price TEXT, " +
                "imageUrl TEXT, " +
                "uploaded TEXT, " +
                "updated TEXT, " +
                "owner TEXT, " +
                "date TEXT, " +
                "time TEXT, " +
                "status TEXT, " +
                "zoneID TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean deleteProductFromDatabase(String _id){
        boolean productDeleted = false;
        int count = sqLiteDatabase.delete("products", "productRemoteId = ?", new String[] {_id});
        if (count > 0) {
            productDeleted = true;
        }
        return productDeleted;
    }
}
