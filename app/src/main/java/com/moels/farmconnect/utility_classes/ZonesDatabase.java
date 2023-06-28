package com.moels.farmconnect.utility_classes;

import android.content.ContentValues;

import com.moels.farmconnect.models.ZoneCardItem;

import java.util.List;

public interface ZonesDatabase {
    List<ZoneCardItem> getZonesFromDatabase();
    String getZoneOwner(String zoneID);
    List<String> getZoneIds(String phoneNumber);
    List<String> getZoneDetails(String zoneID);
    boolean addZoneToDatabase(List<String> zoneDetails);
    boolean updateZone(String zoneID, ContentValues contentValues);
    void deleteZoneFromDatabase(String _id);

}
