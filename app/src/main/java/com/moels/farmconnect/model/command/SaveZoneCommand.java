package com.moels.farmconnect.model.command;

import android.content.Context;

import com.moels.farmconnect.model.database.ZonesTable;
import com.moels.farmconnect.model.observers.Observer;
import com.moels.farmconnect.utils.models.Zone;

public class SaveZoneCommand implements Command{

    private ZonesTable zonesTable;
    private Observer observer;
    private Zone zone;

    public SaveZoneCommand(Context context, Zone zone, Observer observer){
        this.zone = zone;
        this.observer = observer;
        zonesTable = ZonesTable.getInstance(context);
    }

    @Override
    public void execute() {
        zonesTable.registerObserver(observer);
        boolean zoneIsSaved =  zonesTable.addZoneToDatabase(zone);
        if (zoneIsSaved) zonesTable.removeObserver(observer);
    }

}
