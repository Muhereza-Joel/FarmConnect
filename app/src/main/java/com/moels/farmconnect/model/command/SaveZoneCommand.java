package com.moels.farmconnect.model.command;

import android.content.Context;

import com.moels.farmconnect.model.database.ZonesTable;
import com.moels.farmconnect.model.database.ZonesTableUtil;
import com.moels.farmconnect.model.observers.Observer;

import java.util.List;

public class SaveZoneCommand implements Command{

    private ZonesTable zonesTable;
    private Observer observer;
    private List<String> zoneDetails;

    public SaveZoneCommand(Context context, List<String> zoneDetails, Observer observer){
        this.zoneDetails = zoneDetails;
        this.observer = observer;
        zonesTable = ZonesTable.getInstance(context);
    }

    @Override
    public void execute() {
        zonesTable.registerObserver(observer);
        boolean zoneIsSaved =  zonesTable.addZoneToDatabase(zoneDetails);
        if (zoneIsSaved) zonesTable.removeObserver(observer);
    }

}
