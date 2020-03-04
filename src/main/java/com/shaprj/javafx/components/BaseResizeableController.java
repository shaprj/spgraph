package com.shaprj.javafx.components;

import javafx.util.Pair;

import java.util.function.Supplier;

public interface BaseResizeableController {

    void setTopLeftSupplier(Supplier<Pair<Double, Double>> topLeftSupplier);

}
