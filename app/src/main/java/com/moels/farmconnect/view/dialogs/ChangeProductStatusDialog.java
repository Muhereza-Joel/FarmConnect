package com.moels.farmconnect.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.moels.farmconnect.R;
import com.moels.farmconnect.controller.ProductsController;
import com.moels.farmconnect.model.command.CommandListener;
import com.moels.farmconnect.utils.UI;
import com.moels.farmconnect.view.activities.ProductsInAzoneActivity;

public class ChangeProductStatusDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private CommandListener commandListener;
    private RadioGroup radioGroup;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return (builder.setTitle("Change Product Status"))
                .setView(getActivity().getLayoutInflater().inflate(R.layout.product_status_change_dialog, null))
                .setPositiveButton("Change Status", this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        radioGroup = getDialog().findViewById(R.id.product_status_radio_group);
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        RadioButton selectedRadioButton =  radioGroup.findViewById(selectedRadioButtonId);
        if (selectedRadioButton != null){
            String selectedStatus = selectedRadioButton.getText().toString().toLowerCase();
            ProductsController productsController = ProductsController.getInstance();
            productsController.setContext(getContext());
            productsController.setListener(commandListener);
            productsController.changeProductStatus(getActivity().getIntent().getStringExtra("productID"), selectedStatus);
        }
    }

    public void setCommandListener(CommandListener listener){
        this.commandListener = listener;
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
