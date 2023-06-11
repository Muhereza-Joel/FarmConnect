package com.moels.farmconnect.activities;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.moels.farmconnect.R;
import com.moels.farmconnect.services.ZoneUploadService;
import com.moels.farmconnect.utility_classes.UI;
import com.moels.farmconnect.utility_classes.ZonesDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AddNewZoneActivity extends AppCompatActivity {

    private Toolbar addNewZoneActivityToolbar;
    private EditText zoneNameEditText, locationEditText, productsToCollectEditText, descriptionEditText;
    private ZonesDatabaseHelper zonesDatabaseHelper;
    private SharedPreferences myAppPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_zone);
        initUI();
        setUpStatusBar();
        UI.setUpToolbarInDarkMode(getApplicationContext(), addNewZoneActivityToolbar);

        myAppPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        setSupportActionBar(addNewZoneActivityToolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Add New Zone", true);

        zonesDatabaseHelper = new ZonesDatabaseHelper(getApplicationContext());
    }

    private void initUI(){
        addNewZoneActivityToolbar = findViewById(R.id.add_new_zone_top_bar);
        locationEditText = findViewById(R.id.location_edit_text);
        zoneNameEditText = findViewById(R.id.zone_name_edit_text);
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
            View parentView = findViewById(R.id.parent);

            boolean validated = validateViews();
            if (validated){
                boolean zoneCreated = zonesDatabaseHelper.addZoneToDatabase(getValuesFromUI());
                if(zoneCreated) {
                    clearViews();
                    UI.displaySnackBar(getApplicationContext(), parentView, "Collection Zone Created!!");
                    Intent uploadZoneService = new Intent(AddNewZoneActivity.this, ZoneUploadService.class);
                    startService(uploadZoneService);
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

    private List<String> getValuesFromUI(){
        List<String> zoneDetails = new ArrayList<>();

        String uploadedStatus = "false";
        String activeStatus = "active";

        zoneDetails.add(generateUniqueID());
        zoneDetails.add(zoneNameEditText.getText().toString());
        zoneDetails.add(locationEditText.getText().toString());
        zoneDetails.add(productsToCollectEditText.getText().toString());
        zoneDetails.add(descriptionEditText.getText().toString());
        zoneDetails.add(uploadedStatus);
        zoneDetails.add(myAppPreferences.getString("authenticatedPhoneNumber", "123456789"));
        zoneDetails.add(getCurrentDate());
        zoneDetails.add(getCurrentTime());
        zoneDetails.add(activeStatus);

        return zoneDetails;
    }

    private static String generateUniqueID(){
        UUID uuid = UUID.randomUUID();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = simpleDateFormat.format(new Date());

        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        String zoneId = currentTime + randomNumber;
        return zoneId;
    }


    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(calendar.getTime());
        return formattedDate;
    }

    private String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
        String formattedTime = timeFormat.format(calendar.getTime());
        return formattedTime;
    }

    private void clearViews(){
        zoneNameEditText.setText("");
        locationEditText.setText("");
        productsToCollectEditText.setText("");
        descriptionEditText.setText("");
    }

}