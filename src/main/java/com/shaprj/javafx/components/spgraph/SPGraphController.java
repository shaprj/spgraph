package com.shaprj.javafx.components.spgraph;
/*
 * Created by O.Shalaevsky on 04.03.2020
 */

import com.shaprj.javafx.components.BaseController;
import com.shaprj.javafx.components.BaseResizeableController;
import com.shaprj.javafx.util.FilesHelper;
import com.shaprj.javafx.util.ImageHelper;
import com.shaprj.javafx.util.LocalizationMessages;
import javafx.beans.InvalidationListener;
import javafx.css.StyleableProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SPGraphController implements BaseResizeableController, BaseController {

    @FXML
    HBox toolbar;

    @FXML
    AnchorPane anchorPane;

    @FXML
    Group group;

    @FXML
    Pane border;

    @FXML
    Button addS2Button;

    @FXML
    Button addEdgeButton;

    @FXML
    Button removeButton;

    @FXML
    Button repaintButton;

    @FXML
    Button addMapButton;

    @FXML
    Button showGridButton;

    @FXML
    Button hideGridButton;

    @FXML
    ColorPicker colorPicker;

    @FXML
    private TextField xGrid;

    @FXML
    private TextField yGrid;

    @FXML
    public void initialize() {

        initButtons();

    }

    @Override
    public void postInit() {

        InvalidationListener listener = o -> refresh();
        border.widthProperty().addListener(listener);
        border.heightProperty().addListener(listener);

        xGrid.setText("10");
        yGrid.setText("10");

        refresh();
    }

    private Supplier<Pair<Double, Double>> topLeftSupplier;

    private double topLeftX = 0f, topLeftY = 0f;

    @Override
    public void setTopLeftSupplier(Supplier<Pair<Double, Double>> topLeftSupplier) {
        this.topLeftSupplier = topLeftSupplier;
    }

    private boolean isRepaintableLabel(Node node) {
        return node instanceof Label;
    }

    private boolean isRepaintableLine(Node node) {
        return node instanceof Line &&
                edgeList.stream()
                        .map(i -> i.getLine())
                        .filter(i -> i == node)
                        .count() > 0;

    }

    private void initButtons() {

        ((StyleableProperty) addS2Button.graphicProperty()).applyStyle(null,
                ImageHelper.createImageViewFromResource("/icon/add-s2.png", getClass()));
        addS2Button.setTooltip(new Tooltip(LocalizationMessages.getString("button.add.s2.title")));
        ((StyleableProperty) addEdgeButton.graphicProperty()).applyStyle(null,
                ImageHelper.createImageViewFromResource("/icon/add-edge.png", getClass()));
        addEdgeButton.setTooltip(new Tooltip(LocalizationMessages.getString("button.add.edge.title")));
        ((StyleableProperty) removeButton.graphicProperty()).applyStyle(null,
                ImageHelper.createImageViewFromResource("/icon/remove.png", getClass()));
        removeButton.setTooltip(new Tooltip(LocalizationMessages.getString("button.remove.title")));
        ((StyleableProperty) repaintButton.graphicProperty()).applyStyle(null,
                ImageHelper.createImageViewFromResource("/icon/repaint.png", getClass()));
        repaintButton.setTooltip(new Tooltip(LocalizationMessages.getString("button.repaint.title")));
        ((StyleableProperty) addMapButton.graphicProperty()).applyStyle(null,
                ImageHelper.createImageViewFromResource("/icon/add-map.png", getClass()));
        addMapButton.setTooltip(new Tooltip(LocalizationMessages.getString("button.add.map.title")));
        ((StyleableProperty) showGridButton.graphicProperty()).applyStyle(null,
                ImageHelper.createImageViewFromResource("/icon/add-grid.png", getClass()));
        showGridButton.setTooltip(new Tooltip(LocalizationMessages.getString("button.show.grid.title")));
        ((StyleableProperty) hideGridButton.graphicProperty()).applyStyle(null,
                ImageHelper.createImageViewFromResource("/icon/remove-grid.png", getClass()));
        hideGridButton.setTooltip(new Tooltip(LocalizationMessages.getString("button.hide.grid.title")));
    }

    private void refresh() {

        Pair<Double, Double> leftTop = new Pair<>(topLeftSupplier.get().getKey(), topLeftSupplier.get().getValue() - toolbar.getHeight());

        double oldGraphStepX = graphStepX;
        double oldGraphStepY = graphStepY;

        topLeftX = leftTop.getKey() + 10;
        topLeftY = leftTop.getValue() + 80;

        List<Node> nodesToRepaint = group.getChildren().stream()
                .filter(n -> isRepaintableLabel(n) ||
                        isRepaintableLine(n))
                .collect(Collectors.toList());

        group.getChildren().clear();

        if (gridInited)
            initGrid(this.xGridCoord, this.yGridCoord);

        nodesToRepaint.stream().forEach(shape -> {
            shape.setLayoutX((shape.getLayoutX() * (graphStepX + ZERO_X)) / (oldGraphStepX + ZERO_X));
            shape.setLayoutY((shape.getLayoutY() * (graphStepY + ZERO_Y)) / (oldGraphStepY + ZERO_Y));
            group.getChildren().add(shape);
        });
        nodeList.stream().forEach(n -> {
            n.setX((n.getX() * (graphStepX + ZERO_X)) / (oldGraphStepX + ZERO_X));
            n.setY((n.getY() * (graphStepY + ZERO_Y)) / (oldGraphStepY + ZERO_Y));
            n.updateShape();
            if (n instanceof SPGraphS2Node) {
                ((SPGraphS2Node) n).getOnReplaceEdgesConsumer().accept(n.getShape());
            }
            group.getChildren().add(n.getShape());
        });
    }

    private boolean isInited = false;
    private boolean gridInited = false;

    private static final int ZERO_X = 60;
    private static final int ZERO_Y = 60;

    private void checkIfInited() {

        if (!isInited) {

            border.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    addShape(mouseEvent.getSceneX() - topLeftX, mouseEvent.getSceneY() - topLeftY - toolbar.getHeight());
                }
            });
        }
    }

    private int xGridCoord;
    private int yGridCoord;

    @FXML
    private void onHideGridButton(ActionEvent actionEvent) {
        removeGrid();
    }

    public void removeGrid() {

        if (group == null) {
            return;
        }

        List toRemoveFromGroup = group.getChildren().stream()
                .filter(n -> n != null && n.getId() != null && (n.getId().equalsIgnoreCase("vertical-grid-line") || n.getId().equalsIgnoreCase("horizontal-grid-line")))
                .collect(Collectors.toList());

        group.getChildren().removeAll(toRemoveFromGroup);

        gridCrossPointXList.clear();
        gridCrossPointYList.clear();
    }

    private void initGrid(int x, int y) {

        if (x < 0 && y < 0) {
            throw new RuntimeException("Can't multiply by zero");
        }

        double xStep = (border.getWidth() - ZERO_X * 2) / x;
        double yStep = (border.getHeight() - ZERO_Y * 2) / y;

        for (int i = 0; i < x; i++) {
            Line gridLine = new Line((xStep * i) + ZERO_X, ZERO_Y, (xStep * i) + ZERO_X, border.getHeight() - ZERO_Y);
            gridLine.setId("vertical-grid-line");
            gridCrossPointXList.add((xStep * i) + ZERO_X);
            group.getChildren().addAll(gridLine);
        }

        for (int i = 0; i < y; i++) {
            Line gridLine = new Line(ZERO_X, (yStep * i) + ZERO_Y, border.getWidth() - ZERO_X, (yStep * i) + ZERO_Y);
            gridCrossPointYList.add((yStep * i) + ZERO_Y);
            gridLine.setId("horizontal-grid-line");
            group.getChildren().addAll(gridLine);
        }
    }

    private List<Double> gridCrossPointXList = new ArrayList<>();
    private List<Double> gridCrossPointYList = new ArrayList<>();

    private double graphStepX = 0, graphStepY = 0;

    @FXML
    private void onShowGridButton(ActionEvent actionEvent) {
        gridInited = true;
        this.xGridCoord = Integer.valueOf(xGrid.getText());
        this.yGridCoord = Integer.valueOf(yGrid.getText());
        initGrid(xGridCoord, yGridCoord);
    }

    @FXML
    private void onRemoveButton(ActionEvent actionEvent) {
        checkIfInited();
        actionShapeEnum = ActionShapeEnum.REMOVE;
    }

    @FXML
    private void onRepaintButton(ActionEvent actionEvent) {
        checkIfInited();
        currentColor = colorPicker.getValue();
        actionShapeEnum = ActionShapeEnum.RECOLOR;
    }

    private Color currentColor;

    @FXML
    public void onAddMapButton(ActionEvent addMapButton) {
        FilesHelper.openFile(border.getScene().getWindow(), f -> {
            if (f.getAbsolutePath().endsWith(".png") || f.getAbsolutePath().endsWith(".jpg")) {
                try {
                    Image image = new Image(new FileInputStream(f));
                    BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT);
                    border.setBackground(new Background(backgroundImage));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void onAddS2Button(ActionEvent actionEvent) {
        checkIfInited();
        actionShapeEnum = ActionShapeEnum.ADD_NODE;
    }

    private List<SPGraphNode> regLineNodes = new ArrayList<>();

    @FXML
    private void onAddEdgeButton(ActionEvent actionEvent) {
        regLineNodes.clear();
        checkIfInited();
        actionShapeEnum = ActionShapeEnum.ADD_EDJE;
    }

    private enum ActionShapeEnum {
        NONE,
        ADD_NODE,
        ADD_EDJE,
        REMOVE,
        RECOLOR
    }

    private ActionShapeEnum actionShapeEnum = ActionShapeEnum.NONE;


    private List<SPGraphNode> nodeList = new ArrayList<>();
    private List<SPGraphEdge> edgeList = new ArrayList<>();

    private void removeSelectedIfEnabled(Shape shape) {
        if (actionShapeEnum == ActionShapeEnum.REMOVE) {
            removeSPNode(shape);
            group.getChildren().remove(shape);
        }
    }

    private void recolorSelectedIfEnabled(Shape shape) {
        if (actionShapeEnum == ActionShapeEnum.RECOLOR) {
            shape.setFill(currentColor);
            shape.setStroke(currentColor);
        }
    }

    private void addEdgeIfEnabled(Shape c) {

        if (actionShapeEnum != ActionShapeEnum.ADD_EDJE) {
            return;
        }

        SPGraphNode recognized = recognizeSPGraphNode(c);

        Shape shape = getEdge(recognized);

        regLineNodes.add(recognized);

        if (shape != null) {
            group.getChildren().add(shape);
            shape.toFront();
        }
    }

    private void addShape(double x, double y) {

        SPGraphNode SPGraphNode;

        Point2D nearestCrossPoint = getClosestGridCrossPoint(x, y);

        switch (actionShapeEnum) {
            case ADD_NODE:
                SPGraphNode = new SPGraphS2Node(nearestCrossPoint.getX(), nearestCrossPoint.getY(), border.getScene(), this::onSPNodeMousePressed, this::replaceEdgesByNodeDrag);
                nodeList.add(SPGraphNode);
                group.getChildren().addAll(SPGraphNode.getShape(), SPGraphNode.getName());
                break;
            default:
                return;
        }
    }

    private Point2D getClosestGridCrossPoint(double x, double y) {
        Pair<Double, Double> deltaX = gridCrossPointXList.stream()
                .map(n -> new Pair<>(Math.abs(n - x), n - x))
                .min(Comparator.comparing(Pair::getKey)).orElse(new Pair<>(0.0, 1.0));
        Pair<Double, Double> deltaY = gridCrossPointYList.stream()
                .map(n -> new Pair<>(Math.abs(n - y), n - y))
                .min(Comparator.comparing(Pair::getKey)).orElse(new Pair<>(0.0, 1.0));
        return new Point2D(
                deltaX.getValue() > 0 ? x + deltaX.getKey() : x - deltaX.getKey(),
                deltaY.getValue() > 0 ? y + deltaY.getKey() : y - deltaY.getKey());
    }

    private void removeSPNode(Shape shape) {

        SPGraphNode spGraphNode = recognizeSPGraphNode(shape);

        if (spGraphNode instanceof SPGraphS2Node) {

            group.getChildren().remove(spGraphNode.getName());

            spGraphNode.getInEdgeList().stream().forEach(edge -> {
                Line line = (Line) edge.getLine();
                group.getChildren().remove(line);
                edge.getSource().getOutEdgeList().remove(edge);
                edgeList.remove(edge);
            });
            spGraphNode.getOutEdgeList().stream().forEach(edge -> {
                Line line = (Line) edge.getLine();
                group.getChildren().remove(line);
                edge.getDest().getInEdgeList().remove(edge);
                edgeList.remove(edge);
            });
        }

        nodeList.remove(spGraphNode);
    }

    private void replaceEdgesByNodeDrag(Shape shape) {

        SPGraphNode SPGraphNode = recognizeSPGraphNode(shape);

        Circle circle = (Circle) shape;
        double x = circle.getCenterX();
        double y = circle.getCenterY();

        SPGraphNode.getInEdgeList().stream().forEach(edge -> {
            Line line = (Line) edge.getLine();
            line.setEndX(x);
            line.setEndY(y);
        });
        SPGraphNode.getOutEdgeList().stream().forEach(edge -> {
            Line line = (Line) edge.getLine();
            line.setStartX(x);
            line.setStartY(y);
        });
    }

    private void onSPNodeMousePressed(MouseEvent t) {

        Shape c = (Shape) (t.getSource());

        if (t.getButton().equals(MouseButton.SECONDARY)) {

            addEdgeIfEnabled(c);

            removeSelectedIfEnabled(c);

            recolorSelectedIfEnabled(c);

        }
    }

    private SPGraphNode recognizeSPGraphNode(Shape shape) {

        List<SPGraphNode> nodes = nodeList.stream()
                .filter(i -> i.getShape().equals(shape))
                .collect(Collectors.toList());

        if (nodes.isEmpty()) {
            return null;
        }

        return nodes.get(0);
    }

    private Shape getEdge(SPGraphNode next) {
        if (regLineNodes.size() < 1) {
            return null;
        }

        SPGraphNode node1 = regLineNodes.get(regLineNodes.size() - 1);
        SPGraphNode node2 = next;

        Line line = createLine(node1.getX(), node1.getY(), node2.getX(), node2.getY(), Color.GREEN);

        SPGraphEdge SPGraphEdge = new SPGraphEdge(
                node1,
                node2,
                line);

        node1.addOutEdge(SPGraphEdge);
        node2.addInEdge(SPGraphEdge);

        edgeList.add(SPGraphEdge);

        return line;
    }

    private Line createLine(double x1, double y1, double x2, double y2, Color color) {

        Line line = new Line(x1, y1, x2, y2);

        line.setCursor(Cursor.CROSSHAIR);

        line.setOnMousePressed(t -> {

            Line l = (Line) t.getSource();

            if (t.getButton() == MouseButton.SECONDARY) {

                removeSelectedIfEnabled(l);

                recolorSelectedIfEnabled(l);

            }


        });

        line.setFill(color);
        return line;
    }

}
