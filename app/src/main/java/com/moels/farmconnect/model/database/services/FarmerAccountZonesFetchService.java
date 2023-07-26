package com.moels.farmconnect.model.database.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moels.farmconnect.model.database.ContactsDatabase;
import com.moels.farmconnect.model.database.ZonesDatabase;
import com.moels.farmconnect.model.database.ZonesDatabaseHelper;
import com.moels.farmconnect.utils.models.Zone;
import com.moels.farmconnect.model.database.ContactsDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class FarmerAccountZonesFetchService extends Service {
    private static final int POLL_INTERVAL = 2000;
    private Handler handler;
    private Runnable runnable;
    private FarmerZonesFetchListener zonesFetchListener;
    private final IBinder binder = new FarmerZonesFetchServiceBinder();
    private ContactsDatabase contactsDatabase;
    private ZonesDatabase zonesDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        contactsDatabase = ContactsDatabaseHelper.getInstance(getApplicationContext());
        zonesDatabase = ZonesDatabaseHelper.getInstance(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getZonesFromFirebase();
        return START_STICKY;
    }

    private void getZonesFromFirebase(){
        runnable = new Runnable() {
            @Override
            public void run() {
                retrieveZonesByPhoneNumbers(contactsDatabase.getAllRegisteredContacts());
            }
        };
        handler.postDelayed(runnable, POLL_INTERVAL);
    }

    private void retrieveZonesByPhoneNumbers(List<String> phoneNumbers) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        if (phoneNumbers.size() > 0){
            for (String phoneNumber : phoneNumbers) {
                Query query = databaseReference.child("zones").child(phoneNumber);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Zone> zoneList = new ArrayList<>();

                        for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                            // Exclude the retrieval of the products field
                            Zone zone = zoneSnapshot.getValue(Zone.class);
                            zoneList.add(zone);
                        }

                        // Process the zone list without the products field
                        getDetailsForEveryZone(zoneList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (zonesFetchListener != null){
                            zonesFetchListener.onFarmerZonesFetchError(databaseError.getMessage());
                        }

                    }
                });
            }

        }else {
            if(zonesFetchListener != null) {
                zonesFetchListener.onFarmerZonesFetchComplete();
            }
            stopSelf();
            Log.d("FarmConnect", "No Contacts to pick");
        }         

    }

    private void getDetailsForEveryZone(List<Zone> zoneList) {
        for (Zone zone : zoneList) {
            if (zone != null){
                String uploaded = "true";
                String updated = "false";
                List<String> zoneDetails = new ArrayList<>();

                zoneDetails.add(zone.getZoneID());
                zoneDetails.add(zone.getZoneName());
                zoneDetails.add(zone.getZoneLocation());
                zoneDetails.add(zone.getProductsToCollect());
                zoneDetails.add(zone.getDescription());
                zoneDetails.add(uploaded);
                zoneDetails.add(zone.getOwner());
                zoneDetails.add(zone.getDate());
                zoneDetails.add(zone.getTime());
                zoneDetails.add(zone.getStatus());
                zoneDetails.add(updated);

                zonesDatabase.addZoneToDatabase(zoneDetails);
            }
        }
        if (zonesFetchListener != null){
            zonesFetchListener.onFarmerZonesFetchComplete();
        }
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class FarmerZonesFetchServiceBinder extends Binder {
        public FarmerAccountZonesFetchService getFarmerAccountZonesFetchService(){
            return FarmerAccountZonesFetchService.this;
        }

    }

    public void setZonesFetchListener(FarmerZonesFetchListener zonesFetchListener){
        this.zonesFetchListener = zonesFetchListener;
    }

    public interface FarmerZonesFetchListener {
        void onFarmerZonesFetchComplete();
        void onFarmerZonesFetchError(String errorMessage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);

    }

}