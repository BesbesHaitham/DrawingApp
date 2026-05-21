package com.drawingapp.persistence;

import java.util.List;

public record DrawingFileData(
        int nodeSequence,
        List<StoredShapeRecord> shapeRecords,
        List<StoredNodeRecord> nodeRecords,
        List<StoredEdgeRecord> edgeRecords
) {
}
