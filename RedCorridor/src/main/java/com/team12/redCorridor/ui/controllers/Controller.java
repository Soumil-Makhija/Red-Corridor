package com.team12.redCorridor.ui.controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Controller {
    protected Stage stage;
    private static Stage primaryStage;

    public void setStage(Stage stage) {
        this.stage = stage;
        if (primaryStage == null && stage != null) {
            primaryStage = stage;
        }
    }

    protected Stage getPrimaryStage() {
        return primaryStage != null ? primaryStage : stage;
    }

    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case H -> helpMenu();
                case L -> lobby();
                default -> {
                }
            }
        });
    }

    public void helpMenu() {
        Stage ownerStage = stage != null ? stage : getPrimaryStage();
        if (ownerStage == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/team12/redCorridor/ui/scenes/HelpScene.fxml"));
            Parent root = loader.load();

            HelpSceneController controller = loader.getController();
            Stage helpStage = new Stage();
            Scene helpScene = new Scene(root);

            controller.setStage(helpStage);
            controller.setPreviousStage(ownerStage);
            controller.setPreviousScene(ownerStage.getScene());
            controller.recordKey(helpScene);

            helpStage.setTitle("Help");
            helpStage.initOwner(ownerStage);
            helpStage.initModality(Modality.WINDOW_MODAL);
            helpStage.setScene(helpScene);
            helpStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lobby() {
        Stage targetStage = getPrimaryStage();
        if (targetStage == null) {
            return;
        }

        if (stage != null && stage != targetStage) {
            stage.close();
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/team12/redCorridor/ui/scenes/LobbyScene.fxml"));
            Parent root = loader.load();
            LobbySceneController lobbyController = loader.getController();
            Scene lobbyScene = new Scene(root);

            targetStage.setScene(lobbyScene);
            lobbyController.setStage(targetStage);
            lobbyController.recordKey(lobbyScene);
            targetStage.show();
            targetStage.toFront();
            targetStage.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
