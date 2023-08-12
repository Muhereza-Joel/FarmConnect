package com.moels.farmconnect.model.command;

import android.content.Context;

import com.moels.farmconnect.model.database.ZonesTable;
import com.moels.farmconnect.model.database.ZonesTableUtil;
import com.moels.farmconnect.model.observers.Observer;

import java.util.List;

public class SaveZoneCommand implements Command{

    private ZonesTable zonesDatabase;
    private Observer observer;
    private List<String> zoneDetails;

    public SaveZoneCommand(Context context, List<String> zoneDetails, Observer observer){
        this.zoneDetails = zoneDetails;
        this.observer = observer;
        zonesDatabase = ZonesTableUtil.getInstance(context);
    }

    @Override
    public void execute() {
        zonesDatabase.registerObserver(observer);
        boolean zoneIsSaved =  zonesDatabase.addZoneToDatabase(zoneDetails);
        if (zoneIsSaved) zonesDatabase.removeObserver(observer);
    }

}
