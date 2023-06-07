package com.moels.farmconnect.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
   private SharedPreferences myAppPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        database = openOrCreateDatabase("FarmConnectZonesDatabase", MODE_PRIVATE, null);
        myAppPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
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
                String phoneNumber = myAppPreferences.getString("authenticatedPhoneNumber", "123456789");

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
        database.close(); //Close database since there is process that needs to access it again
    }
}