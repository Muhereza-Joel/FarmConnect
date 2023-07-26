package com.moels.farmconnect.model.observers;

public interface ProductsObserver {
    void startListening(final RealTimeProductsObserver.OnProductUpdateListener listener);
    void stopListening();
}
