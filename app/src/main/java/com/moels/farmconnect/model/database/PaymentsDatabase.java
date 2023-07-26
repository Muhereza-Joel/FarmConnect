package com.moels.farmconnect.model.database;

import com.moels.farmconnect.utils.models.PaymentCard;

import java.util.List;

public interface PaymentsDatabase {
    boolean addPaymentRecord(List<String> paymentDetails);
    List<PaymentCard> getPayments(String zoneID);
    List<String> getPaymentDetails(String paymentID);
}
