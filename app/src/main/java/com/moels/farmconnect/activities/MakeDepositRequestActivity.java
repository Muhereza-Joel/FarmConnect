package com.moels.farmconnect.activities;

import static com.moels.farmconnect.easypay.Request.EASY_PAY_PARAMS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.moels.farmconnect.R;
import com.moels.farmconnect.easypay.APICall;
import com.moels.farmconnect.easypay.APICallParameters;
import com.moels.farmconnect.utility_classes.UI;

public class MakeDepositRequestActivity extends AppCompatActivity {
    private TextView amountToPay;
    public static TextView responseMessage;
    private EditText phone_number_field;
    private CountryCodePicker countryCodePicker;
    private APICallParameters apiParameters=new APICallParameters();
    private String amountToDeposit;
    public static ProgressDialog progressDialog;
    private LinearLayout container;
    private Toolbar toolbar;
    private Button payButton;
    private Boolean viewsValidated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_deposit_request);
        initUI();
        setUpStatusBar();
        setSupportActionBar(toolbar);

        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Make Payment", true);

        countryCodePicker.registerCarrierNumberEditText(phone_number_field);

        getIntentData();
        addClickEventOnPayButton();

    }

    private void initUI(){
        phone_number_field=(EditText) findViewById(R.id.phone_number_field);
        amountToPay =findViewById(R.id.ammountToPay);
        countryCodePicker=findViewById(R.id.ccp);
        responseMessage =findViewById(R.id.transactionResponse);
        container = findViewById(R.id.payment_container);
        toolbar = findViewById(R.id.make_withdraw_request_activity_toolbar);
        payButton = (Button) findViewById(R.id.pay);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);
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
                countryCodePicker.setDialogTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            }else {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            }
        }

    }

    private void getIntentData(){
        try {
            apiParameters = (APICallParameters) getIntent().getSerializableExtra(EASY_PAY_PARAMS);
            amountToPay.setText("You are paying " + apiParameters.transactionCurrency+" "+apiParameters.transactionAmount + " to");
        }catch (Exception e){
            e.printStackTrace();
        }

        amountToDeposit =apiParameters.transactionAmount;
    }

    private void addClickEventOnPayButton(){
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateViews();
            }
        });
    }

    private void validateViews(){
        if (phone_number_field.getText().toString().isEmpty()){
            viewsValidated =false;
            phone_number_field.setError("Phone Number Is Required");
            Toast.makeText(MakeDepositRequestActivity.this, "Phone Number Is Required", Toast.LENGTH_SHORT).show();
        }else if (!countryCodePicker.isValidFullNumber()){
            viewsValidated = false;
            Toast.makeText(MakeDepositRequestActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
        }else {
            viewsValidated = true;
            startPaymentProcess();
        }
    }
    private void startPaymentProcess(){
        if (viewsValidated){
            String countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();
            final String recipientPhoneNumber = countryCode+phone_number_field.getText().toString();
            progressDialog.show();
            responseMessage.setVisibility(View.GONE);

            APICall depositAPICall = new APICall()
                    .setActivity(MakeDepositRequestActivity.this)
                    .setRequestUrl(apiParameters.postUrl)
                    .setClientID(apiParameters.APIClientId)
                    .setClientSecret(apiParameters.APIClientSecret)
                    .setRequestAction(apiParameters.requestAction)
                    .setTransactionAmount("500")
                    .setTransactionCurrency(apiParameters.transactionCurrency)
                    .setPhoneNumber(recipientPhoneNumber)
                    .setTransactionReference(apiParameters.reference)
                    .setTransactionReason(apiParameters.reason);

            depositAPICall.execute();

        }
    }

}