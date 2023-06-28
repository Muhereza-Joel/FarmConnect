package com.moels.farmconnect.utility_classes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moels.farmconnect.models.Product;

public class RealTimeProductsObserver implements ProductsObserver {
    private DatabaseReference productsReference;
    private ChildEventListener productsChildEventListener;

    private RealTimeProductsObserver(String phoneNumber, String zoneID){
        productsReference = FirebaseDatabase.getInstance().getReference()
                .child("zones")
                .child(phoneNumber)
                .child(zoneID)
                .child("products");

    }

    public static RealTimeProductsObserver getInstance(String phoneNumber, String zoneID){
        return new RealTimeProductsObserver(phoneNumber, zoneID);
    }

    @Override
    public void startListening(final OnProductUpdateListener listener){
        productsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product = snapshot.getValue(Product.class);
                listener.onProductAdded(product);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product = snapshot.getValue(Product.class);
                listener.onProductChanged(product);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String productID = snapshot.getKey();
                listener.onProductRemoved(productID);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.getMessage());
            }
        };

        productsReference.addChildEventListener(productsChildEventListener);

    }

    @Override
    public void stopListening(){
        if (productsChildEventListener != null){
            productsReference.removeEventListener(productsChildEventListener);
        }
    }

    public interface OnProductUpdateListener{
        void onProductAdded(Product product);

        void onProductChanged(Product product);

        void onProductRemoved(String productId);

        void onError(String errorMessage);
    }
}
