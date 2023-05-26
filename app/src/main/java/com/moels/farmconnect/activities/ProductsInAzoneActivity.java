package com.moels.farmconnect.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.moels.farmconnect.R;
import com.moels.farmconnect.dialogs.DeleteDialog;
import com.moels.farmconnect.utility_classes.UI;

public class ProductsInAzoneActivity extends AppCompatActivity {

    private static final int EDIT_ZONE_REQUEST_CODE = 1;
    private Toolbar toolbar;
    private TextView productsLabelTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_in_azone);
        initUI();
        setUpStatusBar();
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
        configureSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void configureSearchView(Menu menu){
        MenuItem menuItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        searchView.setSubmitButtonEnabled(false);
        searchView.setIconifiedByDefault(true);
        searchView.onActionViewExpanded();

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
            startActivityForResult(intent, EDIT_ZONE_REQUEST_CODE);
        }

        if (id == R.id.delete_zone){
            DeleteDialog deleteDialog = new DeleteDialog();
            deleteDialog.show(getSupportFragmentManager(), "sample");

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_ZONE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            String updatedZoneName = data.getStringExtra("updatedZoneName");
            getSupportActionBar().setTitle(updatedZoneName);
        }
    }

    private void initUI(){
        toolbar = findViewById(R.id.products_in_a_zone_activity_toolbar);
        productsLabelTextView = findViewById(R.id.products_label);
    }

    private void setUpStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }
}