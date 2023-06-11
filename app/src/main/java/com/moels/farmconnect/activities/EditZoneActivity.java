package com.moels.farmconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.moels.farmconnect.R;
import com.moels.farmconnect.services.UpdateZoneService;
import com.moels.farmconnect.utility_classes.UI;
import com.moels.farmconnect.utility_classes.ZonesDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class EditZoneActivity extends AppCompatActivity {
    private Toolbar editZoneActivityToolbar;
    private EditText zoneNameEditText, locationEditText, productsToCollectEditText, descriptionEditText;
    private ZonesDatabaseHelper zonesDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_zone);
        initUI();
        setUpStatusBar();
        UI.setUpToolbarInDarkMode(getApplicationContext(), editZoneActivityToolbar);

        setSupportActionBar(editZoneActivityToolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Edit Zone", true);

        zonesDatabaseHelper = new ZonesDatabaseHelper(getApplicationContext());

        showProductDetails(zonesDatabaseHelper.getZoneDetails(getIntent().getStringExtra("zoneID")));
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

    public void showProductDetails(List<String> productDetails){
        zoneNameEditText.setText(productDetails.get(0));
        locationEditText.setText(productDetails.get(1));
        productsToCollectEditText.setText(productDetails.get(2));
        descriptionEditText.setText(productDetails.get(3));
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
            boolean viewsValidated = validateViews();

            if (viewsValidated){
                boolean zoneUpdated = updateZoneInDatabase(getIntent().getStringExtra("zoneID"), getUpdatedValuesFromUI());
                if (zoneUpdated){
                    UI.displayToast(getApplicationContext(), "Collection Zone Updated!!");
                    clearViews();
                    goToParentActivity();
                    finish();
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateViews(){
        boolean validated = true;
        if (TextUtils.isEmpty(zoneNameEditText.getText().toString())
                || TextUtils.isEmpty(locationEditText.getText().toString())
                || TextUtils.isEmpty(productsToCollectEditText.getText().toString())
                || TextUtils.isEmpty(descriptionEditText.getText().toString())){
            UI.displayToast(getApplicationContext(), "All fields are required");
            validated = false;
        }
        return validated;
    }

    private boolean updateZoneInDatabase(String zoneID, List<String> updatedZoneDetails){

        ContentValues contentValues = new ContentValues();
        contentValues.put("zoneName", updatedZoneDetails.get(0));
        contentValues.put("location", updatedZoneDetails.get(1));
        contentValues.put("products", updatedZoneDetails.get(2));
        contentValues.put("description", updatedZoneDetails.get(3));
        contentValues.put("updated", updatedZoneDetails.get(4));

        boolean zoneUpdated = zonesDatabaseHelper.updateZone(zoneID, contentValues);

        startServiceToUpdateZoneInFirebase(zoneID, updatedZoneDetails);

        return zoneUpdated;
    }

    private List<String> getUpdatedValuesFromUI(){
        String updated = "true";
        List<String> zoneDetails = new ArrayList<>();

        //TODO add flag for updated to true
        zoneDetails.add(zoneNameEditText.getText().toString());
        zoneDetails.add(locationEditText.getText().toString());
        zoneDetails.add(productsToCollectEditText.getText().toString());
        zoneDetails.add(descriptionEditText.getText().toString());
        zoneDetails.add(updated);
        return zoneDetails;
    }

    private void startServiceToUpdateZoneInFirebase(String remote_id, List<String> updatedZoneDetails){
        Intent updateZoneService = new Intent(EditZoneActivity.this, UpdateZoneService.class);
        updateZoneService.putExtra("zoneID", remote_id);
        updateZoneService.putExtra("zoneName", updatedZoneDetails.get(0));
        updateZoneService.putExtra("location", updatedZoneDetails.get(1));
        updateZoneService.putExtra("productsToCollect", updatedZoneDetails.get(2));
        updateZoneService.putExtra("description", updatedZoneDetails.get(3));
        startService(updateZoneService);
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