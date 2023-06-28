package com.moels.farmconnect.utility_classes;

public interface ProductsObserver {
    void startListening(final RealTimeProductsObserver.OnProductUpdateListener listener);
    void stopListening();
}
