package com.moels.farmconnect.utils.models;

import java.util.HashMap;
import java.util.List;

public class Zone {
    private String zoneID, zoneName, zoneLocation, productsToCollect, description, owner, date, time, status;

    public Zone(){}

    public Zone(String zoneID,
                String zoneName,
                String zoneLocation,
                String productsToCollect,
                String description,
                String owner,
                String date,
                String time,
                String status
    ) {
        this.zoneID = zoneID;
        this.zoneName = zoneName;
        this.zoneLocation = zoneLocation;
        this.productsToCollect = productsToCollect;
        this.description = description;
        this.owner = owner;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getZoneID() {
        return zoneID;
    }

    public String getZoneName() {
        return zoneName;
    }

    public String getZoneLocation() {
        return zoneLocation;
    }

    public String getProductsToCollect() {
        return productsToCollect;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

}
