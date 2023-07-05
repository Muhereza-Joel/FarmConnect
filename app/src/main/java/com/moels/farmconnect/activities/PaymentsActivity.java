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
import com.moels.farmconnect.command.Command;
import com.moels.farmconnect.models.PaymentCard;
import com.moels.farmconnect.utility_classes.FarmConnectAppPreferences;
import com.moels.farmconnect.utility_classes.PaymentsDatabase;
import com.moels.farmconnect.utility_classes.PaymentsDatabaseHelper;
import com.moels.farmconnect.utility_classes.Preferences;
import com.moels.farmconnect.utility_classes.UI;

import java.util.ArrayList;
import java.util.List;

public class PaymentsActivity extends AppCompatActivity{

    private Preferences preferences;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private PaymentsListRecyclerViewAdapter paymentsListRecyclerViewAdapter;
    private List<PaymentCard> paymentCards;
    private TextView emptyPaymentsLabel;
    PaymentsDatabase paymentsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        init();
        setUpStatusBar();
        setSupportActionBar(toolbar);
        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);

        setUpOverflowIcon();
        setUpActionBarTitle();

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        paymentsListRecyclerViewAdapter = new PaymentsListRecyclerViewAdapter(getApplicationContext(), paymentCards);
        recyclerView.setAdapter(paymentsListRecyclerViewAdapter);

        if (paymentCards.size() > 0){
            UI.hide(emptyPaymentsLabel);
        }



    }

    private void init(){
        toolbar = findViewById(R.id.payments_activity_toolbar);
        preferences = FarmConnectAppPreferences.getInstance(getApplicationContext());
        recyclerView = findViewById(R.id.payments_recycler_view);
        emptyPaymentsLabel = findViewById(R.id.empty_message_label);
        paymentsDatabase = PaymentsDatabaseHelper.getInstance(getApplicationContext());
        paymentCards = paymentsDatabase.getPayments(getIntent().getStringExtra("zoneID"));
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
            UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Payments", true);
        } else if (preferences.isFarmerAccount()) {
            UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Withdraws", true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.purchases_and_payments_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}