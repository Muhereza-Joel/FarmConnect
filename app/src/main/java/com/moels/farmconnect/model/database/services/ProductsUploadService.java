package com.moels.farmconnect.model.database.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moels.farmconnect.model.database.ProductsTable;
import com.moels.farmconnect.model.database.ZonesTable;
import com.moels.farmconnect.utils.models.Product;
import com.moels.farmconnect.model.database.ProductsTableUtil;
import com.moels.farmconnect.model.database.ZonesTableUtil;

import java.util.List;

public class ProductsUploadService extends Service {
    private static final int POLL_INTERVAL = 1000;
    private Handler handler;
    private Runnable runnable;
    private ProductsTable productsDatabase;
    private ZonesTable zonesDatabase;

    public ProductsUploadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        productsDatabase = ProductsTableUtil.getInstance(getApplicationContext());
        zonesDatabase = ZonesTableUtil.getInstance(getApplicationContext());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startMonitoring(intent.getStringExtra("zoneID"));
        return START_STICKY;
    }

    private void startMonitoring(String zoneID){
        runnable = new Runnable() {
            @Override
            public void run() {
                checkForProducts(zoneID);
                handler.postDelayed(runnable, POLL_INTERVAL);

            }
        };
        handler.postDelayed(runnable, POLL_INTERVAL);
    }

    private void checkForProducts(String zoneID) {
        List<Product> products = productsDatabase.getAllProductsToUpload(zoneID);
        String phoneNumber = zonesDatabase.getZoneOwner(zoneID);
        DatabaseReference productsReference = FirebaseDatabase.getInstance().getReference().child("zones").child(phoneNumber).child(zoneID).child("products");
        for (Product product : products) {
            String productID = product.getProductID();

            // Check if the product already exists in Firebase
            productsReference.child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // TODO Product already exists in Firebase, handle accordingly
                        // For example, you may choose to skip uploading or update existing product's data
                    } else {
                        // Product doesn't exist, proceed with upload
                        productsReference.child(productID).setValue(product, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                if (error == null) {
                                    productsDatabase.updateProductUploadStatus(productID, true);
                                    stopSelf();
                                } else {
                                    productsDatabase.updateProductUploadStatus(productID,false);
                                    stopSelf();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle the cancellation or error case
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
        handler.removeCallbacks(runnable);
    }
}