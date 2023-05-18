package com.moels.farmconnect.utility_classes;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.moels.farmconnect.R;

public class UI {
    public static void show(View view){
        view.setVisibility(View.VISIBLE);
    }
    public static void hide(View view){
        View ProgressBar = null;
        if (view == ProgressBar) view.setVisibility(View.GONE);
        else view.setVisibility(View.INVISIBLE);
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
}
