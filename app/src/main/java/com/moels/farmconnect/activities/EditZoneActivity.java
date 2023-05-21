package com.moels.farmconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.moels.farmconnect.R;
import com.moels.farmconnect.utility_classes.UI;
import com.moels.farmconnect.utility_classes.ZonesDatabaseHelper;

public class EditZoneActivity extends AppCompatActivity {
    private Toolbar editZoneActivityToolbar;
    private EditText zoneNameEditText, locationEditText, productsToCollectEditText, descriptionEditText;
    private ZonesDatabaseHelper zonesDatabaseHelper;
    SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_zone);
        initUI();

        setSupportActionBar(editZoneActivityToolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Edit Zone", true);

        zonesDatabaseHelper = new ZonesDatabaseHelper(getApplicationContext());
        sqLiteDatabase = zonesDatabaseHelper.getWritableDatabase();

        getZoneDetailsFromDatabase();
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
            boolean validated = validateTextViews();

            if (validated == true){
                boolean zoneUpdated = updateZoneInDatabase();
                if (zoneUpdated == true){
                    UI.displayToast(getApplicationContext(), "Collection Zone Updated!!");
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedZoneName", zoneNameEditText.getText().toString());
                    setResult(Activity.RESULT_OK, resultIntent);
                    clearEditTexts();
                    finish();
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void initUI(){
        editZoneActivityToolbar = findViewById(R.id.edit_zone_activity_toolbar);
        zoneNameEditText = findViewById(R.id.zone_name_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        productsToCollectEditText = findViewById(R.id.products_to_collect_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
    }

    private void getZoneDetailsFromDatabase(){
        String _id = getIntent().getStringExtra("zoneID");
        String [] columnsToPick = {"_id","zoneName", "location", "products", "description"};
        Cursor cursor = sqLiteDatabase.query("zones", columnsToPick, "_id = ?", new String[]{_id}, null, null, null);

        if (cursor.moveToNext()){
            do {
                @SuppressLint("Range") String zoneName = cursor.getString(cursor.getColumnIndex("zoneName"));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex("location"));
                @SuppressLint("Range") String products = cursor.getString(cursor.getColumnIndex("products"));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));

                zoneNameEditText.setText(zoneName);
                locationEditText.setText(location);
                productsToCollectEditText.setText(products);
                descriptionEditText.setText(description);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private boolean validateTextViews(){
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

    public void clearEditTexts(){
        zoneNameEditText.setText("");
        locationEditText.setText("");
        productsToCollectEditText.setText("");
        descriptionEditText.setText("");
    }

    private boolean updateZoneInDatabase(){
        String zoneName = zoneNameEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String products = productsToCollectEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put("zoneName", zoneName);
        contentValues.put("location", location);
        contentValues.put("products", products);
        contentValues.put("description", description);

        String _id = getIntent().getStringExtra("zoneID");
        sqLiteDatabase.update("zones", contentValues, "_id = ?", new String[] {_id});

        return true;
    }
}