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
import com.moels.farmconnect.database.services.FetchContactsService;

public class FinishSetUpActivity extends AppCompatActivity implements FetchContactsService.ContactsFetchListener{
    private FetchContactsService fetchContactsService;
    private ProgressDialog progressDialog;
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
        setUpUIForDarkMode();
        setUpStatusBar();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Contacts List...");
        progressDialog.setCancelable(false);
        progressDialog.show();
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

        progressDialog.dismiss();
        stopService(new Intent(FinishSetUpActivity.this, FetchContactsService.class));
    }

    private void setUpUIForDarkMode(){
        UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        int currentMode = uiModeManager.getNightMode();
        if (currentMode == UiModeManager.MODE_NIGHT_YES) {
            LinearLayout layout = findViewById(R.id.finish_setup_layout_container);
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