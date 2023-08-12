package com.moels.farmconnect.controller;

import android.content.Context;

import com.moels.farmconnect.model.command.CommandListener;

public abstract class Controller {
    public CommandListener listener;
    public Context context;
    public void setListener(CommandListener listener) {
        this.listener = listener;
    }

    public void setContext(Context context){
        this.context = context;
    }


}
