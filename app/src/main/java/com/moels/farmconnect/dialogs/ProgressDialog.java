package com.moels.farmconnect.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.moels.farmconnect.R;

public class ProgressDialog extends DialogFragment {

    private ProgressBar progressBar;
    private TextView textViewProgress;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return(builder.setTitle("Confirm Delete")
                .setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_progress, null))
                .setCancelable(false)
                .create());
    }

}

