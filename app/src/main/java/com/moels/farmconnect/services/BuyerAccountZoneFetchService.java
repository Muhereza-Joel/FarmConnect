package com.moels.farmconnect.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.moels.farmconnect.models.Zone;
import com.moels.farmconnect.utility_classes.ContactsDatabaseHelper;
import com.moels.farmconnect.utility_classes.UI;
import com.moels.farmconnect.utility_classes.ZonesDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyerAccountZoneFetchService extends Service {
    private static final int POLL_INTERVAL = 2000;
    private Handler handler;
    private Runnable runnable;
    private ZonesFetchListener zonesFetchListener;
    private final IBinder binder = new ZonesFetchServiceBinder();
    private ContactsDatabaseHelper contactsDatabaseHelper;
    private ZonesDatabaseHelper zonesDatabaseHelper;

    SharedPreferences myAppPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        contactsDatabaseHelper = new ContactsDatabaseHelper(getApplicationContext());
        zonesDatabaseHelper = new ZonesDatabaseHelper(getApplicationContext());
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
                retrieveZoneByPhoneNumber(myAppPreferences.getString("authenticatedPhoneNumber", "123456789"));
            }
        };
        handler.postDelayed(runnable, POLL_INTERVAL);
    }

    private void retrieveZoneByPhoneNumber(String phoneNumber) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
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
                UI.displayToast(getApplicationContext(), "Error retrieving zone data");
            }
        });
    }

    private void getDetailsForEveryZone(List<Zone> zoneList) {
        for (Zone zone : zoneList) {
            if (zone != null){
                String uploaded = "true";
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

                zonesDatabaseHelper.addZoneToDatabase(zoneDetails);
            }
        }
        zonesFetchListener.onBuyerZonesFetchComplete();
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class ZonesFetchServiceBinder extends Binder {
        public BuyerAccountZoneFetchService getBuyerAccountZonesFetchService(){
            return BuyerAccountZoneFetchService.this;
        }

    }

    public void setZonesFetchListener(ZonesFetchListener zonesFetchListener){
        this.zonesFetchListener = zonesFetchListener;
    }

    public interface ZonesFetchListener{
        void onBuyerZonesFetchComplete();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }


}