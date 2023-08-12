package com.moels.farmconnect.model.database.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moels.farmconnect.R;
import com.moels.farmconnect.model.database.ContactsTable;
import com.moels.farmconnect.model.database.ContactsTableUtil;
import com.moels.farmconnect.model.database.ProductsTable;
import com.moels.farmconnect.model.database.ProductsTableUtil;
import com.moels.farmconnect.model.database.ZonesTable;
import com.moels.farmconnect.model.database.ZonesTableUtil;
import com.moels.farmconnect.utils.models.Product;
import com.moels.farmconnect.view.activities.MainActivity;
import com.moels.farmconnect.utils.preferences.FarmConnectAppPreferences;
import com.moels.farmconnect.utils.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductsDataSyncService extends Service{
    private Set<String> syncedData = new HashSet<>();
    private List<String> ids = new ArrayList<>();
    private static final int POLL_INTERVAL = 1000;
    private Handler handler;
    private Runnable runnable;
    private ZonesTable zonesDatabase;
    private ContactsTable contactsDatabase;
    private ProductsTable productsDatabase;

    private DatabaseReference zonesDatabaseReference;
    private DatabaseReference productsReference;
    private ProductsSyncListener productsSyncListener;
    private final IBinder  binder = new ProductsSyncServiceBinder();
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private String authenticatedPhoneNumber;
    private Preferences preferences;

    public ProductsDataSyncService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        zonesDatabase = ZonesTableUtil.getInstance(getApplicationContext());
        contactsDatabase = ContactsTableUtil.getInstance(getApplicationContext());
        productsDatabase = ProductsTableUtil.getInstance(getApplicationContext());
        preferences = FarmConnectAppPreferences.getInstance(getApplicationContext());

        authenticatedPhoneNumber = preferences.getString("authenticatedPhoneNumber");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       boolean productsIdsLoaded = getExistingProductRemoteIDs(productsDatabase.getProductRemoteIds());
        if (productsIdsLoaded) {
            startForeground(NOTIFICATION_ID, createNotification());
            startMonitoring();
        }
        return START_STICKY;
    }


    public boolean getExistingProductRemoteIDs(List<String> productRemoteIDs) {
        syncedData.clear(); // Clear the existing data in the HashSet
        syncedData.addAll(productRemoteIDs); // Add all product remote IDs to the HashSet
        return true;
    }

    private Notification createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel for Android Oreo and above
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW); // Use LOW importance to prevent sound
            channel.setSound(null, null); // Set sound to null
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification for the foreground service
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FarmConnect")
                .setContentText("Syncing data...")
                .setSmallIcon(R.drawable.farmconnectlogo)
                .setContentIntent(createNotificationIntent())
                .setAutoCancel(true);

        return notificationBuilder.build();

    }

    private PendingIntent createNotificationIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private void startMonitoring(){
        runnable = new Runnable() {
            @Override
            public void run() {
                    Log.d("FarmConnect", "run: DataSync Service is running");
                    if (getSizeOfSyncedData() == 0){
                        if(preferences.isFarmerAccount()){
                            getAllProductsForTheFarmer(authenticatedPhoneNumber);
                        }else {
                            getAllProductsFromDatabase();
                        }

                    } else if(getSizeOfSyncedData() > 0){
                        if (preferences.isBuyerAccount()){
                            getNewProductsFromFirebaseDatabase();
                            updateProductDetails();
                            removeObsoleteProductsFromSyncedData();
                        }

                        if (preferences.isFarmerAccount()){
                            if (productsSyncListener != null){
                                productsSyncListener.onProductsSyncComplete();
                            }
                            stopSelf();
                        }

                    }


            }
        };
        handler.postDelayed(runnable, POLL_INTERVAL);
    }

    private void getAllProductsFromDatabase(){
        List<String> registeredContactList = contactsDatabase.getAllRegisteredContacts();
        for (String phoneNumber : registeredContactList){
            List<String> zoneIDs = zonesDatabase.getZoneIds(phoneNumber);

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

                            productsDatabase.addProduct(productDetails);
                            Log.d("FarmConnect", "onDataChange: Product id " + productDetails.get(0) + " Added to database");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        }
        if (productsSyncListener != null){
            productsSyncListener.onProductsSyncComplete();
        }

        stopSelf();

    }

    private void getAllProductsForTheFarmer(String phoneNumber) {
        List<String> registeredContactList = contactsDatabase.getAllRegisteredContacts();
        for (String registeredContact : registeredContactList) {
            List<String> zoneIDs = zonesDatabase.getZoneIds(registeredContact);
            for (String zoneID : zoneIDs) {
                Log.d("FarmConnect", "getProductsFromDatabase: " + zoneID);
                productsReference = FirebaseDatabase.getInstance().getReference().child("zones").child(registeredContact).child(zoneID).child("products");

                productsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            String owner = productSnapshot.child("owner").getValue(String.class);
                            Log.d("FarmConnect", "onDataChange: " + phoneNumber);
                            if (owner != null && owner.equals(phoneNumber)) {
                                List<String> productDetails = new ArrayList<>();

                                productDetails.add(productSnapshot.child("productID").getValue(String.class));
                                productDetails.add(productSnapshot.child("productName").getValue(String.class));
                                productDetails.add(productSnapshot.child("quantity").getValue(String.class));
                                productDetails.add(productSnapshot.child("unitPrice").getValue(String.class));
                                productDetails.add(productSnapshot.child("price").getValue(String.class));
                                productDetails.add(productSnapshot.child("imageUrl").getValue(String.class));
                                productDetails.add("true");
                                productDetails.add("false");
                                productDetails.add(owner);
                                productDetails.add(productSnapshot.child("createDate").getValue(String.class));
                                productDetails.add(productSnapshot.child("createTime").getValue(String.class));
                                productDetails.add(productSnapshot.child("status").getValue(String.class));
                                productDetails.add(productSnapshot.child("zoneID").getValue(String.class));

                                productsDatabase.addProduct(productDetails);
                                Log.d("FarmConnect", "onDataChange: Product id " + productDetails.get(0) + " Added to database");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        }

        if (productsSyncListener != null){
            productsSyncListener.onProductsSyncComplete();
        }

        stopSelf();
    }

    private boolean getNewProductsFromFirebaseDatabase(){
            List<String> registeredContactList = contactsDatabase.getAllRegisteredContacts();
            for (String phoneNumber : registeredContactList) {
                List<String> zoneIDs = zonesDatabase.getZoneIds(phoneNumber);

                for (String zoneID : zoneIDs) {
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

                                    boolean productAdded = productsDatabase.addProduct(productDetails);
                                    if (productAdded){
                                        syncedData.add(productRemoteId); // Add the productRemoteId to the HashSet
                                        Log.d("FarmConnect", "onDataChange: New product with  Product id " + productRemoteId + " added to database");
                                    }

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
            return true;

    }

    private boolean updateProductDetails() {
        List<String> registeredContactList = contactsDatabase.getAllRegisteredContacts();
        for (String phoneNumber : registeredContactList) {
            List<String> zoneIDs = zonesDatabase.getZoneIds(phoneNumber);

            for (String zoneID : zoneIDs) {
                productsReference = FirebaseDatabase.getInstance().getReference().child("zones").child(phoneNumber).child(zoneID).child("products");

                productsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            String productRemoteId = productSnapshot.child("productID").getValue(String.class);

                            // Get the product details from the database
                            Product product = productsDatabase.getProductDetails(productRemoteId);

                            // Check if the product exists in the database
                            if (product != null) {
                                String remoteImageUrl = productSnapshot.child("imageUrl").getValue(String.class);
                                String remoteProductName = productSnapshot.child("productName").getValue(String.class);
                                String remoteQuantity = productSnapshot.child("quantity").getValue(String.class);
                                String remoteUnitPrice = productSnapshot.child("unitPrice").getValue(String.class);
                                String remotePrice = productSnapshot.child("price").getValue(String.class);


                                String localImageUrl = product.getImageUrl();
                                String localProductName = product.getProductName();
                                String localQuantity = product.getQuantity();
                                String localUnitPrice = product.getUnitPrice();
                                String localPrice = product.getPrice();

                                // Compare the fields with Firebase data
                                if (!localProductName.equals(remoteProductName)
                                        || !localUnitPrice.equals(remoteUnitPrice)
                                        || !localPrice.equals(remotePrice)
                                        || !localQuantity.equals(remoteQuantity)
                                        || !localImageUrl.equals(remoteImageUrl)) {

                                    // Update the record in the SQLite database
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("imageUrl", remoteImageUrl);
                                    contentValues.put("productName", remoteProductName);
                                    contentValues.put("quantity", remoteQuantity);
                                    contentValues.put("unitPrice", remoteUnitPrice);
                                    contentValues.put("price", remotePrice);

                                    productsDatabase.updateProduct(productRemoteId, contentValues);
                                    Log.d("FarmConnect", "onDataChange: Product id " + productRemoteId + " updated");
                                }
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
        return true;
    }

    private void removeObsoleteProductsFromSyncedData() {
        List<String> registeredContactList = contactsDatabase.getAllRegisteredContacts();
        for (String phoneNumber : registeredContactList) {
            List<String> zoneIDs = zonesDatabase.getZoneIds(phoneNumber);
            for (String zoneID : zoneIDs) {
                productsReference = FirebaseDatabase.getInstance().getReference().child("zones").child(phoneNumber).child(zoneID).child("products");
                productsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            removeFromSyncedData(productSnapshot.child("productID").getValue(String.class));
//                            removeIdFromSyncedData(productSnapshot.getKey());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                    // Rest of the ValueEventListener implementation...
                });
            }
        }

        Set<String> obsoleteProductIds = new HashSet<>(syncedData);

        // Delete obsolete products from SQLite database
        for (String obsoleteProductId : obsoleteProductIds) {
//            productsDatabaseHelper.deleteProductFromDatabase(obsoleteProductId);
            Log.d("FarmConnect", "removeObsoleteProductsFromSyncedData: Product id " + obsoleteProductId + " deleted from database");
        }

        if (productsSyncListener != null){
            productsSyncListener.onProductsSyncComplete();
        }

        stopSelf();

    }

    public void removeIdFromSyncedData(String productId){
        syncedData.remove(productId);

    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }



   private boolean isDataSynced(String dataId){
        return syncedData.contains(dataId);
   }

//    private void addToSyncedData(String dataId){
//        newProductIdsSet.add(dataId);
//    }

    private void removeFromSyncedData(String dataId){
        syncedData.remove(dataId);
    }

    private int getSizeOfSyncedData(){
        return syncedData.size();
    }

    public class ProductsSyncServiceBinder extends Binder {
        public ProductsDataSyncService getProductsSyncServiceBinder(){
            return ProductsDataSyncService.this;
        }
    }

    public void setProductsSyncListener(ProductsSyncListener productsSyncListener) {
        this.productsSyncListener = productsSyncListener;
    }

    public interface ProductsSyncListener{
        void onProductsSyncComplete();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}