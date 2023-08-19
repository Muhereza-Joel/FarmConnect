package com.moels.farmconnect.model.database;

import android.content.Context;

import com.moels.farmconnect.model.observers.Observable;
import com.moels.farmconnect.utils.models.Zone;
import com.moels.farmconnect.utils.models.ZoneCardItem;

import java.util.List;

public interface ZonesTable extends  Observable {
    static ZonesTable getInstance(Context context) {
        return ZonesTableUtil.getInstance(context);
    }

    List<ZoneCardItem> getZonesFromDatabase();
    String getZoneOwner(String zoneID);
    List<String> getZoneIds(String phoneNumber);
    Zone getZoneDetails(String zoneID);
    List<String> getZonesToUpload();
    boolean addZoneToDatabase(Zone zone);
    boolean updateZone(String zoneID, Zone zone);
    boolean updateZoneUploadStatus(String zoneID, boolean uploadedStatus);
    void deleteZoneFromDatabase(String _id);
    void changeZoneStatus(String zoneID, String status);

}
