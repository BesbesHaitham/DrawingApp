package com.drawingapp.factory;

import com.drawingapp.model.*;

/**
 * Factory Method pour la création de formes.
 */
public class ShapeFactory {

    public static final String RECTANGLE = "RECTANGLE";
    public static final String CIRCLE = "CIRCLE";
    public static final String LINE = "LINE";
    public static final String TRIANGLE = "TRIANGLE";
    public static final String DIAMOND = "DIAMOND";
    public static final String PENTAGON = "PENTAGON";
    public static final String HEXAGON = "HEXAGON";
    public static final String STAR = "STAR";

    /**
     * Crée une forme selon le type spécifié.
     */
    public static ShapeModel createShape(String type, double startX, double startY, 
                                         double endX, double endY, int colorRGB) {
        return switch (type) {
            case RECTANGLE -> new RectangleShape(startX, startY, endX, endY, colorRGB);
            case CIRCLE -> new CircleShape(startX, startY, endX, endY, colorRGB);
            case LINE -> new LineShape(startX, startY, endX, endY, colorRGB);
            case TRIANGLE -> new TriangleShape(startX, startY, endX, endY, colorRGB);
            case DIAMOND -> new DiamondShape(startX, startY, endX, endY, colorRGB);
            case PENTAGON -> new PentagonShape(startX, startY, endX, endY, colorRGB);
            case HEXAGON -> new HexagonShape(startX, startY, endX, endY, colorRGB);
            case STAR -> new StarShape(startX, startY, endX, endY, colorRGB);
            default -> throw new IllegalArgumentException("Type de forme inconnu: " + type);
        };
    }
}
