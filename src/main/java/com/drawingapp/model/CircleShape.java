package com.drawingapp.model;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * Modèle pour un cercle.
 */
public class CircleShape extends ShapeModel {

    public CircleShape(double startX, double startY, double endX, double endY, int colorRGB) {
        super(startX, startY, endX, endY, colorRGB);
    }

    @Override
    public Shape createShape() {
        double centerX = getCenterX();
        double centerY = getCenterY();
        double radius = Math.sqrt(
            Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)
        ) / 2;

        Circle circle = new Circle(centerX, centerY, radius);
        styleFilledShape(circle);
        return circle;
    }

    @Override
    public String getType() {
        return "Cercle";
    }
}
