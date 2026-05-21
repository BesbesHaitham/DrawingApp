package com.drawingapp.graph;

public record GraphPathComputation(
        boolean success,
        String message,
        String pathText,
        GraphNodeItem source,
        GraphNodeItem target,
        ShortestPathAlgorithm algorithm,
        ShortestPathResult result
) {
    public static GraphPathComputation failure(String message) {
        return new GraphPathComputation(false, message, "", null, null, null, null);
    }

    public static GraphPathComputation success(
            String message,
            String pathText,
            GraphNodeItem source,
            GraphNodeItem target,
            ShortestPathAlgorithm algorithm,
            ShortestPathResult result
    ) {
        return new GraphPathComputation(true, message, pathText, source, target, algorithm, result);
    }
}
