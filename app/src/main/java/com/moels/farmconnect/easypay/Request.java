package com.moels.farmconnect.easypay;

import android.app.Activity;
import android.content.Intent;

import com.moels.farmconnect.activities.MakeDepositRequestActivity;
import com.moels.farmconnect.activities.MakeWithdrawRequestActivity;
import com.moels.farmconnect.utility_classes.FarmConnectAppPreferences;
import com.moels.farmconnect.utility_classes.Preferences;

public class Request {
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
    private Preferences preferences;

    public Request(Activity activity) {
        this.activity = activity;
        preferences = FarmConnectAppPreferences.getInstance(activity.getApplicationContext());
    }

    public Request setTransactionAmount(String amountToWithdraw) {
        this.amountToWithdraw = amountToWithdraw;
        return this;
    }

    public Request setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
        return this;
    }

    public Request setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
        return this;
    }

    public Request setRequestAction(String requestAction) {
        this.requestAction = requestAction;
        return this;
    }

    public Request setPostUrl(String postUrl) {
        this.postUrl = postUrl;
        return this;
    }

    public Request setPaymentReason(String paymentReason) {
        this.paymentReason = paymentReason;
        return this;
    }

    public Request setAPIClientID(String APIClientID) {
        this.APIClientID = APIClientID;
        return this;
    }

    public Request setAPIClientSecret(String ClientSecret) {
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

            if (preferences.isBuyerAccount()){
                Intent intent = new Intent(activity, MakeDepositRequestActivity.class);
                intent.putExtra(EASY_PAY_PARAMS, apiWithdrawCallParameters);
                activity.startActivityForResult(intent, EP_REQUEST_CODE);
            } else if (preferences.isFarmerAccount()) {
                Intent intent = new Intent(activity, MakeWithdrawRequestActivity.class);
                intent.putExtra(EASY_PAY_PARAMS, apiWithdrawCallParameters);
                activity.startActivityForResult(intent, EP_REQUEST_CODE);
            }

        }
    }
}
