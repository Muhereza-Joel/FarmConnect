package com.moels.farmconnect.model.command;

import android.content.Context;

import com.moels.farmconnect.model.database.ZonesTable;
import com.moels.farmconnect.model.observers.Observer;
import com.moels.farmconnect.utils.models.Zone;

import java.util.List;

public class EditZoneCommand implements Command{
    private ZonesTable zonesTable;
    private Observer observer;
    private Zone zone;
    private String id;

    public EditZoneCommand(Context context, String zoneID, Zone zone, Observer observer) {
        this.observer = observer;
        this.zone = zone;
        this.id = zoneID;
        zonesTable = ZonesTable.getInstance(context);
    }

    @Override
    public void execute() {
        zonesTable.registerObserver(observer);
        boolean zoneIsUpdated = zonesTable.updateZone(id, zone);
        if (zoneIsUpdated) zonesTable.removeObserver(observer);
    }
}
