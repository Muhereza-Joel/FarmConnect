package com.moels.farmconnect.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.UiModeManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moels.farmconnect.R;
import com.moels.farmconnect.activities.AddProductToZoneActivity;
import com.moels.farmconnect.activities.MainActivity;
import com.moels.farmconnect.activities.ProductsInAzoneActivity;
import com.moels.farmconnect.adapters.ZoneListRecyclerViewAdapter;
import com.moels.farmconnect.models.Zone;
import com.moels.farmconnect.models.ZoneCardItem;
import com.moels.farmconnect.utility_classes.ContactsDatabaseHelper;
import com.moels.farmconnect.utility_classes.RealTimeZonesObserver;
import com.moels.farmconnect.utility_classes.ZonesDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ZonesListFragment extends Fragment {

    private static final int ZONE_DELETE_REQUEST_CODE = 2;
    private RecyclerView zonesListRecyclerView;
    private ZoneListRecyclerViewAdapter zoneListRecyclerViewAdapter;
    private ZonesDatabaseHelper zonesDatabaseHelper;
    private ContactsDatabaseHelper contactsDatabaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private List<ZoneCardItem> zoneCardItems;
    private View view;
    private TextView emptyZonesMessageTextView;

    private SharedPreferences sharedPreferences;
    private boolean isFarmerAccount;
    private boolean isBuyerAccount;
    private RealTimeZonesObserver realTimeZonesObserver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zonesDatabaseHelper = ZonesDatabaseHelper.getInstance(getContext());
        contactsDatabaseHelper = new ContactsDatabaseHelper(getContext());
        sqLiteDatabase = zonesDatabaseHelper.getReadableDatabase();
        sharedPreferences = getActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        isFarmerAccount = sharedPreferences.getBoolean("farmerAccountTypeChosen", false);
        isBuyerAccount = sharedPreferences.getBoolean("buyerAccountTypeChosen", false);
        zoneCardItems = getZonesFromDatabase();

        if (isFarmerAccount){
            observeZonesInFirebase();
        }

    }

    public void observeZonesInFirebase(){
        Log.d("FarmConnect", "ZonesFireBaseObserver: Observer is running");
        realTimeZonesObserver = new RealTimeZonesObserver(contactsDatabaseHelper.getAllRegisteredContacts());
        realTimeZonesObserver.startListening(new RealTimeZonesObserver.OnZoneUpdateListener() {
            @Override
            public void onZoneAdded(Zone zone) {
                if (zone != null){
                    String uploaded = "true";
                    String updated = "false";
                    List<String> zoneDetails = new ArrayList<>();

                    zoneDetails.add(zone.getZoneID());
                    zoneDetails.add(zone.getZoneName());
                    zoneDetails.add(zone.getZoneLocation());
                    zoneDetails.add(zone.getProductsToCollect());
                    zoneDetails.add(zone.getDescription());
                    zoneDetails.add(uploaded);
                    zoneDetails.add(zone.getOwner());
                    zoneDetails.add(zone.getDate());
                    zoneDetails.add(zone.getTime());
                    zoneDetails.add(zone.getStatus());
                    zoneDetails.add(updated);

                    zonesDatabaseHelper.addZoneToDatabase(zoneDetails);

                    zoneCardItems = getZonesFromDatabase();
                    zoneListRecyclerViewAdapter = new ZoneListRecyclerViewAdapter(zoneCardItems, getContext());
                    zonesListRecyclerView.setAdapter(zoneListRecyclerViewAdapter);

                    if (view != null) {
                        // Access the view and perform any necessary modifications
                        if (zoneCardItems.size() > 0) {
                            emptyZonesMessageTextView = view.findViewById(R.id.zones_label);
                            emptyZonesMessageTextView.setVisibility(View.GONE);
                        }else {
                            emptyZonesMessageTextView = view.findViewById(R.id.zones_label);
                            emptyZonesMessageTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    addClickEventOnZoneCards();
                }
            }

            @Override
            public void onZoneChanged(Zone zone) {
                if (zone != null){
                    ContentValues contentValues = new ContentValues();
                    String zoneID = zone.getZoneID();
                    contentValues.put("zoneName", zone.getZoneName());
                    contentValues.put("location", zone.getZoneLocation());
                    contentValues.put("products",zone.getProductsToCollect());
                    contentValues.put("description", zone.getDescription());

                    zonesDatabaseHelper.updateZone(zoneID, contentValues);
                    zoneCardItems = getZonesFromDatabase();
                    zoneListRecyclerViewAdapter = new ZoneListRecyclerViewAdapter(zoneCardItems, getContext());
                    zonesListRecyclerView.setAdapter(zoneListRecyclerViewAdapter);

                    if (view != null) {
                        // Access the view and perform any necessary modifications
                        if (zoneCardItems.size() > 0) {
                            emptyZonesMessageTextView = view.findViewById(R.id.zones_label);
                            emptyZonesMessageTextView.setVisibility(View.GONE);
                        }else {
                            emptyZonesMessageTextView = view.findViewById(R.id.zones_label);
                            emptyZonesMessageTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    addClickEventOnZoneCards();
                }

            }

            @Override
            public void onZoneRemoved(String zoneID) {
                Log.d("farmconnect", "onZoneRemoved: " + zoneID);
                zonesDatabaseHelper.deleteZoneFromDatabase(zoneID);
                zoneCardItems = getZonesFromDatabase();
                zoneListRecyclerViewAdapter = new ZoneListRecyclerViewAdapter(zoneCardItems, getContext());
                zonesListRecyclerView.setAdapter(zoneListRecyclerViewAdapter);

                if (view != null) {
                    // Access the view and perform any necessary modifications
                    if (zoneCardItems.size() > 0) {
                        emptyZonesMessageTextView = view.findViewById(R.id.zones_label);
                        emptyZonesMessageTextView.setVisibility(View.GONE);
                    }else {
                        emptyZonesMessageTextView = view.findViewById(R.id.zones_label);
                        emptyZonesMessageTextView.setVisibility(View.VISIBLE);
                    }
                }

                addClickEventOnZoneCards();
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_zones_list, container, false);
        UiModeManager uiModeManager = (UiModeManager) getActivity().getSystemService(Context.UI_MODE_SERVICE);
        int currentMode = uiModeManager.getNightMode();

        if (currentMode == UiModeManager.MODE_NIGHT_YES){
            ScrollView scrollView = view.findViewById(R.id.zone_list_scroll_view);
            scrollView.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        }

        if (zoneCardItems.size() > 0){
            emptyZonesMessageTextView = view.findViewById(R.id.zones_label);
            emptyZonesMessageTextView.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        zonesListRecyclerView = getView().findViewById(R.id.zones_list_recycler_view);
        zonesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        zonesListRecyclerView.setAdapter(zoneListRecyclerViewAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        zoneCardItems = getZonesFromDatabase();
        zoneListRecyclerViewAdapter = new ZoneListRecyclerViewAdapter(zoneCardItems, getContext());
        zonesListRecyclerView.setAdapter(zoneListRecyclerViewAdapter);

        if (isFarmerAccount){
            observeZonesInFirebase();
        }

        if (view != null) {
            // Access the view and perform any necessary modifications
            if (zoneCardItems.size() > 0) {
                emptyZonesMessageTextView = view.findViewById(R.id.zones_label);
                emptyZonesMessageTextView.setVisibility(View.GONE);
            }else {
                emptyZonesMessageTextView = view.findViewById(R.id.zones_label);
                emptyZonesMessageTextView.setVisibility(View.VISIBLE);
            }
        }

        addClickEventOnZoneCards();
        zoneListRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void addClickEventOnZoneCards(){
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
                    startActivityForResult(intent, ZONE_DELETE_REQUEST_CODE);
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ZONE_DELETE_REQUEST_CODE && requestCode == Activity.RESULT_OK){
            new MainActivity().tabLayout.getTabAt(2).select();
        }
    }

    private List<ZoneCardItem> getZonesFromDatabase(){
        List<ZoneCardItem> listOfZoneCardItems = new ArrayList<>();
        String [] columnsToPick = {"remote_id","zoneName", "location", "createTime", "status", "owner"};
        Cursor cursor = sqLiteDatabase.query("zones", columnsToPick, null, null, null, null, null);

        if (cursor.moveToNext()) {
            do {
                @SuppressLint("Range") String remote_id = cursor.getString(cursor.getColumnIndex("remote_id"));
                @SuppressLint("Range") String zoneName = cursor.getString(cursor.getColumnIndex("zoneName"));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex("location"));
                @SuppressLint("Range") String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));
                @SuppressLint("Range") String owner = cursor.getString(cursor.getColumnIndex("owner"));

                if (!TextUtils.isEmpty(zoneName) || !TextUtils.isEmpty(location)) {
                    ZoneCardItem zoneCardItem = new ZoneCardItem(remote_id, zoneName, location, createTime, status, owner);
                    listOfZoneCardItems.add(zoneCardItem);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listOfZoneCardItems;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isFarmerAccount){
            realTimeZonesObserver.stopListening();
        }
    }
}