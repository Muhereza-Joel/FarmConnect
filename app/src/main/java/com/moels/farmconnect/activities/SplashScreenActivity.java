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
                boolean profileCreated = myAppPreferences.getBoolean("profileCreated", false);
                System.out.println(profileCreated);

                if (userAuthenticated == true) {

                    if (profileCreated == true) {
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashScreenActivity.this, CreateProfileActivity.class);
                        intent.putExtra("phoneNumber", myAppPreferences.getString("authenticatedPhoneNumber", "123456789"));
                        startActivity(intent);
                        finish();
                    }

                }


                if (userAuthenticated == false){
                    Intent intent = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 1000);

    }
}