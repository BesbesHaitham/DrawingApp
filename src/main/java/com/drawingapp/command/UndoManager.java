package com.drawingapp.command;

import java.util.ArrayDeque;
import java.util.Deque;

public class UndoManager {
    private final Deque<Command> commandHistory;
    private final Deque<Command> redoHistory;

    public UndoManager() {
        this.commandHistory = new ArrayDeque<>();
        this.redoHistory = new ArrayDeque<>();
    }

    public void executeCommand(Command command) {
        command.execute();
        commandHistory.push(command);
        redoHistory.clear();
    }

    public Command undo() {
        if (commandHistory.isEmpty()) {
            return null;
        }

        Command command = commandHistory.pop();
        command.undo();
        redoHistory.push(command);
        return command;
    }

    public Command redo() {
        if (redoHistory.isEmpty()) {
            return null;
        }

        Command command = redoHistory.pop();
        command.execute();
        commandHistory.push(command);
        return command;
    }

    public boolean canUndo() {
        return !commandHistory.isEmpty();
    }

    public boolean canRedo() {
        return !redoHistory.isEmpty();
    }

    public int getHistorySize() {
        return commandHistory.size();
    }

    public int getRedoSize() {
        return redoHistory.size();
    }

    public void clearHistory() {
        commandHistory.clear();
        redoHistory.clear();
    }
}
