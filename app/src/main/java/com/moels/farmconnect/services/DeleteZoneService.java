package com.moels.farmconnect.services;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteZoneService extends Service {
   private static final int POLL_INTERVAL = 2000;
   private Handler handler;
   private Runnable runnable;
   private SQLiteDatabase database;


    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        database = openOrCreateDatabase("FarmConnectZonesDatabase", MODE_PRIVATE, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String _id = intent.getStringExtra("zoneID");
        deleteZoneFromFirebaseDatabase(_id);
        return START_STICKY;
    }

    private void deleteZoneFromFirebaseDatabase(String _id){
        runnable = new Runnable() {
            @Override
            public void run() {
                String phoneNumber = "0776579631";  // Replace with the desired phone number

                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                databaseRef.child("zones").child(phoneNumber).child(_id).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                stopSelf();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
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
        database.close();
    }
}