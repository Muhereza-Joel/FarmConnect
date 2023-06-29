package com.moels.farmconnect.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.moels.farmconnect.utility_classes.ContactsDatabase;
import com.moels.farmconnect.utility_classes.ContactsDatabaseHelper;
import com.moels.farmconnect.utility_classes.FarmConnectAppPreferences;
import com.moels.farmconnect.utility_classes.Preferences;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FetchContactsService extends Service {

    private Preferences preferences;
    private static final int POLL_INTERVAL = 1000;
    private Handler handler;
    private Runnable runnable;
    private ContactsDatabase contactsDatabase;
    private final IBinder binder = new FetchContactsServiceBinder();
    private ContactsFetchListener contactsFetchListener;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        contactsDatabase = ContactsDatabaseHelper.getInstance(getApplicationContext());
        preferences = FarmConnectAppPreferences.getInstance(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            queryFirebaseForMatchingPhoneNumbers(getAllContactsOnPhone());
            preferences.putBoolean("contactListFetched", true);

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
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("userAccounts");
        Query query = usersRef.orderByChild("phoneNumber");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String countryCode = "+256"; // Assuming the default country code is +1 (you can change it as per your requirement)
                LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
                for (String phoneNumber : phoneBookList) {
                    boolean isMatchFound = false; // Flag to track if a match is found for a phone number

                    String cleanedPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        String name = snapshot.child("name").getValue(String.class);
                        String firebasePhoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                        String imageUrl = snapshot.child("profilePicUrl").getValue(String.class);
                        String accountType = snapshot.child("accountType").getValue(String.class);
                        String uploaded = "true";
                        String updated = "false";

                        String cleanedFirebasePhoneNumber = firebasePhoneNumber.replaceAll("[^0-9]", "");
                        // Calculate the Levenshtein distance between the phone numbers
                        int distance = levenshteinDistance.apply(cleanedPhoneNumber, cleanedFirebasePhoneNumber);

                        // Adjust the threshold as needed based on the tolerance for matching
                        int threshold = 5;

                        if (distance <= threshold) {
                            Log.d("Firebase Phone Number", firebasePhoneNumber);

                            List<String> contactDetails = new ArrayList<>();
                            contactDetails.add(name);
                            contactDetails.add(firebasePhoneNumber);
                            contactDetails.add(imageUrl);
                            contactDetails.add(accountType);
                            contactDetails.add(uploaded);
                            contactDetails.add(updated);
                            contactsDatabase.addContactToDatabase(contactDetails);
                            Log.d("Farmconnect", "onDataChange: " + firebasePhoneNumber);
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
                preferences.putBoolean("contactListFetched", false);
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
//                Log.d("Phone Number", phoneNumber);
            }
            cursor.close();
            // Use the phone numbers retrieved from the ContentProvider
        } else {
            // Handle the case where the cursor is null
        }

        return listOfContactsOnPhone;
    }

}