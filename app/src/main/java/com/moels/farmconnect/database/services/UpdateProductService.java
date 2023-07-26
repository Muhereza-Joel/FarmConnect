package com.moels.farmconnect.database.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moels.farmconnect.database.ProductsDatabase;
import com.moels.farmconnect.database.ProductsDatabaseHelper;
import com.moels.farmconnect.database.ZonesDatabase;
import com.moels.farmconnect.database.ZonesDatabaseHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateProductService extends Service {
    private static final int POLL_INTERVAL = 1000;
    private Handler handler;
    private Runnable runnable;
    private ProductsDatabase productsDatabase;
    private ZonesDatabase zonesDatabase;

    public UpdateProductService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        productsDatabase = ProductsDatabaseHelper.getInstance(getApplicationContext());
        zonesDatabase = ZonesDatabaseHelper.getInstance(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String productID = intent.getStringExtra("productID");

        startMonitoring(productID);
        return START_STICKY;
    }

    private void startMonitoring(String productID){
        runnable = new Runnable() {
            @Override
            public void run() {
                updateProduct(productID, productsDatabase.getUpdatedProduct(productID));
            }
        };
        handler.postDelayed(runnable, POLL_INTERVAL);
    }

    private void updateProduct(String productID, List<String> updatedProductDetails){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Log.d("FarmConnect", "updateProduct: " + updatedProductDetails.size());

        if (updatedProductDetails.size() > 0){
            Map<String, Object> updatedProduct = new HashMap<>();
            String zoneID = updatedProductDetails.get(0);
            String phoneNumber = zonesDatabase.getZoneOwner(zoneID);
            updatedProduct.put("productName", updatedProductDetails.get(1));
            updatedProduct.put("quantity", updatedProductDetails.get(2));
            updatedProduct.put("unitPrice", updatedProductDetails.get(3));
            updatedProduct.put("price", updatedProductDetails.get(4));
            updatedProduct.put("imageUrl", updatedProductDetails.get(5));

            databaseReference.child("zones").child(phoneNumber)
                    .child(zoneID).child("products").child(productID).updateChildren(updatedProduct)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("FarmConnect", "Product Updated");
                            productsDatabase.updateProductUpdateStatus(productID, false);
                            stopSelf();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            productsDatabase.updateProductUpdateStatus(productID, true);
                            stopSelf();
                            Log.d("FarmConnect", "Product Updated Failed");
                        }
                    });
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}