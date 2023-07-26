package com.moels.farmconnect.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.moels.farmconnect.R;
import com.moels.farmconnect.utils.UI;
import com.moels.farmconnect.model.database.ZonesDatabaseHelper;

public class ZoneDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView zoneNameEditText, locationEditText, productsEditText, descriptionEditText;
    private SQLiteDatabase sqLiteDatabase;
    private ZonesDatabaseHelper zonesDatabaseHelper;

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

        zonesDatabaseHelper = ZonesDatabaseHelper.getInstance(getApplicationContext());
        sqLiteDatabase = zonesDatabaseHelper.getWritableDatabase();

        getZoneDetailsFromDatabase();

    }

    private void initUI(){
        toolbar = findViewById(R.id.products_in_a_zone_activity_toolbar);
        zoneNameEditText = findViewById(R.id.zone_name_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        productsEditText = findViewById(R.id.products_to_collect_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
    }

    private void getZoneDetailsFromDatabase(){
        String remote_id = getIntent().getStringExtra("zoneID");
        String [] columnsToPick = {"remote_id","zoneName", "location", "products", "description"};
        Cursor cursor = sqLiteDatabase.query("zones", columnsToPick, "remote_id = ?", new String[]{remote_id}, null, null, null);

        if (cursor.moveToNext()){
            do {
                @SuppressLint("Range") String zoneName = cursor.getString(cursor.getColumnIndex("zoneName"));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex("location"));
                @SuppressLint("Range") String products = cursor.getString(cursor.getColumnIndex("products"));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));

                zoneNameEditText.setText(zoneName);
                locationEditText.setText(location);
                productsEditText.setText(products);
                descriptionEditText.setText(description);

            } while (cursor.moveToNext());
        }
        cursor.close();
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