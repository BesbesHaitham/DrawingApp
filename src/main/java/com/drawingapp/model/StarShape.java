package com.drawingapp.model;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class StarShape extends ShapeModel {

    public StarShape(double startX, double startY, double endX, double endY, int colorRGB) {
        super(startX, startY, endX, endY, colorRGB);
    }

    @Override
    public Shape createShape() {
        double centerX = getCenterX();
        double centerY = getCenterY();
        double outerRadiusX = getWidth() / 2.0;
        double outerRadiusY = getHeight() / 2.0;
        double innerRadiusX = outerRadiusX * 0.45;
        double innerRadiusY = outerRadiusY * 0.45;

        Polygon star = new Polygon();
        for (int index = 0; index < 10; index++) {
            double angle = Math.toRadians(-90 + (36.0 * index));
            double radiusX = index % 2 == 0 ? outerRadiusX : innerRadiusX;
            double radiusY = index % 2 == 0 ? outerRadiusY : innerRadiusY;
            star.getPoints().addAll(
                    centerX + radiusX * Math.cos(angle),
                    centerY + radiusY * Math.sin(angle)
            );
        }
        styleFilledShape(star);
        return star;
    }

    @Override
    public String getType() {
        return "Etoile";
    }
}
