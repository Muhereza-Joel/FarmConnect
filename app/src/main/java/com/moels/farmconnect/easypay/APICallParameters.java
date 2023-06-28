package com.moels.farmconnect.easypay;

import java.io.Serializable;

public class APICallParameters implements Serializable {
    public String transactionAmount;
    public String recipientPhoneNumber;
    public String transactionCurrency;
    public String requestAction;
    public String postUrl;
    public String APIClientId;
    public String APIClientSecret;
    public String reference;
    public String reason;

    public APICallParameters(){}
}
