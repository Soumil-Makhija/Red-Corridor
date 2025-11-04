package com.team12.redCorridor.ui.controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class LobbySceneController extends Controller {
    @Override
    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case H -> helpMenu();
                case Q -> quit();
                case N -> newGame(event);
                default -> {
                }
            }
        });
    }

    public void newGame(KeyEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/team12/redCorridor/ui/scenes/GameScene.fxml"));

            Parent root = loader.load();
            GameSceneController controller = loader.getController();
            Stage currentStage = stage != null ? stage : (Stage) ((Scene) event.getSource()).getWindow();
            Scene gameScene = new Scene(root);
            controller.setStage(currentStage);
            controller.recordKey(gameScene);
            currentStage.setScene(gameScene);
            currentStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
