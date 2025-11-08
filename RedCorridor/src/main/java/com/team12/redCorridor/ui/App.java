package com.team12.redCorridor.ui;

import com.team12.redCorridor.ui.controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * UI main class
 */
public class App extends Application {
    Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    // Program entry point
    @Override
    public void start(Stage stage) throws Exception {
        try {
            // starts at lobby scene
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/team12/redCorridor/ui/scenes/LobbyScene.fxml"));
            Parent root = loader.load();
            stage.setTitle("RedCorridor");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            controller = loader.getController();
            controller.setStage(stage);
            controller.recordKey(scene);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
