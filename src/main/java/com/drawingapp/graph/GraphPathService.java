package com.drawingapp.graph;

import java.util.stream.Collectors;

public class GraphPathService {
    public GraphPathComputation computeShortestPath(
            GraphModel graphModel,
            String sourceLabel,
            String targetLabel,
            String algorithmName,
            ShortestPathAlgorithmRegistry algorithmRegistry
    ) {
        GraphNodeItem source = graphModel.getNodeByLabel(sourceLabel);
        GraphNodeItem target = graphModel.getNodeByLabel(targetLabel);
        ShortestPathAlgorithm algorithm = algorithmRegistry.getByName(algorithmName);

        if (source == null || target == null || algorithm == null) {
            return GraphPathComputation.failure("Selectionnez les noeuds et l'algorithme avant le calcul.");
        }

        ShortestPathResult result = algorithm.compute(graphModel, source, target);
        if (!result.isSuccess()) {
            return GraphPathComputation.failure(result.getMessage());
        }

        String pathText = result.getPathNodes().stream()
                .map(GraphNodeItem::getLabel)
                .collect(Collectors.joining(" -> "));

        String message = algorithm.getName() + " : " + pathText
                + " | poids total = " + formatNumber(result.getTotalWeight());

        return GraphPathComputation.success(
                message,
                pathText,
                source,
                target,
                algorithm,
                result
        );
    }

    private String formatNumber(double value) {
        return java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}
