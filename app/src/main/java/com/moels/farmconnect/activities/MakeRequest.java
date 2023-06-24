package com.moels.farmconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.moels.farmconnect.R;
import com.moels.farmconnect.easypay.ApiParameters;
import com.moels.farmconnect.utility_classes.UI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.moels.farmconnect.easypay.Request.EASY_PAY_PARAMS;

public class MakeRequest extends AppCompatActivity {
    private TextView ammountToPay;
    public static TextView transactionResponse;
    private EditText phone_number_field;
    private CountryCodePicker countryCodePicker;
    private ApiParameters apiParameters=new ApiParameters();
    public static ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private LinearLayout container;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_request);
        phone_number_field=(EditText) findViewById(R.id.phone_number_field);
        ammountToPay=findViewById(R.id.ammountToPay);
        countryCodePicker=findViewById(R.id.ccp);
        transactionResponse=findViewById(R.id.transactionResponse);
        container = findViewById(R.id.payment_container);
        toolbar = findViewById(R.id.make_request_activity_toolbar);
        setUpStatusBar();
        setSupportActionBar(toolbar);

        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Make Payment", true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);

        progressBar=findViewById(R.id.progressBar);
        Button mpay=(Button) findViewById(R.id.pay);

        try {
            apiParameters = (ApiParameters) getIntent().getSerializableExtra(EASY_PAY_PARAMS);
            ammountToPay.setText("You are paying " + apiParameters.currency+" "+apiParameters.amountToPay + " to");
        }catch (Exception e){
            e.printStackTrace();
        }

        final String transaction_amount=apiParameters.amountToPay;

        mpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pay=true;
                String countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();
                final String transaction_phone=countryCode+phone_number_field.getText().toString();
                phone_number_field.setError(null);
                if (phone_number_field.getText().toString().isEmpty()){
                    pay=false;
                    phone_number_field.setError("Phone Number Is Required");
                    Toast.makeText(MakeRequest.this, "Phone Number Is Required", Toast.LENGTH_SHORT).show();
                }

                if (pay) {
//                    progressBar.setVisibility(View.VISIBLE);
                    progressDialog.show();
                    transactionResponse.setVisibility(View.GONE);
                    CallAPI callAPI = new CallAPI(transaction_amount, transaction_phone, MakeRequest.this);
                    callAPI.execute();
                }

            }
        });

    }

    private void setUpStatusBar() {
        Window window = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
            int currentMode = uiModeManager.getNightMode();
            if (currentMode == UiModeManager.MODE_NIGHT_YES) {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));
                container.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlack));
                phone_number_field.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
            }else {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            }
        }

    }

    public class CallAPI extends AsyncTask<String, String, String> {

        private String post_url="https://www.easypay.co.ug/api/";
        private String client_id=apiParameters.clientID;//change this
        private String secret=apiParameters.ClientSecret;//change this
        private String transaction_action="mmdeposit";
        private String transaction_amount="";
        private String transaction_phone="";
        private String currency=apiParameters.currency;
        private String reference=apiParameters.reference;
        private String reason=apiParameters.paymentReason;
        Context context;

        public CallAPI(String transaction_amount, String transaction_phone, Context context){
            this.transaction_amount=transaction_amount;
            this.transaction_phone=transaction_phone;
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                final String POST_PARAMS = "{" +
                        "\"username\": \""+client_id+"\"," +
                        "\"password\": \""+secret+"\"," +
                        "\"action\": \""+transaction_action+"\"," +
                        "\"amount\": \"" + transaction_amount + "\"," +
                        "\"currency\": \""+currency+"\"," +
                        "\"phone\": \"" + transaction_phone + "\"," +
                        "\"reference\": \"nsiimbi_com_" + System.currentTimeMillis() + "\"," +
                        "\"reason\": \"" + reason + "\"}";
                System.out.println(POST_PARAMS);
                URL obj = new URL(post_url);
                HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
                postConnection.setRequestMethod("POST");
                postConnection.setRequestProperty("Content-Type", "application/json");
                postConnection.setDoOutput(true);
                OutputStream os = postConnection.getOutputStream();
                os.write(POST_PARAMS.getBytes());
                os.flush();
                os.close();
                int responseCode = postConnection.getResponseCode();
                System.out.println("POST Response Code : " + responseCode);
                System.out.println("POST Response Message : " + postConnection.getResponseMessage());
                BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
                String inputLine;
                final StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                final JSONObject js = new JSONObject(String.valueOf(response));
                if(js.get("success").toString().equals("0")) {
                    Log.e("FAILED: ", js.get("errormsg").toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
//                                progressBar.setVisibility(View.GONE);
                                progressDialog.dismiss();
                                transactionResponse.setVisibility(View.VISIBLE);
                                transactionResponse.setText(js.get("errormsg").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else if(js.get("success").toString().equals("1")) {
                    Log.e("WORKED: ", response.toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                transactionResponse.setText(response.toString());
//                                progressBar.setVisibility(View.GONE);
                                progressDialog.dismiss();
                                Intent intent = new Intent();
                                intent.putExtra("response", response.toString());
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }catch (Exception e){
                Log.e("WORKED: ",  "Error: "+e);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}