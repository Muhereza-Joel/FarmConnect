package com.moels.farmconnect.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moels.farmconnect.dialogs.ProgressDialog;
import com.moels.farmconnect.utility_classes.ProductsDatabaseHelper;

public class DeleteProductService extends Service {

    private static final int POLL_INTERVAL = 1000;
    private Handler handler;
    private Runnable runnable;

    public DeleteProductService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("imageUrl");
        deleteImageAtUrl(url);
        return START_STICKY;
    }

    private void deleteImageAtUrl(String url){
        runnable = new Runnable() {
            @Override
            public void run() {
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference = firebaseStorage.getReferenceFromUrl(url);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FarmConnect", "image deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.getLocalizedMessage();
                    }
                });
            }
        };
        handler.postDelayed(runnable, POLL_INTERVAL);
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