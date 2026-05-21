package com.drawingapp.model;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Modèle pour un rectangle.
 */
public class RectangleShape extends ShapeModel {

    public RectangleShape(double startX, double startY, double endX, double endY, int colorRGB) {
        super(startX, startY, endX, endY, colorRGB);
    }

    @Override
    public Shape createShape() {
        Rectangle rect = new Rectangle(getMinX(), getMinY(), getWidth(), getHeight());
        styleFilledShape(rect);
        return rect;
    }

    @Override
    public String getType() {
        return "Rectangle";
    }
}
