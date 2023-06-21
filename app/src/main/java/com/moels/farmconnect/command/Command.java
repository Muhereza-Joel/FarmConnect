package com.moels.farmconnect.command;

public interface Command {
    public void execute();
    public void undo();
}
