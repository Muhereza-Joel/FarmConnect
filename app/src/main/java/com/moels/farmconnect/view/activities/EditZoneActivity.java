package com.moels.farmconnect.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.moels.farmconnect.R;
import com.moels.farmconnect.controller.Controller;
import com.moels.farmconnect.controller.ZonesController;
import com.moels.farmconnect.model.command.Listener;
import com.moels.farmconnect.model.database.services.UpdateZoneService;
import com.moels.farmconnect.utils.UI;
import com.moels.farmconnect.utils.models.Zone;

import java.util.ArrayList;
import java.util.List;

public class EditZoneActivity extends AppCompatActivity implements Listener {
    private Toolbar editZoneActivityToolbar;
    private EditText zoneNameEditText, locationEditText, productsToCollectEditText, descriptionEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_zone);
        initUI();
        setUpStatusBar();
        UI.setUpToolbarInDarkMode(getApplicationContext(), editZoneActivityToolbar);

        setSupportActionBar(editZoneActivityToolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Edit Zone", true);

        ZonesController zonesController = ZonesController.getInstance();
        zonesController.setContext(getApplicationContext());
        showProductDetails(zonesController.getZoneDetails(getIntent().getStringExtra("zoneID")));
    }

    private void initUI(){
        editZoneActivityToolbar = findViewById(R.id.edit_zone_activity_toolbar);
        zoneNameEditText = findViewById(R.id.zone_name_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        productsToCollectEditText = findViewById(R.id.products_to_collect_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
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

    public void showProductDetails(Zone zone){
        zoneNameEditText.setText(zone.getZoneName());
        locationEditText.setText(zone.getZoneLocation());
        productsToCollectEditText.setText(zone.getProductsToCollect());
        descriptionEditText.setText(zone.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_zone_activity_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.save_edited_zone_details_btn);
        SpannableString spannableString = new SpannableString(menuItem.getTitle());
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), 0);
        menuItem.setTitle(spannableString);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_edited_zone_details_btn){

               ZonesController zonesController =  ZonesController.getInstance();
               zonesController.setContext(getApplicationContext());
               zonesController.setListener(this);
               zonesController.updateZone(getIntent().getStringExtra("zoneID"), getUpdatedValuesFromUI());

        }
        return super.onOptionsItemSelected(item);
    }

    private Zone getUpdatedValuesFromUI(){
        Zone zone = new Zone();
        zone.setZoneName(zoneNameEditText.getText().toString());
        zone.setZoneLocation(locationEditText.getText().toString());
        zone.setProductsToCollect(productsToCollectEditText.getText().toString());
        zone.setDescription(descriptionEditText.getText().toString());

        return zone;
    }

    //TODO start zoneUpdate service.
    private void startServiceToUpdateZoneInFirebase(String remote_id, List<String> updatedZoneDetails){
        Intent updateZoneService = new Intent(EditZoneActivity.this, UpdateZoneService.class);
        updateZoneService.putExtra("zoneID", remote_id);
        updateZoneService.putExtra("zoneName", updatedZoneDetails.get(0));
        updateZoneService.putExtra("location", updatedZoneDetails.get(1));
        updateZoneService.putExtra("productsToCollect", updatedZoneDetails.get(2));
        updateZoneService.putExtra("description", updatedZoneDetails.get(3));
        startService(updateZoneService);
    }


    @Override
    public void onFailure() {
        UI.displayToast(getApplicationContext(), "All Fields Are Required");
    }

    @Override
    public void onSuccess() {
        UI.displayToast(getApplicationContext(), "Collection Zone Updated!!");
        goToParentActivity();
        clearViews();
        finish();
    }

    public void clearViews(){
        zoneNameEditText.setText("");
        locationEditText.setText("");
        productsToCollectEditText.setText("");
        descriptionEditText.setText("");
    }

    private void goToParentActivity(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedZoneName", zoneNameEditText.getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
    }
}