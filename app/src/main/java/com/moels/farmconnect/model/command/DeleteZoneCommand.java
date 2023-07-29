package com.moels.farmconnect.model.command;

import android.content.Context;

import com.moels.farmconnect.model.database.ZonesDatabase;
import com.moels.farmconnect.model.database.ZonesDatabaseHelper;

public class DeleteZoneCommand implements Command{
    private ZonesDatabase zonesDatabase;
    private String zoneID;

    public DeleteZoneCommand(Context context, String zoneID) {
        zonesDatabase = ZonesDatabaseHelper.getInstance(context);
        this.zoneID = zoneID;
    }

    @Override
    public void execute() {
        zonesDatabase.deleteZoneFromDatabase(zoneID);
    }
}
