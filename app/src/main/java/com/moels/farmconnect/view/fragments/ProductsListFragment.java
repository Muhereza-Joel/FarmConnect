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
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.moels.farmconnect.R;
import com.moels.farmconnect.utils.preferences.Globals;
import com.moels.farmconnect.view.ToolbarManager;
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
    private ActionMode actionMode;
    private boolean isActionModeActive = false;
    private TextView productCountSelectionTextView;
    private List<Integer> selectedItems = new ArrayList<>();

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

        //Initialise isSelected flag for each card
        for (Card card : cardList){
            card.setSelected(false);
        }

    }

    public void observeFireBase(){
        Log.d("FarmConnect", "observerFireBase: Observer is running");
        String zoneId = getActivity().getIntent().getStringExtra("zoneID");
        productsObserver = RealTimeProductsObserver.getInstance(authenticatedPhoneNumber, zoneId);
        productsObserver.startListening(new RealTimeProductsObserver.OnProductUpdateListener() {
            @Override
            public void onProductAdded(Product product) {
                product.setUploadStatus(Globals.UploadStatus.TRUE.toString());
                product.setUpdatedStatus(Globals.UpdateStatus.FALSE.toString());
//                productDetails.add(product.getZoneID());
                //TODO add table to track services from services with ids

                boolean rowCreated = productsDatabase.addProduct(product, "202307121049247397");
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
                if (!isActionModeActive){

                Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                    Log.d("ClickTest", "Card clicked at position: " + position);

                //This id will be used together with product id to delete product from firebase
                intent.putExtra("zoneID", getActivity().getIntent().getStringExtra("zoneID"));
                intent.putExtra("zoneName", getActivity().getIntent().getStringExtra("zoneName"));
                intent.putExtra("productID", cardList.get(position).getId());
                startActivityForResult(intent, PRODUCT_DELETE_REQUEST_CODE);
                } else {
                    productsRecyclerViewAdapter.toggleItemSelection(position);
                    toggleSelection(position);
                }

            }

            @Override
            public void onLongClick(int position) {
                startActionMode();


            }
        });
    }

    private void startActionMode(){
        if (actionMode == null){
            actionMode = requireActivity().startActionMode(actionModeCallBack);
            isActionModeActive = true;

            if (getActivity() instanceof ToolbarManager){
                ((ToolbarManager) getActivity()).setToolbarVisibility(false);
            }
        }
    }

    private void toggleSelection(int position){

        if (selectedItems.contains(position)){
            selectedItems.remove(Integer.valueOf(position));
        } else {
            selectedItems.add(position);
        }

        if (selectedItems.isEmpty()){
            finishActionMode();
        } else {
            actionMode.invalidate();
        }

        if (actionMode != null) {
            productCountSelectionTextView.setText(new StringBuilder().append(selectedItems.size()).append(" selected").toString());
        }

        productsRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void finishActionMode(){
        if (actionMode != null) {
            // Clear selection state in data source
            for (int position : selectedItems) {
                cardList.get(position).setSelected(false);
            }

            // Notify adapter of data changes
            productsRecyclerViewAdapter.notifyDataSetChanged();

            actionMode.finish();
            actionMode = null;
            isActionModeActive = false;
            selectedItems.clear();

            if (getActivity() instanceof ToolbarManager) {
                ((ToolbarManager) getActivity()).setToolbarVisibility(true);
            }
        }
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

            productsRecyclerViewAdapter.updateData(cardList);
            productsRecyclerViewAdapter.notifyDataSetChanged();
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

    private ActionMode.Callback actionModeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            MenuInflater inflater = mode.getMenuInflater();
//            inflater.inflate(R.menu.products_action_mode_many_seller_menu, menu);

            View customView = LayoutInflater.from(getContext()).inflate(R.layout.action_mode_custom_layout_1, null);
            mode.setCustomView(customView);
            
            productCountSelectionTextView = customView.findViewById(R.id.product_count_text_view);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            finishActionMode();
        }
    };
}