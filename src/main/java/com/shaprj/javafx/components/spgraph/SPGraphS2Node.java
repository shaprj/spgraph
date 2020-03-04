package com.shaprj.javafx.components.spgraph;
/*
 * Created by O.Shalaevsky on 28.01.2020
 */

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.function.Consumer;

public class SPGraphS2Node extends SPGraphNode {

    private final Consumer<MouseEvent> onMousePressedConsumer;
    private final Consumer<Shape> onReplaceEdgesConsumer;
    private double orgSceneX, orgSceneY;

    public SPGraphS2Node(double x, double y, Scene scene,
                         Consumer<MouseEvent> onMousePressedConsumer,
                         Consumer<Shape> onReplaceEdgesConsumer) {
        super(x, y, 4, Color.GREEN, scene);
        this.onMousePressedConsumer = onMousePressedConsumer;
        this.onReplaceEdgesConsumer = onReplaceEdgesConsumer;

        setShape(createCircle(
                getX(),
                getY(),
                getR(),
                getColor()));
    }

    @Override
    public void updateShape() {
        setShape(createCircle(getX(),
                getY(),
                getR(),
                getColor()));
    }

    public Consumer<Shape> getOnReplaceEdgesConsumer() {
        return this.onReplaceEdgesConsumer;
    }

    private Circle createCircle(double x, double y, double r, Color color) {

        Circle circle = new Circle(x, y, r, color);

        circle.setCursor(Cursor.HAND);

        circle.setOnMousePressed((t) -> {

            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();

            onMousePressedConsumer.accept(t);

        });
        circle.setOnMouseDragged((t) -> {

            if (t.getButton() != MouseButton.PRIMARY) {
                return;
            }

            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;

            Circle c = (Circle) (t.getSource());

            double newX = c.getCenterX() + offsetX;
            double newY = c.getCenterY() + offsetY;

            if (isShapeOutOfBounds(getScene(), newX, newY)) {
                return;
            }

            c.setCenterX(newX);
            c.setCenterY(newY);

            getName().setLayoutX(newX - 25);
            getName().setLayoutY(newY - 30);

            onReplaceEdgesConsumer.accept(c);

            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();
        });

        return circle;
    }
}
