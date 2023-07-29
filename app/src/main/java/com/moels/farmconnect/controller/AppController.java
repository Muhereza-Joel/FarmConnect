package com.moels.farmconnect.controller;


import com.moels.farmconnect.model.command.Command;
import com.moels.farmconnect.model.command.EditZoneCommand;
import com.moels.farmconnect.model.command.SaveZoneCommand;
import com.moels.farmconnect.model.database.ZonesDatabase;
import com.moels.farmconnect.model.database.ZonesDatabaseHelper;
import com.moels.farmconnect.model.observers.Observer;
import com.moels.farmconnect.utils.Validator;

import java.util.List;

public final class AppController extends Controller implements Observer {
    public static Controller getInstance(){
        return new AppController();
    }

    public void saveZone(List<String> zoneDetails){
        boolean dataIsValid = Validator.getInstance().validateZoneDetails(zoneDetails);
        if (dataIsValid){
            Command saveZoneCommand = new SaveZoneCommand(context, zoneDetails, this);
            saveZoneCommand.execute();
        }else {
            listener.onFailure();
        }
    }

    @Override
    public List<String> getZoneDetails(String id) {
        ZonesDatabase zonesDatabase = ZonesDatabaseHelper.getInstance(context);
        return zonesDatabase.getZoneDetails(id);

    }

    @Override
    public boolean updateZone(String id, List<String> zoneDetails) {
        boolean zoneUpdated = false;
        boolean dataIsValid = Validator.getInstance().validateZoneDetails(zoneDetails);
        if (dataIsValid){
            Command command = new EditZoneCommand(context, id, zoneDetails, this);
            command.execute();
        } else {
            listener.onFailure();
        }

        return zoneUpdated;
    }

    @Override
    public void update(Object object) {
        listener.onSuccess();
    }

}
