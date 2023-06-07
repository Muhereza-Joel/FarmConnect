package com.moels.farmconnect.fragments;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.List;

public class ProductsListFragment extends Fragment {
    private static final int PRODUCT_DELETE_REQUEST_CODE = 3;
    private RecyclerView productListRecyclerView;
    private ProductsRecyclerViewAdapter productsRecyclerViewAdapter;
    private ProductsDatabaseHelper productsDatabaseHelper;
    public List<ProductCardItem> productCardItems;
    private SharedPreferences sharedPreferences;
    private String authenticatedPhoneNumber;
    private TextView textView;
    private View view;
    private boolean isFarmerAccount;
    private boolean isBuyerAccount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productsDatabaseHelper = new ProductsDatabaseHelper(getContext());
        sharedPreferences = getActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        authenticatedPhoneNumber = sharedPreferences.getString("authenticatedPhoneNumber", "123456789");
        isFarmerAccount = sharedPreferences.getBoolean("farmerAccountTypeChosen", false);
        isBuyerAccount = sharedPreferences.getBoolean("buyerAccountTypeChosen", false);

        if (isFarmerAccount){
            productCardItems = productsDatabaseHelper.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), authenticatedPhoneNumber);
        }
        else if(isBuyerAccount){
            productCardItems = productsDatabaseHelper.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), "");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products_list, container, false);
        if (productCardItems.size() > 0){
            textView = view.findViewById(R.id.products_label);
            textView.setVisibility(View.GONE);
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
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isFarmerAccount){
            productCardItems = productsDatabaseHelper.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), authenticatedPhoneNumber);
        }
        else if(isBuyerAccount){
            productCardItems = productsDatabaseHelper.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), "");
        }

        productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(productCardItems, getContext());
        productListRecyclerView.setAdapter(productsRecyclerViewAdapter);

        if (view != null) {
            // Access the view and perform any necessary modifications
            if (productCardItems.size() > 0) {
                textView = view.findViewById(R.id.products_label);
                textView.setVisibility(View.GONE);
            }else {
                textView = view.findViewById(R.id.products_label);
                textView.setVisibility(View.VISIBLE);
            }
        }

        productsRecyclerViewAdapter.setListener(new ProductsRecyclerViewAdapter.Listener() {
            @Override
            public void onClick(int position) {
                    Intent intent = new Intent(getContext(), ProductDetailsActivity.class);

                    //This id will be used together with product id to delete product from firebase
                    intent.putExtra("zoneID", getActivity().getIntent().getStringExtra("zoneID"));
                    intent.putExtra("productID", productCardItems.get(position).get_id());
                    startActivityForResult(intent, PRODUCT_DELETE_REQUEST_CODE);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PRODUCT_DELETE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            String updatedZoneName = data.getStringExtra("updatedZoneName");

            if (isFarmerAccount){
                productCardItems = productsDatabaseHelper.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), authenticatedPhoneNumber);
            }
            else if(isBuyerAccount){
                productCardItems = productsDatabaseHelper.getAllProducts(getActivity().getIntent().getStringExtra("zoneID"), "");
            }

            productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(productCardItems, getContext());
            productListRecyclerView.setAdapter(productsRecyclerViewAdapter);
        }
    }

}