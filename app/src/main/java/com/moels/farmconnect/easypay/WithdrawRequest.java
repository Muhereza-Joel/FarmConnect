package com.moels.farmconnect.easypay;

import android.app.Activity;
import android.content.Intent;

import com.moels.farmconnect.activities.MakeWithdrawRequestActivity;

public class WithdrawRequest {
    private Activity activity;
    public String amountToWithdraw;
    public String recipientPhoneNumber;
    public String transactionCurrency;
    public String transactionReference;
    private String requestAction;
    public String postUrl;
    public String APIClientID;
    public String APIClientSecret;
    public String paymentReason;
    public static String EASY_PAY_PARAMS="parameters";
    public static int EP_REQUEST_CODE =120;

    public WithdrawRequest(Activity activity) {
        this.activity = activity;
    }

    public WithdrawRequest setAmountToWithdraw(String amountToWithdraw) {
        this.amountToWithdraw = amountToWithdraw;
        return this;
    }

    public WithdrawRequest setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
        return this;
    }

    public WithdrawRequest setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
        return this;
    }

    public WithdrawRequest setRequestAction(String requestAction) {
        this.requestAction = requestAction;
        return this;
    }

    public WithdrawRequest setPostUrl(String postUrl) {
        this.postUrl = postUrl;
        return this;
    }

    public WithdrawRequest setPaymentReason(String paymentReason) {
        this.paymentReason = paymentReason;
        return this;
    }

    public WithdrawRequest setAPIClientID(String APIClientID) {
        this.APIClientID = APIClientID;
        return this;
    }

    public WithdrawRequest setAPIClientSecret(String ClientSecret) {
        this.APIClientSecret = ClientSecret;
        return this;
    }

    public void initialize() {
        if (activity != null) {
            APICallParameters apiWithdrawCallParameters=new APICallParameters();
            apiWithdrawCallParameters.transactionAmount = amountToWithdraw;
            apiWithdrawCallParameters.recipientPhoneNumber = recipientPhoneNumber;
            apiWithdrawCallParameters.transactionCurrency = transactionCurrency;
            apiWithdrawCallParameters.postUrl = postUrl;
            apiWithdrawCallParameters.requestAction = requestAction;
            apiWithdrawCallParameters.APIClientId = APIClientID;
            apiWithdrawCallParameters.APIClientSecret = APIClientSecret;
            apiWithdrawCallParameters.reference = transactionReference;
            apiWithdrawCallParameters.reason = paymentReason;

            Intent intent = new Intent(activity, MakeWithdrawRequestActivity.class);
            intent.putExtra(EASY_PAY_PARAMS, apiWithdrawCallParameters);
            activity.startActivityForResult(intent, EP_REQUEST_CODE);
        }
    }
}
