package com.moels.farmconnect.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moels.farmconnect.R;
import com.moels.farmconnect.adapters.ContactListRecyclerViewAdapter;
import com.moels.farmconnect.models.ContactCardItem;
import com.moels.farmconnect.utility_classes.ContactsDatabaseHelper;
import com.moels.farmconnect.utility_classes.UI;

import java.util.ArrayList;
import java.util.List;

public class SelectContactActivity extends AppCompatActivity {
    ProgressBar progressBar;
    private static final int REQUEST_PERMISSION_CODE = 100;
    private Toolbar callActivityToolBar;
    private RecyclerView contactListRecyclerView;
    private ContactListRecyclerViewAdapter contactListRecyclerViewAdapter;
    private List<ContactCardItem> contactsList;
    private Button createNewContactButton;
    private ContactsDatabaseHelper contactsDatabaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    SharedPreferences myAppPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        initUI();
        setSupportActionBar(callActivityToolBar);
        UI.setUpActionBar(getSupportActionBar(), R.drawable.ic_back_arrow, "Select Contact", true);

        contactsDatabaseHelper = new ContactsDatabaseHelper(getApplicationContext());
        sqLiteDatabase  = contactsDatabaseHelper.getWritableDatabase();

        if (checkRequiredPermissions() == false){
            requestPermissions();
            return;
        }
        myAppPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        editor = myAppPreferences.edit();
        boolean contactListFetched = myAppPreferences.getBoolean("contactListFetched", false);


        if (contactListFetched == false){
            UI.show(progressBar);
            queryFirebaseForMatchingPhoneNumbers(getAllContactsOnPhone());
            editor.putBoolean("contactListFetched", true);
            editor.apply();
        } else {
            getContactsFromDatabase();
            UI.hide(progressBar);
        }


        createNewContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_contacts_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh_contacts_btn){
            UI.show(progressBar);
            queryFirebaseForMatchingPhoneNumbers(getAllContactsOnPhone());
        }
        return super.onOptionsItemSelected(item);
    }

    private void initUI(){
        callActivityToolBar = findViewById(R.id.start_new_call_toolbar);
        contactListRecyclerView = findViewById(R.id.contacts_recycler_view);
        createNewContactButton = findViewById(R.id.create_new_contact_btn);
        progressBar = findViewById(R.id.progress_bar);
    }

    private boolean checkRequiredPermissions(){
        boolean checkRequiredPermissions = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                checkRequiredPermissions = false;
            }
        }
        return checkRequiredPermissions;
    };

    private void requestPermissions(){
        ActivityCompat.requestPermissions(SelectContactActivity.this, new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        }, REQUEST_PERMISSION_CODE);
    }

    private void queryFirebaseForMatchingPhoneNumbers(List<String> phoneBookList) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("profiles");
        Query query = usersRef.orderByChild("phoneNumber");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (String phoneNumber : phoneBookList) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = dataSnapshot.getKey();
                        String firebasePhoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                        String firebaseUsername = snapshot.child("name").getValue(String.class);

                        if (phoneNumber.equals(firebasePhoneNumber)) {
                            Log.d("Firebase Phone Number", firebasePhoneNumber);
                            insertContactToDatabase(firebaseUsername, firebasePhoneNumber);
                            break; // move on to next phone number in phoneBookList
                        }
                        else Log.d("Firebase", "No Match Of Contacts Found");
                    }

                }
                UI.hide(progressBar);
                getContactsFromDatabase();
                UI.displayToast(getApplicationContext(), "Contact List Updated");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                UI.displayToast(getApplicationContext(), databaseError.getMessage());
                UI.hide(progressBar);
                editor.putBoolean("contactListFetched", false);
                editor.apply();
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });
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
    private void getContactsFromDatabase(){
        ContactCardItem contactCardItem;
        contactsList = new ArrayList<>();
        String [] columnsToPick = {"_id","username", "phoneNumber"};
        Cursor cursor = sqLiteDatabase.query("contacts", columnsToPick,
                null, null, null, null, null);

        if (cursor.moveToFirst()){
            do {
                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("username"));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
                if (!(TextUtils.isEmpty(username) || TextUtils.isEmpty(phoneNumber))){
                    Log.d("FarmConnect", username + " " + phoneNumber);
                    contactCardItem = new ContactCardItem();
                    contactCardItem.setCardItem(username, phoneNumber);
                    contactsList.add(contactCardItem);
                }

            } while (cursor.moveToNext());
        }
            else {
            Log.d("FarmConnect", "No Contacts to pick");
        }
            contactListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            contactListRecyclerViewAdapter = new ContactListRecyclerViewAdapter(contactsList, getApplicationContext());
            contactListRecyclerView.setAdapter(contactListRecyclerViewAdapter);
            contactListRecyclerViewAdapter.notifyDataSetChanged();

    }

    @SuppressLint("Range")
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
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
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
}