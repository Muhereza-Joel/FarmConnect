package com.moels.farmconnect.utils;

import android.text.TextUtils;

import com.moels.farmconnect.utils.models.Zone;

import java.util.List;

public final class DataValidator extends Validator{

    @Override
    public boolean validateZoneDetails(Zone zone) {
        boolean dataIsValid = true;
        if (TextUtils.isEmpty(zone.getZoneName())
                || TextUtils.isEmpty(zone.getZoneLocation())
                || TextUtils.isEmpty(zone.getProductsToCollect())
                || TextUtils.isEmpty(zone.getDescription())){
            dataIsValid = false;
        }
        return dataIsValid;
    }
}
