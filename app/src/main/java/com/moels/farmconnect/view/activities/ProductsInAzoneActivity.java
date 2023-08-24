package com.moels.farmconnect.view.activities;

import android.app.Activity;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.moels.farmconnect.R;
import com.moels.farmconnect.model.command.Command;
import com.moels.farmconnect.model.command.CommandListener;
import com.moels.farmconnect.utils.preferences.Globals;
import com.moels.farmconnect.view.dialogs.ChangeZoneStatusDialog;
import com.moels.farmconnect.view.dialogs.DeleteZoneConfirmationDialog;
import com.moels.farmconnect.view.fragments.ProductsListFragment;
import com.moels.farmconnect.utils.UI;

public class ProductsInAzoneActivity extends AppCompatActivity implements CommandListener {

    private static final int EDIT_ZONE_REQUEST_CODE = 1;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_in_azone);
        initUI();
        setUpStatusBar();
        setSupportActionBar(toolbar);
        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, getIntent().getStringExtra("zoneName"), true);

        Drawable icon = toolbar.getOverflowIcon();
        if (icon != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setTint(ContextCompat.getColor(this, R.color.colorWhite));
                toolbar.setOverflowIcon(icon);
            }
        }

        ProductsListFragment productsListFragment = new ProductsListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, productsListFragment);
        fragmentTransaction.commit();


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

        if (id == R.id.payments_icon){
            Intent intent = new Intent(ProductsInAzoneActivity.this, PaymentsActivity.class);
            intent.putExtra("zoneID", getIntent().getStringExtra("zoneID"));
            startActivity(intent);
        }

        if (id == R.id.purchases_icon){
            Intent intent = new Intent(ProductsInAzoneActivity.this, PurchasesActivity.class);
            intent.putExtra("zoneID", getIntent().getStringExtra("zoneID"));
            startActivity(intent);
        }

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

        if (id == R.id.change_zone_status){
            ChangeZoneStatusDialog changeZoneStatusDialog = new ChangeZoneStatusDialog();
            changeZoneStatusDialog.setCommandListener(this);
            changeZoneStatusDialog.show(getSupportFragmentManager(), "change zone status");
        }

        if (id == R.id.delete_zone){
            DeleteZoneConfirmationDialog deleteDialog = new DeleteZoneConfirmationDialog();
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
    public void onFailure() {

    }

    @Override
    public void onSuccess() {
        View parentView = findViewById(R.id.products_in_a_zone_layout);
        UI.displaySnackBar(getApplicationContext(), parentView, Globals.ZONE_STATUS_UPDATE_MSG);
    }
}