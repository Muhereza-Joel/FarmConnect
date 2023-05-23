package com.moels.farmconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

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
        Intent intent = new Intent(FinishSetUpActivity.this, MainActivity.class);
        startActivity(intent);
        if (bound) {
            unbindService(serviceConnection);
            bound = false;
        }
        finish();
        stopService(new Intent(FinishSetUpActivity.this, FetchContactsService.class));
    }
}