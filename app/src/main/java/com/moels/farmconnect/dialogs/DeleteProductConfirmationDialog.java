package com.moels.farmconnect.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.moels.farmconnect.R;
import com.moels.farmconnect.services.DeleteProductService;
import com.moels.farmconnect.services.DeleteZoneService;
import com.moels.farmconnect.utility_classes.ProductsDatabaseHelper;

public class DeleteProductConfirmationDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private ProductsDatabaseHelper productsDatabaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        productsDatabaseHelper = new ProductsDatabaseHelper(getContext());
        sqLiteDatabase = productsDatabaseHelper.getWritableDatabase();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return (builder.setTitle("Continue To Delete Product")
                .setPositiveButton(android.R.string.ok, this)
                .setIcon(R.drawable.baseline_question_mark_24).setNegativeButton(android.R.string.cancel, null)
                .create());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String productID = getActivity().getIntent().getStringExtra("productID");
        String url = getImageUrl(productID);
        if (!TextUtils.isEmpty(url)){
            boolean productIsDeleted = productsDatabaseHelper.deleteProductFromDatabase(productID);
            if (productIsDeleted){
                Intent deleteProductService = new Intent(getActivity(), DeleteProductService.class);
                deleteProductService.putExtra("imageUrl", url);
                getActivity().startService(deleteProductService);

                Intent resultIntent = new Intent();
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();
            }
        }

    }

    @SuppressLint("Range")
    private String getImageUrl(String productID){
        String url = "";
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT imageUrl FROM products WHERE productRemoteId = " + productID, null);
        if (cursor.moveToNext()){
            url = cursor.getString(cursor.getColumnIndex("imageUrl"));
        }
        cursor.close();
        return url;
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
