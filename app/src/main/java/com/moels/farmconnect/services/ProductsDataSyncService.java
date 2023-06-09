package com.moels.farmconnect.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moels.farmconnect.utility_classes.ContactsDatabaseHelper;
import com.moels.farmconnect.utility_classes.ProductsDatabaseHelper;
import com.moels.farmconnect.utility_classes.ZonesDatabaseHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductsDataSyncService extends Service implements ChildEventListener {
    private Set<String> syncedData = new HashSet<>();
    private static final int POLL_INTERVAL = 1000;
    private Handler handler;
    private Runnable runnable;
    private ZonesDatabaseHelper zonesDatabaseHelper;
    private ContactsDatabaseHelper contactsDatabaseHelper;
    private ProductsDatabaseHelper productsDatabaseHelper;

    private DatabaseReference zonesDatabaseReference;
    private DatabaseReference productsReference;
    public ProductsDataSyncService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        zonesDatabaseHelper = new ZonesDatabaseHelper(getApplicationContext());
        contactsDatabaseHelper = new ContactsDatabaseHelper(getApplicationContext());
        productsDatabaseHelper = new ProductsDatabaseHelper(getApplicationContext());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       boolean productsIdsLoaded = getExistingProductRemoteIDs(productsDatabaseHelper.getProductRemoteIds());
       if (productsIdsLoaded){
           startMonitoring();
       }

        return super.onStartCommand(intent, flags, startId);
    }

    public boolean getExistingProductRemoteIDs(List<String> productRemoteIDs) {
        syncedData.clear(); // Clear the existing data in the HashSet
        syncedData.addAll(productRemoteIDs); // Add all product remote IDs to the HashSet
        return true;
    }

    private void startMonitoring(){
        runnable = new Runnable() {
            @Override
            public void run() {
                    Log.d("FarmConnect", "run: DataSync Service is running");
                    if (getSizeOfSyncedData() == 0){
                        getAllProductsFromDatabase();
                    } else if(getSizeOfSyncedData() > 0){
                        getNewProductsFromFirebaseDatabase();
                    }


            }
        };
        handler.postDelayed(runnable, POLL_INTERVAL);
    }

    private void getAllProductsFromDatabase(){
        List<String> registeredContactList = contactsDatabaseHelper.getAllRegisteredContacts();
        for (String phoneNumber : registeredContactList){
            Log.d("FarmConnect", "getProductsFromDatabase: " + phoneNumber);
            List<String> zoneIDs = zonesDatabaseHelper.getZoneIds(phoneNumber);

            for (String zoneID : zoneIDs){
                Log.d("FarmConnect", "getProductsFromDatabase: " + zoneID);
                productsReference = FirebaseDatabase.getInstance().getReference().child("zones").child(phoneNumber).child(zoneID).child("products");

                productsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            List<String> productDetails = new ArrayList<>();

                            productDetails.add(productSnapshot.child("productID").getValue(String.class));
                            productDetails.add(productSnapshot.child("productName").getValue(String.class));
                            productDetails.add(productSnapshot.child("quantity").getValue(String.class));
                            productDetails.add(productSnapshot.child("unitPrice").getValue(String.class));
                            productDetails.add(productSnapshot.child("price").getValue(String.class));
                            productDetails.add(productSnapshot.child("imageUrl").getValue(String.class));
                            productDetails.add("true");
                            productDetails.add("false");
                            productDetails.add(productSnapshot.child("owner").getValue(String.class));
                            productDetails.add(productSnapshot.child("createDate").getValue(String.class));
                            productDetails.add(productSnapshot.child("createTime").getValue(String.class));
                            productDetails.add(productSnapshot.child("status").getValue(String.class));
                            productDetails.add(productSnapshot.child("zoneID").getValue(String.class));

                            productsDatabaseHelper.addProductToDatabase(productDetails);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        }

    }

    private void getNewProductsFromFirebaseDatabase(){
            List<String> registeredContactList = contactsDatabaseHelper.getAllRegisteredContacts();
            for (String phoneNumber : registeredContactList) {
                Log.d("FarmConnect", "getProductsFromDatabase: " + phoneNumber);
                List<String> zoneIDs = zonesDatabaseHelper.getZoneIds(phoneNumber);

                for (String zoneID : zoneIDs) {
                    Log.d("FarmConnect", "getProductsFromDatabase: " + zoneID);
                    productsReference = FirebaseDatabase.getInstance().getReference().child("zones").child(phoneNumber).child(zoneID).child("products");

                    productsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                                String productRemoteId = productSnapshot.child("productID").getValue(String.class);

                                if (!syncedData.contains(productRemoteId)) {
                                    List<String> productDetails = new ArrayList<>();

                                    productDetails.add(productRemoteId);
                                    productDetails.add(productSnapshot.child("productName").getValue(String.class));
                                    productDetails.add(productSnapshot.child("quantity").getValue(String.class));
                                    productDetails.add(productSnapshot.child("unitPrice").getValue(String.class));
                                    productDetails.add(productSnapshot.child("price").getValue(String.class));
                                    productDetails.add(productSnapshot.child("imageUrl").getValue(String.class));
                                    productDetails.add("true");
                                    productDetails.add("false");
                                    productDetails.add(productSnapshot.child("owner").getValue(String.class));
                                    productDetails.add(productSnapshot.child("createDate").getValue(String.class));
                                    productDetails.add(productSnapshot.child("createTime").getValue(String.class));
                                    productDetails.add(productSnapshot.child("status").getValue(String.class));
                                    productDetails.add(productSnapshot.child("zoneID").getValue(String.class));

                                    productsDatabaseHelper.addProductToDatabase(productDetails);

                                    syncedData.add(productRemoteId); // Add the productRemoteId to the HashSet
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                        // Rest of the ValueEventListener implementation...
                    });
                }
            }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }

   private boolean isDataSynced(String dataId){
        return syncedData.contains(dataId);
   }

    private void addToSyncedData(String dataId){
        syncedData.add(dataId);
    }

    private void removeFromSyncedData(String dataId){
        syncedData.remove(dataId);
    }

    private int getSizeOfSyncedData(){
        return syncedData.size();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}