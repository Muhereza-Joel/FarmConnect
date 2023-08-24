package com.moels.farmconnect.view.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.moels.farmconnect.R;
import com.moels.farmconnect.controller.ProductsController;
import com.moels.farmconnect.controller.ZonesController;
import com.moels.farmconnect.model.database.ProductsTable;
import com.moels.farmconnect.utils.UI;
import com.moels.farmconnect.utils.models.Product;
import com.moels.farmconnect.utils.models.Zone;
import com.moels.farmconnect.utils.preferences.Globals;

import java.util.ArrayList;
import java.util.List;

public class ListZonesDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private ListView listView;
    private List<Zone> listOfAllZones;
    private List<Zone> filteredZonesList = new ArrayList<>();
    private List<String> zoneNames = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.list_zones_dialog_layout, null);

        ZonesController zonesController = ZonesController.getInstance();
        zonesController.setContext(getContext());
        listOfAllZones = zonesController.getAllZones();

        ProductsController productsController = ProductsController.getInstance();
        productsController.setContext(getContext());
        Product product = productsController.getProductDetails(getActivity().getIntent().getStringExtra(Globals.PRODUCT_ID));

        for (Zone zone : listOfAllZones){
            if (!product.getZoneID().equals(zone.getZoneID())){
                 System.out.println(product.getZoneID());
                 zoneNames.add(zone.getZoneName());
                 filteredZonesList.add(zone);
            }
        }

        listView = dialogView.findViewById(R.id.zones_list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_single_choice, zoneNames);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (filteredZonesList.size() > 0){
                Zone zone  = filteredZonesList.get(position);
                UI.displayToast(getContext(), zone.getZoneID());
            }
        });

        return (builder.setTitle("Select Zone"))
                .setView(dialogView)
                .setPositiveButton("Ok", this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
