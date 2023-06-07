package com.moels.farmconnect.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moels.farmconnect.utility_classes.ProductsDatabaseHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateProductService extends Service {
    private static final int POLL_INTERVAL = 1000;
    private Handler handler;
    private Runnable runnable;
    private ProductsDatabaseHelper productsDatabaseHelper;
    private SharedPreferences sharedPreferences;

    public UpdateProductService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        productsDatabaseHelper = new ProductsDatabaseHelper(getApplicationContext());
        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
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
                updateProduct(productID, productsDatabaseHelper.getUpdatedProduct(productID));
            }
        };
        handler.postDelayed(runnable, POLL_INTERVAL);
    }

    private void updateProduct(String productID, List<String> updatedProductDetails){
        String phoneNumber = sharedPreferences.getString("authenticatedPhoneNumber", "123456789");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Log.d("FarmConnect", "updateProduct: " + updatedProductDetails.size());

        if (updatedProductDetails.size() > 0){
            Map<String, Object> updatedProduct = new HashMap<>();
            String zoneID = updatedProductDetails.get(0);
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
                            productsDatabaseHelper.updateProductUpdateStatus(productID, false);
                            stopSelf();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            productsDatabaseHelper.updateProductUpdateStatus(productID, true);
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