package com.moels.farmconnect.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moels.farmconnect.activities.CreateProfileActivity;
import com.moels.farmconnect.activities.FinishSetUpActivity;
import com.moels.farmconnect.activities.MainActivity;
import com.moels.farmconnect.dialogs.ProgressDialog;
import com.moels.farmconnect.utility_classes.ContactsDatabaseHelper;
import com.moels.farmconnect.utility_classes.UI;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsService extends Service {

    private SharedPreferences myAppPreferences;
    private SharedPreferences.Editor editor;
    private static final int POLL_INTERVAL = 1000;
    private Handler handler;
    private Runnable runnable;
    private SQLiteDatabase sqLiteDatabase;
    private ContactsDatabaseHelper contactsDatabaseHelper;
    private final IBinder binder = new FetchContactsServiceBinder();
    private ContactsFetchListener contactsFetchListener;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        contactsDatabaseHelper = new ContactsDatabaseHelper(getApplicationContext());
        sqLiteDatabase  = contactsDatabaseHelper.getWritableDatabase();
        myAppPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        editor = myAppPreferences.edit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            queryFirebaseForMatchingPhoneNumbers(getAllContactsOnPhone());
            editor.putBoolean("contactListFetched", true);
            editor.apply();

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class FetchContactsServiceBinder extends Binder {
        public FetchContactsService getFetchContactsService(){
            return FetchContactsService.this;
        }
    }

    public void setContactsFetchListener(ContactsFetchListener contactsFetchListener){
        this.contactsFetchListener = contactsFetchListener;
    }

    public interface ContactsFetchListener{
        public void onContactsFetchComplete();
    }

    private void queryFirebaseForMatchingPhoneNumbers(List<String> phoneBookList) {
        runnable = new Runnable() {
                        @Override
            public void run() {
         if (phoneBookList.size() > 0){
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("profiles");
        Query query = usersRef.orderByChild("phoneNumber");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (String phoneNumber : phoneBookList) {
                    boolean isMatchFound = false; // Flag to track if a match is found for a phone number

                    // Clean phone number by removing non-digit characters
                    String cleanedPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        String firebasePhoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                        String cleanedFirebasePhoneNumber = firebasePhoneNumber.replaceAll("[^0-9]", "");

                        // Compare phone numbers without country codes
                        if (cleanedPhoneNumber.endsWith(cleanedFirebasePhoneNumber)
                                || cleanedFirebasePhoneNumber.endsWith(cleanedPhoneNumber)) {
                            Log.d("Firebase Phone Number", firebasePhoneNumber);
                            insertContactToDatabase(snapshot.child("name").getValue(String.class), firebasePhoneNumber);
                            isMatchFound = true; // Set the flag to true if a match is found
                            break; // Exit the inner loop
                        }
                    }

                    if (!isMatchFound) {
                        Log.d("Firebase", "No Match Of Contacts Found");
                    }
                }
                contactsFetchListener.onContactsFetchComplete();
                stopSelf();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                editor.putBoolean("contactListFetched", false);
                        editor.apply();
                        Log.e("Firebase", "Error: " + error.getMessage());
                        stopSelf();
            }

            // Other overridden methods of ValueEventListener

        });
         } else {
             contactsFetchListener.onContactsFetchComplete();
             stopSelf();
         }}
        };
        handler.postDelayed(runnable, POLL_INTERVAL);
    }


    private List<String> getAllContactsOnPhone(){
        List<String> listOfContactsOnPhone = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                listOfContactsOnPhone.add(phoneNumber);
                Log.d("Phone Number", phoneNumber);
            }
            cursor.close();
            // Use the phone numbers retrieved from the ContentProvider
        } else {
            // Handle the case where the cursor is null
        }

        return listOfContactsOnPhone;
    }

    private void insertContactToDatabase(String username, String phoneNumber){
        String query = "SELECT * FROM contacts WHERE phoneNumber = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{phoneNumber});
        if (cursor.getCount() > 0){
            Log.d("FarmConnect", "Phone Number Exists In Database Already");
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("username", username);
            contentValues.put("phoneNumber", phoneNumber);
            sqLiteDatabase.insert("contacts", null, contentValues);
            Log.d("FarmConnect", "Contact Inserted");
        }
    }




}