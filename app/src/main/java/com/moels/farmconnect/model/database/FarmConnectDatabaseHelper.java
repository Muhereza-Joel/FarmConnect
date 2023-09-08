package com.moels.farmconnect.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class FarmConnectDatabaseHelper extends SQLiteOpenHelper {

    //Upgraded from version 6
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "FarmConnectDatabase";
    public SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

    public FarmConnectDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createContactsTable(db);
        createZonesTable(db);
        createProductsTable(db);
        createProductZoneMappingTable(db);
        createPaymentsTable(db);
        createPurchasesTable(db);
    }

    private void createContactsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contacts (" +
                "_id INTEGER PRIMARY KEY," +
                "username TEXT," +
                "phoneNumber TEXT UNIQUE," +
                "imageUrl TEXT," +
                "accountType TEXT," +
                "uploaded TEXT," +
                "updated TEXT)");
    }

    private void createProductsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE products(" +
                "_pid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productRemoteId TEXT UNIQUE, " +
                "productName TEXT, " +
                "quantity TEXT, " +
                "unitPrice TEXT, " +
                "price TEXT, " +
                "imageUrl TEXT, " +
                "uploaded TEXT, " +
                "updated TEXT, " +
                "owner TEXT, " +
                "date TEXT, " +
                "time TEXT, " +
                "status TEXT)");
    }

    private void createZonesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE zones(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "remote_id TEXT UNIQUE, " +
                "zoneName TEXT, " +
                "location TEXT, " +
                "products TEXT, " +
                "description TEXT, " +
                "uploaded TEXT, " +
                "owner TEXT, " +
                "createDate TEXT, " +
                "createTime TEXT, " +
                "status TEXT, " +
                "updated TEXT" +
                ")");
    }

    private void createProductZoneMappingTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE product_zone_mapping ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " product_id INTEGER, " +
                " zone_id INTEGER, " +
                " FOREIGN KEY (product_id) REFERENCES products (productRemoteId), " +
                " FOREIGN KEY (zone_id) REFERENCES zones (remote_id))");

    }

    private void createPaymentsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE payments(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "payment_remote_id TEXT UNIQUE, " +
                "productRemoteId TEXT, " +
                "paymentMethod TEXT, " +
                "totalAmount TEXT, " +
                "amountPayed TEXT, " +
                "balance TEXT, " +
                "reason TEXT, " +
                "referenceNumber TEXT, " +
                "productOwner TEXT, " +
                "createDate TEXT, " +
                "createTime TEXT, " +
                "uploaded TEXT, " +
                "updated TEXT, " +
                "zoneID TEXT" +
                ")");
    }

    private void createPurchasesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE purchases(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "purchaseRemoteId TEXT UNIQUE, " +
                "productRemoteId TEXT, " +
                "date TEXT, " +
                "time TEXT, " +
                "uploaded TEXT, " +
                "updated TEXT, " +
                "status TEXT, " +
                "zoneID TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        upgradeContactsTable(db, oldVersion, newVersion);
        upgradeZonesTable(db, oldVersion, newVersion);
        upgradeProductsTable(db, oldVersion, newVersion);
        upgradeProductZoneMappingsTable(db, oldVersion, newVersion);

        upgradePurchasesTable(db, oldVersion, newVersion);
    }

    private void upgradeContactsTable(SQLiteDatabase db, int oldVersion, int newVersion) {
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
    private void upgradeProductsTable(SQLiteDatabase db, int oldVersion, int newVersion){
        if (oldVersion < newVersion){
            //Copy original data
            db.execSQL("CREATE TABLE products_temp AS SELECT * FROM products");

            db.execSQL("DROP TABLE IF EXISTS products");

            db.execSQL("CREATE TABLE products(" +
                    "_pid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "productRemoteId TEXT UNIQUE, " +
                    "productName TEXT, " +
                    "quantity TEXT, " +
                    "unitPrice TEXT, " +
                    "price TEXT, " +
                    "imageUrl TEXT, " +
                    "uploaded TEXT, " +
                    "updated TEXT, " +
                    "owner TEXT, " +
                    "date TEXT, " +
                    "time TEXT, " +
                    "status TEXT)");


            //Copy data back to the table
            db.execSQL("INSERT INTO products(" +
                    "productRemoteId, productName, quantity, unitPrice, " +
                    "price, imageUrl, uploaded, updated, owner, " +
                    "date, time, status) " +
                    "SELECT productRemoteId, productName, quantity, unitPrice, " +
                    "price, imageUrl, uploaded, updated, owner, " +
                    "date, time, status FROM products_temp");

            //Delete the temporary table
            db.execSQL("DROP TABLE IF EXISTS products_temp");
    }
}
    private void upgradeProductZoneMappingsTable(SQLiteDatabase db, int oldVersion, int newVersion){
        if (oldVersion < newVersion){
            db.execSQL("CREATE TABLE product_zone_mapping_temp AS SELECT * FROM product_zone_mapping");

            db.execSQL("DROP TABLE IF EXISTS product_zone_mapping");

            db.execSQL("CREATE TABLE IF NOT EXISTS product_zone_mapping ( " +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " product_id INTEGER, " +
                    " zone_id INTEGER, " +
                    " FOREIGN KEY (product_id) REFERENCES products (productRemoteId), " +
                    " FOREIGN KEY (zone_id) REFERENCES zones (remote_id))");

             db.execSQL("INSERT INTO product_zone_mapping (product_id, zone_id) SELECT product_id, zone_id FROM product_zone_mapping_temp");

             db.execSQL("DROP TABLE IF EXISTS product_zone_mapping_temp");
        }
    }
    private void upgradeZonesTable(SQLiteDatabase db,int oldVersion, int newVersion){
        if (oldVersion < newVersion) {

            db.execSQL("CREATE TABLE temp_zones AS SELECT * FROM zones");

            db.execSQL("DROP TABLE IF EXISTS zones");

            db.execSQL("CREATE TABLE zones(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "remote_id TEXT UNIQUE, " +
                    "zoneName TEXT, " +
                    "location TEXT, " +
                    "products TEXT, " +
                    "description TEXT, " +
                    "uploaded TEXT, " +
                    "owner TEXT, " +
                    "createDate TEXT, " +
                    "createTime TEXT, " +
                    "status TEXT, " +
                    "updated TEXT" +
                    ")");

            db.execSQL("INSERT INTO zones (" +
                    "_id, remote_id, zoneName, location, products, description, " +
                    "uploaded, owner, createDate, createTime, status, updated) " +
                    "SELECT _id, remote_id, zoneName, location, products, description, " +
                    "uploaded, owner, createDate, createTime, status, NULL " +
                    "FROM temp_zones");


            // Step 5: Drop the temporary table
            db.execSQL("DROP TABLE IF EXISTS temp_zones");
        }
    }
    private void upgradePurchasesTable(SQLiteDatabase db,int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("CREATE TABLE temp_purchases AS SELECT * FROM purchases");

            db.execSQL("DROP TABLE IF EXISTS purchases");

            db.execSQL("CREATE TABLE purchases(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "purchaseRemoteId TEXT UNIQUE, " +
                    "productRemoteId TEXT, " +
                    "date TEXT, " +
                    "time TEXT, " +
                    "uploaded TEXT, " +
                    "updated TEXT, " +
                    "status TEXT, " +
                    "zoneID TEXT)");

            db.execSQL("INSERT INTO purchases (" +
                    "_id, purchaseRemoteId, productRemoteId, date, time," +
                    "uploaded, updated, status, zoneID) " +
                    "SELECT _id, purchaseRemoteId, productRemoteId, NULL, NULL, uploaded," +
                    "updated, status, zoneID FROM temp_purchases");

            db.execSQL("DROP TABLE IF EXISTS temp_purchases");
        }
    }
    private void upgradePaymentsTable(SQLiteDatabase db, int oldVersion, int newVersion){
        if (oldVersion < newVersion) {
            db.execSQL("CREATE TABLE temp_payments AS SELECT * FROM payments");

            db.execSQL("DROP TABLE IF EXISTS payments");

            db.execSQL("CREATE TABLE payments(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "payment_remote_id TEXT UNIQUE, " +
                    "productRemoteId TEXT, " +
                    "paymentMethod TEXT, " +
                    "totalAmount TEXT, " +
                    "amountPayed TEXT, " +
                    "balance TEXT, " +
                    "reason TEXT, " +
                    "referenceNumber TEXT, " +
                    "productOwner TEXT, " +
                    "createDate TEXT, " +
                    "createTime TEXT, " +
                    "uploaded TEXT, " +
                    "updated TEXT, " +
                    "zoneID TEXT" +
                    ")");

            db.execSQL("INSERT INTO payments (" +
                    "_id, payment_remote_id, productRemoteId, paymentMethod, " +
                    "totalAmount, amountPayed, balance, reason, referenceNumber, productOwner, createDate, createTime," +
                    "uploaded, updated, zoneID) " +
                    "SELECT _id, payment_remote_id, productRemoteId, paymentMethod, " +
                    "totalAmount, amountPayed, balance, reason, referenceNumber, productOwner, createDate, createTime," +
                    "uploaded, updated, zoneID FROM temp_payments");

            db.execSQL("DROP TABLE IF EXISTS temp_payments");
        }
    }
}
