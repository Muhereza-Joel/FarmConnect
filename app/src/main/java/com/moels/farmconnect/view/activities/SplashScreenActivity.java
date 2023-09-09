/*
  This activity is the opening activity and it works as splash screen
  and runs for a specified amount of time in the handler delay period
 */
package com.moels.farmconnect.view.activities;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.moels.farmconnect.R;
import com.moels.farmconnect.utils.filemanager.FileManager;
import com.moels.farmconnect.utils.preferences.FarmConnectAppPreferences;
import com.moels.farmconnect.utils.preferences.Preferences;

public class SplashScreenActivity extends AppCompatActivity {
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setUpStatusBar();


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Preferences preferences = FarmConnectAppPreferences.getInstance(getApplicationContext());
                FileManager.createMediaStorageFolders(getApplicationContext());

                if (preferences.userIsAuthenticated()) {

                    if (preferences.userProfileIsCreated()) {

                        if (preferences.userAccountIsChosen()){
                            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }else{
                            Intent intent = new Intent(SplashScreenActivity.this, AccountSetUpActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(SplashScreenActivity.this, CreateProfileActivity.class);
                        intent.putExtra("phoneNumber", preferences.getAuthenticatedPhoneNumber());
                        startActivity(intent);
                        finish();
                    }

                }


                if (!preferences.userIsAuthenticated()){
                    Intent intent = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 1000);

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
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
            }
        }

    }

}