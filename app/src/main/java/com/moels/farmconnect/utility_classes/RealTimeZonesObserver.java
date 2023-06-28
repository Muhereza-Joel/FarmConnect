package com.moels.farmconnect.utility_classes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moels.farmconnect.models.Zone;

import java.util.ArrayList;
import java.util.List;

public class RealTimeZonesObserver implements ZonesObserver{
    private List<DatabaseReference> zoneReferences;
    private ChildEventListener zonesChildEventListener;

    private RealTimeZonesObserver(List<String> phoneNumbers){
        zoneReferences = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("zones");
        for (String phoneNumber : phoneNumbers){
            DatabaseReference phoneNumberRef = databaseReference.child(phoneNumber);
            zoneReferences.add(phoneNumberRef);
        }
    }

    public static RealTimeZonesObserver getInstance(List<String> phoneNumbers){
        return new RealTimeZonesObserver(phoneNumbers);
    }

    public void startListening(final OnZoneUpdateListener listener){
        zonesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Zone zone = snapshot.getValue(Zone.class);
                listener.onZoneAdded(zone);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Zone zone = snapshot.getValue(Zone.class);
                listener.onZoneChanged(zone);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String zoneID = snapshot.getKey();
                listener.onZoneRemoved(zoneID);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.getMessage());
            }
        };

        for (DatabaseReference reference : zoneReferences) {
            reference.addChildEventListener(zonesChildEventListener);
        }
    }

    @Override
    public void stopListening(){
        if (zonesChildEventListener != null){
            for (DatabaseReference reference : zoneReferences) {
                reference.removeEventListener(zonesChildEventListener);
            }
        }
    }

    public interface OnZoneUpdateListener{
        void onZoneAdded(Zone zone);
        void onZoneChanged(Zone zone);
        void onZoneRemoved(String zoneID);
        void onError(String errorMessage);
    }
}
