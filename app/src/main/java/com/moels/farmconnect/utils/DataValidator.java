package com.moels.farmconnect.utils;

import android.text.TextUtils;

import java.util.List;

public final class DataValidator extends Validator{

    @Override
    public boolean validateZoneDetails(List<String> zoneDetails) {
        boolean dataIsValid = true;
        if (TextUtils.isEmpty(zoneDetails.get(0))
                || TextUtils.isEmpty(zoneDetails.get(1))
                || TextUtils.isEmpty(zoneDetails.get(2))
                || TextUtils.isEmpty(zoneDetails.get(3))){
            dataIsValid = false;
        }
        return dataIsValid;
    }
}
