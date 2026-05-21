package com.drawingapp.graph;

import java.util.HashMap;
import java.util.Map;

public class BellmanFordShortestPathAlgorithm implements ShortestPathAlgorithm {

    @Override
    public String getName() {
        return "Bellman-Ford";
    }

    @Override
    public ShortestPathResult compute(GraphModel graphModel, GraphNodeItem source, GraphNodeItem target) {
        if (source == null || target == null) {
            return ShortestPathResult.failure("Selectionnez les noeuds de depart et d'arrivee.");
        }

        Map<GraphNodeItem, Double> distances = new HashMap<>();
        Map<GraphNodeItem, GraphNodeItem> previousNodes = new HashMap<>();
        Map<GraphNodeItem, GraphEdgeItem> previousEdges = new HashMap<>();

        for (GraphNodeItem node : graphModel.getNodes()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        distances.put(source, 0.0);

        int relaxations = Math.max(0, graphModel.getNodes().size() - 1);
        for (int index = 0; index < relaxations; index++) {
            boolean updated = false;

            for (GraphEdgeItem edge : graphModel.getEdges()) {
                updated |= relaxEdge(edge.getStartNode(), edge.getEndNode(), edge, distances, previousNodes, previousEdges);
                updated |= relaxEdge(edge.getEndNode(), edge.getStartNode(), edge, distances, previousNodes, previousEdges);
            }

            if (!updated) {
                break;
            }
        }

        return ShortestPathSupport.buildResult(source, target, distances, previousNodes, previousEdges);
    }

    private boolean relaxEdge(
            GraphNodeItem from,
            GraphNodeItem to,
            GraphEdgeItem edge,
            Map<GraphNodeItem, Double> distances,
            Map<GraphNodeItem, GraphNodeItem> previousNodes,
            Map<GraphNodeItem, GraphEdgeItem> previousEdges
    ) {
        double sourceDistance = distances.getOrDefault(from, Double.POSITIVE_INFINITY);
        if (Double.isInfinite(sourceDistance)) {
            return false;
        }

        double candidateDistance = sourceDistance + edge.getWeight();
        if (candidateDistance < distances.getOrDefault(to, Double.POSITIVE_INFINITY)) {
            distances.put(to, candidateDistance);
            previousNodes.put(to, from);
            previousEdges.put(to, edge);
            return true;
        }

        return false;
    }
}
