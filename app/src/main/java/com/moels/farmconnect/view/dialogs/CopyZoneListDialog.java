package com.moels.farmconnect.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.moels.farmconnect.utils.UI;
import com.moels.farmconnect.utils.models.Product;
import com.moels.farmconnect.utils.models.Zone;
import com.moels.farmconnect.utils.preferences.Globals;

import java.util.ArrayList;
import java.util.List;

public class CopyZoneListDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private ListView listView;
    private List<Zone> listOfAllZones;
    private List<Zone> filteredZonesList = new ArrayList<>();
    private List<String> zoneNames = new ArrayList<>();
    private Product product;
    private List<String> selectedZoneIDs = new ArrayList<>();
    private final ZonesController zonesController = ZonesController.getInstance();
    private final ProductsController productsController = ProductsController.getInstance();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.list_zones_dialog_layout_2, null);

        zonesController.setContext(getContext());
        listOfAllZones = zonesController.getAllZones();

        productsController.setContext(getContext());
        product = productsController.getProductDetails(getActivity().getIntent().getStringExtra(Globals.PRODUCT_ID));

        for (Zone zone : listOfAllZones){

                List<String> mappedIds = productsController.getProductMappings(product.getProductID());

                if (!mappedIds.contains(zone.getZoneID())){
                    zoneNames.add(zone.getZoneName());
                    filteredZonesList.add(zone);
                }

        }

        listView = dialogView.findViewById(R.id.zones_list_view_2);

        if (zoneNames.size() > 0){
            TextView textView = dialogView.findViewById(R.id.no_zones_to_pick);
            textView.setVisibility(View.GONE);
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (filteredZonesList.size() > 0){
                Zone zone  = filteredZonesList.get(position);

                if (!selectedZoneIDs.contains(zone.getZoneID())){
                     selectedZoneIDs.add(zone.getZoneID());
                }
            }
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
        if (!TextUtils.isEmpty(product.getProductID()) && selectedZoneIDs.size() > 0) {
            productsController.copyProduct(product.getProductID(), selectedZoneIDs);
        }
        else {
            if (zoneNames.size() > 0) {
                UI.displayToast(getContext(), "No zones Selected");
            }
        }
    }
}
