package com.moels.farmconnect.model.command;

import android.content.Context;

import com.moels.farmconnect.model.database.ZonesTable;
import com.moels.farmconnect.model.database.ZonesTableUtil;

public class DeleteZoneCommand implements Command{
    private ZonesTable zonesDatabase;
    private String zoneID;

    public DeleteZoneCommand(Context context, String zoneID) {
        zonesDatabase = ZonesTableUtil.getInstance(context);
        this.zoneID = zoneID;
    }

    @Override
    public void execute() {
        zonesDatabase.deleteZoneFromDatabase(zoneID);
    }
}
