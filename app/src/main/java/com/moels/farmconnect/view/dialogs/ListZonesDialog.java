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

public class ListZonesDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private ListView listView;
    private final  String [] items = {"lorem","joel","lorem","joel","lorem","joel","lorem","joel","lorem","joel","lorem","joel","lorem","joel","lorem","joel"};

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.list_zones_dialog_layout, null);

        // Set up the ListView
        listView = dialogView.findViewById(R.id.zones_list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_single_choice, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Handle item selection here
            // You can use the position parameter to get the selected item
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
