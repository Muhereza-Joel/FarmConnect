package com.moels.farmconnect.utility_classes;

import com.moels.farmconnect.models.PaymentCard;

import java.util.List;

public interface PaymentsDatabase {
    boolean addPaymentRecord(List<String> paymentDetails);
    List<PaymentCard> getPayments(String zoneID);
    List<String> getPaymentDetails(String paymentID);
}
