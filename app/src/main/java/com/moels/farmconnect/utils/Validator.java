package com.moels.farmconnect.utils;

import com.moels.farmconnect.utils.models.Zone;

import java.util.List;

public abstract class Validator {
    public static Validator getInstance() {
        return new DataValidator();
    }

    public abstract boolean validateZoneDetails(Zone zone);
}
