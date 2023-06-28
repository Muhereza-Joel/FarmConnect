package com.moels.farmconnect.utility_classes;

import android.content.Context;
import android.content.SharedPreferences;

public class FarmConnectPreferences implements Preferences{
    private static FarmConnectPreferences uniqueInstance;
    private Context context;
    private final SharedPreferences sharedPreferences;

    private FarmConnectPreferences(Context context) {
      sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
    }

    public static FarmConnectPreferences getInstance(Context context){
        if (uniqueInstance == null){
            uniqueInstance = new FarmConnectPreferences(context);
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
}
