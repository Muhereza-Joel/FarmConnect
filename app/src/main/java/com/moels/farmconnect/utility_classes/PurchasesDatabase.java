package com.moels.farmconnect.utility_classes;

import com.moels.farmconnect.models.PurchasesCard;

import java.util.List;

public interface PurchasesDatabase {
    boolean addPurchaseRecord(List<String> purchaseDetails);
    List<PurchasesCard> getPurchases(String zoneID);
    List<String> getPurchaseDetails(String purchaseID);
}
