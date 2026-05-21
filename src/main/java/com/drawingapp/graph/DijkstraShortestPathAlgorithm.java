package com.drawingapp.graph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class DijkstraShortestPathAlgorithm implements ShortestPathAlgorithm {

    @Override
    public String getName() {
        return "Dijkstra";
    }

    @Override
    public ShortestPathResult compute(GraphModel graphModel, GraphNodeItem source, GraphNodeItem target) {
        if (source == null || target == null) {
            return ShortestPathResult.failure("Selectionnez les noeuds de depart et d'arrivee.");
        }

        Map<GraphNodeItem, Double> distances = new HashMap<>();
        Map<GraphNodeItem, GraphNodeItem> previousNodes = new HashMap<>();
        Map<GraphNodeItem, GraphEdgeItem> previousEdges = new HashMap<>();
        PriorityQueue<GraphNodeItem> queue =
                new PriorityQueue<>(Comparator.comparingDouble(node -> distances.getOrDefault(node, Double.POSITIVE_INFINITY)));

        for (GraphNodeItem node : graphModel.getNodes()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        distances.put(source, 0.0);
        queue.add(source);

        while (!queue.isEmpty()) {
            GraphNodeItem current = queue.poll();

            if (current == target) {
                break;
            }

            for (GraphEdgeItem edge : graphModel.getIncidentEdges(current)) {
                GraphNodeItem neighbor = edge.getOtherNode(current);
                if (neighbor == null) {
                    continue;
                }

                double candidateDistance = distances.get(current) + edge.getWeight();
                if (candidateDistance < distances.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    distances.put(neighbor, candidateDistance);
                    previousNodes.put(neighbor, current);
                    previousEdges.put(neighbor, edge);
                    queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return ShortestPathSupport.buildResult(source, target, distances, previousNodes, previousEdges);
    }
}
