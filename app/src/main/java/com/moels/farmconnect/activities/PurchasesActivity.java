package com.moels.farmconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.UiModeManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.moels.farmconnect.R;
import com.moels.farmconnect.adapters.PaymentsListRecyclerViewAdapter;
import com.moels.farmconnect.adapters.PurchasesListRecyclerViewAdapter;
import com.moels.farmconnect.models.PurchasesCard;
import com.moels.farmconnect.utility_classes.FarmConnectAppPreferences;
import com.moels.farmconnect.utility_classes.Preferences;
import com.moels.farmconnect.utility_classes.PurchasesDatabase;
import com.moels.farmconnect.utility_classes.PurchasesDatabaseHelper;
import com.moels.farmconnect.utility_classes.UI;

import java.util.ArrayList;
import java.util.List;

public class PurchasesActivity extends AppCompatActivity {

    private Preferences preferences;
    private PurchasesDatabase purchasesDatabase;
    private Toolbar toolbar;
    private List<PurchasesCard> purchasesCards;
    private RecyclerView recyclerView;
    private PurchasesListRecyclerViewAdapter purchasesListRecyclerViewAdapter;
    private TextView emptyPurchasesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases);
        init();
        setUpStatusBar();
        setSupportActionBar(toolbar);
        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);

        setUpOverflowIcon();
        setUpActionBarTitle();

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        purchasesListRecyclerViewAdapter = new PurchasesListRecyclerViewAdapter(getApplicationContext(), purchasesCards);

        recyclerView.setAdapter(purchasesListRecyclerViewAdapter);

        if (purchasesCards.size() > 0){
            UI.hide(emptyPurchasesTextView);
        }

    }

    private void init(){
        toolbar = findViewById(R.id.purchases_activity_toolbar);
        preferences = FarmConnectAppPreferences.getInstance(getApplicationContext());
        recyclerView = findViewById(R.id.purchases_recycler_view);
        emptyPurchasesTextView = findViewById(R.id.empty_purchases_text_view);
        purchasesCards = new ArrayList<>();
        purchasesDatabase = PurchasesDatabaseHelper.getInstance(getApplicationContext());
        purchasesCards = purchasesDatabase.getPurchases(getIntent().getStringExtra("zoneID"));
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
    private void setUpOverflowIcon(){
        Drawable icon = toolbar.getOverflowIcon();
        if (icon != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setTint(ContextCompat.getColor(this, R.color.colorWhite));
                toolbar.setOverflowIcon(icon);
            }
        }

    }
    private void setUpActionBarTitle(){
        if (preferences.isBuyerAccount()){
            UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Purchases", true);
        } else if (preferences.isFarmerAccount()) {
            UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Sales", true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.purchases_and_payments_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}