package com.moels.farmconnect.utils.preferences;

public interface Preferences {

    boolean isFarmerAccount();
    boolean isBuyerAccount();
    boolean userIsAuthenticated();
    boolean userProfileIsCreated();
    String getAuthenticatedPhoneNumber();
    void putBoolean(String key, boolean value);
    boolean getBoolean(String key);
    void putString(String key, String value);
    String getString(String key);
}
