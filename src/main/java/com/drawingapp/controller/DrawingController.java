package com.drawingapp.controller;

import com.drawingapp.command.AddGraphEdgeCommand;
import com.drawingapp.command.AddGraphNodeCommand;
import com.drawingapp.command.ClearCanvasCommand;
import com.drawingapp.command.Command;
import com.drawingapp.command.DeleteCommand;
import com.drawingapp.command.DeleteGraphEdgeCommand;
import com.drawingapp.command.DeleteGraphNodeCommand;
import com.drawingapp.command.DrawCommand;
import com.drawingapp.command.UndoManager;
import com.drawingapp.dao.DrawingDAO;
import com.drawingapp.factory.ShapeFactory;
import com.drawingapp.graph.DefaultShortestPathAlgorithmRegistry;
import com.drawingapp.graph.GraphEdgeItem;
import com.drawingapp.graph.GraphModel;
import com.drawingapp.graph.GraphNodeItem;
import com.drawingapp.graph.GraphPathComputation;
import com.drawingapp.graph.GraphPathService;
import com.drawingapp.graph.ShortestPathAlgorithm;
import com.drawingapp.graph.ShortestPathAlgorithmRegistry;
import com.drawingapp.graph.ShortestPathResult;
import com.drawingapp.model.ShapeModel;
import com.drawingapp.observer.ActionObservable;
import com.drawingapp.observer.ActionObserver;
import com.drawingapp.persistence.DrawingFileCodec;
import com.drawingapp.persistence.DrawingFileData;
import com.drawingapp.persistence.StoredEdgeRecord;
import com.drawingapp.persistence.StoredNodeRecord;
import com.drawingapp.persistence.StoredShapeRecord;
import com.drawingapp.strategy.DefaultLogStrategyFactory;
import com.drawingapp.strategy.LogStrategy;
import com.drawingapp.strategy.LogStrategyFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DrawingController implements ActionObserver {
    private static final int DEFAULT_SHAPE_COLOR = 0x2D7FF9;
    private static final double MIN_DRAG_DISTANCE = 6.0;
    private static final double DEFAULT_RECT_WIDTH = 120.0;
    private static final double DEFAULT_RECT_HEIGHT = 80.0;
    private static final double DEFAULT_CIRCLE_SIZE = 96.0;
    private static final double DEFAULT_LINE_LENGTH = 140.0;
    private static final double GRAPH_NODE_RADIUS = 22.0;
    private static final double GRAPH_NODE_GAP = 10.0;
    private static final Paint GRAPH_NODE_FILL = Color.web("#f9fbff");
    private static final Paint GRAPH_NODE_STROKE = Color.web("#31587a");
    private static final Paint GRAPH_EDGE_STROKE = Color.web("#60748a");
    private static final Paint GRAPH_PATH_FILL = Color.web("#fff0bf");
    private static final Paint GRAPH_PATH_STROKE = Color.web("#d97706");
    private static final Paint SELECTION_STROKE = Color.web("#0f766e");
    private static final String DRAWING_FILE_EXTENSION = ".drawing";
    private static final String BASE_STROKE_KEY = "baseStroke";
    private static final String BASE_STROKE_WIDTH_KEY = "baseStrokeWidth";
    private static final String BASE_FILL_KEY = "baseFill";
    private static final String SHAPE_TYPE_KEY = "shapeType";
    private static final String SHAPE_START_X_KEY = "shapeStartX";
    private static final String SHAPE_START_Y_KEY = "shapeStartY";
    private static final String SHAPE_END_X_KEY = "shapeEndX";
    private static final String SHAPE_END_Y_KEY = "shapeEndY";
    private static final String SHAPE_COLOR_RGB_KEY = "shapeColorRgb";
    private static final String ELEMENT_KIND_KEY = "elementKind";
    private static final String GRAPH_NODE_KEY = "graphNode";
    private static final String GRAPH_EDGE_KEY = "graphEdge";
    private static final String ELEMENT_DRAW_SHAPE = "DRAW_SHAPE";
    private static final String ELEMENT_GRAPH_NODE = "GRAPH_NODE";
    private static final String ELEMENT_GRAPH_EDGE = "GRAPH_EDGE";
    private static final String ELEMENT_GRAPH_NODE_LABEL = "GRAPH_NODE_LABEL";
    private static final String ELEMENT_GRAPH_EDGE_LABEL = "GRAPH_EDGE_LABEL";
    private static final String TOOL_SELECT = "SELECT";
    private static final String TOOL_GRAPH_NODE = "GRAPH_NODE_TOOL";
    private static final String TOOL_GRAPH_EDGE = "GRAPH_EDGE_TOOL";
    private static final Path DRAWINGS_DIRECTORY = Paths.get("data", "drawings");
    private static final DateTimeFormatter HISTORY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    private BorderPane root;
    @FXML
    private HBox headerBar;
    @FXML
    private Pane drawingCanvas;
    @FXML
    private VBox emptyStateBox;
    @FXML
    private Button selectBtn;
    @FXML
    private Button rectangleBtn;
    @FXML
    private Button circleBtn;
    @FXML
    private Button lineBtn;
    @FXML
    private Button triangleBtn;
    @FXML
    private Button diamondBtn;
    @FXML
    private Button pentagonBtn;
    @FXML
    private Button hexagonBtn;
    @FXML
    private Button starBtn;
    @FXML
    private Button colorBlueBtn;
    @FXML
    private Button colorTealBtn;
    @FXML
    private Button colorGreenBtn;
    @FXML
    private Button colorYellowBtn;
    @FXML
    private Button colorOrangeBtn;
    @FXML
    private Button colorRedBtn;
    @FXML
    private Button colorPurpleBtn;
    @FXML
    private Button colorGrayBtn;
    @FXML
    private ColorPicker customColorPicker;
    @FXML
    private Pane activeColorPreview;
    @FXML
    private Label activeColorValueLabel;
    @FXML
    private Button graphNodeBtn;
    @FXML
    private Button graphEdgeBtn;
    @FXML
    private Button undoBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private Button redoBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private ComboBox<String> logStrategyCombo;
    @FXML
    private Button saveBtn;
    @FXML
    private Button openBtn;
    @FXML
    private Button quickOpenBtn;
    @FXML
    private ComboBox<String> startNodeCombo;
    @FXML
    private ComboBox<String> endNodeCombo;
    @FXML
    private ComboBox<String> algorithmCombo;
    @FXML
    private Button computePathBtn;
    @FXML
    private Button clearPathBtn;
    @FXML
    private Label graphSummaryLabel;
    @FXML
    private Label pathResultLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label activeToolBadge;
    @FXML
    private ListView<String> historyListView;
    @FXML
    private Button minimizeBtn;
    @FXML
    private Button maximizeBtn;
    @FXML
    private Button closeBtn;

    private final UndoManager undoManager;
    private final ActionObservable actionObservable;
    private final LogStrategyFactory logStrategyFactory;
    private final ShortestPathAlgorithmRegistry shortestPathAlgorithmRegistry;
    private final GraphPathService graphPathService;
    private final DrawingFileCodec drawingFileCodec;
    private final ObservableList<String> actionHistory;
    private final GraphModel graphModel;

    private LogStrategy logStrategy;
    private String activeTool;
    private double startX;
    private double startY;
    private boolean drawingGestureActive;
    private Shape previewShape;
    private Shape selectedShape;
    private boolean windowControlsConfigured;
    private double windowDragOffsetX;
    private double windowDragOffsetY;
    private int graphNodeSequence;
    private GraphNodeItem pendingEdgeStartNode;
    private boolean pathVisualizationActive;
    private boolean updatingGraphControls;
    private boolean updatingColorControls;
    private int currentShapeColorRgb;
    private Button activeColorSwatchButton;
    private Path lastDrawingDirectory;

    public DrawingController() {
        this(
                new UndoManager(),
                new ActionObservable(),
                new GraphModel(),
                new DefaultLogStrategyFactory(new DrawingDAO()),
                new DefaultShortestPathAlgorithmRegistry()
        );
    }

    public DrawingController(
            UndoManager undoManager,
            ActionObservable actionObservable,
            GraphModel graphModel,
            LogStrategyFactory logStrategyFactory,
            ShortestPathAlgorithmRegistry shortestPathAlgorithmRegistry
    ) {
        this.undoManager = undoManager;
        this.actionObservable = actionObservable;
        this.graphModel = graphModel;
        this.logStrategyFactory = logStrategyFactory;
        this.shortestPathAlgorithmRegistry = shortestPathAlgorithmRegistry;
        this.graphPathService = new GraphPathService();
        this.drawingFileCodec = new DrawingFileCodec();
        this.actionHistory = FXCollections.observableArrayList();
        this.logStrategy = logStrategyFactory.create("Console");
        this.activeTool = TOOL_SELECT;
        this.currentShapeColorRgb = DEFAULT_SHAPE_COLOR;
        this.lastDrawingDirectory = Paths.get(System.getProperty("user.home"));
    }

    @FXML
    public void initialize() {
        actionObservable.addObserver(this);

        historyListView.setItems(actionHistory);
        historyListView.setFocusTraversable(false);
        historyListView.setPlaceholder(new Label("Aucune action pour le moment."));

        drawingCanvas.setPickOnBounds(true);
        drawingCanvas.setOnMousePressed(this::onCanvasMousePressed);
        drawingCanvas.setOnMouseDragged(this::onCanvasMouseDragged);
        drawingCanvas.setOnMouseReleased(this::onCanvasMouseReleased);

        selectBtn.setOnAction(event -> activateSelectionMode());
        rectangleBtn.setOnAction(event -> activateShapeTool(ShapeFactory.RECTANGLE));
        circleBtn.setOnAction(event -> activateShapeTool(ShapeFactory.CIRCLE));
        lineBtn.setOnAction(event -> activateShapeTool(ShapeFactory.LINE));
        triangleBtn.setOnAction(event -> activateShapeTool(ShapeFactory.TRIANGLE));
        diamondBtn.setOnAction(event -> activateShapeTool(ShapeFactory.DIAMOND));
        pentagonBtn.setOnAction(event -> activateShapeTool(ShapeFactory.PENTAGON));
        hexagonBtn.setOnAction(event -> activateShapeTool(ShapeFactory.HEXAGON));
        starBtn.setOnAction(event -> activateShapeTool(ShapeFactory.STAR));
        graphNodeBtn.setOnAction(event -> activateGraphNodeTool());
        graphEdgeBtn.setOnAction(event -> activateGraphEdgeTool());

        undoBtn.setOnAction(event -> undo());
        redoBtn.setOnAction(event -> redo());
        deleteBtn.setOnAction(event -> deleteSelected());
        clearBtn.setOnAction(event -> clearCanvas());
        saveBtn.setOnAction(event -> saveDrawing());
        openBtn.setOnAction(event -> openDrawing());
        quickOpenBtn.setOnAction(event -> openDrawing());
        computePathBtn.setOnAction(event -> computeShortestPath());
        clearPathBtn.setOnAction(event -> clearPathVisualization());
        minimizeBtn.setOnAction(event -> minimizeWindow());
        maximizeBtn.setOnAction(event -> toggleMaximizeWindow());
        closeBtn.setOnAction(event -> closeWindow());

        logStrategyCombo.getItems().addAll("Console", "Fichier", "Base de donnees");
        logStrategyCombo.setValue("Console");
        logStrategyCombo.setOnAction(event -> changeLogStrategy());
        initializeColorControls();

        algorithmCombo.getItems().addAll(shortestPathAlgorithmRegistry.algorithmNames());
        algorithmCombo.setValue("Dijkstra");
        algorithmCombo.setOnAction(event -> handleGraphControlSelectionChanged());
        startNodeCombo.setOnAction(event -> handleGraphControlSelectionChanged());
        endNodeCombo.setOnAction(event -> handleGraphControlSelectionChanged());

        root.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null && !windowControlsConfigured) {
                Platform.runLater(this::configureWindowControls);
            }
        });

        openBtn.setDisable(false);
        activateSelectionMode();
        updateActionButtons();
        updateGraphControls();
        updateEmptyState();
        updateStatus("Choisissez un outil de dessin ou creez un graphe sur le canevas.");
    }

    private void activateSelectionMode() {
        setActiveTool(
                TOOL_SELECT,
                "Mode selection",
                "Mode selection actif. Cliquez un element pour le selectionner."
        );
    }

    private void activateShapeTool(String shapeType) {
        setActiveTool(
                shapeType,
                "Outil: " + getShapeDisplayName(shapeType),
                getShapeDisplayName(shapeType) + " actif. Cliquez ou glissez dans le canevas."
        );
    }

    private void activateGraphNodeTool() {
        setActiveTool(
                TOOL_GRAPH_NODE,
                "Mode graphe : noeud",
                "Cliquez dans le canevas pour ajouter un noeud au graphe."
        );
    }

    private void activateGraphEdgeTool() {
        setActiveTool(
                TOOL_GRAPH_EDGE,
                "Mode graphe : arete",
                "Cliquez sur un noeud de depart puis sur un noeud d'arrivee."
        );
    }

    private void setActiveTool(String tool, String badgeText, String statusText) {
        activeTool = tool;
        if (!TOOL_GRAPH_EDGE.equals(tool)) {
            pendingEdgeStartNode = null;
        }
        updateToolBadge(badgeText);
        updateToolButtonStyles();
        updateStatus(statusText);
    }

    private void onCanvasMousePressed(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        if (TOOL_GRAPH_NODE.equals(activeTool)) {
            addGraphNode(event.getX(), event.getY());
            return;
        }

        if (TOOL_GRAPH_EDGE.equals(activeTool)) {
            clearSelection();
            updateStatus("Cliquez sur deux noeuds pour creer une arete ponderee.");
            return;
        }

        if (TOOL_SELECT.equals(activeTool)) {
            clearSelection();
            updateStatus("Mode selection actif. Cliquez un element pour le selectionner.");
            return;
        }

        if (!isShapeDrawingToolActive()) {
            return;
        }

        startDrawing(event.getX(), event.getY());
    }

    private void onCanvasMouseDragged(MouseEvent event) {
        if (!drawingGestureActive) {
            return;
        }

        updateDrawing(event.getX(), event.getY());
    }

    private void onCanvasMouseReleased(MouseEvent event) {
        if (!drawingGestureActive) {
            return;
        }

        finishDrawing(event.getX(), event.getY());
    }

    private boolean isShapeDrawingToolActive() {
        return ShapeFactory.RECTANGLE.equals(activeTool)
                || ShapeFactory.CIRCLE.equals(activeTool)
                || ShapeFactory.LINE.equals(activeTool)
                || ShapeFactory.TRIANGLE.equals(activeTool)
                || ShapeFactory.DIAMOND.equals(activeTool)
                || ShapeFactory.PENTAGON.equals(activeTool)
                || ShapeFactory.HEXAGON.equals(activeTool)
                || ShapeFactory.STAR.equals(activeTool);
    }

    private void startDrawing(double canvasX, double canvasY) {
        startX = clampX(canvasX);
        startY = clampY(canvasY);
        drawingGestureActive = true;
        removePreview();
    }

    private void updateDrawing(double canvasX, double canvasY) {
        double currentX = clampX(canvasX);
        double currentY = clampY(canvasY);

        removePreview();

        ShapeModel previewModel = ShapeFactory.createShape(
                activeTool,
                startX,
                startY,
                currentX,
                currentY,
                currentShapeColorRgb
        );

        previewShape = previewModel.createShape();
        previewShape.setMouseTransparent(true);
        previewShape.setOpacity(0.45);
        previewShape.getStrokeDashArray().setAll(10.0, 6.0);
        drawingCanvas.getChildren().add(previewShape);
        updateEmptyState();
    }

    private void finishDrawing(double canvasX, double canvasY) {
        drawingGestureActive = false;

        double endX = clampX(canvasX);
        double endY = clampY(canvasY);
        double[] resolvedEndPoint = resolveEndPoint(endX, endY);

        removePreview();
        drawShape(startX, startY, resolvedEndPoint[0], resolvedEndPoint[1]);
    }

    private void drawShape(double shapeStartX, double shapeStartY, double shapeEndX, double shapeEndY) {
        ShapeModel shapeModel = ShapeFactory.createShape(
                activeTool,
                shapeStartX,
                shapeStartY,
                shapeEndX,
                shapeEndY,
                currentShapeColorRgb
        );

        DrawCommand command = new DrawCommand(drawingCanvas, shapeModel);
        undoManager.executeCommand(command);

        Shape drawnShape = command.getDrawnShape();
        prepareShape(drawnShape, shapeModel);
        clearPathVisualization(false);
        setSelectedShape(drawnShape);

        actionObservable.notifyActionExecuted(command.getDescription());
        updateStatus(shapeModel.getType() + " ajoute.");
        updateActionButtons();
        updateGraphControls();
        updateEmptyState();
    }

    private void addGraphNode(double canvasX, double canvasY) {
        double centerX = clampGraphCoordinateX(canvasX);
        double centerY = clampGraphCoordinateY(canvasY);

        if (isNodeTooClose(centerX, centerY)) {
            updateStatus("Espace insuffisant. Placez le noeud un peu plus loin.");
            return;
        }

        GraphNodeItem node = createGraphNode(centerX, centerY);
        AddGraphNodeCommand command = new AddGraphNodeCommand(drawingCanvas, graphModel, node);
        undoManager.executeCommand(command);

        clearPathVisualization(false);
        setSelectedShape(node.getCircle());
        actionObservable.notifyActionExecuted(command.getDescription());
        updateStatus("Noeud " + node.getLabel() + " ajoute au graphe.");
        updateActionButtons();
        updateGraphControls();
        updateEmptyState();
    }

    private GraphNodeItem createGraphNode(double centerX, double centerY) {
        int nodeId = ++graphNodeSequence;
        String label = buildNodeLabel(nodeId - 1);
        return createGraphNode(nodeId, label, centerX, centerY, currentShapeColorRgb);
    }

    private GraphNodeItem createGraphNode(int nodeId, String label, double centerX, double centerY, int accentColorRgb) {
        Color accentColor = toFxColor(accentColorRgb);
        Color fillColor = accentColor.deriveColor(0, 1.0, 1.0, 0.16);
        Color strokeColor = accentColor.deriveColor(0, 1.0, 0.74, 1.0);
        Color labelColor = accentColor.deriveColor(0, 1.0, 0.44, 1.0);

        Circle circle = new Circle(centerX, centerY, GRAPH_NODE_RADIUS);
        circle.setFill(fillColor);
        circle.setStroke(strokeColor);
        circle.setStrokeWidth(2.2);

        Text labelText = createCenteredText(label, centerX, centerY + 0.5, 13, toHexColor(labelColor));

        GraphNodeItem node = new GraphNodeItem(nodeId, label, circle, labelText, accentColorRgb);
        prepareGraphNode(node);
        return node;
    }

    private void prepareGraphNode(GraphNodeItem node) {
        Circle circle = node.getCircle();
        circle.getProperties().put(ELEMENT_KIND_KEY, ELEMENT_GRAPH_NODE);
        circle.getProperties().put(GRAPH_NODE_KEY, node);
        circle.getProperties().put(BASE_STROKE_KEY, circle.getStroke());
        circle.getProperties().put(BASE_STROKE_WIDTH_KEY, circle.getStrokeWidth());
        circle.getProperties().put(BASE_FILL_KEY, circle.getFill());
        circle.setMouseTransparent(false);
        Text labelText = node.getLabelText();
        labelText.getProperties().put(ELEMENT_KIND_KEY, ELEMENT_GRAPH_NODE_LABEL);
        labelText.getProperties().put(BASE_FILL_KEY, labelText.getFill());
        labelText.setMouseTransparent(true);
        attachGraphNodeInteractions(node);
        applyGraphNodeVisual(node, false);
    }

    private void attachGraphNodeInteractions(GraphNodeItem node) {
        Circle circle = node.getCircle();
        circle.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }

            if (TOOL_GRAPH_EDGE.equals(activeTool)) {
                handleGraphEdgeNodeSelection(node);
                event.consume();
                return;
            }

            if (TOOL_SELECT.equals(activeTool) || TOOL_GRAPH_NODE.equals(activeTool)) {
                setSelectedShape(circle);
                updateStatus("Noeud " + node.getLabel() + " selectionne.");
                event.consume();
            }
        });
    }

    private void handleGraphEdgeNodeSelection(GraphNodeItem node) {
        if (pendingEdgeStartNode == null) {
            pendingEdgeStartNode = node;
            setSelectedShape(node.getCircle());
            updateStatus("Depart choisi: " + node.getLabel() + ". Cliquez le noeud d'arrivee.");
            return;
        }

        if (pendingEdgeStartNode == node) {
            updateStatus("Choisissez un autre noeud pour terminer l'arete.");
            return;
        }

        if (graphModel.hasEdgeBetween(pendingEdgeStartNode, node)) {
            updateStatus("Une arete existe deja entre " + pendingEdgeStartNode.getLabel()
                    + " et " + node.getLabel() + ".");
            pendingEdgeStartNode = null;
            clearSelection();
            return;
        }

        Double weight = requestEdgeWeight(pendingEdgeStartNode, node);
        if (weight == null) {
            updateStatus("Creation de l'arete annulee.");
            pendingEdgeStartNode = null;
            clearSelection();
            return;
        }

        GraphEdgeItem edge = createGraphEdge(pendingEdgeStartNode, node, weight);
        AddGraphEdgeCommand command = new AddGraphEdgeCommand(drawingCanvas, graphModel, edge);
        undoManager.executeCommand(command);

        pendingEdgeStartNode = null;
        clearPathVisualization(false);
        setSelectedShape(edge.getLine());
        actionObservable.notifyActionExecuted(command.getDescription());
        updateStatus("Arete " + edge.getStartNode().getLabel() + " - " + edge.getEndNode().getLabel()
                + " ajoutee.");
        updateActionButtons();
        updateGraphControls();
        updateEmptyState();
    }

    private GraphEdgeItem createGraphEdge(GraphNodeItem first, GraphNodeItem second, double weight) {
        return createGraphEdge(first, second, weight, currentShapeColorRgb);
    }

    private GraphEdgeItem createGraphEdge(GraphNodeItem first, GraphNodeItem second, double weight, int accentColorRgb) {
        Color accentColor = toFxColor(accentColorRgb);
        Color strokeColor = accentColor.deriveColor(0, 1.0, 0.78, 1.0);
        Color weightColor = accentColor.deriveColor(0, 1.0, 0.46, 1.0);

        Line line = new Line(first.getCenterX(), first.getCenterY(), second.getCenterX(), second.getCenterY());
        line.setStroke(strokeColor);
        line.setStrokeWidth(3);

        double midX = (first.getCenterX() + second.getCenterX()) / 2.0;
        double midY = (first.getCenterY() + second.getCenterY()) / 2.0;
        Text weightText = createCenteredText(formatNumber(weight), midX, midY - 10, 12, toHexColor(weightColor));

        GraphEdgeItem edge = new GraphEdgeItem(first, second, weight, line, weightText, accentColorRgb);
        prepareGraphEdge(edge);
        return edge;
    }

    private void prepareGraphEdge(GraphEdgeItem edge) {
        Line line = edge.getLine();
        line.getProperties().put(ELEMENT_KIND_KEY, ELEMENT_GRAPH_EDGE);
        line.getProperties().put(GRAPH_EDGE_KEY, edge);
        line.getProperties().put(BASE_STROKE_KEY, line.getStroke());
        line.getProperties().put(BASE_STROKE_WIDTH_KEY, line.getStrokeWidth());
        line.getProperties().put(BASE_FILL_KEY, line.getFill());
        line.setMouseTransparent(false);
        Text weightText = edge.getWeightText();
        weightText.getProperties().put(ELEMENT_KIND_KEY, ELEMENT_GRAPH_EDGE_LABEL);
        weightText.getProperties().put(BASE_FILL_KEY, weightText.getFill());
        weightText.setMouseTransparent(true);
        attachGraphEdgeInteractions(edge);
        applyGraphEdgeVisual(edge, false);
    }

    private void attachGraphEdgeInteractions(GraphEdgeItem edge) {
        edge.getLine().setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }

            if (!TOOL_SELECT.equals(activeTool)) {
                return;
            }

            setSelectedShape(edge.getLine());
            updateStatus("Arete " + edge.getStartNode().getLabel() + " - "
                    + edge.getEndNode().getLabel() + " selectionnee.");
            event.consume();
        });
    }

    private Double requestEdgeWeight(GraphNodeItem first, GraphNodeItem second) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Poids de l'arete");
        dialog.setHeaderText("Definir le poids entre " + first.getLabel() + " et " + second.getLabel());
        dialog.setContentText("Poids positif :");

        Stage stage = getStage();
        if (stage != null) {
            dialog.initOwner(stage);
        }

        while (true) {
            var result = dialog.showAndWait();
            if (result.isEmpty()) {
                return null;
            }

            String value = result.get().trim();
            try {
                double weight = Double.parseDouble(value);
                if (weight <= 0) {
                    dialog.setHeaderText("Le poids doit etre strictement positif.");
                    continue;
                }
                return weight;
            } catch (NumberFormatException exception) {
                dialog.setHeaderText("Entrez une valeur numerique valide.");
            }
        }
    }

    private void undo() {
        Command command = undoManager.undo();
        if (command == null) {
            updateStatus("Aucune action a annuler.");
            return;
        }

        removePreview();
        pendingEdgeStartNode = null;
        clearSelection();
        clearPathVisualization(false);

        if (command instanceof DeleteCommand deleteCommand) {
            Shape restoredShape = deleteCommand.getShape();
            if (restoredShape != null && drawingCanvas.getChildren().contains(restoredShape)) {
                setSelectedShape(restoredShape);
            }
        }

        actionObservable.notifyActionUndone(command.getDescription());
        updateStatus("Action annulee: " + command.getDescription());
        updateActionButtons();
        updateGraphControls();
        updateEmptyState();
    }

    private void redo() {
        Command command = undoManager.redo();
        if (command == null) {
            updateStatus("Aucune action a retablir.");
            return;
        }

        pendingEdgeStartNode = null;
        clearSelection();
        clearPathVisualization(false);

        if (command instanceof DrawCommand drawCommand) {
            Shape redrawnShape = drawCommand.getDrawnShape();
            prepareShape(redrawnShape, drawCommand.getShapeModel());
            setSelectedShape(redrawnShape);
        } else if (command instanceof AddGraphNodeCommand addGraphNodeCommand) {
            Shape nodeCircle = findLastGraphShape(ELEMENT_GRAPH_NODE);
            if (nodeCircle != null) {
                GraphNodeItem node = (GraphNodeItem) nodeCircle.getProperties().get(GRAPH_NODE_KEY);
                prepareGraphNode(node);
                setSelectedShape(node.getCircle());
            }
        } else if (command instanceof AddGraphEdgeCommand addGraphEdgeCommand) {
            Shape edgeLine = findLastGraphShape(ELEMENT_GRAPH_EDGE);
            if (edgeLine != null) {
                GraphEdgeItem edge = (GraphEdgeItem) edgeLine.getProperties().get(GRAPH_EDGE_KEY);
                prepareGraphEdge(edge);
                setSelectedShape(edge.getLine());
            }
        }

        actionObservable.notifyActionExecuted("Redo de " + command.getDescription());
        updateStatus("Action retablie: " + command.getDescription());
        updateActionButtons();
        updateGraphControls();
        updateEmptyState();
    }

    private Shape findLastGraphShape(String kind) {
        for (int index = drawingCanvas.getChildren().size() - 1; index >= 0; index--) {
            if (drawingCanvas.getChildren().get(index) instanceof Shape shape) {
                if (kind.equals(shape.getProperties().get(ELEMENT_KIND_KEY))) {
                    return shape;
                }
            }
        }
        return null;
    }

    private void deleteSelected() {
        if (selectedShape == null || !drawingCanvas.getChildren().contains(selectedShape)) {
            updateStatus("Selectionnez un element a supprimer.");
            updateActionButtons();
            return;
        }

        Shape shapeToDelete = selectedShape;
        String kind = (String) shapeToDelete.getProperties().getOrDefault(ELEMENT_KIND_KEY, ELEMENT_DRAW_SHAPE);
        Command command;

        if (ELEMENT_GRAPH_NODE.equals(kind)) {
            GraphNodeItem node = (GraphNodeItem) shapeToDelete.getProperties().get(GRAPH_NODE_KEY);
            command = new DeleteGraphNodeCommand(drawingCanvas, graphModel, node);
        } else if (ELEMENT_GRAPH_EDGE.equals(kind)) {
            GraphEdgeItem edge = (GraphEdgeItem) shapeToDelete.getProperties().get(GRAPH_EDGE_KEY);
            command = new DeleteGraphEdgeCommand(drawingCanvas, graphModel, edge);
        } else {
            command = new DeleteCommand(drawingCanvas, shapeToDelete);
        }

        pendingEdgeStartNode = null;
        clearSelection();
        undoManager.executeCommand(command);
        clearPathVisualization(false);

        actionObservable.notifyActionExecuted(command.getDescription());
        updateStatus("Element supprime.");
        updateActionButtons();
        updateGraphControls();
        updateEmptyState();
    }

    private void clearCanvas() {
        removePreview();

        if (drawingCanvas.getChildren().isEmpty()) {
            updateStatus("Le canevas est deja vide.");
            updateActionButtons();
            return;
        }

        clearSelection();
        pendingEdgeStartNode = null;

        List<GraphNodeItem> graphNodesSnapshot = new ArrayList<>(graphModel.getNodes());
        List<GraphEdgeItem> graphEdgesSnapshot = new ArrayList<>(graphModel.getEdges());

        ClearCanvasCommand command = new ClearCanvasCommand(
                drawingCanvas,
                () -> {
                    graphModel.clear();
                    clearPathVisualization(false);
                    updateGraphControls();
                },
                () -> {
                    graphModel.clear();
                    graphNodesSnapshot.forEach(graphModel::addNode);
                    graphEdgesSnapshot.forEach(graphModel::addEdge);
                    clearPathVisualization(false);
                    updateGraphControls();
                }
        );

        undoManager.executeCommand(command);

        actionObservable.notifyActionExecuted(command.getDescription());
        updateStatus("Canevas efface.");
        updateActionButtons();
        updateGraphControls();
        updateEmptyState();
    }

    private void computeShortestPath() {
        clearPathVisualization(false);
        GraphPathComputation computation = graphPathService.computeShortestPath(
                graphModel,
                startNodeCombo.getValue(),
                endNodeCombo.getValue(),
                algorithmCombo.getValue(),
                shortestPathAlgorithmRegistry
        );
        if (!computation.success()) {
            pathResultLabel.setText(computation.message());
            updateStatus(computation.message());
            updateGraphControls();
            return;
        }

        for (GraphNodeItem node : computation.result().getPathNodes()) {
            node.setHighlighted(true);
            applyGraphNodeVisual(node, node.getCircle() == selectedShape);
        }
        for (GraphEdgeItem edge : computation.result().getPathEdges()) {
            edge.setHighlighted(true);
            applyGraphEdgeVisual(edge, edge.getLine() == selectedShape);
        }

        pathVisualizationActive = true;
        pathResultLabel.setText(computation.message());
        actionObservable.notifyActionExecuted(
                "Plus court chemin calcule avec " + computation.algorithm().getName()
                        + " entre " + computation.source().getLabel()
                        + " et " + computation.target().getLabel()
                        + " : " + computation.pathText()
                        + " (poids " + formatNumber(computation.result().getTotalWeight()) + ")"
        );
        updateStatus("Chemin calcule avec " + computation.algorithm().getName() + ".");
        updateGraphControls();
    }

    private void clearPathVisualization() {
        clearPathVisualization(true);
    }

    private void clearPathVisualization(boolean updateStatusMessage) {
        for (GraphNodeItem node : graphModel.getNodes()) {
            node.setHighlighted(false);
            applyGraphNodeVisual(node, node.getCircle() == selectedShape);
        }
        for (GraphEdgeItem edge : graphModel.getEdges()) {
            edge.setHighlighted(false);
            applyGraphEdgeVisual(edge, edge.getLine() == selectedShape);
        }

        boolean hadPath = pathVisualizationActive;
        pathVisualizationActive = false;
        pathResultLabel.setText(getDefaultPathMessage());
        if (updateStatusMessage) {
            updateStatus(hadPath
                    ? "Mise en evidence du chemin reinitialisee."
                    : "Aucun chemin n'etait affiche.");
        }
        updateGraphControls();
    }

    private void changeLogStrategy() {
        String selected = logStrategyCombo.getValue();
        logStrategy = logStrategyFactory.create(selected);

        actionObservable.notifyActionExecuted("Changement de strategie de log: " + selected);
        updateStatus("Strategie de log active: " + selected);
    }

    private void saveDrawing() {
        String drawingName = "Drawing_" + System.currentTimeMillis() + DRAWING_FILE_EXTENSION;
        Path targetFile = chooseDrawingSaveFile(drawingName);
        if (targetFile == null) {
            updateStatus("Sauvegarde annulee.");
            return;
        }

        try {
            Files.createDirectories(DRAWINGS_DIRECTORY);
            Files.writeString(targetFile, buildDrawingFileData(), StandardCharsets.UTF_8);
            String message = "Dessin sauvegarde dans le fichier.";
            actionObservable.notifyActionExecuted("Sauvegarde du dessin dans le fichier: " + targetFile.getFileName());
            updateStatus(message);
            showSaveAlert(Alert.AlertType.INFORMATION, "Sauvegarde reussie", message + "\nFichier: " + targetFile);
        } catch (IOException exception) {
            String message = "La sauvegarde du fichier a echoue.";
            updateStatus(message);
            showSaveAlert(Alert.AlertType.ERROR, "Erreur de sauvegarde", message + "\n" + exception.getMessage());
        }
    }

    private void openDrawing() {
        Path sourceFile = chooseDrawingOpenFile();
        if (sourceFile == null) {
            updateStatus("Ouverture annulee.");
            return;
        }

        try {
            loadDrawingFromFile(sourceFile);
            actionHistory.clear();
            actionObservable.notifyActionExecuted("Ouverture du dessin depuis le fichier: " + sourceFile.getFileName());
            updateStatus("Dessin charge depuis " + sourceFile.getFileName() + ".");
        } catch (IOException | IllegalArgumentException exception) {
            String message = "Impossible d'ouvrir ce fichier.";
            updateStatus(message);
            showSaveAlert(Alert.AlertType.ERROR, "Erreur d'ouverture", message + "\n" + exception.getMessage());
        }
    }

    private void loadDrawingFromFile(Path sourceFile) throws IOException {
        List<String> lines = Files.readAllLines(sourceFile, StandardCharsets.UTF_8);
        DrawingFileData data = drawingFileCodec.parse(lines);
        applyLoadedDrawing(
                data.shapeRecords(),
                data.nodeRecords(),
                data.edgeRecords(),
                data.nodeSequence()
        );
    }

    private void applyLoadedDrawing(
            List<StoredShapeRecord> shapeRecords,
            List<StoredNodeRecord> nodeRecords,
            List<StoredEdgeRecord> edgeRecords,
            int loadedNodeSequence
    ) {
        removePreview();
        drawingGestureActive = false;
        clearSelection();
        pendingEdgeStartNode = null;
        pathVisualizationActive = false;
        pathResultLabel.setText(getDefaultPathMessage());
        drawingCanvas.getChildren().clear();
        graphModel.clear();
        undoManager.clearHistory();

        Map<Integer, GraphNodeItem> nodesById = new HashMap<>();
        int maxNodeId = 0;

        for (StoredNodeRecord record : nodeRecords) {
            GraphNodeItem node = createGraphNode(
                    record.id(),
                    record.label(),
                    record.centerX(),
                    record.centerY(),
                    record.colorRgb()
            );
            nodesById.put(record.id(), node);
            graphModel.addNode(node);
            maxNodeId = Math.max(maxNodeId, record.id());
        }

        List<GraphEdgeItem> loadedEdges = new ArrayList<>();
        for (StoredEdgeRecord record : edgeRecords) {
            GraphNodeItem startNode = nodesById.get(record.startNodeId());
            GraphNodeItem endNode = nodesById.get(record.endNodeId());
            if (startNode == null || endNode == null) {
                throw new IllegalArgumentException("Une arete reference un noeud inexistant.");
            }

            GraphEdgeItem edge = createGraphEdge(
                    startNode,
                    endNode,
                    record.weight(),
                    record.colorRgb()
            );
            loadedEdges.add(edge);
            graphModel.addEdge(edge);
        }

        for (StoredShapeRecord record : shapeRecords) {
            ShapeModel shapeModel = ShapeFactory.createShape(
                    record.type(),
                    record.startX(),
                    record.startY(),
                    record.endX(),
                    record.endY(),
                    record.colorRgb()
            );
            Shape shape = shapeModel.createShape();
            prepareShape(shape, shapeModel);
            drawingCanvas.getChildren().add(shape);
        }

        for (GraphEdgeItem edge : loadedEdges) {
            drawingCanvas.getChildren().add(edge.getLine());
            drawingCanvas.getChildren().add(edge.getWeightText());
        }

        for (GraphNodeItem node : graphModel.getNodes()) {
            drawingCanvas.getChildren().add(node.getCircle());
            drawingCanvas.getChildren().add(node.getLabelText());
        }

        graphNodeSequence = Math.max(loadedNodeSequence, maxNodeId);
        activateSelectionMode();
        updateActionButtons();
        updateGraphControls();
        updateEmptyState();
    }

    private void prepareShape(Shape shape, ShapeModel shapeModel) {
        String shapeType = shapeModel.getType();
        shape.getProperties().put(ELEMENT_KIND_KEY, ELEMENT_DRAW_SHAPE);
        shape.getProperties().put(SHAPE_TYPE_KEY, shapeType);
        shape.getProperties().put(SHAPE_START_X_KEY, shapeModel.getStartX());
        shape.getProperties().put(SHAPE_START_Y_KEY, shapeModel.getStartY());
        shape.getProperties().put(SHAPE_END_X_KEY, shapeModel.getEndX());
        shape.getProperties().put(SHAPE_END_Y_KEY, shapeModel.getEndY());
        shape.getProperties().put(SHAPE_COLOR_RGB_KEY, shapeModel.getColorRGB());
        shape.getProperties().put(BASE_STROKE_KEY, shape.getStroke());
        shape.getProperties().put(BASE_STROKE_WIDTH_KEY, shape.getStrokeWidth());
        shape.getProperties().put(BASE_FILL_KEY, shape.getFill());
        shape.getStrokeDashArray().clear();
        shape.setOpacity(1.0);
        shape.setMouseTransparent(false);
        attachShapeInteractions(shape);
        applySelectionStyle(shape, false);
    }

    private void attachShapeInteractions(Shape shape) {
        shape.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY || !isShapeDrawingToolActive()) {
                return;
            }

            Point2D point = drawingCanvas.sceneToLocal(event.getSceneX(), event.getSceneY());
            startDrawing(point.getX(), point.getY());
            event.consume();
        });

        shape.setOnMouseDragged(event -> {
            if (!drawingGestureActive) {
                return;
            }

            Point2D point = drawingCanvas.sceneToLocal(event.getSceneX(), event.getSceneY());
            updateDrawing(point.getX(), point.getY());
            event.consume();
        });

        shape.setOnMouseReleased(event -> {
            if (!drawingGestureActive) {
                return;
            }

            Point2D point = drawingCanvas.sceneToLocal(event.getSceneX(), event.getSceneY());
            finishDrawing(point.getX(), point.getY());
            event.consume();
        });

        shape.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY || !TOOL_SELECT.equals(activeTool)) {
                return;
            }

            setSelectedShape(shape);
            updateStatus(getShapeName(shape) + " selectionne.");
            event.consume();
        });
    }

    private void setSelectedShape(Shape shape) {
        if (selectedShape == shape) {
            updateActionButtons();
            return;
        }

        if (selectedShape != null) {
            applySelectionStyle(selectedShape, false);
        }

        selectedShape = shape;

        if (selectedShape != null) {
            applySelectionStyle(selectedShape, true);
            bringSelectedElementToFront(selectedShape);
        }

        updateActionButtons();
    }

    private void clearSelection() {
        if (selectedShape != null) {
            applySelectionStyle(selectedShape, false);
            selectedShape = null;
        }

        updateActionButtons();
    }

    private void applySelectionStyle(Shape shape, boolean selected) {
        String kind = (String) shape.getProperties().getOrDefault(ELEMENT_KIND_KEY, ELEMENT_DRAW_SHAPE);
        if (ELEMENT_GRAPH_NODE.equals(kind)) {
            GraphNodeItem node = (GraphNodeItem) shape.getProperties().get(GRAPH_NODE_KEY);
            applyGraphNodeVisual(node, selected);
            return;
        }
        if (ELEMENT_GRAPH_EDGE.equals(kind)) {
            GraphEdgeItem edge = (GraphEdgeItem) shape.getProperties().get(GRAPH_EDGE_KEY);
            applyGraphEdgeVisual(edge, selected);
            return;
        }

        Paint baseStroke = (Paint) shape.getProperties().getOrDefault(BASE_STROKE_KEY, shape.getStroke());
        double baseStrokeWidth =
                ((Number) shape.getProperties().getOrDefault(BASE_STROKE_WIDTH_KEY, shape.getStrokeWidth()))
                        .doubleValue();

        shape.setStroke(selected ? SELECTION_STROKE : baseStroke);
        shape.setStrokeWidth(selected ? baseStrokeWidth + 1.6 : baseStrokeWidth);
        shape.setEffect(selected ? new DropShadow(18, Color.rgb(15, 23, 42, 0.24)) : null);
    }

    private void bringSelectedElementToFront(Shape shape) {
        String kind = (String) shape.getProperties().getOrDefault(ELEMENT_KIND_KEY, ELEMENT_DRAW_SHAPE);
        if (ELEMENT_GRAPH_NODE.equals(kind)) {
            GraphNodeItem node = (GraphNodeItem) shape.getProperties().get(GRAPH_NODE_KEY);
            bringGraphNodeToFront(node);
            return;
        }
        if (ELEMENT_GRAPH_EDGE.equals(kind)) {
            GraphEdgeItem edge = (GraphEdgeItem) shape.getProperties().get(GRAPH_EDGE_KEY);
            edge.getLine().toFront();
            bringGraphNodeToFront(edge.getStartNode());
            bringGraphNodeToFront(edge.getEndNode());
            edge.getWeightText().toFront();
            return;
        }

        shape.toFront();
    }

    private void bringGraphNodeToFront(GraphNodeItem node) {
        node.getCircle().toFront();
        node.getLabelText().toFront();
    }

    private void applyGraphNodeVisual(GraphNodeItem node, boolean selected) {
        Circle circle = node.getCircle();
        Text label = node.getLabelText();
        Paint baseFill = (Paint) circle.getProperties().getOrDefault(BASE_FILL_KEY, circle.getFill());
        Paint baseStroke = (Paint) circle.getProperties().getOrDefault(BASE_STROKE_KEY, circle.getStroke());
        Paint baseLabelFill = (Paint) label.getProperties().getOrDefault(BASE_FILL_KEY, label.getFill());
        double baseStrokeWidth =
                ((Number) circle.getProperties().getOrDefault(BASE_STROKE_WIDTH_KEY, circle.getStrokeWidth()))
                        .doubleValue();

        circle.setFill(node.isHighlighted() ? GRAPH_PATH_FILL : baseFill);
        circle.setStroke(selected ? SELECTION_STROKE : node.isHighlighted() ? GRAPH_PATH_STROKE : baseStroke);
        circle.setStrokeWidth(selected ? baseStrokeWidth + 1.8 : node.isHighlighted() ? baseStrokeWidth + 1.2 : baseStrokeWidth);
        circle.setEffect(selected
                ? new DropShadow(18, Color.rgb(15, 118, 110, 0.22))
                : node.isHighlighted()
                ? new DropShadow(18, Color.rgb(217, 119, 6, 0.20))
                : null);
        label.setFill(selected
                ? SELECTION_STROKE
                : node.isHighlighted()
                ? Color.web("#9a3412")
                : baseLabelFill);
    }

    private void applyGraphEdgeVisual(GraphEdgeItem edge, boolean selected) {
        Line line = edge.getLine();
        Text weightText = edge.getWeightText();
        Paint baseStroke = (Paint) line.getProperties().getOrDefault(BASE_STROKE_KEY, line.getStroke());
        Paint baseWeightFill = (Paint) weightText.getProperties().getOrDefault(BASE_FILL_KEY, weightText.getFill());
        double baseStrokeWidth =
                ((Number) line.getProperties().getOrDefault(BASE_STROKE_WIDTH_KEY, line.getStrokeWidth()))
                        .doubleValue();

        line.setStroke(selected ? SELECTION_STROKE : edge.isHighlighted() ? GRAPH_PATH_STROKE : baseStroke);
        line.setStrokeWidth(selected ? baseStrokeWidth + 1.6 : edge.isHighlighted() ? baseStrokeWidth + 1.2 : baseStrokeWidth);
        line.setEffect(selected
                ? new DropShadow(16, Color.rgb(15, 118, 110, 0.18))
                : edge.isHighlighted()
                ? new DropShadow(16, Color.rgb(217, 119, 6, 0.18))
                : null);
        weightText.setFill(selected
                ? SELECTION_STROKE
                : edge.isHighlighted()
                ? Color.web("#9a3412")
                : baseWeightFill);
        weightText.setUnderline(edge.isHighlighted());
    }

    private void updateActionButtons() {
        undoBtn.setDisable(!undoManager.canUndo());
        redoBtn.setDisable(!undoManager.canRedo());
        clearBtn.setDisable(drawingCanvas.getChildren().isEmpty());
        deleteBtn.setDisable(selectedShape == null || !drawingCanvas.getChildren().contains(selectedShape));
    }

    private void initializeColorControls() {
        configureColorSwatch(colorBlueBtn, "#2D7FF9");
        configureColorSwatch(colorTealBtn, "#0F766E");
        configureColorSwatch(colorGreenBtn, "#22C55E");
        configureColorSwatch(colorYellowBtn, "#FACC15");
        configureColorSwatch(colorOrangeBtn, "#F97316");
        configureColorSwatch(colorRedBtn, "#EF4444");
        configureColorSwatch(colorPurpleBtn, "#8B5CF6");
        configureColorSwatch(colorGrayBtn, "#64748B");

        customColorPicker.setOnAction(event -> {
            if (updatingColorControls) {
                return;
            }
            int rgb = toRgb(customColorPicker.getValue());
            setCurrentDrawingColor(
                    rgb,
                    null,
                    false,
                    "Couleur personnalisee active: " + formatColorHex(rgb)
            );
        });

        setCurrentDrawingColor(DEFAULT_SHAPE_COLOR, colorBlueBtn, true, null);
    }

    private void configureColorSwatch(Button button, String hexColor) {
        int rgb = Integer.parseInt(hexColor.substring(1), 16);
        button.setText("");
        button.setOnAction(event -> setCurrentDrawingColor(
                rgb,
                button,
                true,
                "Couleur active: " + formatColorHex(rgb)
        ));
        applySwatchStyle(button, rgb, false);
    }

    private void setCurrentDrawingColor(int rgb, Button selectedSwatchButton, boolean syncColorPicker, String statusMessage) {
        currentShapeColorRgb = rgb;
        activeColorSwatchButton = selectedSwatchButton;

        if (syncColorPicker) {
            updatingColorControls = true;
            try {
                customColorPicker.setValue(toFxColor(rgb));
            } finally {
                updatingColorControls = false;
            }
        }

        updateColorControls();

        if (statusMessage != null && !statusMessage.isBlank()) {
            updateStatus(statusMessage);
        }
    }

    private void updateColorControls() {
        List<Button> swatches = List.of(
                colorBlueBtn,
                colorTealBtn,
                colorGreenBtn,
                colorYellowBtn,
                colorOrangeBtn,
                colorRedBtn,
                colorPurpleBtn,
                colorGrayBtn
        );

        for (Button swatch : swatches) {
            String rgbHex = swatch.getProperties().getOrDefault("swatchRgb", formatColorHex(DEFAULT_SHAPE_COLOR)).toString();
            int rgb = Integer.parseInt(rgbHex, 16);
            applySwatchStyle(swatch, rgb, swatch == activeColorSwatchButton);
        }

        activeColorPreview.setStyle(
                "-fx-background-color: " + formatColorHex(currentShapeColorRgb) + ";"
                        + "-fx-background-radius: 999;"
                        + "-fx-border-color: rgba(26, 45, 69, 0.12);"
                        + "-fx-border-radius: 999;"
        );
        activeColorValueLabel.setText("Couleur active " + formatColorHex(currentShapeColorRgb));
    }

    private void applySwatchStyle(Button button, int rgb, boolean active) {
        button.getProperties().put("swatchRgb", String.format("%06X", rgb));
        button.getStyleClass().remove("selected-color-swatch");
        if (active) {
            button.getStyleClass().add("selected-color-swatch");
        }
        button.setStyle(
                "-fx-background-color: " + formatColorHex(rgb) + ";"
                        + "-fx-background-radius: 999;"
                        + "-fx-border-radius: 999;"
        );
    }

    private void handleGraphControlSelectionChanged() {
        if (updatingGraphControls) {
            return;
        }
        updateGraphControlStateOnly();
    }

    private void updateGraphControls() {
        updatingGraphControls = true;
        List<String> nodeLabels = graphModel.getNodes().stream()
                .map(GraphNodeItem::getLabel)
                .toList();

        try {
            String previousStart = startNodeCombo.getValue();
            String previousEnd = endNodeCombo.getValue();

            startNodeCombo.getItems().setAll(nodeLabels);
            endNodeCombo.getItems().setAll(nodeLabels);

            startNodeCombo.setPromptText(nodeLabels.isEmpty() ? "Aucun noeud" : "Choisir un noeud");
            endNodeCombo.setPromptText(nodeLabels.isEmpty() ? "Aucun noeud" : "Choisir un noeud");

            if (nodeLabels.contains(previousStart)) {
                startNodeCombo.getSelectionModel().select(previousStart);
            } else if (!nodeLabels.isEmpty()) {
                startNodeCombo.getSelectionModel().select(nodeLabels.get(0));
            } else {
                startNodeCombo.getSelectionModel().clearSelection();
            }

            if (nodeLabels.contains(previousEnd)) {
                endNodeCombo.getSelectionModel().select(previousEnd);
            } else if (nodeLabels.size() > 1) {
                endNodeCombo.getSelectionModel().select(nodeLabels.get(1));
            } else if (!nodeLabels.isEmpty()) {
                endNodeCombo.getSelectionModel().select(nodeLabels.get(0));
            } else {
                endNodeCombo.getSelectionModel().clearSelection();
            }
        } finally {
            updatingGraphControls = false;
        }

        updateGraphControlStateOnly();
    }

    private void updateGraphControlStateOnly() {
        startNodeCombo.setDisable(startNodeCombo.getItems().isEmpty());
        endNodeCombo.setDisable(endNodeCombo.getItems().isEmpty());

        boolean canCompute = startNodeCombo.getValue() != null
                && endNodeCombo.getValue() != null
                && algorithmCombo.getValue() != null
                && (!graphModel.getEdges().isEmpty() || startNodeCombo.getValue().equals(endNodeCombo.getValue()));

        computePathBtn.setDisable(!canCompute);
        clearPathBtn.setDisable(!pathVisualizationActive);
        graphSummaryLabel.setText(graphModel.getNodes().size() + " noeuds | " + graphModel.getEdges().size() + " aretes");
        if (!pathVisualizationActive && (pathResultLabel.getText() == null || pathResultLabel.getText().isBlank())) {
            pathResultLabel.setText(getDefaultPathMessage());
        }
    }

    private void updateToolBadge(String text) {
        activeToolBadge.setText(text);
    }

    private void updateToolButtonStyles() {
        setToolButtonState(selectBtn, TOOL_SELECT.equals(activeTool));
        setToolButtonState(rectangleBtn, ShapeFactory.RECTANGLE.equals(activeTool));
        setToolButtonState(circleBtn, ShapeFactory.CIRCLE.equals(activeTool));
        setToolButtonState(lineBtn, ShapeFactory.LINE.equals(activeTool));
        setToolButtonState(triangleBtn, ShapeFactory.TRIANGLE.equals(activeTool));
        setToolButtonState(diamondBtn, ShapeFactory.DIAMOND.equals(activeTool));
        setToolButtonState(pentagonBtn, ShapeFactory.PENTAGON.equals(activeTool));
        setToolButtonState(hexagonBtn, ShapeFactory.HEXAGON.equals(activeTool));
        setToolButtonState(starBtn, ShapeFactory.STAR.equals(activeTool));
        setToolButtonState(graphNodeBtn, TOOL_GRAPH_NODE.equals(activeTool));
        setToolButtonState(graphEdgeBtn, TOOL_GRAPH_EDGE.equals(activeTool));
    }

    private void setToolButtonState(Button button, boolean active) {
        button.getStyleClass().remove("selected-tool");
        if (active) {
            button.getStyleClass().add("selected-tool");
        }
    }

    private void updateEmptyState() {
        boolean empty = getDrawableShapeCount() == 0;
        emptyStateBox.setVisible(empty);
        emptyStateBox.setManaged(empty);
    }

    private long getDrawableShapeCount() {
        return drawingCanvas.getChildren().stream()
                .filter(node -> node != previewShape)
                .count();
    }

    private void removePreview() {
        if (previewShape != null) {
            drawingCanvas.getChildren().remove(previewShape);
            previewShape = null;
        }
        updateEmptyState();
    }

    private double[] resolveEndPoint(double endX, double endY) {
        double deltaX = endX - startX;
        double deltaY = endY - startY;

        if (Math.hypot(deltaX, deltaY) >= MIN_DRAG_DISTANCE) {
            return new double[] {endX, endY};
        }

        return switch (activeTool) {
            case ShapeFactory.RECTANGLE -> new double[] {startX + DEFAULT_RECT_WIDTH, startY + DEFAULT_RECT_HEIGHT};
            case ShapeFactory.CIRCLE -> new double[] {startX + DEFAULT_CIRCLE_SIZE, startY + DEFAULT_CIRCLE_SIZE};
            case ShapeFactory.LINE -> new double[] {startX + DEFAULT_LINE_LENGTH, startY};
            case ShapeFactory.TRIANGLE,
                 ShapeFactory.DIAMOND,
                 ShapeFactory.PENTAGON,
                 ShapeFactory.HEXAGON,
                 ShapeFactory.STAR -> new double[] {startX + DEFAULT_RECT_WIDTH, startY + DEFAULT_RECT_HEIGHT};
            default -> new double[] {endX, endY};
        };
    }

    private double clampX(double value) {
        if (drawingCanvas.getWidth() <= 0) {
            return Math.max(0, value);
        }
        return Math.max(0, Math.min(value, drawingCanvas.getWidth()));
    }

    private double clampY(double value) {
        if (drawingCanvas.getHeight() <= 0) {
            return Math.max(0, value);
        }
        return Math.max(0, Math.min(value, drawingCanvas.getHeight()));
    }

    private double clampGraphCoordinateX(double value) {
        return Math.max(GRAPH_NODE_RADIUS + 12, Math.min(value, Math.max(GRAPH_NODE_RADIUS + 12, drawingCanvas.getWidth() - GRAPH_NODE_RADIUS - 12)));
    }

    private double clampGraphCoordinateY(double value) {
        return Math.max(GRAPH_NODE_RADIUS + 12, Math.min(value, Math.max(GRAPH_NODE_RADIUS + 12, drawingCanvas.getHeight() - GRAPH_NODE_RADIUS - 12)));
    }

    private boolean isNodeTooClose(double centerX, double centerY) {
        double minDistance = GRAPH_NODE_RADIUS * 2 + GRAPH_NODE_GAP;
        for (GraphNodeItem node : graphModel.getNodes()) {
            double distance = Math.hypot(centerX - node.getCenterX(), centerY - node.getCenterY());
            if (distance < minDistance) {
                return true;
            }
        }
        return false;
    }

    private String getShapeDisplayName(String shapeType) {
        return switch (shapeType) {
            case ShapeFactory.RECTANGLE -> "Rectangle";
            case ShapeFactory.CIRCLE -> "Cercle";
            case ShapeFactory.LINE -> "Ligne";
            case ShapeFactory.TRIANGLE -> "Triangle";
            case ShapeFactory.DIAMOND -> "Losange";
            case ShapeFactory.PENTAGON -> "Pentagone";
            case ShapeFactory.HEXAGON -> "Hexagone";
            case ShapeFactory.STAR -> "Etoile";
            default -> "Forme";
        };
    }

    private String getShapeName(Shape shape) {
        String kind = (String) shape.getProperties().getOrDefault(ELEMENT_KIND_KEY, ELEMENT_DRAW_SHAPE);
        if (ELEMENT_GRAPH_NODE.equals(kind)) {
            GraphNodeItem node = (GraphNodeItem) shape.getProperties().get(GRAPH_NODE_KEY);
            return "Noeud " + node.getLabel();
        }
        if (ELEMENT_GRAPH_EDGE.equals(kind)) {
            GraphEdgeItem edge = (GraphEdgeItem) shape.getProperties().get(GRAPH_EDGE_KEY);
            return "Arete " + edge.getStartNode().getLabel() + " - " + edge.getEndNode().getLabel();
        }
        Object shapeType = shape.getProperties().get(SHAPE_TYPE_KEY);
        return shapeType == null ? "Forme" : shapeType.toString();
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private String buildDrawingFileData() {
        List<StoredShapeRecord> shapeRecords = new ArrayList<>();
        List<StoredNodeRecord> nodeRecords = new ArrayList<>();
        List<StoredEdgeRecord> edgeRecords = new ArrayList<>();
        Set<GraphNodeItem> serializedNodes = new HashSet<>();
        Set<GraphEdgeItem> serializedEdges = new HashSet<>();

        for (Node node : drawingCanvas.getChildren()) {
            if (node == previewShape || !(node instanceof Shape shape)) {
                continue;
            }

            String kind = (String) shape.getProperties().get(ELEMENT_KIND_KEY);
            if (kind == null) {
                if (shape.getProperties().containsKey(SHAPE_TYPE_KEY)) {
                    kind = ELEMENT_DRAW_SHAPE;
                } else {
                    continue;
                }
            }

            if (ELEMENT_DRAW_SHAPE.equals(kind)) {
                Object storedShapeType = shape.getProperties().get(SHAPE_TYPE_KEY);
                if (storedShapeType == null) {
                    continue;
                }
                shapeRecords.add(new StoredShapeRecord(
                        resolveFactoryShapeType(storedShapeType.toString()),
                        ((Number) shape.getProperties().get(SHAPE_START_X_KEY)).doubleValue(),
                        ((Number) shape.getProperties().get(SHAPE_START_Y_KEY)).doubleValue(),
                        ((Number) shape.getProperties().get(SHAPE_END_X_KEY)).doubleValue(),
                        ((Number) shape.getProperties().get(SHAPE_END_Y_KEY)).doubleValue(),
                        (Integer) shape.getProperties().get(SHAPE_COLOR_RGB_KEY)
                ));
                continue;
            }

            if (ELEMENT_GRAPH_NODE.equals(kind)) {
                GraphNodeItem graphNode = (GraphNodeItem) shape.getProperties().get(GRAPH_NODE_KEY);
                if (serializedNodes.add(graphNode)) {
                    nodeRecords.add(new StoredNodeRecord(
                            graphNode.getId(),
                            graphNode.getLabel(),
                            graphNode.getCenterX(),
                            graphNode.getCenterY(),
                            graphNode.getAccentColorRgb()
                    ));
                }
                continue;
            }

            if (ELEMENT_GRAPH_EDGE.equals(kind)) {
                GraphEdgeItem graphEdge = (GraphEdgeItem) shape.getProperties().get(GRAPH_EDGE_KEY);
                if (serializedEdges.add(graphEdge)) {
                    edgeRecords.add(new StoredEdgeRecord(
                            graphEdge.getStartNode().getId(),
                            graphEdge.getEndNode().getId(),
                            graphEdge.getWeight(),
                            graphEdge.getAccentColorRgb()
                    ));
                }
            }
        }

        return drawingFileCodec.serialize(
                new DrawingFileData(graphNodeSequence, shapeRecords, nodeRecords, edgeRecords)
        );
    }

    private Path chooseDrawingSaveFile(String drawingName) {
        try {
            Files.createDirectories(DRAWINGS_DIRECTORY);
            FileChooser fileChooser = createDrawingFileChooser("Enregistrer le dessin", drawingName);
            Stage stage = getStage();
            java.io.File selectedFile = fileChooser.showSaveDialog(stage);
            if (selectedFile == null) {
                return null;
            }

            if (!selectedFile.getName().toLowerCase().endsWith(DRAWING_FILE_EXTENSION)) {
                selectedFile = new java.io.File(selectedFile.getParentFile(), selectedFile.getName() + DRAWING_FILE_EXTENSION);
            }

            Path selectedPath = selectedFile.toPath();
            rememberDrawingDirectory(selectedPath);
            return selectedPath;
        } catch (IOException exception) {
            System.err.println("Erreur selection fichier sauvegarde: " + exception.getMessage());
            return null;
        }
    }

    private Path chooseDrawingOpenFile() {
        try {
            Files.createDirectories(DRAWINGS_DIRECTORY);
        } catch (IOException exception) {
            System.err.println("Erreur creation dossier dessins: " + exception.getMessage());
        }

        FileChooser fileChooser = createDrawingFileChooser("Ouvrir le dessin", null);
        Stage stage = getStage();
        java.io.File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile == null) {
            return null;
        }

        Path selectedPath = selectedFile.toPath();
        rememberDrawingDirectory(selectedPath);
        return selectedPath;
    }

    private FileChooser createDrawingFileChooser(String title, String initialFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (initialFileName != null && !initialFileName.isBlank()) {
            fileChooser.setInitialFileName(initialFileName);
        }
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Dessin Drawing Studio", "*" + DRAWING_FILE_EXTENSION)
        );
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        Path initialDirectory = resolveInitialDrawingDirectory();
        if (initialDirectory != null) {
            fileChooser.setInitialDirectory(initialDirectory.toFile());
        }
        return fileChooser;
    }

    private Path resolveInitialDrawingDirectory() {
        if (lastDrawingDirectory != null && Files.isDirectory(lastDrawingDirectory)) {
            return lastDrawingDirectory;
        }
        if (Files.isDirectory(DRAWINGS_DIRECTORY)) {
            return DRAWINGS_DIRECTORY;
        }

        Path desktopPath = Paths.get(System.getProperty("user.home"), "Desktop");
        if (Files.isDirectory(desktopPath)) {
            return desktopPath;
        }

        Path homePath = Paths.get(System.getProperty("user.home"));
        return Files.isDirectory(homePath) ? homePath : null;
    }

    private void rememberDrawingDirectory(Path filePath) {
        Path parentDirectory = filePath == null ? null : filePath.getParent();
        if (parentDirectory != null && Files.isDirectory(parentDirectory)) {
            lastDrawingDirectory = parentDirectory;
        }
    }

    private String resolveFactoryShapeType(String savedShapeType) {
        return switch (savedShapeType) {
            case "Rectangle", ShapeFactory.RECTANGLE -> ShapeFactory.RECTANGLE;
            case "Cercle", ShapeFactory.CIRCLE -> ShapeFactory.CIRCLE;
            case "Ligne", ShapeFactory.LINE -> ShapeFactory.LINE;
            case "Triangle", ShapeFactory.TRIANGLE -> ShapeFactory.TRIANGLE;
            case "Losange", ShapeFactory.DIAMOND -> ShapeFactory.DIAMOND;
            case "Pentagone", ShapeFactory.PENTAGON -> ShapeFactory.PENTAGON;
            case "Hexagone", ShapeFactory.HEXAGON -> ShapeFactory.HEXAGON;
            case "Etoile", ShapeFactory.STAR -> ShapeFactory.STAR;
            default -> throw new IllegalArgumentException("Type de forme non supporte: " + savedShapeType);
        };
    }

    private void showSaveAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Stage stage = getStage();
        if (stage != null) {
            alert.initOwner(stage);
        }

        alert.showAndWait();
    }

    private void configureWindowControls() {
        Stage stage = getStage();
        if (stage == null) {
            return;
        }

        headerBar.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }

            windowDragOffsetX = event.getScreenX() - stage.getX();
            windowDragOffsetY = event.getScreenY() - stage.getY();
        });

        headerBar.setOnMouseDragged(event -> {
            if (!event.isPrimaryButtonDown() || stage.isMaximized()) {
                return;
            }

            stage.setX(event.getScreenX() - windowDragOffsetX);
            stage.setY(event.getScreenY() - windowDragOffsetY);
        });

        headerBar.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                toggleMaximizeWindow();
            }
        });

        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> updateMaximizeButton());
        updateMaximizeButton();
        windowControlsConfigured = true;
    }

    private void minimizeWindow() {
        Stage stage = getStage();
        if (stage != null) {
            stage.setIconified(true);
        }
    }

    private void toggleMaximizeWindow() {
        Stage stage = getStage();
        if (stage != null) {
            stage.setMaximized(!stage.isMaximized());
            updateMaximizeButton();
        }
    }

    private void closeWindow() {
        Stage stage = getStage();
        if (stage != null) {
            stage.close();
        }
    }

    private void updateMaximizeButton() {
        Stage stage = getStage();
        if (stage == null) {
            return;
        }

        maximizeBtn.setText(stage.isMaximized() ? "[]" : "[ ]");
    }

    private Stage getStage() {
        if (root.getScene() == null || root.getScene().getWindow() == null) {
            return null;
        }

        return (Stage) root.getScene().getWindow();
    }

    private void addHistoryEntry(String prefix, String actionDescription) {
        String timestamp = LocalTime.now().format(HISTORY_TIME_FORMAT);
        actionHistory.add(0, "[" + timestamp + "] " + prefix + actionDescription);
        historyListView.getSelectionModel().select(0);
        historyListView.scrollTo(0);
    }

    private Text createCenteredText(String content, double centerX, double centerY, int fontSize, String color) {
        Text text = new Text(content);
        text.setFont(Font.font("Segoe UI Semibold", fontSize));
        text.setFill(Color.web(color));
        text.applyCss();
        double width = text.getLayoutBounds().getWidth();
        text.setX(centerX - (width / 2.0));
        text.setY(centerY + 4);
        return text;
    }

    private String buildNodeLabel(int index) {
        StringBuilder builder = new StringBuilder();
        int value = index;
        do {
            builder.insert(0, (char) ('A' + (value % 26)));
            value = (value / 26) - 1;
        } while (value >= 0);
        return builder.toString();
    }

    private Color toFxColor(int rgb) {
        return Color.rgb((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

    private int toRgb(Color color) {
        int red = (int) Math.round(color.getRed() * 255);
        int green = (int) Math.round(color.getGreen() * 255);
        int blue = (int) Math.round(color.getBlue() * 255);
        return (red << 16) | (green << 8) | blue;
    }

    private String toHexColor(Color color) {
        return String.format("#%02X%02X%02X",
                (int) Math.round(color.getRed() * 255),
                (int) Math.round(color.getGreen() * 255),
                (int) Math.round(color.getBlue() * 255)
        );
    }

    private String formatColorHex(int value) {
        return String.format("#%06X", value);
    }

    private String formatNumber(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private String getDefaultPathMessage() {
        return "Selectionnez deux noeuds puis choisissez un algorithme pour calculer le plus court chemin.";
    }

    @Override
    public void onActionExecuted(String actionDescription) {
        addHistoryEntry("ACTION: ", actionDescription);
        logStrategy.log("ACTION: " + actionDescription);
    }

    @Override
    public void onActionUndone(String actionDescription) {
        addHistoryEntry("UNDO: ", actionDescription);
        logStrategy.log("UNDO: " + actionDescription);
    }

    @Override
    public String getObserverName() {
        return "DrawingController Logger";
    }
}
