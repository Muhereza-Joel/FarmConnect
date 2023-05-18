package com.moels.farmconnect.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

public class ZoneUploadService extends Service {
    private static final int POLL_INTERVAL = 60000; // Interval in milliseconds (e.g., 1 minute)
    private Handler handler;
    private Runnable runnable;
    private SQLiteDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        database = openOrCreateDatabase("FarmConnectZonesDatabase.db", MODE_PRIVATE, null);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startMonitoring();
        return START_STICKY;
    }

    private void startMonitoring() {
        runnable = new Runnable() {
            @Override
            public void run() {
                checkForNewZones();
                handler.postDelayed(this, POLL_INTERVAL);
            }
        };
        handler.postDelayed(runnable, POLL_INTERVAL);
    }

    private void checkForNewZones() {
        // Query the SQLite database for new zones
        Cursor cursor = database.rawQuery("SELECT * FROM zones WHERE uploaded = 'false'", null);
        if (cursor.moveToFirst()) {
            do {
                // Retrieve zone data
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("_id"));
                @SuppressLint("Range") String zoneName = cursor.getString(cursor.getColumnIndex("zoneName"));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex("location"));
                @SuppressLint("Range") String productsToCollect = cursor.getString(cursor.getColumnIndex("products"));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));

                // Upload zone data to Firebase Realtime Database
                boolean uploadStatus = uploadZoneDataToFirebase(id, zoneName, location, productsToCollect, description);

                // Update the "uploaded" column in SQLite and Firebase
                if (uploadStatus == true) {
                    updateZoneUploadedStatus(id);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private boolean uploadZoneDataToFirebase(int id, String zoneName, String location, String productsToCollect, String description) {
        // TODO: Implement the code to upload zone data to Firebase Realtime Database
        // You can use Firebase SDK methods to accomplish this
        // Example: FirebaseDatabase.getInstance().getReference().child("zones").child(String.valueOf(id)).setValue(...)

        return true;
    }

    private void updateZoneUploadedStatus(int id) {
        // Update "uploaded" column in SQLite
        database.execSQL("UPDATE zones SET uploaded = 'true' WHERE _id = " + id);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        database.close();
    }
}
