package com.moels.farmconnect.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.moels.farmconnect.R;
import com.moels.farmconnect.model.database.services.BuyerAccountZoneFetchService;
import com.moels.farmconnect.model.database.services.FarmerAccountZonesFetchService;
import com.moels.farmconnect.utils.preferences.FarmConnectAppPreferences;
import com.moels.farmconnect.utils.preferences.Preferences;

public class FinalizeSetupOfZonesActivity extends AppCompatActivity implements FarmerAccountZonesFetchService.FarmerZonesFetchListener, BuyerAccountZoneFetchService.BuyerZonesFetchListener {
    private FarmerAccountZonesFetchService farmerAccountZonesFetchService;
    private BuyerAccountZoneFetchService buyerAccountZoneFetchService;
    private boolean bound = false;
    private ProgressDialog progressDialog;

    private ServiceConnection farmerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            FarmerAccountZonesFetchService.FarmerZonesFetchServiceBinder zonesFetchServiceBinder = (FarmerAccountZonesFetchService.FarmerZonesFetchServiceBinder) binder;
            farmerAccountZonesFetchService = zonesFetchServiceBinder.getFarmerAccountZonesFetchService();
            farmerAccountZonesFetchService.setZonesFetchListener(FinalizeSetupOfZonesActivity.this);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    private ServiceConnection buyerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            BuyerAccountZoneFetchService.BuyerZonesFetchServiceBinder zonesFetchServiceBinder = (BuyerAccountZoneFetchService.BuyerZonesFetchServiceBinder) binder;
            buyerAccountZoneFetchService = zonesFetchServiceBinder.getBuyerAccountZonesFetchService();
            buyerAccountZoneFetchService.setZonesFetchListener(FinalizeSetupOfZonesActivity.this);
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Finding collection zones...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Preferences preferences = FarmConnectAppPreferences.getInstance(getApplicationContext());

        // Start and bind the corresponding service
        if (preferences.isBuyerAccount()) {
            Intent buyerService = new Intent(this, BuyerAccountZoneFetchService.class);
            startService(buyerService);
            bindService(buyerService, buyerServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            Intent farmerService = new Intent(this, FarmerAccountZonesFetchService.class);
            startService(farmerService);
            bindService(farmerService, farmerServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize_setup_ofzones);
        setUpUIForDarkMode();
        setUpStatusBar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound){
            unbindService(farmerServiceConnection);
            unbindService(buyerServiceConnection);
            bound = false;
        }
    }

    @Override
    public void onBuyerZonesFetchComplete() {
        Intent intent = new Intent(FinalizeSetupOfZonesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

        if (bound){
            unbindService(buyerServiceConnection);
            bound = false;
        }
        progressDialog.dismiss();
        stopService(new Intent(FinalizeSetupOfZonesActivity.this, BuyerAccountZoneFetchService.class));
    }


    @Override
    public void onFarmerZonesFetchComplete() {
        Intent intent = new Intent(FinalizeSetupOfZonesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

        if (bound){
            unbindService(farmerServiceConnection);
            bound = false;
        }
        progressDialog.dismiss();
        stopService(new Intent(FinalizeSetupOfZonesActivity.this, FarmerAccountZonesFetchService.class));
    }

    @Override
    public void onFarmerZonesFetchError(String errorMessage) {

    }

    private void setUpUIForDarkMode(){
        UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        int currentMode = uiModeManager.getNightMode();
        if (currentMode == UiModeManager.MODE_NIGHT_YES) {
            LinearLayout layout = findViewById(R.id.finish_setup_of_zones_layout_container);
            layout.setBackgroundColor(getResources().getColor(R.color.colorBlack));
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
}