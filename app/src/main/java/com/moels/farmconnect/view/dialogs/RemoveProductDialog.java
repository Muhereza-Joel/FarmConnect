package com.moels.farmconnect.view.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.moels.farmconnect.R;
import com.moels.farmconnect.controller.ProductsController;
import com.moels.farmconnect.controller.ZonesController;
import com.moels.farmconnect.utils.models.Product;
import com.moels.farmconnect.utils.models.Zone;
import com.moels.farmconnect.utils.preferences.Globals;

import java.util.ArrayList;
import java.util.List;

public class RemoveProductDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private List<String> zoneListWhereProductIsCurrently;
    private String currentZoneID;
    private Product product;
    private final ProductsController productsController = ProductsController.getInstance();
    TextView removeProductMsgTextView, removeProductErrorMsgTextView;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        currentZoneID = getActivity().getIntent().getStringExtra(Globals.ZONE_ID);

        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();

        View dialogView = layoutInflater.inflate(R.layout.remove_product_dialog_layout, null);
        removeProductMsgTextView = dialogView.findViewById(R.id.remove_product_explanation_text_view);
        removeProductErrorMsgTextView = dialogView.findViewById(R.id.remove_product_error_text_view);


        productsController.setContext(getContext());
        product = productsController.getProductDetails(getActivity().getIntent().getStringExtra(Globals.PRODUCT_ID));

        zoneListWhereProductIsCurrently = productsController.getProductMappings(product.getProductID());

        if (zoneListWhereProductIsCurrently.size() == 1){
            removeProductMsgTextView.setVisibility(View.GONE);
            removeProductErrorMsgTextView.setVisibility(View.VISIBLE);

        } else {
            removeProductMsgTextView.setVisibility(View.VISIBLE);
            removeProductErrorMsgTextView.setVisibility(View.GONE);
        }

        return (builder.setTitle("Remove product"))
                .setView(dialogView)
                .setPositiveButton("Ok", this)
                .setNegativeButton("Cancel", null)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (!TextUtils.isEmpty(product.getProductID()) && !TextUtils.isEmpty(currentZoneID) && zoneListWhereProductIsCurrently.size() > 1) {
            productsController.deleteProductMapping(product.getProductID(), currentZoneID);
        }

    }
}
