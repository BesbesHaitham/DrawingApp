package com.drawingapp.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * Interface abstraite pour les formes géométriques.
 */
public abstract class ShapeModel {
    protected double startX;
    protected double startY;
    protected double endX;
    protected double endY;
    protected int colorRGB;

    public ShapeModel(double startX, double startY, double endX, double endY, int colorRGB) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.colorRGB = colorRGB;
    }

    /**
     * Crée et retourne la forme JavaFX correspondante.
     */
    public abstract Shape createShape();

    /**
     * Retourne le type de forme sous forme de chaîne.
     */
    public abstract String getType();

    protected Color getPrimaryColor() {
        return Color.web(String.format("#%06X", colorRGB));
    }

    protected Color getStrokeColor() {
        return getPrimaryColor().deriveColor(0, 1.0, 0.68, 1.0);
    }

    protected double getMinX() {
        return Math.min(startX, endX);
    }

    protected double getMinY() {
        return Math.min(startY, endY);
    }

    protected double getWidth() {
        return Math.abs(endX - startX);
    }

    protected double getHeight() {
        return Math.abs(endY - startY);
    }

    protected double getCenterX() {
        return (startX + endX) / 2.0;
    }

    protected double getCenterY() {
        return (startY + endY) / 2.0;
    }

    protected void styleFilledShape(Shape shape) {
        shape.setFill(getPrimaryColor().deriveColor(0, 1.0, 1.0, 0.86));
        shape.setStroke(getStrokeColor());
        shape.setStrokeWidth(2.2);
    }

    protected void styleOutlinedShape(Shape shape) {
        shape.setStroke(getPrimaryColor());
        shape.setStrokeWidth(2.6);
    }

    // Getters
    public double getStartX() { return startX; }
    public double getStartY() { return startY; }
    public double getEndX() { return endX; }
    public double getEndY() { return endY; }
    public int getColorRGB() { return colorRGB; }
}
