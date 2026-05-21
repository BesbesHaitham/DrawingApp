package com.drawingapp.command;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class ClearCanvasCommand implements Command {
    private final Pane canvas;
    private final List<Node> clearedNodes;
    private final Runnable onExecute;
    private final Runnable onUndo;

    public ClearCanvasCommand(Pane canvas) {
        this(canvas, () -> { }, () -> { });
    }

    public ClearCanvasCommand(Pane canvas, Runnable onExecute, Runnable onUndo) {
        this.canvas = canvas;
        this.clearedNodes = new ArrayList<>();
        this.onExecute = onExecute;
        this.onUndo = onUndo;
    }

    @Override
    public void execute() {
        if (clearedNodes.isEmpty()) {
            clearedNodes.addAll(canvas.getChildren());
        }

        canvas.getChildren().removeAll(clearedNodes);
        onExecute.run();
    }

    @Override
    public void undo() {
        canvas.getChildren().addAll(clearedNodes);
        onUndo.run();
    }

    @Override
    public String getDescription() {
        return "Effacement complet du canevas";
    }
}
