/*
  This activity is the opening activity and it works as splash screen
  and runs for a specified amount of time in the handler delay period
 */
package com.moels.farmconnect.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.moels.farmconnect.R;

public class SplashScreenActivity extends AppCompatActivity {
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences myAppPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                boolean userAuthenticated = myAppPreferences.getBoolean("phoneNumberAuthenticated", false);

                if (userAuthenticated == true) {
                    Intent createProfileActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(createProfileActivity);
                    finish();

                }

                if (userAuthenticated == false){
                    Intent sendOneTimePasswordActivity = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                    startActivity(sendOneTimePasswordActivity);
                    finish();
                }

            }
        }, 1000);

    }
}