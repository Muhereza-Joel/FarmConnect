package com.moels.farmconnect.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moels.farmconnect.utility_classes.FarmConnectAppPreferences;
import com.moels.farmconnect.utility_classes.Preferences;

import java.util.HashMap;
import java.util.Map;

public class UpdateZoneService extends Service {
    private static final int POLL_INTERVAL = 2000;
    private Handler handler;
    private Runnable runnable;
    private SQLiteDatabase database;
    private Preferences preferences;
    // TODO Required to set updated flag for firebase to false in case of any failure

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        preferences = FarmConnectAppPreferences.getInstance(getApplicationContext());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("FarmConnect", "onStartCommand: Zone Update Service Running");
        String remote_id = intent.getStringExtra("zoneID");
        String cleanedZoneId = remote_id.replaceAll("[^a-zA-Z0-9_-]", "");
        String zoneName = intent.getStringExtra("zoneName");
        String location = intent.getStringExtra("location");
        String productsToCollect = intent.getStringExtra("productsToCollect");
        String description = intent.getStringExtra("description");

        updateZoneInFirebaseDatabase(cleanedZoneId, zoneName, location, productsToCollect, description);

        return START_STICKY;
    }

    private void updateZoneInFirebaseDatabase(String remote_id, String zoneName, String location, String productsToCollect, String description){

        runnable = new Runnable() {
            @Override
            public void run() {
                String phoneNumber = preferences.getString("authenticatedPhoneNumber");
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                Map<String, Object> updatedZone = new HashMap<>();
                updatedZone.put("zoneName", zoneName);
                updatedZone.put("zoneLocation", location);
                updatedZone.put("productsToCollect", productsToCollect);
                updatedZone.put("description", description);

                databaseReference.child("zones").child(phoneNumber).child(remote_id).updateChildren(updatedZone)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("FarmConnect", "onSuccess: Collection Zone Edited");

                                stopSelf();
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("FarmConnect", "onFailure: Failed to update zone" + e.getLocalizedMessage());
                                //TODO handle updates which failed
                            }
                        });

            }
        };
        handler.postDelayed(runnable, POLL_INTERVAL);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        Log.d("FarmConnect", "onDestroy: Zone Update Service stoped");
    }
}