package com.moels.farmconnect.utils;

import android.app.UiModeManager;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.moels.farmconnect.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class UI {
    public static void show(View view){
        view.setVisibility(View.VISIBLE);
    }
    public static void hide(View view){
        View ProgressBar = null;
        if (view == ProgressBar) view.setVisibility(View.GONE);
        else view.setVisibility(View.GONE);
    }

    public static void setUpActionBar(ActionBar actionBar, int drawableID, String title, boolean displayBackArrow){
        ActionBar activityActionBar = actionBar;
        if (activityActionBar != null){
            activityActionBar.setHomeAsUpIndicator(drawableID);
            activityActionBar.setTitle(title);
            activityActionBar.setDisplayHomeAsUpEnabled(displayBackArrow);
        }
    }

    public static void setUpActionBar(ActionBar actionBar, String title){
        ActionBar activityActionBar = actionBar;
        if (activityActionBar != null){
            activityActionBar.setTitle(title);
        }
    }

    public static void displayToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void displaySnackBar(Context context, View view, String textToDisplay){
        Snackbar snackbar = Snackbar.make(view, textToDisplay, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryTeal));
        snackbar.show();
    }

    public static void setUpToolbarInDarkMode(Context context, Toolbar toolbar){
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        int currentMode = uiModeManager.getNightMode();

        if (currentMode == UiModeManager.MODE_NIGHT_YES){
            toolbar.setBackgroundColor(context.getResources().getColor(R.color.colorBlack));
        }
    }

    public static void setUpTabLayoutInDarkMode(Context context, TabLayout tabLayout){
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        int currentMode = uiModeManager.getNightMode();

        if (currentMode == UiModeManager.MODE_NIGHT_YES){
            tabLayout.setBackgroundColor(context.getResources().getColor(R.color.colorBlack));
        }
    }

    public static String generateUniqueID(){
        UUID uuid = UUID.randomUUID();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = simpleDateFormat.format(new Date());

        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        return currentTime + randomNumber;
    }

    public static String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(calendar.getTime());
        return formattedDate;
    }

    public static String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
        String formattedTime = timeFormat.format(calendar.getTime());
        return formattedTime;
    }

}
