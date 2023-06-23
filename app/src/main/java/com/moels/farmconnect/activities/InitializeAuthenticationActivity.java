/*
   This activity is used in phone number authentication.
   it is used in conjunction with firebase;
 */

package com.moels.farmconnect.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.moels.farmconnect.R;
import com.moels.farmconnect.utility_classes.UI;

import java.util.concurrent.TimeUnit;

public class InitializeAuthenticationActivity extends AppCompatActivity {
    private EditText phoneNumberToAuthenticate;
    private Button sendOneTimePasswordButton;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = null;
    private FirebaseAuth mAuth;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private boolean allPermissionsGranted;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize_authentication);
        initUI();
        setUpStatusBar();
        setSupportActionBar(toolbar);
        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Sign Up", true);

        if (savedInstanceState != null) {
            String phoneNumber = savedInstanceState.getString("phoneNumberToAuthenticate");
            phoneNumberToAuthenticate.setText(phoneNumber);
        }


        sendOneTimePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRequiredPermissions() == false) {
                    requestPermissions();
                    return;
                }
                requestOTPCodeFromFirebase();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE){
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = true;
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("phoneNumberToAuthenticate", phoneNumberToAuthenticate.getText().toString());
    }
    private boolean checkRequiredPermissions(){
        boolean checkRequiredPermissions = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED
            ){
                checkRequiredPermissions = false;
            }
        }
        return checkRequiredPermissions;
    };

    private void requestPermissions(){
        ActivityCompat.requestPermissions(InitializeAuthenticationActivity.this, new String[]{
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
        }, PERMISSION_REQUEST_CODE);
    }

    private void requestOTPCodeFromFirebase(){
        if (phoneNumberToAuthenticate.getText().toString().trim().isEmpty()) {
            UI.displayToast(InitializeAuthenticationActivity.this, "Phone Number Is Required");
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connecting...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        UI.hide(sendOneTimePasswordButton);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                progressDialog.dismiss();
                UI.show(sendOneTimePasswordButton);
                Log.d("Verification Completed", "onVerificationCompleted:" + phoneAuthCredential);

//                mAuth.signInWithCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
                UI.show(sendOneTimePasswordButton);
                UI.displayToast(InitializeAuthenticationActivity.this, e.getMessage());
                Log.d("Verification Failed", "Failed To Send One Time Password");
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);

                progressDialog.dismiss();
                UI.show(sendOneTimePasswordButton);

                Intent goToAuthenticateUserActivityIntent = new Intent(InitializeAuthenticationActivity.this, AuthenticateUserActivity.class);
                goToAuthenticateUserActivityIntent.putExtra("phoneNumber", phoneNumberToAuthenticate.getText().toString());
                goToAuthenticateUserActivityIntent.putExtra("verificationId", verificationId);
                Log.d("Verification Started", "Phone Number Verification Started " + verificationId);

                startActivity(goToAuthenticateUserActivityIntent);
            }
        };
        mAuth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth).
                setPhoneNumber("+256" + phoneNumberToAuthenticate.getText().toString()).
                setActivity(InitializeAuthenticationActivity.this).
                setTimeout(60L, TimeUnit.SECONDS).
                setCallbacks(mCallbacks).build();

        PhoneAuthProvider.verifyPhoneNumber(options);


    }
    private void initUI(){
        phoneNumberToAuthenticate = findViewById(R.id.phone_number_to_authenticate);
        sendOneTimePasswordButton = findViewById(R.id.send_otp_button);
        progressBar = findViewById(R.id.progress_bar);
        toolbar = findViewById(R.id.initialize_authentication_toolbar);
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
            }else {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            }
        }

    }
}