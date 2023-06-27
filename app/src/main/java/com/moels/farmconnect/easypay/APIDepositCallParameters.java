package com.moels.farmconnect.easypay;

import java.io.Serializable;

public class APIDepositCallParameters implements Serializable {
    public String amountToDeposit;
    public String userPhone;
    public String currency="UGX";
    public String clientID;
    public String reference;
    public String ClientSecret;
    public String paymentReason;

    public APIDepositCallParameters(){}
}
