package com.moels.farmconnect.command;

import android.content.Context;
import android.text.TextUtils;

import com.moels.farmconnect.utility_classes.ZonesDatabase;
import com.moels.farmconnect.utility_classes.ZonesDatabaseHelper;

import java.util.List;

public class SaveZoneCommand implements Command{

    private ZonesDatabase zonesDatabase;
    private Context context;
    private List<String> zoneDetails;
    private Listener listener;

    public SaveZoneCommand(Context context, List<String> zoneDetails, Listener listener){
        this.zoneDetails = zoneDetails;
        this.context = context;
        this.listener = listener;
        zonesDatabase = ZonesDatabaseHelper.getInstance(context);
    }

    @Override
    public void execute() {
        if (dataIsValid()){
            save();
        }
    }

    private void save(){
        boolean zoneIsSaved =  zonesDatabase.addZoneToDatabase(zoneDetails);
        if (zoneIsSaved){
            listener.onSuccess();
        }
    }

    private boolean dataIsValid(){
        boolean dataIsValid = true;
        if (TextUtils.isEmpty(zoneDetails.get(1))
                || TextUtils.isEmpty(zoneDetails.get(2))
                || TextUtils.isEmpty(zoneDetails.get(3))
                || TextUtils.isEmpty(zoneDetails.get(4))){
            dataIsValid = false;
            listener.onFailure();
        }
        return dataIsValid;
    }

    @Override
    public void undo() {

    }
}
