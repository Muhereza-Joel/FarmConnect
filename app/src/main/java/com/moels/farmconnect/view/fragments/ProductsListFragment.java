package com.moels.farmconnect.view.fragments;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.moels.farmconnect.R;
import com.moels.farmconnect.utils.preferences.Globals;
import com.moels.farmconnect.view.activities.ProductDetailsActivity;
import com.moels.farmconnect.view.adapters.ProductsRecyclerViewAdapter;
import com.moels.farmconnect.utils.models.Card;
import com.moels.farmconnect.utils.models.Product;
import com.moels.farmconnect.utils.preferences.FarmConnectAppPreferences;
import com.moels.farmconnect.utils.preferences.Preferences;
import com.moels.farmconnect.model.database.ProductsTable;
import com.moels.farmconnect.model.database.ProductsTableUtil;
import com.moels.farmconnect.model.observers.ProductsObserver;
import com.moels.farmconnect.model.observers.RealTimeProductsObserver;

import java.util.ArrayList;
import java.util.List;

public class ProductsListFragment extends Fragment {
    private static final int PRODUCT_DELETE_REQUEST_CODE = 3;
    private RecyclerView productListRecyclerView;
    private ProductsRecyclerViewAdapter productsRecyclerViewAdapter;
    private ProductsTable productsDatabase;
    public List<Card> cardList;
    private Preferences preferences;
    private String authenticatedPhoneNumber;
    private TextView emptyProductsMessageTextView;
    private View view;
    private ProductsObserver productsObserver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productsDatabase = ProductsTableUtil.getInstance(getContext());
        preferences = FarmConnectAppPreferences.getInstance(getContext());
        authenticatedPhoneNumber = preferences.getString(Globals.AUTHENTICATED_PHONE_NUMBER);

        if (preferences.isFarmerAccount()){
            cardList = productsDatabase.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), authenticatedPhoneNumber);
        }
        else if(preferences.isBuyerAccount()){
            cardList = productsDatabase.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), "");
            observeFireBase();
        }

    }

    public void observeFireBase(){
        Log.d("FarmConnect", "observerFireBase: Observer is running");
        String zoneId = getActivity().getIntent().getStringExtra("zoneID");
        productsObserver = RealTimeProductsObserver.getInstance(authenticatedPhoneNumber, zoneId);
        productsObserver.startListening(new RealTimeProductsObserver.OnProductUpdateListener() {
            @Override
            public void onProductAdded(Product product) {
                List<String> productDetails = new ArrayList<>();
                String uploaded = "true";
                String updated = "false";

                productDetails.add(product.getProductID());
                productDetails.add(product.getProductName());
                productDetails.add(product.getQuantity());
                productDetails.add(product.getUnitPrice());
                productDetails.add(product.getPrice());
                productDetails.add(product.getImageUrl());
                productDetails.add(uploaded);
                productDetails.add(updated);
                productDetails.add(product.getOwner());
                productDetails.add(product.getCreateDate());
                productDetails.add(product.getCreateTime());
                productDetails.add(product.getStatus());
                productDetails.add(product.getZoneID());

                boolean rowCreated = productsDatabase.addProduct(productDetails);
                if (rowCreated){
                    if (getActivity().getIntent().getStringExtra("zoneID") != null){
                        cardList = productsDatabase.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), "");
                        productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(cardList, getContext());
                        productListRecyclerView.setAdapter(productsRecyclerViewAdapter);
                        Log.d("FarmConnect", "Firebase Observer onProductAdded: ProductID " + product.getProductID() + " added to database");
                    }

                }

                addClickListenerOnCards();
            }

            @Override
            public void onProductChanged(Product product) {
                String productID = product.getProductID();

                ContentValues contentValues = new ContentValues();
                contentValues.put("imageUrl", product.getImageUrl());
                contentValues.put("productName", product.getProductName());
                contentValues.put("quantity", product.getQuantity());
                contentValues.put("unitPrice", product.getUnitPrice());
                contentValues.put("price", product.getPrice());

                boolean productUpdated = productsDatabase.updateProduct(productID, contentValues);
                if (productUpdated){
                    if (getActivity().getIntent().getStringExtra("zoneID") != null) {
                        cardList = productsDatabase.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), "");
                        productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(cardList, getContext());
                        productListRecyclerView.setAdapter(productsRecyclerViewAdapter);
                        Log.d("FarmConnect", "Firebase Observer onProductChanged: ProductID " + productID + " updated");
                    }
                }

                addClickListenerOnCards();
            }

            @Override
            public void onProductRemoved(String productId) {
                if (getActivity().getIntent().getStringExtra("zoneID") != null) {
                    productsDatabase.deleteProductFromDatabase(productId);
                    cardList = productsDatabase.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), "");
                    productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(cardList, getContext());
                    productListRecyclerView.setAdapter(productsRecyclerViewAdapter);
                    addClickListenerOnCards();
                }

                if (view != null) {
                    if (cardList.size() > 0) {
                        emptyProductsMessageTextView = view.findViewById(R.id.products_label);
                        emptyProductsMessageTextView.setVisibility(View.GONE);
                    }else {
                        emptyProductsMessageTextView = view.findViewById(R.id.products_label);
                        emptyProductsMessageTextView.setVisibility(View.VISIBLE);
                    }
                }
                Log.d("FarmConnect", "Firebase Observer onProductRemoved: ProductID " + productId + " removed from database");
                //TODO continue testing to find out faults when you delete up to zero products
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("FarmConnect", "onError: " + errorMessage);
            }
        });

    }

    public void addClickListenerOnCards(){
        productsRecyclerViewAdapter.setListener(new ProductsRecyclerViewAdapter.Listener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getContext(), ProductDetailsActivity.class);

                //This id will be used together with product id to delete product from firebase
                intent.putExtra("zoneID", getActivity().getIntent().getStringExtra("zoneID"));
                intent.putExtra("zoneName", getActivity().getIntent().getStringExtra("zoneName"));
                intent.putExtra("productID", cardList.get(position).getId());
                startActivityForResult(intent, PRODUCT_DELETE_REQUEST_CODE);

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products_list, container, false);
        if (cardList.size() > 0){
            emptyProductsMessageTextView = view.findViewById(R.id.products_label);
            emptyProductsMessageTextView.setVisibility(View.GONE);
        }

        UiModeManager uiModeManager = (UiModeManager) getActivity().getSystemService(Context.UI_MODE_SERVICE);
        int currentMode = uiModeManager.getNightMode();

        if (currentMode == UiModeManager.MODE_NIGHT_YES){
            ScrollView scrollView = view.findViewById(R.id.products_scroll_view);
            scrollView.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        productListRecyclerView = getView().findViewById(R.id.products_list_recycler_view);
        productListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productListRecyclerView.setAdapter(productsRecyclerViewAdapter);
//        scrollRecycleViewToBottom(productListRecyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (preferences.isFarmerAccount()){
            cardList = productsDatabase.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), authenticatedPhoneNumber);
        }
        else if(preferences.isBuyerAccount()){
            cardList = productsDatabase.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), "");
            observeFireBase();
        }

        productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(cardList, getContext());
        productListRecyclerView.setAdapter(productsRecyclerViewAdapter);

        if (view != null) {
            // Access the view and perform any necessary modifications
            if (cardList.size() > 0) {
                emptyProductsMessageTextView = view.findViewById(R.id.products_label);
                emptyProductsMessageTextView.setVisibility(View.GONE);
            }else {
                emptyProductsMessageTextView = view.findViewById(R.id.products_label);
                emptyProductsMessageTextView.setVisibility(View.VISIBLE);
            }
        }

        addClickListenerOnCards();
    }

    private void scrollRecycleViewToBottom(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVisibleItemPosition = layoutManager != null ? layoutManager.findLastVisibleItemPosition() : 0;
        int itemCount = layoutManager != null ? layoutManager.getItemCount() : 0;

        if (lastVisibleItemPosition != (itemCount - 1) || recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() > recyclerView.getHeight()) {
            recyclerView.scrollToPosition(itemCount - 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PRODUCT_DELETE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            String updatedZoneName = data.getStringExtra("updatedZoneName");

            if (preferences.isFarmerAccount()){
                cardList = productsDatabase.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), authenticatedPhoneNumber);
            }
            else if(preferences.isBuyerAccount()){
                cardList = productsDatabase.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), "");
            }

            productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(cardList, getContext());
            productListRecyclerView.setAdapter(productsRecyclerViewAdapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (preferences.isBuyerAccount()){
            productsObserver.stopListening();
            Log.d("FarmConnect", "onDestroy: Observer is not running");
        }

    }
}