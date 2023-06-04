package com.moels.farmconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moels.farmconnect.R;
import com.moels.farmconnect.dialogs.DeleteProductConfirmationDialog;
import com.moels.farmconnect.services.DeleteProductService;
import com.moels.farmconnect.utility_classes.ProductsDatabaseHelper;
import com.moels.farmconnect.utility_classes.UI;

public class ProductDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView productImageView;
    private TextView productNameTextView, productQuantityTextView, productPriceTextView;
    private ProductsDatabaseHelper productsDatabaseHelper;
    private SQLiteDatabase sqLiteDatabase;

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

        productsDatabaseHelper = new ProductsDatabaseHelper(getApplicationContext());
        sqLiteDatabase = productsDatabaseHelper.getReadableDatabase();
        getProductDetailsFromDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_details_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initUI(){
        toolbar = findViewById(R.id.product_details_activity_toolbar);
        productImageView = findViewById(R.id.product_image_view);
        productNameTextView = findViewById(R.id.product_name_text_view);
        productQuantityTextView = findViewById(R.id.product_quantity_text_view);
        productPriceTextView = findViewById(R.id.product_price_text_view);
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

    private void getProductDetailsFromDatabase(){
        String productRemoteId = getIntent().getStringExtra("productID");
        String [] columnsToPick = {"imageUrl","productName","quantity", "price"};
        Cursor cursor = sqLiteDatabase.query("products",
                columnsToPick,
                "productRemoteId = ?", new String[]{productRemoteId}, null, null, null);

        if (cursor.moveToNext()){
            @SuppressLint("Range") String productImageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
            @SuppressLint("Range") String productName = cursor.getString(cursor.getColumnIndex("productName"));
            @SuppressLint("Range") String productQuantity = cursor.getString(cursor.getColumnIndex("quantity"));
            @SuppressLint("Range") String productPrice = cursor.getString(cursor.getColumnIndex("price"));

            loadDetailsInUI(productImageUrl, productName, productQuantity, productPrice);

            cursor.close();
        }
    }

    private void loadDetailsInUI(String imageUrl, String productName, String quantity, String price){
        Glide.with(getApplicationContext()).load(imageUrl).into(productImageView);
        productNameTextView.setText(productName);
        productQuantityTextView.setText(quantity);
        productPriceTextView.setText(price);
    }

}