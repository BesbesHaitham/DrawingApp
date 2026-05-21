package com.drawingapp.command;

import com.drawingapp.model.ShapeModel;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 * Commande pour dessiner une forme.
 */
public class DrawCommand implements Command {
    private final Pane canvas;
    private final ShapeModel shapeModel;
    private Shape drawnShape;

    public DrawCommand(Pane canvas, ShapeModel shapeModel) {
        this.canvas = canvas;
        this.shapeModel = shapeModel;
    }

    @Override
    public void execute() {
        drawnShape = shapeModel.createShape();
        canvas.getChildren().add(drawnShape);
    }

    @Override
    public void undo() {
        if (drawnShape != null) {
            canvas.getChildren().remove(drawnShape);
        }
    }

    @Override
    public String getDescription() {
        return "Dessin d'un " + shapeModel.getType();
    }

    public Shape getDrawnShape() {
        return drawnShape;
    }

    public ShapeModel getShapeModel() {
        return shapeModel;
    }
}
