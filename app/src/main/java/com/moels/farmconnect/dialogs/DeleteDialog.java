package com.moels.farmconnect.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.moels.farmconnect.R;
import com.moels.farmconnect.activities.MainActivity;
import com.moels.farmconnect.activities.ProductsInAzoneActivity;
import com.moels.farmconnect.utility_classes.ZonesDatabaseHelper;

public class DeleteDialog extends DialogFragment implements DialogInterface.OnClickListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        return(builder.setTitle("Confirm Delete")
                .setView(getActivity().getLayoutInflater().inflate(R.layout.delete_zone_dialog, null))
                .setPositiveButton(android.R.string.ok, this)
                .setIcon(R.drawable.baseline_question_mark_24)
                .setNegativeButton(android.R.string.cancel, null)
                .create());

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
         String _id = getActivity().getIntent().getStringExtra("zoneID");
         new ZonesDatabaseHelper(getContext()).deleteZoneFromDatabase(_id);

         Intent resultIntent = new Intent();
         getActivity().setResult(Activity.RESULT_OK, resultIntent);
         getActivity().finish();
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
