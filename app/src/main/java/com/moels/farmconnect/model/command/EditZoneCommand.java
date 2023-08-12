package com.moels.farmconnect.model.command;

import android.content.Context;

import com.moels.farmconnect.model.database.ZonesTable;
import com.moels.farmconnect.model.observers.Observer;

import java.util.List;

public class EditZoneCommand implements Command{
    private ZonesTable zonesTable;
    private Observer observer;
    private List<String> zoneDetails;
    private String id;

    public EditZoneCommand(Context context,String zoneID, List<String> zoneDetails, Observer observer) {
        this.observer = observer;
        this.zoneDetails = zoneDetails;
        this.id = zoneID;
        zonesTable = ZonesTable.getInstance(context);
    }

    @Override
    public void execute() {
        zonesTable.registerObserver(observer);
        boolean zoneIsUpdated = zonesTable.updateZone(id, zoneDetails);
        if (zoneIsUpdated) zonesTable.removeObserver(observer);
    }
}
