package com.moels.farmconnect.view.activities;

import android.app.FragmentTransaction;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.moels.farmconnect.R;
import com.moels.farmconnect.utils.UI;

import java.util.List;

public class SettingsActivity extends AppCompatActivity{

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        init();
        setUpStatusBar();
        setSupportActionBar(toolbar);
        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Settings", true);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

    }


    private void init(){
        toolbar = findViewById(R.id.settings_toolbar);
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


    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            addPreferencesFromResource(R.xml.root_preferences);
        }

        @Override
        public boolean onPreferenceTreeClick(@NonNull Preference preference) {

            switch (preference.getKey()){
                case "notifications" :
                    Intent notificationSettingsActivity = new Intent(getContext(), NotificationSettingsActivity.class);
                    startActivity(notificationSettingsActivity);
                    break;

                case "zones" :
                    Intent zoneSettingsActivity = new Intent(getContext(), ZoneSettingsActivity.class);
                    startActivity(zoneSettingsActivity);
                    break;

                case "privacy" :
                    Intent privacySettingsActivity = new Intent(getContext(), PrivacySettingsActivity.class);
                    startActivity(privacySettingsActivity);
                    break;

                case "language" :
                    Intent languageSettingsActivity = new Intent(getContext(), AppLanguageSettingsActivity.class);
                    startActivity(languageSettingsActivity);
                    break;
            }

            return super.onPreferenceTreeClick(preference);
        }
    }
}