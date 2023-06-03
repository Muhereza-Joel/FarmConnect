package com.moels.farmconnect.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.moels.farmconnect.services.FetchContactsService;
import com.moels.farmconnect.utility_classes.ContactsDatabaseHelper;
import com.moels.farmconnect.utility_classes.UI;

import java.util.ArrayList;
import java.util.List;

public class SelectContactActivity extends AppCompatActivity implements FetchContactsService.ContactsFetchListener{
    private FetchContactsService fetchContactsService;
    private boolean bound = false;
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

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            FetchContactsService.FetchContactsServiceBinder fetchContactsServiceBinder = (FetchContactsService.FetchContactsServiceBinder) binder;
            fetchContactsService = fetchContactsServiceBinder.getFetchContactsService();
            fetchContactsService.setContactsFetchListener(SelectContactActivity.this);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        initUI();
        setUpStatusBar();
        setSupportActionBar(callActivityToolBar);
        UI.setUpToolbarInDarkMode(getApplicationContext(), callActivityToolBar);
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


        if (contactListFetched == true){
            getContactsFromDatabase();
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
            Intent intent = new Intent(this, FetchContactsService.class);
            startService(intent);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
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


    @Override
    public void onContactsFetchComplete() {
        if (bound) {
            unbindService(serviceConnection);
            bound = false;
        }
        UI.hide(progressBar);
        getContactsFromDatabase();
        UI.displayToast(getApplicationContext(), "Contact List Updated");
        stopService(new Intent(SelectContactActivity.this, FetchContactsService.class));
    }

    private void setUpStatusBar() {
        Window window = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
            int currentMode = uiModeManager.getNightMode();
            if (currentMode == UiModeManager.MODE_NIGHT_YES) {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));
            }else {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            }
        }

    }
}