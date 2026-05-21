package com.drawingapp.graph;

import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class GraphNodeItem {
    private final int id;
    private final String label;
    private final Circle circle;
    private final Text labelText;
    private final int accentColorRgb;
    private boolean highlighted;

    public GraphNodeItem(int id, String label, Circle circle, Text labelText, int accentColorRgb) {
        this.id = id;
        this.label = label;
        this.circle = circle;
        this.labelText = labelText;
        this.accentColorRgb = accentColorRgb;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Circle getCircle() {
        return circle;
    }

    public Text getLabelText() {
        return labelText;
    }

    public int getAccentColorRgb() {
        return accentColorRgb;
    }

    public double getCenterX() {
        return circle.getCenterX();
    }

    public double getCenterY() {
        return circle.getCenterY();
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }
}
