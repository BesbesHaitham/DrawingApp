package com.drawingapp.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GraphModel {
    private final List<GraphNodeItem> nodes;
    private final List<GraphEdgeItem> edges;

    public GraphModel() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public void addNode(GraphNodeItem node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
            nodes.sort(Comparator.comparingInt(GraphNodeItem::getId));
        }
    }

    public void removeNode(GraphNodeItem node) {
        nodes.remove(node);
    }

    public void addEdge(GraphEdgeItem edge) {
        if (!edges.contains(edge)) {
            edges.add(edge);
        }
    }

    public void removeEdge(GraphEdgeItem edge) {
        edges.remove(edge);
    }

    public List<GraphNodeItem> getNodes() {
        return List.copyOf(nodes);
    }

    public List<GraphEdgeItem> getEdges() {
        return List.copyOf(edges);
    }

    public List<GraphEdgeItem> getIncidentEdges(GraphNodeItem node) {
        return edges.stream()
                .filter(edge -> edge.containsNode(node))
                .toList();
    }

    public GraphNodeItem getNodeByLabel(String label) {
        return nodes.stream()
                .filter(node -> node.getLabel().equals(label))
                .findFirst()
                .orElse(null);
    }

    public boolean hasEdgeBetween(GraphNodeItem first, GraphNodeItem second) {
        return edges.stream().anyMatch(edge -> edge.connects(first, second));
    }

    public void clear() {
        nodes.clear();
        edges.clear();
    }
}
