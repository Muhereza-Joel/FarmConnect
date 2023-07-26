package com.moels.farmconnect.model.database;

import android.content.ContentValues;

import com.moels.farmconnect.utils.models.ZoneCardItem;

import java.util.List;

public interface ZonesDatabase {
    List<ZoneCardItem> getZonesFromDatabase();
    String getZoneOwner(String zoneID);
    List<String> getZoneIds(String phoneNumber);
    List<String> getZoneDetails(String zoneID);
    List<String> getZonesToUpload();
    boolean addZoneToDatabase(List<String> zoneDetails);
    boolean updateZone(String zoneID, ContentValues contentValues);
    boolean updateZoneUploadStatus(String zoneID, boolean uploadedStatus);
    void deleteZoneFromDatabase(String _id);

}
