package com.drawingapp.graph;

import java.math.BigDecimal;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GraphCanvasService {
    public interface EdgeWeightProvider {
        Double requestWeight(GraphNodeItem first, GraphNodeItem second);
    }

    public record EdgeCreationOutcome(
            GraphEdgeItem createdEdge,
            GraphNodeItem selectedNode,
            boolean clearSelection,
            String message
    ) {
    }

    private static final double GRAPH_NODE_RADIUS = 22.0;
    private static final double GRAPH_NODE_GAP = 10.0;
    private static final Paint GRAPH_PATH_FILL = Color.web("#fff0bf");
    private static final Paint GRAPH_PATH_STROKE = Color.web("#d97706");
    private static final Paint SELECTION_STROKE = Color.web("#0f766e");
    private static final String ELEMENT_KIND_KEY = "elementKind";
    private static final String GRAPH_NODE_KEY = "graphNode";
    private static final String GRAPH_EDGE_KEY = "graphEdge";
    private static final String ELEMENT_GRAPH_NODE = "GRAPH_NODE";
    private static final String ELEMENT_GRAPH_EDGE = "GRAPH_EDGE";
    private static final String ELEMENT_GRAPH_NODE_LABEL = "GRAPH_NODE_LABEL";
    private static final String ELEMENT_GRAPH_EDGE_LABEL = "GRAPH_EDGE_LABEL";
    private static final String BASE_STROKE_KEY = "baseStroke";
    private static final String BASE_STROKE_WIDTH_KEY = "baseStrokeWidth";
    private static final String BASE_FILL_KEY = "baseFill";

    private int graphNodeSequence;
    private GraphNodeItem pendingEdgeStartNode;

    public GraphNodeItem createGraphNode(double canvasX, double canvasY, double canvasWidth, double canvasHeight, GraphModel graphModel, int accentColorRgb) {
        double centerX = clampGraphCoordinateX(canvasX, canvasWidth);
        double centerY = clampGraphCoordinateY(canvasY, canvasHeight);
        if (isNodeTooClose(centerX, centerY, graphModel)) {
            return null;
        }
        int nodeId = ++graphNodeSequence;
        String label = buildNodeLabel(nodeId - 1);
        return createGraphNode(nodeId, label, centerX, centerY, accentColorRgb);
    }

    public GraphNodeItem createGraphNode(int nodeId, String label, double centerX, double centerY, int accentColorRgb) {
        Color accentColor = toFxColor(accentColorRgb);
        Color fillColor = accentColor.deriveColor(0, 1.0, 1.0, 0.16);
        Color strokeColor = accentColor.deriveColor(0, 1.0, 0.74, 1.0);
        Color labelColor = accentColor.deriveColor(0, 1.0, 0.44, 1.0);

        Circle circle = new Circle(centerX, centerY, GRAPH_NODE_RADIUS);
        circle.setFill(fillColor);
        circle.setStroke(strokeColor);
        circle.setStrokeWidth(2.2);

        Text labelText = createCenteredText(label, centerX, centerY + 0.5, 13, toHexColor(labelColor));

        GraphNodeItem node = new GraphNodeItem(nodeId, label, circle, labelText, accentColorRgb);
        prepareGraphNode(node);
        return node;
    }

    public GraphEdgeItem createGraphEdge(GraphNodeItem first, GraphNodeItem second, double weight, int accentColorRgb) {
        Color accentColor = toFxColor(accentColorRgb);
        Color strokeColor = accentColor.deriveColor(0, 1.0, 0.78, 1.0);
        Color weightColor = accentColor.deriveColor(0, 1.0, 0.46, 1.0);

        Line line = new Line(first.getCenterX(), first.getCenterY(), second.getCenterX(), second.getCenterY());
        line.setStroke(strokeColor);
        line.setStrokeWidth(3);

        double midX = (first.getCenterX() + second.getCenterX()) / 2.0;
        double midY = (first.getCenterY() + second.getCenterY()) / 2.0;
        Text weightText = createCenteredText(formatNumber(weight), midX, midY - 10, 12, toHexColor(weightColor));

        GraphEdgeItem edge = new GraphEdgeItem(first, second, weight, line, weightText, accentColorRgb);
        prepareGraphEdge(edge);
        return edge;
    }

    public EdgeCreationOutcome selectNodeForEdge(
            GraphNodeItem clickedNode,
            GraphModel graphModel,
            EdgeWeightProvider edgeWeightProvider,
            int accentColorRgb
    ) {
        if (pendingEdgeStartNode == null) {
            pendingEdgeStartNode = clickedNode;
            return new EdgeCreationOutcome(null, clickedNode, false,
                    "Depart choisi: " + clickedNode.getLabel() + ". Cliquez le noeud d'arrivee.");
        }

        if (pendingEdgeStartNode == clickedNode) {
            return new EdgeCreationOutcome(null, pendingEdgeStartNode, false,
                    "Choisissez un autre noeud pour terminer l'arete.");
        }

        if (graphModel.hasEdgeBetween(pendingEdgeStartNode, clickedNode)) {
            String message = "Une arete existe deja entre " + pendingEdgeStartNode.getLabel()
                    + " et " + clickedNode.getLabel() + ".";
            pendingEdgeStartNode = null;
            return new EdgeCreationOutcome(null, null, true, message);
        }

        Double weight = edgeWeightProvider.requestWeight(pendingEdgeStartNode, clickedNode);
        if (weight == null) {
            pendingEdgeStartNode = null;
            return new EdgeCreationOutcome(null, null, true, "Creation de l'arete annulee.");
        }

        GraphEdgeItem createdEdge = createGraphEdge(pendingEdgeStartNode, clickedNode, weight, accentColorRgb);
        pendingEdgeStartNode = null;
        return new EdgeCreationOutcome(createdEdge, null, false,
                "Arete " + createdEdge.getStartNode().getLabel() + " - "
                        + createdEdge.getEndNode().getLabel() + " ajoutee.");
    }

    public void clearPendingEdgeSelection() {
        pendingEdgeStartNode = null;
    }

    public void setNodeSequence(int value) {
        graphNodeSequence = Math.max(0, value);
    }

    public int getNodeSequence() {
        return graphNodeSequence;
    }

    public void applyGraphNodeVisual(GraphNodeItem node, boolean selected) {
        Circle circle = node.getCircle();
        Text label = node.getLabelText();
        Paint baseFill = (Paint) circle.getProperties().getOrDefault(BASE_FILL_KEY, circle.getFill());
        Paint baseStroke = (Paint) circle.getProperties().getOrDefault(BASE_STROKE_KEY, circle.getStroke());
        Paint baseLabelFill = (Paint) label.getProperties().getOrDefault(BASE_FILL_KEY, label.getFill());
        double baseStrokeWidth =
                ((Number) circle.getProperties().getOrDefault(BASE_STROKE_WIDTH_KEY, circle.getStrokeWidth()))
                        .doubleValue();

        circle.setFill(node.isHighlighted() ? GRAPH_PATH_FILL : baseFill);
        circle.setStroke(selected ? SELECTION_STROKE : node.isHighlighted() ? GRAPH_PATH_STROKE : baseStroke);
        circle.setStrokeWidth(selected ? baseStrokeWidth + 1.8 : node.isHighlighted() ? baseStrokeWidth + 1.2 : baseStrokeWidth);
        circle.setEffect(selected
                ? new DropShadow(18, Color.rgb(15, 118, 110, 0.22))
                : node.isHighlighted()
                ? new DropShadow(18, Color.rgb(217, 119, 6, 0.20))
                : null);
        label.setFill(selected
                ? SELECTION_STROKE
                : node.isHighlighted()
                ? Color.web("#9a3412")
                : baseLabelFill);
    }

    public void applyGraphEdgeVisual(GraphEdgeItem edge, boolean selected) {
        Line line = edge.getLine();
        Text weightText = edge.getWeightText();
        Paint baseStroke = (Paint) line.getProperties().getOrDefault(BASE_STROKE_KEY, line.getStroke());
        Paint baseWeightFill = (Paint) weightText.getProperties().getOrDefault(BASE_FILL_KEY, weightText.getFill());
        double baseStrokeWidth =
                ((Number) line.getProperties().getOrDefault(BASE_STROKE_WIDTH_KEY, line.getStrokeWidth()))
                        .doubleValue();

        line.setStroke(selected ? SELECTION_STROKE : edge.isHighlighted() ? GRAPH_PATH_STROKE : baseStroke);
        line.setStrokeWidth(selected ? baseStrokeWidth + 1.6 : edge.isHighlighted() ? baseStrokeWidth + 1.2 : baseStrokeWidth);
        line.setEffect(selected
                ? new DropShadow(16, Color.rgb(15, 118, 110, 0.18))
                : edge.isHighlighted()
                ? new DropShadow(16, Color.rgb(217, 119, 6, 0.18))
                : null);
        weightText.setFill(selected
                ? SELECTION_STROKE
                : edge.isHighlighted()
                ? Color.web("#9a3412")
                : baseWeightFill);
        weightText.setUnderline(edge.isHighlighted());
    }

    public void bringGraphNodeToFront(GraphNodeItem node) {
        node.getCircle().toFront();
        node.getLabelText().toFront();
    }

    private void prepareGraphNode(GraphNodeItem node) {
        Circle circle = node.getCircle();
        circle.getProperties().put(ELEMENT_KIND_KEY, ELEMENT_GRAPH_NODE);
        circle.getProperties().put(GRAPH_NODE_KEY, node);
        circle.getProperties().put(BASE_STROKE_KEY, circle.getStroke());
        circle.getProperties().put(BASE_STROKE_WIDTH_KEY, circle.getStrokeWidth());
        circle.getProperties().put(BASE_FILL_KEY, circle.getFill());
        circle.setMouseTransparent(false);
        Text labelText = node.getLabelText();
        labelText.getProperties().put(ELEMENT_KIND_KEY, ELEMENT_GRAPH_NODE_LABEL);
        labelText.getProperties().put(BASE_FILL_KEY, labelText.getFill());
        labelText.setMouseTransparent(true);
        applyGraphNodeVisual(node, false);
    }

    private void prepareGraphEdge(GraphEdgeItem edge) {
        Line line = edge.getLine();
        line.getProperties().put(ELEMENT_KIND_KEY, ELEMENT_GRAPH_EDGE);
        line.getProperties().put(GRAPH_EDGE_KEY, edge);
        line.getProperties().put(BASE_STROKE_KEY, line.getStroke());
        line.getProperties().put(BASE_STROKE_WIDTH_KEY, line.getStrokeWidth());
        line.getProperties().put(BASE_FILL_KEY, line.getFill());
        line.setMouseTransparent(false);
        Text weightText = edge.getWeightText();
        weightText.getProperties().put(ELEMENT_KIND_KEY, ELEMENT_GRAPH_EDGE_LABEL);
        weightText.getProperties().put(BASE_FILL_KEY, weightText.getFill());
        weightText.setMouseTransparent(true);
        applyGraphEdgeVisual(edge, false);
    }

    private double clampGraphCoordinateX(double value, double canvasWidth) {
        return Math.max(GRAPH_NODE_RADIUS + 12, Math.min(value, Math.max(GRAPH_NODE_RADIUS + 12, canvasWidth - GRAPH_NODE_RADIUS - 12)));
    }

    private double clampGraphCoordinateY(double value, double canvasHeight) {
        return Math.max(GRAPH_NODE_RADIUS + 12, Math.min(value, Math.max(GRAPH_NODE_RADIUS + 12, canvasHeight - GRAPH_NODE_RADIUS - 12)));
    }

    private boolean isNodeTooClose(double centerX, double centerY, GraphModel graphModel) {
        double minDistance = GRAPH_NODE_RADIUS * 2 + GRAPH_NODE_GAP;
        for (GraphNodeItem node : graphModel.getNodes()) {
            double distance = Math.hypot(centerX - node.getCenterX(), centerY - node.getCenterY());
            if (distance < minDistance) {
                return true;
            }
        }
        return false;
    }

    private Text createCenteredText(String content, double centerX, double centerY, int fontSize, String color) {
        Text text = new Text(content);
        text.setFont(Font.font("Segoe UI Semibold", fontSize));
        text.setFill(Color.web(color));
        text.applyCss();
        double width = text.getLayoutBounds().getWidth();
        text.setX(centerX - (width / 2.0));
        text.setY(centerY + 4);
        return text;
    }

    private String buildNodeLabel(int index) {
        StringBuilder builder = new StringBuilder();
        int value = index;
        do {
            builder.insert(0, (char) ('A' + (value % 26)));
            value = (value / 26) - 1;
        } while (value >= 0);
        return builder.toString();
    }

    private Color toFxColor(int rgb) {
        return Color.rgb((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

    private String toHexColor(Color color) {
        return String.format("#%02X%02X%02X",
                (int) Math.round(color.getRed() * 255),
                (int) Math.round(color.getGreen() * 255),
                (int) Math.round(color.getBlue() * 255)
        );
    }

    private String formatNumber(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}
