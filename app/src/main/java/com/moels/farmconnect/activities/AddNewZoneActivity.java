package com.moels.farmconnect.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.moels.farmconnect.R;
import com.moels.farmconnect.utility_classes.UI;
import com.moels.farmconnect.utility_classes.ZonesDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNewZoneActivity extends AppCompatActivity {

    private Toolbar addNewZoneActivityToolbar;
    private EditText zoneNameEditText, locationEditText, productsToCollectEditText, descriptionEditText;
    private ZonesDatabaseHelper zonesDatabaseHelper;

    SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_zone);
        initUI();

        setSupportActionBar(addNewZoneActivityToolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Add New Zone", true);

        zonesDatabaseHelper = new ZonesDatabaseHelper(getApplicationContext());
        sqLiteDatabase = zonesDatabaseHelper.getReadableDatabase();
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
            boolean zoneCreated = addZoneToDatabase();

            if(zoneCreated == true) {
                clearEditTexts();
                UI.displaySnackBar(getApplicationContext(), parentView, "Collection Zone Created!!");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean addZoneToDatabase(){
        String zoneName = zoneNameEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String products = productsToCollectEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put("zoneName", zoneName);
        contentValues.put("location", location);
        contentValues.put("products", products);
        contentValues.put("description", description);
        contentValues.put("uploaded", "false");
        contentValues.put("owner", "0776579631"); //TODO get currently authenticated phone number
        contentValues.put("createDate", getCurrentDate());
        contentValues.put("createTime", getCurrentTime());
        contentValues.put("status", "active");

        sqLiteDatabase.insert("zones", null, contentValues);
        return true;
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

    public void clearEditTexts(){
        zoneNameEditText.setText("");
        locationEditText.setText("");
        productsToCollectEditText.setText("");
        descriptionEditText.setText("");
    }

    private void initUI(){
        addNewZoneActivityToolbar = findViewById(R.id.add_new_zone_top_bar);
        locationEditText = findViewById(R.id.location_edit_text);
        zoneNameEditText = findViewById(R.id.zone_name_edit_text);
        productsToCollectEditText = findViewById(R.id.products_to_collect_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);

    }
}