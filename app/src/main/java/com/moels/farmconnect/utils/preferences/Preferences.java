package com.moels.farmconnect.utils.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.os.IBinder;

import java.lang.reflect.GenericArrayType;

public interface Preferences {

    String FIRST_ZONE_CREATED = "FirstZoneCreated";
    static Preferences getInstance(Context context){
        return FarmConnectAppPreferences.getInstance(context);
    }
    boolean isFarmerAccount();
    boolean isBuyerAccount();
    boolean userIsAuthenticated();
    boolean userProfileIsCreated();
    boolean userAccountIsChosen();
    boolean contactListIsBuilt();
    String getAuthenticatedPhoneNumber();
    void putBoolean(String key, boolean value);
    boolean getBoolean(String key);
    void putString(String key, String value);
    String getString(String key);

}
