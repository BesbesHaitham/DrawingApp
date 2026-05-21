package com.drawingapp.command;

import com.drawingapp.graph.GraphEdgeItem;
import com.drawingapp.graph.GraphModel;
import com.drawingapp.graph.GraphNodeItem;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Pane;

public class DeleteGraphNodeCommand implements Command {
    private final Pane canvas;
    private final GraphModel graphModel;
    private final GraphNodeItem node;
    private final List<GraphEdgeItem> incidentEdges;

    public DeleteGraphNodeCommand(Pane canvas, GraphModel graphModel, GraphNodeItem node) {
        this.canvas = canvas;
        this.graphModel = graphModel;
        this.node = node;
        this.incidentEdges = new ArrayList<>(graphModel.getIncidentEdges(node));
    }

    @Override
    public void execute() {
        for (GraphEdgeItem edge : incidentEdges) {
            canvas.getChildren().remove(edge.getLine());
            canvas.getChildren().remove(edge.getWeightText());
            graphModel.removeEdge(edge);
        }

        canvas.getChildren().remove(node.getCircle());
        canvas.getChildren().remove(node.getLabelText());
        graphModel.removeNode(node);
    }

    @Override
    public void undo() {
        graphModel.addNode(node);
        if (!canvas.getChildren().contains(node.getCircle())) {
            canvas.getChildren().add(node.getCircle());
        }
        if (!canvas.getChildren().contains(node.getLabelText())) {
            canvas.getChildren().add(node.getLabelText());
        }

        for (GraphEdgeItem edge : incidentEdges) {
            if (!canvas.getChildren().contains(edge.getLine())) {
                canvas.getChildren().add(0, edge.getLine());
            }
            if (!canvas.getChildren().contains(edge.getWeightText())) {
                canvas.getChildren().add(edge.getWeightText());
            }
            graphModel.addEdge(edge);
        }
    }

    @Override
    public String getDescription() {
        return "Suppression du noeud " + node.getLabel();
    }
}
