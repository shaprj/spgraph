package com.shaprj.javafx;
/*
 * Created by O.Shalaevsky on 04.03.2020
 */

import com.shaprj.javafx.components.BaseController;
import com.shaprj.javafx.components.BaseResizeableController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        loadProjectManagementApp(stage, "Test SP-Graph", "/fxml/spgraph.fxml");

    }

    private void loadProjectManagementApp(Stage stage, String title, String resourceName) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resourceName));
        Parent root = fxmlLoader.load();
        BaseController baseController = fxmlLoader.getController();
        ((BaseResizeableController)baseController).setTopLeftSupplier(() -> new Pair<>(0.0, 0.0));
        baseController.postInit();
        Scene scene = new Scene(root);
        stage.setTitle(title);
        stage.setMaximized(true);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }
}
