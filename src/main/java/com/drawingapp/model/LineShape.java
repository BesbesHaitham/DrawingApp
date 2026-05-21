package com.drawingapp.model;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

/**
 * Modèle pour une ligne.
 */
public class LineShape extends ShapeModel {

    public LineShape(double startX, double startY, double endX, double endY, int colorRGB) {
        super(startX, startY, endX, endY, colorRGB);
    }

    @Override
    public Shape createShape() {
        Line line = new Line(startX, startY, endX, endY);
        styleOutlinedShape(line);
        return line;
    }

    @Override
    public String getType() {
        return "Ligne";
    }
}
