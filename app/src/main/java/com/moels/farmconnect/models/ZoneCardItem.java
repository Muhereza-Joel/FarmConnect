package com.moels.farmconnect.models;

public class ZoneCardItem {

    private String zoneName;
    private String location;

    public ZoneCardItem(String zoneName, String location) {
        this.zoneName = zoneName;
        this.location = location;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
