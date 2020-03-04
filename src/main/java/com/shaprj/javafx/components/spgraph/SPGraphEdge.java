package com.shaprj.javafx.components.spgraph;
/*
 * Created by O.Shalaevsky on 28.01.2020
 */

import javafx.scene.control.Label;
import javafx.scene.shape.Shape;

public class SPGraphEdge {

    private SPGraphNode source;
    private SPGraphNode dest;
    private double weight;
    private Shape line;
    private Label label;

    public SPGraphEdge(SPGraphNode source, SPGraphNode dest, Shape line) {

        this.source = source;
        this.dest = dest;
        this.line = line;
    }

    public SPGraphNode getSource() {
        return source;
    }

    public void setSource(SPGraphNode source) {
        this.source = source;
    }

    public SPGraphNode getDest() {
        return dest;
    }

    public void setDest(SPGraphNode dest) {
        this.dest = dest;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Shape getLine() {
        return line;
    }

    public void setLine(Shape line) {
        this.line = line;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}
