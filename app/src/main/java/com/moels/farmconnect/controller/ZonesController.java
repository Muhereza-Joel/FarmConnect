package com.moels.farmconnect.controller;


import android.annotation.SuppressLint;

import com.moels.farmconnect.model.command.ChangeProductStatusCommand;
import com.moels.farmconnect.model.command.ChangeZoneStatusCommand;
import com.moels.farmconnect.model.command.Command;
import com.moels.farmconnect.model.command.DeleteZoneCommand;
import com.moels.farmconnect.model.command.EditZoneCommand;
import com.moels.farmconnect.model.command.SaveZoneCommand;
import com.moels.farmconnect.model.database.ZonesTable;
import com.moels.farmconnect.model.database.ZonesTableUtil;
import com.moels.farmconnect.model.observers.Observer;
import com.moels.farmconnect.utils.Validator;
import com.moels.farmconnect.utils.models.Zone;

import java.util.ArrayList;
import java.util.List;

public final class ZonesController extends Controller implements Observer {

    private ZonesController(){}
    @SuppressLint("StaticFieldLeak")
    private static ZonesController uniqueInstance;
    public static ZonesController getInstance(){
        if (uniqueInstance == null){
            uniqueInstance = new ZonesController();
        }
        return uniqueInstance;
    }

    public void saveZone(Zone zone){
        boolean dataIsValid = Validator.getInstance().validateZoneDetails(zone);
        Command saveZoneCommand = null;
        if (dataIsValid){
            saveZoneCommand = new SaveZoneCommand(context, zone, this);
            saveZoneCommand.execute();
        }else {
            listener.onFailure();
        }
    }

    public Zone getZoneDetails(String id) {
        ZonesTable zonesDatabase = ZonesTable.getInstance(context);
        return zonesDatabase.getZoneDetails(id);

    }

    public List<Zone> getAllZones(){
        return ZonesTable.getInstance(context).getAllZones();
    }

    public void changeZoneStatus(String zoneID, String status){
        Command changeZoneStatusCommand = new ChangeZoneStatusCommand(context, zoneID, status, this);
        changeZoneStatusCommand.execute();
    }

    public boolean updateZone(String id, Zone zone) {
        Command command = null;
        boolean zoneUpdated = false;
        boolean dataIsValid = Validator.getInstance().validateZoneDetails(zone);
        if (dataIsValid){
            command = new EditZoneCommand(context, id, zone, this);
            command.execute();
        } else {
            listener.onFailure();
        }

        return zoneUpdated;
    }

    public boolean deleteZone(String id) {
        Command command = new DeleteZoneCommand(context, id);
        command.execute();
        return true;
    }

    @Override
    public void update() {
        listener.onSuccess();
    }

}
