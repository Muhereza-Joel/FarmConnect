package com.moels.farmconnect.models;

public class ZoneCardItem {

    private String _id, zoneName, location, createTime, status;

    public ZoneCardItem(String _id, String zoneName, String location, String createTime, String status) {
        this._id = _id;
        this.zoneName = zoneName;
        this.location = location;
        this.createTime = createTime;
        this.status = status;
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

    public String getCreateTime() {
        return createTime;
    }
    public String getStatus() {
        return status;
    }

    public String get_id() {
        return _id;
    }
}
