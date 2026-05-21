package com.drawingapp.graph;

public interface ShortestPathAlgorithm {
    String getName();

    ShortestPathResult compute(GraphModel graphModel, GraphNodeItem source, GraphNodeItem target);
}
