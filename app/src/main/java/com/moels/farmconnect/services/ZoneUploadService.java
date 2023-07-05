package com.moels.farmconnect.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.database.Cursor;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moels.farmconnect.models.Zone;
import com.moels.farmconnect.utility_classes.FarmConnectAppPreferences;
import com.moels.farmconnect.utility_classes.Preferences;
import com.moels.farmconnect.utility_classes.UI;
import com.moels.farmconnect.utility_classes.ZonesDatabase;
import com.moels.farmconnect.utility_classes.ZonesDatabaseHelper;

import java.util.List;

public class ZoneUploadService extends Service {
    private static final int POLL_INTERVAL = 2000; // Execute after 2 seconds
    private Handler handler;
    private Runnable runnable;
    private ZonesDatabase zonesDatabase;
    private Preferences preferences;
    //TODO remove sqlite functionality from this service

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = FarmConnectAppPreferences.getInstance(getApplicationContext());
        handler = new Handler();
        zonesDatabase = ZonesDatabaseHelper.getInstance(getApplicationContext());
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
        List<String> zoneDetails = zonesDatabase.getZonesToUpload();
        if (zoneDetails != null && zoneDetails.size() > 0){
            uploadZoneDataToFirebase(zoneDetails.get(0), zoneDetails.get(1), zoneDetails.get(2),
                    zoneDetails.get(3), zoneDetails.get(4), zoneDetails.get(5), zoneDetails.get(6), zoneDetails.get(7), zoneDetails.get(8));
        }


    }

    private boolean uploadZoneDataToFirebase(
            String remote_id, String zoneName, String location, String productsToCollect,
            String description, String owner, String createDate, String createTime, String status) {

            String phoneNumber = preferences.getString("authenticatedPhoneNumber");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Zone zone = new Zone(remote_id, zoneName, location, productsToCollect, description, owner, createDate, createTime, status);

            databaseReference.child("zones").child(phoneNumber).child(remote_id).setValue(zone)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        zonesDatabase.updateZoneUploadStatus(remote_id, true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        zonesDatabase.updateZoneUploadStatus(remote_id, false);
                        UI.displayToast(getApplicationContext(), e.getMessage());
                    }
                });

            return true;
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
