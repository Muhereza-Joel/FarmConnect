package com.moels.farmconnect.activities;

import static com.moels.farmconnect.easypay.DepositRequest.EP_REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moels.farmconnect.R;
import com.moels.farmconnect.dialogs.DeleteProductConfirmationDialog;
import com.moels.farmconnect.easypay.DepositRequest;
import com.moels.farmconnect.easypay.WithdrawRequest;
import com.moels.farmconnect.utility_classes.ProductsDatabase;
import com.moels.farmconnect.utility_classes.ProductsDatabaseHelper;
import com.moels.farmconnect.utility_classes.UI;

import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {
    private final String postUrl = "https://www.easypay.co.ug/api/";
    private final String APIClientID = "b462d01c06404cb0";
    private final String APIClientSecret = "527b39bd50e77706";
    private final String transactionCurrency = "UGX";
    private final String transactionReference = "nsiimbi_com_" + System.currentTimeMillis();
    private Toolbar toolbar;
    private ImageView productImageView;
    private TextView productNameTextView, productQuantityTextView,productUnitPriceTextView, productPriceTextView;
    private FloatingActionButton sendMessageFloatingActionButton, makePaymentFloatingActionButton;
    private ProductsDatabase productsDatabase;
    private SharedPreferences sharedPreferences;
    private boolean isFarmerAccount;
    private boolean isBuyerAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        initUI();
        setUpStatusBar();
        setSupportActionBar(toolbar);
        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Product Details", true);

        Drawable icon = toolbar.getOverflowIcon();
        if (icon != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setTint(ContextCompat.getColor(this, R.color.colorWhite));
                toolbar.setOverflowIcon(icon);
            }
        }

        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        isFarmerAccount = sharedPreferences.getBoolean("farmerAccountTypeChosen", false);
        isBuyerAccount = sharedPreferences.getBoolean("buyerAccountTypeChosen", false);
        productsDatabase = ProductsDatabaseHelper.getInstance(getApplicationContext());
        showProductDetails(productsDatabase.getProductDetails(getIntent().getStringExtra("productID")));

        addClickEventOnPaymentFab();
    }

    private void addClickEventOnPaymentFab(){
        makePaymentFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBuyerAccount){
                    initDepositRequest();
                }

                if (isFarmerAccount){
                    initWithdrawRequest();
                }

            }
        });
    }

    private void initDepositRequest(){
        try {
            new DepositRequest(ProductDetailsActivity.this)
                    .setAmountToPay(productPriceTextView.getText().toString())
                    .setTransactionCurrency(transactionCurrency)
                    .setPaymentReason("FarmConnect Purchase Payment")
                    .setAPIClientSecret(APIClientSecret)
                    .setAPIClientID(APIClientID)
                    .initialize();
        }catch (Exception e){
            UI.displayToast(ProductDetailsActivity.this, e.getMessage());
        }
    }

    private void initWithdrawRequest(){
        try {
            new WithdrawRequest(ProductDetailsActivity.this)
                    .setAmountToWithdraw("5000")
                    .setTransactionCurrency(transactionCurrency)
                    .setPaymentReason("FarmConnect Purchase Payment")
                    .setRequestAction("mmpayout")
                    .setPostUrl(postUrl)
                    .setAPIClientSecret(APIClientSecret)
                    .setAPIClientID(APIClientID)
                    .setTransactionReference(transactionReference)
                    .initialize();
        }catch (Exception e){
            UI.displayToast(ProductDetailsActivity.this, e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EP_REQUEST_CODE && resultCode == RESULT_OK) {
            String stringResponse = data.getStringExtra("response");
            UI.displayToast(getApplicationContext(), stringResponse);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProductDetails(productsDatabase.getProductDetails(getIntent().getStringExtra("productID")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isFarmerAccount){
            getMenuInflater().inflate(R.menu.product_details_activity_menu, menu);
        }
        else if (isBuyerAccount){
            getMenuInflater().inflate(R.menu.product_details_activity_menu_for_buyer, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void initUI(){
        toolbar = findViewById(R.id.product_details_activity_toolbar);
        productImageView = findViewById(R.id.product_image_view);
        productNameTextView = findViewById(R.id.product_name_text_view);
        productQuantityTextView = findViewById(R.id.product_quantity_text_view);
        productUnitPriceTextView = findViewById(R.id.product_unit_text_view);
        productPriceTextView = findViewById(R.id.product_price_text_view);
        sendMessageFloatingActionButton = findViewById(R.id.send_message_fab);
        makePaymentFloatingActionButton = findViewById(R.id.make_payment_fab);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_product){
            Intent intent = new Intent(ProductDetailsActivity.this, EditProductActivity.class);
            intent.putExtra("productID", getIntent().getStringExtra("productID"));
            startActivity(intent);
        }

        if (id == R.id.delete_product){
            DeleteProductConfirmationDialog deleteProductConfirmationDialog = new DeleteProductConfirmationDialog();
            deleteProductConfirmationDialog.show(getSupportFragmentManager(), "deleteProduct");
        }

        return super.onOptionsItemSelected(item);
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

    private void showProductDetails(List<String> productDetails){
        if (productDetails.size() > 0){
            Glide.with(getApplicationContext()).load(productDetails.get(0)).into(productImageView);
            productNameTextView.setText(productDetails.get(1));
            productQuantityTextView.setText(productDetails.get(2));
            productUnitPriceTextView.setText(productDetails.get(3));
            productPriceTextView.setText(productDetails.get(4));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isBuyerAccount){
            Intent intent = new Intent(ProductDetailsActivity.this, ProductsInAzoneActivity.class);
            intent.putExtra("zoneID", getIntent().getStringExtra("zoneID"));
            intent.putExtra("zoneName", getIntent().getStringExtra("zoneName"));
            startActivity(intent);
            finish();
        }

        if (isFarmerAccount){
            Intent intent = new Intent(ProductDetailsActivity.this, AddProductToZoneActivity.class);
            intent.putExtra("zoneID", getIntent().getStringExtra("zoneID"));
            intent.putExtra("zoneName", getIntent().getStringExtra("zoneName"));
            startActivity(intent);
            finish();
        }


    }
}