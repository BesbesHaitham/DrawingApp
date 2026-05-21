package com.drawingapp.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

final class ShortestPathSupport {
    private ShortestPathSupport() {
    }

    static ShortestPathResult buildResult(
            GraphNodeItem source,
            GraphNodeItem target,
            Map<GraphNodeItem, Double> distances,
            Map<GraphNodeItem, GraphNodeItem> previousNodes,
            Map<GraphNodeItem, GraphEdgeItem> previousEdges
    ) {
        Double totalWeight = distances.get(target);
        if (totalWeight == null || Double.isInfinite(totalWeight)) {
            return ShortestPathResult.failure(
                    "Aucun chemin trouve entre " + source.getLabel() + " et " + target.getLabel() + "."
            );
        }

        List<GraphNodeItem> pathNodes = new ArrayList<>();
        List<GraphEdgeItem> pathEdges = new ArrayList<>();

        GraphNodeItem current = target;
        pathNodes.add(current);

        while (current != source) {
            GraphEdgeItem edge = previousEdges.get(current);
            GraphNodeItem previous = previousNodes.get(current);

            if (edge == null || previous == null) {
                return ShortestPathResult.failure(
                        "Le chemin n'a pas pu etre reconstruit correctement."
                );
            }

            pathEdges.add(edge);
            pathNodes.add(previous);
            current = previous;
        }

        Collections.reverse(pathNodes);
        Collections.reverse(pathEdges);
        return ShortestPathResult.success(pathNodes, pathEdges, totalWeight);
    }
}
