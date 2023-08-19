package com.moels.farmconnect.view.fragments;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.moels.farmconnect.utils.preferences.Globals;
import com.moels.farmconnect.view.activities.AddProductToZoneActivity;
import com.moels.farmconnect.view.activities.MainActivity;
import com.moels.farmconnect.view.activities.ProductsInAzoneActivity;
import com.moels.farmconnect.view.adapters.ZoneListRecyclerViewAdapter;
import com.moels.farmconnect.utils.models.Zone;
import com.moels.farmconnect.utils.models.ZoneCardItem;
import com.moels.farmconnect.model.database.ContactsTable;
import com.moels.farmconnect.model.database.ContactsTableUtil;
import com.moels.farmconnect.utils.preferences.FarmConnectAppPreferences;
import com.moels.farmconnect.utils.preferences.Preferences;
import com.moels.farmconnect.model.observers.RealTimeZonesObserver;
import com.moels.farmconnect.model.database.ZonesTable;
import com.moels.farmconnect.model.database.ZonesTableUtil;
import com.moels.farmconnect.model.observers.ZonesObserver;

import java.util.List;

public class ZonesListFragment extends Fragment {

    private static final int ZONE_DELETE_REQUEST_CODE = 2;
    private RecyclerView zonesListRecyclerView;
    private ZoneListRecyclerViewAdapter zoneListRecyclerViewAdapter;
    private ZonesTable zonesDatabase;
    private ContactsTable contactsDatabase;
    private List<ZoneCardItem> zoneCardItems;
    private View view;
    private TextView emptyZonesMessageTextView;
    private Preferences preferences;
    private ZonesObserver zonesObserver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zonesDatabase = ZonesTableUtil.getInstance(getContext());
        contactsDatabase = ContactsTableUtil.getInstance(getContext());
        preferences = FarmConnectAppPreferences.getInstance(getContext());
        zoneCardItems = zonesDatabase.getZonesFromDatabase();

        if (preferences.isFarmerAccount()){
            observeZonesInFirebase();
        }

    }

    public void observeZonesInFirebase(){
        Log.d("FarmConnect", "ZonesFireBaseObserver: Observer is running");
        zonesObserver = RealTimeZonesObserver.getInstance(contactsDatabase.getAllRegisteredContacts());
        zonesObserver.startListening(new RealTimeZonesObserver.OnZoneUpdateListener() {
            @Override
            public void onZoneAdded(Zone zone) {
                if (zone != null){
                    zone.setUploadStatus(Globals.UploadStatus.TRUE.toString());
                    zone.setUpdatedStatus(Globals.UpdateStatus.FALSE.toString());

                    zonesDatabase.addZoneToDatabase(zone);

                    zoneCardItems = zonesDatabase.getZonesFromDatabase();
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

                    zonesDatabase.updateZone(zoneID, zone);
                    zoneCardItems = zonesDatabase.getZonesFromDatabase();
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
                zonesDatabase.deleteZoneFromDatabase(zoneID);
                zoneCardItems = zonesDatabase.getZonesFromDatabase();
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
        zoneCardItems = zonesDatabase.getZonesFromDatabase();
        zoneListRecyclerViewAdapter = new ZoneListRecyclerViewAdapter(zoneCardItems, getContext());
        zonesListRecyclerView.setAdapter(zoneListRecyclerViewAdapter);
//        scrollRecycleViewToBottom(zonesListRecyclerView);

        if (preferences.isFarmerAccount()){
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

    private void scrollRecycleViewToBottom(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVisibleItemPosition = layoutManager != null ? layoutManager.findLastVisibleItemPosition() : 0;
        int itemCount = layoutManager != null ? layoutManager.getItemCount() : 0;

        if (lastVisibleItemPosition != (itemCount - 1) || recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() > recyclerView.getHeight()) {
            recyclerView.scrollToPosition(itemCount - 1);
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (preferences.isFarmerAccount()){
            zonesObserver.stopListening();
        }
    }
}