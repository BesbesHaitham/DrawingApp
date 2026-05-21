package com.drawingapp.graph;

import java.util.List;

public class ShortestPathResult {
    private final boolean success;
    private final String message;
    private final List<GraphNodeItem> pathNodes;
    private final List<GraphEdgeItem> pathEdges;
    private final double totalWeight;

    private ShortestPathResult(
            boolean success,
            String message,
            List<GraphNodeItem> pathNodes,
            List<GraphEdgeItem> pathEdges,
            double totalWeight
    ) {
        this.success = success;
        this.message = message;
        this.pathNodes = pathNodes;
        this.pathEdges = pathEdges;
        this.totalWeight = totalWeight;
    }

    public static ShortestPathResult success(
            List<GraphNodeItem> pathNodes,
            List<GraphEdgeItem> pathEdges,
            double totalWeight
    ) {
        return new ShortestPathResult(true, "", List.copyOf(pathNodes), List.copyOf(pathEdges), totalWeight);
    }

    public static ShortestPathResult failure(String message) {
        return new ShortestPathResult(false, message, List.of(), List.of(), 0.0);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<GraphNodeItem> getPathNodes() {
        return pathNodes;
    }

    public List<GraphEdgeItem> getPathEdges() {
        return pathEdges;
    }

    public double getTotalWeight() {
        return totalWeight;
    }
}
