package com.moels.farmconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.moels.farmconnect.R;
import com.moels.farmconnect.utility_classes.UI;

public class CreateProductActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        initUI();
        setUpStatusBar();

        setSupportActionBar(toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Create New Product", true);
    }

    private void initUI(){
        toolbar = findViewById(R.id.create_product_activity_toolbar);
    }

    private void setUpStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }
}