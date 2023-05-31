package com.moels.farmconnect.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moels.farmconnect.R;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productCardItems = new ArrayList<>();
        ProductCardItem productCardItem = new ProductCardItem("1", "Coffee", "100 bags", "", "Not Picked");
        productCardItems.add(productCardItem);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_list, container, false);
        if (productCardItems.size() > 0){
            TextView textView = view.findViewById(R.id.products_label);
            textView.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        productListRecyclerView = getView().findViewById(R.id.products_list_recycler_view);
        productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(productCardItems, getContext());
        productListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productListRecyclerView.setAdapter(productsRecyclerViewAdapter);
    }
}