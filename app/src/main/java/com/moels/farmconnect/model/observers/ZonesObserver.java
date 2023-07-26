package com.moels.farmconnect.model.observers;

public interface ZonesObserver {
    void startListening(final RealTimeZonesObserver.OnZoneUpdateListener listener);
    void stopListening();
}
