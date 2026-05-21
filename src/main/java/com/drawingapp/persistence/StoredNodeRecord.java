package com.drawingapp.persistence;

public record StoredNodeRecord(
        int id,
        String label,
        double centerX,
        double centerY,
        int colorRgb
) {
}
