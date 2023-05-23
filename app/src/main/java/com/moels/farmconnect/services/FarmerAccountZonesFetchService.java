package com.moels.farmconnect.services;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

public class FarmerAccountZonesFetchService extends Service {
    private SQLiteDatabase sqLiteDatabase;
    public FarmerAccountZonesFetchService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void getMatchedContactsFromDatabase(){

    }
}