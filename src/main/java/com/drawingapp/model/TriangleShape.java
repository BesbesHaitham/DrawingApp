package com.drawingapp.model;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class TriangleShape extends ShapeModel {

    public TriangleShape(double startX, double startY, double endX, double endY, int colorRGB) {
        super(startX, startY, endX, endY, colorRGB);
    }

    @Override
    public Shape createShape() {
        double minX = getMinX();
        double minY = getMinY();
        double width = getWidth();
        double height = getHeight();

        Polygon triangle = new Polygon(
                minX + (width / 2.0), minY,
                minX + width, minY + height,
                minX, minY + height
        );
        styleFilledShape(triangle);
        return triangle;
    }

    @Override
    public String getType() {
        return "Triangle";
    }
}
