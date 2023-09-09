package com.moels.farmconnect.view.activities;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.moels.farmconnect.R;
import com.moels.farmconnect.controller.ZonesController;
import com.moels.farmconnect.model.command.Command;
import com.moels.farmconnect.model.command.CommandListener;
import com.moels.farmconnect.model.database.services.ZoneUploadService;
import com.moels.farmconnect.utils.models.Zone;
import com.moels.farmconnect.utils.preferences.FarmConnectAppPreferences;
import com.moels.farmconnect.utils.preferences.Globals;
import com.moels.farmconnect.utils.preferences.Preferences;
import com.moels.farmconnect.utils.UI;

public class AddNewZoneActivity extends AppCompatActivity implements CommandListener {

    private Toolbar addNewZoneActivityToolbar;
    private EditText zoneNameEditText, locationEditText, productsToCollectEditText, descriptionEditText;
    private Preferences preferences;
    private TextView zoneHeaderTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_zone);
        initUI();
        setUpStatusBar();
        setSupportActionBar(addNewZoneActivityToolbar);
        UI.setUpToolbarInDarkMode(getApplicationContext(), addNewZoneActivityToolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Add New Zone", true);
    }

    private void initUI(){
        addNewZoneActivityToolbar = findViewById(R.id.add_new_zone_top_bar);
        locationEditText = findViewById(R.id.location_edit_text);
        zoneNameEditText = findViewById(R.id.zone_name_edit_text);
        productsToCollectEditText = findViewById(R.id.products_to_collect_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        zoneHeaderTextView = findViewById(R.id.add_zone_header);
        preferences = Preferences.getInstance(getApplicationContext());

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
                zoneHeaderTextView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlack));
            }else {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_zone_activity_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.save_zone_btn);
        SpannableString spannableString = new SpannableString(menuItem.getTitle());
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), 0);
        menuItem.setTitle(spannableString);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.save_zone_btn){
            ZonesController controller = ZonesController.getInstance();
            controller.setContext(getApplicationContext());
            controller.setListener(this);
            controller.saveZone(getValuesFromUI());
        }

        return super.onOptionsItemSelected(item);
    }

    private Zone getValuesFromUI(){

        Zone zone = new Zone();
        zone.setZoneID(UI.generateUniqueID());
        zone.setZoneName(zoneNameEditText.getText().toString());
        zone.setZoneLocation(locationEditText.getText().toString());
        zone.setProductsToCollect(productsToCollectEditText.getText().toString());
        zone.setDescription(descriptionEditText.getText().toString());
        zone.setUploadStatus(Globals.UploadStatus.FALSE.toString());
        zone.setOwner(preferences.getString(Globals.AUTHENTICATED_PHONE_NUMBER));
        zone.setDate(UI.getCurrentDate());
        zone.setTime(UI.getCurrentTime());
        zone.setStatus(Globals.ACTIVE);
        zone.setUpdatedStatus(Globals.UpdateStatus.FALSE.toString());

        return zone;
    }

    @Override
    public void onFailure() {
        UI.displayToast(getApplicationContext(), "All Fields Are Required");
    }

    @Override
    public void onSuccess() {
        View parentView = findViewById(R.id.parent);
        clearViews();
        UI.displaySnackBar(getApplicationContext(), parentView, "Collection Zone Created!!");
//        startZoneUploadService();
        saveFirstZoneCreatedPreference(preferences);
    }

    //TODO start zone upload service;
    private void startZoneUploadService(){
        Intent uploadZoneService = new Intent(AddNewZoneActivity.this, ZoneUploadService.class);
        startService(uploadZoneService);
    }
    private void saveFirstZoneCreatedPreference(Preferences preferences){
        preferences.putBoolean(Preferences.FIRST_ZONE_CREATED, true);
    }

    private void clearViews(){
        zoneNameEditText.setText("");
        locationEditText.setText("");
        productsToCollectEditText.setText("");
        descriptionEditText.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideAddZoneBanner(preferences);
    }

    private void hideAddZoneBanner(Preferences preferences){
        if (preferences.getBoolean(Preferences.FIRST_ZONE_CREATED)){
            UI.hide(zoneHeaderTextView);
        }
    }
}