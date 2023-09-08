package com.moels.farmconnect.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.moels.farmconnect.R;
import com.moels.farmconnect.controller.ProductsController;
import com.moels.farmconnect.controller.ZonesController;
import com.moels.farmconnect.utils.models.Zone;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectCopyDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private List<String> listOfAllZonesIDs;
    private List<Zone> zoneList = new ArrayList<>();
    private List<String> productIDs;
    private List<String> zoneNames = new ArrayList<>();
    private List<String> selectedZoneIDs = new ArrayList<>();
    private ListView listView;
    private final ZonesController zonesController = ZonesController.getInstance();
    private final ProductsController productsController = ProductsController.getInstance();

    public void setListOfAllZonesIDs(List<String> listOfAllZonesIDs) {
        this.listOfAllZonesIDs = listOfAllZonesIDs;
    }

    public void setProductIDs(List<String> productIDs) {
        this.productIDs = productIDs;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.list_zones_dialog_layout_2, null);

        zonesController.setContext(getContext());

        productsController.setContext(getContext());

        for (String zoneID : listOfAllZonesIDs){
            Zone zone = zonesController.getZoneDetails(zoneID);
            zoneNames.add(zone.getZoneName());
            zoneList.add(zone);

        }
        listView = dialogView.findViewById(R.id.zones_list_view_2);

        if (zoneNames.size() > 0){
            TextView textView = dialogView.findViewById(R.id.no_zones_to_pick);
            textView.setVisibility(View.GONE);
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
                Zone zone  = zoneList.get(position);

                if (!selectedZoneIDs.contains(zone.getZoneID())){
                    selectedZoneIDs.add(zone.getZoneID());

                } else selectedZoneIDs.remove(zone.getZoneID());

        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, zoneNames);

        listView.setAdapter(adapter);

        return (builder.setTitle("Copy Product To"))
                .setView(dialogView)
                .setPositiveButton("Ok", this)
                .setNegativeButton("Cancel", null)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        productIDs.clear();
        listOfAllZonesIDs.clear();

        super.onDismiss(dialog);
    }
}
