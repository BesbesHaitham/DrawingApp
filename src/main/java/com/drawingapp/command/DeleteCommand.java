package com.drawingapp.command;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 * Commande pour supprimer une forme.
 */
public class DeleteCommand implements Command {
    private final Pane canvas;
    private final Shape shape;

    public DeleteCommand(Pane canvas, Shape shape) {
        this.canvas = canvas;
        this.shape = shape;
    }

    @Override
    public void execute() {
        canvas.getChildren().remove(shape);
    }

    @Override
    public void undo() {
        if (!canvas.getChildren().contains(shape)) {
            canvas.getChildren().add(shape);
        }
    }

    @Override
    public String getDescription() {
        return "Suppression d'une forme";
    }

    public Shape getShape() {
        return shape;
    }
}
