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
import java.util.List;

public class BuyerAccountZoneFetchService extends Service {
    private static final int POLL_INTERVAL = 2000;
    private Handler handler;
    private Runnable runnable;
    private SQLiteDatabase zonesDatabase;
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
        zonesDatabase = zonesDatabaseHelper.getWritableDatabase();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getZonesFromFirebase();
        return START_STICKY;
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
                    Zone zone = zoneSnapshot.getValue(Zone.class);
                    zoneList.add(zone);
                }
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
                String id = zone.getZoneID();
                String zoneName = zone.getZoneName();
                String location = zone.getZoneLocation();
                String productsToCollect = zone.getProductsToCollect();
                String description = zone.getDescription();
                String owner = zone.getOwner();
                String createDate = zone.getDate();
                String createTime = zone.getTime();
                String status = zone.getStatus();
                String products = zone.getProducts();

                addZoneToDatabase(id, zoneName, location, productsToCollect, description, owner, createDate, createTime, status);

            }
        }
        zonesFetchListener.onBuyerZonesFetchComplete();
        stopSelf();
    }

    private boolean addZoneToDatabase(String remote_id, String zoneName, String location, String productsToCollect, String description, String owner, String date, String time, String status){
        String query = "SELECT * FROM zones WHERE remote_id = ? AND owner = ?";
        Cursor cursor = zonesDatabase.rawQuery(query, new String[]{remote_id, owner});
        if (cursor.getCount() > 0){
            Log.d("FarmConnect", "Zone " + remote_id + " Exists In Database Already");
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("remote_id", remote_id);
            contentValues.put("zoneName", zoneName);
            contentValues.put("location", location);
            contentValues.put("products", productsToCollect);
            contentValues.put("description", description);
            contentValues.put("uploaded", "true");
            contentValues.put("owner", owner);
            contentValues.put("createDate", date);
            contentValues.put("createTime", time);
            contentValues.put("status", status);

            zonesDatabase.insert("zones", null, contentValues);
            Log.d("FarmConnect", "Contact Added To Database");

        }

        return true;
    }

}