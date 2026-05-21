package com.drawingapp.persistence;

import java.util.ArrayList;
import java.util.List;

public class DrawingFileCodec {
    private static final String DRAWING_FILE_SIGNATURE = "DRAWING_STUDIO_FILE_V1";
    private static final String SHAPE_RECORD_PREFIX = "SHAPE";
    private static final String NODE_RECORD_PREFIX = "NODE";
    private static final String EDGE_RECORD_PREFIX = "EDGE";
    private static final String NODE_SEQUENCE_RECORD_PREFIX = "NODE_SEQUENCE";

    public String serialize(DrawingFileData data) {
        String lineSeparator = System.lineSeparator();
        StringBuilder builder = new StringBuilder();
        builder.append(DRAWING_FILE_SIGNATURE).append(lineSeparator);
        builder.append(NODE_SEQUENCE_RECORD_PREFIX).append("|").append(data.nodeSequence()).append(lineSeparator);

        for (StoredShapeRecord record : data.shapeRecords()) {
            builder.append(SHAPE_RECORD_PREFIX).append("|")
                    .append(record.type()).append("|")
                    .append(record.startX()).append("|")
                    .append(record.startY()).append("|")
                    .append(record.endX()).append("|")
                    .append(record.endY()).append("|")
                    .append(String.format("%06X", record.colorRgb()))
                    .append(lineSeparator);
        }

        for (StoredNodeRecord record : data.nodeRecords()) {
            builder.append(NODE_RECORD_PREFIX).append("|")
                    .append(record.id()).append("|")
                    .append(record.label()).append("|")
                    .append(record.centerX()).append("|")
                    .append(record.centerY()).append("|")
                    .append(String.format("%06X", record.colorRgb()))
                    .append(lineSeparator);
        }

        for (StoredEdgeRecord record : data.edgeRecords()) {
            builder.append(EDGE_RECORD_PREFIX).append("|")
                    .append(record.startNodeId()).append("|")
                    .append(record.endNodeId()).append("|")
                    .append(record.weight()).append("|")
                    .append(String.format("%06X", record.colorRgb()))
                    .append(lineSeparator);
        }

        return builder.toString();
    }

    public DrawingFileData parse(List<String> lines) {
        if (lines.isEmpty() || !DRAWING_FILE_SIGNATURE.equals(lines.get(0).trim())) {
            throw new IllegalArgumentException("Format de fichier Drawing Studio invalide.");
        }

        List<StoredShapeRecord> shapeRecords = new ArrayList<>();
        List<StoredNodeRecord> nodeRecords = new ArrayList<>();
        List<StoredEdgeRecord> edgeRecords = new ArrayList<>();
        int loadedNodeSequence = 0;

        for (int index = 1; index < lines.size(); index++) {
            String line = lines.get(index).trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\|", -1);
            switch (parts[0]) {
                case NODE_SEQUENCE_RECORD_PREFIX -> {
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Ligne invalide: " + line);
                    }
                    loadedNodeSequence = Integer.parseInt(parts[1]);
                }
                case SHAPE_RECORD_PREFIX -> {
                    if (parts.length != 7) {
                        throw new IllegalArgumentException("Ligne de forme invalide: " + line);
                    }
                    shapeRecords.add(new StoredShapeRecord(
                            parts[1],
                            Double.parseDouble(parts[2]),
                            Double.parseDouble(parts[3]),
                            Double.parseDouble(parts[4]),
                            Double.parseDouble(parts[5]),
                            Integer.parseInt(parts[6], 16)
                    ));
                }
                case NODE_RECORD_PREFIX -> {
                    if (parts.length != 6) {
                        throw new IllegalArgumentException("Ligne de noeud invalide: " + line);
                    }
                    nodeRecords.add(new StoredNodeRecord(
                            Integer.parseInt(parts[1]),
                            parts[2],
                            Double.parseDouble(parts[3]),
                            Double.parseDouble(parts[4]),
                            Integer.parseInt(parts[5], 16)
                    ));
                }
                case EDGE_RECORD_PREFIX -> {
                    if (parts.length != 5) {
                        throw new IllegalArgumentException("Ligne d'arete invalide: " + line);
                    }
                    edgeRecords.add(new StoredEdgeRecord(
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]),
                            Double.parseDouble(parts[3]),
                            Integer.parseInt(parts[4], 16)
                    ));
                }
                default -> throw new IllegalArgumentException("Type d'enregistrement inconnu: " + parts[0]);
            }
        }

        return new DrawingFileData(loadedNodeSequence, shapeRecords, nodeRecords, edgeRecords);
    }
}
