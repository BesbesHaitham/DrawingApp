package com.drawingapp.model;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class PentagonShape extends ShapeModel {

    public PentagonShape(double startX, double startY, double endX, double endY, int colorRGB) {
        super(startX, startY, endX, endY, colorRGB);
    }

    @Override
    public Shape createShape() {
        Polygon pentagon = createRegularPolygon(5, -90);
        styleFilledShape(pentagon);
        return pentagon;
    }

    @Override
    public String getType() {
        return "Pentagone";
    }

    private Polygon createRegularPolygon(int sides, double rotationDegrees) {
        double centerX = getCenterX();
        double centerY = getCenterY();
        double radiusX = getWidth() / 2.0;
        double radiusY = getHeight() / 2.0;

        Polygon polygon = new Polygon();
        for (int index = 0; index < sides; index++) {
            double angle = Math.toRadians(rotationDegrees + (360.0 / sides * index));
            polygon.getPoints().addAll(
                    centerX + radiusX * Math.cos(angle),
                    centerY + radiusY * Math.sin(angle)
            );
        }
        return polygon;
    }
}
