package com.team12.redCorridor.ui;

import com.team12.redCorridor.ui.controllers.Controller;
import com.team12.redCorridor.ui.controllers.LobbySceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/LobbyScene.fxml"));
            Parent root = loader.load();
            stage.setTitle("RedCorridor");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            controller = new LobbySceneController();
            controller.recordKey(scene);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
