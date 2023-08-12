package com.moels.farmconnect.model.database;

import com.moels.farmconnect.utils.models.PurchasesCard;

import java.util.List;

public interface PurchasesTable {
    boolean addPurchaseRecord(List<String> purchaseDetails);
    List<PurchasesCard> getPurchases(String zoneID);
    List<String> getPurchaseDetails(String purchaseID);
}
