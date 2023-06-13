package com.moels.farmconnect.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moels.farmconnect.R;
import com.moels.farmconnect.adapters.ContactListRecyclerViewAdapter;
import com.moels.farmconnect.models.ContactCardItem;
import com.moels.farmconnect.services.BuyerAccountZoneFetchService;
import com.moels.farmconnect.services.FarmerAccountZonesFetchService;
import com.moels.farmconnect.services.FetchContactsService;
import com.moels.farmconnect.utility_classes.ContactsDatabaseHelper;
import com.moels.farmconnect.utility_classes.UI;

import java.util.ArrayList;
import java.util.List;

public class SelectContactActivity extends AppCompatActivity implements FetchContactsService.ContactsFetchListener, BuyerAccountZoneFetchService.BuyerZonesFetchListener, FarmerAccountZonesFetchService.FarmerZonesFetchListener {
    private FetchContactsService fetchContactsService;
    private BuyerAccountZoneFetchService buyerAccountZoneFetchService;
    private FarmerAccountZonesFetchService farmerAccountZonesFetchService;
    private boolean bound = false;
    private ProgressBar progressBar;
    private static final int REQUEST_PERMISSION_CODE = 100;
    private Toolbar callActivityToolBar;
    private RecyclerView contactListRecyclerView;
    private ContactListRecyclerViewAdapter contactListRecyclerViewAdapter;
    private List<ContactCardItem> contactsList;
    private Button createNewContactButton;
    private ContactsDatabaseHelper contactsDatabaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private SharedPreferences myAppPreferences;
    private TextView emptyContactListTextView;
    private boolean isFarmerAccount;
    private boolean isBuyerAccount;

    private ServiceConnection farmerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            FarmerAccountZonesFetchService.FarmerZonesFetchServiceBinder zonesFetchServiceBinder = (FarmerAccountZonesFetchService.FarmerZonesFetchServiceBinder) binder;
            farmerAccountZonesFetchService = zonesFetchServiceBinder.getFarmerAccountZonesFetchService();
            farmerAccountZonesFetchService.setZonesFetchListener(SelectContactActivity.this);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    private ServiceConnection buyerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder2) {
            BuyerAccountZoneFetchService.BuyerZonesFetchServiceBinder zonesFetchServiceBinder = (BuyerAccountZoneFetchService.BuyerZonesFetchServiceBinder) binder2;
            buyerAccountZoneFetchService = zonesFetchServiceBinder.getBuyerAccountZonesFetchService();
            buyerAccountZoneFetchService.setZonesFetchListener(SelectContactActivity.this);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    private final ServiceConnection serviceConnection = new ServiceConnection() {
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
        isBuyerAccount = myAppPreferences.getBoolean("buyerAccountTypeChosen", false);
        isFarmerAccount = myAppPreferences.getBoolean("farmerAccountTypeChosen", false);
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
        emptyContactListTextView = findViewById(R.id.empty_contacts_list_text_view);
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
        String [] columnsToPick = {"_id","username", "phoneNumber", "imageUrl","accountType"};
        Cursor cursor = sqLiteDatabase.query("contacts", columnsToPick,
                null, null, null, null, null);

        if (cursor.moveToFirst()){
            do {
                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("username"));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
                @SuppressLint("Range") String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                @SuppressLint("Range") String accountType = cursor.getString(cursor.getColumnIndex("accountType"));

                String accountBudge = "";

                if (accountType.equals("Buyer account")){
                    accountBudge = "Buyer";
                } else {
                    accountBudge = "Seller";
                }


                if (!(TextUtils.isEmpty(username) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(imageUrl) || TextUtils.isEmpty(accountType))){
                    contactCardItem = new ContactCardItem();
                    contactCardItem.setCardItem(username,imageUrl, phoneNumber, accountBudge);
                    contactsList.add(contactCardItem);
                }

            } while (cursor.moveToNext());
        }
            else {
            emptyContactListTextView.setVisibility(View.VISIBLE);
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

        if (isBuyerAccount){
            Intent intent = new Intent(this, BuyerAccountZoneFetchService.class);
            startService(intent);
            bindService(intent, buyerServiceConnection, Context.BIND_AUTO_CREATE);
        }

        if (isFarmerAccount){
            Intent serviceIntent = new Intent(getApplicationContext(), FarmerAccountZonesFetchService.class);
            startService(serviceIntent);
            bindService(serviceIntent, farmerServiceConnection, Context.BIND_AUTO_CREATE);
        }


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

    @Override
    public void onBuyerZonesFetchComplete() {
        if (bound) {
            unbindService(buyerServiceConnection);
            bound = false;
            UI.displayToast(getApplicationContext(), "Collection Zones Updated");
            stopService(new Intent(SelectContactActivity.this, BuyerAccountZoneFetchService.class));
        }

    }

    @Override
    public void onFarmerZonesFetchComplete() {
        if (bound) {
            unbindService(farmerServiceConnection);
            bound = false;
            UI.displayToast(getApplicationContext(), "Collection Zones Updated");
            stopService(new Intent(SelectContactActivity.this, FarmerAccountZonesFetchService.class));
        }

    }

    @Override
    public void onFarmerZonesFetchError(String errorMessage) {
        UI.displayToast(getApplicationContext(), "Error " + errorMessage);
    }
}