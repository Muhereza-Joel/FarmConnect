package com.moels.farmconnect.utils.models;

import java.util.HashMap;
import java.util.List;

public class Zone {
    private String zoneID, zoneName, zoneLocation, productsToCollect, description, owner, date, time, status, uploadStatus, updatedStatus;

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

    public void setZoneID(String zoneID) {
        this.zoneID = zoneID;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public void setZoneLocation(String zoneLocation) {
        this.zoneLocation = zoneLocation;
    }

    public void setProductsToCollect(String productsToCollect) {
        this.productsToCollect = productsToCollect;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public void setUpdatedStatus(String updatedStatus) {
        this.updatedStatus = updatedStatus;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public String getUpdatedStatus() {
        return updatedStatus;
    }
}
