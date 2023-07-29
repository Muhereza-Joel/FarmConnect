package com.moels.farmconnect.model.command;

import android.content.Context;

import com.moels.farmconnect.model.database.ZonesDatabase;
import com.moels.farmconnect.model.database.ZonesDatabaseHelper;
import com.moels.farmconnect.model.observers.Observer;

import java.util.List;

public class EditZoneCommand implements Command{
    private ZonesDatabase zonesDatabase;
    private Observer observer;
    private List<String> zoneDetails;
    private String id;

    public EditZoneCommand(Context context,String zoneID, List<String> zoneDetails, Observer observer) {
        this.observer = observer;
        this.zoneDetails = zoneDetails;
        this.id = zoneID;
        zonesDatabase = ZonesDatabaseHelper.getInstance(context);
    }

    @Override
    public void execute() {
        zonesDatabase.registerObserver(observer);
        boolean zoneIsUpdated = zonesDatabase.updateZone(id, zoneDetails);
        if (zoneIsUpdated) zonesDatabase.removeObserver(observer);
    }
}
