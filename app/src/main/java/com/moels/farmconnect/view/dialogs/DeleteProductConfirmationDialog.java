package com.moels.farmconnect.view.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.moels.farmconnect.R;
import com.moels.farmconnect.database.services.DeleteProductService;
import com.moels.farmconnect.database.ProductsDatabase;
import com.moels.farmconnect.database.ProductsDatabaseHelper;

public class DeleteProductConfirmationDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private ProductsDatabase productsDatabase;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        productsDatabase = ProductsDatabaseHelper.getInstance(getContext());;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return (builder.setTitle("Continue To Delete Product")
                .setPositiveButton(android.R.string.ok, this)
                .setIcon(R.drawable.baseline_question_mark_24).setNegativeButton(android.R.string.cancel, null)
                .create());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String zoneID = getActivity().getIntent().getStringExtra("zoneID");
        String productID = getActivity().getIntent().getStringExtra("productID");
        String url = productsDatabase.getProductImageUrl(productID);
        if (!TextUtils.isEmpty(url)){
            boolean productIsDeleted = productsDatabase.deleteProductFromDatabase(productID);
            if (productIsDeleted){
                Intent deleteProductService = new Intent(getActivity(), DeleteProductService.class);
                deleteProductService.putExtra("zoneID", zoneID);
                deleteProductService.putExtra("productID", productID);
                deleteProductService.putExtra("imageUrl", url);
                getActivity().startService(deleteProductService);

                Intent resultIntent = new Intent();
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();
            }
        }

    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
