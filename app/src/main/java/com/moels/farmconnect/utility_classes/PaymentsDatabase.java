package com.moels.farmconnect.utility_classes;

import java.util.List;

public interface PaymentsDatabase {
    boolean addPaymentRecord(List<String> paymentDetails);
    List<String> getPayments(String zoneID);
    List<String> getPaymentDetails(String paymentID);
}
