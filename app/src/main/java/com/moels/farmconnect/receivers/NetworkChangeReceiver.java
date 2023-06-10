package com.moels.farmconnect.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {

    public static NetworkChangeListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                listener.onWifiConnected();
            }else {
                if (listener != null){
                    listener.onNetworkConnected();
                }
            }

        } else{
            if (listener != null){
                listener.onNetworkDisconnected();
            }
        }
    }

    public static void setListener(NetworkChangeListener listener) {
        NetworkChangeReceiver.listener = listener;
    }

    public interface NetworkChangeListener{
        void onNetworkConnected();
        void onWifiConnected();
        void onNetworkDisconnected();
    }
}