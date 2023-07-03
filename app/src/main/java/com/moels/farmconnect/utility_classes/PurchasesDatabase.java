package com.moels.farmconnect.utility_classes;

import java.util.List;

public interface PurchasesDatabase {
    boolean addPurchaseRecord(List<String> purchaseDetails);
    List<String> getPurchases(String zoneID);
    List<String> getPurchaseDetails(String purchaseID);
}
