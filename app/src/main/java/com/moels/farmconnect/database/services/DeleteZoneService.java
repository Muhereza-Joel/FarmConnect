package com.moels.farmconnect.database.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moels.farmconnect.utils.preferences.FarmConnectAppPreferences;
import com.moels.farmconnect.utils.preferences.Preferences;

public class DeleteZoneService extends Service {
   private static final int POLL_INTERVAL = 2000;
   private Handler handler;
   private Runnable runnable;
   private Preferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        preferences = FarmConnectAppPreferences.getInstance(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String remote_id = intent.getStringExtra("zoneID");
        deleteZoneFromFirebaseDatabase(remote_id);
        return START_STICKY;
    }

    private void deleteZoneFromFirebaseDatabase(String remote_id){
        runnable = new Runnable() {
            @Override
            public void run() {
                String phoneNumber = preferences.getString("authenticatedPhoneNumber");

                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                databaseRef.child("zones").child(phoneNumber).child(remote_id).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                stopSelf();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                stopSelf();
                                // Failed to delete zone
                                // Handle the error appropriately
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
    }
}