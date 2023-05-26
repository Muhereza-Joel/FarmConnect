package com.moels.farmconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.moels.farmconnect.R;
import com.moels.farmconnect.services.FetchContactsService;
import com.moels.farmconnect.utility_classes.UI;

public class FinishSetUpActivity extends AppCompatActivity implements FetchContactsService.ContactsFetchListener{
    private FetchContactsService fetchContactsService;
    private boolean bound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
                FetchContactsService.FetchContactsServiceBinder fetchContactsServiceBinder = (FetchContactsService.FetchContactsServiceBinder) binder;
                fetchContactsService = fetchContactsServiceBinder.getFetchContactsService();
                fetchContactsService.setContactsFetchListener(FinishSetUpActivity.this);
                bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
                bound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, FetchContactsService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_set_up);
        setUpStatusBar();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (bound){
            unbindService(serviceConnection);
            bound = false;
        }
    }

    @Override
    public void onContactsFetchComplete() {
        Intent intent = new Intent(this, FinalizeSetupOfZonesActivity.class);
        startActivity(intent);
        finish();

        if (bound) {
            unbindService(serviceConnection);
            bound = false;
        }

        stopService(new Intent(FinishSetUpActivity.this, FetchContactsService.class));
    }

    private void setUpStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }
}