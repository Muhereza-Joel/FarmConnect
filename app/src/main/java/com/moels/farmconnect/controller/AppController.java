package com.moels.farmconnect.controller;


import com.moels.farmconnect.model.command.Command;
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

    public void save(List<String> zoneDetails){
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
    public void update(Object object) {
        listener.onSuccess();
    }

}
