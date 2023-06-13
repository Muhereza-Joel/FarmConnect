package com.moels.farmconnect.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moels.farmconnect.dialogs.ProgressDialog;
import com.moels.farmconnect.utility_classes.ProductsDatabaseHelper;
import com.moels.farmconnect.utility_classes.ZonesDatabaseHelper;

public class DeleteProductService extends Service {

    private static final int POLL_INTERVAL = 1000;
    private Handler handler;
    private Runnable runnable;
    private SharedPreferences sharedPreferences;
    private ZonesDatabaseHelper zonesDatabaseHelper;
    public DeleteProductService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        zonesDatabaseHelper = new ZonesDatabaseHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String zoneID = intent.getStringExtra("zoneID");
        String productID = intent.getStringExtra("productID");
        String url = intent.getStringExtra("imageUrl");
        deleteImageAtUrl(zoneID, productID, url);
        return START_STICKY;
    }

    private void deleteImageAtUrl(String zoneID, String productID, String url){
        runnable = new Runnable() {
            @Override
            public void run() {
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference = firebaseStorage.getReferenceFromUrl(url);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FarmConnect", "image deleted");
                        deleteProductFromFirebase(zoneID, productID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        deleteProductFromFirebase(zoneID, productID);
                        e.getLocalizedMessage();
                    }
                });
            }
        };
        handler.postDelayed(runnable, POLL_INTERVAL);
    }

    private void deleteProductFromFirebase(String zoneID, String productID) {
        String phoneNumber = zonesDatabaseHelper.getZoneOwner(zoneID);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("zones").child(phoneNumber).child(zoneID).child("products").child(productID).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FarmConnect", "Phone Number " + phoneNumber);
                        Log.d("FarmConnect", "Zone ID " + zoneID);
                        Log.d("FarmConnect", "Product ID " + productID);
                        Log.d("FarmConnect", "Product Deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.getLocalizedMessage();
                    }
                });
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}