package com.drawingapp.command;

import com.drawingapp.graph.GraphModel;
import com.drawingapp.graph.GraphNodeItem;
import javafx.scene.layout.Pane;

public class AddGraphNodeCommand implements Command {
    private final Pane canvas;
    private final GraphModel graphModel;
    private final GraphNodeItem node;

    public AddGraphNodeCommand(Pane canvas, GraphModel graphModel, GraphNodeItem node) {
        this.canvas = canvas;
        this.graphModel = graphModel;
        this.node = node;
    }

    @Override
    public void execute() {
        if (!canvas.getChildren().contains(node.getCircle())) {
            canvas.getChildren().add(node.getCircle());
        }
        if (!canvas.getChildren().contains(node.getLabelText())) {
            canvas.getChildren().add(node.getLabelText());
        }
        graphModel.addNode(node);
    }

    @Override
    public void undo() {
        canvas.getChildren().remove(node.getCircle());
        canvas.getChildren().remove(node.getLabelText());
        graphModel.removeNode(node);
    }

    @Override
    public String getDescription() {
        return "Ajout du noeud " + node.getLabel();
    }
}
