package com.drawingapp;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/drawingapp/view/drawing-view.fxml"));
        BorderPane root = loader.load();

        Scene scene = new Scene(root, 1440, 900);
        scene.getStylesheets().add(
                getClass().getResource("/com/drawingapp/view/app.css").toExternalForm()
        );

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Drawing Studio");
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(760);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
