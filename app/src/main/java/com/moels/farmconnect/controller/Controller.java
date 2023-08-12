package com.moels.farmconnect.controller;

import android.content.Context;

import com.moels.farmconnect.model.command.Listener;

import java.util.List;

public abstract class Controller {
    public Listener listener;
    public Context context;
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setContext(Context context){
        this.context = context;
    }


}
