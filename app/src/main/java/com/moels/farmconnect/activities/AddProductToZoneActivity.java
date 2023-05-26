package com.moels.farmconnect.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.moels.farmconnect.R;
import com.moels.farmconnect.utility_classes.UI;

public class AddProductToZoneActivity extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_to_zone);
        initUI();

        setSupportActionBar(toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, getIntent().getStringExtra("zoneName"), true);
    }

    private void initUI(){
        toolbar = findViewById(R.id.add_product_to_zone_activity_toolbar);
    }
}