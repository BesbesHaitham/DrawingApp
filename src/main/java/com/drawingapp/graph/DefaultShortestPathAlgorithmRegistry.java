package com.drawingapp.graph;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultShortestPathAlgorithmRegistry implements ShortestPathAlgorithmRegistry {
    private final Map<String, ShortestPathAlgorithm> algorithms;

    public DefaultShortestPathAlgorithmRegistry() {
        this.algorithms = new LinkedHashMap<>();
        this.algorithms.put("Dijkstra", new DijkstraShortestPathAlgorithm());
        this.algorithms.put("Bellman-Ford", new BellmanFordShortestPathAlgorithm());
    }

    @Override
    public List<String> algorithmNames() {
        return List.copyOf(algorithms.keySet());
    }

    @Override
    public ShortestPathAlgorithm getByName(String name) {
        return algorithms.get(name);
    }
}
