package com.moels.farmconnect.fragments;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.moels.farmconnect.R;
import com.moels.farmconnect.activities.ProductDetailsActivity;
import com.moels.farmconnect.adapters.ProductsRecyclerViewAdapter;
import com.moels.farmconnect.models.ProductCardItem;
import com.moels.farmconnect.utility_classes.ProductsDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ProductsListFragment extends Fragment {
    private RecyclerView productListRecyclerView;
    private ProductsRecyclerViewAdapter productsRecyclerViewAdapter;
    private ProductsDatabaseHelper productsDatabaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    public List<ProductCardItem> productCardItems;
    private SharedPreferences sharedPreferences;
    private String authenticatedPhoneNumber;
    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productsDatabaseHelper = new ProductsDatabaseHelper(getContext());
        sqLiteDatabase = productsDatabaseHelper.getReadableDatabase();
        sharedPreferences = getActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        authenticatedPhoneNumber = sharedPreferences.getString("authenticatedPhoneNumber", "123456789");

        productCardItems = getProductsFromDatabase(getActivity().getIntent().getStringExtra("zoneID"), authenticatedPhoneNumber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_list, container, false);
        UiModeManager uiModeManager = (UiModeManager) getActivity().getSystemService(Context.UI_MODE_SERVICE);
        int currentMode = uiModeManager.getNightMode();

        if (currentMode == UiModeManager.MODE_NIGHT_YES){
            ScrollView scrollView = view.findViewById(R.id.products_scroll_view);
            scrollView.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productListRecyclerView = getView().findViewById(R.id.products_list_recycler_view);
        productListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productListRecyclerView.setAdapter(productsRecyclerViewAdapter);

        if (productCardItems.size() > 0){
            textView = view.findViewById(R.id.products_label);
            textView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        productCardItems = getProductsFromDatabase(getActivity().getIntent().getStringExtra("zoneID"), authenticatedPhoneNumber);
        productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(productCardItems, getContext());
        productListRecyclerView.setAdapter(productsRecyclerViewAdapter);
        //TODO Create bundle to to save tex view or zone name and hide the text view

        productsRecyclerViewAdapter.setListener(new ProductsRecyclerViewAdapter.Listener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                intent.putExtra("productID", productCardItems.get(position).get_id());
                startActivity(intent);
            }
        });
    }

    private List<ProductCardItem> getProductsFromDatabase(String zoneID, String owner){
        List<ProductCardItem> items = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM products WHERE zoneID = '" + zoneID + "' AND owner = '" + owner + "'", null);

        if (cursor.moveToNext()) {
            do {
                @SuppressLint("Range") String productRemoteID = cursor.getString(cursor.getColumnIndex("productRemoteId"));
                @SuppressLint("Range") String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                @SuppressLint("Range") String productName = cursor.getString(cursor.getColumnIndex("productName"));
                @SuppressLint("Range") String productQuantity = cursor.getString(cursor.getColumnIndex("quantity"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("time"));
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));

                if (!TextUtils.isEmpty(imageUrl) || !TextUtils.isEmpty(productName) || !TextUtils.isEmpty(productQuantity)) {
                    ProductCardItem productCardItem = new ProductCardItem(productRemoteID, productName, productQuantity, imageUrl, createTime, status);
                    items.add(productCardItem);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;

    }
}