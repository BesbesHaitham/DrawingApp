package com.drawingapp.graph;

import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class GraphEdgeItem {
    private final GraphNodeItem startNode;
    private final GraphNodeItem endNode;
    private final double weight;
    private final Line line;
    private final Text weightText;
    private final int accentColorRgb;
    private boolean highlighted;

    public GraphEdgeItem(
            GraphNodeItem startNode,
            GraphNodeItem endNode,
            double weight,
            Line line,
            Text weightText,
            int accentColorRgb
    ) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.weight = weight;
        this.line = line;
        this.weightText = weightText;
        this.accentColorRgb = accentColorRgb;
    }

    public GraphNodeItem getStartNode() {
        return startNode;
    }

    public GraphNodeItem getEndNode() {
        return endNode;
    }

    public double getWeight() {
        return weight;
    }

    public Line getLine() {
        return line;
    }

    public Text getWeightText() {
        return weightText;
    }

    public int getAccentColorRgb() {
        return accentColorRgb;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean containsNode(GraphNodeItem node) {
        return startNode == node || endNode == node;
    }

    public boolean connects(GraphNodeItem first, GraphNodeItem second) {
        return (startNode == first && endNode == second)
                || (startNode == second && endNode == first);
    }

    public GraphNodeItem getOtherNode(GraphNodeItem node) {
        if (startNode == node) {
            return endNode;
        }
        if (endNode == node) {
            return startNode;
        }
        return null;
    }
}
