package com.moels.farmconnect.view.activities;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moels.farmconnect.R;
import com.moels.farmconnect.view.fragments.ProductsListFragment;
import com.moels.farmconnect.utils.UI;

public class AddProductToZoneActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton addNewProductFAB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_to_zone);
        initUI();
        setUpStatusBar();
        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);

        setSupportActionBar(toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, getIntent().getStringExtra("zoneName"), true);

        Drawable icon = toolbar.getOverflowIcon();
        if (icon != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setTint(ContextCompat.getColor(this, R.color.colorWhite));
                toolbar.setOverflowIcon(icon);
            }
        }

        addFragmentsToActivity();
        addNewProductFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProductToZoneActivity.this, CreateProductActivity.class);
                intent.putExtra("zoneID", getIntent().getStringExtra("zoneID"));
                intent.putExtra("zoneName", getIntent().getStringExtra("zoneName"));
                startActivity(intent);
            }
        });


    }

    private void addFragmentsToActivity(){
        ProductsListFragment productsListFragment = new ProductsListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, productsListFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle(getIntent().getStringExtra("zoneName"));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_product_to_zone_menu, menu);
        configureSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void configureSearchView(Menu menu){
        MenuItem menuItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) menuItem.getActionView();
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
        int id  = item.getItemId();

        if (id == R.id.zone_details){
            Intent intent = new Intent(AddProductToZoneActivity.this, ZoneDetailsActivity.class);
            intent.putExtra("zoneID", getIntent().getStringExtra("zoneID"));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initUI(){
        toolbar = findViewById(R.id.products_in_a_zone_activity_toolbar);
        addNewProductFAB = findViewById(R.id.add_new_product_fab);
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
}