package com.moels.farmconnect.controller;


import android.content.Context;
import android.text.TextUtils;

import com.moels.farmconnect.model.command.Command;
import com.moels.farmconnect.model.command.Listener;
import com.moels.farmconnect.model.command.SaveZoneCommand;
import com.moels.farmconnect.model.observers.Observer;
import com.moels.farmconnect.view.activities.AddNewZoneActivity;

import java.util.List;

public class ZonesController implements Observer {
    private final List<String> zoneDetails;
    private final Context context;
    private final Listener listener;

    public ZonesController(Context context, List<String> zoneDetails, Listener listener) {
        this.zoneDetails = zoneDetails;
        this.context = context;
        this.listener = listener;
    }

    public void saveZone(){
        if (dataIsValid()){
            Command saveZoneCommand = new SaveZoneCommand(context, zoneDetails, this);
            saveZoneCommand.execute();
        }else {
            listener.onFailure();
        }
    }

    private boolean dataIsValid(){
        boolean dataIsValid = true;
        if (TextUtils.isEmpty(zoneDetails.get(1))
                || TextUtils.isEmpty(zoneDetails.get(2))
                || TextUtils.isEmpty(zoneDetails.get(3))
                || TextUtils.isEmpty(zoneDetails.get(4))){
            dataIsValid = false;
        }
        return dataIsValid;
    }

    @Override
    public void update(Object object) {
        listener.onSuccess();
    }
}
