package com.moels.farmconnect.easypay;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.moels.farmconnect.view.activities.MakeDepositRequestActivity;
import com.moels.farmconnect.view.activities.MakeWithdrawRequestActivity;
import com.moels.farmconnect.utils.preferences.FarmConnectAppPreferences;
import com.moels.farmconnect.database.PaymentsDatabase;
import com.moels.farmconnect.database.PaymentsDatabaseHelper;
import com.moels.farmconnect.utils.preferences.Preferences;
import com.moels.farmconnect.database.ProductsDatabase;
import com.moels.farmconnect.database.ProductsDatabaseHelper;
import com.moels.farmconnect.utils.UI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class APICall extends AsyncTask<String, String, String> {

    private String requestUrl;
    private String client_id;
    private String client_secret;
    private String phoneNumber;
    private String requestAction;
    private String amount;
    private String transactionReference;
    private String transactionCurrency;
    private String transactionReason;
    private Activity activity;
    private Preferences preferences;

    public APICall(){}

    public APICall setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    public APICall setClientID(String client_id) {
        this.client_id = client_id;
        return this;
    }

    public APICall setClientSecret(String client_secret) {
        this.client_secret = client_secret;
        return this;
    }

    public APICall setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public APICall setRequestAction(String requestAction) {
        this.requestAction = requestAction;
        return this;
    }

    public APICall setTransactionAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public APICall setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
        return this;
    }

    public APICall setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
        return this;
    }

    public APICall setTransactionReason(String transactionReason) {
        this.transactionReason = transactionReason;
        return this;
    }

    public APICall setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            final String POST_PARAMS = combinePostParameters();
            HttpURLConnection httpURLConnection = createHttpURLConnection();
            OutputStream outputStream = saveToOutPutStream(httpURLConnection, POST_PARAMS);
            StringBuffer response = getResponse(httpURLConnection);
            final JSONObject jsonObject = getJSONObject(response);
            boolean transactionWasSuccessful = checkTransactionStatus(jsonObject);

            if (transactionWasSuccessful){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            preferences = FarmConnectAppPreferences.getInstance(activity.getApplicationContext());

                            if (preferences.isBuyerAccount() && jsonObject != null){
                                MakeDepositRequestActivity.responseMessage.setVisibility(View.VISIBLE);
//                                MakeDepositRequestActivity.responseMessage.setText(response.toString());
                                MakeDepositRequestActivity.progressDialog.dismiss();
                                UI.displayToast(activity.getApplicationContext(), "Transaction was successful");
                                saveTransactionDetails(jsonObject);

                                Intent intent = new Intent();
                                intent.putExtra("response", response.toString());
                                activity.setResult(Activity.RESULT_OK, intent);
                                activity.finish();

                            } else if (preferences.isFarmerAccount() && jsonObject != null){
                                MakeWithdrawRequestActivity.responseMessage.setVisibility(View.VISIBLE);
                                MakeWithdrawRequestActivity.responseMessage.setText(response.toString());
                                MakeWithdrawRequestActivity.progressDialog.dismiss();

                                UI.displayToast(activity.getApplicationContext(), "Transaction was successful");
                                Log.e("FarmConnect: ", jsonObject.get("errormsg").toString());
                                Intent intent = new Intent();
                                intent.putExtra("response", response.toString());
                                activity.setResult(Activity.RESULT_OK, intent);
                                activity.finish();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            preferences = FarmConnectAppPreferences.getInstance(activity.getApplicationContext());
                            if (preferences.isBuyerAccount() && jsonObject != null){
                                MakeDepositRequestActivity.progressDialog.dismiss();
                                MakeDepositRequestActivity.responseMessage.setVisibility(View.VISIBLE);
                                MakeDepositRequestActivity.responseMessage.setText(jsonObject.get("errormsg").toString());
                                Log.e("FarmConnect: ", jsonObject.get("errormsg").toString());
                            } else if (preferences.isBuyerAccount()){
                                MakeDepositRequestActivity.progressDialog.dismiss();
                                MakeDepositRequestActivity.responseMessage.setVisibility(View.VISIBLE);
                                MakeDepositRequestActivity.responseMessage.setText("Transaction was not successfull");
                            }

                            if (preferences.isFarmerAccount() && jsonObject != null){
                                MakeWithdrawRequestActivity.progressDialog.dismiss();
                                MakeWithdrawRequestActivity.responseMessage.setVisibility(View.VISIBLE);
                                MakeWithdrawRequestActivity.responseMessage.setText(jsonObject.get("errormsg").toString());
                                 Log.e("FarmConnect: ", jsonObject.get("errormsg").toString());
                            } else if (preferences.isFarmerAccount()){
                                MakeWithdrawRequestActivity.progressDialog.dismiss();
                                MakeWithdrawRequestActivity.responseMessage.setVisibility(View.VISIBLE);
                                MakeWithdrawRequestActivity.responseMessage.setText("Transaction was not successfull");

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }

        } catch (Exception e){
            throw new RuntimeException(e);
        }

        return "";
    }

    private String combinePostParameters(){
        final String POST_PARAMS = "{" +
                "\"username\": \""+client_id+"\"," +
                "\"password\": \""+client_secret+"\"," +
                "\"action\": \""+requestAction+"\"," +
                "\"amount\": \"" + amount + "\"," +
                "\"currency\": \""+transactionCurrency+"\"," +
                "\"phone\": \"" + phoneNumber + "\"," +
                "\"reference\": \""+ transactionReference + "\"," +
                "\"reason\": \"" + transactionReason + "\"}";

        return POST_PARAMS;
    }

    private HttpURLConnection createHttpURLConnection(){
        HttpURLConnection postConnection = null;
        try {
            URL obj = new URL(requestUrl);
            postConnection = (HttpURLConnection) obj.openConnection();
            postConnection.setRequestMethod("POST");
            postConnection.setRequestProperty("Content-Type", "application/json");
            postConnection.setDoOutput(true);

        } catch (Exception e){
            throw new RuntimeException(e);
        }

        return postConnection;
    }


    private OutputStream saveToOutPutStream(HttpURLConnection httpURLConnection, String POST_PARAMS){
        OutputStream outputStream = null;
        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStream.write(POST_PARAMS.getBytes());
            outputStream.flush();
            outputStream.close();

        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return outputStream;
    }


    private StringBuffer getResponse(HttpURLConnection httpURLConnection){
        StringBuffer response = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();

        }catch (Exception e){
            e.getLocalizedMessage();
        }
        return response;
    }

    private JSONObject getJSONObject(StringBuffer response){
        JSONObject jsonObject = null;
        try {
            if (response != null){
                jsonObject = new JSONObject(String.valueOf(response));
            }
            
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

    private boolean checkTransactionStatus(JSONObject jsonObject){
        boolean transactionWasSuccessful = false;
        try {
            if (jsonObject != null){
                if(jsonObject.get("success").toString().equals("0")) {
                    transactionWasSuccessful = false;
                } else if (jsonObject.get("success").toString().equals("1")){
                    transactionWasSuccessful = true;
                }
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return transactionWasSuccessful;
    }

    private boolean saveTransactionDetails(JSONObject jsonObject){
            boolean transactionDetailsSaved = false;
        if (jsonObject != null) {
            PaymentsDatabase paymentsDatabase = PaymentsDatabaseHelper.getInstance(activity.getApplicationContext());
            ProductsDatabase productsDatabase = ProductsDatabaseHelper.getInstance(activity.getApplicationContext());
            List<String> paymentDetails = new ArrayList<>();
            paymentDetails.add(UI.generateUniqueID());
            paymentDetails.add(MakeDepositRequestActivity.apiParameters.productID);
            paymentDetails.add("Mobile Money");
            paymentDetails.add(amount);
            paymentDetails.add(amount);
            paymentDetails.add("0");
            paymentDetails.add(transactionReason);
            paymentDetails.add(transactionReference);
            paymentDetails.add(productsDatabase.getProductOwner(MakeDepositRequestActivity.apiParameters.productID));
            paymentDetails.add(UI.getCurrentDate());
            paymentDetails.add(UI.getCurrentTime());
            paymentDetails.add("false");
            paymentDetails.add("false");

            paymentDetails.add(productsDatabase.getProductZoneID(MakeDepositRequestActivity.apiParameters.productID));

            paymentsDatabase.addPaymentRecord(paymentDetails);
        }

        return transactionDetailsSaved;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}

