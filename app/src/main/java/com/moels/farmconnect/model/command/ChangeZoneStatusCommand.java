package com.moels.farmconnect.model.command;

import android.content.Context;

import com.moels.farmconnect.model.database.ZonesTable;
import com.moels.farmconnect.model.observers.Observer;

public class ChangeZoneStatusCommand implements Command{

    private String zoneID, status;
    private Observer observer;
    private ZonesTable zonesTable;

    public ChangeZoneStatusCommand(Context context, String zoneID, String status, Observer observer){
        this.zoneID = zoneID;
        this.status = status;
        this.observer = observer;
        zonesTable = ZonesTable.getInstance(context);
    }

    @Override
    public void execute() {
        zonesTable.registerObserver(observer);
        zonesTable.changeZoneStatus(zoneID, status);
        zonesTable.removeObserver(observer);
    }
}
