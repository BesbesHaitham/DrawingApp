package com.drawingapp.model;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class HexagonShape extends ShapeModel {

    public HexagonShape(double startX, double startY, double endX, double endY, int colorRGB) {
        super(startX, startY, endX, endY, colorRGB);
    }

    @Override
    public Shape createShape() {
        double centerX = getCenterX();
        double centerY = getCenterY();
        double radiusX = getWidth() / 2.0;
        double radiusY = getHeight() / 2.0;

        Polygon hexagon = new Polygon();
        for (int index = 0; index < 6; index++) {
            double angle = Math.toRadians(-30 + (60.0 * index));
            hexagon.getPoints().addAll(
                    centerX + radiusX * Math.cos(angle),
                    centerY + radiusY * Math.sin(angle)
            );
        }
        styleFilledShape(hexagon);
        return hexagon;
    }

    @Override
    public String getType() {
        return "Hexagone";
    }
}
