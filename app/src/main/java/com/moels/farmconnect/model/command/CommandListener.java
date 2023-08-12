package com.moels.farmconnect.model.command;

public interface CommandListener {
    void onFailure();
    void onSuccess();
}