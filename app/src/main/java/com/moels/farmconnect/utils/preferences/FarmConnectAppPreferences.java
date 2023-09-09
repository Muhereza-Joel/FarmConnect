package com.moels.farmconnect.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class FarmConnectAppPreferences implements Preferences{
    private static FarmConnectAppPreferences uniqueInstance;
    private Context context;
    private final SharedPreferences sharedPreferences;

    private FarmConnectAppPreferences(Context context) {
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static FarmConnectAppPreferences getInstance(Context context){
        if (uniqueInstance == null){
            uniqueInstance = new FarmConnectAppPreferences(context);
        }
        return uniqueInstance;
    }

    @Override
    public boolean isFarmerAccount() {
        return sharedPreferences.getBoolean("farmerAccountTypeChosen", false);
    }

    @Override
    public boolean isBuyerAccount() {
        return  sharedPreferences.getBoolean("buyerAccountTypeChosen", false);
    }

    @Override
    public boolean userIsAuthenticated() {
        return sharedPreferences.getBoolean("phoneNumberAuthenticated", true);
    }

    @Override
    public boolean userProfileIsCreated() {
        return sharedPreferences.getBoolean("profileCreated", false);
    }

    @Override
    public boolean userAccountIsChosen() {
        return sharedPreferences.getBoolean("userAccountChosen", false);
    }

    @Override
    public boolean contactListIsBuilt() {
        return sharedPreferences.getBoolean("contactListFetched", false);
    }

    @Override
    public String getAuthenticatedPhoneNumber() {
        return sharedPreferences.getString("authenticatedPhoneNumber", "123456789");
    }

    @Override
    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();

    }

    @Override
    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    @Override
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public String getString(String key) {
        return sharedPreferences.getString(key, " ");
    }
}
