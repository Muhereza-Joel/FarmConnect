package com.moels.farmconnect.controller;

import android.content.Context;

import com.moels.farmconnect.model.command.Listener;

import java.util.List;

public abstract class Controller {
    public Listener listener;
    public Context context;
    public Controller setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public Controller setContext(Context context){
        this.context = context;
        return this;
    }
    public abstract void save(List<String> zoneDetails);
    public abstract void getDetails(String id);

}
