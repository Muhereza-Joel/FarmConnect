package com.moels.farmconnect.view.activities;

import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moels.farmconnect.R;
import com.moels.farmconnect.utils.preferences.FarmConnectAppPreferences;
import com.moels.farmconnect.utils.preferences.Preferences;
import com.moels.farmconnect.utils.UI;

import java.util.concurrent.TimeUnit;

public class AuthenticateUserActivity extends AppCompatActivity {
    private String verificationId;
    private EditText oneTimePasswordEditTextOne,
            oneTimePasswordEditTextTwo,
            oneTimePasswordEditTextThree,
            oneTimePasswordEditTextFour,
            oneTimePasswordEditTextFive,
            oneTimePasswordEditTextSix;
    private TextView phoneNumberToAuthenticate;
    private Button verifyPhoneNumberButton,reSendOneTimePasswordButton;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate_user);

        initUI();
        setUpStatusBar();
        setSupportActionBar(toolbar);
        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);

        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Phone Number Verification", true);
        preferences = FarmConnectAppPreferences.getInstance(getApplicationContext());

        phoneNumberToAuthenticate.setText(getIntent().getStringExtra("phoneNumber"));

        setUpOTPInputs();

        verificationId = getIntent().getStringExtra("verificationId");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);

        verifyPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateOneTimePasswordEditTexts() == true){
                    String oneTimePassword = combineCodeFromEditTexts();
                    if (verificationId != null){
                        progressDialog.show();
                        UI.hide(verifyPhoneNumberButton);
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, oneTimePassword);
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).
                                addOnCompleteListener( new OnCompleteListener<AuthResult>(){
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressDialog.dismiss();
                                        UI.show(verifyPhoneNumberButton);

                                        if (task.isSuccessful()){
                                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            String verifiedPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                                            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
                                            usersReference.child(userID).child(verifiedPhoneNumber).setValue(verifiedPhoneNumber);
                                            preferences.putBoolean("phoneNumberAuthenticated", true);
                                            preferences.putString("authenticatedPhoneNumber", getIntent().getStringExtra("phoneNumber"));

                                            Intent createProfileActivity = new Intent(getApplicationContext(), CreateProfileActivity.class);
                                            createProfileActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            createProfileActivity.putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber"));
                                            startActivity(createProfileActivity);

                                            }

                                            else {
                                            UI.displayToast(AuthenticateUserActivity.this, "You Entered An Invalid Code");
                                        }
                                    }
                                });

                    }
                } else {
                    Toast.makeText(AuthenticateUserActivity.this, "Please Enter Valid Code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reSendOneTimePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+256" + getIntent().getStringExtra("phoneNumber"),
                        60, TimeUnit.SECONDS,
                        AuthenticateUserActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                UI.displayToast(AuthenticateUserActivity.this, e.getMessage());
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = newVerificationId;
                                UI.displayToast(AuthenticateUserActivity.this, "OTP Sent");
                            }
                        }
                );
            }
        });
    }
    private void initUI(){
        phoneNumberToAuthenticate = findViewById(R.id.textMobile);
        oneTimePasswordEditTextOne = findViewById(R.id.input_code1);
        oneTimePasswordEditTextTwo = findViewById(R.id.input_code2);
        oneTimePasswordEditTextThree = findViewById(R.id.input_code3);
        oneTimePasswordEditTextFour = findViewById(R.id.input_code4);
        oneTimePasswordEditTextFive = findViewById(R.id.input_code5);
        oneTimePasswordEditTextSix = findViewById(R.id.input_code6);
        progressBar = findViewById(R.id.progress_bar);
        verifyPhoneNumberButton = findViewById(R.id.buttonVerifyOTP);
        toolbar = findViewById(R.id.my_toolbar);
        reSendOneTimePasswordButton = findViewById(R.id.resend_otp);
    }
    private void setUpOTPInputs(){
        oneTimePasswordEditTextOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    oneTimePasswordEditTextTwo.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        oneTimePasswordEditTextTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    oneTimePasswordEditTextThree.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        oneTimePasswordEditTextThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    oneTimePasswordEditTextFour.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        oneTimePasswordEditTextFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    oneTimePasswordEditTextFive.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        oneTimePasswordEditTextFive.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    oneTimePasswordEditTextSix.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validateOneTimePasswordEditTexts(){
        boolean validated = true;
        if(oneTimePasswordEditTextOne.getText().toString().trim().isEmpty()
                || oneTimePasswordEditTextTwo.getText().toString().trim().isEmpty()
                || oneTimePasswordEditTextThree.getText().toString().trim().isEmpty()
                || oneTimePasswordEditTextFour.getText().toString().trim().isEmpty()
                || oneTimePasswordEditTextFive.getText().toString().trim().isEmpty()
                || oneTimePasswordEditTextSix.getText().toString().trim().isEmpty()){
            validated = false;
        }
        return validated;
    }
    private String combineCodeFromEditTexts(){
        String oneTimePassword = oneTimePasswordEditTextOne.getText().toString() +
                oneTimePasswordEditTextTwo.getText().toString() +
                oneTimePasswordEditTextThree.getText().toString() +
                oneTimePasswordEditTextFour.getText().toString() +
                oneTimePasswordEditTextFive.getText().toString() +
                oneTimePasswordEditTextSix.getText().toString();
        return oneTimePassword;
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