package com.moels.farmconnect.easypay;

import java.io.Serializable;

public class APIWithdrawCallParameters implements Serializable {
    public String amountToWithdraw;
    public String recipientPhoneNumber;
    public String transactionCurrency;
    public String postUrl;
    public String APIClientId;
    public String APIClientSecret;
    public String reference;
    public String withdrawReason;

    public APIWithdrawCallParameters(){}
}
