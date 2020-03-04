package com.shaprj.javafx.components.spgraph;
/*
 * Created by O.Shalaevsky on 28.01.2020
 */

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public abstract class SPGraphNode {

    private double x;
    private double y;
    private double r;
    private Color color;
    private Label name;
    private Shape shape;
    private List<SPGraphEdge> inEdgeList = new ArrayList<>();
    private List<SPGraphEdge> outEdgeList = new ArrayList<>();
    private Scene scene;

    private static int ctr = 1;

    abstract public void updateShape();

    public SPGraphNode(double x, double y, double r, Color color, Scene scene) {

        this(x, y, r, color, scene, String.valueOf(ctr++));
    }

    public SPGraphNode(double x, double y, double r, Color color, Scene scene, String name) {

        this.x = x;
        this.y = y;
        this.r = r;
        this.color = color;
        this.scene = scene;
        this.name = new Label(name);
        this.name.setId("SP-grid-node-label");
        this.name.setLayoutX(x - 25);
        this.name.setLayoutY(y - 30);
    }

    protected boolean isShapeOutOfBounds(Scene scene, double x, double y) {

        double xMin = scene.getX();
        double xMax = scene.getX() + scene.getWidth();

        double yMin = scene.getY();
        double yMax = scene.getY() + scene.getHeight();

        return x < xMin || x > xMax - 20 || y < yMin - 20 || y > yMax - 30 - 40;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<SPGraphEdge> getInEdgeList() {
        return inEdgeList;
    }

    public List<SPGraphEdge> getOutEdgeList() {
        return outEdgeList;
    }

    public void addOutEdge(SPGraphEdge SPGraphEdge) {
        this.outEdgeList.add(SPGraphEdge);
    }

    public void removeOutEdge(SPGraphEdge SPGraphEdge) {
        this.outEdgeList.remove(SPGraphEdge);
    }

    public void addInEdge(SPGraphEdge SPGraphEdge) {
        this.inEdgeList.add(SPGraphEdge);
    }

    public void removeInEdge(SPGraphEdge SPGraphEdge) {
        this.inEdgeList.remove(SPGraphEdge);
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Label getName() {
        return name;
    }

    public void setName(Label name) {
        this.name = name;
    }
}
