package com.drawingapp.graph;

import java.util.List;

public interface ShortestPathAlgorithmRegistry {
    List<String> algorithmNames();

    ShortestPathAlgorithm getByName(String name);
}
