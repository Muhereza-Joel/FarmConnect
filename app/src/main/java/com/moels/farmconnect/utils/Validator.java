package com.moels.farmconnect.utils;

import java.util.List;

public abstract class Validator {
    public static Validator getInstance() {
        return new DataValidator();
    }

    public abstract boolean validateZoneDetails(List<String> zoneDetails);
}
