package com.moels.farmconnect.model.command;

import android.content.Context;

import com.moels.farmconnect.model.database.ZonesTable;

public class DeleteZoneCommand implements Command{
    private ZonesTable zonesTable;
    private String zoneID;

    public DeleteZoneCommand(Context context, String zoneID) {
        zonesTable = ZonesTable.getInstance(context);
        this.zoneID = zoneID;
    }

    @Override
    public void execute() {
        zonesTable.deleteZoneFromDatabase(zoneID);
    }
}
