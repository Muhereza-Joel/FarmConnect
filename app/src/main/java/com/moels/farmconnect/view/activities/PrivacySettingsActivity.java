package com.moels.farmconnect.view.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;

import android.app.UiModeManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.moels.farmconnect.R;
import com.moels.farmconnect.utils.UI;

public class PrivacySettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);
        init();
        setUpStatusBar();
        setSupportActionBar(toolbar);

        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Privacy", true);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.privacy_settings_fragment_container, new PrivacySettings())
                    .commit();
        }

    }

    private void init(){
        toolbar = findViewById(R.id.privacy_settings_activity_toolbar);
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

    public static class PrivacySettings extends PreferenceFragmentCompat{

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            addPreferencesFromResource(R.xml.privacy_preferences);
        }
    }
}