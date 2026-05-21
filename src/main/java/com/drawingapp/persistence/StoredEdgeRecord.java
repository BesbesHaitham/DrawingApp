package com.drawingapp.persistence;

public record StoredEdgeRecord(
        int startNodeId,
        int endNodeId,
        double weight,
        int colorRgb
) {
}
