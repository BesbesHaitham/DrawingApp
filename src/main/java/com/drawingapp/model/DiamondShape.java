package com.drawingapp.model;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class DiamondShape extends ShapeModel {

    public DiamondShape(double startX, double startY, double endX, double endY, int colorRGB) {
        super(startX, startY, endX, endY, colorRGB);
    }

    @Override
    public Shape createShape() {
        double minX = getMinX();
        double minY = getMinY();
        double width = getWidth();
        double height = getHeight();

        Polygon diamond = new Polygon(
                minX + (width / 2.0), minY,
                minX + width, minY + (height / 2.0),
                minX + (width / 2.0), minY + height,
                minX, minY + (height / 2.0)
        );
        styleFilledShape(diamond);
        return diamond;
    }

    @Override
    public String getType() {
        return "Losange";
    }
}
