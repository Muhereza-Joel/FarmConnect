package com.moels.farmconnect.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moels.farmconnect.models.Zone;
import com.moels.farmconnect.utility_classes.UI;

public class ZoneUploadService extends Service {
    private static final int POLL_INTERVAL = 2000; // Execute after 2 seconds
    private Handler handler;
    private Runnable runnable;
    private SQLiteDatabase database;
    private SharedPreferences myAppPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        myAppPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        handler = new Handler();
        database = openOrCreateDatabase("FarmConnectZonesDatabase", MODE_PRIVATE, null);
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
        Cursor cursor = database.rawQuery("SELECT * FROM zones WHERE uploaded = 'false'", null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String remote_id = cursor.getString(cursor.getColumnIndex("remote_id"));
                @SuppressLint("Range") String zoneName = cursor.getString(cursor.getColumnIndex("zoneName"));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex("location"));
                @SuppressLint("Range") String productsToCollect = cursor.getString(cursor.getColumnIndex("products"));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));
                @SuppressLint("Range") String owner = cursor.getString(cursor.getColumnIndex("owner"));
                @SuppressLint("Range") String createDate = cursor.getString(cursor.getColumnIndex("createDate"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));

                String products = " ";
                uploadZoneDataToFirebase(remote_id, zoneName, location, productsToCollect, description, owner, createDate, createTime, status, products);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private boolean uploadZoneDataToFirebase(
            String remote_id, String zoneName, String location, String productsToCollect,
            String description, String owner, String createDate, String createTime, String status, String products) {

            String phoneNumber = myAppPreferences.getString("authenticatedPhoneNumber", "123456789");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Zone zone = new Zone(remote_id, zoneName, location, productsToCollect, description, owner, createDate, createTime, status, products);

            databaseReference.child("zones").child(phoneNumber).child(remote_id).setValue(zone)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateZoneUploadedStatus(remote_id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        UI.displayToast(getApplicationContext(), "Network Error Occurred");
                    }
                });

            return true;
    }

    private void updateZoneUploadedStatus(String remote_id) {
        database.execSQL("UPDATE zones SET uploaded = 'true' WHERE remote_id = " + remote_id);

        // Check if the last zone is uploaded and stop the service
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM zones WHERE uploaded = 'false'", null);
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            if (count == 0) {
                stopSelf();  // Stop the service if the last zone is uploaded
                cursor.close();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
