package com.moels.farmconnect.utility_classes;

public interface ZonesObserver {
    void startListening(final RealTimeZonesObserver.OnZoneUpdateListener listener);
    void stopListening();
}
