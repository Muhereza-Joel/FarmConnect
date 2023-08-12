package com.moels.farmconnect.model.database;

import android.content.Context;

import com.moels.farmconnect.model.observers.Observable;
import com.moels.farmconnect.utils.models.ZoneCardItem;

import java.util.List;

public interface ZonesTable extends  Observable {
    static ZonesTable getInstance(Context context) {
        return ZonesTableUtil.getInstance(context);
    }

    List<ZoneCardItem> getZonesFromDatabase();
    String getZoneOwner(String zoneID);
    List<String> getZoneIds(String phoneNumber);
    List<String> getZoneDetails(String zoneID);
    List<String> getZonesToUpload();
    boolean addZoneToDatabase(List<String> zoneDetails);
    boolean updateZone(String zoneID, List<String> zoneDetails);
    boolean updateZoneUploadStatus(String zoneID, boolean uploadedStatus);
    void deleteZoneFromDatabase(String _id);

}
