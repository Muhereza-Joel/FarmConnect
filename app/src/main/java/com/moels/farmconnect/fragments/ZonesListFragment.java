package com.moels.farmconnect.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moels.farmconnect.R;
import com.moels.farmconnect.activities.AddProductToZoneActivity;
import com.moels.farmconnect.activities.FinalizeSetupOfZonesActivity;
import com.moels.farmconnect.activities.MainActivity;
import com.moels.farmconnect.activities.ProductsInAzoneActivity;
import com.moels.farmconnect.adapters.ZoneListRecyclerViewAdapter;
import com.moels.farmconnect.models.ZoneCardItem;
import com.moels.farmconnect.utility_classes.ZonesDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ZonesListFragment extends Fragment {

    private static final int DELETE_REQUEST_CODE = 2;
    private RecyclerView zonesListRecyclerView;
    private ZoneListRecyclerViewAdapter zoneListRecyclerViewAdapter;
    private ZonesDatabaseHelper zonesDatabaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private List<ZoneCardItem> zoneCardItems;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zones_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        zonesDatabaseHelper = new ZonesDatabaseHelper(getContext());
        sqLiteDatabase = zonesDatabaseHelper.getReadableDatabase();

        zonesListRecyclerView = getView().findViewById(R.id.zones_list_recycler_view);
        zonesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        zonesListRecyclerView.setAdapter(zoneListRecyclerViewAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        zoneCardItems = getZonesFromDatabase();
        zoneListRecyclerViewAdapter = new ZoneListRecyclerViewAdapter(getZonesFromDatabase(), getContext());
        zonesListRecyclerView.setAdapter(zoneListRecyclerViewAdapter);

        zoneListRecyclerViewAdapter.setListener(new ZoneListRecyclerViewAdapter.Listener() {
            @Override
            public void onClick(int position) {

                SharedPreferences myAppPreferences = getActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                if (myAppPreferences.getBoolean("farmerAccountTypeChosen", false) == true){
                    Intent intent = new Intent(getContext(), AddProductToZoneActivity.class);
                    intent.putExtra("zoneName", zoneCardItems.get(position).getZoneName());
                    intent.putExtra("zoneID", zoneCardItems.get(position).get_id());
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(getContext(), ProductsInAzoneActivity.class);
                    intent.putExtra("zoneName", zoneCardItems.get(position).getZoneName());
                    intent.putExtra("zoneID", zoneCardItems.get(position).get_id());
                    startActivityForResult(intent, DELETE_REQUEST_CODE);
                }

            }
        });
        zoneListRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DELETE_REQUEST_CODE && requestCode == Activity.RESULT_OK){
            new MainActivity().tabLayout.getTabAt(2).select();
        }
    }

    private List<ZoneCardItem> getZonesFromDatabase(){
        List<ZoneCardItem> listOfZoneCardItems = new ArrayList<>();
        String [] columnsToPick = {"_id","zoneName", "location", "createTime", "status"};

        //TODO select from farmer zones database if farmer account chosen
        Cursor cursor = sqLiteDatabase.query("zones", columnsToPick, null, null, null, null, null);

        if (cursor.moveToNext()) {
            do {
                @SuppressLint("Range") String _id = cursor.getString(cursor.getColumnIndex("_id"));
                @SuppressLint("Range") String zoneName = cursor.getString(cursor.getColumnIndex("zoneName"));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex("location"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));

                if (!TextUtils.isEmpty(zoneName) || !TextUtils.isEmpty(location)) {
                    ZoneCardItem zoneCardItem = new ZoneCardItem(_id, zoneName, location, createTime, status);
                    listOfZoneCardItems.add(zoneCardItem);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listOfZoneCardItems;
    }



}