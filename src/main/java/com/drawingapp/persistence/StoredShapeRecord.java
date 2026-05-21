package com.drawingapp.persistence;

public record StoredShapeRecord(
        String type,
        double startX,
        double startY,
        double endX,
        double endY,
        int colorRgb
) {
}
