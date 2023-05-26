package com.moels.farmconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import android.view.WindowManager;

import com.moels.farmconnect.R;
import com.moels.farmconnect.services.FarmerAccountZonesFetchService;
import com.moels.farmconnect.services.FetchContactsService;

public class FinalizeSetupOfZonesActivity extends AppCompatActivity implements FarmerAccountZonesFetchService.ZonesFetchListener {
    private FarmerAccountZonesFetchService farmerAccountZonesFetchService;
    private boolean bound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            FarmerAccountZonesFetchService.ZonesFetchServiceBinder zonesFetchServiceBinder = (FarmerAccountZonesFetchService.ZonesFetchServiceBinder) binder;
            farmerAccountZonesFetchService = zonesFetchServiceBinder.getFarmerAccountZonesFetchService();
            farmerAccountZonesFetchService.setZonesFetchListener(FinalizeSetupOfZonesActivity.this);
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
        Intent intent = new Intent(this, FarmerAccountZonesFetchService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize_setup_ofzones);
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
    public void onZonesFetchComplete() {
        Intent intent = new Intent(FinalizeSetupOfZonesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

        if (bound){
            unbindService(serviceConnection);
            bound = false;
        }
        stopService(new Intent(FinalizeSetupOfZonesActivity.this, FarmerAccountZonesFetchService.class));
    }

    private void setUpStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }
}