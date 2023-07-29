package com.moels.farmconnect.view.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.moels.farmconnect.R;
import com.moels.farmconnect.controller.AppController;
import com.moels.farmconnect.model.database.services.DeleteZoneService;
import com.moels.farmconnect.model.database.ZonesDatabaseHelper;

public class DeleteZoneConfirmationDialog extends DialogFragment implements DialogInterface.OnClickListener {

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
         String remote_id = getActivity().getIntent().getStringExtra("zoneID");
         AppController.getInstance().setContext(getActivity().getApplicationContext()).deleteZone(remote_id);

         Intent resultIntent = new Intent();
         getActivity().setResult(Activity.RESULT_OK, resultIntent);
         getActivity().finish();

         //TODO start zone delete service;
//         Intent deleteZoneService = new Intent(getActivity(), DeleteZoneService.class);
//         deleteZoneService.putExtra("zoneID", remote_id);
//         getActivity().startService(deleteZoneService);
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
