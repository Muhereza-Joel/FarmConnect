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
import com.moels.farmconnect.controller.ZonesController;
import com.moels.farmconnect.model.command.CommandListener;

public class ChangeZoneStatusDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private CommandListener commandListener;
    private RadioGroup radioGroup;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return (builder.setTitle("Migrate Status To"))
                .setView(getActivity().getLayoutInflater().inflate(R.layout.zone_status_change_dialog, null))
                .setPositiveButton("Ok", this)
                .setNegativeButton("Cancel", null)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        radioGroup = getDialog().findViewById(R.id.change_zone_status_radio_group);
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        RadioButton selectedRadioButton = radioGroup.findViewById(selectedRadioButtonId);
        if (selectedRadioButton != null){
            String selectedZoneStatus = selectedRadioButton.getText().toString().toLowerCase();

            ZonesController zonesController = ZonesController.getInstance();
            zonesController.setContext(getContext());
            zonesController.setListener(commandListener);
            zonesController.changeZoneStatus(getActivity().getIntent().getStringExtra("zoneID"), selectedZoneStatus);

            Log.d("FarmConnect", "onClick: " + selectedZoneStatus);
        }

    }

    public void setCommandListener(CommandListener listener){
        this.commandListener = listener;
    }

}
