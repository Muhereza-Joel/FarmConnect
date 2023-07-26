package com.moels.farmconnect.controller.command;

public interface Command{
    public void execute();
    public void undo();
}
