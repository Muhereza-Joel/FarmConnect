package com.moels.farmconnect.model.command;

import android.content.Context;
import android.text.TextUtils;

import com.moels.farmconnect.model.database.ZonesDatabase;
import com.moels.farmconnect.model.database.ZonesDatabaseHelper;
import com.moels.farmconnect.model.observers.Observer;

import java.util.List;

public class SaveZoneCommand implements Command{

    private ZonesDatabase zonesDatabase;
    private Observer observer;
    private Context context;
    private List<String> zoneDetails;

    public SaveZoneCommand(Context context, List<String> zoneDetails, Observer observer){
        this.zoneDetails = zoneDetails;
        this.context = context;
        this.observer = observer;
        zonesDatabase = ZonesDatabaseHelper.getInstance(context);
    }

    @Override
    public void execute() {
        zonesDatabase.registerObserver(observer);
        boolean zoneIsSaved =  zonesDatabase.addZoneToDatabase(zoneDetails);
        if (zoneIsSaved) zonesDatabase.removeObserver(observer);
    }

}
