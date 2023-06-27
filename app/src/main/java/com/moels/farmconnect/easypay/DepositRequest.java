package com.moels.farmconnect.easypay;

import android.app.Activity;
import android.content.Intent;

import com.moels.farmconnect.activities.MakeDepositRequestActivity;
public class DepositRequest {
    private Activity activity;
    public String amountToPay;
    public String userPhone;
    public String transactionCurrency;
    public String APIClientID;
    public String APIClientSecret;
    public String paymentReason;
    public static String EASY_PAY_PARAMS="parameters";
    public static int EP_REQUEST_CODE =120;

    public DepositRequest(Activity activity) {
        this.activity = activity;
    }

    public DepositRequest setAmountToPay(String amountToPay) {
        this.amountToPay = amountToPay;
        return this;
    }

    public DepositRequest setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
        return this;
    }

    public DepositRequest setPaymentReason(String paymentReason) {
        this.paymentReason = paymentReason;
        return this;
    }

    public DepositRequest setAPIClientID(String APIClientID) {
        this.APIClientID = APIClientID;
        return this;
    }

    public DepositRequest setAPIClientSecret(String ClientSecret) {
        this.APIClientSecret = ClientSecret;
        return this;
    }

    public void initialize() {
        if (activity != null) {
            APIDepositCallParameters apiDepositCallParameters=new APIDepositCallParameters();
            apiDepositCallParameters.amountToDeposit =amountToPay;;
            apiDepositCallParameters.userPhone=userPhone;;
            apiDepositCallParameters.currency= transactionCurrency;;
            apiDepositCallParameters.clientID= APIClientID;;
            apiDepositCallParameters.ClientSecret= APIClientSecret;;
            Intent intent = new Intent(activity, MakeDepositRequestActivity.class);
            intent.putExtra(EASY_PAY_PARAMS, apiDepositCallParameters);
            activity.startActivityForResult(intent, EP_REQUEST_CODE);
        }
    }
}
