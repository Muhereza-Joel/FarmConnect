package com.moels.farmconnect.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.UiModeManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.moels.farmconnect.R;
import com.moels.farmconnect.controller.ZonesController;
import com.moels.farmconnect.utils.UI;
import com.moels.farmconnect.utils.models.Zone;

import java.util.List;

public class ZoneDetailsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView zoneNameEditText, locationEditText, productsEditText, descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_details);
        initUI();
        setUpStatusBar();
        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);

        Drawable icon = toolbar.getOverflowIcon();
        if (icon != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setTint(ContextCompat.getColor(this, R.color.colorWhite));
                toolbar.setOverflowIcon(icon);
            }
        }

        setSupportActionBar(toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Zone Details", true);

        showZoneDetails();

    }

    private void initUI(){
        toolbar = findViewById(R.id.products_in_a_zone_activity_toolbar);
        zoneNameEditText = findViewById(R.id.zone_name_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        productsEditText = findViewById(R.id.products_to_collect_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
    }

    private void showZoneDetails(){
        ZonesController zonesController = ZonesController.getInstance();
        zonesController.setContext(getApplicationContext());
        Zone zone = zonesController.getZoneDetails(getIntent().getStringExtra("zoneID"));
        zoneNameEditText.setText(zone.getZoneName());
        locationEditText.setText(zone.getZoneLocation());
        productsEditText.setText(zone.getProductsToCollect());
        descriptionEditText.setText(zone.getDescription());
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