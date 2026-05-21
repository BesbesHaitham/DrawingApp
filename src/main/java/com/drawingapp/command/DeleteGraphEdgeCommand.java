package com.drawingapp.command;

import com.drawingapp.graph.GraphEdgeItem;
import com.drawingapp.graph.GraphModel;
import javafx.scene.layout.Pane;

public class DeleteGraphEdgeCommand implements Command {
    private final Pane canvas;
    private final GraphModel graphModel;
    private final GraphEdgeItem edge;

    public DeleteGraphEdgeCommand(Pane canvas, GraphModel graphModel, GraphEdgeItem edge) {
        this.canvas = canvas;
        this.graphModel = graphModel;
        this.edge = edge;
    }

    @Override
    public void execute() {
        canvas.getChildren().remove(edge.getLine());
        canvas.getChildren().remove(edge.getWeightText());
        graphModel.removeEdge(edge);
    }

    @Override
    public void undo() {
        if (!canvas.getChildren().contains(edge.getLine())) {
            canvas.getChildren().add(0, edge.getLine());
        }
        if (!canvas.getChildren().contains(edge.getWeightText())) {
            canvas.getChildren().add(edge.getWeightText());
        }
        graphModel.addEdge(edge);
    }

    @Override
    public String getDescription() {
        return "Suppression de l'arete " + edge.getStartNode().getLabel()
                + " - " + edge.getEndNode().getLabel();
    }
}
