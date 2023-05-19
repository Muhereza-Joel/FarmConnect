package com.moels.farmconnect.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.moels.farmconnect.R;
import com.moels.farmconnect.utility_classes.UI;

public class ProductsInAzoneActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView productsLabelTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_in_azone);
        initUI();
        setSupportActionBar(toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, getIntent().getStringExtra("zoneName"), true);

        Drawable icon = toolbar.getOverflowIcon();
        if (icon != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setTint(ContextCompat.getColor(this, R.color.colorWhite));
                toolbar.setOverflowIcon(icon);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.products_in_zone_activity_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.zone_details){
            Intent intent = new Intent(ProductsInAzoneActivity.this, ZoneDetailsActivity.class);
            intent.putExtra("zoneID", getIntent().getStringExtra("zoneID"));
            startActivity(intent);
        }

        if (id == R.id.edit_zone) {
            Intent intent = new Intent(ProductsInAzoneActivity.this, EditZoneActivity.class);
            intent.putExtra("zoneID", getIntent().getStringExtra("zoneID"));
            startActivity(intent);
        }

        if (id == R.id.delete_zone){
            //TODO create logic to delete zone from database
        }

        return super.onOptionsItemSelected(item);
    }

    private void initUI(){
        toolbar = findViewById(R.id.products_in_a_zone_activity_toolbar);
        productsLabelTextView = findViewById(R.id.products_label);
    }
}